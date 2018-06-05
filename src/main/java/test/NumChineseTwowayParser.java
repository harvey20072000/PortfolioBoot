package test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import tw.com.geosat.util.WebUtil;

@Getter
@Setter
public class NumChineseTwowayParser {
	
	private int mode = 0;	// 0:數字轉國字__1:國字轉數字
	
	private int showPolicy = 2;	// 0:顯示單位__1:不顯示單位__2:有無單位的都一起顯示
	
	private Map<String, List<String>> numChMap = new HashMap<>();
	private Map<String, String> numCodeMap = new HashMap<>();
	private Map<String, List<String>> unitCodeMap = new HashMap<>();
	
	public NumChineseTwowayParser(){
		List<String> list = new LinkedList<>();
		numChMap.put("#0", inputArrayToList(new LinkedList<>(), "零", "凌"));
		numChMap.put("#1", inputArrayToList(new LinkedList<>(), "一", "壹"));
		numChMap.put("#2", inputArrayToList(new LinkedList<>(), "二", "貳"));
		numChMap.put("#3", inputArrayToList(new LinkedList<>(), "三", "參", "叁", "参"));
		numChMap.put("#4", inputArrayToList(new LinkedList<>(), "四", "肆"));
		numChMap.put("#5", inputArrayToList(new LinkedList<>(), "五", "伍"));
		numChMap.put("#6", inputArrayToList(new LinkedList<>(), "六", "陸"));
		numChMap.put("#7", inputArrayToList(new LinkedList<>(), "七", "柒"));
		numChMap.put("#8", inputArrayToList(new LinkedList<>(), "八", "捌"));
		numChMap.put("#9", inputArrayToList(new LinkedList<>(), "九", "玖"));
		
		numCodeMap.put("0", "#0");
		numCodeMap.put("1", "#1");
		numCodeMap.put("2", "#2");
		numCodeMap.put("3", "#3");
		numCodeMap.put("4", "#4");
		numCodeMap.put("5", "#5");
		numCodeMap.put("6", "#6");
		numCodeMap.put("7", "#7");
		numCodeMap.put("8", "#8");
		numCodeMap.put("9", "#9");
		
		unitCodeMap.put("#十", inputArrayToList(new LinkedList<>(), "十", "拾"));
		unitCodeMap.put("#百", inputArrayToList(new LinkedList<>(), "百", "佰"));
		unitCodeMap.put("#千", inputArrayToList(new LinkedList<>(), "千", "仟"));
		unitCodeMap.put("#萬", inputArrayToList(new LinkedList<>(), "萬"));
	}
	
	private List<String> inputArrayToList(List<String> list, String... inputs){
		for(String input : inputs){
			list.add(input);
		}
		return list;
	}
	
	/*
	 * 新增同音字
	 */
	public void addHomonyms(String target, String input) {
		if(numChMap.containsKey(target)){
			numChMap.get(target).add(input);
		}else if (unitCodeMap.containsKey(target)) {
			unitCodeMap.get(target).add(input);
		}
	}
	
	/*
	 * 移除同音字
	 */
	public void removeHomonyms(String target, String input) throws Exception{
		if(numChMap.containsKey(target) && numChMap.get(target).contains(input)){
			numChMap.get(target).remove(input);
		}else if (unitCodeMap.containsKey(target) && unitCodeMap.get(target).contains(input)) {
			unitCodeMap.get(target).remove(input);
		}else {
			throw new Exception(target+"的集合裡面沒有"+input+"喔~");
		}
	}
	
	/*
	 * 取得特定集合的同音字
	 */
	public List<String> getHomonyms(String target) throws Exception{
		if(numChMap.containsKey(target)){
			return numChMap.get(target);
		}else if (unitCodeMap.containsKey(target)) {
			return unitCodeMap.get(target);
		}
		return null;
	}
	
	/*
	 * 列出同音字
	 * type 0:數字的__1:單位的
	 */
	public Map list(int type) throws Exception{
		if(type == 0){
			return new TreeMap<>(numChMap);
		}else if(type == 1){
			return new TreeMap<>(unitCodeMap);
		}else {
			throw new Exception("no such type:"+type);
		}
	}
	
