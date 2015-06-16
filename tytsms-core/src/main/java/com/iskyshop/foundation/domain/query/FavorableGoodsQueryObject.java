package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class FavorableGoodsQueryObject extends QueryObject {
	public FavorableGoodsQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public FavorableGoodsQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
