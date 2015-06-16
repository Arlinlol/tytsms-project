package com.iskyshop.foundation.test;

import java.util.HashMap;
import java.util.Map;

public class TestDate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String json = "[{'goods_id':'3','gods_name':'阿斯蒂芬阿斯蒂芬阿斯蒂芬'}]";
		Map map = new HashMap();
		map.put("k", json);
		System.out.println(map);
		Map map2 = new HashMap();
		map2.put("m", map);
		System.out.println(map2);
	}
}
