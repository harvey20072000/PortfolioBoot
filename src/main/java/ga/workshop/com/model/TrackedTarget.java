package ga.workshop.com.model;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
//@Entity
//@Table(name="Target", schema="PORTFOLIO")
public class TrackedTarget {

	// 
	@Id 
	@Column(name = "STOCK_ID")			
	private String stockId;								// 股票代號
	
	@Column(name = "STOCK_NAME")
	private String stockName;							// 股票名稱
	
	@Column(name = "NOTE")
	private String note;								// 備註
	
	@Column(name = "TRACKED_PRICES")
	private Map<String, Double> trackedPrices;			// 追蹤股價
	
	@Column(name = "TRACKED_VOLUMES")
	private Map<String, Double> trackedVolumes;			// 追蹤交易量
	
	@Column(name = "EX_DIVIDEND_DATE")
	private String exDividendDate;						// 除息日
	
	@Column(name = "Detail")
	private Target detail;								// 股票細節
	
	//
	
	@Column(name = "UPDATE_TIME")						// 更新時間
	private Date updateTime;
	
	public TrackedTarget(){
		this.trackedPrices = new TreeMap<>();
		this.trackedVolumes = new TreeMap<>();
	}

	public TrackedTarget(String stockId){
		this();
		this.stockId = stockId;
	}
	
	public TrackedTarget(Target target){
		this();
		this.stockId = target.getStockId();
		this.stockName = target.getStockName();
		this.detail = target;
	}
	
}
