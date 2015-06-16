package com.iskyshop.foundation.test;


public class TestPattern {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s="mozilla/5.0 (iphone; cpu iphone os 7_1_2 like mac os x) applewebkit/537.51.2 (khtml, like gecko) mobile/11d257 micromessenger/5.3.1";
		String reg = ".+?\\(iphone; cpu \\w+ os [1-9]\\d*_\\d+_\\d+ \\w+ mac os x\\).+";
		System.out.println(s.matches(reg));

	}

}
