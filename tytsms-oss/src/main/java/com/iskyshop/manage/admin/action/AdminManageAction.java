package com.iskyshop.manage.admin.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.SecurityManager;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Res;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.RoleGroup;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.UserQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IResService;
import com.iskyshop.foundation.service.IRoleGroupService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.buyer.action.AccountBuyerAction;
import com.iskyshop.manage.buyer.action.AddressBuyerAction;
import com.iskyshop.manage.buyer.action.BaseBuyerAction;
import com.iskyshop.manage.buyer.action.ComplaintBuyerAction;
import com.iskyshop.manage.buyer.action.ConsultBuyerAction;
import com.iskyshop.manage.buyer.action.CouponBuyerAction;
import com.iskyshop.manage.buyer.action.FavoriteBuyerAction;
import com.iskyshop.manage.buyer.action.GroupBuyerAction;
import com.iskyshop.manage.buyer.action.IntegralOrderBuyerAction;
import com.iskyshop.manage.buyer.action.MessageBuyerAction;
import com.iskyshop.manage.buyer.action.OrderBuyerAction;
import com.iskyshop.manage.buyer.action.PredepositBuyerAction;
import com.iskyshop.manage.buyer.action.PredepositCashBuyerAction;
import com.iskyshop.manage.buyer.action.ReportBuyerAction;
import com.iskyshop.manage.seller.action.ActivitySellerAction;
import com.iskyshop.manage.seller.action.AdvertSellerAction;
import com.iskyshop.manage.seller.action.AlbumSellerAction;
import com.iskyshop.manage.seller.action.BaseSellerAction;
import com.iskyshop.manage.seller.action.ComplaintSellerAction;
import com.iskyshop.manage.seller.action.ConsultSellerAction;
import com.iskyshop.manage.seller.action.CouponSellerAction;
import com.iskyshop.manage.seller.action.FreeTransportValueSellerAction;
import com.iskyshop.manage.seller.action.GoldSellerAction;
import com.iskyshop.manage.seller.action.GoodsBrandSellerAction;
import com.iskyshop.manage.seller.action.GoodsClassSellerAction;
import com.iskyshop.manage.seller.action.GoodsSellerAction;
import com.iskyshop.manage.seller.action.GoodsSpecSellerAction;
import com.iskyshop.manage.seller.action.GroupSellerAction;
import com.iskyshop.manage.seller.action.OrderSellerAction;
import com.iskyshop.manage.seller.action.PayoffLogsellerAction;
import com.iskyshop.manage.seller.action.ReturnSellerAction;
import com.iskyshop.manage.seller.action.StoreNavSellerAction;
import com.iskyshop.manage.seller.action.StoreNoticeAction;
import com.iskyshop.manage.seller.action.StoreSellerAction;
import com.iskyshop.manage.seller.action.SubAccountSellerAction;
import com.iskyshop.manage.seller.action.TaobaoSellerAction;
import com.iskyshop.manage.seller.action.TransportSellerAction;
import com.iskyshop.manage.seller.action.WaterMarkSellerAction;
import com.iskyshop.manage.seller.action.ZtcSellerAction;
import com.iskyshop.moudle.chatting.manage.action.PlatChattingManageAction;
import com.iskyshop.moudle.chatting.view.action.StoreChattingViewAction;
import com.iskyshop.view.web.action.CartViewAction;
import com.iskyshop.view.web.action.IntegralViewAction;
import com.iskyshop.view.web.action.RechargeAction;
import com.iskyshop.view.web.action.SellerApplyAction;

