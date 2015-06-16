package com.iskyshop.foundation.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestComposite {
	public static void main(String[] args) {
		// 一维字符串数组
		String[] ele = new String[] { "红色，黑色，黄色，紫色", "XL，XXL，XXXL", "35，36，37",
 };
		StringHandler(ele);
	}

	/**
	 * 
	 * 将一维的字符串数组组合成为二维的数组
	 * 
	 * @param ele
	 * @return
	 */
	public static String StringHandler(String[] ele) {
		String res = "";
		int len = ele.length;
		String[] ts = ele[1].split("，");
		String[][] abc = new String[len][ts.length];
		for (int f = 0; f < ele.length; f++) {
			String[] sub = ele[f].split("，");
			abc[f] = sub;
		}
		String[][] ret = doExchange(abc);
		String[] result = ret[0];
		System.out.println("共有：" + result.length + "种组合！");
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		return res;
	}

	/**
	 * 
	 * 使用递归调用将二维数组中的值进行排列组合
	 * 
	 * @param doubleArrays
	 * @return
	 */
	private static String[][] doExchange(String[][] doubleArrays) {
		int len = doubleArrays.length;
		if (len >= 2) {
			int len1 = doubleArrays[0].length;
			int len2 = doubleArrays[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			int index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					temp[index] = doubleArrays[0][i] + "   |    "
							+ doubleArrays[1][j];
					index++;
				}
			}
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = doubleArrays[i];
			}
			newArray[0] = temp;
			return doExchange(newArray);
		} else {
			return doubleArrays;
		}

	}

}
