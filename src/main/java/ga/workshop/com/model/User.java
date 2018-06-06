package ga.workshop.com.model;


import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;

import ga.workshop.com.util.Const;
import lombok.Getter;
import lombok.Setter;

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
	
	private String session;								// UUID.millisecondtime
	
	@Column(name = "NOTE")
	private String note;								// 備註
	
	//
	
	@Column(name = "UPDATE_TIME")						// 更新時間
	private Date updateTime;
	
	public User(){
		this.session = UUID.randomUUID()+"."+(new Date().getTime()+Const.SESSION_PASSOUT_INTERVAL);
	}

	public User(String name , String password){
		this();
		this.name = name;
		this.password = password;
	}

	@Override
	public String toString() {
		return String.format("User [name = %s , password = %s ]", 
				this.name,this.password);
	}
	
}
