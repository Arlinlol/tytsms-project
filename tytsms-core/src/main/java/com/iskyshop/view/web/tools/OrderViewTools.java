package com.iskyshop.view.web.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.IOrderFormService;

/**
 * 
 * <p>
 * Title: OrderViewTools.java
 * </p>
 * 
 * <p>
 * Description: 前端订单处理工具类，用来查询订单信息显示在前端
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-22
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class OrderViewTools {
	@Autowired
	private IOrderFormService orderFormService;

	public int query_user_order(String order_status) {
		Map params = new HashMap();
		int status = -1;
		if (order_status.equals("order_submit")) {// 已经提交
			status = 10;
		}
		if (order_status.equals("order_pay")) {// 已经付款
			status = 20;
		}
		if (order_status.equals("order_shipping")) {// 已经发货
			status = 30;
		}
		if (order_status.equals("order_receive")) {// 已经收货
			status = 40;
		}
		if (order_status.equals("order_finish")) {// 已经完成
			status = 60;
		}
		if (order_status.equals("order_cancel")) {// 已经取消
			status = 0;
		}
		params.put("status", status);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<OrderForm> ofs = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:status and obj.user.id=:user_id",
						params, -1, -1);
		return ofs.size();
	}

	public int query_store_order(String order_status) {
		if (SecurityUserHolder.getCurrentUser().getStore() != null) {
			Map params = new HashMap();
			int status = -1;
			if (order_status.equals("order_submit")) {// 已经提交
				status = 10;
			}
			if (order_status.equals("order_pay")) {// 已经付款
				status = 20;
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				status = 30;
			}
			if (order_status.equals("order_receive")) {// 已经收货
				status = 40;
			}
			if (order_status.equals("order_finish")) {// 已经完成
				status = 60;
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				status = 0;
			}
			params.put("status", status);
			params.put("store_id", SecurityUserHolder.getCurrentUser()
					.getStore().getId());
			List<OrderForm> ofs = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:status and obj.store.id=:store_id",
							params, -1, -1);
			return ofs.size();
		} else
			return 0;
	}
}
