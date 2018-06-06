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
import ga.workshop.com.model.TargetAlert;
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
public class TargetAlertCRUD {

	private final String DATA_URL = "https://statementdog.com/api/v1/fundamentals/{STOCK_ID}/2012/1/{YEAR}/4/?queried_by_user=false&_={TIME_STAMP}";
    
	@Autowired
	private TargetDAO targetDAO;
	
	@Autowired
	private DataProcesser dataProcesser;
	
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
	
	private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(10);
	
	private JsonParser parser = new JsonParser();
	
	
	public TargetAlertCRUD(){
		
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
	public String outputDatas(String userName){
		UserDataChangeRecord record = changeRecords.get(userName);
		
		if(record.getCacheDataUpdatedCounts() == record.getDataUpdatedCounts())
			return "unchange";
		if(record.getCacheDataUpdatedCounts() < record.getDataUpdatedCounts()){
			log.error("outputDatas fail, logic error");
			record.setDataUpdatedCounts(record.getCacheDataUpdatedCounts());
			return "logic error";
		}
		try {
			record.setDataUpdatedCounts(record.getCacheDataUpdatedCounts());
			targetDAO.outputData(Const.USERS_ASSETS_FILE_PATH, usersAssets);
			return "true";
		} catch (Exception e) {
			log.error("outputDatas fail, exception => {}",e.toString());
			return "false";
		}
	}
	
	private boolean isValidInput(String... inputs){
    	for(String input : inputs){
    		if(input == null || "".equals(input) || "undefined".equals(input)){
    			return false;
    		}
    	}
    	return true;
    }
	
	/*
	 * 新增
	 */
	public String addTarget(String userName,String stockId, String... args) {
		if (!isValidInput(stockId))
			return "false";
		UserDataChangeRecord record = changeRecords.get(userName);
		
		int codeCount = 0;
		while(true){
			codeCount++;
			if(usersAssets.get(userName).getTargetAlerts().containsKey(stockId+"-"+codeCount)){
				continue;
			}
			break;
		}
		TargetAlert alert = new TargetAlert(stockId,codeCount+"");
		
		String regex = ":";
		for(String arg : args){
			for(Field field : alert.getClass().getDeclaredFields()){
				if(field.getName().toLowerCase().equals(arg.split(regex)[0].toLowerCase())){
					try {
						alert.getClass().getMethod("set"+firstCharToUpper(field.getName()), String.class).invoke(alert, arg.split(regex)[1]);
						break;
						// field.set(this, arg.split("-")[1]);
					} catch (Exception e) {
						log.error("addTarget -> cannot invoke method from arg({}) ,exception => {}",arg,e.toString());
					}
				}
			}
		}
		
		usersAssets.get(userName).getTargetAlerts().put(alert.getId(), alert);
		record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts()+1);
		log.debug("Alert for target({}) added!!", stockId);
		return "true";
	}
	
	private boolean checkCache(String userName,String key){
    	if(usersAssets.get(userName).getTargetAlerts().get(key) != null)
    		return true;
    	return false;
    }
	
	/*
	 * 抓取單一
	 */
	public TargetAlert getTarget(String userName,String alertId) {
		if (!checkCache(userName,alertId))
			return null;
		return usersAssets.get(userName).getTargetAlerts().get(alertId);
	}
	
	/*
	 * 取得全部
	 */
	public List<TargetAlert> listAll(String userName) {
		Map<String,TargetAlert> treeMap = new TreeMap<>(usersAssets.get(userName).getTargetAlerts());
		return new LinkedList<>(treeMap.values());
	}
	
	/*
	 * 取得全部觸發alert
	 */
	public List<TargetAlert> listAllTriggered(String userName) {
		Map<String,TargetAlert> treeMap = new TreeMap<>(usersAssets.get(userName).getTargetAlerts());
		List<TargetAlert> retList = new LinkedList<>();
		for(TargetAlert alert : treeMap.values()){
			if(alert.isOn() && alert.isTriggered()){
				retList.add(alert);
			}
		}
		return retList;
	}
	
