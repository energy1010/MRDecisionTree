package com.younger.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 */
public class FileFinder {

	public static List<File> findFiles(String baseDirName, String targetFileName, int count) {
		List<File> fileList = new ArrayList<File>();
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()){
			System.err.println("can't find the dir" + baseDirName + "is not a dir");
			return fileList;
		}
		String tempName = null;
		List<File> queue = new LinkedList<File>();
		queue.add(baseDir);
		File tempFile = null;
		while (!queue.isEmpty()) {
			tempFile = (File) ((LinkedList) queue).poll();
			if (tempFile.exists() && tempFile.isDirectory()) {
				File[] files = tempFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) { 
						queue.add(files[i]);
					} else {
						tempName =  files[i].getName(); 
						if (FileFinder.wildcardMatch(targetFileName, tempName)) {
							fileList.add(files[i].getAbsoluteFile()); 
							if ((count != 0) && (fileList.size() >= count)) {
								return fileList;
							}
						}
					}
				}
			} 
		}
		return fileList;
	}

	
	private static boolean wildcardMatch(String pattern, String str) {
		int patternLength = pattern.length();
		int strLength = str.length();
		int strIndex = 0;
		char ch;
		for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
			ch = pattern.charAt(patternIndex);
			if (ch == '*') {
				while (strIndex < strLength) {
					if (wildcardMatch(pattern.substring(patternIndex + 1),
							str.substring(strIndex))) {
						return true;
					}
					strIndex++;
				}
			} else if (ch == '?') {
				strIndex++;
				if (strIndex > strLength) {
					return false;
				}
			} else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
					return false;
				}
				strIndex++;
			}
		}
		return (strIndex == strLength);
	}

	public static void main(String[] paramert) {
		String baseDIR = "C:/temp"; 
		String fileName = "*.txt"; 
		int countNumber = 5; 
		List resultList = FileFinder.findFiles(baseDIR, fileName, countNumber); 
		if (resultList.size() == 0) {
			System.err.println("No File Fount.");
		} else {
			for (int i = 0; i < resultList.size(); i++) {
				System.out.println(resultList.get(i));
			}
		}
	}
}
