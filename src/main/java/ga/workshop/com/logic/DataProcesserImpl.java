package ga.workshop.com.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ga.workshop.com.model.Target;
import lombok.extern.slf4j.Slf4j;
import tw.com.geosat.util.WebUtil;

@Slf4j
@Service
public class DataProcesserImpl implements DataProcesser {

	private JsonParser parser = new JsonParser();
	private Gson gson = new Gson();
	private String charset = "utf-8";
	//https://www.cmoney.tw/finance/technicalanalysis.aspx?s=3003
	//http://www.goodinfo.tw/StockInfo/StockDetail.asp?STOCK_ID={STOCK_ID}
	private final String GOODINFO_URL = "https://www.cmoney.tw/finance/technicalanalysis.aspx?s={STOCK_ID}";
	
	@Override
	public Target processData(Target target ,String jsonStr) throws Exception{
		JsonObject jsonObject, tempObject, tempObject2;
		String label,temp;
		
		jsonObject = parser.parse(jsonStr).getAsJsonObject();
		if (jsonObject.get("error") != null) {
			String errorMsg = jsonObject.getAsJsonObject("error").get("message").getAsString();
//			log.error("stock : {} , fail to process => {} !", target.getStockId(), errorMsg);
			throw new Exception(errorMsg);
		}

		tempObject = jsonObject.getAsJsonObject("1");
		log.debug("stock : {} , calculate {}", target.getStockId(), label = tempObject.get("label").getAsString());
		tempObject2 = tempObject.getAsJsonObject("data");
		target.setStockName(tempObject2.get("ticker_name").getAsString().split(" ")[1]);
		target.setCategory(tempObject2.get("category").getAsString());
		target.setMainBusiness(tempObject2.get("main_business").getAsString().toLowerCase().replaceAll("<br>", "；"));
		target.setMarket(tempObject2.get("stock_exchange").getAsString());
		temp = tempObject2.get("latest_closing_price").getAsString();
		target.setLatestClosingPriceAndDate(temp.split("[ ,　]+")[0] + "　　" + temp.split("[ ,　]+")[1]);

		tempObject = jsonObject.getAsJsonObject("2");	// TimeM
		label = tempObject.get("label").getAsString();
		target.setN12m(getStrings(tempObject.getAsJsonArray("data"), "N12M"));
		
		tempObject = jsonObject.getAsJsonObject("4");	// TimeY
		label = tempObject.get("label").getAsString();
		target.setYears(getStrings(tempObject.getAsJsonArray("data"), label));
		
		// 數字是jsonObject的member的編號，各代表不同資料
		target = inputJsonValue(jsonObject, target);
		
		// 計算是否適合投資，日後可依照不同產業做權重調整
		try {
			if(target.getStockId().equals("5258")){
				System.out.println("start");
			}
			target = judgeIsShresholdPassed(target);
		} catch (Exception e) {
			log.error("judgeIsShresholdPassed fail , target : {} , exception => {}",target.getStockId(),e.toString());
		}
		
		target.setUrl(GOODINFO_URL.replace("{STOCK_ID}", target.getStockId()));

		target.setUpdateTime(new Date());
		return target;
	}
	
