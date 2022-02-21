package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.math.*;

public class Fitness {

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
	 * 随机生成达到序列
	 * 
	 * @return 一组随机达到序列列表
	 * */
	private static List stackSequence() {

		// 方案一： 精确按到达概率生成到达序列
		List stack = new LinkedList();
		int[] numberOfEachWeightGroup = new int[3];
		numberOfEachWeightGroup[0] = (int) (pH * row * tier);
		for (int i = 0; i < numberOfEachWeightGroup[0]; i++) {
			stack.add(3);
		}
		numberOfEachWeightGroup[1] = (int) (pM * row * tier);
		for (int i = 0; i < numberOfEachWeightGroup[1]; i++) {
			stack.add(2);
		}
		numberOfEachWeightGroup[2] = row * tier - numberOfEachWeightGroup[0] - numberOfEachWeightGroup[1];
		for (int i = 0; i < numberOfEachWeightGroup[2]; i++) {
			stack.add(1);
		}
		Collections.shuffle(stack);

		// 方案二： 不精确按达到概率生产达到序列
		/*List stack = new LinkedList();
		for (int i =0;i<tier*row;i++){
			double t = Math.random();
			if (t <pL){
				stack.add(1);
			}else if (t<pL+pM){
				stack.add(2);
			}else{
				stack.add(3);
			}
		}*/

		return stack;
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

	private static List finalState(int[][] gene) {
		List stacksequence = stackSequence();
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

	public static double averageRehandling(int[][] gene) {
		double totalRehandling = 0;
		int simulationTimes = 1000; // 仿真次数

		for (int i = 0; i < simulationTimes; i++) {
			totalRehandling = totalRehandling + rehandlingNumber(finalState(gene));
		}

		return totalRehandling / simulationTimes;
	}

	/**
	 * @param tier
	 *            the tier to set
	 */
	public static void setTier(int tier) {
		Fitness.tier = tier;
	}

	/**
	 * @param row
	 *            the row to set
	 */
	public static void setRow(int row) {
		Fitness.row = row;
	}

	/**
	 * @param pL
	 *            the pL to set
	 */
	public static void setpL(double pL) {
		Fitness.pL = pL;
	}

	/**
	 * @param pM
	 *            the pM to set
	 */
	public static void setpM(double pM) {
		Fitness.pM = pM;
	}

	/**
	 * @param pH
	 *            the pH to set
	 */
	public static void setpH(double pH) {
		Fitness.pH = pH;
	}

}