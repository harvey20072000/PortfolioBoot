package ga.workshop.com.model;


import java.util.Date;
import java.util.HashMap;
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
public class User {

	// 
	 
	@Id
	@Column(name = "NAME")
	private String name;								// 名稱
	
	@Column(name = "PASSWORD")
	private String password;							// 密碼
	
	@Column(name = "NOTE")
	private String note;								// 備註
	
	@Column(name = "TRACKED_TARGETS")
	private Map<String, TrackedTarget> trackedTargets;	// 追蹤股票
	
	@Column(name = "TARGET_ALERTS")
	private Map<String, TargetAlert> targetAlerts;		// 警示
	
	
	//
	
	@Column(name = "UPDATE_TIME")						// 更新時間
	private Date updateTime;
	
	public User(){
		this.trackedTargets = new TreeMap<>();
		this.targetAlerts = new HashMap<>();
	}

	public User(String name , String password){
		this();
		this.name = name;
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", password=" + password + "]";
	}
	
}
