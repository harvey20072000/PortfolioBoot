package ga.workshop.com.logic;

import ga.workshop.com.model.TrackedTarget;

public interface TWStockService {

	/**
	 * 查個股股價
	 * @param stock				股票代號或名稱
	 * @return	股價
	 */
	//String getPrice(String stock);
	
	/**
	 * 查個股資訊
	 * @param stock				股票代號或名稱
	 * @return	個股資訊
	 */
	//StockInfo getInfo(String stock);
	
	/**
	 * 查股票名稱
	 * @param stockId				股票代號
	 * @return	股票名稱
	 */
	//Stock getStockById(String stockId);
	
	/**
	 * 查股票代號
	 * @param stockName				股票名稱
	 * @return	股票代號
	 */
	//Stock getStockByName(String stockName);
	
	TrackedTarget trackStockInfo(TrackedTarget trackedTarget);
}
