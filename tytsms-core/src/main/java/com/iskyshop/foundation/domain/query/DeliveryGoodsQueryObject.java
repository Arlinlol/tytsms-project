package com.iskyshop.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.query.QueryObject;

public class DeliveryGoodsQueryObject extends QueryObject {
	public DeliveryGoodsQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public DeliveryGoodsQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
