package ga.workshop.com.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
//@Entity
//@Table(name="Target", schema="PORTFOLIO")
public class TargetAlert {

	@Id 
	@Column(name = "ID")			
	private String id;									// 股票代號-編號
	
	@Column(name = "STOCK_ID")			
	private String stockId;								// 股票代號
	
	@Column(name = "CODE")			
	private String code;								// 編號
	
	@Column(name = "TYPE")
	private String type;								// 類別
	
	@Column(name = "COMPARE_SYMBOL")
	private String compareSymbol;						// 比較符號
	
	@Column(name = "THRESHOLD_VALUE")
	private String thresholdValue;						// 門檻值
	
	@Column(name = "REPEAT_TIMES")
	private String repeatTimes = "";					// 重複次數
	
	@Column(name = "REPEAT_TIMES_IN_INT")
	private int repeatTimesInInt = -1;					// 重複次數(數字)
	
	@Column(name = "IS_ON")
	private boolean isOn = true;						// 是否啟用
	
	@Column(name = "IS_TRIGGERED")
	private boolean isTriggered;						// 是否達成條件
	
	@Column(name = "NOTE")
	private String note = "";							// 備註
	
	@Column(name = "UPDATE_TIME")						
	private Date updateTime;							// 更新時間
	
	public TargetAlert(){
	}

	public TargetAlert(String stockId, String code) {
		this();
		this.stockId = stockId;
		this.code = code;
		this.id = stockId + "-" + code;
	}

	public void setRepeatTimes(String repeatTimes){
		this.repeatTimes = repeatTimes;
		if(repeatTimes != null && !"".equals(repeatTimes) && repeatTimes.matches("[0-9]+")){
			try {
				this.repeatTimesInInt = Integer.parseInt(repeatTimes);
			} catch (Exception e) {
				log.error("setRepeatTimesInInt fails, exception => {}",e.toString());
				this.repeatTimesInInt = -1;
			}
		}
	}
	
	public void setOn(Boolean isOn) {
		this.isOn = isOn;
	}
	
}
