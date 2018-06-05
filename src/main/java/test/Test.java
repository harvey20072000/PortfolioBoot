package test;

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

public class Test {
	

	public static void main(String[] args) throws Exception{
//		ReadExcel re = new ReadExcel();
		String word = null, question = "[查詢]≡TOPIC≡的[鬧鐘]";
		int questionIndex = 0;
		List<String> words = new LinkedList<>();
		for (int i=0; i<question.length() ; i++) {
			word = getWord(questionIndex, i, question);
			i += word.length()-1;
			words.add(word);
		}
		System.out.println(words);
	}
	
	static String getWord(int questionIndex, int startIndex, String question) {
		String result = question.substring(startIndex, startIndex+1);
		if ("[".equals(result)) {
			result = "§" + question.substring(startIndex+1, 
					startIndex + question.substring(startIndex+1).indexOf("]")+1) + "§";
		} else if ("≡".equals(result)) {
			String attr = question.substring(startIndex, 
					startIndex + question.substring(startIndex+1).indexOf("≡")+2);
//			if (attrs.indexOf(attr)<0) { 
//				attrs.add(attr);
//			}
//			List tmp;
//			if ((tmp = attrsMapping.get(questionIndex))==null) {
//				tmp = new LinkedList();
//				attrsMapping.put(questionIndex, tmp);
//			}
//			tmp.add(attrs.indexOf(attr));
//			result = "(?<attr" + attrs.indexOf(attr) + ">[^-~]+)";
			result = attr;
		} else {
			String subString = question.substring(startIndex);
			int cut = subString.length();
			int cut1 = subString.indexOf("≡");
			if (cut1>0) cut = cut1;
			int cut2 = subString.indexOf("[");
			if (cut2>0 && cut2<cut)cut = cut2;
			result = question.substring(startIndex, startIndex + cut);
		}
		return result;
	}

}
