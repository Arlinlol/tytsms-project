package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.RefundLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GroupInfoQueryObject;
import com.iskyshop.foundation.domain.query.RefundLogQueryObject;
import com.iskyshop.foundation.domain.query.ReturnGoodsLogQueryObject;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * 
 * <p>
 * Title: RefundManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台向买家进行退款，退款统一给买家发放预存款，买家通过预存款兑换现金
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
 * @author jinxinzhe
 * 
 * @date 2014年5月14日
 * 
 * @version 1.0
 */
@Controller
public class RefundManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRefundLogService refundLogService;

	/**
	 * refund_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品退款列表", value = "/admin/refund_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_list.htm")
	public ModelAndView refund_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String user_name,String order_id,
			String refund_status) {
		ModelAndView mv = new JModelAndView("admin/blue/refund_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLogQueryObject qo = new ReturnGoodsLogQueryObject(
				currentPage, mv, orderBy, orderType);
		List<OrderForm> orderForms = null;
		if(order_id != null && !"".equals(order_id)){
			Map params = new HashMap();
			params.put("order_id", order_id);
			orderForms = orderFormService.query("select obj from OrderForm obj where obj.order_id =:order_id", 
					params, -1, 1);
		}		
		qo.addQuery("obj.goods_return_status", new SysMap(
				"goods_return_status", "10"), "=");
		if (refund_status != null && !refund_status.equals("")) {
			qo.addQuery("obj.refund_status", new SysMap("refund_status",
					CommUtil.null2Int(refund_status)), "=");
		}
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name),
					"=");
		}
		if (name != null && !name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + name
					+ "%"), "like");
		}
		if(orderForms != null && !orderForms.isEmpty()){
			List<Long> lists = new ArrayList<Long>();
			for(OrderForm orderForm : orderForms){
				lists.add(orderForm.getId());
			}
			qo.addQuery("obj.return_order_id", new SysMap("order_id",lists), "in");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, ReturnGoodsLog.class, mv);
		IPageList pList = this.returngoodslogService.list(qo);
		List<ReturnGoodsLog> datas = pList.getResult();
		for(int i = 0 ; i < datas.size() ; i++){
			Long return_order_id = datas.get(i).getReturn_order_id();
			OrderForm orderForm = orderFormService.getObjById(return_order_id);
			if(orderForm != null){
				datas.get(i).setOrder_id(orderForm.getOrder_id());
				datas.get(i).setStore_name(orderForm.getStore_name());
			}
		}
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("user_name", user_name);
		return mv;
	}

	/**
	 * refund_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "消费码退款列表", value = "/admin/groupinfo_refund_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/groupinfo_refund_list.htm")
	public ModelAndView groupinfo_refund_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String group_sn, String user_name,String order_id,
			String refund_status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/groupinfo_refund_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv,
				orderBy, orderType);
		List<OrderForm> orderForms = null;
		if(order_id != null && !"".equals(order_id)){
			Map params = new HashMap();
			params.put("order_id", order_id);
			orderForms = orderFormService.query("select obj from OrderForm obj where obj.order_id =:order_id", 
					params, -1, 1);
		}
		if(orderForms != null && !orderForms.isEmpty()){
			List<Long> lists = new ArrayList<Long>();
			for(OrderForm orderForm : orderForms){
				lists.add(orderForm.getId());
			}
			qo.addQuery("obj.order_id", new SysMap("order_id",lists), "in");
		}
		qo.addQuery("obj.status", new SysMap("status", 5), "=");
		if (group_sn != null && !group_sn.equals("")) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", group_sn), "=");
			mv.addObject("group_sn", group_sn);
		}
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name),
					"=");
			mv.addObject("user_name", user_name);
		}
		if (refund_status != null && !refund_status.equals("")) {
			qo.addQuery("obj.status",
					new SysMap("status", CommUtil.null2Int(refund_status)), "=");
			mv.addObject("refund_status", refund_status);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GroupInfo.class, mv);
		IPageList pList = this.groupinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * refund_view查看
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "查看退款", value = "/admin/refund_view.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_view.htm")
	public ModelAndView refund_view(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/refund_predeposit_modify.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (type != null && !type.equals("")) {
			if (type.equals("groupinfo")) {// 消费码退款
				mv.addObject("type", type);
				GroupInfo gi = this.groupinfoService.getObjById(CommUtil
						.null2Long(id));
				User user = this.userService.getObjById(gi.getUser_id());
				mv.addObject("refund_money", gi.getLifeGoods().getGroup_price());
				mv.addObject("user", user);
				mv.addObject("gi", gi);
				mv.addObject(
						"msg",
						gi.getLifeGoods().getGg_name() + "消费码"
								+ gi.getGroup_sn() + "退款成功，预存款"
								+ gi.getLifeGoods().getGroup_price()
								+ "元已存入您的账户");
			}
		} else {// 商品退款
			ReturnGoodsLog obj = this.returngoodslogService.getObjById(CommUtil
					.null2Long(id));
			OrderForm of = this.orderFormService.getObjById(obj
					.getReturn_order_id());
			double temp_refund_money = 0.0;
			if (of.getCoupon_info() != null && !of.getCoupon_info().equals("")) {
				Map map = this.orderFormTools.queryCouponInfo(of
						.getCoupon_info());
				BigDecimal rate = new BigDecimal(map.get("coupon_goods_rate")
						.toString());
				BigDecimal old_price = new BigDecimal(obj.getGoods_all_price());
				double refund_money = CommUtil.mul(rate, old_price);
				temp_refund_money = refund_money;
				mv.addObject("refund_money", refund_money);
			} else {
				temp_refund_money = CommUtil.null2Double(obj
						.getGoods_all_price());
				mv.addObject("refund_money", obj.getGoods_all_price());
			}
			mv.addObject("msg", "退货服务号为" + obj.getReturn_service_id()
					+ "的商品退款成功，预存款" + temp_refund_money + "元已存入您的账户");
			mv.addObject("obj", obj);
			User user = this.userService.getObjById(obj.getUser_id());
			mv.addObject("user", user);
		}
		return mv;
	}

	/**
	 * refundlog_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "退款日志列表", value = "/admin/refundlog_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refundlog_list.htm")
	public ModelAndView refundlog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String refund_id, String user_name,
			String return_service_id, String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/refundlog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		RefundLogQueryObject qo = new RefundLogQueryObject(currentPage, mv,
				orderBy, orderType);
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.returnLog_userName", new SysMap(
					"returnLog_userName", user_name), "=");
		}
		if (refund_id != null && !refund_id.equals("")) {
			qo.addQuery("obj.refund_id", new SysMap("refund_id", refund_id),
					"=");
		}
		if (return_service_id != null && !return_service_id.equals("")) {
			qo.addQuery("obj.returnService_id", new SysMap("returnService_id",
					return_service_id), "=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, RefundLog.class, mv);
		IPageList pList = this.refundLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/refundlog_list.htm",
				"", params, pList, mv);
		mv.addObject("refund_id", refund_id);
		mv.addObject("user_name", user_name);
		mv.addObject("return_service_id", return_service_id);
		return mv;
	}
	
	
	/***
	 * 查看退款原因
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "查看退款原因", value = "/admin/refund_reason.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_reason.htm")
	public ModelAndView refund_reason(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/refund_reason.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = this.returngoodslogService.getObjById(CommUtil
				.null2Long(id));
		OrderForm of = this.orderFormService.getObjById(obj
				.getReturn_order_id());
		double temp_refund_money = 0.0;
		if (of.getCoupon_info() != null && !of.getCoupon_info().equals("")) {
			Map map = this.orderFormTools.queryCouponInfo(of
					.getCoupon_info());
			BigDecimal rate = new BigDecimal(map.get("coupon_goods_rate")
					.toString());
			BigDecimal old_price = new BigDecimal(obj.getGoods_all_price());
			double refund_money = CommUtil.mul(rate, old_price);
			temp_refund_money = refund_money;
			mv.addObject("refund_money", refund_money);
		} else {
			temp_refund_money = CommUtil.null2Double(obj
					.getGoods_all_price());
			mv.addObject("refund_money", obj.getGoods_all_price());
		}
		String img = obj.getRefund_img();
		if(img != null && !"".equals(img)){
			String[] imgs = img.split(",");
			mv.addObject("imgs", imgs);
		}
		mv.addObject("obj", obj);
		return mv;
	}
	

	@SecurityMapping(title = "退款导出excel", value = "/admin/refund_excel.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_excel.htm")
	public void refund_excel(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String user_name,String order_id,
			String refund_status) {

		ReturnGoodsLogQueryObject qo = new ReturnGoodsLogQueryObject();
		qo.setPageSize(1000000000);
		List<OrderForm> orderForms = null;
		if(order_id != null && !"".equals(order_id)){
			Map params = new HashMap();
			params.put("order_id", order_id);
			orderForms = orderFormService.query("select obj from OrderForm obj where obj.order_id =:order_id", 
					params, -1, 1);
		}		
		qo.addQuery("obj.goods_return_status", new SysMap(
				"goods_return_status", "10"), "=");
		if (refund_status != null && !refund_status.equals("")) {
			qo.addQuery("obj.refund_status", new SysMap("refund_status",
					CommUtil.null2Int(refund_status)), "=");
		}
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name),
					"=");
		}
		if (name != null && !name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + name
					+ "%"), "like");
		}
		if(orderForms != null && !orderForms.isEmpty()){
			List<Long> lists = new ArrayList<Long>();
			for(OrderForm orderForm : orderForms){
				lists.add(orderForm.getId());
			}
			qo.addQuery("obj.return_order_id", new SysMap("order_id",lists), "in");
		}
		IPageList pList = this.returngoodslogService.list(qo);
		if(pList.getResult() != null){
			List<ReturnGoodsLog> datas = pList.getResult();
			for(int i = 0 ; i < datas.size() ; i++){
				Long return_order_id = datas.get(i).getReturn_order_id();
				OrderForm orderForm = orderFormService.getObjById(return_order_id);
				if(orderForm != null){
					datas.get(i).setOrder_id(orderForm.getOrder_id());
					datas.get(i).setStore_name(orderForm.getStore_name());
				}
			}
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
		    HSSFSheet sheet = wb.createSheet("商品退款");
		    //创建绘图对象
		    HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		    List<HSSFClientAnchor> anchor = new ArrayList<HSSFClientAnchor>();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1,
						2 + i, (short) 1, 2 + i));
			}
			// 设置excel每列宽度
			sheet.setColumnWidth(0, 6000);
			sheet.setColumnWidth(1, 10000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
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
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			cell.setCellValue(this.configService.getSysConfig().getTitle()
					+"商品退货明细" );
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
			cell.setCellValue("订单号");
			
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("商品名称");
			
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("店铺名称");
			
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("退款原因");
			
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("申请者");
			
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("商品单价");
			
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("商品数量");
			
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("商品总价");
			
			for(int j = 2 ; j < datas.size()+1 ; j++){
				row = sheet.createRow(j);
				// 设置单元格的样式格式
				int i = 0;
				
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getOrder_id());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getGoods_name());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getStore_name());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getReturn_content());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getUser_name());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getGoods_price()));
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getGoods_count());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2).getGoods_all_price()));
				
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
