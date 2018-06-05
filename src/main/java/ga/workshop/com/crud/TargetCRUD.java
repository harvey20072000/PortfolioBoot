package ga.workshop.com.crud;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ga.workshop.com.dao.TargetDAO;
import ga.workshop.com.logic.DataProcesser;
import ga.workshop.com.model.Target;
import ga.workshop.com.util.Const;
import ga.workshop.com.util.DataStorageSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tw.com.geosat.util.WebUtil;

@Slf4j
@Getter
@Setter
@Service
public class TargetCRUD {

    private String propFileName;
    private Properties properties;
	
//    private String rootPath = "./src/main/java/portfolio";	
//	private String dataInputFilePath = "C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/targets to run.txt";
//	private String dataFixedInputFilePath = "C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/targets fixed to run.txt";
//	private String dataOutputFilePath = "C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/targets analyzed {DATE}.txt";
//	
//	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private final String DATA_URL = "https://statementdog.com/api/v1/fundamentals/{STOCK_ID}/2012/1/{YEAR}/4/?queried_by_user=false&_={TIME_STAMP}";
    
	@Autowired
	private TargetDAO targetDAO;
	
	@Autowired
	private DataProcesser dataProcesser;
	
	private Map<String ,Target> rawDataInputMap = new ConcurrentHashMap<>();
	private Map<String ,Target> cacheMap = new ConcurrentHashMap<>();
	private volatile int cacheDataUpdatedCounts;	// 要和dataUpdatedCounts做比對，有不一樣就自動儲存
	private volatile int dataUpdatedCounts;
	private volatile int cacheDataUpdatedCountsForThreads;
	
	private List<Target> retList = new ArrayList<>();
	private int maxDataSend = 10;
	
	private volatile String dataProcessedStatus;
	
	private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(10);
	private static ScheduledFuture f;
	
	private Long mapUpdateTime;
//	private Map<String, Long> cacheUpdateTime = new HashMap<>();
	private long cacheInterval = 5*60*1000;
	
	private Map<String, Checker> runningThreads = new ConcurrentHashMap<>();
	
	public TargetCRUD(){
		
	}
	
