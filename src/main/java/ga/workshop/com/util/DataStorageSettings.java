package ga.workshop.com.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "data.storage.local")
public class DataStorageSettings {

//	public DataStorageSettings instance = new DataStorageSettings();
//	public String rootPath = "./src/main/java/ga/workshop/com";
//	public String fileRootPath = "C:/Users/harvey20072000/Desktop/SideProjects/Portfolio/targets file";
//	public String dataInputFilePath = fileRootPath + "/targets to run.txt";
//	public String dateFixedInputFilePath = fileRootPath + "/targets fixed to run.txt";
//	public String dataOutputFilePath = fileRootPath + "/targets analyzed {DATE}.txt";
//	
//	public String usersInputFilePath = fileRootPath + "/users to run.txt";
//	public String usersOutputFilePath = fileRootPath + "/users output datas.txt";
	
//	public String rootPath;
//	public String fileRootPath;
//	public String dataInputFilePath;
//	public String dateFixedInputFilePath;
//	public String dataOutputFilePath;
//	
//	public String usersInputFilePath;
//	public String usersOutputFilePath;
	
	private String rootPath;
	private String fileRootPath;
	private String dataInputFilePath;
	private String dateFixedInputFilePath;
	private String dataOutputFilePath;
	
	private String usersInputFilePath;
	private String usersOutputFilePath;
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public String getFileRootPath() {
		return fileRootPath;
	}
	public void setFileRootPath(String fileRootPath) {
		this.fileRootPath = fileRootPath;
	}
	public String getDataInputFilePath() {
		return dataInputFilePath;
	}
	public void setDataInputFilePath(String dataInputFilePath) {
		this.dataInputFilePath = dataInputFilePath;
	}
	public String getDateFixedInputFilePath() {
		return dateFixedInputFilePath;
	}
	public void setDateFixedInputFilePath(String dateFixedInputFilePath) {
		this.dateFixedInputFilePath = dateFixedInputFilePath;
	}
	public String getDataOutputFilePath() {
		return dataOutputFilePath;
	}
	public void setDataOutputFilePath(String dataOutputFilePath) {
		this.dataOutputFilePath = dataOutputFilePath;
	}
	public String getUsersInputFilePath() {
		return usersInputFilePath;
	}
	public void setUsersInputFilePath(String usersInputFilePath) {
		this.usersInputFilePath = usersInputFilePath;
	}
	public String getUsersOutputFilePath() {
		return usersOutputFilePath;
	}
	public void setUsersOutputFilePath(String usersOutputFilePath) {
		this.usersOutputFilePath = usersOutputFilePath;
	}
	
	
}
