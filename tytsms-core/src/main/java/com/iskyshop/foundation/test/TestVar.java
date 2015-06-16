package com.iskyshop.foundation.test;

import com.iskyshop.core.tools.CommUtil;

public class TestVar {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String suffix = "";
		String imageSuffix = "gif|jpg|jpeg|bmp|png";
		String[] list = imageSuffix.split("\\|");
		for (String l : list) {
			suffix = "*." + l + ";" + suffix;
		}
		System.out.println(suffix.substring(0, suffix.length() - 1));
	}

}
