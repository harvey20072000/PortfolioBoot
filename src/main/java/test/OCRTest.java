package test;

public class OCRTest {
	

	public static void main(String[] args) throws Exception{
		String[] Ss = new String[]{"A2Le","a10","ba1","3x2x"}, Ts = new String[]{"2pL1","10a","1Ad","8"};
		for(int i=0;i<Ss.length;i++){
			System.out.println(Ss[i]+"相比於"+Ts[i]+":"+ solution(Ss[i], Ts[i]));
		}
	}
	
	public static boolean solution(String S , String T) throws Exception{
		S = generateCompString(S);
		T = generateCompString(T);
		if(S.length() != T.length()){
			return false;
		}else if (!compareStrings(S, T)) {
			return false;
		}
		return true;
	}
	
//	private static String generateCompString(String input){
//		final String REGEX_FOR_ALPHA = "[a-zA-Z]", REGEX_FOR_NUM = "[0-9]";
//		int lastWordStat = 0, currentWordStat = 0; // 1:alpha___2:num
//		String currentCharacter = "", passedString = "", tempNum = "", tempNumToString = "";
//		for(int i=0;i<input.length();i++){
//			currentCharacter = input.substring(i, i+1);
//			if(currentCharacter.matches(REGEX_FOR_ALPHA)){
//				currentWordStat = 1;
//			}else if (currentCharacter.matches(REGEX_FOR_NUM)) {
//				tempNum += currentCharacter;
//				currentWordStat = 2;
//			}
//			if((lastWordStat == 2 && currentWordStat == 1 && tempNum != "") || (i == input.length() - 1 && tempNum != "")){
//				for(int j=0;j<Integer.parseInt(tempNum);j++){
//					tempNumToString += "$";
//				}
//				input = passedString + tempNumToString + input.substring(i);
//				i = tempNumToString.length();
//				tempNumToString = "";
//				tempNum = "";
//			}
//			if(!currentCharacter.matches(REGEX_FOR_NUM)){
//				passedString = input.substring(0, i+1);
//			}
//			lastWordStat = currentWordStat;
//		}
//		return input;
//	}
	
	private static String generateCompString(String input){
		final String REGEX_FOR_ALPHA = "[a-zA-Z]+";
		String[] nums = input.split(REGEX_FOR_ALPHA);
		String tempNumToString = "";
		for(int i=0;i<nums.length;i++){
			try {
				for(int j=0;j<Integer.parseInt(nums[i]);j++){
					tempNumToString += "$";
				}
				input = input.replace(nums[i], tempNumToString);
				tempNumToString = "";
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return input;
	}
	
	private static boolean compareStrings(String S, String T){
		char[] Ss = S.toCharArray() , Ts = T.toCharArray();
		for(int i=0;i<Ss.length;i++){
			if(Ss[i] == '$' || Ts[i] == '$'){
				continue;
			}
			if(Ss[i] != Ts[i])
				return false;
		}
		return true;
	}
}
