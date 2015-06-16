package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class UserIpQueryObject extends QueryObject {
	public UserIpQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public UserIpQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
