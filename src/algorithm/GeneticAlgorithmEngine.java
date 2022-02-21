package algorithm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GeneticAlgorithmEngine {

	// 种群
	private ArrayList<Chromosome> population;
	// 种群规模、最大繁殖代数
	private int popSize, maxGeneration;
	// 基因换位概率、基因移位概率、基因换位个数、基因移位个数
	private double conversionRate, shiftRate;
	private int conversionTimes, shiftTimes;

	/**
	 * 遗传算法引擎的构造函数
	 * 
	 * @param popSize
	 * @param maxGeneration
	 * @param conversionRate
	 * @param shiftRate
	 * @param conversionTimes
	 * @param shiftTimes
	 */
	public GeneticAlgorithmEngine(int popSize, int maxGeneration, double conversionRate, double shiftRate, int conversionTimes, int shiftTimes) {
		super();
		this.popSize = popSize;
		this.maxGeneration = maxGeneration;
		this.conversionRate = conversionRate;
		this.shiftRate = shiftRate;
		this.conversionTimes = conversionTimes;
		this.shiftTimes = shiftTimes;
	}

	/**
	 * 初始化种群
	 */
	private void initiatePop(int t) {
		// 方法一：给定一个较优初始基因

		population = new ArrayList<Chromosome>();
		// 3层初始基因
		if (t == 3) {
			int[][] gene1 = { { 1, 2, 5, 4, 3, 6, 7, 8, 9, 10, 11, 12, 13 }, { 3, 9, 5, 2, 1, 4, 6, 7, 8, 10, 11, 12, 13 },
					{ 4, 13, 7, 10, 11, 12, 6, 8, 3, 9, 2, 5, 1 } };
			population.add(new Chromosome(gene1));
			for (int i = 1; i < popSize; i++) {
				population.add(new Chromosome(randomGene()));
			}
		}
		if (t == 4) {// 4层初始基因
			int[][] gene1 = {
					{ 1, 20, 25, 2, 5, 8, 14, 3, 6, 9, 40, 7, 19, 4, 15, 38, 16, 33, 29, 32, 23, 31, 12, 28, 34, 11, 37, 39, 17, 21, 10, 27, 22, 26, 30, 35,
							36, 13, 24, 18 },
					{ 12, 23, 17, 3, 37, 25, 32, 39, 1, 30, 36, 9, 26, 6, 2, 5, 8, 14, 15, 33, 18, 24, 16, 29, 34, 27, 21, 40, 35, 4, 22, 7, 31, 13, 28, 11,
							10, 20, 38, 19 },
					{ 13, 28, 29, 33, 32, 22, 40, 37, 4, 7, 20, 35, 34, 38, 10, 31, 27, 25, 19, 39, 24, 12, 26, 16, 9, 18, 8, 30, 36, 17, 15, 3, 6, 5, 23, 14,
							11, 2, 21, 1 } };
			population.add(new Chromosome(gene1));
			for (int i = 1; i < popSize; i++) {
				population.add(new Chromosome(randomGene()));
			}
		}
		if (t == 5) {// 5层初始基因
			int[][] gene1 = {
					{ 2, 5, 14, 41, 1, 3, 6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42, 44, 45, 50, 51, 53, 54, 68, 69, 71, 72, 77, 78, 80, 81, 4, 7, 10, 11, 12,
							13, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 43, 46, 47, 48, 49, 52, 55, 56, 57, 58, 59, 60, 61,
							62, 63, 64, 65, 66, 67, 70, 73, 74, 75, 76, 79, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101,
							102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121 },
					{ 3, 6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42, 44, 45, 50, 51, 53, 54, 68, 69, 71, 72, 77, 78, 80, 81, 1, 2, 5, 14, 41, 4, 7, 10, 11, 12,
							13, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 43, 46, 47, 48, 49, 52, 55, 56, 57, 58, 59, 60, 61,
							62, 63, 64, 65, 66, 67, 70, 73, 74, 75, 76, 79, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101,
							102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121 },
					{ 4, 7, 10, 11, 12, 13, 16, 19, 20, 21, 22, 25, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 43, 46, 47, 48, 49, 52, 55, 56, 57, 58,
							59, 60, 61, 62, 63, 64, 65, 66, 67, 70, 73, 74, 75, 76, 79, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99,
							100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 1, 2, 5, 14, 41, 3,
							6, 8, 9, 15, 17, 18, 23, 24, 26, 27, 42, 44, 45, 50, 51, 53, 54, 68, 69, 71, 72, 77, 78, 80, 81 } };
			population.add(new Chromosome(gene1));
			for (int i = 1; i < popSize; i++) {
				population.add(new Chromosome(randomGene()));
			}
		}

	}

	/**
	 * 种群初始基因
	 * 
	 * @return
	 */
	private int[][] randomGene() {
		int typeSize = Fitness.topStateNumber(); // 可行的堆放状态数 对于三层高的堆垛为13
		int[][] type = new int[3][typeSize];

		List L = new LinkedList();
		List M = new LinkedList();
		List H = new LinkedList();
		for (int j = 1; j <= typeSize; j++) {
			L.add(j);
			M.add(j);
			H.add(j);
		}

		Collections.shuffle(L); // 产生随机序列
		Collections.shuffle(M);
		Collections.shuffle(H);

		// 初始化一组LMH排序策略
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < typeSize; j++) {
				switch (i) {
				case 0:
					type[i][j] = (int) L.get(j);
					break;
				case 1:
					type[i][j] = (int) M.get(j);
					break;
				case 2:
					type[i][j] = (int) H.get(j);
					break;
				}
			}
		}

		return type;
	}

	/**
	 * 得到种群的最优个体。
	 * 
	 * @return 种群的最优个体。
	 */
	public Chromosome getBestFitnessChromo() {
		Chromosome bestChromo = population.get(0).clone();
		for (int i = 1; i < popSize; i++) {
			if (population.get(i).betterThan(bestChromo)) {
				bestChromo = population.get(i);
			}
		}
		return bestChromo;
	}

	/**
	 * 得到种群的平均适应度值。
	 * 
	 * @return 种群平均适应度值。
	 */
	public double getAverageFitness() {
		return getRealTotalFitness() / population.size();
	}

	/**
	 * 得到种群的总适应度值。
	 * 
	 * @return 种群总适应度值。
	 */
	public double getRealTotalFitness() {
		double sum = 0.0;
		for (int i = 0; i < popSize; i++) {
			sum += population.get(i).getFitness();
		}
		return sum;
	}

	/**
	 * 得到种群处理后的"总适应度值"。
	 * 
	 * @return 种群总适应度值。
	 */
	public double getTotalFitness() {
		double sum = 0.0;
		for (int i = 0; i < popSize; i++) {
			sum += 1 / (population.get(i).getFitness() + 0.01); // 相当于实际适应度函数为
																// 1/(a+0.01)
																// *重要
		}
		return sum;
	}

	/**
	 * 选择优质个体。
	 * 
	 * @return 按照一定规则选中的优质个体。
	 */
	private Chromosome getChromoRoulette() {
		Chromosome theChosenOne = null;
		double slice = Math.random() * getTotalFitness();
		double cumulatedFitness = 0;
		for (int i = 0; i < popSize; i++) {
			cumulatedFitness += 1 / (population.get(i).getFitness() + 0.01);
			if (cumulatedFitness >= slice) {
				theChosenOne = population.get(i);
				break;
			}
		}
		return theChosenOne;
	}

	/**
	 * 基因换位过程
	 * 
	 * @param cs
	 * @return 换位后的个体
	 */
	private Chromosome conversion(Chromosome cs) {
		Chromosome clonedCs = cs.clone();
		if (Math.random() < conversionRate) {
			Random r = new Random();
			// int weight = r.nextInt(3);
			int cT = r.nextInt(conversionTimes) + 1; // 换位次数
			for (int i = 0; i < cT; i++) {
				int[][] gene = clonedCs.getGene();
				for (int weight = 0; weight < 3; weight++) {
					int t1 = r.nextInt(gene[weight].length); // 选择换位位置
					int t2 = r.nextInt(gene[weight].length);

					while (t2 == t1) {
						t2 = r.nextInt(gene[weight].length);
					}

					int temp = gene[weight][t1];
					gene[weight][t1] = gene[weight][t2];
					gene[weight][t2] = temp;
				}
				clonedCs.setGene(gene);
			}
		}
		return clonedCs;
	}

	/**
	 * 基因移位过程
	 * 
	 * @param cs
	 * @return 移位后的个体
	 */
	private Chromosome shift(Chromosome cs) {
		if (Math.random() < shiftRate) {
			Random r = new Random();
			int weight = r.nextInt(3);
			int cT = r.nextInt(shiftTimes) + 1; // 换位次数
			for (int i = 0; i < cT; i++) {
				int[][] gene = cs.getGene();
				int t1 = r.nextInt(gene[weight].length - 1) + 1; // 选择移位位置
				int t2 = r.nextInt(t1) + 1;

				for (int j = 0; j < t2; j++) {
					int temp = gene[weight][t1];
					gene[weight][t1] = gene[weight][t1 - 1];
					gene[weight][t1 - 1] = temp;
					t1--;
				}
				cs.setGene(gene);
			}
		}
		return cs;
	}

	/**
	 * 产生下一代的过程。
	 * 
	 * 现采用的是先选择后繁殖的方式，以后可以改成先繁殖后选择的方式，优化效果会更好。
	 */
	private void epoch() {
		ArrayList<Chromosome> newPop = new ArrayList<Chromosome>();
		newPop.add(getBestFitnessChromo()); // 最优保持
		while (newPop.size() < popSize) {
			Chromosome father = getChromoRoulette();
			Chromosome baby1 = conversion(father);
			Chromosome baby2 = shift(baby1);
			newPop.add(baby2);
		}
		population = newPop;
	}

	public void start(int s, int t, double ratio[]) {

		initiatePop(t);
		System.out.println("第1代：");
		System.out.println("最优基因得分:" + getBestFitnessChromo().getFitness() + "    平均得分:" + getAverageFitness());
		System.out.println();
		for (int generation = 0; generation < maxGeneration; generation++) {
			epoch();
			System.out.println("第" + (generation + 2) + "代：");
			System.out.println("最优基因得分:" + getBestFitnessChromo().getFitness() + "    平均得分:" + getAverageFitness());
			System.out.println();
		}

		int[][] temp = getBestFitnessChromo().getGene();
		for (int k = 0; k < temp.length; k++) {
			for (int i = 0; i < temp[k].length; i++) {
				System.out.print(temp[k][i] + ",");
			}
			System.out.print("  ");
		}
		System.out.println();

	}

}

