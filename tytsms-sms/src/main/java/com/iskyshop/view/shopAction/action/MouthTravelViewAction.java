package com.iskyshop.view.shopAction.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.ActFileTools;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsFloor;
import com.iskyshop.foundation.domain.Partner;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IArticleClassService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsFloorService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.INavigationService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPartnerService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.moudle.chatting.service.IChattingLogService;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsFloorViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: Complaint.java
 * </p>
 * 
 * <p>
 * Description:商城首页控制器
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
 * @author erikzhang、jinxinzhe、hezeng
 * 
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class MouthTravelViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IPartnerService partnerService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private INavigationService navigationService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsFloorViewTools gf_tools;
	@Autowired
	private GoodsClassViewTools gcViewTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IChattingLogService chattinglogService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private ICouponInfoService couponInfoService;

	private int index_recommend_count = 5;// 首页推荐商品及推荐用户喜欢的商品个数，所有在这个页面位置的商品都以该数量作为查询基准，定义为一个参数，便于修改

	/**
	 * 让嘴巴去旅行活动首页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("shop/mouthTravel/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/activityIndex.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
	/*	List<Goods> mouth_travel_goods = this.goodsService.queryActionGoods();
		User user = SecurityUserHolder.getCurrentUser();
		if(user != null){
			Coupon coupon = this.couponService.getObjById(ActFileTools.COUPON_ID);
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("coupon_id", coupon.getId());
			List<CouponInfo> couponList = this.couponInfoService.query(""
					+ "select obj from CouponInfo obj where obj.user.id=:user_id and obj.coupon.id=:coupon_id", params, -1, -1);
			if(couponList.size()>0){
				mv.addObject("eggCoupon", "eggCoupon");
			}
		}
		mv.addObject("mouth_travel_goods", mouth_travel_goods);*/
		return mv;
	}
	

}
