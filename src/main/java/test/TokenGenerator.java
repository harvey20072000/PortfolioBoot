package test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.com.geosat.util.WebUtil;

public class TokenGenerator {
	

	public static void main(String[] args) throws Exception{
		System.out.println(genToken(30));
	}
	
	static String genToken(int requiredLength) {
		// A~Z:65~90 a~z:67~122 0~9:48~57
		String elementsString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String[] elements = new String[elementsString.length()];
		for(int i=0;i<elements.length;i++){
			elements[i] = elementsString.substring(i, i+1);
		}
		String[] resultArray = new String[requiredLength];
		int subIndex = requiredLength/3;
		Random random = new Random();
		
		for(int i=0;i<requiredLength;i++){
			if(i != requiredLength - 1 && i%subIndex == (subIndex - 1)){
				resultArray[i] = "-";
				continue;
			}
			resultArray[i] = elements[random.nextInt(elementsString.length())];
		}
		
		StringBuilder sb = new StringBuilder("");
		for(String s : resultArray){
			sb.append(s);
		}
		return sb.toString();
	}

}