/**
 * 
 * <p>
 * Title: AdminManageAction.java
 * </p>
 * 
 * <p>
 * Description: 超级管理员管理控制器,用来添加、编辑管理员信息，包括给管理员分配权限，初始化系统权限等等
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
 * @date 2014-5-16
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class AdminManageAction implements ServletContextAware {
	private ServletContext servletContext;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IRoleGroupService roleGroupService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	SecurityManager securityManager;
	@Autowired
	private IResService resService;
	@Autowired
	private IAdvertService advertService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	

	@SecurityMapping(title = "管理员列表", value = "/admin/admin_list.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_list.htm")
	public ModelAndView admin_list(String currentPage, String orderBy,
			String orderType, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, uqo, User.class, mv);
		uqo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "=");
		uqo.addQuery("obj.userRole", new SysMap("userRole1",
				"ADMIN_BUYER_SELLER"), "=", "or");
		IPageList pList = this.userService.list(uqo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/admin/admin_list.htm", "",
				"", pList, mv);
		mv.addObject("userRole", "ADMIN");
		return mv;
	}

	@SecurityMapping(title = "管理员添加", value = "/admin/admin_add.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_add.htm")
	public ModelAndView admin_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("type", "ADMIN");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("op", "admin_add");
		return mv;
	}

	@SecurityMapping(title = "管理员编辑", value = "/admin/admin_edit.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_edit.htm")
	public ModelAndView admin_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String op) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("type", "ADMIN");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.sequence asc",
						params, -1, -1);
		if (id != null) {
			if (!id.equals("")) {
				User user = this.userService.getObjById(Long.parseLong(id));
				mv.addObject("obj", user);
			}
		}
		mv.addObject("rgs", rgs);
		mv.addObject("op", op);
		return mv;
	}

	@SecurityMapping(title = "管理员保存", value = "/admin/admin_save.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_save.htm")
	public ModelAndView admin_save(HttpServletRequest request,
			HttpServletResponse response, String id, String role_ids,
			String list_url, String add_url, String password,
			String new_password) {
		WebForm wf = new WebForm();
		User user = null;
		if (id.equals("")) {
			user = wf.toPo(request, User.class);
			user.setAddTime(new Date());
			if (CommUtil.null2String(password).equals("")) {
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
			} else {
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			}
		} else {
			User u = this.userService.getObjById(CommUtil.null2Long(id));
			user = (User) wf.toPo(request, u);
			if (!CommUtil.null2String(new_password).equals("")) {
				user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
			}
		}
		Boolean ret = (!id.equals(""))&&CommUtil.null2String(new_password).equals("");
		if (id.equals("")||ret) {
			user.getRoles().clear();
			if (user.getUserRole().equalsIgnoreCase("ADMIN")) {
				Map params = new HashMap();
				params.put("display", false);
				params.put("type", "ADMIN");
				params.put("type1", "BUYER");
				List<Role> roles = this.roleService
						.query("select obj from Role obj where (obj.display=:display and obj.type=:type) or obj.type=:type1",
								params, -1, -1);
				user.getRoles().addAll(roles);
			}
		}
		String[] rids = role_ids.split(",");
		Set<Role> rs = user.getRoles();
		for (String rid : rids) {
			if (!rid.equals("")) {
				Role role = this.roleService.getObjById(Long.parseLong(rid));
				user.getRoles().add(role);
				}
			}
		if (id.equals("")) {
			this.userService.save(user);
		} else {
			this.userService.update(user);
		}

		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存管理员成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	@SecurityMapping(title = "管理员删除", value = "/admin/admin_del.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_del.htm")
	public String admin_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				User user = this.userService.getObjById(Long.parseLong(id));
				if (!user.getUsername().equals("admin")) {
					this.databaseTools.execute("delete from "
							+ Globals.DEFAULT_TABLE_SUFFIX
							+ "syslog where user_id=" + id);
					this.databaseTools.execute("delete from "
							+ Globals.DEFAULT_TABLE_SUFFIX
							+ "user_role where user_id=" + id);
					Map params = new HashMap();
					params.put("uid", user.getId());// 处理广告
					List<Advert> adv_list = this.advertService
							.query("select obj from Advert obj where obj.ad_user.id=:uid",
									params, -1, -1);
					for (Advert ad : adv_list) {
						this.advertService.delete(ad.getId());
					}
					// 处理商品
					List<Goods> goods = this.goodsService.query("select obj from Goods obj where obj.user_admin.id=:uid",
							params, -1, -1);
					for (Goods obj : goods) {
						this.goodsService.delete(obj.getId());
					}
					// 处理附件
					List<Accessory> accs = this.accessoryService.query("select obj from Accessory obj where obj.user.id=:uid",
							params, -1, -1);
					for (Accessory acc : accs) {
						this.accessoryService.delete(acc.getId());
					}
					this.userService.delete(user.getId());
				}
			}
		}
		return "redirect:admin_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "管理员修改密码", value = "/admin/admin_pws.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/admin_pws.htm")
	public ModelAndView admin_pws(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_pws.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "管理员密码保存", value = "/admin/admin_pws_save.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/admin_pws_save.htm")
	public ModelAndView admin_pws_save(HttpServletRequest request,
			HttpServletResponse response, String old_password, String password) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (Md5Encrypt.md5(old_password).toLowerCase()
				.equals(user.getPassword())) {
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.update(user);
			mv.addObject("op_title", "修改密码成功");
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "原密码错误");
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/admin_pws.htm");
		return mv;
	}

	@RequestMapping("/admin/init_role.htm")
	public String init_role() {
		// TODO Auto-generated method stub
		User current_user = SecurityUserHolder.getCurrentUser();
		if (current_user != null
				&& current_user.getUserRole().indexOf("ADMIN") >= 0
				&& current_user.getUsername().equals("admin")) {
			// this.databaseTools.execute("delete from iskyshop_role_res");
			// this.databaseTools.execute("delete from iskyshop_res");
			// this.databaseTools.execute("delete from iskyshop_user_role");
			// this.databaseTools.execute("delete from iskyshop_role");
			// this.databaseTools.execute("delete from iskyshop_rolegroup");
			List<Class> clzs = new ArrayList<Class>();
			// 超级管理权限加载
			clzs.add(BaseManageAction.class);
			clzs.add(PaymentManageAction.class);
			clzs.add(TemplateManageAction.class);
			clzs.add(AreaManageAction.class);
			clzs.add(TransAreaManageAction.class);
			clzs.add(GoodsManageAction.class);
			clzs.add(GoodsClassManageAction.class);
			clzs.add(GoodsBrandManageAction.class);
			clzs.add(GoodsTypeManageAction.class);
			clzs.add(GoodsSelfManageAction.class);
			clzs.add(OrderSelfManageAction.class);
			clzs.add(TaobaoSelfManageAction.class);
			clzs.add(ImageSelfManageAction.class);
			clzs.add(AlbumSelfManageAction.class);
			clzs.add(TransportSelfManageAction.class);
			clzs.add(GoodsSpecSelfManageAction.class);
			clzs.add(GroupSelfManageAction.class);
			clzs.add(ActivitySelfManageAction.class);
			clzs.add(AdminEvaManageAction.class);
			clzs.add(SelfReturnManageAction.class);
			clzs.add(StoreManageAction.class);
			clzs.add(StoreGradeManageAction.class);
			clzs.add(UserManageAction.class);
			clzs.add(PredepositManageAction.class);
			clzs.add(PredepositLogManageAction.class);
			clzs.add(AdminManageAction.class);
			clzs.add(OrderManageAction.class);
			clzs.add(ConsultManageAction.class);
			// clzs.add(ReportManageAction.class);
			// clzs.add(ReportSubjectManageAction.class);
			// clzs.add(ReportTypeManageAction.class);
			clzs.add(EvaluateManageAction.class);
			clzs.add(ComplaintManageAction.class);
			clzs.add(ComplaintSubjectManageAction.class);
			clzs.add(ArticleManageAction.class);
			clzs.add(ArticleClassManageAction.class);
			clzs.add(PartnerManageAction.class);
			clzs.add(DocumentManageAction.class);
			clzs.add(NavigationManageAction.class);
			clzs.add(OperationManageAction.class);
			clzs.add(GoldRecordManageAction.class);
			clzs.add(IntegralLogManageAction.class);
			clzs.add(ZtcManageAction.class);
			clzs.add(CouponManageAction.class);
			clzs.add(AdvertManageAction.class);
			clzs.add(IntegralGoodsManageAction.class);
			clzs.add(GroupAreaManageAction.class);
			clzs.add(GroupClassManageAction.class);
			clzs.add(GroupManageAction.class);
			clzs.add(GroupPriceRangeManageAction.class);
			clzs.add(GoodsFloorManageAction.class);
			clzs.add(DatabaseManageAction.class);
			clzs.add(CacheManageAction.class);
			clzs.add(LuceneManageAction.class);
			clzs.add(ActivityManageAction.class);
			clzs.add(ExpressCompanyManageAction.class);
			clzs.add(TransAreaManageAction.class);
			clzs.add(UcenterManageAction.class);
			// clzs.add(SnsManageAction.class);
			clzs.add(ImageManageAction.class);
			clzs.add(MobileClientManageAction.class);
			clzs.add(PayoffLogManageAction.class);
			clzs.add(RefundManageAction.class);
			clzs.add(StatManageAction.class);
			clzs.add(PlatChattingManageAction.class);
			// 卖家权限加载
			clzs.add(GoodsSellerAction.class);
			clzs.add(GoodsSpecSellerAction.class);
			clzs.add(BaseSellerAction.class);
			clzs.add(ComplaintSellerAction.class);
			clzs.add(ConsultSellerAction.class);
			clzs.add(CouponSellerAction.class);
			clzs.add(GoldSellerAction.class);
			clzs.add(GoodsBrandSellerAction.class);
			clzs.add(GoodsClassSellerAction.class);
			clzs.add(GroupSellerAction.class);
			clzs.add(OrderSellerAction.class);
			clzs.add(PayoffLogsellerAction.class);
			clzs.add(ReturnSellerAction.class);
			clzs.add(StoreNavSellerAction.class);
			clzs.add(StoreNoticeAction.class);
			clzs.add(StoreSellerAction.class);
			clzs.add(SubAccountSellerAction.class);
			clzs.add(TaobaoSellerAction.class);
			clzs.add(TransportSellerAction.class);
			clzs.add(WaterMarkSellerAction.class);
			clzs.add(ZtcSellerAction.class);
			clzs.add(ActivitySellerAction.class);
			clzs.add(AdvertSellerAction.class);
			clzs.add(AlbumSellerAction.class);
			clzs.add(StoreChattingViewAction.class);
			clzs.add(FreeTransportValueSellerAction.class);
			// 买家权限加载
			clzs.add(AccountBuyerAction.class);
			clzs.add(AddressBuyerAction.class);
			clzs.add(BaseBuyerAction.class);
			clzs.add(ComplaintBuyerAction.class);
			clzs.add(ConsultBuyerAction.class);
			clzs.add(CouponBuyerAction.class);
			clzs.add(FavoriteBuyerAction.class);
			clzs.add(GroupBuyerAction.class);
			clzs.add(IntegralOrderBuyerAction.class);
			clzs.add(MessageBuyerAction.class);
			clzs.add(OrderBuyerAction.class);
			clzs.add(PredepositBuyerAction.class);
			clzs.add(PredepositCashBuyerAction.class);
			clzs.add(ReportBuyerAction.class);
			clzs.add(IntegralViewAction.class);
			clzs.add(SellerApplyAction.class);
			clzs.add(RechargeAction.class);
			// 购物权限加载
			clzs.add(CartViewAction.class);
			int sequence = 0;
			for (Class clz : clzs) {
				try {
					Method[] ms = clz.getMethods();
					for (Method m : ms) {
						Annotation[] annotation = m.getAnnotations();
						for (Annotation tag : annotation) {
							if (SecurityMapping.class.isAssignableFrom(tag
									.annotationType())) {
								String value = ((SecurityMapping) tag).value();
								Map params = new HashMap();
								params.put("value", value);
								List<Res> ress = this.resService
										.query("select obj from Res obj where obj.value=:value",
												params, -1, -1);
								if (ress.size() == 0) {
									Res res = new Res();
									res.setResName(((SecurityMapping) tag)
											.title());
									res.setValue(value);
									res.setType("URL");
									res.setAddTime(new Date());
									this.resService.save(res);
									String rname = ((SecurityMapping) tag)
											.rname();
									String roleCode = ((SecurityMapping) tag)
											.rcode();
									if (roleCode.indexOf("ROLE_") != 0) {
										roleCode = ("ROLE_" + roleCode)
												.toUpperCase();
									}
									params.clear();
									params.put("roleCode", roleCode);
									List<Role> roles = this.roleService
											.query("select obj from Role obj where obj.roleCode=:roleCode",
													params, -1, -1);
									Role role = null;
									if (roles.size() > 0) {
										role = roles.get(0);
									}
									if (role == null) {
										role = new Role();
										role.setRoleName(((SecurityMapping) tag)
												.rname());
										role.setRoleCode(roleCode.toUpperCase());
									}
									role.getReses().add(res);
									res.getRoles().add(role);
									role.setAddTime(new Date());
									role.setDisplay(((SecurityMapping) tag)
											.display());
									role.setType(((SecurityMapping) tag)
											.rtype().toUpperCase());
									// 获取权限分组
									String groupName = ((SecurityMapping) tag)
											.rgroup();
									RoleGroup rg = this.roleGroupService
											.getObjByProperty("name", groupName);
									if (rg == null) {
										rg = new RoleGroup();
										rg.setAddTime(new Date());
										rg.setName(groupName);
										rg.setSequence(sequence);
										rg.setType(role.getType());
										this.roleGroupService.save(rg);
									}
									role.setRg(rg);
									this.roleService.save(role);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				sequence++;
			}
			// 添加默认超级管理员并赋予所有权限
			User user = this.userService.getObjByProperty("userName", "admin");
			Map params = new HashMap();
			List<Role> roles = this.roleService.query(
					"select obj from Role obj order by obj.addTime desc", null,
					-1, -1);
			if (user == null) {
				user = new User();
				user.setUserName("admin");
				user.setUserRole("ADMIN");
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				for (Role role : roles) {
					if (!role.getType().equalsIgnoreCase("SELLER")) {
						user.getRoles().add(role);
					}
				}
				this.userService.save(user);
			} else {
				for (Role role : roles) {
					if (!role.getType().equals("SELLER")) {
						System.out.println(role.getRoleName() + " "
								+ role.getType() + " " + role.getRoleCode());
						user.getRoles().add(role);
					}
				}
				this.userService.update(user);
			}
			// 给其他管理员添加系统默认的权限及买家权限
			params.clear();
			params.put("display", false);
			params.put("type", "ADMIN");
			List<Role> admin_roles = this.roleService
					.query("select obj from Role obj where obj.display=:display and obj.type=:type",
							params, -1, -1);
			params.clear();
			params.put("type", "BUYER");
			List<Role> buyer_roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			params.clear();
			params.put("userRole", "ADMIN");
			params.put("userName", "admin");
			List<User> admins = this.userService
					.query("select obj from User obj where obj.userRole=:userRole and obj.userName!=:userName",
							params, -1, -1);
			for (User admin : admins) {
				admin.getRoles().addAll(admin_roles);
				admin.getRoles().addAll(buyer_roles);
				this.userService.update(admin);
			}
			// 给所有用户添加买家权限
			params.clear();
			params.put("userRole", "BUYER");
			List<User> buyers = this.userService.query(
					"select obj from User obj where obj.userRole=:userRole",
					params, -1, -1);
			for (User buyer : buyers) {
				buyer.getRoles().addAll(buyer_roles);
				this.userService.update(buyer);
			}
			// 给所有卖家添加卖家权限
			params.clear();
			params.put("type1", "BUYER");
			params.put("type2", "SELLER");
			List<Role> seller_roles = this.roleService
					.query("select obj from Role obj where (obj.type=:type1 or obj.type=:type2)",
							params, -1, -1);
			params.clear();
			params.put("userRole0", "SELLER");
			params.put("userName", "admin");
			List<User> sellers = this.userService
					.query("select obj from User obj where obj.userRole=:userRole0 and obj.userName!=:userName ",
							params, -1, -1);
			for (User seller : sellers) {
				if (seller.getStore() != null
						&& seller.getStore().getStore_status() == 15) {// 商家店铺正常营业状态下，初始化权限时候才给该商家赋予权限
					seller.getRoles().addAll(buyer_roles);
					seller.getRoles().addAll(seller_roles);
					this.userService.update(seller);
				}
			}
			// 重新加载系统权限
			Map<String, String> urlAuthorities = this.securityManager
					.loadUrlAuthorities();
			this.servletContext.setAttribute("urlAuthorities", urlAuthorities);
			return "redirect:admin_list.htm";
		} else {
			return "redirect:login.htm";
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.servletContext = servletContext;
	}
}
