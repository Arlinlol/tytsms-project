package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class GoodsClassQueryObject extends QueryObject {
	public GoodsClassQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public GoodsClassQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
