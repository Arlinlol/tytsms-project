package com.iskyshop.view.mobileWap.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.wechatpay.util.HttpTool;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.iskyshop.view.web.tools.NavViewTools;

/**
 * 
 * <p>
 * Title: MobileIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端商城前台请求控制器
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
 * @author hezeng
 * 
 * @date 2014-7-14
 * 
 * @version 1.0
 */
@Controller
public class MobileWapIntegralViewAction extends MobileWapBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;
	
	@Autowired
	private NavViewTools navTools;
	
	@Autowired
	private IGoodsCartService goodsCartService;
	
	@Autowired
	private IPaymentService paymentService;

	HttpSession indexSession ;
			
	@Autowired
	private IntegralViewTools integralViewTools;
	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/mobileWap/integral.htm")
	public ModelAndView integral(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("mobileWap/view/integral.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
	 	Map map = checkLogin(request, "");
	 	User user = null;
	 	if( map.get("user") != null){
	 		user = (User) map.get("user");
	 	}
	 	String level_name = null;
	 	if(user != null && user.getId() != null){
	 		level_name = integralViewTools
				.query_user_level_name(CommUtil.null2String(user
						.getId()));
	 	}
	 	mv.addObject("user" , user);
	 	mv.addObject("level_name" , level_name);
		return mv;
	}
	
	

	
}
