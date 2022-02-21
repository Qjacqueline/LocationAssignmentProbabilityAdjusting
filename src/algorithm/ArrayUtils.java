package algorithm;

public class ArrayUtils {

	/**
	 * 一维int数组克隆
	 * 
	 * @param a
	 * @return
	 */
	public static int[] oneDimensionArrayClone(int[] a) {
		int[] cloneResult = new int[a.length];

		for (int i = 0; i < a.length; i++) {
			cloneResult[i] = a[i];
		}
		return cloneResult;
	}

	/**
	 * 二维int数组克隆
	 * 
	 * @param a
	 * @return
	 */
	public static int[][] twoDimensionArrayClone(int[][] a) {
		int[][] cloneResult = new int[a.length][];

		for (int i = 0; i < a.length; i++) {
			cloneResult[i] = oneDimensionArrayClone(a[i]);
		}

		return cloneResult;
	}

	/**
	 * 三维int数组克隆
	 * 
	 * @param a
	 * @return
	 */
	public static int[][][] threeDimensionArrayClone(int[][][] a) {
		int[][][] cloneResult = new int[a.length][][];

		for (int i = 0; i < a.length; i++) {
			cloneResult[i] = twoDimensionArrayClone(a[i]);
		}

		return cloneResult;
	}

	/**
	 * 四维int数组克隆
	 * 
	 * @param a
	 * @return
	 */
	public static int[][][][] fourDimensionArrayClone(int[][][][] a) {
		int[][][][] cloneResult = new int[a.length][][][];

		for (int i = 0; i < a.length; i++) {
			cloneResult[i] = threeDimensionArrayClone(a[i]);
		}

		return cloneResult;
	}
}
