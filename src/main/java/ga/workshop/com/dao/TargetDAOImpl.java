package ga.workshop.com.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import ga.workshop.com.model.Target;
import ga.workshop.com.model.User;
import ga.workshop.com.model.UserAssets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("rawtypes")
public class TargetDAOImpl implements TargetDAO {

	private JsonParser parser = new JsonParser();
	private Gson gson = new Gson();
	private GsonBuilder gsonBuilder = new GsonBuilder();
	private String charset = "utf-8";
	
	@Override
	public Map<String ,Target> inputData(String filePath , Map<String ,Target> map) throws Exception{
		InputStreamReader isr = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("inputData : " + file.getAbsolutePath());
			String line = null;
			isr = new InputStreamReader(new FileInputStream(file), charset);
			// FileReader fr = new FileReader(file);
			// InputStream is = new FileInputStream(destination);
			// Reader decoder = new InputStreamReader(new GZIPInputStream(is),
			// encoding); // 此行為關鍵代碼!
			BufferedReader br = new BufferedReader(/* decoder */isr);
//			JsonObject jsonObject, tempObject;
			JsonArray tempArray;
			String string;
			String[] array;
			while ((line = br.readLine()) != null) {
				array = line.trim().split("[\t ,　]+");
				if(array.length > 1)
					map.putIfAbsent(array[0].trim(), new Target(array[0].trim()));
			}
			
			br.close();
			// destination.delete();
			return map;
		} catch (Exception e) {
			log.error("inputData fail, exception => {}", e.toString());
//			e.printStackTrace();
			throw e;
		}finally {
			if(isr != null)
				isr.close();
		}
		
	}

	@Override
	public boolean outputData(String filePath , Map map) throws Exception{
		OutputStream os = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("outputData : " + file.getAbsolutePath());
			// create a new OutputStreamWriter
			os = new FileOutputStream(filePath);
			OutputStreamWriter writer = new OutputStreamWriter(os,charset);
			gsonBuilder.serializeSpecialFloatingPointValues();
			Gson bGson = gsonBuilder.create();
			String outputString = bGson.toJson(new LinkedList<>(map.values()));
			
			// write something in the file
			writer.write(outputString);

			// flush the stream
			writer.flush();
			return true;
		} catch (Exception ex) {
			log.error("outputData fail, exception => {}", ex.toString());
//			ex.printStackTrace();
			throw ex;
		}finally {
			if(os != null)
				os.close();
		}
	}
	
	@Override
	public boolean outputLine(String filePath , Map<String ,String> map) throws Exception{
		OutputStream os = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("outputLine : " + file.getAbsolutePath());
			// create a new OutputStreamWriter
			os = new FileOutputStream(filePath);
			OutputStreamWriter writer = new OutputStreamWriter(os,charset);

			for(String key : map.keySet()){
				writer.write(key+"　"+map.get(key)+"\r\n");
			}
//			String outputString = gson.toJson(new LinkedList<>(map.values()));

			// flush the stream
			writer.flush();
			return true;
		} catch (Exception ex) {
			log.error("outputLine fail, exception => {}", ex.toString());
//			ex.printStackTrace();
			throw ex;
		}finally {
			if(os != null)
				os.close();
		}
	}
	
	private File createFileIfNotExist(String filePath) throws IOException{
		File file = new File(filePath);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

	@Override
	public Map<String, User> inputUsers(String filePath, Map<String, User> map) throws Exception {
		InputStreamReader isr = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("inputUsers : " + file.getAbsolutePath());
			String line = null;
			isr = new InputStreamReader(new FileInputStream(file), charset);
			BufferedReader br = new BufferedReader(isr);
			String name,password;
			String[] array;
			while ((line = br.readLine()) != null) {
				array = line.trim().split("[ \t 　]+");
				if(array.length <= 1){
					if(array.length == 1){
						log.error("load user info fail => {}",array[0]);
					}else {
						log.error("no user to load !");
					}
					continue;
				}
				if (array[0].trim().matches("[a-z,A-Z,0-9]+") && array[1].trim().matches("[a-z,A-Z,0-9]+")){
					name = array[0].trim();
					password = array[1].trim();
					map.put(name, new User(name,password));
				}
//				if ((name = array[0].trim()).matches("[a-z,A-Z,0-9]+") && (password = array[1].trim()).matches("[a-z,A-Z,0-9]+")){
//					map.put(name, new User(name,password));
//				}
			}
			
			br.close();
			return map;
		} catch (Exception e) {
			log.error("inputUsers fail, exception => {}", e.toString());
//			e.printStackTrace();
			throw e;
		}finally {
			if(isr != null)
				isr.close();
		}
	}
	
	@Override
	public Map<String, User> inputUsersWithDatas(String filePath, Map<String, User> map) throws Exception {
		InputStreamReader isr = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("inputUsersWithDatas : " + file.getAbsolutePath());
			String line = null;
			isr = new InputStreamReader(new FileInputStream(file), charset);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder("");
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			Type listType = new TypeToken<ArrayList<User>>() {
			}.getType();
			ArrayList<User> jsonArr = gson.fromJson(sb.toString(), listType);
			for (User user : jsonArr) {
				map.put(user.getName(), user);
			}
			
			br.close();
			return map;
		} catch (Exception e) {
			log.error("inputUsersWithDatas fail, exception => {}", e.toString());
//			e.printStackTrace();
			throw e;
		}finally {
			if(isr != null)
				isr.close();
		}
	}

