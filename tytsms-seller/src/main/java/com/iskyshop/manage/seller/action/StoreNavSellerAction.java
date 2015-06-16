package com.iskyshop.manage.seller.action;

import java.text.ParseException;
import java.util.Date;

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
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreNavigation;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.StoreNavigationQueryObject;
import com.iskyshop.foundation.service.IStoreNavigationService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * 
 * <p>
 * Title:StoreNavSellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家后台店铺导航管理控制器
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
 * @author jy,wyj
 * 
 * @date 2014年4月24日
 * 
 * @version 1.0
 */
@Controller
public class StoreNavSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;

	/**
	 * StoreNavigation列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "卖家导航管理", value = "/seller/store_nav.htm*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping("/seller/store_nav.htm")
	public ModelAndView store_nav(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreNavigationQueryObject qo = new StoreNavigationQueryObject(
				currentPage, mv, orderBy, orderType);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.store.id", new SysMap("store_id", user.getStore()
				.getId()), "=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		IPageList pList = this.storenavigationService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * storenavigation添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "卖家导航添加", value = "/seller/store_nav_add.htm*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping("/seller/store_nav_add.htm")
	public ModelAndView store_nav_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_nav_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * storenavigation编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "卖家导航编辑", value = "/seller/store_nav_edit.htm*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping("/seller/store_nav_edit.htm")
	public ModelAndView store_nav_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_nav_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			StoreNavigation storenavigation = this.storenavigationService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", storenavigation);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * storenavigation保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家导航保存", value = "/seller/store_nav_save.htm*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping("/seller/store_nav_save.htm")
	public String store_nav_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd) {
		WebForm wf = new WebForm();
		StoreNavigation storenavigation = null;
		if (id.equals("")) {
			storenavigation = wf.toPo(request, StoreNavigation.class);
			storenavigation.setAddTime(new Date());
		} else {
			StoreNavigation obj = this.storenavigationService.getObjById(Long
					.parseLong(id));
			storenavigation = (StoreNavigation) wf.toPo(request, obj);
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		storenavigation.setStore(store);
		if (id.equals("")) {
			this.storenavigationService.save(storenavigation);
		} else
			this.storenavigationService.update(storenavigation);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/store_nav.htm");
		request.getSession(false).setAttribute("op_title", "保存导航成功");
		return "redirect:/seller/success.htm";
	}

	@SecurityMapping(title = "卖家导航删除", value = "/seller/store_nav_del.htm*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping("/seller/store_nav_del.htm")
	public String store_nav_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				StoreNavigation storenavigation = this.storenavigationService
						.getObjById(Long.parseLong(id));
				this.storenavigationService.delete(Long.parseLong(id));
			}
		}
		return "redirect:store_nav.htm?currentPage=" + currentPage;
	}

}