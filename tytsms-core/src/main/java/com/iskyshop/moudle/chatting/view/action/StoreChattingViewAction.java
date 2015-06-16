package com.iskyshop.moudle.chatting.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.moudle.chatting.domain.Chatting;
import com.iskyshop.moudle.chatting.domain.ChattingConfig;
import com.iskyshop.moudle.chatting.domain.ChattingLog;
import com.iskyshop.moudle.chatting.domain.query.ChattingLogQueryObject;
import com.iskyshop.moudle.chatting.service.IChattingConfigService;
import com.iskyshop.moudle.chatting.service.IChattingLogService;
import com.iskyshop.moudle.chatting.service.IChattingService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: ChattingViewAction.java
 * </p>
 * 
 * <p>
 * Description: 系统聊天工具,作为单独聊天系统系统，可以集成其他系统
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
 * @author hezeng
 * 
 * @date 2014年5月22日
 * 
 * @version 1.0
 */
@Controller
public class StoreChattingViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsclassService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IChattingService chattingService;
	@Autowired
	private IChattingLogService chattinglogService;
	@Autowired
	private IChattingConfigService chattingconfigService;
	@Autowired
	private IAccessoryService accessoryService;

	/*
	 * 商家客服受理咨询信息请求，
	 */
	@SecurityMapping(title = "商家客服", value = "/store_chatting.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_chatting.htm")
	public ModelAndView store_chatting(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("moudle/store_chatting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean ret = true;
		User seller = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (!seller.getUserRole().equals("SELLER")) {
			ret = false;
		}
		if (seller.getStore() == null) {
			ret = false;
		} else {
			if (seller.getStore().getStore_status() != 15) {
				ret = false;
			}
		}
		if (ret) {
			HttpSession session = request.getSession(false);
			session.setAttribute("chatting_session", "chatting_session");
			ChattingConfig config = new ChattingConfig();
			Map params = new HashMap();
			params.put("store_id", seller.getStore().getId());
			params.put("chatting_display", 0);// 显示
			List<Chatting> chattings = this.chattingService
					.query("select obj from Chatting obj where obj.config.store_id=:store_id  and obj.chatting_display=:chatting_display and obj.logs.size>0 order by addTime desc",
							params, -1, -1);
			// 生成聊天组件配置信息
			params.clear();
			params.put("store_id", seller.getStore().getId());
			List<ChattingConfig> config_list = this.chattingconfigService
					.query("select obj from ChattingConfig obj where obj.store_id=:store_id ",
							params, 0, 1);
			if (config_list.size() == 0) {
				config.setAddTime(new Date());
				config.setConfig_type(0);
				config.setStore_id(seller.getStore().getId());
				config.setKf_name(seller.getStore().getStore_name() + "在线客服");
				this.chattingconfigService.save(config);
			} else {
				config = config_list.get(0);
			}
			params.clear();
			params.put("store_id", seller.getStore().getId());
			params.put("store_read", 0);
			List<ChattingLog> logs = this.chattinglogService
					.query("select obj from ChattingLog obj where obj.chatting.config.store_id=:store_id and obj.store_read=:store_read order by addTime asc",
							params, -1, -1);// 所有未读聊天记录

			mv.addObject("logs", logs);
			mv.addObject("chattingConfig", config);
			mv.addObject("chattings", chattings);// 所有联系人列表,

		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/*
	 * 商家客服受理咨询信息请求，
	 */
	@SecurityMapping(title = "商家客服", value = "/store_chatting_open.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_chatting_open.htm")
	public ModelAndView store_chatting_open(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new JModelAndView("moudle/store_chatting_open.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean ret = true;
		User seller = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (!seller.getUserRole().equals("SELLER")) {
			ret = false;
		}
		if (seller.getStore() == null) {
			ret = false;
		} else {
			if (seller.getStore().getStore_status() != 15) {
				ret = false;
			}
		}
		if (ret) {
			Chatting chatting = this.chattingService.getObjById(CommUtil
					.null2Long(chatting_id));
			Map params = new HashMap();
			params.put("chatting_id", chatting.getId());
			params.put("store_read", 0);// 商家客服未读信息
			List<ChattingLog> logs = this.chattinglogService
					.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read order by addTime asc",
							params, -1, -1);
			for (ChattingLog cl : logs) {// 查询所有未读信息，并将所有未读信息标记为已读
				cl.setStore_read(1);// 设置为商家已读
				this.chattinglogService.update(cl);
			}
			if (chatting.getGoods_id() != null) {
				Long gid = chatting.getGoods_id();
				Goods goods = this.goodsService.getObjById(gid);
				mv.addObject("goods", goods);
			}
			mv.addObject("chatting", chatting);
			mv.addObject("objs", logs);
		}
		return mv;
	}

	/*
	 * 商家客服受理咨询信息请求，
	 */
	@SecurityMapping(title = "商家客服", value = "/store_chatting_close.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_chatting_close.htm")
	public void store_chatting_close(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		boolean ret = true;
		User seller = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (!seller.getUserRole().equals("SELLER")) {
			ret = false;
		}
		if (seller.getStore() == null) {
			ret = false;
		} else {
			if (seller.getStore().getStore_status() != 15) {
				ret = false;
			}
		}
		if (ret) {
			Chatting chatting = this.chattingService.getObjById(CommUtil
					.null2Long(chatting_id));
			chatting.setChatting_display(-1);
			ret = this.chattingService.update(chatting);
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

	/**
	 * 商家客服端定时刷新请求， type;// 对话类型，0为用户和商家对话，1为用户和平台对话 mark;// 聊天内容标记，0为未读，1为已读，
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商家客服", value = "/store_chatting_refresh.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_chatting_refresh.htm")
	public ModelAndView store_chatting_refresh(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new JModelAndView("moudle/store_chatting_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			boolean ret = true;
			User seller = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (!seller.getUserRole().equals("SELLER")) {
				ret = false;
			}
			if (seller.getStore() == null) {
				ret = false;
			} else {
				if (seller.getStore().getStore_status() != 15) {
					ret = false;
				}
			}
			if (ret) {
				Chatting chatting = this.chattingService.getObjById(CommUtil
						.null2Long(chatting_id));
				if (SecurityUserHolder.getCurrentUser() != null
						&& chatting != null) {
					Map params = new HashMap();
					params.put("chatting_id", CommUtil.null2Long(chatting_id));
					params.put("store_read", 0);
					List<ChattingLog> logs = this.chattinglogService
							.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.store_read=:store_read order by addTime asc",
									params, -1, -1);
					for (ChattingLog cl : logs) {
						cl.setStore_read(1);// 设置为商家已读
						this.chattinglogService.update(cl);
					}
					mv.addObject("objs", logs);
					mv.addObject("chatting", chatting);
					HttpSession session = request.getSession(false);
					String chatting_session = CommUtil.null2String(session
							.getAttribute("chatting_session"));
					if (session != null && !session.equals("")) {
						mv.addObject("chatting_session", chatting_session);
					}
				}
			}
		}
		return mv;
	}

	/**
	 * 商家客服端定时刷新请求， type;// 对话类型，0为用户和商家对话，1为用户和平台对话 mark;// 聊天内容标记，0为未读，1为已读，
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商家客服", value = "/store_refresh_users.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_refresh_users.htm")
	public ModelAndView store_refresh_users(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new JModelAndView("moudle/chatting_users.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			boolean ret = true;
			User seller = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (!seller.getUserRole().equals("SELLER")) {
				ret = false;
			}
			if (seller.getStore() == null) {
				ret = false;
			} else {
				if (seller.getStore().getStore_status() != 15) {
					ret = false;
				}
			}
			if (ret) {
				Map params = new HashMap();
				params.put("store_id", seller.getStore().getId());
				params.put("chatting_display", 0);// 显示
				List<Chatting> chattings = this.chattingService
						.query("select obj from Chatting obj where obj.config.store_id=:store_id  and obj.chatting_display=:chatting_display and obj.logs.size>0 order by addTime desc",
								params, -1, -1);
				params.clear();
				params.put("store_id", seller.getStore().getId());
				params.put("store_read", 0);
				List<ChattingLog> logs = this.chattinglogService
						.query("select obj from ChattingLog obj where obj.chatting.config.store_id=:store_id and obj.store_read=:store_read order by addTime asc",
								params, -1, -1);// 所有未读聊天记录
				mv.addObject("logs", logs);
				mv.addObject("chattings", chattings);// 所有联系人列表,
			}
		}
		return mv;
	}

	@SecurityMapping(title = "商家客服", value = "/store_chatting_save.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_chatting_save.htm")
	public ModelAndView store_chatting_save(HttpServletRequest request,
			HttpServletResponse response, String text, String chatting_id,
			String font, String font_size, String font_colour) {
		ModelAndView mv = new JModelAndView("moudle/store_chatting_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = this.chattingService.getObjById(CommUtil
				.null2Long(chatting_id));
		boolean ret = true;
		User seller = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (!seller.getUserRole().equals("SELLER")) {
			ret = false;
		}
		if (seller.getStore() == null) {
			ret = false;
		} else {
			if (seller.getStore().getStore_status() != 15) {
				ret = false;
			}
		}
		if (ret) {
			ChattingLog log = new ChattingLog();
			log.setAddTime(new Date());
			log.setChatting(chatting);
			log.setContent(text);
			// 保存聊天记录字体信息
			log.setFont(font);
			log.setFont_size(font_size);
			log.setFont_colour(font_colour);
			// 保存商家聊天组件字体信息
			if (!font.equals(chatting.getConfig().getFont())
					&& !font.equals("")) {
				chatting.getConfig().setFont(font);
			}
			if (!font_size.equals(chatting.getConfig().getFont_size())
					&& !font_size.equals("")) {
				chatting.getConfig().setFont_size(font_size);
			}
			if (!font_colour.equals(chatting.getConfig().getFont_colour())
					&& !font_colour.equals("")) {
				chatting.getConfig().setFont_colour(font_colour);
			}
			this.chattingconfigService.update(chatting.getConfig());
			log.setStore_id(seller.getStore().getId());
			log.setStore_read(1);// 自己发布的消息设置为自己已读
			this.chattinglogService.save(log);
			List<ChattingLog> logs = new ArrayList<ChattingLog>();
			logs.add(log);
			mv.addObject("objs", logs);// 发送消息即时显示
			// 重新设置sessin
			HttpSession session = request.getSession(false);
			session.removeAttribute("chatting_session");
			session.setAttribute("chatting_session", "chatting_session");
			String chatting_session = CommUtil.null2String(session
					.getAttribute("chatting_session"));
			if (session != null && !session.equals("")) {
				mv.addObject("chatting_session", chatting_session);
			}
		}
		return mv;
	}

	/*
	 * 聊天信息设置
	 */
	@SecurityMapping(title = "商家客服", value = "/store_chatting_set.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_chatting_set.htm")
	public void store_chatting_set(HttpServletRequest request,
			HttpServletResponse response, String chattingConfig_id,
			String kf_name, String content, String reply_open) {
		boolean ret = true;
		User seller = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		ChattingConfig config = this.chattingconfigService.getObjById(CommUtil
				.null2Long(chattingConfig_id));
		if (!seller.getUserRole().equals("SELLER")) {
			ret = false;
		}
		if (config == null) {
			ret = false;
		}
		if (config.getStore_id() != seller.getStore().getId()) {
			ret = false;
		}
		if (seller.getStore() == null) {
			ret = false;
		} else {
			if (seller.getStore().getStore_status() != 15) {
				ret = false;
			}
		}
		if (ret) {
			if (kf_name != null && !kf_name.equals("")) {
				config.setKf_name(kf_name);
			}
			if (content != null && !content.equals("")) {
				config.setQuick_reply_content(content);
			}
			if (reply_open != null && !reply_open.equals("")) {
				config.setQuick_reply_open(CommUtil.null2Int(reply_open));
				if (reply_open.equals("1")
						&& config.getQuick_reply_content() == null) {
					config.setQuick_reply_content("不能及时回复，敬请原谅！");
				}
			}
			this.chattingconfigService.update(config);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(config.getQuick_reply_open());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/store_img_upload.htm")
	public void store_img_upload(HttpServletRequest request,
			HttpServletResponse response, String cid)
			throws FileUploadException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User seller = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
			String saveFilePathName = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "chatting";
			PrintWriter writer;
			Map map = new HashMap();
			Accessory photo = null;
			try {
				map = CommUtil.saveFileToServer(configService,request, "image", "", "", null);
				String reg = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png|.tbi|.TBI)$";
				String imgp = (String) map.get("fileName");
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(imgp.toLowerCase());
				if (matcher.find()) {
					map = CommUtil.saveFileToServer(configService,request, "image",
							saveFilePathName, "", null);
					if (map.get("fileName") != "") {
						photo = new Accessory();
						photo.setName((String) map.get("fileName"));
						photo.setExt("." + (String) map.get("mime"));
						photo.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						photo.setPath(uploadFilePath + "/chatting");
						photo.setWidth((Integer) map.get("width"));
						photo.setHeight((Integer) map.get("height"));
						photo.setAddTime(new Date());
						this.accessoryService.save(photo);
						String files = CommUtil.getURL(request) + "/"
								+ photo.getPath() + "/" + photo.getName();
						String img = "<img id='waiting_img' src='"
								+ files
								+ "' onclick='show_image(this)' style='max-height:50px;cursor:pointer'/>";
						Chatting chatting = this.chattingService
								.getObjById(CommUtil.null2Long(cid));
						ChattingLog log = new ChattingLog();
						log.setAddTime(new Date());
						log.setChatting(chatting);
						log.setContent(img);
						log.setStore_id(seller.getStore().getId());
						log.setStore_read(1);// 自己发布的消息设置为自己已读
						this.chattinglogService.save(log);
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
	}

	@SecurityMapping(title = "商家客服", value = "/store_show_history.htm*", rtype = "seller", rname = "商家客服", rcode = "seller_chatting", rgroup = "客户服务")
	@RequestMapping("/store_show_history.htm")
	public ModelAndView store_show_history(HttpServletRequest request,
			HttpServletResponse response, String chatting_id, String currentPage) {
		ModelAndView mv = new JModelAndView("moudle/history_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Chatting chatting = this.chattingService.getObjById(CommUtil
					.null2Long(chatting_id));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			boolean flag = false;
			if (user.getUserRole().equals("SELLER")) {
				if (chatting.getConfig().getStore_id()
						.equals(user.getStore().getId())) {
					flag = true;
				}
			}
			if (user.getUserRole().equals("BUYER")) {
				if (chatting.getUser_id().equals(user.getId())) {
					flag = true;
				}
			}
			if (user.getUserRole().equals("ADMIN")) {
				if (chatting.getConfig().getConfig_type() == 1) {
					flag = true;
				}
			}
			if (flag) {
				ChattingLogQueryObject qo = new ChattingLogQueryObject(
						currentPage, mv, "addTime", "desc");
				qo.addQuery("obj.chatting.id", new SysMap("chatting_id",
						chatting.getId()), "=");
				qo.setPageSize(20);
				IPageList pList = this.chattinglogService.list(qo);
				CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
						+ "/store_show_history.htm", "", "", pList, mv);
				mv.addObject("chatting_id", chatting.getId());
			}
		}
		return mv;
	}
}
