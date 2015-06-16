package com.iskyshop.manage.buyer.Tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * 
 * <p>
 * Title:CartTools.java
 * </p>
 * 
 * <p>
 * Description:订单工具类，用以处理各个店铺的优惠劵信息
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
 * @author jy
 * 
 * @date 2014年5月12日
 * 
 * @version 1.0
 */
@Component
public class CartTools {
	@Autowired
	private IUserService userService;
	@Autowired
	private ICouponInfoService couponInfoService;

	/**
	 * 查出该用户在店铺（包括自营）所可以使用的优惠券
	 * 
	 * @param store_id
	 * @param total_price
	 * @return
	 */
	public List<CouponInfo> query_coupon(String store_id, String total_price) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Map params = new HashMap();
		List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();
		params.put("coupon_order_amount",
				BigDecimal.valueOf(CommUtil.null2Double(total_price)));
		params.put("user_id", user.getId());
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		params.put("status", 0);
		if (store_id.equals("self")) {// 自营
			params.put("coupon_type", 0);
			couponinfos = this.couponInfoService
					.query("select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_type=:coupon_type "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
							params, -1, -1);
		} else {// 第三方经营
			params.put("store_id", CommUtil.null2Long(store_id));
			couponinfos = this.couponInfoService
					.query("select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.store.id=:store_id "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
							params, -1, -1);
		}
		return couponinfos;
	}

	/**
	 * 手机端优惠券查询
	 * 
	 * @param store_id
	 * @param total_price
	 * @return
	 */
	public List<CouponInfo> mobile_query_coupon(String store_id,
			String total_price, String user_id) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map params = new HashMap();
		List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();
		params.put("coupon_order_amount",
				BigDecimal.valueOf(CommUtil.null2Double(total_price)));
		params.put("user_id", user.getId());
		params.put("coupon_begin_time", new Date());
		params.put("coupon_end_time", new Date());
		params.put("status", 0);
		if (store_id.equals("self")) {// 自营
			params.put("coupon_type", 0);
			couponinfos = this.couponInfoService
					.query("select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_type=:coupon_type "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
							params, -1, -1);
		} else {// 第三方经营
			params.put("store_id", CommUtil.null2Long(store_id));
			couponinfos = this.couponInfoService
					.query("select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount "
							+ "and obj.status=:status and obj.user.id=:user_id and obj.coupon.store.id=:store_id "
							+ "and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
							params, -1, -1);
		}
		return couponinfos;
	}
}