	private Target inputJsonValue(JsonObject jsonObject,Target target){
		JsonObject tempObject;
		String label;
		List<Double> tempList;
		JsonArray tempArray;
		Double tempDouble;
		String[] membersToGetArray = new String[]{"17","65","11","83","95","96","86","98","99","102","220","固定資產比例","131","92"};
		//83:負債比(季)__65:EPS(季)__92:存貨週轉(季)__95:毛利率(季)__96:利益率(季)__86:速動比(季)__17:本益比(月)__98:ROA(季)__99:ROE(季)__102:盈再率(季)__11:價格(月)__220:現金股利發放率(年)__131:營收年增率(季)
		for (String key : membersToGetArray) {
			if ("固定資產比例".equals(key)) { // 188要除以189(固定資產比例)
				target.setStatic_assets_ratios_y(
						calValuesByTerms("percentage","divide", getValues(jsonObject.getAsJsonObject("188").getAsJsonArray("data"), null), getValues(jsonObject.getAsJsonObject("189").getAsJsonArray("data"), null)));
			} else {
				tempObject = jsonObject.getAsJsonObject(key);
				log.debug("stock : {} , calculate　{}", target.getStockId(), label = tempObject.get("label").getAsString());
				if ("11".equals(key)) {
					tempArray = tempObject.getAsJsonObject("data").getAsJsonObject("month").getAsJsonArray("data");
					target.setPrice(getCurrentValue(tempArray, label));
					if(target.getMax_ben_yi_ratio() != null && target.getEps() != null)
						target.setCal_max_price(target.getMax_ben_yi_ratio()*getCurrentModValue(target.getEpss_y(), null));
					if(target.getMin_ben_yi_ratio() != null && target.getEps() != null)
						target.setCal_min_price(target.getMin_ben_yi_ratio()*getCurrentModValue(target.getEpss_y(), null));
					target.setPrices_N12M(getValues(tempArray, "N12M"));
					target.setPrice_slope(calSlope(tempArray, label));
				} else {
					tempArray = tempObject.getAsJsonArray("data");
					if ("92".equals(key)) {
						tempDouble = getCurrentValue(tempArray, label);
//						target.setCuen_huo_round_day(tempDouble == null ? null : (tempDouble >= 0.125 ? (90 / tempDouble) : 999));
						target.setCuen_huo_round_days_y(calBiggerRangeValues(getValues(tempArray, label),4,"avg"));
						target.setCuen_huo_round_day(getCurrentModValue(target.getCuen_huo_round_days_y(), label));
						target.setCuen_huo_round_slope(calSlope(tempArray, label));
					} else if ("83".equals(key)) {
//						target.setFu_zhai_ratio(getCurrentValue(tempArray, label));
						target.setFu_zhai_ratios_y(calBiggerRangeValues(getValues(tempArray, label), 4,"avg"));
						target.setFu_zhai_ratio(getCurrentModValue(target.getFu_zhai_ratios_y(), label));
						target.setFu_zhai_slope(calSlope(tempArray, label));
					} else if ("86".equals(key)) {
//						target.setSu_don_ratio(getCurrentValue(tempArray, label));
						target.setSu_don_ratios_y(calBiggerRangeValues(getValues(tempArray, label), 4,"avg"));
						target.setSu_don_ratio(getCurrentModValue(target.getSu_don_ratios_y(), label));
						target.setSu_don_slope(calSlope(tempArray, label));
					} else if ("95".equals(key)) {
//						target.setYing_yie_mao_li_ratio(getCurrentValue(tempArray, label));
						target.setYing_yie_mao_li_ratios_y(calBiggerRangeValues(getValues(tempArray, label), 4,"avg"));
						target.setYing_yie_mao_li_ratio(getCurrentModValue(target.getYing_yie_mao_li_ratios_y(), label));
						target.setYing_yie_mao_li_slope(calSlope(tempArray, label));
					} else if ("96".equals(key)) {
//						target.setYing_yie_li_yi_ratio(getCurrentValue(tempArray, label));
						target.setYing_yie_li_yi_ratios_y(calBiggerRangeValues(getValues(tempArray, label), 4,"avg"));
						target.setYing_yie_li_yi_ratio(getCurrentModValue(target.getYing_yie_li_yi_ratios_y(), label));
						target.setYing_yie_li_yi_slope(calSlope(tempArray, label));
					} else if ("65".equals(key)) {
//						target.setEps(getCurrentValue(tempArray, label));
						target.setEpss_y(calBiggerRangeValues(getValues(tempArray, label), 4,"sum"));
						target.setEps(getCurrentModValue(target.getEpss_y(), label));
						target.setEps_slope(calSlope(tempArray, label));
					} else if ("17".equals(key)) {
						target.setBen_yi_ratio(getCurrentValue(tempArray, label));
						target.setBen_yi_ratios_y(calBiggerRangeValues(getValues(tempArray, label), 12,"avg"));
						tempList = getMinMaxValue(tempArray, label);
						target.setMin_ben_yi_ratio(tempList.get(0));
						target.setMax_ben_yi_ratio(tempList.get(1));
					} else if ("98".equals(key)) {
//						target.setRoa(getCurrentValue(tempArray, label));
						target.setRoas_y(calBiggerRangeValues(getValues(tempArray, label), 4,"sum"));
						target.setRoa(getCurrentModValue(target.getRoas_y(), label));
						target.setRoa_slope(calSlope(tempArray, label));
					} else if ("99".equals(key)) {
//						target.setRoe(getCurrentValue(tempArray, label));
						target.setRoes_y(calBiggerRangeValues(getValues(tempArray, label), 4,"sum"));
						target.setRoe(getCurrentModValue(target.getRoes_y(), label));
						target.setRoe_slope(calSlope(tempArray, label));
					} else if ("102".equals(key)) {
						target.setYing_zai_ratios_y(calBiggerRangeValues(getValues(tempArray, label), 4,"avg"));
						target.setYing_zai_ratio(getCurrentModValue(target.getYing_zai_ratios_y(), label));
						target.setYing_zai_slope(calSlope(tempArray, label));
					} else if ("131".equals(key)) {
						target.setYing_sho_up_ratios_y(calBiggerRangeValues(getValues(tempArray, label), 4,"avg"));
						target.setYing_sho_up_ratio(getCurrentModValue(target.getYing_sho_up_ratios_y(), label));
						target.setYing_sho_up_slope(calSlope(tempArray, label));
					} else if ("220".equals(key)) {
//						target.setXian_zin_release_ratio(getCurrentValue(tempArray, label));
						target.setXian_zin_release_ratios_y(getValues(tempArray, label));
						target.setXian_zin_release_slope(calSlope(tempArray, label));
					}
				}
			}
			
		}
		return target;
	}
	
//	private tryCatch
	
