package ga.workshop.com.model;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Entity
//@Table(name="Target", schema="PORTFOLIO")
public class UserAssets {

	// 
	 
	@Id
	@Column(name = "NAME")
	private String name;								// user名稱
	
	@Column(name = "TRACKED_TARGETS")
	private Map<String, TrackedTarget> trackedTargets;	// 追蹤股票
	
	@Column(name = "TARGET_ALERTS")
	private Map<String, TargetAlert> targetAlerts;		// 警示
	
	//
	
	@Column(name = "UPDATE_TIME")						// 更新時間
	private Date updateTime;
	
	public UserAssets(){
		this.trackedTargets = new TreeMap<>();
		this.targetAlerts = new HashMap<>();
	}

	public UserAssets(String name){
		this();
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("User [name = %s , tracked_targets_count = %s , target_alerts_count = %s ]", 
				this.name,
				this.trackedTargets.size(),
				this.targetAlerts.size());
	}
	
}
