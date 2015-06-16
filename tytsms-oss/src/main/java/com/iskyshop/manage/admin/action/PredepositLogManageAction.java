package com.iskyshop.manage.admin.action;

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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.query.PredepositLogQueryObject;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
/**
 * 
* <p>Title: PredepositLogManageAction.java</p>

* <p>Description: 商城预存款管理控制器，用来显示系统预存款明细数据</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-5-30

* @version iskyshop_b2b2c 1.0
 */
@Controller
public class PredepositLogManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPredepositLogService predepositlogService;

	/**
	 * PredepositLog列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "预存款明细列表", value = "/admin/predepositlog_list.htm*", rtype = "admin", rname = "预存款明细", rcode = "predeposit_log", rgroup = "会员")
	@RequestMapping("/admin/predepositlog_list.htm")
	public ModelAndView predepositlog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String userName) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/predepositlog_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			String url = this.configService.getSysConfig().getAddress();
			if (url == null || url.equals("")) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			PredepositLogQueryObject qo = new PredepositLogQueryObject(
					currentPage, mv, orderBy, orderType);
			if (!CommUtil.null2String(userName).equals("")) {
				qo.addQuery("obj.pd_log_user.userName", new SysMap("userName",
						userName), "=");
			}
			IPageList pList = this.predepositlogService.list(qo);
			CommUtil.saveIPageList2ModelAndView(url
					+ "/admin/predepositlog_list.htm", "", params, pList, mv);
			mv.addObject("userName", userName);
		} else {
			mv = new JModelAndView("admin/blue/error.html", configService
					.getSysConfig(), this.userConfigService.getUserConfig(), 0,
					request, response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}

}