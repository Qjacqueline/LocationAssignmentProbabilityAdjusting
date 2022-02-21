package algorithmCompare;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RolloutAlgorithm {

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

	private static List partialOptimization(int[][] gene, List stacksequence) {
		List initialstate = initialState();
		int simulationTimes = 100;

		for (int i = 0; i < stacksequence.size(); i++) {
			int weight = (int) stacksequence.get(i); // 提取第i个到达集装箱的重量
			List tempStackSequence = new ArrayList(stacksequence.subList(i + 1,
					stacksequence.size()));// 获取剩余到达序列，用于之后仿真

			List tempstate = new ArrayList();// 更改当前状态，用于之后仿真比较
			for (int k = 0; k < initialstate.size(); k++) {
				if ((int) initialstate.get(k) <= topStateNumber()) {
					tempstate.add(stateChange(initialstate, weight, k));
					// @ToDo
				} else {
					tempstate.add(null); // 堆满的stack的不仿真
				}
			}

			int[] totalRehandling = new int[row]; // 对各个状态进行仿真，比较总翻倒次数
			for (int j = 0; j < simulationTimes; j++) {
				Collections.shuffle(tempStackSequence);
				for (int m = 0; m < tempstate.size(); m++) {
					if (tempstate.get(m) != null) {
						totalRehandling[m] = totalRehandling[m]
								+ rehandlingNumber(simulationModule(gene,
										tempStackSequence,
										(List) tempstate.get(m)));
					} else {
						totalRehandling[m] = 999999999;
					}

				}
			}

			int b = 10000000; // 表示优先次序
			int opt = 0; // 表示需要改变的状态的位置
			for (int k = 0; k < totalRehandling.length; k++) { // 查找最优堆放状态
				if (totalRehandling[k] < b) {
					b = totalRehandling[k];
					opt = k;
				}
			}

			initialstate = stateChange(initialstate, weight, opt);
		}

		return initialstate;
	}

	private static List stateChange(List initialstate, int weight, int opt) {

		List clonestate = new ArrayList(initialstate);
		int state = (int) clonestate.get(opt);
		int statechange = 0;

		statechange = 3 * state - 2 + weight;// 更新状态
		clonestate.set(opt, statechange);
		return clonestate;

	}

	private static List simulationModule(int[][] gene,
			List partialstacksequence, List partialinitialstate) {

		for (int i = 0; i < partialstacksequence.size(); i++) {
			int weight = (int) partialstacksequence.get(i); // 提取第i个到达集装箱的重量
			int[] strategy = gene[weight - 1]; // 选择对应该重量的堆放策略
			int[] a = new int[row]; // 存放各个堆垛状态所对应堆放策略优先级的临时数组
			for (int j = 0; j < partialinitialstate.size(); j++) { // 查找每个状态在堆放策略中的位置
				if ((int) partialinitialstate.get(j) <= topStateNumber()) {
					for (int k = 0; k < strategy.length; k++) {
						if (strategy[k] == (int) partialinitialstate.get(j)) {
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

			partialinitialstate = stateChange(partialinitialstate, weight, opt);

		}

		List finalState = partialinitialstate;

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
			rehandlingNumber = rehandlingNumber
					+ turnoverNumber(reverse((int) finalstate.get(i)));
		}

		return rehandlingNumber;
	}

	private static List finalState(int[][] gene, List stacksequence) {
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

	/*
	 * 测试用代码
	 */
	public static void main(String args[]) {

		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path
					+ "RolloutAlgorithm10000_100.txt", false);
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

					double totalRehandling1 = 0;
					double totalRehandling2 = 0;
					long startTime = System.currentTimeMillis();
					// 3层优化序列
					EvalutionPreparation evalutionPreparation = new EvalutionPreparation();
					List[] permuationList = evalutionPreparation
							.generateArrivaingContainerPermutation(s, t, gn,
									ratio);
					if (s == 4 && t == 3) {
						int[][] gene = {
								{ 1, 2, 5, 12, 6, 7, 10, 13, 9, 8, 4, 11, 3 },
								{ 3, 6, 2, 12, 8, 5, 9, 10, 11, 4, 1, 7, 13 },
								{ 13, 7, 12, 10, 4, 8, 9, 11, 5, 3, 2, 6, 1 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 4 && t == 4) {
						int[][] gene = {
								{ 1, 5, 2, 24, 23, 38, 32, 14, 16, 12, 9, 22,
										11, 40, 21, 26, 27, 39, 13, 19, 3, 7,
										4, 28, 25, 31, 20, 8, 10, 17, 33, 15,
										36, 18, 35, 30, 34, 29, 37, 6 },
								{ 3, 23, 22, 34, 37, 39, 40, 10, 15, 13, 4, 12,
										29, 14, 28, 25, 26, 7, 32, 6, 27, 2,
										16, 30, 17, 18, 21, 31, 19, 1, 9, 5,
										36, 24, 38, 11, 20, 8, 33, 35 },
								{ 13, 35, 15, 7, 30, 16, 25, 10, 9, 36, 26, 20,
										34, 27, 19, 3, 31, 4, 33, 24, 37, 28,
										32, 29, 6, 39, 38, 22, 11, 12, 23, 18,
										21, 17, 8, 40, 5, 2, 14, 1 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 4 && t == 5) {
						int[][] gene = {
								{ 2, 69, 71, 66, 1, 32, 92, 39, 61, 101, 51,
										14, 46, 23, 52, 27, 62, 64, 81, 104,
										107, 5, 109, 22, 17, 91, 13, 76, 28,
										97, 48, 54, 117, 7, 35, 68, 41, 47,
										121, 78, 115, 31, 99, 84, 106, 29, 89,
										108, 93, 33, 34, 21, 70, 6, 44, 49, 67,
										102, 72, 3, 40, 26, 100, 42, 43, 45,
										59, 114, 20, 77, 25, 30, 24, 113, 4,
										110, 63, 74, 65, 53, 105, 82, 79, 37,
										111, 19, 87, 36, 119, 75, 94, 83, 12,
										55, 95, 96, 80, 98, 60, 58, 18, 57, 11,
										50, 103, 9, 38, 16, 10, 90, 85, 15, 88,
										8, 56, 116, 73, 118, 112, 120, 86 },
								{ 106, 6, 97, 9, 15, 17, 18, 39, 24, 63, 3, 42,
										115, 45, 50, 21, 34, 41, 80, 85, 114,
										92, 118, 40, 37, 47, 4, 95, 5, 2, 14,
										78, 58, 7, 59, 61, 36, 23, 96, 32, 29,
										111, 105, 25, 28, 69, 74, 72, 94, 33,
										121, 46, 84, 64, 38, 93, 31, 43, 71,
										12, 48, 81, 62, 119, 56, 49, 73, 86,
										60, 117, 113, 67, 68, 100, 108, 103,
										79, 52, 51, 109, 70, 53, 83, 11, 27,
										101, 44, 107, 116, 90, 13, 16, 88, 87,
										75, 10, 26, 65, 99, 1, 91, 20, 82, 104,
										98, 55, 19, 76, 35, 77, 22, 54, 89, 30,
										110, 66, 8, 57, 112, 120, 102 },
								{ 4, 114, 10, 64, 12, 95, 120, 70, 19, 20, 49,
										22, 34, 39, 42, 79, 31, 46, 111, 55,
										13, 33, 119, 36, 63, 52, 9, 40, 43, 61,
										67, 83, 38, 106, 100, 30, 65, 29, 57,
										58, 28, 112, 105, 3, 8, 74, 75, 35, 73,
										47, 76, 51, 16, 93, 82, 94, 37, 44, 48,
										108, 88, 7, 50, 6, 77, 53, 23, 32, 89,
										62, 87, 101, 85, 113, 102, 86, 17, 5,
										60, 117, 97, 98, 26, 92, 41, 84, 91,
										118, 110, 21, 121, 45, 96, 78, 25, 59,
										109, 115, 11, 99, 71, 107, 18, 103, 24,
										66, 68, 104, 69, 14, 15, 116, 54, 56,
										2, 1, 90, 72, 27, 80, 81 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 5 && t == 3) {
						int[][] gene = {
								{ 11, 1, 2, 5, 7, 12, 8, 13, 9, 4, 10, 3, 6 },
								{ 9, 6, 5, 3, 2, 7, 11, 10, 4, 1, 13, 12, 8 },
								{ 7, 13, 11, 8, 3, 9, 4, 10, 12, 5, 2, 6, 1 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 5 && t == 4) {
						int[][] gene = {
								{ 1, 40, 5, 26, 39, 27, 38, 33, 24, 2, 36, 19,
										14, 28, 34, 6, 30, 32, 13, 35, 25, 23,
										7, 16, 4, 31, 11, 9, 22, 17, 29, 20, 3,
										10, 37, 18, 12, 21, 8, 15 },
								{ 30, 28, 39, 20, 17, 34, 21, 3, 24, 6, 36, 9,
										29, 18, 2, 38, 14, 19, 11, 13, 10, 8,
										1, 26, 27, 33, 23, 40, 12, 32, 22, 5,
										31, 7, 35, 16, 25, 37, 4, 15 },
								{ 33, 31, 38, 7, 27, 32, 17, 15, 22, 20, 24, 8,
										35, 23, 4, 10, 29, 21, 25, 19, 40, 39,
										11, 13, 36, 12, 16, 28, 30, 2, 14, 3,
										37, 5, 34, 26, 9, 6, 1, 18 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 5 && t == 5) {
						int[][] gene = {
								{ 2, 5, 94, 86, 1, 14, 70, 8, 31, 121, 73, 88,
										23, 36, 21, 39, 120, 51, 103, 62, 90,
										53, 66, 68, 30, 61, 41, 77, 59, 6, 48,
										28, 105, 35, 12, 79, 115, 16, 11, 49,
										56, 99, 18, 69, 83, 72, 9, 109, 10, 89,
										111, 25, 37, 38, 74, 81, 40, 43, 87,
										54, 24, 75, 15, 55, 114, 57, 67, 78,
										60, 45, 46, 50, 4, 64, 101, 71, 22, 80,
										85, 17, 95, 82, 63, 97, 20, 44, 19, 27,
										42, 116, 118, 92, 65, 76, 13, 106, 58,
										117, 110, 34, 98, 102, 52, 47, 7, 96,
										108, 107, 33, 32, 29, 91, 113, 26, 84,
										3, 93, 112, 100, 104, 119 },
								{ 3, 31, 6, 8, 9, 62, 82, 85, 111, 57, 26, 80,
										42, 30, 112, 15, 81, 53, 54, 68, 119,
										10, 61, 13, 97, 27, 89, 11, 18, 94, 14,
										41, 72, 5, 121, 98, 106, 2, 76, 1, 96,
										21, 12, 25, 114, 29, 64, 75, 71, 99,
										35, 36, 37, 47, 20, 40, 43, 46, 38, 58,
										60, 52, 116, 56, 24, 51, 101, 23, 48,
										45, 34, 19, 65, 66, 39, 103, 87, 74,
										32, 16, 59, 79, 83, 86, 73, 95, 117,
										22, 17, 105, 67, 55, 93, 49, 28, 91,
										104, 90, 44, 100, 63, 102, 107, 88,
										113, 118, 70, 78, 109, 110, 92, 7, 120,
										4, 115, 77, 69, 84, 50, 108, 33 },
								{ 48, 22, 7, 92, 52, 46, 13, 16, 10, 117, 21,
										100, 64, 28, 29, 30, 111, 33, 114, 61,
										36, 37, 90, 72, 39, 23, 68, 26, 95, 4,
										49, 17, 97, 56, 58, 118, 59, 40, 102,
										108, 63, 82, 105, 119, 60, 93, 70, 53,
										11, 75, 47, 9, 25, 57, 83, 103, 78, 81,
										87, 43, 20, 77, 116, 74, 94, 101, 15,
										45, 55, 76, 121, 104, 31, 67, 113, 106,
										19, 120, 85, 41, 14, 32, 35, 115, 91,
										89, 98, 69, 88, 112, 80, 2, 5, 12, 107,
										73, 6, 86, 96, 54, 8, 18, 24, 62, 66,
										27, 42, 44, 110, 50, 51, 3, 34, 99, 65,
										71, 38, 84, 1, 79, 109 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 6 && t == 3) {
						int[][] gene = {
								{ 2, 12, 11, 1, 5, 10, 6, 4, 7, 9, 3, 8, 13 },
								{ 3, 8, 9, 7, 1, 5, 13, 6, 2, 11, 10, 12, 4 },
								{ 4, 10, 11, 6, 8, 12, 13, 1, 7, 9, 5, 2, 3 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 6 && t == 4) {
						int[][] gene = {
								{ 5, 11, 25, 2, 1, 34, 14, 22, 23, 9, 36, 7,
										40, 4, 33, 37, 19, 16, 31, 30, 6, 8,
										32, 28, 12, 15, 3, 18, 39, 17, 29, 27,
										10, 24, 20, 35, 38, 13, 26, 21 },
								{ 9, 18, 38, 3, 31, 23, 27, 15, 1, 11, 36, 12,
										26, 6, 19, 5, 8, 29, 39, 25, 14, 24,
										16, 40, 34, 32, 2, 33, 7, 4, 20, 13,
										37, 35, 28, 22, 10, 30, 17, 21 },
								{ 4, 22, 29, 33, 7, 28, 37, 40, 20, 13, 34, 32,
										1, 38, 17, 10, 31, 3, 25, 35, 19, 27,
										26, 6, 16, 39, 18, 12, 9, 15, 8, 24,
										11, 5, 23, 14, 30, 2, 21, 36 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 6 && t == 5) {
						int[][] gene = {
								{ 2, 5, 14, 41, 1, 3, 6, 8, 9, 15, 17, 18, 23,
										24, 26, 27, 42, 44, 45, 50, 51, 53, 54,
										68, 69, 71, 72, 77, 78, 80, 81, 4, 7,
										10, 11, 12, 13, 16, 19, 20, 21, 22, 25,
										28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
										38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
										66, 67, 70, 73, 74, 75, 76, 79, 82, 83,
										84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102,
										103, 104, 105, 106, 107, 108, 109, 110,
										111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121 },
								{ 3, 6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42,
										44, 45, 50, 51, 53, 54, 68, 69, 71, 72,
										77, 78, 80, 81, 1, 2, 5, 14, 41, 4, 7,
										10, 11, 12, 13, 16, 19, 20, 21, 22, 25,
										28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
										38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
										66, 67, 70, 73, 74, 75, 76, 79, 82, 83,
										84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102,
										103, 104, 105, 106, 107, 108, 109, 110,
										111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121 },
								{ 4, 7, 10, 11, 12, 13, 16, 19, 20, 21, 22, 25,
										28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
										38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
										66, 67, 70, 73, 74, 75, 76, 79, 82, 83,
										84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102,
										103, 104, 105, 106, 107, 108, 109, 110,
										111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121, 1, 2, 5, 14, 41, 3, 6,
										8, 9, 15, 17, 18, 23, 24, 26, 27, 42,
										44, 45, 50, 51, 53, 54, 68, 69, 71, 72,
										77, 78, 80, 81 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 7 && t == 3) {
						int[][] gene = {
								{ 2, 1, 5, 13, 3, 12, 7, 4, 10, 6, 9, 11, 8 },
								{ 11, 9, 3, 1, 12, 5, 8, 6, 2, 7, 13, 10, 4 },
								{ 13, 4, 1, 9, 7, 11, 6, 3, 10, 5, 12, 8, 2 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 7 && t == 4) {
						int[][] gene = {
								{ 5, 26, 8, 2, 1, 25, 14, 3, 23, 16, 40, 12,
										30, 4, 17, 38, 27, 33, 37, 32, 21, 6,
										15, 28, 20, 31, 11, 19, 39, 7, 34, 10,
										9, 22, 29, 35, 36, 13, 24, 18 },
								{ 12, 34, 23, 17, 3, 37, 32, 30, 15, 27, 36, 9,
										1, 6, 2, 29, 39, 14, 18, 33, 5, 35, 16,
										26, 28, 21, 8, 40, 24, 4, 22, 7, 19,
										13, 25, 10, 11, 20, 38, 31 },
								{ 13, 39, 20, 8, 32, 22, 40, 29, 4, 7, 36, 15,
										18, 1, 10, 31, 27, 25, 19, 28, 3, 12,
										26, 30, 9, 34, 37, 23, 33, 17, 35, 2,
										6, 14, 16, 5, 24, 11, 21, 38 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}
					if (s == 7 && t == 5) {
						int[][] gene = {
								{ 2, 5, 14, 41, 1, 3, 6, 8, 9, 15, 17, 18, 23,
										24, 26, 27, 42, 44, 45, 50, 51, 53, 54,
										68, 69, 71, 72, 77, 78, 80, 81, 4, 7,
										10, 11, 12, 13, 16, 19, 20, 21, 22, 25,
										28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
										38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
										66, 67, 70, 73, 74, 75, 76, 79, 82, 83,
										84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102,
										103, 104, 105, 106, 107, 108, 109, 110,
										111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121 },
								{ 3, 6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42,
										44, 45, 50, 51, 53, 54, 68, 69, 71, 72,
										77, 78, 80, 81, 1, 2, 5, 14, 41, 4, 7,
										10, 11, 12, 13, 16, 19, 20, 21, 22, 25,
										28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
										38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
										66, 67, 70, 73, 74, 75, 76, 79, 82, 83,
										84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102,
										103, 104, 105, 106, 107, 108, 109, 110,
										111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121 },
								{ 4, 7, 10, 11, 12, 13, 16, 19, 20, 21, 22, 25,
										28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
										38, 39, 40, 43, 46, 47, 48, 49, 52, 55,
										56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
										66, 67, 70, 73, 74, 75, 76, 79, 82, 83,
										84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
										94, 95, 96, 97, 98, 99, 100, 101, 102,
										103, 104, 105, 106, 107, 108, 109, 110,
										111, 112, 113, 114, 115, 116, 117, 118,
										119, 120, 121, 1, 2, 5, 14, 41, 3, 6,
										8, 9, 15, 17, 18, 23, 24, 26, 27, 42,
										44, 45, 50, 51, 53, 54, 68, 69, 71, 72,
										77, 78, 80, 81 } };
						for (int i = 0; i < permuationList.length; i++) {
							List finalstate1 = finalState(gene,
									permuationList[i]);
							List finalstate2 = partialOptimization(gene,
									permuationList[i]);

							totalRehandling1 = totalRehandling1
									+ rehandlingNumber(finalstate1);
							totalRehandling2 = totalRehandling2
									+ rehandlingNumber(finalstate2);
						}
					}

					long endTime = System.currentTimeMillis();
					long calculateTime = endTime - startTime;
					System.out.println("运行时间： " + calculateTime);
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t"
							+ permuationList.length + "\t" + totalRehandling1
							+ "\t" + totalRehandling2 + "\t" + calculateTime);

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