	private Double getCurrentValue(JsonArray dataArray , String label){
		for(int i=1;i<=dataArray.size();i++){
			if(!dataArray.get(dataArray.size()-i).getAsJsonArray().get(1).getAsString().matches("[-.,0-9]+")){
				continue;
			}
			return dataArray.get(dataArray.size()-i).getAsJsonArray().get(1).getAsDouble();
		}
		log.error("{} getCurrentValue fail",label);
		return null;
	}
	
	private Double getCurrentModValue(List<Double> list,String label){
		return list.get(list.size()-1);
//		for(int i=1;i<=list.size();i++){
//			if(list.get(list.size()-i) == null){
//				continue;
//			}
//			return list.get(list.size()-i);
//		}
//		log.error("{} getCurrentModValue fail",label);
//		return null;
	}
	
	private List<Double> getMinMaxValue(JsonArray dataArray , String label){
		Double max = 0d,min = 10000d,tempDouble;
		JsonElement tempElement;
		List<Double> list = new LinkedList<>();
		if("本益比".equals(label)){
			for(int i=dataArray.size()-1;i>=dataArray.size()-36;i--){
				if((tempElement = dataArray.get(i).getAsJsonArray().get(1)) != null && tempElement.getAsString().trim().matches("[-.,0-9]+")){
					tempDouble = Double.parseDouble(tempElement.getAsString().trim());
					if(tempDouble >= max)
						max = tempDouble;
					if(tempDouble <= min)
						min = tempDouble;
				}
			}
			list.add(min);
			list.add(max);
			return list;
		}
//		log.error("{} getMinMaxValue fail",label);
		System.out.println(label + " getMaxMinValue no value !");
		return list;
	}

	private Double calSlope(JsonArray dataArray, String label){
		List<Double> xList = new ArrayList<>(), yList = new ArrayList<>();
		JsonArray tempArray;
		Double tempDouble,initializer = 0d;
		String temp;
		int count = 0;
		for (JsonElement element : dataArray) {
			tempArray = element.getAsJsonArray();
			xList.add(tempArray.get(0).getAsDouble());
			if(!tempArray.get(1).getAsString().matches("[-.,0-9]+")){
				xList.clear();
				yList.clear();
				continue;
			}
			tempDouble = tempArray.get(1).getAsDouble();
			if(label.equals("存貨週轉"))
				tempDouble = 100 / tempDouble;	// 乘上100%
//			
			yList.add(tempDouble);
		}
		if(yList.size() == 0){
			log.error("{} calSlope fail",label);
			return null;
		}
		Double xAvg = calAvg(xList), yAvg = calAvg(yList), upZone = 0d, downZone = 0d;
		for (int i = 0; i < xList.size(); i++) {
			upZone += ((xList.get(i) - xAvg) * (yList.get(i) - yAvg));
			downZone += Math.pow(xList.get(i) - xAvg, 2);
		}
		return upZone / downZone;
	}
	
	/*
	 * 取得原始陣列的值(會做初步轉換)
	 */
	private List<Double> getValues(JsonArray dataArray, String label) {
		List<Double> list = new ArrayList<>();
		JsonArray tempArray;
		Double tempDouble;
		for(JsonElement element : dataArray){
			tempArray = element.getAsJsonArray();
			if(!tempArray.get(1).getAsString().matches("[-.,0-9]+")){
				list.add(null);
				continue;
			}
			if("TimeM".equals(label)){
				
			}
			tempDouble = tempArray.get(1).getAsDouble();
			if("存貨週轉".equals(label))
				tempDouble = (tempDouble >= 0.125 ? (90 / tempDouble) : 999);	// 天數
			list.add(tempDouble);
		}
		if("N12M".equals(label)){
			return list.subList(list.size()-12, list.size());
		}
		return list;
	}
	
