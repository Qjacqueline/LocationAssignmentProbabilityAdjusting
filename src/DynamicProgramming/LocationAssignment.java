package DynamicProgramming;

import java.util.ArrayList;
import java.util.Date;
import java.io.*;
import java.util.*;
import javax.swing.*;

class DialogBox {
	void createDialogBox(String title, String content) {
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		JOptionPane.showMessageDialog(frame, content, title, JOptionPane.ERROR_MESSAGE);
		frame.dispose();
	}
}

class MyProperties {
	String getproperties(String properties) {
		// \u8BFB\u6587\u4EF6\u6240\u5728\u7684\u6839\u76EE\u5F55
		Properties properties1 = System.getProperties();
		String path = properties1.getProperty("user.dir");
		String rootPath = path.substring(0, 1);
		// \u914D\u7F6E\u6587\u4EF6\u5305\u62EC\u76EE\u5F55\u548C\u5C5E\u6027
		InputStream inputStream;
		Properties p = new Properties();
		try {
			try {
				inputStream = new BufferedInputStream(new FileInputStream(rootPath
						+ ":\\zcr\\program\\LocationAssignmentProbabilityAdjustment\\config.properties"));
				p.load(inputStream);
				String proPath = p.getProperty("path");
				p.setProperty("path", rootPath + proPath);
			} catch (FileNotFoundException e) {
				DialogBox dialogBox = new DialogBox();
				dialogBox.createDialogBox("\u8B66\u544A",
						"\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
								+ e.toString());
			}
		} catch (IOException e) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("\u8B66\u544A ",
					"\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
							+ e.toString());
		}
		return p.getProperty(properties);
	}
}

class DynamicProgram {
	int stateSize;
	double finalObjectiveValue;
	String[] followingStateNumber;
	int[][] followingStateWeight;
	int[] arrivingWeightGroup;
	double[] currentRehandleTimes;
	int[] tierNoChosen;
	int[] stateNumberChosen;
	int[] stateWeightChosen;

	int[] stackEmptySlotNumber;
	int[] stackWeightGroup;
	double[][] stackValueForEachWeight;

	int calculateRehandleTimes(int sc, int gn) {
		int rehandleTimes = 0;

		int r1 = sc % 10;
		int r10 = (sc % 100) / 10;
		int r100 = (sc % 1000) / 100;

		if (gn == 1) {
			if (r10 + r100 > 0)
				rehandleTimes = 1;
		}

		if (gn == 10) {
			if (r100 > 0)
				rehandleTimes = 1;
		}

		return rehandleTimes;
	}

	double calculateObjectiveValue(int t, String nc, int[] sc, int[] gn, String[] ncPrevious, int[][] scPrevious,
			double[] vaPrevious, double[] ratio, boolean[] havingReaming) {
		followingStateNumber = new String[gn.length];
		followingStateWeight = new int[gn.length][sc.length];
		arrivingWeightGroup = new int[gn.length];
		currentRehandleTimes = new double[gn.length];
		tierNoChosen = new int[gn.length];
		stateNumberChosen = new int[gn.length];
		stateWeightChosen = new int[gn.length];

		double[] objectiveValue = new double[gn.length];
		for (int i = 0; i < gn.length; i++) {
			if (havingReaming[i]) {
				objectiveValue[i] = Double.MAX_VALUE;
			} else {
				objectiveValue[i] = 0.0;
			}
		}

		// ����ļ�װ��
		for (int i = 0; i < gn.length; i++) {

			if (havingReaming[i]) {
				// ÿһ���п�λ�õ�stack��Ҫ���һ��
				for (int j = 0; j < nc.length(); j++) {
					if (nc.charAt(j) > '0') {
						String ncTemp = nc;
						int[] scTemp = new int[sc.length];
						for (int k = 0; k < sc.length; k++)
							scTemp[k] = sc[k];

						// ���Կ����ǿ�λ�õĲ���Ҳ���ǵ��Ｏװ����Է��õĲ�����ֵԽ��Խ�ڵײ�
						int tierNoTemp = Integer.parseInt(String.valueOf(nc.charAt(j)));
						int stateWeightChosenTemp = sc[j]; // ��stack��Ȩ�����

						int rehandleTimes = calculateRehandleTimes(scTemp[j], gn[i]);

						if (objectiveValue[i] > rehandleTimes) {
							// adjust nc
							String firstPart = "";
							String thirdPart = "";
							String secondPart = "";
							if (j > 0)
								firstPart = ncTemp.substring(0, j - 1 - 0 + 1);
							if (j < nc.length() - 1)
								thirdPart = ncTemp.substring(j + 1, ncTemp.length() - 1 - (j + 1) + 1 + (j + 1));
							int leftEmpty = Integer.parseInt(String.valueOf(ncTemp.charAt(j))) - 1;
							secondPart = String.valueOf(leftEmpty);
							ncTemp = firstPart + secondPart + thirdPart;

							// adjust sc
							scTemp[j] = scTemp[j] + gn[i];

							// re-sequence nc and
							// sc��//��ʵֻ�Ǹ�ݣ���������Ϊ��֤һ���ԣ�Ҳ��ԣ���������
							for (int k = 0; k < ncTemp.length() - 1; k++) {
								int first = Integer.parseInt(String.valueOf(ncTemp.charAt(k)));
								int second = Integer.parseInt(String.valueOf(ncTemp.charAt(k + 1)));
								String firstPart2 = "";
								String fourthPart2 = "";
								String secondPart2 = "";
								String thirdPart2 = "";
								if (first < second) {
									if (k > 0)
										firstPart2 = ncTemp.substring(0, k - 1 - 0 + 1);
									if (k < nc.length() - 2)
										fourthPart2 = ncTemp.substring(k + 2, ncTemp.length() - 1 - (k + 2) + 1
												+ (k + 2));
									secondPart2 = String.valueOf(second);
									thirdPart2 = String.valueOf(first);
									ncTemp = firstPart2 + secondPart2 + thirdPart2 + fourthPart2;
									// re-sequence sc
									int tempSc = scTemp[k + 1];
									scTemp[k + 1] = scTemp[k];
									scTemp[k] = tempSc;
								}
							}

							// re-sequence sc��//�Ծ�����ȣ��ģ�����밴����Ȩ����Ͻ�������
							boolean needed = true;
							while (needed) {
								needed = false;
								for (int k = 0; k < ncTemp.length() - 1; k++) {
									int first = Integer.parseInt(String.valueOf(ncTemp.charAt(k)));
									int second = Integer.parseInt(String.valueOf(ncTemp.charAt(k + 1)));
									int first1 = scTemp[k];
									int second1 = scTemp[k + 1];
									if (first == second && first1 < second1) {
										needed = true;
										int tempSc = scTemp[k + 1];
										scTemp[k + 1] = scTemp[k];
										scTemp[k] = tempSc;
									}
								}
							}

							// ���ҵ�����㡡��ȡ���״̬��,����㲻һ�����
							int startIndex = 0;
							for (int k = 0; k < ncPrevious.length; k++) {
								if (ncTemp.equals(ncPrevious[k])) {
									startIndex = k;
									break;
								}
							}

							int start = startIndex;
							int interval = 100;
							while (start < ncPrevious.length) {
								if (ncTemp.equals(ncPrevious[start])) {
									boolean small = false;
									for (int m = 0; m < scTemp.length; m++) {
										// scPrevious�ǰ��մӴ�С��������ģ����scTemp[m]
										// <
										// scPrevious[start][m]����Ҫƥ���sc_temp[m]Ӧ�û��ں��
										if (scTemp[m] < scPrevious[start][m]) {
											boolean allEqual = true;
											for (int k = 0; k < m; k++) { // �ж�֮ǰ���Ƿ����
												if (scTemp[k] != scPrevious[start][k]) {
													allEqual = false;
													break;
												}
											}
											if (allEqual) {
												small = true;
												break;
											}
										}
									}

									if (small) { // ���� Ҫ�ҵĻ��ں��
										int nextStart = start + interval;
										if (nextStart < ncPrevious.length) {
											if (ncTemp.compareTo(ncPrevious[nextStart]) > 0) { // �ҹ�ͷ�ˣ���Ҫ�ҵ�����֮ǰ
												break;
											}
											if (ncTemp.equals(ncPrevious[nextStart])) {
												boolean small2 = false;
												for (int m = 0; m < scTemp.length; m++) {
													if (scTemp[m] < scPrevious[nextStart][m]) {
														boolean allEqual2 = true;
														for (int k = 0; k < m; k++) {
															if (scTemp[k] != scPrevious[nextStart][k]) {
																allEqual2 = false;
																break;
															}
														}
														if (allEqual2) {
															small2 = true;
															break;
														}
													}
												}
												if (small2) { // ���� Ҫ�ҵĻ��ں��
													start = start + interval + 1;
												}
												if (!small2) {
													break;
												}
											}
										} else {
											break;
										}
									} else {
										break;
									}
								}
							}

							// �ҵ�ƥ���
							double followingValue = 0.0;
							for (int k = start; k < ncPrevious.length; k++) {
								if (ncTemp.equals(ncPrevious[k])) {
									boolean match = true;
									for (int m = 0; m < scTemp.length; m++)
										if (scTemp[m] != scPrevious[k][m]) {
											match = false;
											break;
										}
									if (match) {
										followingValue = vaPrevious[k];
										break;
									}
								}
							}

							// calculate objective value
							double valueTemp = followingValue + rehandleTimes;
							if (objectiveValue[i] > valueTemp) {
								objectiveValue[i] = valueTemp;
								followingStateNumber[i] = ncTemp;
								for (int m = 0; m < scTemp.length; m++)
									followingStateWeight[i][m] = scTemp[m];
								arrivingWeightGroup[i] = gn[i];
								currentRehandleTimes[i] = rehandleTimes;
								tierNoChosen[i] = tierNoTemp;
								stateNumberChosen[i] = tierNoTemp; // ���ģ������Ĳ��
								stateWeightChosen[i] = stateWeightChosenTemp; // ���ģ�����������Ȩ��
							}
						}
					}
				}
			}
		}
		double totalObjectiveValue = 0.0;
		for (int i = 0; i < objectiveValue.length; i++)
			totalObjectiveValue = totalObjectiveValue + objectiveValue[i] * ratio[i];
		return totalObjectiveValue;
	}

