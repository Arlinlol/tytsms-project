package com.iskyshop.manage.seller.action;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: FreeTransportValueSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台免运费额度设置
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 广州泰易淘网络科技有限公司 www.taiyitao.com
 * </p>
 * 
 * @author kingbox
 * 
 * @date 2015-5-8
 * 
 * @version taiyitao 2.0
 */
@Controller
public class FreeTransportValueSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IUserService userService;

	/**
	 * 商家店铺免运费额度
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "免运费额度", value = "/seller/free_transport_value.htm*", rtype = "seller", rname = "免运费额度", rcode = "store_free_transport", rgroup = "我的店铺")
	@RequestMapping("/seller/free_transport_value.htm")
	public ModelAndView store_free_transport(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/free_transport_value.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		return mv;
	}
	
	/**
	 * 免运费额度保存成功
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "免运费额度保存", value = "/seller/free_transport_save.htm*", rtype = "seller", rname = "免运费额度", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/free_transport_save.htm")
	public String free_transport_save(HttpServletRequest request,
			HttpServletResponse response, String store_free_price) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
			if (!CommUtil.null2String(store_free_price).equals("")
					&& !store_free_price.equals(store
							.getStore_free_price().toString())) {
				BigDecimal sfp =new BigDecimal(CommUtil.null2Amount(store_free_price));
				store.setStore_free_price(sfp);
			}
		this.storeService.update(store);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/free_transport_value.htm");
		request.getSession(false).setAttribute("op_title", "免运费额度设置成功");
		return "redirect:/seller/success.htm";
	}

}