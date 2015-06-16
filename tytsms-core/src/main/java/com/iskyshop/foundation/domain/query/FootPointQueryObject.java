package com.iskyshop.foundation.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class FootPointQueryObject extends QueryObject {

	public FootPointQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	
	public FootPointQueryObject(String construct, String currentPage,
			ModelAndView mv, String orderBy, String orderType) {
		super(construct, currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}

	public FootPointQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
