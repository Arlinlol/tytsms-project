package com.iskyshop.view.mobileWap.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.CouponInfoQueryObject;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * @info 买家优惠券管理控制器，商场管理员赠送给买家优惠券后，买家可以在这里查看优惠券信息
 * @since V1.3
 * @version iskyshop_b2b2c 1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class MobileWapBuyerCouponAction  extends MobileWapBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ICouponInfoService couponInfoService;
	
	@Autowired
	private IUserService userService;

	@RequestMapping("/mobileWap/buyer_coupon.htm")
	public ModelAndView coupon(HttpServletRequest request,
			HttpServletResponse response, String token,
			String beginCount, String selectCount) {
		ModelAndView mv = new JModelAndView("mobileWap/view/buyer/coupon.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true;
		Map json_map = new HashMap();
		List coupon_list = new ArrayList();
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		
		if (verify && user_id != null && !user_id.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(user_id));
					params.put("status", 0);
					List<CouponInfo> couponinfos = this.couponInfoService
							.query("select obj from CouponInfo obj where obj.user.id=:user_id and obj.status=:status",
									params, CommUtil.null2Int(beginCount),
									CommUtil.null2Int(selectCount));
					for (CouponInfo ci : couponinfos) {
						Map map = new HashMap();
						map.put("coupon_sn", ci.getCoupon_sn());
						map.put("coupon_addTime", ci.getAddTime());
						String status = "未使用";
						if (ci.getStatus() == 1) {
							status = "已使用";
						}
						if (ci.getStatus() == -1) {
							status = "已过期";
						}
						map.put("coupon_status", status);
						map.put("coupon_amount", ci.getCoupon()
								.getCoupon_amount());
						map.put("coupon_order_amount", ci.getCoupon()
								.getCoupon_order_amount());
						map.put("coupon_beginTime", ci.getCoupon()
								.getCoupon_begin_time());
						map.put("coupon_endTime", ci.getCoupon()
								.getCoupon_end_time());
						map.put("coupon_id", ci.getId());
						map.put("coupon_name", ci.getCoupon().getCoupon_name());
						map.put("coupon_info", "优惠"
								+ ci.getCoupon().getCoupon_amount() + "元");
						coupon_list.add(map);
					}
					json_map.put("coupon_list", coupon_list);
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
		System.out.println("json_map:" + json_map);
		mv.addObject("json_map", json_map);
		return mv;
	}
}
