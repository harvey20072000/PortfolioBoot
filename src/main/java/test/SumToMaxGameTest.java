package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SumToMaxGameTest {
	private static int MAX_NUM_PER_ROUND = 3 + new Random().nextInt(4);
	private static int SUM_LIMIT;
	private static int HARDNESS = 2;	//0:簡單__1:中等__2:困難

	public static void main(String[] args) throws Exception{
		SUM_LIMIT = MAX_NUM_PER_ROUND * 7 - new Random().nextInt(MAX_NUM_PER_ROUND);
		
		int tempLimit = SUM_LIMIT, tempNum = 0, turn = 1;
		Map turnMap = new HashMap(){{
			put(1,"你");
			put(-1,"電腦");
		}};
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("累加數字遊戲開始");
		System.out.println("最大可選數字為"+MAX_NUM_PER_ROUND);
		System.out.println("最先讓起始數"+SUM_LIMIT+"減少到0的人就輸了");
		while(true){
			if(tempLimit <= 1){
				System.out.println(turnMap.get(turn)+"輸了");
				break;
			}else {
				if(turn > 0){
					System.out.print("請輸入你要減去的數字:");
					try {
						tempNum = Integer.parseInt(scanner.nextLine());
						if(tempNum > MAX_NUM_PER_ROUND || tempNum <= 0){
							System.out.println("輸入數字需介於1~"+MAX_NUM_PER_ROUND+"之間");
							continue;
						}
					} catch (Exception e) {
						System.out.println("輸入的不是數字");
						continue;
					}
				}else {
					tempNum = chooseNum(tempNum, tempLimit, HARDNESS);
					System.out.println("電腦選擇了"+tempNum);
				}
				System.out.println("總數字減少為"+(tempLimit -= tempNum));
				TimeUnit.SECONDS.sleep(1);
			}
			turn *= -1;
		}
		System.out.println("遊戲結束");
	}
	
	private static int chooseNum(int tempNum, int limit, int hardness){
		Random random = new Random();
		if(limit <= MAX_NUM_PER_ROUND + 1){
			if(hardness == 0){
				tempNum = random.nextInt(MAX_NUM_PER_ROUND) + 1;
			}else if (hardness > 0) {
				tempNum = limit - 1;
			}
		}else {
			if(hardness == 0){
				tempNum = random.nextInt(MAX_NUM_PER_ROUND) + 1;
			}else if (hardness == 1) {
				if(random.nextInt(10) >= 5){
					tempNum = random.nextInt(MAX_NUM_PER_ROUND) + 1;
				}else {
					if((tempNum = (limit - (MAX_NUM_PER_ROUND + 2)) % (MAX_NUM_PER_ROUND + 1)) == 0){
						tempNum = random.nextInt(MAX_NUM_PER_ROUND) + 1;
					}
				}
			}else if (hardness == 2) {
				if((tempNum = (limit - (MAX_NUM_PER_ROUND + 2)) % (MAX_NUM_PER_ROUND + 1)) == 0){
					tempNum = random.nextInt(MAX_NUM_PER_ROUND) + 1;
				}
//				if(limit <= (MAX_NUM_PER_ROUND + 2)){
//					tempNum = limit % (MAX_NUM_PER_ROUND + 2);
//				}else {
//					tempNum = limit % (MAX_NUM_PER_ROUND + 1);
//				}
//				if(tempNum == 0)
//					tempNum = random.nextInt(MAX_NUM_PER_ROUND) + 1;
			}
		}
		return tempNum;
	}
}
