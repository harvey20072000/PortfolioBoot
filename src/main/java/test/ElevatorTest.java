package test;

import java.util.HashSet;
import java.util.Set;

public class ElevatorTest {
	

	public static void main(String[] args) throws Exception{
		int[] A = new int[]{40,40,100,80,20}, B = new int[]{3,3,2,2,3};
		int M = 3, X = 5, Y = 200;
		System.out.println("電梯總停靠次數:"+ solution(A, B, M, X, Y));
	}
	
	public static int solution(int[] A, int[] B, int M, int X, int Y) throws Exception{
		// M:最高樓層數
		// X:電梯人數上限
		// Y:電梯載重上限
		int N = A.length, totalStopTimes = 0, tempTotalWeight = 0, tempTotalNum = 0;
		
		if(N != B.length)
			throw new Exception("A.length should be the same as B.length");
		
		Set<Integer> targetFloors = new HashSet<>();
		for(int i=0;i<N;i++){
			tempTotalWeight += A[i];
			tempTotalNum += 1;
			targetFloors.add(B[i]);
			if(((tempTotalWeight + A[i]) > Y || (tempTotalNum + 1) > X)
					|| (i == N-1 && tempTotalNum != 0)){
				totalStopTimes += targetFloors.size() + 1;
				tempTotalNum = 0;
				tempTotalWeight = 0;
				targetFloors.clear();
			}
		}
		return totalStopTimes;
	}
}
