package com.iskyshop.view.weixin.action;

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
public class WeiXinIndexViewAction extends WeiXinBaseAction{
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
			
	/**
	 * 手机客户端商城首页，返回html页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String type) {
		String user_id ="";
		HttpSession session = request.getSession();
		try{
			if(indexSession ==null ||!indexSession.getAttributeNames().hasMoreElements()){
				indexSession = session;
				indexSession.putValue("lastSessionTime", new Date());
			}else if(indexSession.getAttribute("lastSessionTime")  ==null ){
				indexSession.putValue("lastSessionTime", new Date());
			}else if(indexSession.getAttribute("lastSessionTime")  !=null 
					&&  new Date().getTime() -((Date)indexSession.getAttribute("lastSessionTime")).getTime()  > 1000*60*30){
				/***30分钟失效***/
				indexSession = session;
				indexSession.putValue("lastSessionTime", new Date());
			}
		}catch(Exception e){
			indexSession = session;
			indexSession.putValue("lastSessionTime", new Date());
		}
		
	   	String code = request.getParameter("code");
	   	if(code != null && !"".equals(code)){
		      //获取openid
	   		try{
	   			
	   			Payment payment = this.paymentService.getObjByProperty("mark", "wechatpay");
	   			if (payment == null){
	   				payment = new Payment();
	   			}
				String returnJSON = HttpTool.getToken(payment.getAppid(), payment.getAppSecret(), "authorization_code", code);
				System.out.println("returnJSON:" + returnJSON);
				JSONObject obj = JSONObject.fromObject(returnJSON);
				String openId = obj.getString("openid");
				request.setAttribute("openId",openId);
				session.putValue("openId", openId);
				System.out.println("openId:" + session.getAttribute("openId"));
	   		}catch(Exception e ){
	   			e.printStackTrace();
	   		}
			
	   	}
	/*   	else {
	   		try{
		   		Payment payment = this.paymentService.getObjByProperty("mark", "wechatpay");
	   			if (payment == null){
	   				payment = new Payment();
	   			}
		   		JSONObject jsonObject = HttpTool.httpRequest(HttpTool.getCodeUrl(payment.getAppid(), request, response),"GET", null);
	   		}catch(Exception e ){
	   			e.printStackTrace();
	   		}
	   	}*/
		
	   	Map map = checkLogin(request, user_id);
	   	if(map.get("user_id") != null){
	   		user_id = map.get("user_id").toString();
	   	}

	   	
		ModelAndView mv = new JModelAndView("weiXin/view/index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map params = new HashMap();
		params.clear();
		List<Goods> goods_hots ;
		if(indexSession.getAttribute("goods_hots") !=null){
			goods_hots = (List<Goods>) indexSession.getAttribute("goods_hots");
		}else {
			params.put("mobile_hot", 1);
			params.put("goods_status", 0);
			goods_hots = this.goodsService
					.query("select obj from Goods obj where obj.mobile_hot=:mobile_hot and obj.goods_status =:goods_status order by mobile_hotTime desc",
							params, 0, 4);
		}
		mv.addObject("goods_hots", goods_hots);
		
		params.clear();
		List<Goods> top_recommends;
		if(indexSession.getAttribute("top_recommends") !=null){
			top_recommends = (List<Goods>) indexSession.getAttribute("top_recommends");
		}else {
			params.put("mobile_recommend", 1);
			params.put("goods_status", 0);
			top_recommends = this.goodsService
					.query("select obj from Goods obj where obj.mobile_recommend=:mobile_recommend and obj.goods_status =:goods_status order by mobile_recommendTime desc",
						params, 0, 4);
		}
		mv.addObject("top_recommends", top_recommends);

		params.clear();
		List<Goods> bottom_recommends ;
		if(indexSession.getAttribute("bottom_recommends") !=null){
			bottom_recommends = (List<Goods>) indexSession.getAttribute("bottom_recommends");
		}else {
			params.put("mobile_recommend", 1);
			params.put("goods_status", 0);
			bottom_recommends = this.goodsService
					.query("select obj from Goods obj where obj.mobile_recommend=:mobile_recommend and obj.goods_status =:goods_status order by mobile_recommendTime desc",
							params, 5, 4);
		}
		mv.addObject("bottom_recommends", bottom_recommends);

		params.clear();
		List<GoodsBrand> gbs ;
		if(indexSession.getAttribute("gbs") !=null){
			gbs = (List<GoodsBrand>) indexSession.getAttribute("gbs");
		}else {
			params.put("mobile_recommend", 1);
			params.put("audit", 1);
			gbs = this.brandService
					.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.mobile_recommend=:mobile_recommend order by mobile_recommendTime desc",
						params, 0, 6);
		}
		mv.addObject("gbs", gbs);
		
		params.clear();
		List<ActivityGoods> activitygoods = new ArrayList();
		if(indexSession.getAttribute("activitygoods") !=null){
			activitygoods = (List<ActivityGoods>) indexSession.getAttribute("activitygoods");
		}else {
			params.put("ac_begin_time", new Date());
			params.put("ac_end_time", new Date());
			params.put("ac_status", 1);
			List<Activity> activitys = this.activityService
					.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and "
							+ "obj.ac_end_time>=:ac_end_time", params, 0, 1);
			if (activitys.size() > 0) {
				params.clear();
				params.put("ag_status", 1);
				params.put("goods_status", 0);
				params.put("mobile_recommend", 1);
				params.put("act_id", activitys.get(0).getId());
				activitygoods = this.activityGoodsService
						.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
								+ "and obj.mobile_recommend=:mobile_recommend and obj.act.id=:act_id "
								+ " order by mobile_recommendTime desc", params, 0,
								3);
			}
		}
		
		mv.addObject("activitygoods", activitygoods);
		
		params.clear();
		List<GroupGoods> groupgoods ;
		if(indexSession.getAttribute("groupgoods") !=null){
			groupgoods = (List<GroupGoods>) indexSession.getAttribute("groupgoods");
		}else {
			params.put("gg_status", 1);
			params.put("group_status", 0);
			params.put("mobile_recommend", 1);
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_beginTime", new Date());
			params.put("group_endTime", new Date());
			groupgoods = this.groupgoodsService
					.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.group.status=:group_status "
							+ "and obj.mobile_recommend=:mobile_recommend and obj.group.beginTime<=:group_beginTime and "
							+ "obj.group.endTime>=:group_endTime and obj.beginTime<=:beginTime and obj.endTime>=:endTime order by mobile_recommendTime desc",
							params, 0, 4);
		}
		mv.addObject("groupgoods", groupgoods);
		mv.addObject("type", type);
		mv.addObject("user_id", user_id);
		
		indexSession.putValue("groupgoods", groupgoods);
		indexSession.putValue("activitygoods", activitygoods);
		indexSession.putValue("gbs", gbs);
		indexSession.putValue("bottom_recommends", bottom_recommends);
		indexSession.putValue("top_recommends", top_recommends);
		indexSession.putValue("goods_hots", goods_hots);
		
		return mv;
	}

	
	
	
	/**
	 * 手机客户端商城首页，返回html页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/weiXin/close.htm")
	public ModelAndView close(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("weiXin/view/close.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	

	/**
	 * 前台公用顶部导航页面，使用自定义标签httpInclude.include("/weiXin/footer.htm")完成页面读取
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/weiXin/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weiXin/view/footer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession();

		String user_id = "" ;
	
		Map map = checkLogin(request, user_id);
	   	if(map.get("user_id") != null){
	   		user_id = map.get("user_id").toString();
	   	}
		
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		Map cart_map = new HashMap();
		if (user_id != null && !"".equals(user_id)) {
		
				cart_map.clear();
				cart_map.put("user_id", Long.parseLong(user_id));
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
		} 
		mv.addObject("carts", carts_user);
		mv.addObject("carts_size", carts_user.size());
		
		
		mv.addObject("navTools", navTools);
		mv.addObject("user_id" , user_id);
		return mv;
	}
	
	
	/*
	 * 获取微信的CODE
	 */
	@RequestMapping("/weiXin/getCode.htm") 
	public void  getCode(HttpServletRequest request,
			HttpServletResponse response){
		
		Payment payment = this.paymentService.getObjByProperty("mark", "wechatpay");
			if (payment == null){
				payment = new Payment();
			}
		String get_access_token_url = HttpTool.getCodeUrl(payment.getAppid(),request,response);
        response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		Map json_map = new HashMap();
		json_map.put("code", 100);
		json_map.put("url", get_access_token_url);
		String json = Json.toJson(json_map, JsonFormat.compact());
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 通过code获取微信用户openId
	 */
	@RequestMapping("/weiXin/user/banding.htm") 
	public void getWeixinOpenId(HttpServletRequest request , HttpServletResponse response){

		
			HttpSession session = request.getSession();
		   	String code = request.getParameter("code");
		   	String url = request.getParameter("url");
		   	
	      //获取openid
		   	Payment payment = this.paymentService.getObjByProperty("mark", "wechatpay");
			if (payment == null){
				payment = new Payment();
			}
			String returnJSON=HttpTool.getToken(payment.getAppid(), payment.getAppSecret(), "authorization_code", code);
			JSONObject obj = JSONObject.fromObject(returnJSON);
			String openid=obj.get("openid").toString();
			request.setAttribute("openId",openid);
			session.putValue("openId", openid);
			
//			  ModelAndView mv = new JModelAndView("weiXin/index.htm",
//						configService.getSysConfig(),
//						this.userConfigService.getUserConfig(), 1, request, response);
//			
//			return mv;
	   } 
	
	

	
	/**
	 * 和微信服务器建立连接并读取返回值
	 * 
	 */
	private JSONObject getUrlConnection(String urlString, String context,
			String requestMethod) {
		JSONObject json = null;
		HttpURLConnection http = null;
		try {
			URL url = new URL(urlString);
			http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod(requestMethod);
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			http.setConnectTimeout(30000);
			http.setReadTimeout(30000);
			http.connect();
			if (context != null && !"".equals(context)) {
				OutputStream os = http.getOutputStream();
				os.write(context.getBytes("UTF-8"));
				os.flush();
				os.close();
			}
			InputStream is = http.getInputStream();
			String returnMessage = readInputStream(is);
			json = JSONObject.fromObject(returnMessage);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			http.disconnect();
		}
		return json;
	}
	
	
	/**
	 * 读取输入流转成格式为utf-8的字符串
	 */
	public String readInputStream(InputStream in) {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(in, "UTF-8");
			reader = new BufferedReader(isr);
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (isr != null) {
					isr.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}
	
	private static void get(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);
			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// 打印响应内容长度
					System.out.println("Response content length: " + entity.getContentLength());
					// 打印响应内容
					System.out.println("Response content: " + EntityUtils.toString(entity));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			httpclient.getConnectionManager().shutdown();
		}
	}
	
}
