package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class ComplaintGoodsQueryObject extends QueryObject {
	public ComplaintGoodsQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public ComplaintGoodsQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
