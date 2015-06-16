package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class GoodsSpecificationQueryObject extends QueryObject {
	public GoodsSpecificationQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public GoodsSpecificationQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
