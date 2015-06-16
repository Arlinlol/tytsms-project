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
import org.springframework.web.util.HtmlUtils;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
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
 * Description: 系统聊天工具,作为单独聊天系统，可以集成其他系统,用户使用聊天系统超过session时长自动关闭会话窗口
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
public class UserChattingViewAction {
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
	private IOrderFormService ofService;
	@Autowired
	private IChattingService chattingService;
	@Autowired
	private IChattingLogService chattinglogService;
	@Autowired
	private IChattingConfigService chattingconfigService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private OrderFormTools orderformTools;
	@Autowired
	private IArticleService articleService;

	/**
	 * 用户聊天窗口，开启窗口同时创建会话session，会话session到期后自动关闭会话窗口
	 * 
	 * @param request
	 * @param response
	 * @param gid
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/user_chatting.htm")
	public ModelAndView user_chatting(HttpServletRequest request,
			HttpServletResponse response, String gid, String type,
			String store_id, String currentPage) {
		ModelAndView mv = new JModelAndView("moudle/user_chatting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		List<Chatting> chattings = null;
		Chatting chatting = new Chatting();
		ChattingConfig config = new ChattingConfig();
		if (user != null) {
			HttpSession session = request.getSession(false);
			session.setAttribute("chatting_session", "chatting_session");
			if (gid != null && !gid.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(gid));
				mv.addObject("goods", goods);
				mv.addObject("goodsViewTools", goodsViewTools);
				if (goods.getGoods_type() == 1) {// 和商家客服对话
					Map map = new HashMap();
					// 生成聊天组件配置信息
					map.put("store_id", goods.getGoods_store().getId());
					List<ChattingConfig> config_list = this.chattingconfigService
							.query("select obj from ChattingConfig obj where obj.store_id=:store_id ",
									map, 0, 1);
					if (config_list.size() == 0) {
						config.setAddTime(new Date());
						config.setConfig_type(0);
						config.setStore_id(goods.getGoods_store().getId());
						config.setKf_name(goods.getGoods_store()
								.getStore_name() + "在线客服");
						this.chattingconfigService.save(config);
					} else {
						config = config_list.get(0);
					}
					map.clear();
					map.put("uid", SecurityUserHolder.getCurrentUser().getId());
					map.put("store_id", goods.getGoods_store().getId());
					chattings = this.chattingService
							.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.store_id=:store_id order by addTime asc",
									map, 0, 1);
					if (chattings.size() == 0) {
						chatting.setAddTime(new Date());
						chatting.setUser_id(user.getId());
						chatting.setConfig(config);
						chatting.setUser_name(user.getUserName());
						chatting.setGoods_id(CommUtil.null2Long(gid));
						this.chattingService.save(chatting);
					} else {
						chatting = chattings.get(0);
						chatting.setGoods_id(CommUtil.null2Long(gid));
						this.chattingService.update(chatting);
					}
					mv.addObject("store", goods.getGoods_store());
					this.generic_evaluate(goods.getGoods_store(), mv);// 店铺信用信息
				} else {// 和平台客服对话
					Map map = new HashMap();
					// 生成聊天组件配置信息
					map.put("config_type", 1);// 平台
					List<ChattingConfig> config_list = this.chattingconfigService
							.query("select obj from ChattingConfig obj where obj.config_type=:config_type ",
									map, 0, 1);
					if (config_list.size() == 0) {
						config.setAddTime(new Date());
						config.setConfig_type(1);// 平台聊天
						config.setKf_name("平台在线客服");
						this.chattingconfigService.save(config);
					} else {
						config = config_list.get(0);
					}
					map.clear();
					map.put("uid", SecurityUserHolder.getCurrentUser().getId());
					map.put("config_type", 1);
					chattings = this.chattingService
							.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.config_type=:config_type order by addTime asc",
									map, 0, 1);
					if (chattings.size() == 0) {
						chatting.setAddTime(new Date());
						chatting.setUser_id(user.getId());
						chatting.setConfig(config);
						chatting.setUser_name(user.getUserName());
						chatting.setGoods_id(CommUtil.null2Long(gid));
						this.chattingService.save(chatting);
					} else {
						chatting = chattings.get(0);
						chatting.setGoods_id(CommUtil.null2Long(gid));
						this.chattingService.update(chatting);
					}
				}
			} else {// 当不传入商品id时，聊天窗口不显示商品信息，显示推荐商品
				Map map = new HashMap();
				if (type.equals("store")) {// 没有传入商品id时和商家客服对话
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(store_id));
					map.clear();
					map.put("store_id", store.getId());
					map.put("goods_status", 0);
					List<Goods> recommends = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_store.id=:store_id order by goods_salenum desc",
									map, 0, 5);
					mv.addObject("recommends", recommends);
					// 生成聊天组件配置信息
					map.clear();
					map.put("store_id", store.getId());
					List<ChattingConfig> config_list = this.chattingconfigService
							.query("select obj from ChattingConfig obj where obj.store_id=:store_id ",
									map, 0, 1);
					if (config_list.size() == 0) {
						config.setAddTime(new Date());
						config.setConfig_type(0);
						config.setStore_id(store.getId());
						config.setKf_name(store.getStore_name() + "在线客服");
						this.chattingconfigService.save(config);
					} else {
						config = config_list.get(0);
					}
					map.clear();
					map.put("uid", SecurityUserHolder.getCurrentUser().getId());
					map.put("store_id", store.getId());
					chattings = this.chattingService
							.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.store_id=:store_id order by addTime asc",
									map, 0, 1);
					if (chattings.size() == 0) {
						chatting.setAddTime(new Date());
						chatting.setUser_id(user.getId());
						chatting.setConfig(config);
						chatting.setUser_name(user.getUserName());
						this.chattingService.save(chatting);
					} else {
						chatting = chattings.get(0);
					}
					mv.addObject("store", store);
					this.generic_evaluate(store, mv);// 店铺信用信息
				} else {// 没有传入商品id时和平台客服对话
					map.clear();
					map.put("goods_type", 0);
					map.put("goods_status", 0);
					List<Goods> recommends = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=:goods_status and obj.goods_type=:goods_type order by goods_salenum desc",
									map, 0, 5);
					mv.addObject("recommends", recommends);
					// 生成聊天组件配置信息
					map.clear();
					map.put("config_type", 1);// 平台
					List<ChattingConfig> config_list = this.chattingconfigService
							.query("select obj from ChattingConfig obj where obj.config_type=:config_type ",
									map, 0, 1);
					if (config_list.size() == 0) {
						config.setAddTime(new Date());
						config.setConfig_type(1);// 平台聊天
						config.setKf_name("平台在线客服");
						this.chattingconfigService.save(config);
					} else {
						config = config_list.get(0);
					}
					map.clear();
					map.put("uid", SecurityUserHolder.getCurrentUser().getId());
					map.put("config_type", 1);
					chattings = this.chattingService
							.query("select obj from Chatting obj where obj.user_id=:uid and obj.config.config_type=:config_type order by addTime asc",
									map, 0, 1);
					if (chattings.size() == 0) {
						chatting.setAddTime(new Date());
						chatting.setUser_id(user.getId());
						chatting.setConfig(config);
						chatting.setUser_name(user.getUserName());
						this.chattingService.save(chatting);
					} else {
						chatting = chattings.get(0);
					}
				}
			}
			// 我的订单
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId()
					.toString());
			List<OrderForm> orders = this.ofService
					.query("select obj from OrderForm obj where obj.user_id=:user_id order by addTime desc",
							params, 0, 1);
			params.clear();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId()
					.toString());
			List<OrderForm> all_orders = this.ofService
					.query("select obj.id from OrderForm obj where obj.user_id=:user_id order by addTime desc",
							params, -1, -1);
			// 我的退货
			params.clear();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<ReturnGoodsLog> returnlogs = this.returngoodslogService
					.query("select obj from ReturnGoodsLog obj where obj.user_id=:user_id order by addTime desc",
							params, 0, 1);
			params.clear();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<ReturnGoodsLog> all_returnlogs = this.returngoodslogService
					.query("select obj.id from ReturnGoodsLog obj where obj.user_id=:user_id order by addTime desc",
							params, -1, -1);
			// 文章
			params.clear();
			params.put("class_mark", "chatting_article");
			params.put("display", true);
			List<Article> article = this.articleService
					.query("select obj from Article obj where obj.articleClass.parent.mark=:class_mark and obj.display=:display order by obj.addTime desc",
							params, 0, 10);
			mv.addObject("chatting", chatting);
			mv.addObject("user", user);
			mv.addObject("article", article);
			mv.addObject("returnlogs", returnlogs);
			mv.addObject("all_returnlogs", all_returnlogs.size());
			mv.addObject("orders", orders);
			mv.addObject("all_orders", all_orders.size());
			mv.addObject("orderformTools", orderformTools);
		} else {
			mv.addObject("chatting_error", true);
		}
		return mv;
	}

	/**
	 * 用户端定时刷新请求， type;// 对话类型，0为用户和商家对话，1为用户和平台对话 user_read:用户没有读过的信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/user_chatting_refresh.htm")
	public ModelAndView user_chatting_refresh(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new JModelAndView("moudle/user_chatting_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = this.chattingService.getObjById(CommUtil
				.null2Long(chatting_id));
		if (SecurityUserHolder.getCurrentUser() != null && chatting != null) {
			Map params = new HashMap();
			params.put("chatting_id", CommUtil.null2Long(chatting_id));
			params.put("user_read", 0);// user_read:用户没有读过的信息
			List<ChattingLog> logs = this.chattinglogService
					.query("select obj from ChattingLog obj where obj.chatting.id=:chatting_id and obj.user_read=:user_read order by addTime asc",
							params, -1, -1);
			for (ChattingLog log : logs) {
				log.setUser_read(1);// 标记为用户已读
				this.chattinglogService.update(log);
			}
			HttpSession session = request.getSession(false);
			String chatting_session = CommUtil.null2String(session
					.getAttribute("chatting_session"));
			if (session != null && !session.equals("")) {
				mv.addObject("chatting_session", chatting_session);
			}
			mv.addObject("objs", logs);
			mv.addObject("chatting", chatting);
		}
		return mv;
	}

	@RequestMapping("/user_chatting_save.htm")
	public ModelAndView user_chatting_save(HttpServletRequest request,
			HttpServletResponse response, String text, String chatting_id,
			String font, String font_size, String font_colour) {
		ModelAndView mv = new JModelAndView("moudle/user_chatting_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean ret = true;
		Chatting chatt = this.chattingService.getObjById(CommUtil
				.null2Long(chatting_id));
		if (chatt == null) {
			ret = false;
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user == null) {
			ret = false;
		}
		if (ret) {
			chatt.setChatting_display(0);// 显示
			this.chattingService.update(chatt);
			ChattingLog log = new ChattingLog();
			log.setAddTime(new Date());
			log.setContent(text);
			log.setFont(font);
			log.setFont_size(font_size);
			log.setFont_colour(font_colour);
			log.setChatting(chatt);
			log.setUser_id(user.getId());
			log.setUser_read(1);// 自己发布的消息设置为自己已读
			this.chattinglogService.save(log);
			// 保存用户聊天组件字体信息
			if (!font.equals(chatt.getFont()) && !font.equals("")) {
				chatt.setFont(font);
			}
			if (!font_size.equals(chatt.getFont_size())
					&& !font_size.equals("")) {
				chatt.setFont_size(font_size);
			}
			if (!font_colour.equals(chatt.getFont_colour())
					&& !font_colour.equals("")) {
				chatt.setFont_colour(font_colour);
			}
			this.chattingService.update(chatt);
			List<ChattingLog> cls = new ArrayList<ChattingLog>();
			log.setContent(HtmlUtils.htmlUnescape(log.getContent()));
			cls.add(log);
			// 客服自动回复
			if (chatt.getConfig().getQuick_reply_open() == 1) {
				ChattingLog log2 = new ChattingLog();
				log2.setAddTime(new Date());
				log2.setChatting(chatt);
				log2.setContent(chatt.getConfig().getQuick_reply_content()
						+ "[自动回复]");
				log2.setFont(chatt.getConfig().getFont());
				log2.setFont_size(chatt.getConfig().getFont_size());
				log2.setFont_colour(chatt.getConfig().getFont_colour());
				log2.setStore_id(chatt.getConfig().getStore_id());// 当与平台对话时，该值为null
				this.chattinglogService.save(log2);
			}
			mv.addObject("objs", cls);
			mv.addObject("chatting", chatt);
			// 重新设置sessin
			HttpSession session = request.getSession(false);
			session.removeAttribute("chatting_session");
			session.setAttribute("chatting_session", "chatting_session");
			String chatting_session = CommUtil.null2String(session
					.getAttribute("chatting_session"));
			if (session != null && !session.equals("")) {
				mv.addObject("chatting_session", chatting_session);
			}
		} else {
			mv.addObject("chatting_error", true);
		}
		return mv;
	}

	@RequestMapping("/user_img_upload.htm")
	public void user_img_upload(HttpServletRequest request,
			HttpServletResponse response, String cid)
			throws FileUploadException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
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
						Chatting chatt = this.chattingService
								.getObjById(CommUtil.null2Long(cid));
						ChattingLog log = new ChattingLog();
						log.setAddTime(new Date());
						log.setContent(img);
						log.setChatting(chatt);
						log.setUser_id(user.getId());
						log.setUser_read(1);// 自己发布的消息设置为自己已读
						this.chattinglogService.save(log);
						writer = response.getWriter();
						writer.print(files);
						// 客服自动回复
						if (chatt.getConfig().getQuick_reply_open() == 1) {
							ChattingLog log2 = new ChattingLog();
							log2.setAddTime(new Date());
							log2.setChatting(chatt);
							log2.setContent(chatt.getConfig()
									.getQuick_reply_content());
							log2.setFont(chatt.getConfig().getFont());
							log2.setFont_size(chatt.getConfig().getFont_size());
							log2.setFont_colour(chatt.getConfig()
									.getFont_colour());
							log2.setStore_id(chatt.getConfig().getStore_id());// 当与平台对话时，该值为null
							this.chattinglogService.save(log2);
						}
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

	// 我的订单Ajax分页
	@RequestMapping("/chatting_order_ajax.htm")
	public ModelAndView chatting_order_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("moudle/user_order_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId()
					.toString());
			String query = "select obj from OrderForm obj where obj.user_id=:user_id order by addTime desc";
			List<OrderForm> orders = this.ofService.query(query, params,
					CommUtil.null2Int(currentPage), 1);
			mv.addObject("orders", orders);
			mv.addObject("orderformTools", orderformTools);
		}
		return mv;
	}

	// 我的订单Ajax分页
	@RequestMapping("/chatting_return_ajax.htm")
	public ModelAndView chatting_return_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("moudle/user_return_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<ReturnGoodsLog> returnlogs = this.returngoodslogService
					.query("select obj from ReturnGoodsLog obj where obj.user_id=:user_id order by addTime desc",
							params, CommUtil.null2Int(currentPage), 1);
			mv.addObject("returnlogs", returnlogs);
			mv.addObject("orderformTools", orderformTools);
		}
		return mv;
	}

	@RequestMapping("/user_show_history.htm")
	public ModelAndView user_show_history(HttpServletRequest request,
			HttpServletResponse response, String chatting_id, String currentPage) {
		ModelAndView mv = new JModelAndView("moudle/user_history_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Chatting chatting = this.chattingService.getObjById(CommUtil
					.null2Long(chatting_id));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			boolean flag = false;
			if (user.getUserRole().equals("SELLER")) {
				if (!chatting.getConfig().getStore_id()
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
						+ "/user_show_history.htm", "", "", pList, mv);
				mv.addObject("chatting_id", chatting.getId());
			}
		}
		return mv;
	}

	/**
	 * 加载店铺评分信息
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		GoodsClass gc = this.goodsclassService
				.getObjById(store.getGc_main_id());
		if (store != null && gc != null && store.getPoint() != null) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());
			// 计算和同行比较结果
			description_result = CommUtil.div(store_description_evaluate
					- description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate
					- service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
					ship_evaluate);
		}
		if (description_result > 0) {
			mv.addObject("description_css", "red");
			mv.addObject("description_css1", "bg_red");
			mv.addObject("description_type", "高于");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100) > 100 ? 100
							: CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "orange");
			mv.addObject("description_css1", "bg_orange");
			mv.addObject("description_type", "持平");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "green");
			mv.addObject("description_css1", "bg_green");
			mv.addObject("description_type", "低于");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "red");
			mv.addObject("service_css1", "bg_red");
			mv.addObject("service_type", "高于");
			mv.addObject(
					"service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100) > 100 ? 100
							: CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "orange");
			mv.addObject("service_css1", "bg_orange");
			mv.addObject("service_type", "持平");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "green");
			mv.addObject("service_css1", "bg_green");
			mv.addObject("service_type", "低于");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "red");
			mv.addObject("ship_css1", "bg_red");
			mv.addObject("ship_type", "高于");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100) > 100 ? 100
							: CommUtil.mul(ship_result, 100))
							+ "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "orange");
			mv.addObject("ship_css1", "bg_orange");
			mv.addObject("ship_type", "持平");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "green");
			mv.addObject("ship_css1", "bg_green");
			mv.addObject("ship_type", "低于");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}

}
