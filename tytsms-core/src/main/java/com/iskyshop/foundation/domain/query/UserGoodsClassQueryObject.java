package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class UserGoodsClassQueryObject extends QueryObject {
	public UserGoodsClassQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public UserGoodsClassQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