	/*
	 * 轉換
	 */
	public String parse(int mode, String input) throws Exception{
		String result = "";
		if(mode == 0){
			result = parseToStringCode(input);
			return result;
		}else if (mode == 1) {
			return parseToNum(input);
		}else {
			throw new Exception("undefined mode:"+mode);
		}
	}
	
	private String parseToStringCode(String input) throws Exception{
		Integer inputNum = null;
		try {
			inputNum = Integer.parseInt(input);
		} catch (Exception e) {
			throw new Exception("wrong input:"+input+", must input pure number 0-9");
		}
		
		List<String> list = new LinkedList<>();
		for(int i=input.length()-1;i>=0;i--){
			if(i == input.length()-2)
				list.add(0, "#十");
			if(i == input.length()-3)
				list.add(0, "#百");
			if(i == input.length()-4)
				list.add(0, "#千");
			if(i == input.length()-5)
				list.add(0, "#萬");
			list.add(0, numCodeMap.get(input.substring(i, i+1)));
		}
		
		for(String key:unitCodeMap.keySet()){
			if(list.indexOf(key) > 0 && "#0".equals(list.get(list.indexOf(key)-1))){
				list.remove(list.indexOf(key));
			}
		}
//		while(list.contains("#0") && unitCodeMap.containsKey(list.get(list.indexOf("#0")+1))){
//			list.remove(list.indexOf("#0")+1);
//		}
		while("#0".equals(list.get(0))) {
			list.remove(list.get(0));
		}
		
		
		List<String> results = new LinkedList<>();
		results.add(listContentsToString(list,0,0));
		int size = results.size();
		for(String s : list){
			if(numChMap.containsKey(s)){
				for(String replaceString:numChMap.get(s)){
					for(int i=0;i<results.size();i++){
						if(results.contains(results.get(i).replaceAll(s, replaceString)))
							continue;
						results.add(results.get(i).replaceAll(s, replaceString));
					}
				}
				if(list.size() == 3 && "#1".equals(s)){
					for(int i=0;i<results.size();i++){
						if(results.contains(results.get(i).replaceAll(s, "")))
							continue;
						results.add(results.get(i).replaceAll(s, ""));
					}
				}
			}else if (unitCodeMap.containsKey(s)) {
				if(showPolicy == 0){
					for(String replaceString:unitCodeMap.get(s)){
						for(int i=0;i<results.size();i++){
							if(results.contains(results.get(i).replaceAll(s, replaceString)))
								continue;
							results.add(results.get(i).replaceAll(s, replaceString));
						}
					}
				} else if (showPolicy == 1) {
					for (int i = 0; i < results.size(); i++) {
						if (results.contains(results.get(i).replaceAll(s, "")))
							continue;
						results.add(results.get(i).replaceAll(s, ""));
					}
				}else if (showPolicy == 2) {
					for(String replaceString:unitCodeMap.get(s)){
						for(int i=0;i<results.size();i++){
							if(results.contains(results.get(i).replaceAll(s, replaceString)))
								continue;
							results.add(results.get(i).replaceAll(s, replaceString));
						}
					}
					for (int i = 0; i < results.size(); i++) {
						if (results.contains(results.get(i).replaceAll(s, "")))
							continue;
						if(!results.get(i).startsWith("#十"))
							results.add(results.get(i).replaceAll(s, ""));
					}
				}
				
			}
			for(int j=0;j<results.size();j++){
				if(results.get(j).contains(s)){
					results.remove(j);
					j--;
				}
			}
			size = results.size();
		}
		
		Collections.sort(results);
		
		return listContentsToString(results, 1, 1);
	}
	
	/*
	 * nextLine 0:不換行__1:換行
	 * isResult 0:非結果輸出__1:為結果輸出
	 */
	private String listContentsToString(List<String> list, int nextLine, int isResult){
		String result = "";
		for(String temp : list){
			if(isResult == 1 && temp.contains("#"))
				continue;
			result += temp;
			if(nextLine == 1){
				result += "\n";
			}
		}
		return result;
	}

	private String parseToNum(String input) throws Exception{
		// TODO 以後再弄
		return null;
	}
	
	public static void main(String[] args){
		NumChineseTwowayParser parser = new NumChineseTwowayParser();
		try {
			System.out.println(parser.parseToStringCode("951"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