	/*
	 * 把日、月資料計算成月、年資料
	 * *sum的最新資料改成近X月or季
	 */
	private List<Double> calBiggerRangeValues(List<Double> list , int everyNum , String processType){
		if(everyNum == 0)
			everyNum = 4;
		int runTimes = list.size() % everyNum == 0 ? (list.size() / everyNum) : (list.size() / everyNum + 1);
		List<Double> retList = new ArrayList<>();
		if ("avg".equals(processType)) {
			for (int i = 0; i < runTimes; i++) {
				if (i == (runTimes - 1)) {
					retList.add(calAvg(list.subList(i * everyNum, list.size())));
					break;
				}
				retList.add(calAvg(list.subList(i * everyNum, (i + 1) * everyNum)));
			}
		} else if ("sum".equals(processType)) {
			for (int i = 0; i < runTimes; i++) {
				if (i == (runTimes - 1)) {
					retList.add(calSum(list.subList(list.size() - everyNum, list.size())));
					break;
				}
				retList.add(calSum(list.subList(i * everyNum, (i + 1) * everyNum)));
			}
		}
		return retList;
	}
	
	private List<String> getStrings(JsonArray dataArray, String label) {
		List<String> list = new ArrayList<>();
		JsonArray tempArray;
		for(JsonElement element : dataArray){
			tempArray = element.getAsJsonArray();
			if(tempArray.get(1).getAsString().contains("無")){
				list.add(null);
				continue;
			}
			if("TimeM".equals(label) || "N12M".equals(label) || "TimeY".equals(label)){
				list.add(tempArray.get(1).getAsString());
			}
		}
		if("N12M".equals(label)){
			return list.subList(list.size()-12, list.size());
		}
		return list;
	}
	
	private List calValuesByTerms(String label,String processType,List<Double>... arrays){
		List<List<Double>> lists = new ArrayList<>(); 
		List<Double> calList = new ArrayList<>();
		for(List<Double> array : arrays){
			lists.add(array);
		}
		if("divide".equals(processType) && lists.size() == 2){
			for(int i=0;i<lists.get(0).size();i++){
				if(lists.get(0).get(i) != null && lists.get(1).get(i) != null){
					if("percentage".equals(label)){
						calList.add(lists.get(0).get(i)/lists.get(1).get(i)*100);
					}else {
						calList.add(lists.get(0).get(i)/lists.get(1).get(i));
					}
				}else {
					calList.add(null);
				}
			}
		}
		return calList;
	}
	
	private Double calAvg(List<Double> list){
		Double total = 0d;
		int nums = 0;
		for(Double n : list){
			if(n == null)
				continue;
			total += n;
			nums++;
		}
		if(nums > 0)
			return total / nums;
		return null;
	}
	
	private Double calSum(List<Double> list){
		Double total = 0d;
		int nums = 0;
		for(Double n : list){
			if(n == null)
				continue;
			total += n;
			nums++;
		}
		if(nums == list.size()){
			return total;
		}else if (nums > 0) {
			return total/nums*list.size();
		}
		return null;
	}
	
