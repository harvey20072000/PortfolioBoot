package ga.workshop.com.util;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Const {

	public static String ROOT_PATH = "./src/main/java/ga/workshop/com";
	public static String FILE_ROOT_PATH = "C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file";
	public static String DATA_INPUT_FILE_PATH = FILE_ROOT_PATH + "/targets to run.txt";
	public static String DATA_FIXED_INPUT_FILE_PATH = FILE_ROOT_PATH + "/targets fixed to run.txt";
	public static String DATA_OUTPUT_FILE_PATH = FILE_ROOT_PATH + "/targets analyzed {DATE}.txt";
	
	public static String USERS_INPUT_FILE_PATH = FILE_ROOT_PATH + "/users to run.txt";
	public static String USERS_OUTPUT_FILE_PATH = FILE_ROOT_PATH + "/users output datas.txt";
	public static String USERS_ASSETS_FILE_PATH = FILE_ROOT_PATH + "/users assets datas.txt";
	
	public static SimpleDateFormat SDF_NO_TIME = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat SDF_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static long SESSION_PASSOUT_INTERVAL = 1*24*60*60*1000;
	
	@Value("${data.storage.local}")
    public void setFileRootPath(String value) {
		log.debug("setFileRootPath exeuted. time = {}",System.currentTimeMillis());
		String tempRoot = FILE_ROOT_PATH;
		FILE_ROOT_PATH = value;
		DATA_INPUT_FILE_PATH = DATA_INPUT_FILE_PATH.replace(tempRoot, value);
		DATA_FIXED_INPUT_FILE_PATH = DATA_FIXED_INPUT_FILE_PATH.replace(tempRoot, value);
		DATA_OUTPUT_FILE_PATH = DATA_OUTPUT_FILE_PATH.replace(tempRoot, value);
		USERS_INPUT_FILE_PATH = USERS_INPUT_FILE_PATH.replace(tempRoot, value);
		USERS_OUTPUT_FILE_PATH = USERS_OUTPUT_FILE_PATH.replace(tempRoot, value);
		USERS_ASSETS_FILE_PATH = USERS_ASSETS_FILE_PATH.replace(tempRoot, value);
    }
}
