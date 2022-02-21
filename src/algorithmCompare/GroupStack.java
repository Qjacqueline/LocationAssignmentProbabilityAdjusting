package algorithmCompare;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhongm
 * 
 */
public class GroupStack {

	static private int tier; // 层高为3
	static private int row; // 堆垛为4

	static private double pL; // 集装箱重量为Ｌ的到达概率，下同
	static private double pM;
	static private double pH;

	public static int topStateNumber() { // 生成当前层高所对应可堆放的最高的状态数 3层=13 4层=40
		int topStateNumber = 0;
		for (int i = 1; i < tier; i++) {
			topStateNumber = topStateNumber + (int) Math.pow(3, i);
		}

		return topStateNumber + 1;
	}


	/**
	 * 初始化堆存状态
	 * 
	 * @return 初始堆存状态
	 * */
	private static List initialState() {
		List initialstate = new ArrayList();

		for (int i = 0; i < row; i++) {
			initialstate.add(1); // 空箱状态为1
		}
		return initialstate;
	}

	private static List finalState(List stacksequence, int[][] gene) {
		// List stacksequence = stackSequence();
		List initialstate = initialState();

		for (int i = 0; i < stacksequence.size(); i++) {
			int weight = (int) stacksequence.get(i); // 提取第i个到达集装箱的重量
			int[] strategy = gene[weight - 1]; // 选择对应该重量的堆放策略
			int[] a = new int[row]; // 存放各个堆垛状态所对应堆放策略优先级的临时数组
			for (int j = 0; j < initialstate.size(); j++) { // 查找每个状态在堆放策略中的位置
				if ((int) initialstate.get(j) <= topStateNumber()) {
					for (int k = 0; k < strategy.length; k++) {
						if (strategy[k] == (int) initialstate.get(j)) {
							a[j] = k;
							break;
						}
					}
				} else {
					a[j] = 1000;
				}
			}

			int b = 1000; // 表示优先次序
			int opt = 0; // 表示需要改变的状态的位置
			for (int k = 0; k < a.length; k++) { // 查找最优堆放状态
				if (a[k] < b) {
					b = a[k];
					opt = k;
				}
			}

			int state = (int) initialstate.get(opt);
			int statechange = 0;

			statechange = 3 * state - 2 + weight;// 更新状态
			initialstate.set(opt, statechange);

		}

		List finalState = initialstate;

		return finalState;
	}

	private static List reverse(int state) {
		List reverseState = new ArrayList();
		for (int i = 0; i < tier; i++) {
			if (state == 1)
				break; // 判别是否已到最底层 ，（一般不需要，用于应对stack没堆满情况）

			int w = state % 3;
			switch (w) {
			case 2: // 当前层为L
				reverseState.add(1);
				state = (state + 1) / 3; // 改变状态为下一层状态数
				break;
			case 0: // 当前层为M
				reverseState.add(2);
				state = state / 3;
				break;
			case 1: // 当前层为H
				reverseState.add(3);
				state = (state - 1) / 3;
				break;
			}

		}
		return reverseState;
	}

	private static int turnoverNumber(List reverseState) {

		int type = 1;
		int turnoverNumber = 0;
		for (int i = 1; i < reverseState.size() + 1; i++) {
			int temp = (int) reverseState.get(reverseState.size() - i); // 从底层开始计算
			if (temp > type) {
				type = temp; // 增加当前状态的重量表述
			} else if (temp < type) {
				turnoverNumber++; // 增加一次翻倒数
			}
		}
		return turnoverNumber;
	}

	private static int rehandlingNumber(List finalstate) {
		int rehandlingNumber = 0;

		for (int i = 0; i < finalstate.size(); i++) {
			rehandlingNumber = rehandlingNumber + turnoverNumber(reverse((int) finalstate.get(i)));
		}

		return rehandlingNumber;
	}

	/*	
		测试用代码*/
	public static void main(String args[]) {

		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "GroupStack.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			String[] gn = { "3", "2", "1" };
			double[] ratio = { (double) 1 / 6, (double) 1 / 6, (double) 2 / 3 };
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 6; t++) {
					row = s; // 堆垛为4
					tier = t; // 层高为3
					pH = ratio[0];
					pM = ratio[1];
					pL = ratio[2]; // 集装箱重量为Ｌ的到达概率，下同

					double totalRehandling = 0;
					long startTime = System.currentTimeMillis();
					// 3层优化序列
					EvalutionPreparation evalutionPreparation = new EvalutionPreparation();
					List[] permuationList = evalutionPreparation.generateArrivaingContainerPermutation(s, t, gn, ratio);
					if (t == 5) {
						// 5层优化序列
						int[][] gene = {
								{ 2, 5, 14, 41, 1, 3, 6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42, 44, 45, 50, 51, 53, 54, 68, 69, 71, 72, 77, 78, 80, 81, 4, 7,
										10, 11, 12, 13, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 70, 73, 74, 75, 76, 79, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121 },
								{ 3, 6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42, 44, 45, 50, 51, 53, 54, 68, 69, 71, 72, 77, 78, 80, 81, 1, 2, 5, 14, 41, 4, 7,
										10, 11, 12, 13, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 70, 73, 74, 75, 76, 79, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121 },
								{ 4, 7, 10, 11, 12, 13, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 70, 73, 74, 75, 76, 79, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121, 1, 2, 5, 14, 41, 3, 6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42, 44, 45, 50, 51, 53, 54, 68, 69, 71, 72, 77,
										78, 80, 81 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate = finalState(permuationList[i], gene);
							totalRehandling = totalRehandling + rehandlingNumber(finalstate);
						}
					}
					if (t == 3) {
						int[][] gene = { { 2, 5, 1, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13 }, { 3, 9, 6, 8, 1, 2, 5, 4, 7, 10, 11, 12, 13 },
								{ 4, 13, 7, 10, 11, 12, 1, 2, 3, 5, 6, 8, 9 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate = finalState(permuationList[i], gene);
							totalRehandling = totalRehandling + rehandlingNumber(finalstate);
						}
					}
					if (t == 4) {
						// 4层优化序列
						int[][] gene = {
								{ 2, 5, 14, 1, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
										34, 35, 36, 37, 38, 39, 40 },
								{ 3, 9, 27, 6, 8, 15, 17, 18, 23, 24, 26, 1, 2, 4, 5, 7, 10, 11, 12, 13, 14, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33,
										34, 35, 36, 37, 38, 39, 40 },
								{ 4, 13, 40, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 7, 10, 11, 12, 1, 2, 3, 5, 6, 8, 9, 14,
										15, 17, 18, 23, 24, 26, 27 } };

						for (int i = 0; i < permuationList.length; i++) {
							List finalstate = finalState(permuationList[i], gene);
							totalRehandling = totalRehandling + rehandlingNumber(finalstate);
						}
					}
					System.out.println("总翻倒次数" + totalRehandling);
					long endTime = System.currentTimeMillis();
					long calculateTime = endTime - startTime;
					System.out.println("运行时间： " + calculateTime);
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t" + permuationList.length + "\t" + totalRehandling + "\t" + calculateTime);

				}
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

	}
}