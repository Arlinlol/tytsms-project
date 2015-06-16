package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.query.ConsultQueryObject;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
/**
 * 
* <p>Title: ConsultManageAction.java</p>

* <p>Description: 系统咨询管理类，</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author hezeng

* @date 2014年4月24日

* @version iskyshop_b2b2c 1.0
 */
@Controller
public class ConsultManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IConsultService consultService;

	/**
	 * Consult列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "咨询列表", value = "/admin/consult_list.htm*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
	@RequestMapping("/admin/consult_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType , String store_name , String consult_user_userName,
			String consult_content,String beginTime,String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/consult_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.setPageSize(1);
		if(store_name != null && !"".equals(store_name)){
			qo.addQuery("obj.goods.goods_store.store_name", new SysMap("store_name",
					"%" + store_name + "%"), "like");
		}
		if (consult_user_userName != null && !consult_user_userName.equals("")) {
			qo.addQuery("obj.consult_user.userName", new SysMap("userName",
					CommUtil.null2String(consult_user_userName).trim()), "=");
		}
		if (consult_content != null && !consult_content.equals("")) {
			qo.addQuery("obj.consult_content", new SysMap("consult_content",
					"%" + consult_content + "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)),
					"<=");
		}
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/consult_list.htm",
				"", params, pList, mv);
		mv.addObject("consult_user_userName", consult_user_userName);
		mv.addObject("consult_content", consult_content);
		mv.addObject("store_name",store_name);
		return mv;
	}

	@SecurityMapping(title = "咨询删除", value = "/admin/consult_del.htm*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
	@RequestMapping("/admin/consult_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Consult consult = this.consultService.getObjById(Long
						.parseLong(id));
				this.consultService.delete(Long.parseLong(id));
			}
		}
		return "redirect:consult_list.htm?currentPage=" + currentPage;
	}
}