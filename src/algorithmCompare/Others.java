package algorithmCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Others {

}

class EvalutionPreparation {
	String[][] currentState;
	String[][] followingState;
	int[][] stateNumberChosen;
	int[][] stateWeightChosen;
	int dynamicProgrammingResultSize;

	List[] generateArrivaingContainerPermutation(int s, int t, String[] gn, double[] ratio) {

		int[] numberOfEachWeightGroup = new int[ratio.length];
		int partialTotalNumber = 0;
		for (int g = 0; g < ratio.length - 1; g++) {
			numberOfEachWeightGroup[g] = (int) (ratio[g] * s * t);
			partialTotalNumber = partialTotalNumber + numberOfEachWeightGroup[g];
		}
		numberOfEachWeightGroup[ratio.length - 1] = (s * t) - partialTotalNumber;

		ArrayList<String> values = new ArrayList<String>();
		String[] permutationTemp = new String[s * t];
		List[] permutation;
		int index = 0;
		int accumulation = 10000;

	//	if (s * t >= 16) {
			List tempValues = new LinkedList();
			for (int g = 0; g < ratio.length; g++) {
				for (int j = 0; j < numberOfEachWeightGroup[g]; j++) {
					tempValues.add(gn[g]);
				}
			}

			while (index < accumulation) {
				Collections.shuffle(tempValues);
				// System.out.print(index+": ");
				for (int k = 0; k < tempValues.size(); k++) {
					values.add(String.valueOf(tempValues.get(k)));
					// System.out.print(String.valueOf(tempValues.get(k))+" ");
				}
				// System.out.println();
				index++;
			}
		//}
/*
		if (s * t == 15) {
			accumulation = 1000000;
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															for (int j2 = 0; j2 < gn.length; j2++) {
																for (int j3 = 0; j3 < gn.length; j3++) {
																	for (int j4 = 0; j4 < gn.length; j4++) {
																		if (index < accumulation) {
																			permutationTemp[0] = gn[i];
																			permutationTemp[1] = gn[i1];
																			permutationTemp[2] = gn[i2];
																			permutationTemp[3] = gn[i3];
																			permutationTemp[4] = gn[i4];
																			permutationTemp[5] = gn[i5];
																			permutationTemp[6] = gn[i6];
																			permutationTemp[7] = gn[i7];
																			permutationTemp[8] = gn[i8];
																			permutationTemp[9] = gn[i9];
																			permutationTemp[10] = gn[j];
																			permutationTemp[11] = gn[j1];
																			permutationTemp[12] = gn[j2];
																			permutationTemp[13] = gn[j3];
																			permutationTemp[14] = gn[j4];
																			int[] accumulatedNumberOfEachWeightGroup = new int[ratio.length];
																			for (int g = 0; g < ratio.length; g++) {
																				accumulatedNumberOfEachWeightGroup[g] = 0;
																			}
																			for (int p = 0; p < permutationTemp.length; p++) {
																				for (int g = 0; g < ratio.length; g++) {
																					if (permutationTemp[p].equals(gn[g])) {
																						accumulatedNumberOfEachWeightGroup[g]++;
																					}
																				}
																			}
																			boolean isCorrectState = true;
																			for (int g = 0; g < ratio.length; g++) {
																				if (accumulatedNumberOfEachWeightGroup[g] != numberOfEachWeightGroup[g]) {
																					isCorrectState = false;
																				}
																			}
																			if (isCorrectState) {
																				for (int p = 0; p < permutationTemp.length; p++) {
																					values.add(permutationTemp[p]);
																				}
																				index++;
																			}

																		} else
																			break;
																	}
																	if (index >= accumulation)
																		break;
																}
																if (index >= accumulation)
																	break;
															}
															if (index >= accumulation)
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}
		}

		if (s * t == 12) {
			accumulation = 1000000;
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												for (int i9 = 0; i9 < gn.length; i9++) {
													for (int j = 0; j < gn.length; j++) {
														for (int j1 = 0; j1 < gn.length; j1++) {
															if (index < accumulation) {
																permutationTemp[0] = gn[i];
																permutationTemp[1] = gn[i1];
																permutationTemp[2] = gn[i2];
																permutationTemp[3] = gn[i3];
																permutationTemp[4] = gn[i4];
																permutationTemp[5] = gn[i5];
																permutationTemp[6] = gn[i6];
																permutationTemp[7] = gn[i7];
																permutationTemp[8] = gn[i8];
																permutationTemp[9] = gn[i9];
																permutationTemp[10] = gn[j];
																permutationTemp[11] = gn[j1];
																int[] accumulatedNumberOfEachWeightGroup = new int[ratio.length];
																for (int g = 0; g < ratio.length; g++) {
																	accumulatedNumberOfEachWeightGroup[g] = 0;
																}
																for (int p = 0; p < permutationTemp.length; p++) {
																	for (int g = 0; g < ratio.length; g++) {
																		if (permutationTemp[p].equals(gn[g])) {
																			accumulatedNumberOfEachWeightGroup[g]++;
																		}
																	}
																}
																boolean isCorrectState = true;
																for (int g = 0; g < ratio.length; g++) {
																	if (accumulatedNumberOfEachWeightGroup[g] != numberOfEachWeightGroup[g]) {
																		isCorrectState = false;
																	}
																}
																if (isCorrectState) {
																	for (int p = 0; p < permutationTemp.length; p++) {
																		values.add(permutationTemp[p]);
																	}
																	index++;
																}

															} else
																break;
														}
														if (index >= accumulation)
															break;
													}
													if (index >= accumulation)
														break;
												}
												if (index >= accumulation)
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}
		}

		if (s * t == 9) {
			accumulation = 1000000;
			for (int i = 0; i < gn.length; i++) {
				for (int i1 = 0; i1 < gn.length; i1++) {
					for (int i2 = 0; i2 < gn.length; i2++) {
						for (int i3 = 0; i3 < gn.length; i3++) {
							for (int i4 = 0; i4 < gn.length; i4++) {
								for (int i5 = 0; i5 < gn.length; i5++) {
									for (int i6 = 0; i6 < gn.length; i6++) {
										for (int i7 = 0; i7 < gn.length; i7++) {
											for (int i8 = 0; i8 < gn.length; i8++) {
												if (index < accumulation) {
													permutationTemp[0] = gn[i];
													permutationTemp[1] = gn[i1];
													permutationTemp[2] = gn[i2];
													permutationTemp[3] = gn[i3];
													permutationTemp[4] = gn[i4];
													permutationTemp[5] = gn[i5];
													permutationTemp[6] = gn[i6];
													permutationTemp[7] = gn[i7];
													permutationTemp[8] = gn[i8];
													int[] accumulatedNumberOfEachWeightGroup = new int[ratio.length];
													for (int g = 0; g < ratio.length; g++) {
														accumulatedNumberOfEachWeightGroup[g] = 0;
													}
													for (int p = 0; p < permutationTemp.length; p++) {
														for (int g = 0; g < ratio.length; g++) {
															if (permutationTemp[p].equals(gn[g])) {
																accumulatedNumberOfEachWeightGroup[g]++;
															}
														}
													}
													boolean isCorrectState = true;
													for (int g = 0; g < ratio.length; g++) {
														if (accumulatedNumberOfEachWeightGroup[g] != numberOfEachWeightGroup[g]) {
															isCorrectState = false;
														}
													}
													if (isCorrectState) {
														for (int p = 0; p < permutationTemp.length; p++) {
															values.add(permutationTemp[p]);
														}
														index++;
													}

												} else
													break;
											}
											if (index >= accumulation)
												break;
										}
										if (index >= accumulation)
											break;
									}
									if (index >= accumulation)
										break;
								}
								if (index >= accumulation)
									break;
							}
							if (index >= accumulation)
								break;
						}
						if (index >= accumulation)
							break;
					}
					if (index >= accumulation)
						break;
				}
				if (index >= accumulation)
					break;
			}
		}

		if (s * t == 4) {
			accumulation = 1000000;
			for (int i = 0; i < gn.length; i++)
				for (int i1 = 0; i1 < gn.length; i1++)
					for (int i2 = 0; i2 < gn.length; i2++)
						for (int i3 = 0; i3 < gn.length; i3++) {
							permutationTemp[0] = gn[i];
							permutationTemp[1] = gn[i1];
							permutationTemp[2] = gn[i2];
							permutationTemp[3] = gn[i3];
							int[] accumulatedNumberOfEachWeightGroup = new int[ratio.length];
							for (int g = 0; g < ratio.length; g++) {
								accumulatedNumberOfEachWeightGroup[g] = 0;
							}
							for (int p = 0; p < permutationTemp.length; p++) {
								for (int g = 0; g < ratio.length; g++) {
									if (permutationTemp[p].equals(gn[g])) {
										accumulatedNumberOfEachWeightGroup[g]++;
									}
								}
							}
							boolean isCorrectState = true;
							for (int g = 0; g < ratio.length; g++) {
								if (accumulatedNumberOfEachWeightGroup[g] != numberOfEachWeightGroup[g]) {
									isCorrectState = false;
								}
							}
							if (isCorrectState) {
								for (int p = 0; p < permutationTemp.length; p++) {
									values.add(permutationTemp[p]);
								}
								index++;
							}

						}
		}
*/
		int number = values.size() / (s * t);
		permutation = new List[number];
		for (int i = 0; i < number; i++) {
			permutation[i] = new LinkedList();
		}
		for (int i = 0; i < number; i++) {
			for (int j = 0; j < s * t; j++) {
				permutation[i].add(Integer.valueOf(values.get(i * s * t + j)));
			}
		}

		return permutation;
	}
}