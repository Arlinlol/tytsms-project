package com.iskyshop.manage.buyer.action;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Decoder;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.AESCUtil;
import com.iskyshop.core.tools.Base64;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.domain.query.UserIpQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.SendMsgAndEmTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.uc.api.UCClient;
import com.taiyitao.user.service.IUserIpService;

/**
 * 
 * <p>
 * Title: AccountBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:“我的账户”管理控制器
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
 * @author erikzhang、hezeng、jinxinzhe
 * 
 * @date 2014-4-28
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class AccountBuyerAction {
	@Autowired
	private IUserIpService userIpService;
	@Autowired
	private ISysConfigService configService;
	
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private SendMsgAndEmTools sendMsgAndEmTools;
	@Autowired
	private IGoodsClassService goodsClassService;
	/** 默认的头像文件扩展名 */
	private static final String DEFAULT_AVATAR_FILE_EXT = ".jpg";
	/** 解码器 */
	private static BASE64Decoder _decoder = new BASE64Decoder();
	/** 上传成功 */
	public static final String OPERATE_RESULT_CODE_SUCCESS = "200";
	/** 上传失败 */
	public static final String OPERATE_RESULT_CODE_FAIL = "400";

	@SecurityMapping(title = "个人信息导航", value = "/buyer/account_nav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_nav.htm")
	public ModelAndView account_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "个人信息", value = "/buyer/account.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account.htm")
	public ModelAndView account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		List<GoodsClass> gcList=goodsClassService.query("select obj from GoodsClass obj where obj.level=0", null, -1, -1);
		mv.addObject("areas", areas);
		mv.addObject("gcList", gcList);
		mv.addObject("SysConfig", configService.getSysConfig());
		return mv;
	}

	@SecurityMapping(title = "个人信息获取下级地区ajax", value = "/buyer/account_getAreaChilds.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_getAreaChilds.htm")
	public ModelAndView account_getAreaChilds(HttpServletRequest request,
			HttpServletResponse response, String parent_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_area_chlids.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		map.put("parent_id", CommUtil.null2Long(parent_id));
		List<Area> childs = this.areaService.query(
				"select obj from Area obj where obj.parent.id=:parent_id", map,
				-1, -1);
		if (childs.size() > 0) {
			mv.addObject("childs", childs);
		}
		return mv;
	}

	@SecurityMapping(title = "个人信息保存", value = "/buyer/account_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_save.htm")
	public ModelAndView account_save(HttpServletRequest request,
			HttpServletResponse response, String area_id, String birthday) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		User user = (User) wf.toPo(request, this.userService
				.getObjById(SecurityUserHolder.getCurrentUser().getId()));
		if (area_id != null && !area_id.equals("")) {
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id));
			user.setArea(area);
		}
		if (birthday != null && !birthday.equals("")) {
			String y[] = birthday.split("-");
			Calendar calendar = new GregorianCalendar();
			int years = calendar.get(Calendar.YEAR) - CommUtil.null2Int(y[0]);
			user.setYears(years);
		}
		this.userService.update(user);
		mv.addObject("op_title", "个人信息修改成功");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
		return mv;
	}

	@SecurityMapping(title = "密码修改", value = "/buyer/account_password.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_password.htm")
	public ModelAndView account_password(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_password.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "密码修改保存", value = "/buyer/account_password_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_password_save.htm")
	public ModelAndView account_password_save(HttpServletRequest request,
			HttpServletResponse response, String old_password,
			String new_password) throws Exception {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getPassword().equals(
				Md5Encrypt.md5(old_password).toLowerCase())) {
			user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
			boolean ret = this.userService.update(user);
			if (ret && this.configService.getSysConfig().isUc_bbs()) {
				UCClient uc = new UCClient();
				String uc_pws_ret = uc.uc_user_edit(user.getUsername(),
						CommUtil.null2String(old_password),
						CommUtil.null2String(new_password),
						CommUtil.null2String(user.getEmail()), 1, 0, 0);
				// System.out.println(uc_pws_ret);
			}
			mv.addObject("op_title", "密码修改成功");
			Map map = new HashMap();
			map.put("receiver_id", user.getId().toString());
			String json = Json.toJson(map);
			String path = TytsmsStringUtils.generatorImagesFolderServerPath(request);
			String url = CommUtil.getURL(request);
			this.sendMsgAndEmTools.sendMsg(url, path,
					"sms_tobuyer_pws_modify_notify", user.getMobile(), json);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "原始密码输入错误，修改失败");
		}
		mv.addObject("url", CommUtil.getURL(request)
				+ "/buyer/account_password.htm");
		return mv;
	}

	@SecurityMapping(title = "邮箱修改", value = "/buyer/account_email.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_email.htm")
	public ModelAndView account_email(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_email.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "邮箱修改保存", value = "/buyer/account_email_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_email_save.htm")
	public ModelAndView account_email_save(HttpServletRequest request,
			HttpServletResponse response, String password, String email) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getPassword().equals(Md5Encrypt.md5(StringUtils.isNotBlank(password)?password:"").toLowerCase())) {
			user.setEmail(email);
			this.userService.update(user);
			mv.addObject("op_title", "邮箱修改成功");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "密码输入错误，邮箱修改失败");
		}
		mv.addObject("url", CommUtil.getURL(request)
				+ "/buyer/account_email.htm");
		return mv;
	}

	@SecurityMapping(title = "图像修改", value = "/buyer/account_avatar.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_avatar.htm")
	public ModelAndView account_avatar(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_avatar.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	@Transactional
	@SecurityMapping(title = "图像上传", value = "/buyer/upload_avatar.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/upload_avatar.htm")
	public void upload_avatar(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try {
			// <1>. 判断路径是否存在,若不存在则创建路径
			String filePath = TytsmsStringUtils.generatorImagesFolderServerPath(request)+ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME
					+File.separator+ "avatar";
			File uploadDir = new File(filePath);
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}
			// <2>.自定义参数：可用于传递用户Id、用户标识之类的，以区分不同的用户
			String customParams = CommUtil.null2String(request
					.getParameter("custom_params"));
			System.out.println("custom_params = " + customParams);
			// <3>. 保存文件
			// ---文件类型
			String imageType = CommUtil.null2String(request
					.getParameter("image_type"));
			if ("".equals(imageType)) {
				imageType = DEFAULT_AVATAR_FILE_EXT;
			}
			// 大头像内容
			String bigAvatarContent = CommUtil.null2String(request
					.getParameter("big_avatar"));
			User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			String bigAvatarName = SecurityUserHolder.getCurrentUser().getId()
					+ "_big";
			// @@@保存大头像
			saveImage(filePath, imageType, bigAvatarContent, bigAvatarName);
			Accessory photo = new Accessory();
			if (user.getPhoto() != null) {
				photo = user.getPhoto();
			} else {
				photo.setAddTime(new Date());
				photo.setWidth(132);
				photo.setHeight(132);
			}
			photo.setName(bigAvatarName + imageType);
			photo.setExt(imageType);
			photo.setPath(ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME
					+ "/avatar");
			if (user.getPhoto() == null) {
				this.accessoryService.save(photo);
			} else {
				this.accessoryService.update(photo);
			}
			user.setPhoto(photo);
			this.userService.update(user);
			// ###中头像内容
			// String middleAvatarContent = CommUtil.null2String(request
			// .getParameter("middle_avatar"));
			// // ###中头像名称
			// String middleAvatarName = CommUtil.null2String(request
			// .getParameter("middle_avatar_name"));
			// // ###保存中头像
			// saveImage(filePath, imageType, middleAvatarContent,
			// middleAvatarName);
			// // $$$小头像内容
			// String littleAvatarContent = CommUtil.null2String(request
			// .getParameter("little_avatar"));
			// // $$$小头像名称
			// String littleAvatarName = CommUtil.null2String(request
			// .getParameter("little_avatar_name"));
			// // $$$保存小头像
			// saveImage(filePath, imageType, littleAvatarContent,
			// littleAvatarName);
			// <4>. 设置返回值: 200表示成功，其它表示失败
			response.setContentType("text/xml");
			// 上传成功标识
			response.getWriter().write(OPERATE_RESULT_CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("text/xml");
			// 上传失败标识
			response.getWriter().write(OPERATE_RESULT_CODE_FAIL);
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param filePath
	 *            保存路径
	 * @param imageType
	 *            文件类型(.jpg、.png、.gif)
	 * @param avatarContent
	 *            文件内容
	 * @param avatarName
	 *            文件名称(不包括扩展名)
	 * @throws IOException
	 */
	private void saveImage(String filePath, String imageType,
			String avatarContent, String avatarName) throws IOException {
		avatarContent = CommUtil.null2String(avatarContent);
		if (!"".equals(avatarContent)) {
			if ("".equals(avatarName)) {
				avatarName = UUID.randomUUID().toString()
						+ DEFAULT_AVATAR_FILE_EXT;
			} else {
				avatarName = avatarName + imageType;
			}
			byte[] data = _decoder.decodeBuffer(avatarContent);
			File f = new File(filePath + File.separator + avatarName);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
			dos.write(data);
			dos.flush();
			dos.close();
		}
	}

	@SecurityMapping(title = "手机号码修改", value = "/buyer/account_mobile.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_mobile.htm")
	public ModelAndView account_mobile(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_mobile.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}

	@SecurityMapping(title = "手机号码保存", value = "/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_mobile_save.htm")
	public ModelAndView account_mobile_save(HttpServletRequest request,
			HttpServletResponse response, String mobile_verify_code,
			String mobile) throws Exception {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(
				"mobile", mobile);
		if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
			user.setMobile(mobile);
			this.userService.update(user);
			this.mobileverifycodeService.delete(mvc.getId());
			mv.addObject("op_title", "手机绑定成功");
			// 绑定成功后发送手机短信提醒
			Map map = new HashMap();
			map.put("receiver_id", user.getId().toString());
			String json = Json.toJson(map);
			String path = TytsmsStringUtils.generatorImagesFolderServerPath(request);
			String url = CommUtil.getURL(request);
			this.sendMsgAndEmTools.sendMsg(url, path,
					"sms_tobuyer_mobilebind_notify", user.getMobile(), json);
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "验证码错误，手机绑定失败");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/account_mobile.htm");
		}
		return mv;
	}

	/**
	 * 手机短信发送
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "手机短信发送", value = "/buyer/account_mobile_sms.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_mobile_sms.htm")
	public void account_mobile_sms(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile)
			throws UnsupportedEncodingException {
		String ret = "100";
		if (type.equals("mobile_vetify_code")) {
			String code = CommUtil.randomString(4).toUpperCase();
			String content = "尊敬的"
					+ SecurityUserHolder.getCurrentUser().getUserName()
					+ "您好，您在试图修改"
					+ this.configService.getSysConfig().getWebsiteName()
					+ "用户绑定手机，手机验证码为：" + code + "。["
					+ this.configService.getSysConfig().getTitle() + "]";
			if (this.configService.getSysConfig().isSmsEnbale()) {
				boolean ret1 = this.msgTools.sendSMS(mobile, content);
				if (ret1) {
					VerifyCode mvc = this.mobileverifycodeService
							.getObjByProperty("mobile", mobile);
					if (mvc == null) {
						mvc = new VerifyCode();
					}
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					mvc.setMobile(mobile);
					this.mobileverifycodeService.update(mvc);
				} else {
					ret = "200";
				}
			} else {
				ret = "300";
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

	@SecurityMapping(title = "账号绑定", value = "/buyer/account_bind.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_bind.htm")
	public ModelAndView account_bind(HttpServletRequest request,
			HttpServletResponse response,String error) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_bind.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user", user);
		mv.addObject("error", error);
		return mv;
	}

	@SecurityMapping(title = "账号解除绑定", value = "/buyer/account_bind_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/account_bind_cancel.htm")
	public String account_bind_cancel(HttpServletRequest request,
			HttpServletResponse response, String account) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/account_bind.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2String(account).equals("qq")) {
			user.setQq_openid(null);
		}
		if (CommUtil.null2String(account).equals("sina")) {
			user.setSina_openid(null);
		}
		this.userService.update(user);
		return "redirect:account_bind.htm";
	}

	@Transactional
	@SecurityMapping(title = "上传头像", value = "/buyer/ajax_img_upload.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/ajax_img_upload.htm")
	public void ajax_img_upload(HttpServletRequest request,
			HttpServletResponse response) throws FileUploadException {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME+"/"+"member";
		String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
				+ uploadFilePath;
		File file = new File(saveFilePathName);
		if(!file.exists()){
			file.mkdirs();
		}
		Accessory old_photo = user.getPhoto();
		PrintWriter writer;
		Map map = new HashMap();
		Accessory photo = null;
		try {
			map = CommUtil.saveFileToServer(configService,request, "my_photo", saveFilePathName, "", null);
			String reg = ".+("+configService.getSysConfig().getImageSuffix()+")$";
			String imgp = (String) map.get("fileName");
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(imgp.toLowerCase());
			if (matcher.find()) {
				CommUtil.del_acc(request, old_photo);
				if (map.get("fileName") != "") {
					photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath);
					photo.setWidth((Integer) map.get("width"));
					photo.setHeight((Integer) map.get("height"));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
 					user.setPhoto(photo);
					this.userService.update(user);
					String files = CommUtil.getURL(request) + "/"
							+ photo.getPath() + "/" + photo.getName();
					response.setContentType("text/html;charset=UTF-8");
					writer = response.getWriter();
					writer.print(files);
				}
			} else {
				String files = "error";
				writer = response.getWriter();
				writer.print(files);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@SecurityMapping(title = "登录详细", value = "/buyer/buyer_login_detail.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/buyer_login_detail.htm")
	public ModelAndView login_detail(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_login_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		try{
			UserIpQueryObject qo = new UserIpQueryObject(
					currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.userId", new SysMap("user_id",
					SecurityUserHolder.getCurrentUser().getId()), "=");
			
			if (!CommUtil.null2String(beginTime).equals("")) {
				qo.addQuery("obj.addTime",
						new SysMap("beginTime", CommUtil.formatDate(beginTime)),
						">=");
				mv.addObject("beginTime", beginTime);
			}
			if (!CommUtil.null2String(endTime).equals("")) {
				String ends = endTime + " 23:59:59";
				qo.addQuery(
						"obj.addTime",
						new SysMap("endTime", CommUtil.formatDate(ends,
								"yyyy-MM-dd hh:mm:ss")), "<=");
				mv.addObject("endTime", endTime);
			}
			
			IPageList pList = this.userIpService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId()));
		}catch(Exception e){
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		
		return mv;
	}
	
	
	@SecurityMapping(title = "邀请注册", value = "/buyer/invite_register.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/invite_register.htm")
	public ModelAndView invite_register(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String beginTime, String endTime) {
		User user =  this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
		ModelAndView mv = new JModelAndView("user/default/usercenter/invite_register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		try{
			SysConfig config = configService.getSysConfig();
			mv.addObject("config",config);
			String webPath = CommUtil.getURL(request);
			String userInfo =Base64.getURLEncode(user.getId()+"#"+user.getUserName()+"#"+new Date().getTime());
			/*webPath = str+ CommUtil.generic_domain(request) + port
					+ contextPath+"/register.htm?info="+Base64.get3DESEncrypt(userInfo, Globals.DEFAULT_AESC_PASSWORD);*/
			webPath = webPath+"/register.htm?info="+userInfo;
			mv.addObject("url",webPath);
		}catch(Exception e){
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index.htm");
		}
		return mv;
	}
}
