package ga.workshop.com.crud;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.JsonParser;

import ga.workshop.com.dao.TargetDAO;
import ga.workshop.com.logic.DataProcesser;
import ga.workshop.com.logic.TWStockService;
import ga.workshop.com.logic.YahooFinanceService;
import ga.workshop.com.model.Target;
import ga.workshop.com.model.TrackedTarget;
import ga.workshop.com.model.User;
import ga.workshop.com.model.UserDataChangeRecord;
import ga.workshop.com.util.Const;
import ga.workshop.com.util.PortfolioContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Service
public class TrackedTargetCRUD {

	private final String DATA_URL = "https://statementdog.com/api/v1/fundamentals/{STOCK_ID}/2012/1/{YEAR}/4/?queried_by_user=false&_={TIME_STAMP}";
    
	@Autowired
	private TargetDAO targetDAO;
	
	@Autowired
	private DataProcesser dataProcesser;
	
	@Autowired
	private FlowOfCRUD flowOfCRUD;
	
	@Autowired
	private TargetAlertCRUD targetAlertCRUD;
	
	@Autowired
	private TWStockService twStockService;
	
	@Autowired
	private YahooFinanceService yahooFinanceService;
	
//	private Map<String ,Target> rawDataInputMap = new ConcurrentHashMap<>();
	private Map<String ,User> users = new ConcurrentHashMap<>();
	private Map<String, UserDataChangeRecord> changeRecords = new ConcurrentHashMap<>();
	
//	private volatile int cacheDataUpdatedCounts;	// 要和dataUpdatedCounts做比對，有不一樣就自動儲存
//	private volatile int dataUpdatedCounts;
	
	private Map<String, Checker> runningThreads = new ConcurrentHashMap<>();	// key是userName
	private Map<String, Integer> cacheDataUpdatedCountsMapForThreads = new ConcurrentHashMap<>();
	private Map<String, Integer> cacheTrackedSizeCountsMapForThreads = new ConcurrentHashMap<>();
//	private volatile int cacheDataUpdatedCountsForThreads;
	
//	private List<Target> retList = new ArrayList<>();
	private int maxDataSend = 4;
	
	private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(10);
	private static ScheduledFuture f;
	
	private Long mapUpdateTime;
//	private Map<String, Long> cacheUpdateTime = new HashMap<>();
	private long cacheInterval = 5*60*1000;
	
	private JsonParser parser = new JsonParser();
	
	
	public TrackedTargetCRUD(){
		
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
	
	private void initCache() {
		int count = 1;
		while(true){
			try {
				if(count == 1){
					users = targetDAO.inputUsersWithDatas(Const.USERS_OUTPUT_FILE_PATH, users);
				}else if (count == 2) {
					users = targetDAO.inputUsers(Const.USERS_INPUT_FILE_PATH, users);
				}
				if(users.size() != 0 || count >= 10){
					break;
				}
			} catch (Exception e) {
				log.error("initCache fail , count：{} , exception => {}",count, e.toString());
			}
			count++;
		}
		for (String name : users.keySet())
			changeRecords.put(name, new UserDataChangeRecord(name));
	}
	
	// 每個處理方法都要用此方法決定目前是哪個user的list
	private User loadCurrentUser(){
		if(users.containsKey(PortfolioContext.userName))
			return users.get(PortfolioContext.userName);
		log.error("loadCurrentUser fail => {}",PortfolioContext.userName);
		return null;
	}
	
	/*
	 * 
	 */
	public String outputDatas(){
//		try {
//			Map<String, Target> tempCacheMap = new HashMap<>();
//			tempCacheMap = targetDAO.inputData(dataInputFilePath, tempCacheMap);
//			if (rawDataInputMap.size() != tempCacheMap.size() || !hasSameContents(rawDataInputMap, tempCacheMap)){
//				rawDataInputMap.clear();
//				rawDataInputMap.putAll(tempCacheMap);
//				users.clear();
//				users.putAll(rawDataInputMap);
//				runningThreads.clear();
//			}
//		} catch (Exception e1) {
//			log.error("outputDatas checking is-input-file-changed fail, exception => {}", e1.toString());
//		}
		User user = loadCurrentUser();
		UserDataChangeRecord record = changeRecords.get(loadCurrentUser().getName());
		UserDataChangeRecord record2 = targetAlertCRUD.getChangeRecords().get(user.getName());
		record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts()+record2.getCacheDataUpdatedCounts());
		record.setDataUpdatedCounts(record.getDataUpdatedCounts()+record2.getDataUpdatedCounts());
		users.get(user.getName()).setTargetAlerts(targetAlertCRUD.getUsers().get(user.getName()).getTargetAlerts());
		
		if(record.getCacheDataUpdatedCounts() == record.getDataUpdatedCounts())
			return "unchange";
		if(record.getCacheDataUpdatedCounts() < record.getDataUpdatedCounts()){
			log.error("outputDatas fail, logic error");
			record.setDataUpdatedCounts(record.getCacheDataUpdatedCounts());
			return "logic error";
		}
		try {
			record.setDataUpdatedCounts(record.getCacheDataUpdatedCounts());
			targetDAO.outputUsers(Const.USERS_OUTPUT_FILE_PATH, users);
			return "true";
		} catch (Exception e) {
			log.error("outputDatas fail, exception => {}",e.toString());
			return "false";
		}
	}
	
