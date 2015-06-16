package com.iskyshop.core.ip;

/**
 * 
* <p>Title: IPEntry.java</p>

* <p>Description:纯真ip查询，该类用来读取QQWry.dat中的的IP记录信息 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class IPEntry {
	public String beginIp;
	public String endIp;
	public String country;
	public String area;

	/**
	 * 14. * 构造函数
	 */
	public IPEntry() {
		beginIp = endIp = country = area = "";
	}

}
