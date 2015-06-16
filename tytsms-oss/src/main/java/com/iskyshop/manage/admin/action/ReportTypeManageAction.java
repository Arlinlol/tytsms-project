package com.iskyshop.manage.admin.action;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.ReportType;
import com.iskyshop.foundation.domain.query.ReportTypeQueryObject;
import com.iskyshop.foundation.service.IReportTypeService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

@Controller
public class ReportTypeManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IReportTypeService reporttypeService;

	/**
	 * ReportType列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	// @SecurityMapping(title = "举报类型列表", value = "/admin/reporttype_list.htm*",
	// rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
	// @RequestMapping("/admin/reporttype_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/reporttype_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		ReportTypeQueryObject qo = new ReportTypeQueryObject(currentPage, mv,
				orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, ReportType.class, mv);
		IPageList pList = this.reporttypeService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/reporttype_list.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * reporttype添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	// @SecurityMapping(title = "举报类型增加", value = "/admin/reporttype_add.htm*",
	// rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
	// @RequestMapping("/admin/reporttype_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/reporttype_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * reporttype编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	// @SecurityMapping(title = "举报类型编辑", value = "/admin/reporttype_edit.htm*",
	// rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
	// @RequestMapping("/admin/reporttype_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/reporttype_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ReportType reporttype = this.reporttypeService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", reporttype);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * reporttype保存管理
	 * 
	 * @param id
	 * @return
	 */
	// @SecurityMapping(title = "举报类型保存", value = "/admin/reporttype_save.htm*",
	// rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
	// @RequestMapping("/admin/reporttype_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		ReportType reporttype = null;
		if (id.equals("")) {
			reporttype = wf.toPo(request, ReportType.class);
			reporttype.setAddTime(new Date());
		} else {
			ReportType obj = this.reporttypeService.getObjById(Long
					.parseLong(id));
			reporttype = (ReportType) wf.toPo(request, obj);
		}

		if (id.equals("")) {
			this.reporttypeService.save(reporttype);
		} else
			this.reporttypeService.update(reporttype);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存举报类型成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	// @SecurityMapping(title = "举报类型删除", value = "/admin/reporttype_del.htm*",
	// rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
	// @RequestMapping("/admin/reporttype_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ReportType reporttype = this.reporttypeService.getObjById(Long
						.parseLong(id));
				this.reporttypeService.delete(Long.parseLong(id));
			}
		}
		return "redirect:reporttype_list.htm?currentPage=" + currentPage;
	}
}