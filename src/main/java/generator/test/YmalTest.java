package generator.test;

import generator.utils.FileUtil;
import org.ho.yaml.Yaml;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class YmalTest {

	public static void main(String[] args) {

		String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\model\\Controller.txt";
//		File yml = new File(projectPath);
//		try {
//			Map map =  Yaml.loadType(yml, HashMap.class);
//			System.out.println();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println();
//		FileUtil.getInstance().editFileContent(projectPath, "debug: true", "debug: false");
//		System.out.println(testFinally());
//		FileUtil.getInstance().copyFile(new File(projectPath + "application.yml"),
//				projectPath + "aa\\application-dev.xml");
//		System.out.println(FileUtil.getInstance().getFile());
//		try {
//			InputStream in = new FileInputStream(new File(projectPath));
//			InputStreamReader inReader = new InputStreamReader(in);
//			BufferedReader bf = new BufferedReader(inReader);
//			String str;
//			while ((str = bf.readLine()) != null) {
//				System.out.println(str);
//			}
//			bf.close();
//			inReader.close();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	public static String testFinally(){
		try{
			return "aa";
		}finally {
			return "bb";
		}
	}
}
