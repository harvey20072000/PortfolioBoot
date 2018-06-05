package test;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//ReadExcel.java
public class ReadExcel {

    private String filePath = "C:\\Users\\harvey20072000\\Desktop\\test.xls";
    private String xlsxFilePath = "C:\\Users\\harvey20072000\\Desktop\\test.xlsx";
    String[] strData = new String[3] ;
    
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

	public void readElsx() throws IOException {
		FileInputStream fis = new FileInputStream(xlsxFilePath);
//		POIFSFileSystem fs = new POIFSFileSystem(fis);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0); // 取得Excel第一個sheet(從0開始)
		XSSFCell cell;

		// getPhysicalNumberOfRows這個比較好 //getLastRowNum:這個好像會差1筆
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { // 由於第 0 Row為 title,故 i 從 1開始
			XSSFRow row = sheet.getRow(i); // 取得第 i Row
			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				cell = row.getCell(j);
				strData[j] = cell.toString();
			}
			System.out.println("Name = " + strData[0] + ", Passwd = " + strData[1] + ", Email = " + strData[2]);
		}

		fis.close();
	}
	
	public static void main(String[] args) throws IOException {
		ReadExcel rafe = new ReadExcel();
		//rafe.readExcel();
		rafe.readElsx();
	}
}