	private Target judgeIsShresholdPassed(Target target) throws Exception{
		int isPass = 1;
		int[] passes = new int[6];
		
		// 近兩年營收成長率相加>0或當下營收成長>10
		// 或前年>去年，且當下成長>0
		if(target.getYing_sho_up_ratio() != null && target.getYing_sho_up_ratio() > 10){
			passes[0] = 1;
		}else if(target.getYing_sho_up_ratios_y().size() > 0){
			double tempTotal = 0d;
			int startIndex = 0;
			int compareNumSize = 2;
			List<Double> tempList = target.getYing_sho_up_ratios_y().subList(0, target.getYears().size());
			if(tempList.size() <= compareNumSize){
				startIndex = 0;
			}else {
				startIndex = tempList.size()-compareNumSize;
			}
			double temp;
			boolean isUp = false;
			for(int i=tempList.size()-1;i>=startIndex;i--){
				if(tempList.get(i) != null){
					tempTotal += tempList.get(i);
					temp = tempTotal;
					if (i == tempList.size() - 2 && tempTotal >= temp && target.getYing_sho_up_ratio() != null
							&& target.getYing_sho_up_ratio() > 0) {
						isUp = true;
					}
				}
			}
			if(tempTotal > 0 || isUp)
				passes[0] = 1;
		}
		
		// ROE回歸線斜率>0或近兩年ROE都不低於6
		if(target.getRoe_slope() != null && target.getRoe_slope() > 0){
			passes[1] = 1;
		}else if(target.getRoes_y().size() > 0){
			int judgeLimit = 6;
			int startIndex = 0;
			int compareNumSize = 2;
			List<Double> tempList = target.getRoes_y().subList(0, target.getYears().size());
			if(tempList.size() <= compareNumSize){
				startIndex = 0;
			}else {
				startIndex = tempList.size()-compareNumSize;
			}
			for(int i=tempList.size()-1;i>=startIndex;i--){
				if(tempList.get(i) == null)
					continue;
				if(tempList.get(i) < judgeLimit)
					break;
				if(i == startIndex)
					passes[1] = 1;
			}
		}
		
		// ROA回歸線斜率>0或近兩年ROA都不低於3
		if(target.getRoa_slope() != null && target.getRoa_slope() > 0){
			passes[2] = 1;
		}else if(target.getRoas_y().size() > 0){
			int judgeLimit = 3;
			int startIndex = 0;
			int compareNumSize = 2;
			List<Double> tempList = target.getRoas_y().subList(0, target.getYears().size());
			if(tempList.size() <= compareNumSize){
				startIndex = 0;
			}else {
				startIndex = tempList.size()-compareNumSize;
			}
			for(int i=tempList.size()-1;i>=startIndex;i--){
				if(tempList.get(i) == null)
					continue;
				if(tempList.get(i) < judgeLimit)
					break;
				if(i == startIndex)
					passes[2] = 1;
			}
		}
		
		// 營業利益率回歸線斜率>0且當下利益率>5
		// 或是當下利益率>10
		if(target.getYing_yie_li_yi_slope() != null && target.getYing_yie_li_yi_slope() > 0 
				&& target.getYing_yie_li_yi_ratio() != null && target.getYing_yie_li_yi_ratio() > 5){
			passes[3] = 1;
		}else if (target.getYing_yie_li_yi_ratio() != null && target.getYing_yie_li_yi_ratio() > 10) {
			passes[3] = 1;
		}
		
		// 當下盈再率低於150
		passes[4] = 1;
		if(target.getYing_zai_ratio() != null && target.getYing_zai_ratio() >= 150)
			passes[4] = 0;
//		if(target.getYing_zai_ratios_y().size() > 0){
//			int judgeLimit = 200;
//			int startIndex = 0;
//			int compareNumSize = 3;
//			List<Double> tempList = target.getYing_zai_ratios_y().subList(0, target.getYears().size());
//			if(tempList.size() <= compareNumSize){
//				startIndex = 0;
//			}else {
//				startIndex = tempList.size()-compareNumSize;
//			}
//			for(int i=tempList.size()-1;i>=startIndex;i--){
//				if(tempList.get(i) == null)
//					continue;
//				if(tempList.get(i) > judgeLimit)
//					break;
//				if(i == startIndex)
//					passes[4] = 1;
//			}
//		}
		
		// 當下股價<=80
		if(target.getPrice() != null && target.getPrice() <= 80){
			passes[5] = 1;
		}
//		List<Double> slopeList1 = new ArrayList<>();	// 越低越好的list
//		slopeList1.add(target.getFu_zhai_slope());
//		slopeList1.add(target.getCuen_huo_round_slope());
//		List<Double> slopeList2 = new ArrayList<>();	// 越高越好的list
//		slopeList2.add(target.getSu_don_slope());
//		slopeList2.add(target.getYing_yie_mao_li_slope());
//		slopeList2.add(target.getYing_yie_li_yi_slope());
//		slopeList2.add(target.getEps_slope());
//		
//		for(Double tempDouble : slopeList1){
//			if(tempDouble != null){
//				totalValidTermsNum++;
//				total += tempDouble;
//				if(tempDouble <= 0)
//					shresholdPassedNum++;
//			}
//		}
//		for(Double tempDouble : slopeList2){
//			if(tempDouble != null){
//				totalValidTermsNum++;
//				total += tempDouble;
//				if(tempDouble >= 0)
//					shresholdPassedNum++;
//			}
//		}
		// total > 0
		for(Integer i : passes){
			isPass *= i;
		}
		if (isPass > 0) {
			target.setPassThreshold("true");
		}else {
			target.setPassThreshold("false");
		}
		return target;
	}

	public static void main(String[] args) throws Exception {
		DataProcesserImpl impl = new DataProcesserImpl();
		String string = WebUtil.sendGet("https://statementdog.com/api/v1/fundamentals/1525/2016/1/2017/4/?queried_by_user=false&_=1499219282720");
		Target target = new Target();
		target.setStockId("1525");
		impl.processData(target, string);
		System.out.println(target);
	}
	
}
