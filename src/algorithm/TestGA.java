package algorithm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TestGA {

	public static void main(String[] args) {
		MyProperties myProperties = new MyProperties();
		try {
			String path = myProperties.getproperties("path");
			FileWriter fileWriter = new FileWriter(path + "genetic.txt", false);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			double[] ratio = { (double) 1 / 6, (double) 1 / 6, (double) 2 / 3 };
			for (int s = 4; s < 8; s++) {
				for (int t = 3; t < 6; t++) {
					Fitness.setTier(t);
					Fitness.setRow(s);
					Fitness.setpH(ratio[0]);
					Fitness.setpM(ratio[1]);
					Fitness.setpL(ratio[2]);
					long startTime = System.currentTimeMillis();
					GeneticAlgorithmEngine ga = new GeneticAlgorithmEngine(10, 1000, 1, 0.1, 1, 1);
					ga.start(s, t, ratio);
					long endTime = System.currentTimeMillis();
					long calculateTime = endTime - startTime;
					System.out.println("运行时间： " + calculateTime);
					printWriter.println(s + "\t" + t + "\t" + 3 + "\t" + calculateTime);

					int[][] temp = ga.getBestFitnessChromo().getGene();
					for (int k = 0; k < temp.length; k++) {
						printWriter.print("{");
						for (int i = 0; i < temp[k].length; i++) {
							if(i== temp[k].length-1){
								printWriter.print(temp[k][i] + "},");
							}
							else{
							printWriter.print(temp[k][i] + ",");
							}
						}
						
						printWriter.println();
					}
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
