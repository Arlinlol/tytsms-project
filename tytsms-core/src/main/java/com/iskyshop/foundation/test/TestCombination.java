package com.iskyshop.foundation.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCombination {

	public static void main(String arg[]) {
		List<List<String>> lists = new ArrayList<List<String>>();
		String[] list1 = new String[] { "A", "B", "C", "D" };
		String[] list2 = new String[] { "A1", "B1", "C1", "D1", "E1" };
		String[] list3 = new String[] { "A2", "B2", "C3" };
		lists.add(Arrays.asList(list1));
		lists.add(Arrays.asList(list2));
		lists.add(Arrays.asList(list3));
		String[][] str = { { "A", "B", "C", "D" },
				{ "A1", "B1", "C1", "D1", "E1" }, { "A2", "B2", "C3" },{"A3","B3"} }; // 这个str可以换成动态的二维数组，或者list[]
		int max = 1;
		for (int i = 0; i < str.length; i++) {
			max *= str[i].length;
		}

		for (int i = 0; i < max; i++) {
			String s = "";
			int temp = 1; // 注意这个temp的用法。
			for (int j = 0; j < str.length; j++) {
				temp *= str[j].length;
				s += str[j][i / (max / temp) % str[j].length];
			}
			System.out.println("第 " + (i + 1) + " 个： " + s);
		}

		System.out.println(max);
	}

}
