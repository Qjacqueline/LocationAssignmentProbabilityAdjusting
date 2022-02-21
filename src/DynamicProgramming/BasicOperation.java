package DynamicProgramming;
public class BasicOperation {
	protected int totalDynamicRehandleTimes;
	protected int totalStaticRehandleTimes;
	protected int arrivingContainerPermutationSize;

	public BasicOperation() {
		super();
	}

	protected void assignLocationAccordingToDPResults(int s, int t, String[] gn,
			EvalutionPreparation evaluationPreparation, String[] target, int[][] bayState,
			int[][] assignedYardLocation, String currentNcScTemp, int startSearchingIndexInDynamicProgrammingResults) {
		for (int j = 0; j < target.length; j++) {
			int targetMatchingIndexInDynamicProgrammingResults = -2;
			// find the target index
			for (int k = startSearchingIndexInDynamicProgrammingResults; k >= 0; k--) {
				if (currentNcScTemp.equals(evaluationPreparation.currentState[k][0])) {
					targetMatchingIndexInDynamicProgrammingResults = k;
					startSearchingIndexInDynamicProgrammingResults = k;
					break;
				}
			}
			// find the position of the target weight group
			int targetWeightIndex = -2;
			for (int k = 0; k < gn.length; k++) {
				if (gn[k].equals(target[j])) {
					targetWeightIndex = k;
					break;
				}
			}
			currentNcScTemp = evaluationPreparation.followingState[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex];

			// ����bayState
			int stackNoChosenInBayState = -2;
			for (int k = 0; k < s; k++) {
				if (bayState[k][0] == evaluationPreparation.stateNumberChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex])
					if (bayState[k][1] == evaluationPreparation.stateWeightChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex]) {
						stackNoChosenInBayState = k;
						break;
					}
			}
			bayState[stackNoChosenInBayState][0] = bayState[stackNoChosenInBayState][0] - 1;
			if (bayState[stackNoChosenInBayState][0] == 0)
				bayState[stackNoChosenInBayState][1] = t;
			else {
				if (targetWeightIndex == gn.length - 1)
					bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 1;
				if (targetWeightIndex == gn.length - 2)
					bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 10;
				if (targetWeightIndex == gn.length - 3)
					bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 100;
				if (targetWeightIndex == gn.length - 4)
					bayState[stackNoChosenInBayState][1] = bayState[stackNoChosenInBayState][1] + 1000;
			}

