package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EmailGenTest {
	

	public static void main(String[] args) throws Exception{
		String S = "Daniel Radcliffe; Rupert Grint; Emma Watson; Gemma Jones; Ellie Darcey-Alden; Hero Fiennes-Tiffin; Helena Bonham Carter; Helena Bicon Carter", C = "gathatosy";
		for(String result : solution(S, C)){
			System.out.println(result);
		}
	}
	
	public static List<String> solution(String S , String C) throws Exception{
		String[] names = S.split("; "), eachName;
		String first = "", middle = "", last = "", username = "";
		C = C.toLowerCase();
		String emailEnd = "@"+C+".com";
		Map<String,Integer> resultsMap = new HashMap<>();
		List<String> results = new LinkedList<>();
		for(String name : names){
			eachName = handleStringArray(name.split(" "));
			if(eachName.length == 3){
				first = eachName[0];
				middle = eachName[1];
				last = eachName[2];
				username = last + "_" + first + "_" + middle.substring(0, 1);
			}else {
				first = eachName[0];
				last = eachName[1];
				username = last + "_" + first;
			}
			if (resultsMap.containsKey(username)) {
				resultsMap.put(username, resultsMap.get(username) + 1);
			} else {
				resultsMap.put(username, 1);
			}
			if(resultsMap.get(username).equals(1)){
				results.add(username+emailEnd);
			}else {
				results.add(username + resultsMap.get(username)+ emailEnd);
			}
		}
		return results;
	}
	
	private static String[] handleStringArray(String[] inputs){
		for(int i=0;i<inputs.length;i++){
			inputs[i] = inputs[i].toLowerCase().replaceAll("-", "");
		}
		return inputs;
	}
}
