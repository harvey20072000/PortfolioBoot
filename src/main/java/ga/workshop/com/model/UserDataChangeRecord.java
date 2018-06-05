package ga.workshop.com.model;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDataChangeRecord {

	// 
	private String userName;								// 使用者名稱
	
	private volatile int cacheDataUpdatedCounts;			// 要和dataUpdatedCounts做比對，有不一樣就自動儲存
	
	private volatile int dataUpdatedCounts;
	
//	private volatile int cacheDataUpdatedCountsForThreads;	// 要和cacheDataUpdatedCounts做比對，有不一樣就做docheck
	
	
	//
	
	private Date updateTime;								// 更新時間
	
	public UserDataChangeRecord(){
	}

	public UserDataChangeRecord(String name){
		this();
		this.userName = name;
	}
}
