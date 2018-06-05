package ga.workshop.com.logic;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import ga.workshop.com.model.TrackedTarget;
import ga.workshop.com.util.Const;
import lombok.extern.slf4j.Slf4j;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

@Slf4j
@Service
public class YahooFinanceServiceImpl implements YahooFinanceService {

	@PostConstruct  
    public void  init(){  
    }
	
	
	public static void main(String[] argv) throws InterruptedException {
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
	public TrackedTarget trackStockInfo(TrackedTarget trackedTarget) throws Exception{
		
		Date date = new Date();
		if(date.getDay() == 0 || date.getDay() == 6)	// 若是六日則不紀錄
			return trackedTarget;
		if(date.getHours() < 9 || date.getHours() >= 19)	// 九點之前不紀錄
			return trackedTarget;
		
		String stockId = trackedTarget.getStockId();
//		if (checkCache(stockId)) { // 檢查cache，有值則回傳
//			return stockInfos_cache.get(stockId);
//		}

		Stock yStock = null;
		try {
			yStock = YahooFinance.get(stockId + ".TW");
		} catch (Exception e) {
			//log.error("trackStockInfo fail, stockId({}) not exist => {}", stockId, e.toString());
			//return trackedTarget;
			throw e;
		}

		StockQuote quote = yStock.getQuote();
		if(quote == null){
			//log.error("stockId({}) quote is null !",stockId);
			//return trackedTarget;
			throw new Exception("stockId("+stockId+") quote is null !");
		}
		
		String dateString = Const.SDF_NO_TIME.format(date);
		Number temp = 0.0;
//		try {
//			temp = quote.getPrice().doubleValue();
//		} catch (Exception e) {
//			log.error("trackStockInfo -> stock({}) getPrice fail, exception => {}",stockId,e.toString());
//		}
		temp = quote.getPrice().doubleValue();
		trackedTarget.getTrackedPrices().put(dateString, temp.doubleValue());
		
		temp = quote.getVolume()/1000.0;
		trackedTarget.getTrackedVolumes().put(dateString, temp.doubleValue());
		
		return trackedTarget;
	}

}
