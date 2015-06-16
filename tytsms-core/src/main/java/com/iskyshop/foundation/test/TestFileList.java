package com.iskyshop.foundation.test;

import java.io.File;
import java.util.Date;

import com.iskyshop.core.tools.CommUtil;

public class TestFileList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String strPath="F:\\JAVA_PRO\\iskyshop\\data\\20120829_1";
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		for(File f:files){
		  System.out.println(f.getName());
		}
	}
 
}
