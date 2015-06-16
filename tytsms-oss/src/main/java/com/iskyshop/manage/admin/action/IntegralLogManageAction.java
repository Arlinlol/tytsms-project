package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.IntegralLogQueryObject;
import com.iskyshop.foundation.domain.query.UserQueryObject;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: IntegralLogManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统积分日志管理类
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
 * @author erikzhang
 * 
 * @date 2014年5月27日
 * 
 * @version 1.0
 */
@Controller
public class IntegralLogManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IIntegralLogService integrallogService;
	@Autowired
	private IUserService userService;

	/**
	 * IntegralLog列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "积分明细", value = "/admin/integrallog_list.htm*", rtype = "admin", rname = "积分明细", rcode = "user_integral", rgroup = "会员")
	@RequestMapping("/admin/integrallog_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String userName) {
		ModelAndView mv = new JModelAndView("admin/blue/integrallog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		IntegralLogQueryObject qo = new IntegralLogQueryObject(currentPage, mv,
				orderBy, orderType);
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,IntegralLog.class,mv);
		if (userName != null && !userName.equals(""))
			qo.addQuery("obj.integral_user.userName", new SysMap("userName",
					userName), "=");
		IPageList pList = this.integrallogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(
				url + "/admin/integrallog_list.htm", "", "&userName="
						+ CommUtil.null2String(userName), pList, mv);
		mv.addObject("userName", userName);
		return mv;
	}

	@SecurityMapping(title = "积分管理", value = "/admin/user_integral.htm*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping("/admin/user_integral.htm")
	public ModelAndView user_integral(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/user_integral.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isIntegral()) {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分功能，设置失败");
			mv.addObject("open_url", "admin/operation_base_set.htm");
			mv.addObject("open_op", "积分开启");
			mv.addObject("open_mark", "operation_base_op");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/welcome.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "积分动态获取", value = "/admin/verify_user_integral.htm*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping("/admin/verify_user_integral.htm")
	public void verify_user_integral(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		User user = this.userService.getObjByProperty("userName", userName);
		int ret = -1;
		if (user != null) {
			ret = user.getIntegral();
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

	@SecurityMapping(title = "积分管理保存", value = "/admin/user_integral_save.htm*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping("/admin/user_integral_save.htm")
	@Transactional
	public ModelAndView user_integral_save(HttpServletRequest request,
			HttpServletResponse response, String userName, String operate_type,
			String integral, String content) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// 更新用户积分
		User user = this.userService.getObjByProperty("userName", userName);
		if (operate_type.equals("add")) {
			user.setIntegral(user.getIntegral() + CommUtil.null2Int(integral));
		} else {
			if (user.getIntegral() > CommUtil.null2Int(integral)) {
				user.setIntegral(user.getIntegral()
						- CommUtil.null2Int(integral));
			} else {
				user.setIntegral(0);
			}
		}
		this.userService.update(user);
		// 增加积分日志
		IntegralLog log = new IntegralLog();
		log.setAddTime(new Date());
		log.setContent(content);
		if (operate_type.equals("add")) {
			log.setIntegral(CommUtil.null2Int(integral));
		} else {
			log.setIntegral(-CommUtil.null2Int(integral));
		}
		log.setOperate_user(SecurityUserHolder.getCurrentUser());
		log.setIntegral_user(user);
		log.setType("system");
		this.integrallogService.save(log);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/user_integral.htm");
		mv.addObject("op_title", "操作用户积分成功");
		return mv;
	}
	
	@SecurityMapping(title = "积分明细导出excel", value = "/admin/integrallog_excel.htm*", rtype = "admin", rname = "积分管理", rcode = "refund_log", rgroup = "会员")
	@RequestMapping("/admin/integrallog_excel.htm")
	public void user_execl(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String userName) {
        
		ModelAndView mv = new ModelAndView();
		IntegralLogQueryObject qo = new IntegralLogQueryObject(currentPage, mv,
				orderBy, orderType);
		
		if (userName != null && !userName.equals("")){
			qo.addQuery("obj.integral_user.userName", new SysMap("userName",userName), "=");
	       }
		IPageList pList = this.integrallogService.list(qo);
		if(pList.getResult() != null){
			List<IntegralLog> datas = pList.getResult();
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
		    HSSFSheet sheet = wb.createSheet("会员");
		    //创建绘图对象
		    HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		    List<HSSFClientAnchor> anchor = new ArrayList<HSSFClientAnchor>();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1,
						2 + i, (short) 1, 2 + i));
			}
			// 设置excel每列宽度
			sheet.setColumnWidth(0, 6000);
			sheet.setColumnWidth(1, 6000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 10000);
			// 创建字体样式
			HSSFFont font = wb.createFont();
			font.setFontName("Verdana");
			font.setBoldweight((short) 100);
			font.setFontHeight((short) 300);
			font.setColor(HSSFColor.BLUE.index);
			// 创建单元格样式
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 设置边框
			style.setBottomBorderColor(HSSFColor.RED.index);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			// 设置字体
			style.setFont(font);
			// 创建Excel的sheet的一行
			HSSFRow row = sheet.createRow(0);
			// 设定行的高度
			row.setHeight((short) 500);
			// 创建一个Excel的单元格
			HSSFCell cell = row.createCell(0);
			// 合并单元格(startRow，endRow，startColumn，endColumn)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			cell.setCellValue(this.configService.getSysConfig().getTitle()
					+"积分明细" );
			// 设置单元格内容格式时间
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true);// 自动换行
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			row = sheet.createRow(1);
			
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue("会员名");
			
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("操作管理员");
			
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("积分值");
			
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("操作时间");
			
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("获取途径");
			
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("描述");
			
			for(int j = 0 ; j < datas.size() ; j++){
				int i = 0;
				row = sheet.createRow(j+2);
				// 设置单元格的样式格式
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j).getIntegral_user().getUsername());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				if(datas.get(j).getOperate_user() != null){
					cell.setCellValue(datas.get(j).getOperate_user().getUsername());
				}else{
					cell.setCellValue("");
				}
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j).getIntegral());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j).getAddTime()));
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				String type = datas.get(j).getType();
				switch(type){
				  case "system":type="系统积分管理"; break;
				  case "reg":type="用户注册";break;
				  case "login":type="用户登录";break;
				  case "integral_order":type="积分兑换";break;
				  case "order":type="订单操作";break;
				  case "inviteRegister":type="邀请用户注册";break;
				  case "award":type="用户抽奖";break;
				  case "share":type="用户分享";break;
				}
				cell.setCellValue(type);
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j).getContent());
				
				
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String excel_name = sdf.format(new Date());
			try {
				String path = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + excel_name + ".xls");
				OutputStream os = response.getOutputStream();
				wb.write(os);
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}	
	}
}