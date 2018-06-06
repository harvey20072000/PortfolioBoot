package ga.workshop.com.crud;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ga.workshop.com.dao.TargetDAO;
import ga.workshop.com.logic.DataProcesser;
import ga.workshop.com.model.ActionBean;
import ga.workshop.com.model.ActionBean.ACTION_PROP;
import ga.workshop.com.model.ActionBean.ACTION_TYPE;
import ga.workshop.com.model.Target;
import ga.workshop.com.util.Const;
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
	private boolean isActionPerformed = false;	// 純粹用作同步資料時的狀況描述
	
	private List<Target> retList = new ArrayList<>();
	private int maxDataSend = 10;
	
	private volatile String dataProcessedStatus;
	
	private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(5);
	
	private final long CACHE_INTERVAL = 7*24*60*60*1000; // 7天
	private List<ActionBean> actionList = new LinkedList<>();
	
	public TargetCRUD(){}
	
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
		stpe.scheduleWithFixedDelay(new Checker(), 1, 1, TimeUnit.MINUTES);
		stpe.execute(new ActionWorker());
	}
	
	private void initCache() {
		try {
			Map<String,Target> tempMap = new HashMap<>();
			tempMap = targetDAO.inputData(Const.DATA_INPUT_FILE_PATH, tempMap);
			if(rawDataInputMap.isEmpty() 
					|| rawDataInputMap.size() != tempMap.size() 
					|| !hasSameContents(rawDataInputMap, tempMap)){
				rawDataInputMap.clear();
				rawDataInputMap.putAll(tempMap);
				for(String key : rawDataInputMap.keySet()){
					cacheMap.putIfAbsent(key, rawDataInputMap.get(key));
				}
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
		if(!isActionPerformed) {
			returnString = "unchange";
		}
		try {
			targetDAO.outputData(Const.DATA_OUTPUT_FILE_PATH.replace("{DATE}", Const.SDF_NO_TIME.format(new Date())), cacheMap);
			returnString = "true";
		} catch (Exception e) {
			log.error("outputDatas fail, exception => {}",e.toString());
			returnString = "false";
		}
		isActionPerformed = false;
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
		actionList.add(new ActionBean(ACTION_TYPE.CREATE).addProp(ACTION_PROP.target.toString(), new Target(stockId)));
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
	 * 暫時用不到
	 */
	public String updateTarget(String stockId) {
		if(!checkCache(stockId) || !isValidInput(stockId))
			return "false";
//		Target target = cacheMap.get(stockId);
//		cacheMap.put(target.getStockId(), target);
		return "true";
	}
	
	/*
	 * 
	 */
	public String deleteTarget(String stockId) {
		if (!checkCache(stockId))
			return "false";
		actionList.add(new ActionBean(ACTION_TYPE.DELETE).addProp(ACTION_PROP.target.toString(), new Target(stockId)));
		return "true";
	}
	
	/**
	 * 從網路上抓取股票資料，並且送進分析處理
	 * @author harvey20072000
	 *
	 */
	private class Checker extends Thread{
		@Override
		public void run(){
			System.out.println("TargetCRUD Checker running~~~~~~~~");
			String response,url;
			Calendar calendar = Calendar.getInstance();
			int dataProcessedNum = 0,totalTargetSize = cacheMap.size();
			Map<String,Target> tempTargetMap = new HashMap<>(cacheMap);
			List<String> toBeDeleteList = new LinkedList<>();
			for (Target target : tempTargetMap.values()) {
				try {
					// https://statementdog.com/api/v1/fundamentals/{STOCK_ID}/2012/1/{YEAR}/4/?queried_by_user=false&_={TIME_STAMP}
					// 以這兩個屬性有無值和更新時間是否超過interval來判斷是否已經跑過url
					if(target.getMarket() != null && 
							target.getLatestClosingPriceAndDate() != null && 
							target.getUpdateTime().getTime() + CACHE_INTERVAL > new Date().getTime()) {
						totalTargetSize--;
						continue;
					}
					url = DATA_URL.replace("{STOCK_ID}",target.getStockId())
							.replace("{YEAR}", calendar.get(Calendar.YEAR)+"").replace("{TIME_STAMP}",calendar.getTimeInMillis() + "");
					System.out.println("Checker run url："+url);
					response = WebUtil.sendGet(url);
					if(dataProcesser.processData(target, response)){
						tempTargetMap.put(target.getStockId(),target);
						dataProcessedNum++;
					}
				} catch (Exception e) {
					log.error("target : {} , Checker fail to execute , exception => {}",target.getStockId(),e.toString());
					toBeDeleteList.add(target.getStockId());
				}
			}
			for(String id : toBeDeleteList) {
				tempTargetMap.remove(id);
				cacheMap.remove(id);
			}
			cacheMap.putAll(tempTargetMap);
			fixDataInput();
			dataProcessedStatus = dataProcessedNum+"/"+totalTargetSize+" 分析完成 !";
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
	
	private class ActionWorker extends Thread{
		@Override
		public void run() {
			ActionBean actionBean = null;
			Target target = null;
			while(true) {
				if(actionList.isEmpty())
					continue;
				actionBean = actionList.remove(0);
				try {
					if(ACTION_TYPE.CREATE.equals(actionBean.getActionType()) &&
							actionBean.getProp(ACTION_PROP.target.toString()) != null) {
						target = (Target)actionBean.getProp(ACTION_PROP.target.toString());
						cacheMap.put(target.getStockId(), target);
					}else if (ACTION_TYPE.UPDATE.equals(actionBean.getActionType())) {
						// TODO 暫時用不到
					}else if (ACTION_TYPE.DELETE.equals(actionBean.getActionType()) &&
							actionBean.getProp(ACTION_PROP.target.toString()) != null) {
						target = (Target)actionBean.getProp(ACTION_PROP.target.toString());
						cacheMap.remove(target.getStockId());
					}
					isActionPerformed = true;
				} catch (Exception e) {
					log.error("ActionWorker for action({},{}) fail, exception => {}",
							actionBean.getActionType().toString(),
							Const.SDF_TIMESTAMP.format(new Date(actionBean.getCreatTime())),
							e.toString());
				}
			}
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
