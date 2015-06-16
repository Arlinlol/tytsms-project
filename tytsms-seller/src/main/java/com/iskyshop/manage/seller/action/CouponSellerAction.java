package com.iskyshop.manage.seller.action;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

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
import com.iskyshop.core.service.IQueryService;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.CouponInfoQueryObject;
import com.iskyshop.foundation.domain.query.CouponQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStoreService;
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
 * 
 * <p>
 * Title:CouponSeller.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心优惠劵控制器
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
 * @author jy
 * 
 * @date 2014年5月5日
 * 
 * @version 1.0
 */
@Controller
public class CouponSellerAction {
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
	@Autowired
	private IStoreService storeService;

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
	@SecurityMapping(title = "优惠券列表", value = "/seller/coupon.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon.htm")
	public ModelAndView coupon(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_name, String coupon_begin_time,
			String coupon_end_time) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_coupon.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		CouponQueryObject qo = new CouponQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()), "=");
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
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
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
	@SecurityMapping(title = "优惠券添加", value = "/seller/coupon_add.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_add.htm")
	public ModelAndView coupon_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_coupon_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 编辑优惠券信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return 优惠券添加页面
	 */
	@SecurityMapping(title = "优惠券编辑", value = "/seller/coupon_edit.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_edit.htm")
	public ModelAndView coupon_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_coupon_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", coupon);
		mv.addObject("edit", true);
		return mv;
	}

	/**
	 * 优惠券保存，保存或者更新一个优惠券信息
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "优惠券保存", value = "/seller/coupon_save.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_save.htm")
	@Transactional
	public String coupon_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		WebForm wf = new WebForm();
		Coupon coupon = null;
		if (id.equals("")) {
			coupon = wf.toPo(request, Coupon.class);
			coupon.setAddTime(new Date());
		} else {
			coupon = this.couponService.getObjById(Long.parseLong(id));
			coupon = (Coupon) wf.toPo(request, coupon);
		}
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		String saveFilePathName =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath + File.separator + "coupon";
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
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
				String targetImgName = UUID.randomUUID().toString() + ".jpg";
				String targetImg = saveFilePathName + File.separator
						+ targetImgName;
				if (!CommUtil.fileExist(saveFilePathName)) {
					CommUtil.createFolder(saveFilePathName);
				}
				try {
					Font font = new Font("宋体", Font.PLAIN, 15);
					waterMarkWithText(pressImg, targetImg,
							"满 " + coupon.getCoupon_order_amount() + " 减",
							"#726960", font, 95, 90, 1);
					font = new Font("Garamond", Font.CENTER_BASELINE, 75);
					waterMarkWithText(
							targetImg,
							targetImg,
							this.configService.getSysConfig()
									.getCurrency_code()
									+ coupon.getCoupon_amount(), "#FF7455",
							font, 24, 75, 1);
				} catch (Exception e) {

				}
				coupon_acc.setName(targetImgName);
				File file = new File(pressImg);
				file.delete();
				this.accessoryService.update(coupon_acc);
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
							"满 " + coupon.getCoupon_order_amount() + " 减",
							"#726960", font, 95, 90, 1);
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
		if (id.equals("")) {
			coupon.setCoupon_type(1);// 设置为商家发布
			coupon.setStore(store);
			this.couponService.save(coupon);
		} else {
			this.couponService.update(coupon);
		}
		return "redirect:coupon_success.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "优惠券保存成功", value = "/seller/coupon_success.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_success.htm")
	public ModelAndView coupon_success(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/coupon.htm?currentPage=" + currentPage);
		mv.addObject("op_title", "优惠券保存成功");
		return mv;
	}

	/**
	 * 删除优惠券
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return 优惠券添加页面
	 */
	@SecurityMapping(title = "优惠券删除", value = "/seller/coupon_del.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_del.htm")
	public String coupon_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		Accessory acc = coupon.getCoupon_acc();// 删除优惠券图片
		this.couponService.delete(CommUtil.null2Long(id));
		boolean ret = this.accessoryService.delete(acc.getId());
		if (ret) {
			CommUtil.del_acc(request, acc);
		}
		return "redirect:coupon.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "优惠券发放", value = "/seller/coupon_send.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_send.htm")
	public ModelAndView coupon_send(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_coupon_send.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		mv.addObject("obj",
				this.couponService.getObjById(CommUtil.null2Long(id)));
		return mv;
	}

	@SecurityMapping(title = "优惠券发放保存", value = "/seller/coupon_send_save.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_send_save.htm")
	public ModelAndView coupon_send_save(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String users,
			String order_amount, String currentPage) throws IOException {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<User> user_list = new ArrayList<User>();
		Coupon coupon = this.couponService.getObjById(CommUtil.null2Long(id));
		Store store = coupon.getStore();
		if (type.equals("all_user")) {
			Map params = new HashMap();
			params.put("userRole", "ADMIN");
			params.put("user_id", store.getUser().getId());
			user_list = this.userService
					.query("select obj from User obj where obj.userRole!=:userRole and obj.id!= :user_id order by obj.user_goods_fee desc",
							params, -1, -1);
		}
		if (type.equals("the_user")) {
			List<String> user_names = CommUtil.str2list(users);
			for (String user_name : user_names) {
				User user = this.userService.getObjByProperty("userName",
						user_name);
				if (user.getId() != CommUtil.null2Long(store.getUser().getId())) {// 排除当前用户
					user_list.add(user);
				}
			}
		}
		if (type.equals("the_order")) {
			Map params = new HashMap();
			params.put("order_status", 50);
			params.put("store_id", CommUtil.null2String(store.getId()));
			List list = this.queryService
					.query("select obj.user_id,sum(obj.totalPrice) from OrderForm obj where obj.order_status>=:order_status and obj.store_id= :store_id group by obj.user_id",
							params, -1, -1);

			for (int i = 0; i < list.size(); i++) {
				Object[] list1 = (Object[]) list.get(i);
				Long user_id = CommUtil.null2Long(list1[0]);
				double order_total_amount = CommUtil.null2Double(list1[1]);
				if (order_total_amount > CommUtil.null2Double(order_amount)) {
					User user = this.userService.getObjById(user_id);
					user_list.add(user);
				}
			}
		}

		for (int i = 0; i < user_list.size(); i++) {
			if (coupon.getCoupon_count() > 0) {
				if (i < coupon.getCoupon_count()) {
					CouponInfo info = new CouponInfo();
					info.setAddTime(new Date());
					info.setCoupon(coupon);
					info.setCoupon_sn(UUID.randomUUID().toString());
					info.setUser(user_list.get(i));
					this.couponinfoService.save(info);
				} else
					break;
			} else {
				CouponInfo info = new CouponInfo();
				info.setAddTime(new Date());
				info.setCoupon(coupon);
				info.setCoupon_sn(UUID.randomUUID().toString());
				info.setUser(user_list.get(i));
				this.couponinfoService.save(info);
			}
		}
		mv.addObject("op_title", "优惠券发放成功");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/coupon.htm?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "优惠券详细信息", value = "/seller/coupon_ajax.htm*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping("/seller/coupon_info_list.htm")
	public ModelAndView coupon_info_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/store_coupon_info_list.html",
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
		CommUtil.saveIPageList2ModelAndView(url
				+ "/seller/coupon_info_list.htm", "", params, pList, mv);
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
