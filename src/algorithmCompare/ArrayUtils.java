package algorithmCompare;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
				inputStream = new BufferedInputStream(new FileInputStream(rootPath + ":\\zcr\\program\\LocationAssignmentProbabilityAdjustment\\config.properties"));
				p.load(inputStream);
				String proPath = p.getProperty("path");
				p.setProperty("path", rootPath + proPath);
			} catch (FileNotFoundException e) {
				DialogBox dialogBox = new DialogBox();
				dialogBox.createDialogBox("\u8B66\u544A", "\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
						+ e.toString());
			}
		} catch (IOException e) {
			DialogBox dialogBox = new DialogBox();
			dialogBox.createDialogBox("\u8B66\u544A ", "\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2."
					+ e.toString());
		}
		return p.getProperty(properties);
	}
}