	/*
	 * 修改
	 */
	public String updateTarget(String userName,String alertId, String... args) {
		if(!checkCache(userName,alertId) || !isValidInput(alertId))
			return "false";
		UserDataChangeRecord record = changeRecords.get(userName);
		TargetAlert alert = usersAssets.get(userName).getTargetAlerts().get(alertId);
		alert.setTriggered(false);
		
		String regex = ":";
		for(String arg : args){
			for(Field field : alert.getClass().getDeclaredFields()){
				if(field.getName().toLowerCase().equals(arg.split(regex)[0].toLowerCase())){
					try {
						if(field.getName().indexOf("is") >= 0){
							alert.getClass().getMethod("set"+firstCharToUpper(field.getName().replace("is", "")), Boolean.class).invoke(alert, Boolean.parseBoolean(arg.split(regex)[1]));
						}else {
							alert.getClass().getMethod("set"+firstCharToUpper(field.getName()), String.class).invoke(alert, arg.split(regex)[1]);
						}
						break;
						// field.set(this, arg.split("-")[1]);
					} catch (Exception e) {
						log.error("updateTarget -> cannot invoke method from arg({}) ,exception => {}",arg,e.toString());
					}
				}
			}
		}
		usersAssets.get(userName).getTargetAlerts().put(alertId, alert);
		record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts()+1);
		log.debug("Alert({}) updated!!", alertId);
		return "true";
	}
	
	private String firstCharToUpper(String input){
		if(input == null || "".equals(input))
			return "";
		return input.substring(0,1).toUpperCase()+input.substring(1);
	}
	
	/*
	 * 刪除
	 */
	public String removeTarget(String userName,String alertId) {
		if (!checkCache(userName,alertId))
			return "false";
		UserDataChangeRecord record = changeRecords.get(userName);
		usersAssets.get(userName).getTargetAlerts().remove(alertId);
		record.setCacheDataUpdatedCounts(record.getCacheDataUpdatedCounts()+1);
		log.debug("Alert({}) removed!!", alertId);
		return "true";
	}
	
	/*
	 * 自動跑警示
	 */
	@Scheduled(cron = "* */2 * * * *")
	public void doCheck(){
		Checker checker;
		String threadKey;
		System.out.println("alert doCheck executed!");
		UserDataChangeRecord record;
		
		if(cacheDataUpdatedCountsMapForThreads.isEmpty()){
			for(UserAssets userAsset : usersAssets.values())
				cacheDataUpdatedCountsMapForThreads.put(userAsset.getName(), new Integer(0));
		}

		for(UserAssets userAsset : usersAssets.values()){
			record = changeRecords.get(userAsset.getName());
			threadKey = userAsset.getName();
			if (userAsset.getTargetAlerts().size() > 0
					&& cacheTrackedSizeCountsMapForThreads.size() == 0) {
				checker = new Checker(userAsset.getName(),userAsset.getTargetAlerts().values());
				runningThreads.put(threadKey, checker);
				cacheTrackedSizeCountsMapForThreads.put(threadKey, userAsset.getTargetAlerts().size());
				runCheck(checker);
				System.out.printf("alert doCheck -> thread：[%s] init executed!%n",userAsset.getName());
			} else if(record.getCacheDataUpdatedCounts() > cacheDataUpdatedCountsMapForThreads.get(threadKey)){
				if(runningThreads.get(threadKey).getTargetAlerts().size() < userAsset.getTargetAlerts().size()){	// add
					checker = new Checker(userAsset.getName(),userAsset.getTargetAlerts().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("alert doCheck -> thread：[%s] add executed!%n",userAsset.getName());
				}else if(runningThreads.get(threadKey).getTargetAlerts().size() == userAsset.getTargetAlerts().size()){	// update
					checker = new Checker(userAsset.getName(),userAsset.getTargetAlerts().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("alert doCheck -> thread：[%s] update executed!%n",userAsset.getName());
				}else if (runningThreads.get(threadKey).getTargetAlerts().size() > userAsset.getTargetAlerts().size()) {	// delete
					checker = new Checker(userAsset.getName(),userAsset.getTargetAlerts().values());
					runningThreads.put(threadKey, checker);
					runCheck(checker);
					System.out.printf("alert doCheck -> thread：[%s] remove executed!%n",userAsset.getName());
				}
				cacheDataUpdatedCountsMapForThreads.put(threadKey, record.getCacheDataUpdatedCounts());
			}else {
				stpe.execute(new Checker(userAsset.getName(), userAsset.getTargetAlerts().values()));
				System.out.printf("alert doCheck -> thread：[%s] auto executing !%n",userAsset.getName());
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
		
		List<TargetAlert> targetAlerts = new ArrayList<>();
		
		public Checker() {
		}
		
		public Checker(String userName,Collection<TargetAlert> inputList){
			this();
			this.userName = userName;
			this.targetAlerts.addAll(inputList);
		}
		
		@Override
		public void run(){
			System.out.println("Checker running~~~~~~~~");
			int dataProcessedNum = 0;
			UserAssets userAsset = usersAssets.get(userName);
			TargetAlert resultAlert;
			Map<String, List<TargetAlert>> tempAlertsMap = new ConcurrentHashMap<>();
			for(TargetAlert alert : targetAlerts){
				if(!tempAlertsMap.containsKey(alert.getStockId())){
					tempAlertsMap.put(alert.getStockId(), new LinkedList<>());
				}
				tempAlertsMap.get(alert.getStockId()).add(alert);
			}
			List<TargetAlert> tempList;
			for(TrackedTarget trackedTarget : userAsset.getTrackedTargets().values()){
				if((tempList = tempAlertsMap.get(trackedTarget.getStockId())) != null){
					for (TargetAlert alert : tempList) {
						try {
							System.out.printf("user：%s , running target alert：%s%n",userName,alert.getId());
							resultAlert = examAlert(trackedTarget, alert);
							userAsset.getTargetAlerts().put(resultAlert.getId(), resultAlert);
							if(resultAlert.isOn() && resultAlert.isTriggered()){
								//丟進mailservice
							}
							dataProcessedNum++;
						} catch (Exception e) {
							log.error("user：{} , alert：{} , Checker fail to execute , exception => {}"
									,userName,alert.getId(),e.toString());
							continue;
						}
					}
				}
			}
			System.out.printf("user : %s , 跑過 %d/%d 個警示！%n",userName,dataProcessedNum,targetAlerts.size());
		}
	}
	
	private TargetAlert examAlert(TrackedTarget trackedTarget, TargetAlert alert) {
		double compareValue1 = 0, compareValue2 = 0;
		List<Double> tempList = new LinkedList<>();
		try {
			if(alert.isOn()){
				if(alert.getRepeatTimesInInt() != 0){
					if("股價".equals(alert.getType())){
						tempList.addAll(trackedTarget.getTrackedPrices().values());
					}else if ("成交量".equals(alert.getType())) {
						tempList.addAll(trackedTarget.getTrackedVolumes().values());
					}
					compareValue1 = tempList.get(tempList.size()-1);
					compareValue2 = Double.parseDouble(alert.getThresholdValue());
					if("≧".equals(alert.getCompareSymbol())){
						if(compareValue1 >= compareValue2){
							alert.setTriggered(true);
						}else {
							alert.setTriggered(false);
						}
					}else if ("≦".equals(alert.getCompareSymbol())) {
						if(compareValue1 <= compareValue2){
							alert.setTriggered(true);
						}else {
							alert.setTriggered(false);
						}
					}
				}
				if(alert.getRepeatTimesInInt() > 0){
					alert.setRepeatTimesInInt(alert.getRepeatTimesInInt() - 1);
				}else if (alert.getRepeatTimesInInt() == 0) {
					alert.setOn(false);
				}
			}
		} catch (Exception e) {
			log.error("examAlert(id:{}) fail, exception => {}", alert.getId(),e.toString());
		}
		return alert;
	}

	public static void main(String[] args) throws Exception {
		TargetAlertCRUD crud = new TargetAlertCRUD();
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
			if(!list.get(i).equals("5")){
				list.remove(i);
				i--;
				continue;
			}
			System.out.println("i\t:"+i);
			System.out.println("list.get(i):"+list.get(i));
			System.out.println("list size:"+list.size());
		}
		
	}
}