//	@Override
//	public boolean outputUsers(String filePath, Map<String, User> map) throws Exception {
//		OutputStream os = null;
//		try {
//			File file = createFileIfNotExist(filePath);
//			
//			System.out.println("outputUsers : " + file.getAbsolutePath());
//			// create a new OutputStreamWriter
//			os = new FileOutputStream(filePath);
//			OutputStreamWriter writer = new OutputStreamWriter(os,charset);
//			gsonBuilder.serializeSpecialFloatingPointValues();
//			Gson bGson = gsonBuilder.create();
//			String outputString = bGson.toJson(new LinkedList<>(map.values()));
//			
//			// write something in the file
//			writer.write(outputString);
//
//			// flush the stream
//			writer.flush();
//			return true;
//		} catch (Exception ex) {
//			log.error("outputUsers fail, exception => {}", ex.toString());
//			ex.printStackTrace();
//			throw ex;
//		}finally {
//			if(os != null)
//				os.close();
//		}
//	}
	
	@Override
	public Map<String, UserAssets> inputUsersAssets(String filePath, Map<String, UserAssets> map) throws Exception {
		InputStreamReader isr = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("inputUsersAssets : " + file.getAbsolutePath());
			String line = null;
			isr = new InputStreamReader(new FileInputStream(file), charset);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder("");
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			Type listType = new TypeToken<ArrayList<UserAssets>>() {
			}.getType();
			ArrayList<UserAssets> jsonArr = gson.fromJson(sb.toString(), listType);
			for (UserAssets asset : jsonArr) {
				map.put(asset.getName(), asset);
			}
			
			br.close();
			return map;
		} catch (Exception e) {
			log.error("inputUsersAssets fail, exception => {}", e.toString());
//			e.printStackTrace();
			throw e;
		}finally {
			if(isr != null)
				isr.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		TargetDAOImpl impl = new TargetDAOImpl();
//		for(Target target : impl.inputData("C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/targets to run.txt", new HashMap<>()).values()){
//			System.out.println(target.getStockId());
//		}
		
//		Map<String, User> map = new HashMap<>();
//		map.put("root", new User("root", "1234"));
//		map.get("root").getTrackedTargets().put("2023", new TrackedTarget("2023"));
//		map.get("root").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		map.put("Harvey", new User("Harvey", "0819"));
//		map.get("Harvey").getTrackedTargets().put("2023", new TrackedTarget("2023"));
//		map.get("Harvey").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		map.get("Harvey").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		map.put("Judy", new User("Judy", "0419"));
//		map.get("Judy").getTrackedTargets().put("1012", new TrackedTarget("1012"));
//		impl.outputUsers("C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/users output datas.txt", map);
//		
//		for(User user : impl.inputUsersWithDatas("C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file/users output datas.txt", map).values())
//			System.out.println(user.toString()+"　targets size："+user.getTrackedTargets().size());
		String[] array = new String[]{"root","1234"};
		if(array[0].matches("[a-z,A-Z,0-9]+") && array[1].matches("[a-z,A-Z,0-9]+")){
			System.out.println("match");
		}else {
			System.out.println("not match");
		}
	}


}
