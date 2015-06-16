package com.iskyshop.manage.admin.action;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.service.IQueryService;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.CouponInfoQueryObject;
import com.iskyshop.foundation.domain.query.CouponQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 * <p>
 * Title: CouponManageAction.java
 * </p>
 * 
 * <p>
 * Description: 优惠券控制管理器，管理商城系统优惠券信息 优惠券使用方法：
 * 1、管理员添加优惠券，包括优惠券面额、使用条件（订单满多少可以可以用），优惠券数量、使用时间区间 2、优惠券只有平台管理员赠送用户才能获取
 * 3、用户购物订单金额满足优惠券使用条件后可以使用优惠券
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
public class CouponManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private ICouponInfoService couponinfoService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IQueryService queryService;

	/**
	 * 优惠券列表信息页面，分页显示优惠券列表信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return 优惠券列表信息页
	 */
	@SecurityMapping(title = "优惠券列表", value = "/admin/coupon_list.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_list.htm")
	public ModelAndView coupon_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_name, String coupon_begin_time,
			String coupon_end_time) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		CouponQueryObject qo = new CouponQueryObject(currentPage, mv, orderBy,
				orderType);
		if (!CommUtil.null2String(coupon_name).equals("")) {
			qo.addQuery("obj.coupon_name", new SysMap("coupon_name", "%"
					+ coupon_name + "%"), "like");
		}
		if (!CommUtil.null2String(coupon_begin_time).equals("")) {
			qo.addQuery(
					"obj.coupon_begin_time",
					new SysMap("coupon_begin_time", CommUtil
							.formatDate(coupon_begin_time)), ">=");
		}
		if (!CommUtil.null2String(coupon_end_time).equals("")) {
			qo.addQuery("obj.coupon_end_time", new SysMap("coupon_end_time",
					CommUtil.formatDate(coupon_end_time)), "<=");
		}
		IPageList pList = this.couponService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/coupon_list.htm", "",
				params, pList, mv);
		return mv;
	}

	/**
	 * 添加优惠券信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return 优惠券添加页面
	 */
	@SecurityMapping(title = "优惠券添加", value = "/admin/coupon_add.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_add.htm")
	public ModelAndView coupon_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 优惠券保存，保存或者更新一个优惠券信息
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "优惠券保存", value = "/admin/coupon_save.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_save.htm")
	public String coupon_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		WebForm wf = new WebForm();
		Coupon coupon = wf.toPo(request, Coupon.class);
		coupon.setAddTime(new Date());
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "coupon";
		Map map = new HashMap();
		try {
			map = CommUtil.saveFileToServer(configService,request, "coupon_img",
					saveFilePathName, null, null);
			if (map.get("fileName") != "") {
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(CommUtil.null2String(map.get("fileName")));
				coupon_acc.setExt((String) map.get("mime"));
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				coupon_acc.setPath(uploadFilePath + "/coupon");
				coupon_acc.setWidth(CommUtil.null2Int(map.get("width")));
				coupon_acc.setHeight(CommUtil.null2Int(map.get("height")));
				coupon_acc.setAddTime(new Date());
				this.accessoryService.save(coupon_acc);
				String pressImg = saveFilePathName + File.separator
						+ coupon_acc.getName();
				String targetImg = saveFilePathName + File.separator
						+ coupon_acc.getName() + "." + coupon_acc.getExt();
				if (!CommUtil.fileExist(saveFilePathName)) {
					CommUtil.createFolder(saveFilePathName);
				}
				try {
					Font font = new Font("Garamond", Font.CENTER_BASELINE, 75);
					waterMarkWithText(
							pressImg,
							targetImg,
							this.configService.getSysConfig()
									.getCurrency_code()
									+ coupon.getCoupon_amount(), "#FF7455",
							font, 24, 75, 1);
					font = new Font("宋体", Font.PLAIN, 15);
					waterMarkWithText(targetImg, targetImg,
							"满 " + coupon.getCoupon_order_amount() + " 减"
									+ coupon.getCoupon_count(), "#726960",
							font, 95, 90, 1);
				} catch (Exception e) {

				}
				coupon.setCoupon_acc(coupon_acc);
			} else {
				String pressImg = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator
						+ "resources"
						+ File.separator
						+ "style"
						+ File.separator
						+ "common"
						+ File.separator
						+ "template" + File.separator + "coupon_template.jpg";
				String targetImgPath = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator
						+ uploadFilePath
						+ File.separator
						+ "coupon" + File.separator;
				if (!CommUtil.fileExist(targetImgPath)) {
					CommUtil.createFolder(targetImgPath);
				}
				String targetImgName = UUID.randomUUID().toString() + ".jpg";
				try {
					Font font = new Font("Garamond", Font.CENTER_BASELINE, 75);
					waterMarkWithText(
							pressImg,
							targetImgPath + targetImgName,
							this.configService.getSysConfig()
									.getCurrency_code()
									+ coupon.getCoupon_amount(), "#FF7455",
							font, 24, 75, 1);
					font = new Font("宋体", Font.PLAIN, 15);
					waterMarkWithText(targetImgPath + targetImgName,
							targetImgPath + targetImgName,
							"满 " + coupon.getCoupon_order_amount() + " 减"
									+ coupon.getCoupon_count(), "#726960",
							font, 95, 90, 1);
				} catch (Exception e) {

				}
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(targetImgName);
				coupon_acc.setExt("jpg");
				coupon_acc.setPath(uploadFilePath + "/coupon");
				coupon_acc.setAddTime(new Date());
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil
						.null2Double(28.4)));
				this.accessoryService.save(coupon_acc);
				coupon.setCoupon_acc(coupon_acc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.couponService.save(coupon);
		return "redirect:coupon_success.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "优惠券保存成功", value = "/admin/coupon_success.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_success.htm")
	public ModelAndView coupon_success(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/coupon_list.htm");
		mv.addObject("op_title", "优惠券保存成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/coupon_add.htm" + "?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "优惠券发放", value = "/admin/coupon_send.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_send.htm")
	public ModelAndView coupon_send(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_send.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<StoreGrade> grades = this.storeGradeService.query(
				"select obj from StoreGrade obj order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("grades", grades);
		mv.addObject("currentPage", currentPage);
		mv.addObject("obj",
				this.couponService.getObjById(CommUtil.null2Long(id)));
		return mv;
	}

	@SecurityMapping(title = "优惠券发放保存", value = "/admin/coupon_send_save.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_send_save.htm")
	@Transactional
	public ModelAndView coupon_send_save(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String users,
			String grades, String order_amount) throws IOException {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<User> user_list = new ArrayList<User>();
		if (type.equals("all_user")) {
			Map params = new HashMap();
			params.put("userRole", "ADMIN");
			user_list = this.userService
					.query("select obj from User obj where obj.userRole!=:userRole order by obj.user_goods_fee desc",
							params, -1, -1);
		}
		if (type.equals("the_user")) {
			List<String> user_names = CommUtil.str2list(users);
			for (String user_name : user_names) {
				User user = this.userService.getObjByProperty("userName",
						user_name);
				user_list.add(user);
			}
		}
		if (type.equals("all_store")) {
			user_list = this.userService
					.query("select obj from User obj where obj.store.id is not null order by obj.addTime desc",
							null, -1, -1);
		}
		if (type.equals("the_store")) {
			Map params = new HashMap();
			Set<Long> store_ids = new TreeSet<Long>();
			for (String grade : grades.split(",")) {
				store_ids.add(Long.parseLong(grade));
			}
			params.put("store_ids", store_ids);
			user_list = this.userService
					.query("select obj from User obj where obj.grade.id in(:store_ids)",
							params, -1, -1);
		}
		if (type.equals("the_order")) {
			Map params = new HashMap();
			params.put("userRole", "ADMIN");
			List<User> list = this.userService
					.query("select obj from User obj where obj.userRole!=:userRole order by obj.user_goods_fee desc",
							params, -1, -1);
			for (int i = 0; i < list.size(); i++) {
				Long user_id = CommUtil.null2Long(list.get(i).getId());
				double order_total_amount = CommUtil.null2Double(list.get(i)
						.getUser_goods_fee());
				if (order_total_amount >= CommUtil.null2Double(order_amount)) {
					User user = this.userService.getObjById(user_id);
					user_list.add(user);
				} else {
					break;
				}
			}
		}
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		int length = coupon.getCouponinfos().size();
		for (int i = 0; i < user_list.size(); i++) {
			if (length < coupon.getCoupon_count()
					|| coupon.getCoupon_count() == 0) {
				CouponInfo info = new CouponInfo();
				info.setAddTime(new Date());
				info.setCoupon(coupon);
				info.setCoupon_sn(UUID.randomUUID().toString());
				info.setUser(user_list.get(i));
				this.couponinfoService.save(info);
				if (this.couponService.update(coupon)) {
					length++;
				}
				;
			} else
				break;
		}
		mv.addObject("op_title", "优惠券发放成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/coupon_list.htm");
		return mv;
	}

	@SecurityMapping(title = "优惠券AJAX更新", value = "/admin/coupon_ajax.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_ajax.htm")
	public void coupon_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Coupon obj = this.couponService.getObjById(Long.parseLong(id));
		Field[] fields = Coupon.class.getDeclaredFields();
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
		this.couponService.update(obj);
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

	@SecurityMapping(title = "优惠券详细信息", value = "/admin/coupon_info_list.htm*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping("/admin/coupon_info_list.htm")
	public ModelAndView coupon_info_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_id) {
		ModelAndView mv = new JModelAndView("admin/blue/coupon_info_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.coupon.id",
				new SysMap("coupon_id", CommUtil.null2Long(coupon_id)), "=");
		IPageList pList = this.couponinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
		mv.addObject("coupon_id", coupon_id);
		return mv;
	}

	private static boolean waterMarkWithText(String filePath, String outPath,
			String text, String markContentColor, Font font, int left, int top,
			float qualNum) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image theImg = imgIcon.getImage();
		int width = theImg.getWidth(null);
		int height = theImg.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		if (font == null) {
			font = new Font("宋体", Font.BOLD, 20);
			g.setFont(font);
		} else {
			g.setFont(font);
		}
		g.setColor(CommUtil.getColor(markContentColor));
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
		g.drawImage(theImg, 0, 0, null);
		FontMetrics metrics = new FontMetrics(font) {
		};
		g.drawString(text, left, top); // 添加水印的文字和设置水印文字出现的内容
		g.dispose();
		try {
			FileOutputStream out = new FileOutputStream(outPath);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
			param.setQuality(qualNum, true);
			encoder.encode(bimage, param);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}