package com.iskyshop.view.shopAction.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.ActFileTools;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Share;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IShareService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * 功能描述：食品活动刮奖、分享、领取优惠劵子流程功能
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author cty 新增日期：2015年4月17日
 * @author cty 修改日期：2015年4月17日
 *
 */
@Controller
public class FoodActivityAction {

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
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IShareService shareService;

	private static final String COUPON_SEND = "conpon_send";
	private static final String SCRATCH_COUPON = "scratch_conpon";
	private static final String SHARE_ORDER_INTEGRAL = "share_order_integral";

	private static Log log = LogFactory.getLog(FoodActivityAction.class);
	
	@SecurityMapping(title = "优惠券发放保存", value = "/coupon_send_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/coupon_send_save.htm")
	public void coupon_send_save(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Coupon coupon = this.couponService.getObjById(ActFileTools.COUPON_ID);
		Store store = coupon.getStore();
		User user = SecurityUserHolder.getCurrentUser();
		boolean ischecked = false;
		if (user.getId() != CommUtil.null2Long(store.getUser().getId())) {// 排除当前用户
			HttpSession session = request.getSession();
			AtomicInteger coupon_send = (AtomicInteger) session.getAttribute(COUPON_SEND+user.getId());
			if(coupon_send == null) { //处理高并发的情况
				synchronized(FoodActivityAction.class){ //控制并发
					coupon_send = (AtomicInteger) session.getAttribute(COUPON_SEND+user.getId());
					if(coupon_send == null){
						Map params = new HashMap();
						params.put("user_id", user.getId());
						params.put("coupon_id", coupon.getId());
						List<CouponInfo> couponList = this.couponInfoService.query(""
								+ "select obj from CouponInfo obj where obj.user.id=:user_id and obj.coupon.id=:coupon_id", params, -1, -1);
						coupon_send = new AtomicInteger(couponList.size());
						request.getSession().setAttribute(COUPON_SEND+user.getId(),coupon_send);
						//判断用户领取优惠劵次数
						if(coupon_send.intValue() <1){
							coupon_send.incrementAndGet();
							ischecked = true;
						}
					}else{
						//判断用户领取优惠劵次数
						if(coupon_send.intValue() <1){
							coupon_send.incrementAndGet();
							ischecked = true;
						}
					}
				}
			}else{
				synchronized(FoodActivityAction.class){ //控制并发
					//判断用户领取优惠劵次数
					if(coupon_send.intValue() <1){
						coupon_send.incrementAndGet();
						ischecked = true;
					}
				}
			}
			if(ischecked){
				CouponInfo info = new CouponInfo();
				info.setAddTime(new Date());
				info.setCoupon(coupon);
				info.setCoupon_sn(UUID.randomUUID().toString());
				info.setUser(user);
				this.couponInfoService.save(info);
			}
		}
		try{
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("{\"result\":\""+ischecked+"\"}");
		}catch(IOException e){
			log.info(e);
		}
	}
	
	@SecurityMapping(title = "食品活动刮奖", value = "/scratch_coupon*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/scratch_coupon.htm")
	public void scratchCoupon(HttpServletRequest request,
			HttpServletResponse response,String orderId){
		Coupon coupon = this.couponService.getObjById(ActFileTools.SCRATCH_COUPON);
		Store store = coupon.getStore();
		User user = SecurityUserHolder.getCurrentUser();
		boolean ischecked = false;
		if (user.getId() != CommUtil.null2Long(store.getUser().getId())) {// 排除当前用户
			HttpSession session = request.getSession();
			AtomicInteger scratch_coupon = (AtomicInteger) session.getAttribute(SCRATCH_COUPON+orderId);
			if(scratch_coupon == null) { //处理高并发的情况
				synchronized(FoodActivityAction.class){ //控制并发
					scratch_coupon = (AtomicInteger) session.getAttribute(SCRATCH_COUPON+orderId);
					if(scratch_coupon == null){
						Map params = new HashMap();
						params.put("user_id", user.getId());
						params.put("remark", orderId);
						List<CouponInfo> couponList = this.couponInfoService.query(""
								+ "select obj from CouponInfo obj where obj.user.id=:user_id and obj.remark=:remark", params, -1, -1);
						scratch_coupon = new AtomicInteger(couponList.size());
						request.getSession().setAttribute(SCRATCH_COUPON+orderId,scratch_coupon);
						//判断用户领取优惠劵次数
						if(scratch_coupon.intValue() <1){
							scratch_coupon.incrementAndGet();
							ischecked = true;
						}
					}else{
						//判断用户领取优惠劵次数
						if(scratch_coupon.intValue() <1){
							scratch_coupon.incrementAndGet();
							ischecked = true;
						}
					}
				}
			}else{
				synchronized(FoodActivityAction.class){ //控制并发
					//判断用户领取优惠劵次数
					if(scratch_coupon.intValue() <1){
						scratch_coupon.incrementAndGet();
						ischecked = true;
					}
				}
			}
			if(ischecked){
				CouponInfo info = new CouponInfo();
				info.setAddTime(new Date());
				info.setCoupon(coupon);
				info.setCoupon_sn(UUID.randomUUID().toString());
				info.setUser(user);
				info.setRemark(orderId);
				this.couponInfoService.save(info);
			}
		}
		try{
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("{\"result\":\""+ischecked+"\"}");
		}catch(IOException e){
			log.info(e);
		}
	}
	
	@SecurityMapping(title = "食品活动分享积分", value = "/share_order_integral.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/share_order_integral.htm")
	public void shareOrderIntegral(HttpServletRequest request,
			HttpServletResponse response,String shareCode,String orderId){
		Map maps = new HashMap();
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		maps.put("share_type", 2L);
		maps.put("share_title", orderId);
		HttpSession session = request.getSession();
		AtomicInteger share_count = (AtomicInteger) session.getAttribute(SHARE_ORDER_INTEGRAL);
		boolean ischecked = true;
		if(share_count == null){
			List<Share> shareList = this.shareService.query("select obj from Share obj where obj.user.id=:user_id "
					+ "and obj.share_type=:share_type and obj.share_title=:share_title", maps, -1, -1);
			share_count = new AtomicInteger(shareList.size());
			session.setAttribute(SHARE_ORDER_INTEGRAL,share_count);
			if(share_count.intValue() >0){
				ischecked = false;
			}
		}else{
			if(share_count.intValue() >0){
				ischecked = false;
			}
		}
		if(ischecked){
			share_count.incrementAndGet();
			//根据用户ID查询用户积分
			User users = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			//用户分享信息记录
			Share shares = new Share();
			shares.setAddTime(new Date());
			shares.setDeleteStatus(0);
			shares.setShare_code(shareCode);
			shares.setShare_title(orderId);
			shares.setShare_type(2L);
			shares.setUser(users);
			this.shareService.save(shares);
			//增加用户积分
			users.setIntegral(users.getIntegral()+20);
			this.userService.update(users);
			//积分日志记录
			IntegralLog log = new IntegralLog();
			log.setAddTime(new Date());
			log.setContent("让嘴巴去旅行活动分享增加20积分");
			log.setIntegral(20);
			log.setIntegral_user(users);
			log.setType("share");
			this.integralLogService.save(log);
		}
	}
}
