package com.iskyshop.manage.admin.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * @info UC配置信息管理控制器
 * @since V1.3
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class UcenterManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;

	@SecurityMapping(title = "UC配置", value = "/admin/ucenter.htm*", rtype = "admin", rname = "UC整合", rcode = "admin_bbs", rgroup = "工具")
	@RequestMapping("/admin/ucenter.htm")
	public ModelAndView ucenter(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/ucenter.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);

		return mv;
	}

	@SecurityMapping(title = "UC信息保存", value = "/admin/ucenter_save.htm*", rtype = "admin", rname = "UC整合", rcode = "admin_bbs", rgroup = "工具")
	@RequestMapping("/admin/ucenter_save.htm")
	public ModelAndView ucenter_save(HttpServletRequest request,
			HttpServletResponse response, String uc_bbs, String uc_appid,
			String uc_api, String uc_key, String uc_ip, String uc_database_url,
			String uc_database_port, String uc_database_username,
			String uc_database_pws, String uc_database, String uc_table_preffix) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		config.setUc_bbs(CommUtil.null2Boolean(uc_bbs));
		config.setUc_appid(uc_appid);
		config.setUc_api(uc_api);
		config.setUc_key(uc_key);
		config.setUc_ip(uc_ip);
		config.setUc_database_url(uc_database_url);
		config.setUc_database_port(uc_database_port);
		config.setUc_database_username(uc_database_username);
		config.setUc_database_pws(uc_database_pws);
		config.setUc_database(uc_database);
		config.setUc_table_preffix(uc_table_preffix);
		this.configService.update(config);
		mv.addObject("op_title", "UC配置保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/ucenter.htm");
		return mv;
	}
}
