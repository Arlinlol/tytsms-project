package com.iskyshop.foundation.test;

import java.io.File;
import java.util.Arrays;

import com.iskyshop.core.tools.CommUtil;

public class TestImage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f = new File("F://JAVA_PRO//iskyshop//upload//store//163840");
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {
			File temp = files[i];
			String source = temp.getPath();
			String target = temp.getPath() + "_small.jpg";
			CommUtil.createSmall(source, target, 160,160);
		}
	}
}
