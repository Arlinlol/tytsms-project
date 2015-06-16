package com.iskyshop.manage.buyer.action;

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
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.query.ConsultQueryObject;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 卖家咨询管理器
 * 
 * @author erikzhang
 * 
 */
@Controller
public class ConsultBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IConsultService consultService;

	@SecurityMapping(title = "买家咨询列表", value = "/buyer/consult.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/consult.htm")
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_consult.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				"addTime", "desc");
		if (!CommUtil.null2String(reply).equals("")) {
			qo.addQuery("obj.reply", new SysMap("reply", CommUtil
					.null2Boolean(reply)), "=");
		}
		qo.addQuery("obj.consult_user.id", new SysMap("consult_user",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("reply", CommUtil.null2String(reply));
		return mv;
	}
}
