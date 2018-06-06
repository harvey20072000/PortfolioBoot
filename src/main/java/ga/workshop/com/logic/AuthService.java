package ga.workshop.com.logic;

import java.util.Map;

import ga.workshop.com.model.User;

public interface AuthService {

	/**
	 * 初始化用戶資料
	 * @param userName
	 * @param password
	 * @return session
	 */
	String initUser(String userName,String password);
	
	/**
	 * 取得用戶資料
	 * @param userName
	 * @param password
	 * @return
	 */
//	User getUser(String userName,String password);
	
	/**
	 * 驗證並取得用戶資料
	 * @param session
	 * @return
	 */
	User checkAndGrabUserInfo(String session);
	
	Map<String, User> getUsersMap();
}
