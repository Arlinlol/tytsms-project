package com.iskyshop.core.ip;

/**
 * 
 * <p>
 * Title: IPtest.java
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */

public class IPtest {
	public static void main(String[] args) {
		// 指定纯真数据库的文件名，所在文件夹
		IPSeeker ip = new IPSeeker("QQWry.Dat", "f:/");
		String temp = "192.168.1.1";
		// 测试IP 58.20.43.13
		System.out.println(ip.getIPLocation(temp).getCountry() + ":"
				+ ip.getIPLocation(temp).getArea());
	}

}
