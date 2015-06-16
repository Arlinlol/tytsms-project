package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class GoodsCartQueryObject extends QueryObject {
	public GoodsCartQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public GoodsCartQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
