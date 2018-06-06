package ga.workshop.com.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ga.workshop.com.dao.TargetDAO;
import ga.workshop.com.model.User;
import ga.workshop.com.util.Const;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	private Map<String, User> users = new HashMap<>();
	
	@Autowired
	private TargetDAO targetDAO;
	
	@PostConstruct  
    public void  init(){ 
		new Thread() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (Exception e) {}
				initCache();
			}
		}.start();
    }
	
	private void initCache() {
		int count = 1;
		Map<String, User> tempUsers = new HashMap<>();
		while(true){
			try {
				if ((tempUsers = targetDAO.inputUsers(Const.USERS_INPUT_FILE_PATH, tempUsers)).size() != 0 || count >= 10){
					log.debug("inputUsers success !!");
					break;
				}
			} catch (Exception e) {
				log.error("initCache fail , exception => {}", e.toString());
			}
			count++;
		}
		users.putAll(tempUsers);
		synchronized (users) {
			users.notifyAll();
		}
		log.info("initCache finished with size:{} !!",users.size());
	}

	@Override
	public User checkAndGrabUserInfo(String session) {
		try {
			String[] sessionParts = session.split("\\.");
			for(User user : users.values()) {
				if(user.getSession().contains(sessionParts[0]) && isValid(user.getSession(), session))
					return user;
			}
		} catch (Exception e) {}
		return null;
	}
	
	private boolean isValid(String src,String match) {
		String[] matches = match.split("\\."), srcs = src.split("\\.");
		try {
			if(!srcs[0].equals(matches[0]))
				return false;
			if(new Date(Long.parseLong(srcs[1])).before(new Date(Long.parseLong(matches[1]))))
				return false;
		} catch (Exception e) {}
		return true;
	}

	@Override
	public String initUser(String userName, String password) {
		boolean find = false;
		for(User user : users.values()) {
			if(user.getName().equals(userName) && user.getPassword().equals(password)) {
				find = true;
				break;
			}
		}
		if (find) {
			User user = new User(userName, password);
			users.put(userName, user);
			return user.getSession().split("\\.")[0];
		}
		return "";
	}
	
	@Override
	public Map<String, User> getUsersMap(){
		return this.users;
	}
	
}
