package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.ComplaintGoods;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositCash;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.query.UserQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IComplaintGoodsService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsSpecificationService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositCashService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

@Controller
public class UserManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAdvertService advertService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IUserGoodsClassService ugcService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IPayoffLogService paylogService;
	@Autowired
	private IGoodsSpecPropertyService specpropertyService;
	@Autowired
	private IGoodsSpecificationService specService;
	@Autowired
	private IGoldLogService goldlogService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IComplaintGoodsService complaintGoodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoldRecordService grService;
	@Autowired
	private IStorePointService storepointService;
	@Autowired
	private IGoldLogService glService;
	@Autowired
	private IPredepositCashService redepositcashService;
	@Autowired
	private StoreTools storeTools;

	@SecurityMapping(title = "会员添加", value = "/admin/user_add.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping("/admin/user_add.htm")
	public ModelAndView user_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/user_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "会员编辑", value = "/admin/user_edit.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping("/admin/user_edit.htm")
	public ModelAndView user_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String op) {
		ModelAndView mv = new JModelAndView("admin/blue/user_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("obj", this.userService.getObjById(Long.parseLong(id)));
		mv.addObject("edit", true);
		return mv;
	}

	@SecurityMapping(title = "企业用户", value = "/admin/company_user.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping("/admin/company_user.htm")
	public ModelAndView company_user(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/company_user.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("obj", this.userService.getObjById(Long.parseLong(id)));
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	
	@SecurityMapping(title = "会员列表", value = "/admin/user_list.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping("/admin/user_list.htm")
	public ModelAndView user_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String condition, String value) {
		ModelAndView mv = new JModelAndView("admin/blue/user_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, uqo, User.class, mv);
		uqo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");
		if (condition != null) {
			if(value!=null&&!value.equals("")){
				if (condition.equals("userName")) {
					uqo.addQuery("obj.userName", new SysMap("userName", value), "=");
				}
				if (condition.equals("email")) {
					uqo.addQuery("obj.email", new SysMap("email", value), "=");
				}
				if (condition.equals("trueName")) {
					uqo.addQuery("obj.trueName", new SysMap("trueName", value), "=");
				}
				if(condition.equals("mobile")){
					uqo.addQuery("obj.mobile", new SysMap("mobile", CommUtil.null2String(value)), "=");
				}
			}	
		}
		uqo.addQuery("obj.parent.id is null", null);
		IPageList pList = this.userService.list(uqo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/admin/user_list.htm", "",
				"", pList, mv);
		mv.addObject("userRole", "USER");
		mv.addObject("storeTools", storeTools);
		mv.addObject("SysConfig", configService.getSysConfig());
		return mv;
	}

	@SecurityMapping(title = "会员保存", value = "/admin/user_save.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping("/admin/user_save.htm")
	public ModelAndView user_save(HttpServletRequest request,
			HttpServletResponse response, String id, String role_ids,
			String list_url, String add_url, String password) {
		WebForm wf = new WebForm();
		User user = null;
		if (id.equals("")) {
			user = wf.toPo(request, User.class);
			user.setAddTime(new Date());
		} else {
			User u = this.userService.getObjById(Long.parseLong(id));
			user = (User) wf.toPo(request, u);
		}
		if (password != null && !password.equals("")) {
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
		}
		if (id.equals("")) {
			user.setUserRole("BUYER");
			user.getRoles().clear();
			Map params = new HashMap();
			params.put("type", "BUYER");
			List<Role> roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			user.getRoles().addAll(roles);
			this.userService.save(user);
			// 创建用户默认相册
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(-10000);
			album.setUser(user);
			this.albumService.save(album);
		} else {
			this.userService.update(user);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存用户成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	@SecurityMapping(title = "会员删除", value = "/admin/user_del.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
	@RequestMapping("/admin/user_del.htm")
	public String user_del(HttpServletRequest request, String mulitId,
			String currentPage) throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				User parent = this.userService.getObjById(Long.parseLong(id));
				if (!parent.getUsername().equals("admin")) {
					for (User user : parent.getChilds()) {
						user.getRoles().clear();
						if (user.getStore() != null) {
							if (parent.getStore() != null) {
								this.store_del(request, user.getStore().getId());// 删除店铺
							}
							Map map = new HashMap();
							map.put("uid", user.getId().toString());
							List<OrderForm> ofs = this.orderFormService
									.query("select obj.id from OrderForm obj where obj.user_id=:uid",
											map, -1, -1);
							for (OrderForm of : ofs) {// 删除订单
								this.orderFormService.delete(of.getId());
							}
						}
						for (CouponInfo ci : parent.getCouponinfos()) {// 用户拥有的优惠券
							this.couponInfoService.delete(ci.getId());
						}
						parent.getCouponinfos().remove(parent.getCouponinfos());
						for (Accessory acc : parent.getFiles()) {// 用户附件
							if (acc.getAlbum() != null) {
								if (acc.getAlbum().getAlbum_cover() != null) {
									if (acc.getAlbum().getAlbum_cover().getId()
											.equals(acc.getId())) {
										acc.getAlbum().setAlbum_cover(null);
										this.albumService
												.update(acc.getAlbum());
									}
								}
							}
							CommUtil.del_acc(request, acc);
							this.accessoryService.delete(acc.getId());
						}
						parent.getFiles().removeAll(parent.getFiles());
						parent.getCouponinfos().remove(parent.getCouponinfos());// 用户的所有购物车
						for (GoodsCart cart : parent.getGoodscarts()) {
							this.goodsCartService.delete(cart.getId());
						}
						for (UserGoodsClass ugc : parent.getUgcs()) {
							ugc.setParent(null);
							ugc.setUser(null);
							this.ugcService.update(ugc);
							this.ugcService.delete(ugc.getId());
						}
						// 充值记录
						Map params = new HashMap();
						params.put("uid", user.getId());
						List<PredepositCash> PredepositCash_list = this.redepositcashService
								.query("select obj from PredepositCash obj where obj.cash_user.id=:uid",
										params, -1, -1);
						for (PredepositCash pc : PredepositCash_list) {
							this.redepositcashService.delete(pc.getId());
						}
						params.clear();
						params.put("uid", user.getId());
						List<GoldLog> GoldLog_list = this.goldlogService
								.query("select obj from GoldLog obj where obj.gl_user.id=:uid",
										params, -1, -1);
						for (GoldLog gl : GoldLog_list) {
							this.goldlogService.delete(gl.getId());
						}
						params.clear();
						params.put("uid", user.getId());
						List<StorePoint> storepoint_list = this.storepointService
								.query("select obj from StorePoint obj where obj.user.id=:uid",
										params, -1, -1);
						for (StorePoint sp : storepoint_list) {
							this.storepointService.delete(sp.getId());
						}
						params.clear();
						params.put("uid", user.getId());// 商家广告
						List<Advert> adv_list = this.advertService
								.query("select obj from Advert obj where obj.ad_user.id=:uid",
										params, -1, -1);
						for (Advert ad : adv_list) {
							this.advertService.delete(ad.getId());
						}
						parent.getUgcs().removeAll(parent.getUgcs());
						this.userService.delete(user.getId());
					}
					parent.getRoles().clear();
					if (parent.getStore() != null) {
						if (parent.getStore() != null) {
							this.store_del(request, parent.getStore().getId());
						}
					}
					for (Accessory acc : parent.getFiles()) {// 用户附件
						if (acc.getAlbum() != null) {
							if (acc.getAlbum().getAlbum_cover() != null) {
								if (acc.getAlbum().getAlbum_cover().getId()
										.equals(acc.getId())) {
									acc.getAlbum().setAlbum_cover(null);
									this.albumService.update(acc.getAlbum());
								}
							}
						}
						CommUtil.del_acc(request, acc);
						this.accessoryService.delete(acc.getId());
					}
					parent.getFiles().removeAll(parent.getFiles());
					for (CouponInfo ci : parent.getCouponinfos()) {// 用户拥有的优惠券
						this.couponInfoService.delete(ci.getId());
					}
					parent.getCouponinfos().remove(parent.getCouponinfos());// 用户的所有购物车
					for (GoodsCart cart : parent.getGoodscarts()) {
						this.goodsCartService.delete(cart.getId());
					}
					for (UserGoodsClass ugc : parent.getUgcs()) {
						ugc.setParent(null);
						ugc.setUser(null);
						this.ugcService.update(ugc);
						this.ugcService.delete(ugc.getId());
					}
					parent.getUgcs().removeAll(parent.getUgcs());
					parent.getGoodscarts().removeAll(parent.getGoodscarts());
					// 充值记录
					Map params = new HashMap();
					params.put("uid", parent.getId());
					List<PredepositCash> PredepositCash_list = this.redepositcashService
							.query("select obj from PredepositCash obj where obj.cash_user.id=:uid",
									params, -1, -1);
					for (PredepositCash pc : PredepositCash_list) {
						this.redepositcashService.delete(pc.getId());
					}
					params.clear();
					params.put("uid", parent.getId());
					List<GoldLog> GoldLog_list = this.goldlogService
							.query("select obj from GoldLog obj where obj.gl_user.id=:uid",
									params, -1, -1);
					for (GoldLog gl : GoldLog_list) {
						this.goldlogService.delete(gl.getId());
					}
					params.clear();
					params.put("uid", parent.getId());// 店铺统计
					List<StorePoint> storepoint_list = this.storepointService
							.query("select obj from StorePoint obj where obj.user.id=:uid",
									params, -1, -1);
					for (StorePoint sp : storepoint_list) {
						this.storepointService.delete(sp.getId());
					}
					params.clear();
					params.put("uid", parent.getId());// 商家广告
					List<Advert> adv_list = this.advertService
							.query("select obj from Advert obj where obj.ad_user.id=:uid",
									params, -1, -1);
					for (Advert ad : adv_list) {
						this.advertService.delete(ad.getId());
					}

					this.userService.delete(parent.getId());
				}
			}
		}
		return "redirect:user_list.htm?currentPage=" + currentPage;
	}

	private void store_del(HttpServletRequest request, Long id)
			throws Exception {
		if (!id.equals("")) {
			Store store = this.storeService.getObjById(id);
			if (store.getUser() != null)
				store.getUser().setStore(null);
			User user = store.getUser();
			if (user != null) {
				user.getRoles().clear();// 删除用户所有权限
				user.setUserRole("BUYER");
				// 给用户赋予买家权限
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				for (Role role : roles) {
					user.getRoles().add(role);
				}
				user.setStore_apply_step(0);
				user.getRoles().addAll(roles);
				this.userService.update(user);
				for (Goods goods : store.getGoods_list()) {// 店铺内的商品
					goods.setGoods_main_photo(null);
					goods.setGoods_brand(null);
					this.goodsService.update(goods);
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					goods.getGoods_ugcs().clear();
				}
				for (Goods goods : store.getGoods_list()) {// 删除店铺内的商品
					for (GoodsCart gc : goods.getCarts()) {
						this.goodsCartService.delete(gc.getId());
					}
					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					for (Favorite fav : goods.getFavs()) {
						this.favoriteService.delete(fav.getId());
					}
					for (ComplaintGoods cg : goods.getCgs()) {
						this.complaintGoodsService.delete(cg.getId());
					}
					goods.getCarts().removeAll(goods.getCarts());// 移除对象中的购物车
					goods.getEvaluates().removeAll(goods.getEvaluates());
					goods.getFavs().removeAll(goods.getFavs());
					goods.getCgs().removeAll(goods.getCgs());
					this.goodsService.delete(goods.getId());
				}
				store.getGoods_list().removeAll(store.getGoods_list());
				for (GoldRecord gr : user.getGold_record()) {// 用户充值记录
					this.grService.delete(gr.getId());
				}
				params.clear();
				params.put("uid", user.getId());
				List<GoldLog> gls = this.glService
						.query("select obj from GoldLog obj where obj.gl_user.id=:uid",
								params, -1, -1);
				for (GoldLog gl : gls) {
					this.glService.delete(gl.getId());
				}
				for (GoldRecord gr : user.getGold_record()) {
					this.grService.delete(gr.getId());
				}
				for (GroupLifeGoods glg : user.getGrouplifegoods()) {// 用户发布的生活购
					for (GroupInfo gi : glg.getGroupInfos()) {
						this.groupinfoService.delete(gi.getId());
					}
					glg.getGroupInfos().removeAll(glg.getGroupInfos());
					this.grouplifegoodsService.delete(CommUtil.null2Long(glg
							.getId()));
				}
				for (PayoffLog log : user.getPaylogs()) {// 商家结算日志
					this.paylogService.delete(log.getId());
				}
				for (Album album : user.getAlbums()) {// 商家相册删除
					album.setAlbum_cover(null);
					this.albumService.update(album);
					for (Accessory acc : album.getPhotos()) {
						CommUtil.del_acc(request, acc);
						this.accessoryService.delete(acc.getId());
					}
					album.getPhotos().removeAll(album.getPhotos());
					this.albumService.delete(album.getId());
				}
				for (GoodsSpecification spec : store.getSpecs()) {// 店铺规格
					for (GoodsSpecProperty pro : spec.getProperties()) {
						this.specpropertyService.delete(pro.getId());
					}
					spec.getProperties().removeAll(spec.getProperties());
				}
			}
			String path =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME
					+ File.separator + "store" + File.separator + store.getId();
			CommUtil.deleteFolder(path);
			this.storeService.delete(id);
		}
	}

	@SecurityMapping(title = "会员通知", value = "/admin/user_msg.htm*", rtype = "admin", rname = "会员通知", rcode = "user_msg", rgroup = "会员")
	@RequestMapping("/admin/user_msg.htm")
	public ModelAndView user_msg(HttpServletRequest request,
			HttpServletResponse response, String userName, String list_url) {
		ModelAndView mv = new JModelAndView("admin/blue/user_msg.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<StoreGrade> grades = this.storeGradeService.query(
				"select obj from StoreGrade obj order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("grades", grades);
		if (!"".equals(userName)) {
			mv.addObject("userName", userName);
		}
		if (!"".equals(list_url)) {
			mv.addObject("list_url", list_url);
		}
		return mv;
	}

	@SecurityMapping(title = "会员通知发送", value = "/admin/user_msg_send.htm*", rtype = "admin", rname = "会员通知", rcode = "user_msg", rgroup = "会员")
	@RequestMapping("/admin/user_msg_send.htm")
	public ModelAndView user_msg_send(HttpServletRequest request,
			HttpServletResponse response, String type, String list_url,
			String users, String grades, String content) throws IOException {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<User> user_list = new ArrayList<User>();
		if (type.equals("all_user")) {
			Map params = new HashMap();
			params.put("userRole", "ADMIN");
			user_list = this.userService
					.query("select obj from User obj where obj.userRole!=:userRole order by obj.addTime desc",
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
			Set<Long> grade_ids = new TreeSet<Long>();
			for (String grade : grades.split(",")) {
				grade_ids.add(Long.parseLong(grade));
			}
			params.put("grade_ids", grade_ids);
			user_list = this.userService
					.query("select obj from User obj where obj.store.grade.id in(:grade_ids)",
							params, -1, -1);
		}
		for (User user : user_list) {
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			msg.setToUser(user);
			this.messageService.save(msg);
		}
		mv.addObject("op_title", "会员通知发送成功");
		mv.addObject("list_url", list_url);
		return mv;
	}

	@SecurityMapping(title = "会员等级", value = "/admin/user_level.htm*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping("/admin/user_level.htm")
	public ModelAndView user_level(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/user_level.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "会员等级保存", value = "/admin/user_level_save.htm*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping("/admin/user_level_save.htm")
	public ModelAndView user_level_save(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		Map map = new HashMap();
		for (int i = 0; i <= 6; i++) {
			map.put("creditrule" + i,
					CommUtil.null2Int(request.getParameter("creditrule" + i)));
		}
		String user_creditrule = Json.toJson(map, JsonFormat.compact());
		sc.setUser_level(user_creditrule);
		if (id.equals("")) {
			this.configService.save(sc);
		} else
			this.configService.update(sc);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存会员等级成功");
		return mv;
	}
	
	@SecurityMapping(title = "用户AJAX ", value = "/admin/search_user.htm*", rtype = "admin", rname = "会员管理", rcode = "admin_search_user", rgroup = "会员")
	@RequestMapping("/admin/search_user.htm")
	public void search_user_ajax(HttpServletRequest request,
			HttpServletResponse response,
			String value) throws ClassNotFoundException {
		JSONArray jsonArray = null;
		if(value != null && !"".equals(value)){
			Map params = new HashMap();
			params.put("name", "%"+value+"%");
			List<User> users =  userService.query("select obj from User obj where obj.userName like :name", 
					params, -1, 1);
			if(!users.isEmpty()){
				jsonArray = new JSONArray();
				for(User user : users){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("userName", user.getUserName());
					jsonArray.add(jsonObject);
				}
			}
		}		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonArray);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@SecurityMapping(title = "会员导出excel", value = "/admin/user_excel.htm*", rtype = "admin", rname = "会员管理", rcode = "refund_log", rgroup = "会员")
	@RequestMapping("/admin/user_excel.htm")
	public void user_execl(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String condition, String value) {
        
		ModelAndView mv = new ModelAndView();
		UserQueryObject qo = new UserQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");
		if (condition != null) {
			if (condition.equals("userName")) {
				qo.addQuery("obj.userName", new SysMap("userName", value), "=");
			}
			if (condition.equals("email")) {
				qo.addQuery("obj.email", new SysMap("email", value), "=");
			}
			if (condition.equals("trueName")) {
				qo.addQuery("obj.trueName", new SysMap("trueName", value), "=");
			}
			if(condition.equals("mobile")){
				qo.addQuery("obj.mobile", new SysMap("mobile", value), "=");
			}
			mv.addObject("condition", condition);
			mv.addObject("value", value);
		}
		qo.addQuery("obj.parent.id is null", null);
		IPageList pList = this.userService.list(qo);
		if(pList.getResult() != null){
			List<User> datas = pList.getResult();
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
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
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
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			cell.setCellValue(this.configService.getSysConfig().getTitle()
					+"会员信息" );
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
			cell.setCellValue("注册时间");
			
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("手机号");
			
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("会员积分");
			
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("会员金币");
			
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("登录次数");
			
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("最后登录");
			
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("预存款");
			
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("开店");
			
			for(int j = 0 ; j < datas.size() ; j++){
				int i = 0;
				row = sheet.createRow(j+2);
				// 设置单元格的样式格式
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j).getUsername()+"(真实姓名:"+CommUtil.null2String(datas.get(j).getTrueName())+")");
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j).getAddTime()));
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j).getMobile());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j).getIntegral());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j).getGold());
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j).getLoginCount()));
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j).getLastLoginDate()));
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue("可用:"+CommUtil.null2Amount(datas.get(j).getAvailableBalance())+"元"
						+"\r\n冻结:"+CommUtil.null2Amount(datas.get(j).getFreezeBlance())+"元");
				
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(storeTools.query_store_with_user(datas.get(j).getId()+"")==1?"开店":"未开店");
				
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
