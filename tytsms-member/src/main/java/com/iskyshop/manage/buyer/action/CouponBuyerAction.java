package com.iskyshop.manage.buyer.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.query.CouponInfoQueryObject;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
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
public class CouponBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private IUserService userService;
	

	@SecurityMapping(title = "买家优惠券列表", value = "/buyer/coupon.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/coupon.htm")
	public ModelAndView coupon(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_coupon.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.couponInfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

}
