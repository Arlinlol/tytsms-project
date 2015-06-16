package com.iskyshop.view.mobileWap.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * @info 广告调用控制器,
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class MobileWapAdvertViewAction  extends MobileWapBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IAdvertService advertService;

	/**
	 * 广告调用方法
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobileWap/advertInvoke.htm")
	public ModelAndView advert_invoke(HttpServletRequest request,
			HttpServletResponse response, String advertPositionId) {
		ModelAndView mv = new JModelAndView("mobileWap/view/advert_invoke.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		advertPositionId = "7";
		if (advertPositionId != null && !advertPositionId.equals("")) {
			AdvertPosition ap = this.advertPositionService.getAdvertPositionById(CommUtil
					.null2Long(advertPositionId));
			mv.addObject("obj", ap);
		}
		return mv;
	}
	
	
	/**
	 * 广告调用方法
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/mobileWap/advertInvoke9.htm")
	public ModelAndView advert_invoke9(HttpServletRequest request,
			HttpServletResponse response, String advertPositionId) {
		ModelAndView mv = new JModelAndView("mobileWap/view/advert_invoke.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		advertPositionId = "9";
		if (advertPositionId != null && !advertPositionId.equals("")) {
			AdvertPosition ap = this.advertPositionService.getAdvertPositionById(CommUtil
					.null2Long(advertPositionId));
			mv.addObject("obj", ap);
		}
		return mv;
	}
	
	
	
	
	/**
	 * 广告URL跳转方法
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @param id
	 */
	@RequestMapping("/mobileWap/advert_redirect.htm")
	public void advert_redirect(HttpServletRequest request,
			HttpServletResponse response, String id) {
		try {
			Advert adv = this.advertService.getObjById(CommUtil.null2Long(id));
			if (adv != null) {
				adv.setAd_click_num(adv.getAd_click_num() + 1);
				this.advertService.update(adv);
			}
			if (adv != null) {
				String url = adv.getAd_url();
				response.sendRedirect(url);
			} else {
				response.sendRedirect(CommUtil.getURL(request));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
