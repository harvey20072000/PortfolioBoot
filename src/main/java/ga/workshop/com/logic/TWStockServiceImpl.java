package ga.workshop.com.logic;

import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ga.workshop.com.model.StockInfo;
import ga.workshop.com.model.TrackedTarget;
import ga.workshop.com.util.Const;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TWStockServiceImpl implements TWStockService {

	private Map<String, StockInfo> stockInfos = new HashMap<>();
	
	HttpClient client = new HttpClient();
	JsonParser parser = new JsonParser();
	Thread keepThread;
	
	@PostConstruct  
    public void  init(){  
		doInitConnection();
    }
	
//	@Override
//	public String getPrice(String stock) {
//		StockInfo info = null;
//		Stock s = parseStock(stock);
//		if (s != null) {
//			if ((info = queryStockInfo(s.getId(), s.isOTC()))!=null) {
//				stockInfos.put(info.getId(), info);
//			} else {
//				log.info("stock({}) not exist!", stock);
//			}
//		}
//		return info!=null?info.getSoldPrice():null;
//	}
//
//	public StockInfo getInfo(String stock) {
//		StockInfo info = null;
//		Stock s = parseStock(stock);
//		if (s != null) {
//			if ((info = queryStockInfo(s.getId(), s.isOTC()))!=null) {
//				stockInfos.put(info.getId(), info);
//			} else {
//				log.info("stock({}) not exist!", stock);
//			}
//		}
//		return info!=null?info:null;
//	}
//	
//	private Stock parseStock(String stock) {
//		List<Stock> s = stockRepository.findByIdName(stock, stock);
//		if(s.isEmpty())
//			s = stockRepository.findByIdName(stock, "%" + stock + "%");
//		return s.size()==0?null:s.get(0);
//	}
	
	private StockInfo queryStockInfo(String stockId, boolean isOTC) {
		StockInfo info = null;
		if ((info = stockInfos.get(stockId))!=null &&	// 一分內有更新過的不用查詢
				info.getLastUpdateTime() + 60 * 1000 > new Date().getTime()) {	
			return info;
		}
		
//		try {
//			Connection con = Jsoup.connect("https://tw.stock.yahoo.com/q/q?s=" + stockId)
//					.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 "
//							+ "(KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").timeout(10000);
//		    Connection.Response resp = con.execute();
//		    if (resp.statusCode() == 200) {
//		    	Elements tds = con.get().select("td[bgcolor=#FFFfff]");
//		    	for (org.jsoup.nodes.Element element : tds) {
//		            System.out.println(element.text());   
//		        }
//		    }
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		StringBuilder url = new StringBuilder(
				"http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=");
		url.append(isOTC?"otc":"tse").append("_");
		url.append(stockId).append(".tw");
		url.append("&json=1&delay=0&_=").append(new Date().getTime());
		
		try {
			HttpMethod method = new GetMethod(url.toString());
			if (client.executeMethod(method) == HttpStatus.SC_OK) {
				String responseBody = new String(method.getResponseBody(), "utf-8");
//				System.out.println(new String(responseBody));
				log.debug("responseBody:" + responseBody);
				JsonObject root = parser.parse(responseBody).getAsJsonObject();
				JsonArray msgArray = root.getAsJsonArray("msgArray");
				if (msgArray.size()>0) {
					info = new StockInfo(msgArray.get(0).getAsJsonObject());
					stockInfos.put(info.getId(), info);
//					log.info("put stock({}) data", info.getId());
					return info;
				}
			}
//			Connection con = Jsoup.connect(url.toString())
//					.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 "
//							+ "(KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").timeout(10000);
//			Connection.Response resp = con.execute();
//			if (resp.statusCode() == 200) {
//		    	JsonObject root = parser.parse(new String(resp.body())).getAsJsonObject();
//				JsonArray msgArray = root.getAsJsonArray("msgArray");
//				if (msgArray.size()>0) {
//					info = new StockInfo(msgArray.get(0).getAsJsonObject());
//					stockInfos.put(info.getId(), info);
////					log.info("put stock({}) data", info.getId());
//					return info;
//				}
//		    }
		} catch (ConnectException e) {
			if (doInitConnection()) {
				info =  queryStockInfo(stockId, false);
			}
		} catch (Exception e) {
			log.error("query TWstock info fail, exception => {}", e.toString());
			log.debug("start query Wantgoo");
			if(info == null && (info = queryStockInfo_FromWantgoo(stockId, isOTC)) != null){
				return info;
			}
		} finally {
//			method.releaseConnection();
		}
		return info;
	}
	
	private boolean doInitConnection() {
		log.debug("do init twse connection");
		final HttpMethod method = new GetMethod(
				"http://mis.twse.com.tw/stock/fibest.jsp?stock=1101&json=1&delay=0&_=" + 
						new Date().getTime());
		try {
			if (client.executeMethod(method) == HttpStatus.SC_OK) {
				if (keepThread!=null) {
					keepThread.interrupt();
				}	
				keepThread = new Thread() {
					public void run() {
						try {
							client.executeMethod(method);
						} catch (Exception e) {}
						try {
							TimeUnit.SECONDS.sleep(30);
						} catch (InterruptedException e) {
						}
					}
				};
				keepThread.start();
				return true;
			}
		} catch (Exception e) {
			log.error("do init twse connection fail, exception => {}", e.toString());
		} finally {
//			method.releaseConnection();
		}
		return false;
	}
	
	public static void main(String[] argv) throws InterruptedException {
//		TWStockServiceImpl test = new TWStockServiceImpl();
//		test.queryStockInfo("2498", true);
//		TimeUnit.SECONDS.sleep(1);
//		test.queryStockInfo("2498", true);
		
//		TimeUnit.SECONDS.sleep(1);
//		test.queryStockInfo("1101");
//		TimeUnit.SECONDS.sleep(1);
//		test.queryStockInfo("2353");
		
		try {
//			Connection con = Jsoup.connect("https://tw.stock.yahoo.com/q/q?s=2498")
//					.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 "
//							+ "(KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").timeout(10000);
//		    Connection.Response resp = con.execute();
//		    if (resp.statusCode() == 200) {
//		    	Elements tds = con.get().select("td[bgcolor=#FFFfff]");
//		    	for (org.jsoup.nodes.Element element : tds) {
//		            System.out.println(element.text());   
//		        }
//		    }
			String url = "http://www.wantgoo.com/stock/2330";
			Document doc = Jsoup.connect(url).get();
			String response = doc.toString();
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private StockInfo queryStockInfo_FromWantgoo(String stockId, boolean isOTC){	// 爬"玩股"
		try {
			String url = "http://www.wantgoo.com/stock/"+stockId,temp;
			Document doc = Jsoup.connect(url).get();
			Elements targets = doc.select("script");
			JsonObject object;
			StockInfo stockInfo = null;
			for(Element target : targets){
				if(target.toString().contains("var StockInfo = {")){
					temp = target.toString();
					temp = temp.substring(temp.indexOf("var StockInfo = ")+"var StockInfo = ".length(),temp.lastIndexOf(";"));
					object = parser.parse(temp).getAsJsonObject();
					stockInfo = new StockInfo();
					stockInfo.setId(stockId);
					stockInfo.setName(object.get("StockName").getAsString());
					stockInfo.setSoldPrice(object.get("Deal").getAsString());
					stockInfo.setHighestPrice(object.get("High").getAsString());
					stockInfo.setLowestPrice(object.get("Low").getAsString());
					stockInfo.setPriceChange(object.get("Change").getAsString());
					stockInfo.setTotal(object.get("TotalVolume").getAsString());
					stockInfo.setLastClosePrice(object.get("Last").getAsString());
					stockInfo.setLastUpdateTime(new Date().getTime());
					
					stockInfos.put(stockInfo.getId(), stockInfo);
					break;
				}
			}
			return stockInfo;
		} catch (Exception e) {
			log.error("queryStockInfo_FromWantgoo fail, exception => {}", e.toString());
		}
		return null;
	}

//	@Override
//	public Stock getStockById(String stockId) {
//		return stockRepository.findOne(stockId);
//	}
//
//	@Override
//	public Stock getStockByName(String stockName) {
//		List<Stock> stocks = 
//				stockRepository.findByName("%" + stockName + "%");
//		return stocks.size()>0?stocks.get(0):null;
//	}
	
	@Override
	public TrackedTarget trackStockInfo(TrackedTarget trackedTarget) {
		StockInfo info = null;
		StringBuilder url = new StringBuilder(
				"http://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=");
		url.append(trackedTarget.getDetail().getMarket().contains("櫃")?"otc":"tse").append("_");
		url.append(trackedTarget.getStockId()).append(".tw");
		url.append("&json=1&delay=0&_=").append(new Date().getTime());
		
		try {
			HttpMethod method = new GetMethod(url.toString());
			if (client.executeMethod(method) == HttpStatus.SC_OK) {
				String responseBody = new String(method.getResponseBody(), "utf-8");
				log.debug("responseBody:" + responseBody);
				JsonObject root = parser.parse(responseBody).getAsJsonObject();
				JsonArray msgArray = root.getAsJsonArray("msgArray");
				if (msgArray.size()>0) {
					info = new StockInfo(msgArray.get(0).getAsJsonObject());
					stockInfos.put(info.getId(), info);
					log.info("put stock({}) data", info.getId());
					return inputInfoToTarget(trackedTarget, info);
				}
			}
		} catch (ConnectException e) {
			if (doInitConnection()) {
				info =  queryStockInfo(trackedTarget.getStockId(), false);
			}
		} catch (Exception e) {
			log.error("query stock info fail, exception => {}", e.toString());
		} finally {
			if(info == null && (info = queryStockInfo_FromWantgoo(trackedTarget.getStockId(), trackedTarget.getDetail().getMarket().contains("櫃"))) != null){
				return inputInfoToTarget(trackedTarget, info);
			}
		}
		return null;
	}

	private TrackedTarget inputInfoToTarget(TrackedTarget trackedTarget, StockInfo info){
//		if (checkCache(stockId)) { // 檢查cache，有值則回傳
//			return stockInfos_cache.get(stockId);
//		}

		Date date = new Date();
		if(date.getDay() == 0 || date.getDay() == 6)	// 若是六日則不紀錄
			return trackedTarget;
		if(date.getHours() < 9)	// 九點之前不紀錄
			return trackedTarget;
		String dateString = Const.SDF_NO_TIME.format(new Date());
		Number temp = 0.0;
		try {
			temp = Double.parseDouble(info.getSoldPrice());
		} catch (Exception e) {
			log.error("inputInfoToTarget fail, exception => {}",e.toString());
		}
		trackedTarget.getTrackedPrices().put(dateString, temp.doubleValue());
		
		temp = Integer.parseInt(info.getTotal());
		trackedTarget.getTrackedVolumes().put(dateString, temp.doubleValue());
		
		return trackedTarget;
	}

}
