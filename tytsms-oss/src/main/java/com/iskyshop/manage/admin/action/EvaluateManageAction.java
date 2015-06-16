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
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
/**
 * 
* <p>Title: EvaluateManageAction.java</p>

* <p>Description: 系统商品评价管理类</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014年5月27日

* @version 1.0
 */
@Controller
public class EvaluateManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;

	@SecurityMapping(title = "商品评价列表", value = "/admin/evaluate_list.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping("/admin/evaluate_list.htm")
	public ModelAndView evaluate_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String order_id , String goods_name, String userName) {
		ModelAndView mv = new JModelAndView("admin/blue/evaluate_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				orderBy, orderType);
		if (!CommUtil.null2String(order_id).equals("")) {
			qo.addQuery("obj.of.order_id", new SysMap(
					"order_id", "%" + order_id + "%"), "like");
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.evaluate_goods.goods_name", new SysMap(
					"goods_name", "%" + goods_name + "%"), "like");
		}
		if (!CommUtil.null2String(userName).equals("")) {
			qo.addQuery("obj.evaluate_user.userName", new SysMap(
					"evaluate_user", userName), "=");
		}
		mv.addObject("goods_name", goods_name);
		mv.addObject("userName", userName);
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "商品评价编辑", value = "/admin/evaluate_edit.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping("/admin/evaluate_edit.htm")
	public ModelAndView evaluate_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/evaluate_edit.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		Evaluate obj = this.evaluateService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "商品评价编辑", value = "/admin/evaluate_save.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping("/admin/evaluate_save.htm")
	public ModelAndView evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String evaluate_status, String evaluate_admin_info,
			String list_url, String edit) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		Evaluate obj = this.evaluateService.getObjById(CommUtil.null2Long(id));
		obj.setEvaluate_admin_info(evaluate_admin_info);
		obj.setEvaluate_status(CommUtil.null2Int(evaluate_status));
		this.evaluateService.update(obj);
		if (CommUtil.null2Boolean(edit) && obj.getEvaluate_status() == 2) {// 取消评价，重新计算买卖双方的信用值
			User user = obj.getEvaluate_user();
			user.setUser_credit(user.getUser_credit()
					- obj.getEvaluate_buyer_val());
			this.userService.update(user);
		}
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "商品评价编辑成功");
		return mv;
	}
}