	private boolean hasSameContents(Map<String, Target> map1,Map<String, Target> map2){
		for(String key:map1.keySet()){
			if(!map2.containsKey(key))
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
	public String addTarget(String stockId){
		if (!isValidInput(stockId))
			return "false";
		User user = loadCurrentUser();
		UserDataChangeRecord record = changeRecords.get(user.getName());
		String[] stockIds = stockId.split(",");
		Target target;
		String report = null;
		for(String tempStockId : stockIds){
			target = flowOfCRUD.getTargetCRUD().getCacheMap().get(tempStockId);
			user.getTrackedTargets().putIfAbsent(tempStockId, new TrackedTarget(target));
			record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts()+1);
			if(report == null){
				report = tempStockId;
			}else {
				report += ", "+tempStockId;
			}
		}
		log.debug("Target( {} ) added to track!!", stockId);
		return "true";
	}
	
	private boolean checkCache(String key){
    	if(loadCurrentUser().getTrackedTargets().get(key) != null)
    		return true;
    	return false;
    }
	
	/*
	 * 
	 */
	public TrackedTarget getTarget(String stockId) {
		if (!checkCache(stockId))
			return null;
		return loadCurrentUser().getTrackedTargets().get(stockId);
	}
	
	/*
	 * 
	 */
	public int maxPages() {
		return loadCurrentUser().getTrackedTargets().size() % maxDataSend > 0 ? 
				(loadCurrentUser().getTrackedTargets().size() / maxDataSend + 1) : 
					(loadCurrentUser().getTrackedTargets().size() / maxDataSend);
	}
	
	/*
	 * 
	 */
	public List<TrackedTarget> listAll(int page) {
		User user = loadCurrentUser();
		List<TrackedTarget> retList = new LinkedList<>();
		Map<String,TrackedTarget> treeMap = new TreeMap<>(user.getTrackedTargets());
		List<TrackedTarget> trackedTargets = new LinkedList<>(treeMap.values()),primaryTargets = new LinkedList<>();
		for(int i = 0;i<trackedTargets.size();i++){
			if(trackedTargets.get(i).getNote() != null && 
					(trackedTargets.get(i).getNote().contains("*") || trackedTargets.get(i).getNote().contains("＊"))){
				primaryTargets.add(trackedTargets.remove(i));
				i--;
			}
		}
		retList.addAll(primaryTargets);
		retList.addAll(trackedTargets);
		return listPart(retList, page);
	}
	
	// 分頁給資料
	private List<TrackedTarget> listPart(List<TrackedTarget> list,int page){
		int maxPage = list.size() % maxDataSend > 0 ? (list.size() / maxDataSend + 1) : (list.size() / maxDataSend); 
		for(int i=0;i<maxPage;i++){
			if(page != (i+1))
				continue;
			if(page < maxPage){
//				System.out.println("list part size : "+retSubList.size());
				return list.subList(i * maxDataSend, (i+1)*maxDataSend);
			}
			if(page == maxPage){
//				System.out.println("list part size : "+list.subList(i * maxDataSend, list.size()).size());
				return list.subList(i * maxDataSend, list.size());
			}
		}
		log.error("index  : {} ,beyond max page : {}",page,maxPage);
		return new LinkedList<>();
	}
	
	/*
	 * 
	 */
	public String updateTarget(String stockId, String... args) {
		if(!checkCache(stockId) || !isValidInput(stockId))
			return "false";
		String regex = ":";
		User user = loadCurrentUser();
		UserDataChangeRecord record = changeRecords.get(user.getName());
		TrackedTarget trackedTarget = user.getTrackedTargets().get(stockId);
		for(String arg : args){
			for(Field field : trackedTarget.getClass().getDeclaredFields()){
				if(field.getName().toLowerCase().equals(arg.split(regex)[0].toLowerCase())){
					try {
						trackedTarget.getClass().getMethod("set"+firstCharToUpper(field.getName()), String.class).invoke(trackedTarget, arg.split(regex)[1]);
						break;
						// field.set(this, arg.split("-")[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		user.getTrackedTargets().put(stockId, trackedTarget);
		record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts()+1);
		log.debug("Tracked Target( {} ) updated!!", stockId);
		return "true";
	}
	
	private String firstCharToUpper(String input){
		if(input == null || "".equals(input))
			return "";
		return input.substring(0,1).toUpperCase()+input.substring(1);
	}
	
	/*
	 * 
	 */
	public String removeTarget(String stockId) {
		if (!checkCache(stockId))
			return "false";
		User user = loadCurrentUser();
		UserDataChangeRecord record = changeRecords.get(user.getName());
		user.getTrackedTargets().remove(stockId);
		record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts()+1);
		log.debug("Tracked Target( {} ) removed!!", stockId);
		return "true";
	}
	
	// 追蹤股票資訊
	@Scheduled(cron = "0 */3 * * * *")
	public void doCheck(){
		Checker checker;
		Target target;
		String threadKey;
		System.out.println("tracker doCheck executed!");
		UserDataChangeRecord record;
		
		if(cacheDataUpdatedCountsMapForThreads.isEmpty()){
			for(User user : users.values())
				cacheDataUpdatedCountsMapForThreads.put(user.getName(), new Integer(0));
		}

		for(User user : users.values()){
			record = changeRecords.get(user.getName());
			threadKey = user.getName();
			if (user.getTrackedTargets().size() > 0
					&& cacheTrackedSizeCountsMapForThreads.size() == 0) {
				checker = new Checker(user.getName(),user.getTrackedTargets().values());
				runningThreads.put(threadKey, checker);
				cacheTrackedSizeCountsMapForThreads.put(threadKey, user.getTrackedTargets().size());
				runCheck(checker);
				System.out.printf("tracker doCheck -> thread：[%s] init executed!%n",user.getName());
			} else if(record.getCacheDataUpdatedCounts() > cacheDataUpdatedCountsMapForThreads.get(threadKey)){
				if(runningThreads.get(threadKey).getTrackedTargets().size() < user.getTrackedTargets().size()){	// add
					checker = new Checker(user.getName(),user.getTrackedTargets().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("tracker doCheck -> thread：[%s] add executed!%n",user.getName());
				}else if(runningThreads.get(threadKey).getTrackedTargets().size() == user.getTrackedTargets().size()){	// update
					checker = new Checker(user.getName(),user.getTrackedTargets().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("tracker doCheck -> thread：[%s] update executed!%n",user.getName());
				}else if (runningThreads.get(threadKey).getTrackedTargets().size() > user.getTrackedTargets().size()) {	// delete
					checker = new Checker(user.getName(),user.getTrackedTargets().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("tracker doCheck -> thread：[%s] remove executed!%n",user.getName());
				}
				cacheDataUpdatedCountsMapForThreads.put(threadKey, record.getCacheDataUpdatedCounts());
			}else {
				stpe.execute(new Checker(user.getName(), user.getTrackedTargets().values()));
				System.out.printf("tracker doCheck -> thread：[%s] auto tracking !%n",user.getName());
			}
		}
		System.out.println("users count : "+users.size());
		System.out.println("runningThreads count : "+runningThreads.size());
		System.out.println("ThreadPool active counts : "+stpe.getActiveCount());
		System.out.println("ThreadPool size : "+stpe.getPoolSize());
	}
	
	private void runCheck(Checker checker){
		if (!stpe.remove(checker)) {
			stpe.setRemoveOnCancelPolicy(true);
			stpe.remove(checker);
			stpe.setRemoveOnCancelPolicy(false);
		}
		stpe.execute(checker);
	}
	
	@Getter
	@Setter
	private class Checker extends Thread{
		
		String userName;
		
		List<TrackedTarget> trackedTargets = new ArrayList<>();
		
		public Checker() {
		}
		
		public Checker(String userName,Collection<TrackedTarget> inputList){
			this();
			this.userName = userName;
			this.trackedTargets.addAll(inputList);
		}
		
		@Override
		public void run(){
			System.out.println("Checker running~~~~~~~~");
			int dataProcessedNum = 0;
			for (TrackedTarget trackedTarget : trackedTargets) {
				// yahooFinance
				try {
					System.out.printf("user：%s , tracking target：%s%n",userName,trackedTarget.getStockId());
					trackedTarget = trackStockInfo(trackedTarget);
					dataProcessedNum++;
				} catch (Exception e) {
					log.error("user：{} , target：{} , Checker fail to execute , exception => {}"
							,userName,trackedTarget.getStockId(),e.toString());
					continue;
				}
			}
//			fixDataInput();
			System.out.printf("user : %s , 有 %d/%d 支股票追蹤完成！%n",userName,dataProcessedNum,trackedTargets.size());
		}
	}
	
	private TrackedTarget trackStockInfo(TrackedTarget trackedTarget) {
		/*
		String stockId = trackedTarget.getStockId();
		if (checkCache(stockId)) { // 檢查cache，有值則回傳
//			return stockInfos_cache.get(stockId);
		}

		Stock yStock = null;
		try {
			yStock = YahooFinance.get(stockId + ".TW");
		} catch (Exception e) {
			log.error("trackStockInfo fail, stockId({}) not exist => {}", stockId, e.toString());
			return trackedTarget;
		}

		StockQuote quote = yStock.getQuote();
		if(quote == null){
			log.error("stockId({}) quote is null !",stockId);
			return trackedTarget;
		}
		
		Date date = new Date();
		if(date.getDay() == 0 || date.getDay() == 6)	// 若是六日則不紀錄
			return trackedTarget;
		if(date.getHours() < 9)	// 九點之前不紀錄
			return trackedTarget;
		String dateString = Const.SDF_NO_TIME.format(new Date());
		Number temp = 0.0;
		try {
			temp = quote.getPrice().doubleValue();
		} catch (Exception e) {
			log.error("trackStockInfo -> getPrice fail, exception => {}",e.toString());
		}
		trackedTarget.getTrackedPrices().put(dateString, temp.doubleValue());
		
		temp = quote.getVolume()/1000.0;
		trackedTarget.getTrackedVolumes().put(dateString, temp.doubleValue());
		
		return trackedTarget;
	*/
		String serviceName = "";
		for(int i=0;i<2;i++){
			try {
				if(i == 0){
					serviceName = "yahooFinanceService";
					trackedTarget = yahooFinanceService.trackStockInfo(trackedTarget);
				}else if (i == 1) {
					serviceName = "twStockService";
					trackedTarget = twStockService.trackStockInfo(trackedTarget);
				}
				break;
			} catch (Exception e) {
				log.error("stock({}) invoke {}.trackStockInfo fail, exception => {}",trackedTarget.getStockId(),serviceName,e.toString());
			}
		}
		return trackedTarget;
	}

	private Number processFinanceValues(String processType , Number... nums) throws Exception{
		Double result = 0.0;
		try {
			System.out.printf("數值處理方式：%s , 處理個數：%d%n",processType,nums.length);
			if("sum".equals(processType)){
				for(Number num : nums){
					result += num.doubleValue();
				}
			}else if("avg".equals(processType)){
				for(Number num : nums){
					result += num.doubleValue();
				}
				if(nums.length != 0)
					result /= (nums.length+0d);
			}
			return result;
		} catch (Exception e) {
			String errMsg;
			if(nums == null || nums[0] == null){
				errMsg = "processFinanceValues fail => nums includes null value !";
			}else {
				errMsg = "processFinanceValues fail, exception => {}" + e.toString();
			}
			throw new Exception(errMsg);
		}
	}
	
	public static void main(String[] args) throws Exception {
		TrackedTargetCRUD crud = new TrackedTargetCRUD();
//		PortfolioContext.userName = "root";
//		crud.users.put("root", new User("root", "1234"));
//		crud.users.get("root").getTrackedTargets().put("1111", new TrackedTarget("1111"));
//		crud.changeRecords.put("root", new UserDataChangeRecord("root"));
//		
//		crud.updateTarget("1111", "stockName:鴻海","note:測試","exDividendDate：2017-08-09");
		
//		Gson gson = new Gson();
//		Boolean b = true;
//		String a = b.toString(),a1 = b.toString()+"";
//		System.out.println(gson.toJson(a));
//		System.out.println(gson.toJson(b));
//		String stockId = "2330";
//		Stock yStock = null;
//		try {
//			yStock = YahooFinance.get(stockId + ".TW");
//		} catch (Exception e) {
//			log.error("trackStockInfo fail, stockId({}) not exist => {}", stockId, e.toString());
//			return ;
//		}
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(2017, 7, 25, 10, 01);
//		List<HistoricalQuote> historicalQuotes = yStock.getHistory(calendar);
//		for(HistoricalQuote historicalQuote : historicalQuotes){
//			System.out.println(historicalQuote.toString());
//		}
//		System.out.println(new Date().getTime());
		List<String> list = new ArrayList<>();
		list.add("0");
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		for(int i=0;i<list.size();i++){
			if(list.get(i).equals("2"))
				list.remove(i);
			System.out.println(list.get(i));
			System.out.println("i:"+i);
			System.out.println("list size:"+list.size());
		}
		
	}
}
