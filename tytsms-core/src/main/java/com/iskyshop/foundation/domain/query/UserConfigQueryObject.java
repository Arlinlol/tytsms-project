package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class UserConfigQueryObject extends QueryObject {
	public UserConfigQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public UserConfigQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
