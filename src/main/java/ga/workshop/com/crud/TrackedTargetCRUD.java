package ga.workshop.com.crud;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.JsonParser;

import ga.workshop.com.dao.TargetDAO;
import ga.workshop.com.logic.AuthService;
import ga.workshop.com.logic.DataProcesser;
import ga.workshop.com.logic.TWStockService;
import ga.workshop.com.logic.YahooFinanceService;
import ga.workshop.com.model.Target;
import ga.workshop.com.model.TrackedTarget;
import ga.workshop.com.model.User;
import ga.workshop.com.model.UserAssets;
import ga.workshop.com.model.UserDataChangeRecord;
import ga.workshop.com.util.Const;
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
	private TargetCRUD targetCRUD;
	
	@Autowired
	private TargetAlertCRUD targetAlertCRUD;
	
	@Autowired
	private TWStockService twStockService;
	
	@Autowired
	private YahooFinanceService yahooFinanceService;
	
	@Autowired
	private AuthService authService;
	
	private Map<String ,UserAssets> usersAssets = new ConcurrentHashMap<>();
	private Map<String, UserDataChangeRecord> changeRecords = new ConcurrentHashMap<>();
	
	private Map<String, Checker> runningThreads = new ConcurrentHashMap<>();	// key是userName
	private Map<String, Integer> cacheDataUpdatedCountsMapForThreads = new ConcurrentHashMap<>();
	private Map<String, Integer> cacheTrackedSizeCountsMapForThreads = new ConcurrentHashMap<>();
	
	private int maxDataSend = 4;
	
	private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(10);
	
	private final long CACHE_INTERVAL = 1*60*1000; // 1分鐘
	
	private JsonParser parser = new JsonParser();
	
	
	public TrackedTargetCRUD(){}
	
	@PostConstruct
	private void init() {
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
		Map<String, UserAssets> tempUserAssetsMap = new HashMap<>();
		while(true){
			try {
				if(count == 1){
					tempUserAssetsMap = targetDAO.inputUsersAssets(Const.USERS_ASSETS_FILE_PATH, tempUserAssetsMap);
				}else if (count >= 2) {
					synchronized (authService.getUsersMap()) {
						while(authService.getUsersMap() == null || authService.getUsersMap().size() == 0) {
							log.debug("inputUsersAssets waiting......");
							authService.getUsersMap().wait();
						}
					}
					for(User user : authService.getUsersMap().values()) {
						tempUserAssetsMap.put(user.getName(), new UserAssets(user.getName()));
					}
				}
				if(tempUserAssetsMap.size() != 0 || count >= 10){
					log.debug("inputUsersAssets success !!");
					break;
				}
			} catch (Exception e) {
				log.error("initCache fail , count：{} , exception => {}",count, e.toString());
			}
			count++;
		}
		usersAssets.putAll(tempUserAssetsMap);
		for (String name : usersAssets.keySet())
			changeRecords.put(name, new UserDataChangeRecord(name));
	}
	
	/*
	 * 
	 */
	public String outputDatas(String userName) {
		UserDataChangeRecord record = changeRecords.get(userName);
		UserDataChangeRecord record2 = targetAlertCRUD.getChangeRecords().get(userName);
		record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts() + record2.getCacheDataUpdatedCounts());
		record.setDataUpdatedCounts(record.getDataUpdatedCounts() + record2.getDataUpdatedCounts());
		usersAssets.get(userName).setTargetAlerts(targetAlertCRUD.getUsersAssets().get(userName).getTargetAlerts());

		if (record.getCacheDataUpdatedCounts() == record.getDataUpdatedCounts())
			return "unchange";
		if (record.getCacheDataUpdatedCounts() < record.getDataUpdatedCounts()) {
			log.error("outputDatas fail, logic error");
			record.setDataUpdatedCounts(record.getCacheDataUpdatedCounts());
			return "logic error";
		}
		try {
			record.setDataUpdatedCounts(record.getCacheDataUpdatedCounts());
			targetDAO.outputData(Const.USERS_ASSETS_FILE_PATH, usersAssets);
			return "true";
		} catch (Exception e) {
			log.error("outputDatas fail, exception => {}", e.toString());
			return "false";
		}
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
	public String addTarget(String userName,String stockId){
		if (!isValidInput(stockId))
			return "false";
		UserDataChangeRecord record = changeRecords.get(userName);
		String[] stockIds = stockId.split(",");
		Target target;
		String report = null;
		for(String tempStockId : stockIds){
			target = targetCRUD.getCacheMap().get(tempStockId);
			usersAssets.get(userName).getTrackedTargets().putIfAbsent(tempStockId, new TrackedTarget(target));
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
	
	private boolean checkCache(String userName,String key){
    	if(usersAssets.get(userName).getTrackedTargets().get(key) != null)
    		return true;
    	return false;
    }
	
	/*
	 * 
	 */
	public TrackedTarget getTarget(String userName,String stockId) {
		if (!checkCache(userName,stockId))
			return null;
		return usersAssets.get(userName).getTrackedTargets().get(stockId);
	}
	
	/*
	 * 
	 */
	public int maxPages(String userName) {
		return usersAssets.get(userName).getTrackedTargets().size() % maxDataSend > 0 ? 
				(usersAssets.get(userName).getTrackedTargets().size() / maxDataSend + 1) : 
					(usersAssets.get(userName).getTrackedTargets().size() / maxDataSend);
	}
	
	/*
	 * 
	 */
	public List<TrackedTarget> listAll(String userName,int page) {
		List<TrackedTarget> retList = new LinkedList<>();
		Map<String,TrackedTarget> treeMap = new TreeMap<>(usersAssets.get(userName).getTrackedTargets());
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
	public String updateTarget(String userName,String stockId, String... args) {
		if(!checkCache(userName,stockId) || !isValidInput(stockId))
			return "false";
		String regex = ":";
		UserDataChangeRecord record = changeRecords.get(userName);
		TrackedTarget trackedTarget = usersAssets.get(userName).getTrackedTargets().get(stockId);
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
		usersAssets.get(userName).getTrackedTargets().put(stockId, trackedTarget);
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
	public String removeTarget(String userName,String stockId) {
		if (!checkCache(userName,stockId))
			return "false";
		UserDataChangeRecord record = changeRecords.get(userName);
		usersAssets.get(userName).getTrackedTargets().remove(stockId);
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
			for(UserAssets userAsset : usersAssets.values())
				cacheDataUpdatedCountsMapForThreads.put(userAsset.getName(), new Integer(0));
		}

		for(UserAssets userAsset : usersAssets.values()){
			record = changeRecords.get(userAsset.getName());
			threadKey = userAsset.getName();
			if (userAsset.getTrackedTargets().size() > 0
					&& cacheTrackedSizeCountsMapForThreads.size() == 0) {
				checker = new Checker(userAsset.getName(),userAsset.getTrackedTargets().values());
				runningThreads.put(threadKey, checker);
				cacheTrackedSizeCountsMapForThreads.put(threadKey, userAsset.getTrackedTargets().size());
				runCheck(checker);
				System.out.printf("tracker doCheck -> thread：[%s] init executed!%n",userAsset.getName());
			} else if(record.getCacheDataUpdatedCounts() > cacheDataUpdatedCountsMapForThreads.get(threadKey)){
				if(runningThreads.get(threadKey).getTrackedTargets().size() < userAsset.getTrackedTargets().size()){	// add
					checker = new Checker(userAsset.getName(),userAsset.getTrackedTargets().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("tracker doCheck -> thread：[%s] add executed!%n",userAsset.getName());
				}else if(runningThreads.get(threadKey).getTrackedTargets().size() == userAsset.getTrackedTargets().size()){	// update
					checker = new Checker(userAsset.getName(),userAsset.getTrackedTargets().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("tracker doCheck -> thread：[%s] update executed!%n",userAsset.getName());
				}else if (runningThreads.get(threadKey).getTrackedTargets().size() > userAsset.getTrackedTargets().size()) {	// delete
					checker = new Checker(userAsset.getName(),userAsset.getTrackedTargets().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("tracker doCheck -> thread：[%s] remove executed!%n",userAsset.getName());
				}
				cacheDataUpdatedCountsMapForThreads.put(threadKey, record.getCacheDataUpdatedCounts());
			}else {
				stpe.execute(new Checker(userAsset.getName(), userAsset.getTrackedTargets().values()));
				System.out.printf("tracker doCheck -> thread：[%s] auto tracking !%n",userAsset.getName());
			}
		}
		System.out.println("users count : "+usersAssets.size());
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
			System.out.println("TrackedTargetCRUD Checker running~~~~~~~~");
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
