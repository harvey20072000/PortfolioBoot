package ga.workshop.com.dao;

import java.util.Map;

import ga.workshop.com.model.Target;
import ga.workshop.com.model.User;
import ga.workshop.com.model.UserAssets;

@SuppressWarnings("rawtypes")
public interface TargetDAO {

	Map<String ,Target> inputData(String filePath , Map<String ,Target> map) throws Exception;
	
	boolean outputData(String filePath , Map map) throws Exception;
	
	boolean outputLine(String filePath , Map<String ,String> map) throws Exception;
	
	Map<String ,User> inputUsers(String filePath , Map<String ,User> map) throws Exception;
	
	Map<String ,User> inputUsersWithDatas(String filePath , Map<String ,User> map) throws Exception;
	
//	boolean outputUsers(String filePath , Map<String ,User> map) throws Exception;
	
	Map<String ,UserAssets> inputUsersAssets(String filePath , Map<String ,UserAssets> map) throws Exception;
	
	
}