			// assignedYardLocation�����ά��
			// ��һ�������ӵ�Ȩ�أ��ڶ����Ǵ洢��stack No���������tier No
			if (targetWeightIndex == gn.length - 1)
				assignedYardLocation[j][0] = 1;
			if (targetWeightIndex == gn.length - 2)
				assignedYardLocation[j][0] = 10;
			if (targetWeightIndex == gn.length - 3)
				assignedYardLocation[j][0] = 100;
			if (targetWeightIndex == gn.length - 4)
				assignedYardLocation[j][0] = 1000;
			assignedYardLocation[j][1] = stackNoChosenInBayState;
			assignedYardLocation[j][2] = evaluationPreparation.stateNumberChosen[targetMatchingIndexInDynamicProgrammingResults][targetWeightIndex];
		}
	}

	protected void assignLocationAccordingToHeuristicResults(int s, int t, String[] gn, double[] ratio, String[] target,
			int[][] bayState, int[][] assignedYardLocation, DynamicProgram dynamicProgramming, int[][] monteCarloList) {

		// Ϊ��������Ѱ�ҳ���λ��
		for (int j = 0; j < target.length; j++) {
			// find the position of the target weight group
			int targetWeightIndex = -2;
			for (int k = 0; k < gn.length; k++) {
				if (target[j].equals(gn[k])) {
					targetWeightIndex = k;
					break;
				}
			}

			// Ѱ�ҷ����������ٵ�stack
			int stackNoChosenInBayState = -2;
			double amountTemp = Double.MAX_VALUE;
			int tierNoSelected = -2;
			/*
						// ֱ��������ʽ����
						for (int k = 0; k < s; k++) {
							if (bayState[k][0] > 0) {
								for (int i = 0; i < dynamicProgramming.stackEmptySlotNumber.length; i++) {
									if (dynamicProgramming.stackEmptySlotNumber[i] == bayState[k][0]
											&& dynamicProgramming.stackWeightGroup[i] == bayState[k][1]) {
										if (dynamicProgramming.stackValueForEachWeight[i][targetWeightIndex] < amountTemp) {
											amountTemp = dynamicProgramming.stackValueForEachWeight[i][targetWeightIndex];
											stackNoChosenInBayState = k;
											tierNoSelected = bayState[k][0];
											break;
										}
									}
								}
							}
						}
						*/

			// ����RollOut�������
			for (int k = 0; k < s; k++) {
				if (bayState[k][0] > 0) {
					// ���㵱ǰbay����������ļ�װ��ĸ���
					int[] numberOfEachWeightGroup = new int[gn.length];
					for (int jj = 0; jj < gn.length; jj++) {
						numberOfEachWeightGroup[jj] = (bayState[k][1] % (Integer.valueOf(gn[jj]) * 10))
								/ Integer.valueOf(gn[jj]);
					}

					double objectiveTemp = 0.0;
					for (int jj = 0; jj < gn.length; jj++) {
						if (jj < targetWeightIndex) {
							objectiveTemp = objectiveTemp + numberOfEachWeightGroup[jj];
						}
					}

					int simulationTimes = monteCarloList.length;
					double simulationValue = 0.0;

					// ��ʼ���ؿ������
					for (int p = 0; p < simulationTimes; p++) {
						// ���һ�������bayStateTemp���������ؿ������
						int[][] bayStateTemp = new int[bayState.length][2];
						for (int m = 0; m < bayState.length; m++) {
							for (int n = 0; n < 2; n++) {
								bayStateTemp[m][n] = bayState[m][n];
							}
						}
						bayStateTemp[k][0] = bayStateTemp[k][0] - 1;
						if (bayStateTemp[k][0] == 0) {
							bayStateTemp[k][1] = t;
						} else {
							bayStateTemp[k][1] = Integer.valueOf(gn[targetWeightIndex]) + bayStateTemp[k][1];
						}

						// ������ؿ�����������
						for (int jj = j + 1; jj < monteCarloList[p].length; jj++) {
							
							double probability = 1;
							for (int jjj = j + 1; jjj <= jj; jjj++) {
								probability = probability * ratio[monteCarloList[p][jjj]];
							}
							
							// ���ؿ�����浽�Ｏװ���������index
							int targetWeightIndexTemp = monteCarloList[p][jj];

							// Ѱ�ҷ����������ٵ�stack
							int stackNoChosenInBayStateTemp = -2;
							double amountTempTemp = Double.MAX_VALUE;
							int tierNoSelectedTemp = -2;

							for (int kk = 0; kk < s; kk++) {
								if (bayStateTemp[kk][0] > 0) {
									for (int i = 0; i < dynamicProgramming.stackEmptySlotNumber.length; i++) {
										if (dynamicProgramming.stackEmptySlotNumber[i] == bayStateTemp[kk][0]
												&& dynamicProgramming.stackWeightGroup[i] == bayStateTemp[kk][1]) {
											if (dynamicProgramming.stackValueForEachWeight[i][targetWeightIndexTemp] < amountTempTemp) {
												amountTempTemp = dynamicProgramming.stackValueForEachWeight[i][targetWeightIndexTemp];
												stackNoChosenInBayStateTemp = kk;
												tierNoSelectedTemp = bayStateTemp[kk][0];
												break;
											}
										}
									}
								}
							}

							double[] numberOfEachWeightGroupTemp = new double[gn.length];
							for (int jjj = 0; jjj < gn.length; jjj++) {
								numberOfEachWeightGroupTemp[jjj] = (bayStateTemp[stackNoChosenInBayStateTemp][1] % (Integer
										.valueOf(gn[jjj]) * 10)) / Integer.valueOf(gn[jjj]);
							}

							for (int jjj = 0; jjj < gn.length; jjj++) {
								if (jjj < targetWeightIndexTemp) {
									simulationValue = simulationValue + numberOfEachWeightGroupTemp[jjj] * probability;
								}
							}

							// ����bayStateTemp
							bayStateTemp[stackNoChosenInBayStateTemp][0] = tierNoSelectedTemp - 1;
							if (bayStateTemp[stackNoChosenInBayStateTemp][0] == 0) {
								bayStateTemp[stackNoChosenInBayStateTemp][1] = t;
							} else {
								bayStateTemp[stackNoChosenInBayStateTemp][1] = Integer
										.valueOf(gn[targetWeightIndexTemp])
										+ bayStateTemp[stackNoChosenInBayStateTemp][1];
							}

						}
					}
					if (objectiveTemp + (double) (simulationValue / simulationTimes) < amountTemp) {
						amountTemp = objectiveTemp + (double) (simulationValue / simulationTimes);
						stackNoChosenInBayState = k;
						tierNoSelected = Integer.valueOf(bayState[k][0]);
					}

				}
			}

			// ����bayState
			bayState[stackNoChosenInBayState][0] = tierNoSelected - 1;
			if (bayState[stackNoChosenInBayState][0] == 0) {
				bayState[stackNoChosenInBayState][1] = t;
			} else {
				bayState[stackNoChosenInBayState][1] = Integer.valueOf(gn[targetWeightIndex])
						+ bayState[stackNoChosenInBayState][1];
			}

			// assignedYardLocation�����ά��
			// ��һ�������ӵ�Ȩ�أ��ڶ����Ǵ洢��stack No���������tier No
			if (targetWeightIndex == gn.length - 1) {
				assignedYardLocation[j][0] = 1;
			}
			if (targetWeightIndex == gn.length - 2) {
				assignedYardLocation[j][0] = 10;
			}
			if (targetWeightIndex == gn.length - 3) {
				assignedYardLocation[j][0] = 100;
			}
			if (targetWeightIndex == gn.length - 4) {
				assignedYardLocation[j][0] = 1000;
			}
			assignedYardLocation[j][1] = stackNoChosenInBayState;
			assignedYardLocation[j][2] = tierNoSelected;
		}
	}
}