	String[] calculateEmptyPermutation(int s, int t, int n) {
		ArrayList<String> value = new ArrayList<String>();

		if (s == 2) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					if (i + ii == n) {
						value.add(String.valueOf(i) + String.valueOf(ii));
					}
		}

		if (s == 3) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--) {
						if (i + ii + j == n) {
							value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j));
						}
					}
		}

		if (s == 4) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--) {
							if (i + ii + j + jj == n) {
								value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
										+ String.valueOf(jj));
							}
						}
		}

		if (s == 5) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--) {
								if (i + ii + j + jj + k == n) {
									value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
											+ String.valueOf(jj) + String.valueOf(k));
								}
							}
		}

		if (s == 6) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--) {
									if (i + ii + j + jj + k + kk == n) {
										value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
												+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk));
									}
								}
		}

		if (s == 7) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--)
									for (int h = kk; h >= 0; h--) {
										if (i + ii + j + jj + k + kk + h == n) {
											value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
													+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk)
													+ String.valueOf(h));
										}
									}
		}

		if (s == 8) {
			for (int i = t; i >= 0; i--)
				for (int ii = i; ii >= 0; ii--)
					for (int j = ii; j >= 0; j--)
						for (int jj = j; jj >= 0; jj--)
							for (int k = jj; k >= 0; k--)
								for (int kk = k; kk >= 0; kk--)
									for (int h = kk; h >= 0; h--)
										for (int hh = h; hh >= 0; hh--) {
											if (i + ii + j + jj + k + kk + h + hh == n) {
												value.add(String.valueOf(i) + String.valueOf(ii) + String.valueOf(j)
														+ String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk)
														+ String.valueOf(h) + String.valueOf(hh));
											}
										}
		}

		String[] result = new String[value.size()];

		for (int i = 0; i < value.size(); i++)
			result[i] = (String) value.get(i);

		for (int i = 0; i < result.length; i++)
			System.out.println(result[i]);
		return result;
	}

	/**
	 * ���������ΪfullColumnNumberʱ��������״̬
	 * 
	 * @param fullColumn
	 * @param fullColumnNumber
	 * @return
	 */
	int[][] generateOtherFullColumnsState(int[] fullColumn, int fullColumnNumber) {

		ArrayList<Integer> value = new ArrayList<Integer>();
		if (fullColumnNumber == 1) {
			for (int i = 0; i < fullColumn.length; i++) {
				value.add(fullColumn[i]);
			}
		}

		if (fullColumnNumber == 2) {
			for (int i = 0; i < fullColumn.length; i++)
				for (int ii = i; ii < fullColumn.length; ii++) {
					value.add(fullColumn[i]);
					value.add(fullColumn[ii]);
				}
		}

		if (fullColumnNumber == 3) {
			for (int i = 0; i < fullColumn.length; i++)
				for (int ii = i; ii < fullColumn.length; ii++)
					for (int j = ii; j < fullColumn.length; j++) {
						value.add(fullColumn[i]);
						value.add(fullColumn[ii]);
						value.add(fullColumn[j]);
					}
		}

		if (fullColumnNumber == 4) {
			for (int i = 0; i < fullColumn.length; i++)
				for (int ii = i; ii < fullColumn.length; ii++)
					for (int j = ii; j < fullColumn.length; j++)
						for (int jj = j; jj < fullColumn.length; jj++) {
							value.add(fullColumn[i]);
							value.add(fullColumn[ii]);
							value.add(fullColumn[j]);
							value.add(fullColumn[jj]);
						}
		}

		if (fullColumnNumber == 5) {
			for (int i = 0; i < fullColumn.length; i++)
				for (int ii = i; ii < fullColumn.length; ii++)
					for (int j = ii; j < fullColumn.length; j++)
						for (int jj = j; jj < fullColumn.length; jj++)
							for (int k = jj; k < fullColumn.length; k++) {
								value.add(fullColumn[i]);
								value.add(fullColumn[ii]);
								value.add(fullColumn[j]);
								value.add(fullColumn[jj]);
								value.add(fullColumn[k]);
							}
		}

		if (fullColumnNumber == 6) {
			for (int i = 0; i < fullColumn.length; i++)
				for (int ii = i; ii < fullColumn.length; ii++)
					for (int j = ii; j < fullColumn.length; j++)
						for (int jj = j; jj < fullColumn.length; jj++)
							for (int k = jj; k < fullColumn.length; k++)
								for (int kk = k; kk < fullColumn.length; kk++) {
									value.add(fullColumn[i]);
									value.add(fullColumn[ii]);
									value.add(fullColumn[j]);
									value.add(fullColumn[jj]);
									value.add(fullColumn[k]);
									value.add(fullColumn[kk]);
								}
		}

		if (fullColumnNumber == 7) {
			for (int i = 0; i < fullColumn.length; i++)
				for (int ii = i; ii < fullColumn.length; ii++)
					for (int j = ii; j < fullColumn.length; j++)
						for (int jj = j; jj < fullColumn.length; jj++)
							for (int k = jj; k < fullColumn.length; k++)
								for (int kk = k; kk < fullColumn.length; kk++)
									for (int m = kk; m < fullColumn.length; m++) {
										value.add(fullColumn[i]);
										value.add(fullColumn[ii]);
										value.add(fullColumn[j]);
										value.add(fullColumn[jj]);
										value.add(fullColumn[k]);
										value.add(fullColumn[kk]);
										value.add(fullColumn[m]);
									}
		}

		if (fullColumnNumber == 8) {
			for (int i = 0; i < fullColumn.length; i++)
				for (int ii = i; ii < fullColumn.length; ii++)
					for (int j = ii; j < fullColumn.length; j++)
						for (int jj = j; jj < fullColumn.length; jj++)
							for (int k = jj; k < fullColumn.length; k++)
								for (int kk = k; kk < fullColumn.length; kk++)
									for (int m = kk; m < fullColumn.length; m++)
										for (int mm = m; mm < fullColumn.length; mm++) {
											value.add(fullColumn[i]);
											value.add(fullColumn[ii]);
											value.add(fullColumn[j]);
											value.add(fullColumn[jj]);
											value.add(fullColumn[k]);
											value.add(fullColumn[kk]);
											value.add(fullColumn[m]);
											value.add(fullColumn[mm]);
										}
		}

		int[][] returnValue = new int[value.size() / fullColumnNumber][fullColumnNumber];
		for (int i = 0; i < returnValue.length; i++)
			for (int j = 0; j < fullColumnNumber; j++)
				returnValue[i][j] = value.get(i * fullColumnNumber + j);

		return returnValue;
	}

	// calculate the number of stacks on which there are empty slots
	/**
	 * ����״̬�ļ���
	 * 
	 * @param nc
	 * @param t
	 * @param gn
	 * @return
	 */
	int[][] calculateWeightPermutation(String nc, int t, int[] gn) {
		int totalColumnWithContainers = 0;
		for (int i = 0; i < nc.length(); i++) {
			if (nc.charAt(i) > '0')
				totalColumnWithContainers++;
		}

		ArrayList<Integer> value = new ArrayList<Integer>();

		// ������������Ѿ��Ѷ����˵Ķ�
		int[] fullColumnState = generateColumnState(t, gn);
		int[][] otherfullColumnsState = null;
		if (totalColumnWithContainers < nc.length()) {
			otherfullColumnsState = generateOtherFullColumnsState(fullColumnState, nc.length()
					- totalColumnWithContainers);
		}

		if (totalColumnWithContainers == 0) {
			for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
				for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {
					value.add(otherfullColumnsState[fr][fc]);
				}
			}
		}

		if (totalColumnWithContainers == 1) {
			// ��stackӵ�еļ�װ�����
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int[] column1;
			column1 = generateColumnState(alreadyHave1, gn);

			for (int i = 0; i < column1.length; i++) {
				if (totalColumnWithContainers < nc.length()) {
					for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
						value.add(column1[i]); // ȡ��һ��
						for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
							value.add(otherfullColumnsState[fr][fc]);
						}
					}
				} else {
					value.add(column1[i]); // ȡ��һ��
				}
			}
		}

		if (totalColumnWithContainers == 2) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				// ȷ����λ����ȵ���stack֮�䣬����Ȩ�ش������ǰ��
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int j = startPosition2; j < columnStateSize2; j++) {
					if (totalColumnWithContainers < nc.length()) {
						for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
							value.add(column1[i]); // ȡ��һ��
							value.add(column2[j]); // ȡ�ڶ���
							for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
								value.add(otherfullColumnsState[fr][fc]);
							}
						}
					} else {
						value.add(column1[i]); // ȡ��һ��
						value.add(column2[j]); // ȡ�ڶ���
					}
				}
			}
		}

		if (totalColumnWithContainers == 3) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						if (totalColumnWithContainers < nc.length()) {
							for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
								value.add(column1[i]);
								value.add(column2[ii]);
								value.add(column3[j]);
								for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
									value.add(otherfullColumnsState[fr][fc]);
								}
							}
						} else {
							value.add(column1[i]);
							value.add(column2[ii]);
							value.add(column3[j]);
						}
					}
				}
			}
		}

		if (totalColumnWithContainers == 4) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							if (totalColumnWithContainers < nc.length()) {
								for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
									value.add(column1[i]);
									value.add(column2[ii]);
									value.add(column3[j]);
									value.add(column4[jj]);
									for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
										value.add(otherfullColumnsState[fr][fc]);
									}
								}
							} else {
								value.add(column1[i]);
								value.add(column2[ii]);
								value.add(column3[j]);
								value.add(column4[jj]);
							}
						}
					}
				}
			}
		}

		if (totalColumnWithContainers == 5) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								if (totalColumnWithContainers < nc.length()) {
									for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
										value.add(column1[i]);
										value.add(column2[ii]);
										value.add(column3[j]);
										value.add(column4[jj]);
										value.add(column5[k]);
										for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
											value.add(otherfullColumnsState[fr][fc]);
										}
									}
								} else {
									value.add(column1[i]);
									value.add(column2[ii]);
									value.add(column3[j]);
									value.add(column4[jj]);
									value.add(column5[k]);
								}
							}
						}
					}
				}
			}
		}

		if (totalColumnWithContainers == 6) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int alreadyHave6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int[] column6 = generateColumnState(alreadyHave6, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			int columnStateSize6 = column6.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								int startPosition6;
								if (alreadyHave6 == alreadyHave5)
									startPosition6 = k;
								else
									startPosition6 = 0;
								for (int kk = startPosition6; kk < columnStateSize6; kk++) {
									if (totalColumnWithContainers < nc.length()) {
										for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
											value.add(column1[i]);
											value.add(column2[ii]);
											value.add(column3[j]);
											value.add(column4[jj]);
											value.add(column5[k]);
											value.add(column6[kk]);
											for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
												value.add(otherfullColumnsState[fr][fc]);
											}
										}
									} else {
										value.add(column1[i]);
										value.add(column2[ii]);
										value.add(column3[j]);
										value.add(column4[jj]);
										value.add(column5[k]);
										value.add(column6[kk]);
									}
								}
							}
						}
					}
				}
			}
		}

		if (totalColumnWithContainers == 7) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int alreadyHave6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
			int alreadyHave7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int[] column6 = generateColumnState(alreadyHave6, gn);
			int[] column7 = generateColumnState(alreadyHave7, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			int columnStateSize6 = column6.length;
			int columnStateSize7 = column7.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								int startPosition6;
								if (alreadyHave6 == alreadyHave5)
									startPosition6 = k;
								else
									startPosition6 = 0;
								for (int kk = startPosition6; kk < columnStateSize6; kk++) {
									int startPosition7;
									if (alreadyHave7 == alreadyHave6)
										startPosition7 = kk;
									else
										startPosition7 = 0;
									for (int m = startPosition7; m < columnStateSize7; m++) {
										if (totalColumnWithContainers < nc.length()) {
											for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
												value.add(column1[i]);
												value.add(column2[ii]);
												value.add(column3[j]);
												value.add(column4[jj]);
												value.add(column5[k]);
												value.add(column6[kk]);
												value.add(column7[m]);
												for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
													value.add(otherfullColumnsState[fr][fc]);
												}
											}
										} else {
											value.add(column1[i]);
											value.add(column2[ii]);
											value.add(column3[j]);
											value.add(column4[jj]);
											value.add(column5[k]);
											value.add(column6[kk]);
											value.add(column7[m]);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (totalColumnWithContainers == 8) {
			int alreadyHave1 = t - Integer.parseInt(String.valueOf(nc.charAt(0)));
			int alreadyHave2 = t - Integer.parseInt(String.valueOf(nc.charAt(1)));
			int alreadyHave3 = t - Integer.parseInt(String.valueOf(nc.charAt(2)));
			int alreadyHave4 = t - Integer.parseInt(String.valueOf(nc.charAt(3)));
			int alreadyHave5 = t - Integer.parseInt(String.valueOf(nc.charAt(4)));
			int alreadyHave6 = t - Integer.parseInt(String.valueOf(nc.charAt(5)));
			int alreadyHave7 = t - Integer.parseInt(String.valueOf(nc.charAt(6)));
			int alreadyHave8 = t - Integer.parseInt(String.valueOf(nc.charAt(7)));
			int[] column1 = generateColumnState(alreadyHave1, gn);
			int[] column2 = generateColumnState(alreadyHave2, gn);
			int[] column3 = generateColumnState(alreadyHave3, gn);
			int[] column4 = generateColumnState(alreadyHave4, gn);
			int[] column5 = generateColumnState(alreadyHave5, gn);
			int[] column6 = generateColumnState(alreadyHave6, gn);
			int[] column7 = generateColumnState(alreadyHave7, gn);
			int[] column8 = generateColumnState(alreadyHave8, gn);
			int columnStateSize1 = column1.length;
			int columnStateSize2 = column2.length;
			int columnStateSize3 = column3.length;
			int columnStateSize4 = column4.length;
			int columnStateSize5 = column5.length;
			int columnStateSize6 = column6.length;
			int columnStateSize7 = column7.length;
			int columnStateSize8 = column8.length;
			for (int i = 0; i < columnStateSize1; i++) {
				int startPosition2;
				if (alreadyHave2 == alreadyHave1)
					startPosition2 = i;
				else
					startPosition2 = 0;
				for (int ii = startPosition2; ii < columnStateSize2; ii++) {
					int startPosition3;
					if (alreadyHave3 == alreadyHave2)
						startPosition3 = ii;
					else
						startPosition3 = 0;
					for (int j = startPosition3; j < columnStateSize3; j++) {
						int startPosition4;
						if (alreadyHave4 == alreadyHave3)
							startPosition4 = j;
						else
							startPosition4 = 0;
						for (int jj = startPosition4; jj < columnStateSize4; jj++) {
							int startPosition5;
							if (alreadyHave5 == alreadyHave4)
								startPosition5 = jj;
							else
								startPosition5 = 0;
							for (int k = startPosition5; k < columnStateSize5; k++) {
								int startPosition6;
								if (alreadyHave6 == alreadyHave5)
									startPosition6 = k;
								else
									startPosition6 = 0;
								for (int kk = startPosition6; kk < columnStateSize6; kk++) {
									int startPosition7;
									if (alreadyHave7 == alreadyHave6)
										startPosition7 = kk;
									else
										startPosition7 = 0;
									for (int m = startPosition7; m < columnStateSize7; m++) {
										int startPosition8;
										if (alreadyHave8 == alreadyHave7)
											startPosition8 = m;
										else
											startPosition8 = 0;
										for (int mm = startPosition8; mm < columnStateSize8; mm++) {
											if (totalColumnWithContainers < nc.length()) {
												for (int fr = 0; fr < otherfullColumnsState.length; fr++) {
													value.add(column1[i]);
													value.add(column2[ii]);
													value.add(column3[j]);
													value.add(column4[jj]);
													value.add(column5[k]);
													value.add(column6[kk]);
													value.add(column7[m]);
													value.add(column8[mm]);
													for (int fc = 0; fc < otherfullColumnsState[fr].length; fc++) {// ����stack��Ҫ����
														value.add(otherfullColumnsState[fr][fc]);
													}
												}
											} else {
												value.add(column1[i]);
												value.add(column2[ii]);
												value.add(column3[j]);
												value.add(column4[jj]);
												value.add(column5[k]);
												value.add(column6[kk]);
												value.add(column7[m]);
												value.add(column8[mm]);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		int[][] sc;
		sc = new int[value.size() / nc.length()][nc.length()];
		for (int i = 0; i < value.size() / nc.length(); i++) {
			for (int j = 0; j < nc.length(); j++) {
				sc[i][j] = value.get(i * nc.length() + j); // ��ֵȡ����
			}
		}
		return sc;
	}

	/**
	 * ��һ��������columnNumber����װ��ʱ����ɸö�����п��ܵ�״̬
	 * 
	 * @param columnNumber
	 * @param gn
	 * @return
	 */
	int[] generateColumnState(int columnNumber, int[] gn) {
		ArrayList<Integer> value = new ArrayList<Integer>();

		if (gn.length == 2) {
			// ��һ������Ȩ�صļ�װ��ĸ���
			for (int i = columnNumber; i >= 0; i--)
				// �ڶ�������Ȩ�صļ�װ��ĸ���
				for (int ii = columnNumber; ii >= 0; ii--) {
					if (i + ii == columnNumber) {
						value.add(i * gn[0] + ii * gn[1]);

					}
				}
		}
		if (gn.length == 3) {
			// ��һ������Ȩ�صļ�װ��ĸ���
			for (int i = columnNumber; i >= 0; i--)
				// �ڶ�������Ȩ�صļ�װ��ĸ���
				for (int ii = columnNumber; ii >= 0; ii--)
					// ����������Ȩ�صļ�װ��ĸ���
					for (int j = columnNumber; j >= 0; j--) {
						if (i + ii + j == columnNumber) {
							value.add(i * gn[0] + ii * gn[1] + j * gn[2]);

						}
					}
		}
		if (gn.length == 4) {
			for (int i = columnNumber; i >= 0; i--)
				for (int ii = columnNumber; ii >= 0; ii--)
					for (int j = columnNumber; j >= 0; j--)
						for (int jj = columnNumber; jj >= 0; jj--) {
							if (i + ii + j + jj == columnNumber) {
								value.add(i * gn[0] + ii * gn[1] + j * gn[2] + jj * gn[3]);
							}
						}
		}

		int[] result = new int[value.size()];
		for (int i = 0; i < value.size(); i++)
			result[i] = value.get(i);

		return result;

	}

	void executeDynamicProgram(int s, int t, int[] gn, double[] ratio, int type, double[][] transProb) {
		// s: the number of stacks
		// t: the number of tiers
		// g: the number of weight groups

		//calculate the number of containers for each weight group
		int[] numberOfEachWeightGroup = new int[ratio.length];
		int partialTotalNumber = 0;
		for (int g = 0; g < ratio.length - 1; g++) {
			numberOfEachWeightGroup[g] = (int) (ratio[g] * s * t);
			partialTotalNumber = partialTotalNumber + numberOfEachWeightGroup[g];
		}
		numberOfEachWeightGroup[ratio.length - 1] = (s * t) - partialTotalNumber;

		// define the results of the previous iteration
		int n = 0;
		String[] ncPrevious;
		int[][] scPrevious;
		double[] vaPrevious;
		int tnPrevious;

		String temp = "";
		for (int j = 0; j < s; j++)
			temp = temp + "0";
		int[][] initialSc = calculateWeightPermutation(temp, t, gn);

		//when type==1, the exact model
		if (type == 1) {
			ArrayList initialValues = new ArrayList();
			for (int i = 0; i < initialSc.length; i++) {
				int[] initialAccumulatedNumberOfEachWeightGroup = new int[gn.length];
				for (int g = 0; g < gn.length; g++) {
					initialAccumulatedNumberOfEachWeightGroup[g] = 0;
				}
				for (int j = 0; j < initialSc[i].length; j++) {
					if (gn.length == 3) {
						initialAccumulatedNumberOfEachWeightGroup[0] = initialAccumulatedNumberOfEachWeightGroup[0]
								+ initialSc[i][j] / 100;
						initialAccumulatedNumberOfEachWeightGroup[1] = initialAccumulatedNumberOfEachWeightGroup[1]
								+ (initialSc[i][j] % 100) / 10;
						initialAccumulatedNumberOfEachWeightGroup[2] = initialAccumulatedNumberOfEachWeightGroup[2]
								+ initialSc[i][j] % 10;
					}
					if (gn.length == 2) {
						initialAccumulatedNumberOfEachWeightGroup[0] = initialAccumulatedNumberOfEachWeightGroup[0]
								+ initialSc[i][j] / 10;
						initialAccumulatedNumberOfEachWeightGroup[1] = initialAccumulatedNumberOfEachWeightGroup[1]
								+ initialSc[i][j] % 10;
					}
				}
				boolean initalCorrectState = true;

				for (int g = 0; g < gn.length; g++) {
					if (initialAccumulatedNumberOfEachWeightGroup[g] > numberOfEachWeightGroup[g]) {
						initalCorrectState = false;
					}
				}
				if (initalCorrectState) {
					for (int j = 0; j < initialSc[i].length; j++) {
						initialValues.add(initialSc[i][j]);
					}
				}
			}
			tnPrevious = initialValues.size() / s;
			ncPrevious = new String[tnPrevious];
			scPrevious = new int[tnPrevious][s];
			vaPrevious = new double[tnPrevious];

			for (int i = 0; i < tnPrevious; i++) {
				ncPrevious[i] = temp;
				for (int j = 0; j < s; j++) {
					scPrevious[i][j] = (int) initialValues.get(i * s + j);
				}
				vaPrevious[i] = 0.0;
			}

		} else {
			tnPrevious = initialSc.length;
			ncPrevious = new String[tnPrevious];
			scPrevious = new int[tnPrevious][s];
			vaPrevious = new double[tnPrevious];

			for (int i = 0; i < tnPrevious; i++) {
				ncPrevious[i] = temp;
				for (int j = 0; j < s; j++) {
					scPrevious[i][j] = initialSc[i][j];
				}
				vaPrevious[i] = 0.0;
			}
		}
		stateSize = tnPrevious; 

		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t)
					+ String.valueOf(gn.length) + String.valueOf(type) + ".txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			while (n < s * t) {
				n = n + 1;

				// ��λ����=n�ĸ������
				String[] nc = calculateEmptyPermutation(s, t, n);
				ArrayList values = new ArrayList();

				for (int i = 0; i < nc.length; i++) {
					int[][] sc = calculateWeightPermutation(nc[i], t, gn); //all weight state

					for (int j = 0; j < sc.length; j++) {

						// �����ۻ�����
						int[] accumulatedNumberOfEachWeightGroup = new int[gn.length];
						for (int g = 0; g < gn.length; g++) {
							accumulatedNumberOfEachWeightGroup[g] = 0;
						}
						for (int jj = 0; jj < sc[j].length; jj++) {
							if (gn.length == 3) {
								accumulatedNumberOfEachWeightGroup[0] = accumulatedNumberOfEachWeightGroup[0]
										+ sc[j][jj] / 100;
								accumulatedNumberOfEachWeightGroup[1] = accumulatedNumberOfEachWeightGroup[1]
										+ (sc[j][jj] % 100) / 10;
								accumulatedNumberOfEachWeightGroup[2] = accumulatedNumberOfEachWeightGroup[2]
										+ sc[j][jj] % 10;
							}
							if (gn.length == 2) {
								accumulatedNumberOfEachWeightGroup[0] = accumulatedNumberOfEachWeightGroup[0]
										+ sc[j][jj] / 10;
								accumulatedNumberOfEachWeightGroup[1] = accumulatedNumberOfEachWeightGroup[1]
										+ sc[j][jj] % 10;
							}
						}

						boolean[] havingRemaining = new boolean[gn.length];
						for (int g = 0; g < gn.length; g++) {
							havingRemaining[g] = true;
						}
						boolean correctState = true;
						if (type == 1) {
							for (int g = 0; g < gn.length; g++) {
								if (accumulatedNumberOfEachWeightGroup[g] > numberOfEachWeightGroup[g]) {
									correctState = false;
								}
								if (accumulatedNumberOfEachWeightGroup[g] >= numberOfEachWeightGroup[g]) {
									havingRemaining[g] = false;
								}
							}
						}

						double finalValue = 0.0;
						double[] preciseRatio = new double[gn.length];
						if (type == 1 && correctState) { 
							values.add(nc[i]);
							for (int k = 0; k < sc[j].length; k++)
								values.add(sc[j][k]);
							for (int g = 0; g < gn.length; g++) {
								preciseRatio[g] = (double) (numberOfEachWeightGroup[g] - accumulatedNumberOfEachWeightGroup[g])
										/ n;
							}
							finalValue = calculateObjectiveValue(t, nc[i], sc[j], gn, ncPrevious, scPrevious,
									vaPrevious, preciseRatio, havingRemaining);
						}

						//without adjustment
						if (type == 2) { 
							values.add(nc[i]);
							for (int k = 0; k < sc[j].length; k++)
								values.add(sc[j][k]);
							finalValue = calculateObjectiveValue(t, nc[i], sc[j], gn, ncPrevious, scPrevious,
									vaPrevious, ratio, havingRemaining);
						}

						//fuzzy adjustment
						double[] fuzzyRatio = new double[gn.length]; 
						if (type == 3) { 
							values.add(nc[i]);
							for (int k = 0; k < sc[j].length; k++)
								values.add(sc[j][k]);

							double[] fuzzyRatioStep1 = new double[gn.length];
							double totalFuzzyRatioStep1 = 0;
							for (int g = 0; g < gn.length; g++) {
								if (numberOfEachWeightGroup[g] >= accumulatedNumberOfEachWeightGroup[g]) {
									fuzzyRatioStep1[g] = (double) (numberOfEachWeightGroup[g] - accumulatedNumberOfEachWeightGroup[g])
											/ n;
								} else {
									fuzzyRatioStep1[g] = 0.0;
								}
								totalFuzzyRatioStep1 = totalFuzzyRatioStep1 + fuzzyRatioStep1[g];
							}
							double[] fuzzyRatioStep2 = new double[gn.length]; 
							for (int g = 0; g < gn.length; g++) {
								fuzzyRatioStep2[g] = fuzzyRatioStep1[g] / totalFuzzyRatioStep1;
							}
							for (int g = 0; g < gn.length; g++) {
								double tempRatio = 0.0;
								for (int g2 = 0; g2 < gn.length; g2++) {
									tempRatio = tempRatio + fuzzyRatioStep2[g2] * transProb[g2][g];
								}
								fuzzyRatio[g] = tempRatio;
							}
							finalValue = calculateObjectiveValue(t, nc[i], sc[j], gn, ncPrevious, scPrevious,
									vaPrevious, fuzzyRatio, havingRemaining);
						}

						if ((type == 1 && correctState) || (type == 2) || (type == 3)) {

							values.add(finalValue);

							if (n == s * t)
								finalObjectiveValue = finalValue;

							String scTemp = "";
							for (int m = 0; m < s; m++)
								scTemp = scTemp + sc[j][m];
							printWriter.print(nc[i] + scTemp + "\t");
							for (int g = 0; g < gn.length; g++) {
								String followingStateWeightTemp = "";
								for (int m = 0; m < s; m++)
									followingStateWeightTemp = followingStateWeightTemp + followingStateWeight[g][m];
								printWriter.print(followingStateNumber[g] + followingStateWeightTemp + "\t");
								printWriter.print(stateNumberChosen[g] + "\t");
								printWriter.print(stateWeightChosen[g] + "\t");
								/*if (type == 1 && correctState) {
									printWriter.print(preciseRatio[g] + "\t");
								}
								if (type == 2) {
									printWriter.print(ratio[g] + "\t");
								}
								if (type == 3) {
									printWriter.print(fuzzyRatio[g] + "\t");
								}*/
							}
							/*printWriter.print(finalValue + "\t");*/
							printWriter.println();
						}

					}
				}

				tnPrevious = values.size() / (s + 2); // ��һ�׶���ɵ�״̬��������״̬������״̬����ϣ�
				stateSize = stateSize + tnPrevious; // �ܵ�״̬����

				// ������һ�׶ε�ֵ
				ncPrevious = new String[tnPrevious];
				scPrevious = new int[tnPrevious][s];
				vaPrevious = new double[tnPrevious];
				for (int i = 0; i < tnPrevious; i++) {
					ncPrevious[i] = (String) values.get(i * (s + 2));
					for (int k = 1; k <= s; k++)
						scPrevious[i][k - 1] = (Integer) values.get(i * (s + 2) + k);
					vaPrevious[i] = (double) values.get(i * (s + 2) + s + 1);
				}

				for (int i = 0; i < ncPrevious.length; i++) {
					System.out.print(i + "  " + ncPrevious[i] + " ");
					for (int k = 0; k < s; k++)
						System.out.print(scPrevious[i][k] + " ");
					System.out.println(vaPrevious[i]);
				}
				System.out.println(stateSize);

			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}

	}
}

// using a static indicator
class StaticEvaluation extends BasicOperation {
	int calculateRehandleTimes(int[][] input) {
		int rehandleTimes = 0;
		for (int i = 0; i < input.length; i++) {
			int currentContainer = input[i][0];

			// ���kang's method ����ķ�������ֵ
			boolean isRehandledAlready = false;
			for (int j = 0; j < input.length; j++) {
				if (currentContainer < input[j][0] && !isRehandledAlready)
					if (input[i][1] == input[j][1] && input[i][2] < input[j][2]) {
						// the jth and ith containers are in the same stack and
						// the ith one is stacked above the jth one.
						// the smaller value of input[i][2], the higher tier
						rehandleTimes = rehandleTimes + 1;
						isRehandledAlready = true;
					}
			}

		}
		return rehandleTimes;
	}

	void evaluateTotalRehandleTimes(int s, int t, String[] gn, double[] ratio, DynamicProgram dynamicProgramming,
			boolean usingDynamicProgram, int type) {
		EvalutionPreparation evaluationPreparation = new EvalutionPreparation();
		if (usingDynamicProgram) {
			evaluationPreparation.importDynamicProgrammingResults("store_value" + String.valueOf(s) + String.valueOf(t)
					+ String.valueOf(gn.length) + String.valueOf(type) + ".txt", s, gn);
		}

		String[][] arrivingContainerPermutaions = evaluationPreparation.generateArrivaingContainerPermutation(s, t, gn,
				ratio);
		arrivingContainerPermutationSize = arrivingContainerPermutaions.length;
		int[] rehandleTimes = new int[arrivingContainerPermutaions.length];
		for (int i = 0; i < rehandleTimes.length; i++)
			rehandleTimes[i] = 0;
		int finalTotalRehandleTimes = 0;

		long beginTime8, endTime8, duration8;
		Date myDate3 = new Date();
		beginTime8 = myDate3.getTime();

		// Ӧ��RollOut����ʱ����Ҫ������ؿ����������
		int simulationTimes = 300;
		int[][] monteCarloList = new int[simulationTimes][s * t];
		for (int ss = 0; ss < simulationTimes; ss++) {
			for (int tt = 0; tt < s * t; tt++) {
				boolean isFound = false;
				while (!isFound) {
					double randomNumber = Math.random();
					double tempNumber = 0.0;
					for (int h = 0; h < gn.length; h++) {
						if (randomNumber >= tempNumber && randomNumber < tempNumber + ratio[h]) {
							monteCarloList[ss][tt] = h;
							isFound = true;
							break;
						}
						tempNumber = tempNumber + ratio[h];
					}
				}
			}
		}

		for (int i = 0; i < arrivingContainerPermutaions.length; i++) {

			String[] target = arrivingContainerPermutaions[i];

			// ��¼��ʵ�ı���״̬
			int[][] bayState = new int[s][2];
			// initialize bay_state
			for (int k = 0; k < s; k++) {
				bayState[k][0] = t;
				bayState[k][1] = 0;
			}

			// ��¼ÿһ�����Ｏװ��������λ��
			int[][] assignedYardLocation = new int[target.length][3];

			// ��������ı���״̬
			String currentNc = "";
			String currentSc = "";
			// the initial state
			for (int k = 0; k < s; k++) {
				currentNc = currentNc + String.valueOf(t);
				currentSc = currentSc + String.valueOf(0);
			}

			String currentNcScTemp = currentNc + currentSc;
			int startSearchingIndexInDynamicProgrammingResults = evaluationPreparation.dynamicProgrammingResultSize - 1;

			// ��ݶ�̬�滮�����䳡��λ��
			if (usingDynamicProgram) {
				assignLocationAccordingToDPResults(s, t, gn, evaluationPreparation, target, bayState,
						assignedYardLocation, currentNcScTemp, startSearchingIndexInDynamicProgrammingResults);
			}

			// �������ʽ�����䳡��λ��
			if (!usingDynamicProgram) {
				assignLocationAccordingToHeuristicResults(s, t, gn, ratio, target, bayState, assignedYardLocation,
						dynamicProgramming, monteCarloList);
			}

			rehandleTimes[i] = calculateRehandleTimes(assignedYardLocation);
			finalTotalRehandleTimes = finalTotalRehandleTimes + rehandleTimes[i];

			/*for (int tt = 0; tt < target.length; tt++) {
				System.out.print(target[tt] + " ");
			}
			System.out.print(rehandleTimes[i]);
			System.out.println();*/
		}

		Date myDate4 = new Date();
		endTime8 = myDate4.getTime();
		duration8 = endTime8 - beginTime8;
		System.out.println("total rehandle times:   " + finalTotalRehandleTimes);
		totalStaticRehandleTimes = finalTotalRehandleTimes;
		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "total_rehandel_times" + String.valueOf(s)
					+ String.valueOf(t) + String.valueOf(gn.length) + String.valueOf(type) + ".txt", false);
			PrintWriter printWrite = new PrintWriter(fileWriter);
			printWrite.println(s + "\t" + t + "\t" + arrivingContainerPermutaions.length + "\t"
					+ finalTotalRehandleTimes + "\t" + duration8 + "\t");
			fileWriter.close();
			printWrite.close();
		} catch (IOException f) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("error", f.toString());
		}
	}

}

// .txt�ļ�����?
class Input {
	String[][] readData(String fileName) {
		int totalLine = 0;
		String line = null;
		int columnNumber = 0;
		// ��ͳ���ļ��ж���?
		ArrayList<String> values = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			boolean firstTime = true;
			while ((line = bufferedReader.readLine()) != null) {
				String[] ss = line.split("\t");
				for (int j = 0; j < ss.length; j++)
					values.add(ss[j]);
				if (firstTime) {
					columnNumber = line.split("\t").length;
					firstTime = false;
				}
				totalLine = totalLine + 1;
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException ae) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("���� ", "���ܵ�ԭ����: 1.��Ӧ���ļ�û���ҵ���2." + ae.toString());
		}

		String[][] res = new String[totalLine][columnNumber];
		for (int i = 0; i < totalLine; i++)
			for (int j = 0; j < columnNumber; j++)
				res[i][j] = values.get(i * columnNumber + j);

		return res;
	}
}

public class LocationAssignment {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		DynamicProgram dynamicProgram = new DynamicProgram();
		MyProperties myProperties = new MyProperties();

		int[] gn = { 10, 1 };
		double[] ratio = { 0.5, 0.5 };
		int[] gn2 = { 100, 10, 1 };
		double[] ratio2 = { (double) 1 / 3, (double) 1 / 3, (double) 1 / 3 };
		double[][] transProb = { { 0.879, 0.082, 0.039 }, { 0.383, 0.438, 0.179 }, { 0.066, 0.134, 0.8 } };
//		long beginTime, endTime, duration;
//		try {
//			String path = myProperties.getproperties("path");
//			FileWriter fileWriter = new FileWriter(path + "final2.txt", false);
//			PrintWriter printWriter = new PrintWriter(fileWriter);
			for (int s = 4; s < 5; s++)
				for (int t = 4; t < 5; t++) {
					// if(s*t<=24){
//					Date myDate = new Date();
//					beginTime = myDate.getTime();
//					dynamicProgram.executeDynamicProgram(s, t, gn, ratio, 1,transProb);
					dynamicProgram.executeDynamicProgram(s, t, gn2, ratio2, 1, transProb);
//					Date myDate2 = new Date();
//					endTime = myDate2.getTime();
//					duration = endTime - beginTime;
//					printWriter.println(s + "\t" + t + "\t" + 2 + "\t" + dynamicProgram.stateSize + "\t"
//							+ dynamicProgram.finalObjectiveValue + "\t" + duration);
				}
//			fileWriter.close();
//			printWriter.close();
//		} catch (IOException f) {
//			DialogBox dialogBox = new DialogBox();
//			dialogBox.createDialogBox("error", f.toString());
//		}

		// using the static indicator to test the quality of the new model.
//		long beginTime1, endTime1, duration1;
//		try {
//			String path = myProperties.getproperties("path");
//			FileWriter fileWriter = new FileWriter(path + "static2.txt", true);
//			PrintWriter printWriter = new PrintWriter(fileWriter);
//			for (int s = 5; s < 8; s++)
//				for (int t = 3; t < 6; t++) {
//					if (s * t <= 15) {
//						String[] gnSpecial = { "10", "1" };
//						Date myDate = new Date();
//						beginTime1 = myDate.getTime();
//						StaticEvaluation staticEvaluation = new StaticEvaluation();
//						staticEvaluation.evaluateTotalRehandleTimes(s, t, gnSpecial, ratio, dynamicProgram, false);
//						Date myDate2 = new Date();
//						endTime1 = myDate2.getTime();
//						duration1 = endTime1 - beginTime1;
//						printWriter.println(s + "\t" + t + "\t" + 2 + "\t"
//								+ staticEvaluation.arrivingContainerPermutationSize + "\t"
//								+ staticEvaluation.totalStaticRehandleTimes + "\t" + duration1);
//					}
//				}
//			fileWriter.close();
//			printWriter.close();
//		} catch (IOException f) {
//			DialogBox dialogBox = new DialogBox();
//			dialogBox.createDialogBox("error", f.toString());
//		}

		// for the case with three weight groups.
		// using dymanic programming to calculate the optimal locations.

//		int[] gn2 = { 100, 10, 1 };
//		double[] ratio2 = { (double) 1 / 6, (double) 1 / 6, (double) 2 / 3 };
//		double[][] transProb = { { 0.879, 0.082, 0.039 }, { 0.383, 0.438, 0.179 }, { 0.066, 0.134, 0.8 } };
//		int type = 3;

//		long beginTime2, endTime2, duration2;
//		try {
//			String path = myProperties.getproperties("path");
//			FileWriter fileWriter = new FileWriter(path + "final3" + String.valueOf(type) + ".txt", false);
//			PrintWriter printWriter = new PrintWriter(fileWriter);
//			for (int s = 4; s < 8; s++)
//				for (int t = 3; t < 6; t++)
//					// {
//					if (s * t <= 24) {
//						Date myDate = new Date();
//						beginTime2 = myDate.getTime();
//						dynamicProgram.executeDynamicProgram(s, t, gn2, ratio2, type, transProb);
//						Date myDate2 = new Date();
//						endTime2 = myDate2.getTime();
//						duration2 = endTime2 - beginTime2;
//						printWriter.println(s + "\t" + t + "\t" + 3 + "\t" + type + "\t" + dynamicProgram.stateSize
//								+ "\t" + dynamicProgram.finalObjectiveValue + "\t" + duration2);
//					}
//			fileWriter.close();
//			printWriter.close();
//		} catch (IOException f) {
//			DialogBox dialogBox = new DialogBox();
//			dialogBox.createDialogBox("error", f.toString());
//		}

		
		
		// using the static indicator to test the quality of the new model.
//		long beginTime3, endTime3, duration3;
//		try {
//			String path = myProperties.getproperties("path");
//			FileWriter fileWriter = new FileWriter(path + "static3" + String.valueOf(type) + ".txt", false);
//			PrintWriter printWriter = new PrintWriter(fileWriter);
//			for (int s = 4; s < 8; s++)
//				for (int t = 3; t < 6; t++)
//					// {
//					if (s * t <= 24) {
//						String[] gnSpecial = { "100", "10", "1" };
//						Date myDate = new Date();
//						beginTime3 = myDate.getTime();
//						StaticEvaluation evaluate = new StaticEvaluation();
//						evaluate.evaluateTotalRehandleTimes(s, t, gnSpecial, ratio2, dynamicProgram, true, type);
//						Date myDate2 = new Date();
//						endTime3 = myDate2.getTime();
//						duration3 = endTime3 - beginTime3;
//						printWriter.println(s + "\t" + t + "\t" + 3 + "\t" + evaluate.arrivingContainerPermutationSize
//								+ "\t" + evaluate.totalStaticRehandleTimes + "\t" + duration3);
//					}
//			fileWriter.close();
//			printWriter.close();
//		} catch (IOException f) {
//			DialogBox dialogBox = new DialogBox();
//			dialogBox.createDialogBox("error", f.toString());
//		}
//

	}

}
