package test;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import tw.com.geosat.util.WebUtil;

public class GuessFourNumberGameTest {
	

	public static void main(String[] args) throws Exception{
		
		String target = formTarget(1),answer = "";
		Scanner scanner = new Scanner(System.in);
		String[] targetSplit = parseStringToArray(target);
		System.out.println("猜數字開始");
		while(true){
			System.out.print("請輸入數字：");;
			answer = scanner.nextLine();
			if(target.equals(answer))
				break;
			if(!answer.matches("[0-9]+")){
				System.out.println("格式不對，請重新輸入...");
				continue;
			}
			System.out.println(findDifference(parseStringToArray(answer), targetSplit));
		}
		System.out.println("恭喜你猜對了!!! 答案就是 "+target);
	}
	
	private static String formTarget(int type){
		//type 0=可重複;1=不可重複
		DecimalFormat format = new DecimalFormat("0000");
		String target = "";
		String[] targetSplit;
		boolean regenerate = false;
		while(true){
			regenerate = false;
			target = format.format(new Random().nextInt(9999));
			if(type != 1)
				break;
			targetSplit = parseStringToArray(target);
			for(int i=0;i<targetSplit.length;i++){
				for(int j=0;j<targetSplit.length;j++){
					if(i==j)
						continue;
					if(targetSplit[i].equals(targetSplit[j])){
						regenerate = true;
						break;
					}
				}
				if(regenerate)
					break;
			}
			if(!regenerate)
				break;
		}
		return target;
	}

	private static String[] parseStringToArray(String input){
		String[] array = new String[input.length()];
		for(int i=0;i<input.length();i++){
			array[i] = input.substring(i,i+1);
		}
		return array;
	}
	
	private static String findDifference(String[] array , String[] targetArray){
		List<String> list = new LinkedList<>(), targetList = new LinkedList<>();
		
		Map<String, Integer> difference = new HashMap<>();
		difference.put("A", 0);
		difference.put("B", 0);
		
		for(int i=0;i<array.length;i++){
			list.add(array[i]);
		}
		for(int j=0;j<targetArray.length;j++){
			targetList.add(targetArray[j]);
		}
		
		for(int i=0;i<list.size();i++){
			if(list.get(i).equals(targetList.get(i))){
				difference.put("A", difference.get("A")+1);
				targetList.remove(i);
				list.remove(i);
				i--;
			}
		}
		
		for(int i=0;i<list.size();i++){
			for(int j=0;j<targetList.size();j++){
				if(list.get(i).equals(targetList.get(j))){
					difference.put("B", difference.get("B")+1);
					targetList.remove(j);
					j--;
					list.remove(i);
					i--;
					break;
				}
			}
		}
		
		String retString = "";
		for(String key : difference.keySet()){
			retString += difference.get(key)+key;
		}
		return retString;
	}
}
