package com.iskyshop.view.mobile.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: MobileClassViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端更多设置
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
 * @date 2014-8-28
 * 
 * @version 1.0
 */
@Controller
public class MobileMoreViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IDocumentService docService;

	/**
	 * 手机客户端获取当前客户端版本
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/getversion.htm")
	public void getversion(HttpServletRequest request,
			HttpServletResponse response, String type) {
		type = type.toLowerCase();
		String version = "客户端版本已是最新";
		String appUrl = null;
		Map json_map = new HashMap();
		if (type.equals("iphone")) {
			version = this.configService.getSysConfig().getIos_version();
			appUrl = this.configService.getSysConfig().getIos_download();
		}
		if (type.equals("android")) {
			version = this.configService.getSysConfig().getAndroid_version();
			appUrl = this.configService.getSysConfig().getAndroid_download();
		}
		json_map.put("version", version);
		json_map.put("appUrl", appUrl);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 手机客户端客服联系方式
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/getservice.htm")
	public void getcontact(HttpServletRequest request,
			HttpServletResponse response) {
		String telphone_list = this.configService.getSysConfig()
				.getService_telphone_list();
		String qq_list = this.configService.getSysConfig().getService_qq_list();
		Map json_map = new HashMap();
		json_map.put("telphone_list", telphone_list);
		json_map.put("qq_list", qq_list);
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 手机客户端客获取使用帮助
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/get_help_doc.htm")
	public ModelAndView get_help_doc(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("mobile/view/doc.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.docService.getObjByProperty("mark", "mobile_help");
		mv.addObject("doc", doc);
		return mv;
	}
	
	/**
	 * 手机客户端软件使用协议
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/mobile/get_software_doc.htm")
	public ModelAndView get_software_doc(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("mobile/view/doc.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.docService.getObjByProperty("mark", "mobile_software");
		mv.addObject("doc", doc);
		return mv;
	}
}
