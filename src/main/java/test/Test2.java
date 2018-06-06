package test;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.com.geosat.util.WebUtil;

public class Test2 {

	public static void main(String[] args) throws Exception{
//		String s = URLEncoder.encode("中興保全");
//		System.out.println(s);
//		System.out.println(s.replaceAll("%", ""));
//		System.out.println(s.replaceAll("%", "").toLowerCase());
		System.out.println("root".matches("[a-z,A-Z,0-9]+"));
		System.out.println("1234".matches("[a-z,A-Z,0-9]+"));
		System.out.println("root1234".matches("[a-z,A-Z,0-9]+"));
	}
	
}
