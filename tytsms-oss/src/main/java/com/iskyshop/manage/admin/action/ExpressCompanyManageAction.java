package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.query.ExpressCompanyQueryObject;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 商城平台快递公司管理，通过快递公司代码查询对应的订单信息数据
 * 
 * @since V1.3
 * @author www.iskyshop.com erikzhang
 * 
 */
@Controller
public class ExpressCompanyManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IExpressCompanyService expresscompanyService;
	@Autowired
	private IOrderFormService orderFormService;

	/**
	 * 系统集成“快递100”数据接口，支持多种快递在线查询显示，需要向“快递100”申请id后才可以使用
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "快递设置", value = "/admin/set_kuaidi.htm*", rtype = "admin", rname = "快递设置", rcode = "admin_set_kuaidi", rgroup = "设置")
	@RequestMapping("/admin/set_kuaidi.htm")
	public ModelAndView set_kuaidi(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_kuaidi.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param kuaidi_id
	 * @return
	 */
	@SecurityMapping(title = "保存快递设置", value = "/admin/set_kuaidi_save.htm*", rtype = "admin", rname = "快递设置", rcode = "admin_set_kuaidi", rgroup = "设置")
	@RequestMapping("/admin/set_kuaidi_save.htm")
	public ModelAndView set_kuaidi_save(HttpServletRequest request,
			HttpServletResponse response, String id, String kuaidi_id) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig config = null;
		if (id.equals("")) {
			config = wf.toPo(request, SysConfig.class);
			config.setAddTime(new Date());
		} else {
			config = (SysConfig) wf.toPo(request, obj);
		}
		config.setKuaidi_id(kuaidi_id);
		if (id.equals("")) {
			this.configService.save(config);
		} else {
			this.configService.update(config);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "快递设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/set_kuaidi.htm");
		return mv;
	}

	/**
	 * ExpressCompany列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "快递公司列表", value = "/admin/express_company_list.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_list.htm")
	public ModelAndView express_company_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		ExpressCompanyQueryObject qo = new ExpressCompanyQueryObject(
				currentPage, mv, "company_sequence", "asc");
		IPageList pList = this.expresscompanyService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/admin/expresscompany_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "快递公司添加", value = "/admin/express_company_add.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_add.htm")
	public ModelAndView express_company_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * expresscompany编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "快递公司编辑", value = "/admin/express_company_edit.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_edit.htm")
	public ModelAndView express_company_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/express_company_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ExpressCompany expresscompany = this.expresscompanyService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", expresscompany);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * expresscompany保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "快递公司保存", value = "/admin/express_company_save.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_save.htm")
	public ModelAndView express_company_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		WebForm wf = new WebForm();
		ExpressCompany expresscompany = null;
		if (id.equals("")) {
			expresscompany = wf.toPo(request, ExpressCompany.class);
			expresscompany.setAddTime(new Date());
		} else {
			ExpressCompany obj = this.expresscompanyService.getObjById(Long
					.parseLong(id));
			expresscompany = (ExpressCompany) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.expresscompanyService.save(expresscompany);
		} else
			this.expresscompanyService.update(expresscompany);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/express_company_list.htm");
		mv.addObject("op_title", "保存快递公司成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/express_company_add.htm?currentPage=" + currentPage);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "快递公司删除", value = "/admin/express_company_del.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_del.htm")
	public String express_company_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ExpressCompany ec = this.expresscompanyService.getObjById(Long
						.parseLong(id));
				this.expresscompanyService.delete(Long.parseLong(id));
			}
		}
		return "redirect:express_company_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "快递公司Ajax更新数据", value = "/admin/express_company_ajax.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping("/admin/express_company_ajax.htm")
	public void express_company_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		ExpressCompany obj = this.expresscompanyService.getObjById(Long
				.parseLong(id));
		Field[] fields = ExpressCompany.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.expresscompanyService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("/admin/express_company_mark.htm")
	public void express_company_mark(HttpServletRequest request,
			HttpServletResponse response, String company_mark, String id) {
		Map params = new HashMap();
		params.put("company_mark", company_mark.trim());
		params.put("id", CommUtil.null2Long(id));
		List<ExpressCompany> ecs = this.expresscompanyService
				.query(
						"select obj from ExpressCompany obj where obj.company_mark=:company_mark and obj.id!=:id",
						params, -1, -1);
		boolean ret = true;
		if (ecs.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}