	@PostConstruct
	private void init() {
		log.debug("path value : {}",Const.DATA_INPUT_FILE_PATH);
		new Thread() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (Exception e) {}
				initCache();
			}
		}.start();
	}
	
	private void initProp(){
		if(null == propFileName || "".equals(propFileName))
    		propFileName = "kill_giant_game_story";
    	String propFilePath = "{ROOT_PATH}/story/{PROP_FILE_NAME}.properties";
    	try {
    		properties = new Properties();
			properties.load(new InputStreamReader(new FileInputStream(propFilePath.replace("{ROOT_PATH}", Const.ROOT_PATH)
					.replace("{PROP_FILE_NAME}", propFileName)), "utf-8"));
		} catch (Exception e) {
			log.error("fail to load property file : {} , exception => {}",propFileName,e.toString());
		}
	}
	
	private void initCache() {
		try {
			Map<String,Target> tempMap = new HashMap<>();
			log.debug("initCache inputData from path({}) , time = {}",Const.DATA_INPUT_FILE_PATH,System.currentTimeMillis());
//			log.debug("initCache inputData from DataStorageSetting({}) , time = {}",DataStorageSettings.fileRootPath,System.currentTimeMillis());
			tempMap = targetDAO.inputData(Const.DATA_INPUT_FILE_PATH, tempMap);
			if(rawDataInputMap.isEmpty() 
					|| rawDataInputMap.size() != tempMap.size() 
					|| !hasSameContents(rawDataInputMap, tempMap)){
				rawDataInputMap.clear();
				rawDataInputMap.putAll(tempMap);
				for(String key : rawDataInputMap.keySet()){
					cacheMap.putIfAbsent(key, rawDataInputMap.get(key));
				}
				runningThreads.clear();
			}
		} catch (Exception e) {
			log.error("initCache fail, exception => {}",e.toString());
		}
	}
	
	/*
	 * 同步資料，包含輸出到外部檔案
	 */
	public String outputDatas(){
		initCache();
		log.info("outputDatas => checking is-input-file-changed");
		String returnString;
		if(cacheDataUpdatedCounts == dataUpdatedCounts)
			returnString = "unchange";
		if(cacheDataUpdatedCounts < dataUpdatedCounts){
			log.error("outputDatas fail, logic error");
			returnString = "logic error";
		}
		try {
			targetDAO.outputData(Const.DATA_OUTPUT_FILE_PATH.replace("{DATE}", Const.SDF_NO_TIME.format(new Date())), cacheMap);
			returnString = "true";
		} catch (Exception e) {
			log.error("outputDatas fail, exception => {}",e.toString());
			returnString = "false";
		}
		dataUpdatedCounts = cacheDataUpdatedCounts;
		return returnString;
	}
	
	private boolean hasSameContents(Map<String, Target> map1,Map<String, Target> map2){
		for(String key:map1.keySet()){
			if(!map2.containsKey(key))
				return false;
		}
		for(String key:map2.keySet()){
			if(!map1.containsKey(key))
				return false;
		}
		return true;
	}
	
	private boolean isValidInput(String... inputs){
    	for(String input : inputs){
    		if(input == null || "".equals(input)){
    			return false;
    		}
    	}
    	return true;
    }
	
	/*
	 * 
	 */
	public String createTarget(String stockId){
		if (!isValidInput(stockId))
			return "false";
		cacheMap.put(stockId, new Target(stockId));
		cacheDataUpdatedCounts++;
		log.debug("Target( {} ) created in cache!!", stockId);
		return "true";
	}
	
	private boolean checkCache(String key){
    	if(cacheMap.get(key) != null)
    		return true;
    	return false;
    }
	
	/*
	 * 
	 */
	public Target getTarget(String stockId) {
		if (!checkCache(stockId))
			return null;
		return cacheMap.get(stockId);
	}
	
	/*
	 * 
	 */
	public int maxPages() {
		return cacheMap.size() % maxDataSend > 0 ? (cacheMap.size() / maxDataSend + 1) : (cacheMap.size() / maxDataSend);
	}
	
	/*
	 * 
	 */
	public List<Target> listAll(int page) {
//		retList
		if(retList.size() == cacheMap.values().size() && retList.containsAll(cacheMap.values())
				&& cacheMap.values().containsAll(retList))
			return listPart(retList, page);
		Map<String,Target> treeMap = new TreeMap<>(cacheMap);
		retList.clear();
		retList.addAll(treeMap.values());
		return listPart(retList, page);
	}
	
	// 分頁給資料
	private List<Target> listPart(List<Target> list,int page){
		int maxPage = list.size() % maxDataSend > 0 ? (list.size() / maxDataSend + 1) : (list.size() / maxDataSend); 
		for(int i=0;i<maxPage;i++){
			if(page != (i+1))
				continue;
			if(page < maxPage){
				System.out.println("list part size : "+list.subList(i * maxDataSend, (i+1)*maxDataSend).size());
				return list.subList(i * maxDataSend, (i+1)*maxDataSend);
			}
			if(page == maxPage){
				System.out.println("list part size : "+list.subList(i * maxDataSend, list.size()).size());
				return list.subList(i * maxDataSend, list.size());
			}
		}
		log.error("index  : {} ,beyond max page : {}",page,maxPage);
		return new LinkedList<>();
	}
	
	/*
	 * 
	 */
	public String updateTarget(String stockId) {
		if(!checkCache(stockId) || !isValidInput(stockId))
			return "false";
		Target target = cacheMap.get(stockId);
		cacheMap.put(target.getStockId(), target);
		cacheDataUpdatedCounts++;
		return "true";
	}
	
	/*
	 * 
	 */
	public String deleteTarget(String stockId) {
		if (!checkCache(stockId))
			return "false";
		cacheMap.remove(stockId);
		cacheDataUpdatedCounts++;
		return "true";
	}
	
	// 執行檢測url狀態
	@Scheduled(cron = "*/30 * * * * *")
	public void doCheck(){
		Checker checker;
		Target target;
		String threadKey = "checker";
		System.out.println("analyzer doCheck executed!");
		if (cacheDataUpdatedCounts == 0 && cacheDataUpdatedCountsForThreads == 0 && cacheMap.size() > 0
				&& runningThreads.size() == 0) {
			checker = new Checker();
			runningThreads.put(threadKey, checker);
			runCheck(checker);
			System.out.println("analyzer doCheck -> thread init executed!");
		}
		if(cacheDataUpdatedCounts > cacheDataUpdatedCountsForThreads){
			if(runningThreads.size() < cacheMap.size()){	// create
				checker = new Checker();
				runningThreads.put(threadKey, checker);
				runCheck(checker);
				System.out.println("analyzer doCheck -> create executed!");
			}else if(runningThreads.size() == cacheMap.size()){	// update
				checker = new Checker();
				runningThreads.put(threadKey, checker);
				runCheck(checker);
				System.out.println("analyzer doCheck -> update executed!");
			}else if (runningThreads.size() > cacheMap.size()) {	// delete
				checker = new Checker();
				runningThreads.put(threadKey, checker);
				runCheck(checker);
				System.out.println("analyzer doCheck -> delete executed!");
			}
			cacheDataUpdatedCountsForThreads = cacheDataUpdatedCounts;
		}
		System.out.println("cacheMap count : "+cacheMap.size());
		System.out.println("runningThreads count : "+runningThreads.size());
		System.out.println("ThreadPool active counts : "+stpe.getActiveCount());
		System.out.println("ThreadPool size : "+stpe.getPoolSize());
	}
	
	private void runCheck(Checker checker){
		if (!stpe.isShutdown()) {
			stpe.shutdown();
			stpe = new ScheduledThreadPoolExecutor(10);
		}
		stpe.execute(checker);
	}
	
	private class Checker extends Thread{
		@Override
		public void run(){
			System.out.println("Checker running~~~~~~~~");
			String response,url;
			Date date;
			int dataProcessedNum = 0;
			for (Target target : cacheMap.values()) {
				try {
					// https://statementdog.com/api/v1/fundamentals/{STOCK_ID}/2012/1/{YEAR}/4/?queried_by_user=false&_={TIME_STAMP}
					if(target.getMarket() != null && target.getLatestClosingPriceAndDate() != null)	// 以這兩個屬性有無值來判斷是否已經跑過url
						continue;
					date = new Date();
					url = DATA_URL.replace("{STOCK_ID}",
							target.getStockId()).replace("{YEAR}", (date.getYear() + 1900) + "").replace("{TIME_STAMP}",
									date.getTime() + "");
					System.out.println("url："+url);
					response = WebUtil.sendGet(url);
					if((target = dataProcesser.processData(target, response)) != null){
						cacheMap.put(target.getStockId(), target);
						dataProcessedNum++;
					}
				} catch (Exception e) {
					log.error("target : {} , Checker fail to execute , exception => {}",target.getStockId(),e.toString());
					cacheMap.remove(target.getStockId());
					continue;
				}
			}
			fixDataInput();
			dataProcessedStatus = dataProcessedNum+"/"+cacheMap.size()+" 分析完成 !";
			System.out.println("analyzer："+dataProcessedStatus);
		}
	}
	
	private void fixDataInput(){
		Map<String, String> fixedMap = new HashMap<>();
		for(Target target:cacheMap.values()){
			fixedMap.put(target.getStockId(), target.getStockName());
		}
		try {
			targetDAO.outputLine(Const.DATA_INPUT_FILE_PATH, fixedMap);
		} catch (Exception e) {
			log.error("fixDataInput fail , exception => {}",e.toString());
		}
	}
	
	public static void main(String[] args) throws Exception {
		TargetCRUD crud = new TargetCRUD();
		
//		Gson gson = new Gson();
//		Boolean b = true;
//		String a = b.toString(),a1 = b.toString()+"";
//		System.out.println(gson.toJson(a));
//		System.out.println(gson.toJson(b));
		
	}
}
