package generator.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileUtil {

	public static boolean createFile(String filePath, String fileName, String content){

		File targetFile = new File(filePath);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + fileName));
			bw.write(content);
			bw.close();
			System.out.println(filePath + "\\" + fileName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 目录下查找目录
	 */
	public static String getFolder(String path, String folderName) {
		File file = new File(path);
		String resultPath = null;
		// 如果这个路径是文件夹
		if (file.isDirectory()) {
			// 获取路径下的所有文件
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				// 如果是需要的文件夹 则返回
				if (files[i].isDirectory() && folderName.equals(files[i].getName())) {
					resultPath = files[i].getPath();
					break;
				}
				resultPath = getFolder(files[i].getPath(), folderName);
				if (null != resultPath) break;
			}
		}
		return resultPath;
	}

	/**
	 * 转换下划线以及大小写
	 * @param firsCharBig 首字母是否大写
	 */
	public static String stringFormat(String str, boolean firsCharBig){

		String[] strs = str.split("_");
		str = "";
		for (int i = 0; i < strs.length; i++) {
			str += strs[i].substring(0, 1).toUpperCase() + strs[i].substring(1).toLowerCase();
		}
		if (firsCharBig) return str;
		return str.substring(0,1).toLowerCase() + str.substring(1);
	}

	/**
	 * 目录下查找目录
	 */
	public static String getFile(String path, String fileName) {
		File file = new File(path);
		String resultPath = null;
		// 如果这个路径是文件夹
		if (file.isDirectory()) {
			// 获取路径下的所有文件
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				// 如果是文件夹 则继续查找
				if (files[i].isDirectory()) {
					resultPath = getFile(files[i].getPath(), fileName);
				} else {
					if (files[i].getName().equals(fileName)) {
						resultPath = files[i].getPath();
						break;
					}
				}
			}
		}
		return resultPath;
	}
}
