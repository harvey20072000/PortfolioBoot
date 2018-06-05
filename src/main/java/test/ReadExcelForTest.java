package test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import lombok.Getter;
import lombok.Setter;

//ReadExcel.java
public class ReadExcelForTest {

    private String filePath = "C:\\Users\\harvey20072000\\Desktop\\test.xls";
    private String xlsxFilePath = "C:\\Users\\harvey20072000\\Desktop\\the list.xlsx";
    String[] strData = new String[3] ;
    private String charset = "utf-8";
    
	public void readExcel() throws IOException {
		FileInputStream fis = new FileInputStream(filePath);
		POIFSFileSystem fs = new POIFSFileSystem(fis);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0); // 取得Excel第一個sheet(從0開始)
		HSSFCell cell;

		// getPhysicalNumberOfRows這個比較好 //getLastRowNum:這個好像會差1筆
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { // 由於第 0 Row為 title,故 i 從 1開始
			HSSFRow row = sheet.getRow(i); // 取得第 i Row
			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				cell = row.getCell(j);
				strData[j] = cell.toString();
			}
			System.out.println("Name = " + strData[0] + ", Passwd = " + strData[1] + ", Email = " + strData[2]);
		}

		fis.close();
	}

	public List<List<String>> readElsx() throws IOException {
		FileInputStream fis = new FileInputStream(xlsxFilePath);
//		POIFSFileSystem fs = new POIFSFileSystem(fis);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0); // 取得Excel第一個sheet(從0開始)
		XSSFCell cell;

		List<List<String>> result = new LinkedList<>();
		// getPhysicalNumberOfRows這個比較好 //getLastRowNum:這個好像會差1筆
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { // 由於第 0 Row為 title,故 i 從 1開始
			XSSFRow row = sheet.getRow(i); // 取得第 i Row
			result.add(new LinkedList<>());
			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				cell = row.getCell(j);
				result.get(i).add(cell.toString());
			}
//			cell = row.getCell(1);
//			result.put(cell.toString(), cell.toString());
		}

		fis.close();
		return result;
	}
	
	public boolean outputLine(String filePath , Map<String ,String> map) throws Exception{
		OutputStream os = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("outputLine : " + file.getAbsolutePath());
			// create a new OutputStreamWriter
			os = new FileOutputStream(filePath);
			OutputStreamWriter writer = new OutputStreamWriter(os,charset);

			for(String key : map.keySet()){
				writer.write(key+"\r\n");
			}
//			String outputString = gson.toJson(new LinkedList<>(map.values()));

			// flush the stream
			writer.flush();
			return true;
		} catch (Exception ex) {
//			log.error("outputLine fail, exception => {}", ex.toString());
			ex.printStackTrace();
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
	
	public boolean writeElsx(String filePath, List<List<String>> data, int sheetNum) throws Exception{
//		FileInputStream fis = new FileInputStream(xlsxFilePath);
//		POIFSFileSystem fs = new POIFSFileSystem(fis);
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(); // 取得Excel第一個sheet(從0開始)
		XSSFCell cell;
		
		/* get the last row number to append new data */ 
		int rownum = sheet.getLastRowNum();
		synchronized (data) {
			for (List<String> rows : data) {
				XSSFRow row = sheet.createRow(rownum++);
				int cellnum = 0;
				for(String s : rows){
					cell = row.createCell(cellnum++);
					cell.setCellValue(s);
				}
			}
		}
		
		OutputStream os = null;
		try {
			File file = createFileIfNotExist(filePath);
			
			System.out.println("outputLine : " + file.getAbsolutePath());
			// create a new OutputStreamWriter
			os = new FileOutputStream(filePath);
			
			wb.write(os);
//			String outputString = gson.toJson(new LinkedList<>(map.values()));

			// flush the stream
			System.out.println("Writing on XLSX file Finished ...");
			return true;
		} catch (Exception ex) {
//			log.error("outputLine fail, exception => {}", ex.toString());
			ex.printStackTrace();
			return false;
		}finally {
			if(os != null)
				os.close();
		}
		

		//Read more: http://www.java67.com/2014/09/how-to-read-write-xlsx-file-in-java-apache-poi-example.html#ixzz53lYaWjRg
	}
	
	public static void main(String[] args) throws Exception {
		ReadExcelForTest rafe = new ReadExcelForTest();
		System.out.println("reading excel done");
		//rafe.readExcel();
		List<List<String>> result = rafe.readElsx(),success = new LinkedList<>(),fail = new LinkedList<>();
		System.out.println("targets size:"+result.size());
//		List<ScheduledThreadPoolExecutor> executors = new LinkedList<>();
//		for(int a = 0;a < 12;a++){
//			executors.add(new ScheduledThreadPoolExecutor(1000));
//		}
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(12000);
		
		Checker checker;
		String key = null;
		int i=0;
		for(List<String> rows : result){
			//success.size() + fail.size() < result.size()
			while(executor.getActiveCount() >= 200){
				try {
					TimeUnit.SECONDS.sleep(8);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			i++;
			key = rows.get(3);
			checker = rafe.new Checker(i + "", key, rows, success, fail);
			executor.execute(checker);
			System.out.println("proccessing "+(success.size() + fail.size())+"/"+result.size());
//			if(i >= 1000)
//				break;
//			if("email".equals(key))
//				continue;
//			if(checkEmail(key)){
//				success.add(rows);
//			}else {
//				fail.add(rows);
//			}
//			System.out.println("proccessing "+i+"/"+result.size());
		}
		while(executor.getActiveCount() != 0){
			System.out.println("waiting...");
		}
		rafe.writeElsx("C:\\Users\\harvey20072000\\Desktop\\success.xlsx", success, 0);
		rafe.writeElsx("C:\\Users\\harvey20072000\\Desktop\\fail.xlsx", fail, 0);
		System.out.println("done");
//		System.out.println(checkEmail("harvey20072000@hotmail.com"));
	}
	
	public static boolean checkEmail(String email) {
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            return false;
        }
  
        String host = "";
        String hostName = email.split("@")[1];
        Record[] result = null;
        SMTPClient client = new SMTPClient();
  
        try {
            // 查找MX记录
            Lookup lookup = new Lookup(hostName, Type.MX);
            lookup.run();
            if (lookup.getResult() != Lookup.SUCCESSFUL) {
                return false;
            } else {
                result = lookup.getAnswers();
            }
  
            // 连接到邮箱服务器
            for (int i = 0; i < result.length; i++) {
                host = result[i].getAdditionalName().toString();
                client.connect(host);
                if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
                    client.disconnect();
                    continue;
                } else {
                    break;
                }
            }
			if (200 <= client.getReplyCode() && 250 >= client.getReplyCode()) {
				return true;
			}
            //以下2项自己填写快速的，有效的邮箱
//            client.login("gmail.com");
//            client.setSender("harvey20072000@gmail.com");
//            client.addRecipient(email);
//            if (250 == client.getReplyCode()) {
//                return true;
//            }
        } catch (Exception e) {
            System.err.printf("check email(%s) fail, exception => %s",email,e.toString());
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
            }
        }
        return false;
    }
	
	@Getter
	@Setter
	private class Checker extends Thread{
		
		String userName;
		String email;
		List<String> row;
		List<List<String>> success,fail;
		
		public Checker() {
		}
		
		public Checker(String userName,String email, List<String> row,List<List<String>> success,List<List<String>> fail){
			this();
			this.userName = userName;
			this.email = email;
			this.row = row;
			this.success = success;
			this.fail = fail;
		}
		
		@Override
		public void run(){
				try {
					if("email".equals(email))
						return;
					if(checkEmail(email)){
						synchronized(success){
							success.add(row);
						}
					}else {
						synchronized(fail){
							fail.add(row);
						}
					}
				} catch (Exception e) {
					System.out.println("check email:"+email+" fail, exception => "+e.toString());
				}
		}
	}
}
