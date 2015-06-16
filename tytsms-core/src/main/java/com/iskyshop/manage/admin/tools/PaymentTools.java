package com.iskyshop.manage.admin.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: PaymentTools.java
 * </p>
 * 
 * <p>
 * Description: 支付方式处理工具类，用来管理支付方式信息，主要包括查询支付方式等
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
 * @date 2014-5-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class PaymentTools {
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IUserService userService;

	public boolean queryPayment(String mark) {
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		if (objs.size() > 0) {
			// System.out.println(objs.get(0).isInstall());
			return objs.get(0).isInstall();
		} else
			return false;
	}

	public Map queryShopPayment(String mark) {
		Map ret = new HashMap();
		Map params = new HashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		if (objs.size() == 1) {
			ret.put("install", objs.get(0).isInstall());
			ret.put("content", objs.get(0).getContent());
		} else {
			ret.put("install", false);
			ret.put("content", "");
		}
		return ret;
	}
}
