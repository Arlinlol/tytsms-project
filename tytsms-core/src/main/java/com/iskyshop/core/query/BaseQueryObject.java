package com.iskyshop.core.query;

import org.springframework.web.servlet.ModelAndView;

/**
 * 
* <p>Title: BaseQueryObject.java</p>

* <p>Description: 基础的查询对象类，包装了page信息和order信息</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class BaseQueryObject extends QueryObject {

	public BaseQueryObject(String currentPage, ModelAndView mv, String orderBy,
			String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getQuery() {

		return super.getQuery();
	}
}
