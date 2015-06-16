package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
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
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IReportService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.ITransportService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.iskyshop.manage.admin.tools.MsgTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.seller.Tools.TransportTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo;
import com.taiyitao.elasticsearch.IndexVoTools;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.quartz.job.GeneratorTools;

/**
 * 
 * <p>
 * Title: GoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品管理类
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
public class GoodsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsSpecPropertyService specPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IReportService reportService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IZTCGoldLogService ztcglService;
	@Autowired
	private GeneratorTools generatorTools;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

	/**
	 * Goods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品列表", value = "/admin/goods_list.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String brand_id, String gc_id, String goods_type,
			String goods_name, String store_recommend, String status,
			String store_name) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		if (store_name != null && !"".equals(store_name)) {
			qo.addQuery("obj.goods_store.store_name", new SysMap("goods_store_name",
					"%"+ store_name + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		if (brand_id != null && !brand_id.equals("")) {
			qo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id",
					CommUtil.null2Long(brand_id)), "=");
			mv.addObject("brand_id", brand_id);
		}
		if (gc_id != null && !gc_id.equals("")) {
			qo.addQuery("obj.gc.id",
					new SysMap("goods_gc_id", CommUtil.null2Long(gc_id)), "=");
			mv.addObject("gc_id", gc_id);
		}
		if (goods_type != null && !goods_type.equals("")) {
			qo.addQuery("obj.goods_type", new SysMap("goods_goods_type",
					CommUtil.null2Int(goods_type)), "=");
			mv.addObject("goods_type", goods_type);
		}
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_goods_name", "%"
					+ goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (store_recommend != null && !store_recommend.equals("")) {
			qo.addQuery(
					"obj.store_recommend",
					new SysMap("goods_store_recommend", CommUtil
							.null2Boolean(store_recommend)), "=");
			mv.addObject("store_recommend", store_recommend);
		}
		if (status != null && !status.equals("")) {
			qo.addQuery("obj.goods_status",
					new SysMap("goods_status", CommUtil.null2Int(status)), "=");
			mv.addObject("status", status);
		} else {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), ">");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	@SecurityMapping(title = "违规商品列表", value = "/admin/goods_outline.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_outline.htm")
	public ModelAndView goods_outline(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String goods_name,String gb_id,String gc_id) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_outline.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_goods_name", "%"
					+ goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (gb_id != null && !gb_id.equals("")) {
			qo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id",
					CommUtil.null2Long(gb_id)), "=");
			mv.addObject("gb_id", gb_id);
		}
		if (gc_id != null && !gc_id.equals("")) {
			qo.addQuery("obj.gc.id", new SysMap("goods_class_id",
					CommUtil.null2Long(gc_id)), "=");
			mv.addObject("gc_id", gc_id);
		}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "",
				params, pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.level=1 order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	/**
	 * goods添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品添加", value = "/admin/goods_add.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_add.htm")
	public ModelAndView goods_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * goods编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品编辑", value = "/admin/goods_edit.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_edit.htm")
	public ModelAndView goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Goods goods = this.goodsService.getObjById(Long.parseLong(id));
			mv.addObject("obj", goods);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}

	/**
	 * goods保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品保存", value = "/admin/goods_save.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_save.htm")
	public ModelAndView goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Goods goods = null;
		if (id.equals("")) {
			goods = wf.toPo(request, Goods.class);
			goods.setAddTime(new Date());
		} else {
			Goods obj = this.goodsService.getObjById(Long.parseLong(id));
			goods = (Goods) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.goodsService.save(goods);
		} else
			this.goodsService.update(goods);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存商品成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "商品删除", value = "/admin/goods_del.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_del.htm")
	public String goods_del(HttpServletRequest request, String mulitId)
			throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				List<Evaluate> evaluates = goods.getEvaluates();
				for (Evaluate e : evaluates) {
					this.evaluateService.delete(e.getId());
				}
				goods.getGoods_ugcs().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_photos().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_specs().clear();
				Map params = new HashMap();
				params.clear();// 直通车商品记录
				params.put("gid", goods.getId());
				List<ZTCGoldLog> ztcgls = this.ztcglService
						.query("select obj from ZTCGoldLog obj where obj.zgl_goods.id=:gid",
								params, -1, -1);
				for (ZTCGoldLog ztc : ztcgls) {
					this.ztcglService.delete(ztc.getId());
				}
				goods.setGoods_main_photo(null);
				this.goodsService.delete(goods.getId());
				// 删除索引
				elasticsearchUtil.indexDelete(IndexName.GOODS, IndexType.GOODS, CommUtil.null2String(id));
				// 发送站内短信提醒卖家
				if (goods.getGoods_type() == 1) {
					this.send_site_msg(request,
							"msg_toseller_goods_delete_by_admin_notify", goods
									.getGoods_store().getUser(), goods,
							"商城存在违规");
				}
			}
		}
		return "redirect:goods_list.htm";
	}

	private void send_site_msg(HttpServletRequest request, String mark,
			User user, Goods goods, String reason) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String path = TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME+File.separator;
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + mark+".vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
//			Properties p = new Properties();
//			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
//					TytsmsStringUtils.generatorFilesFolderServerPath(request)+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator);
//
//			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
//			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
//			Velocity.init(p);
//			org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
//					"UTF-8");
			
			VelocityEngine velocityEngine = new VelocityEngine();
            Properties properties = new Properties();
            properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,path);
            System.out.println(properties.get(Velocity.FILE_RESOURCE_LOADER_PATH));
            velocityEngine.init(properties);  
            org.apache.velocity.Template blank = velocityEngine.getTemplate(mark+".vm");
			
			VelocityContext context = new VelocityContext();
			context.put("reason", reason);
			context.put("user", user);
			context.put("goods", goods);
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			User fromUser = this.userService.getObjByProperty("userName",
					"admin");
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(fromUser);
			msg.setTitle(template.getTitle());
			msg.setToUser(user);
			msg.setType(0);
			this.messageService.save(msg);
			CommUtil.deleteFile(path + "temp.vm");
			writer.flush();
			writer.close();
		}
	}

	@SecurityMapping(title = "商品AJAX更新", value = "/admin/goods_ajax.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value,String goods_reason)
			throws ClassNotFoundException {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		Field[] fields = Goods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
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
		if (fieldName.equals("store_recommend")) {
			if (obj.isStore_recommend()) {
				obj.setStore_recommend_time(new Date());
			} else
				obj.setStore_recommend_time(null);
		}
		if(goods_reason != null && !"".equals(goods_reason)){
			obj.setUndercarriage_reasons(goods_reason);	
		}
		this.goodsService.update(obj);
		if (obj.getGoods_status() == 0) {
//			String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//					+ File.separator + "luence" + File.separator + "goods";
//			File file = new File(goods_lucene_path);
//			if (!file.exists()) {
//				CommUtil.createFolder(goods_lucene_path);
//			}
			// 更新lucene索引
//			IndexVo vo = new IndexVo();
//			vo.setVo_id(obj.getId());
//			vo.setVo_title(obj.getGoods_name());
//			vo.setVo_content(obj.getGoods_details());
//			vo.setVo_type("goods");
//			vo.setVo_store_price(CommUtil.null2Double(obj.getGoods_current_price()));
//			vo.setVo_add_time(obj.getAddTime().getTime());
//			vo.setVo_goods_salenum(obj.getGoods_salenum());
			elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS,
					CommUtil.null2String(obj.getId()), IndexVoTools.goodsToIndexVo(obj));
		} else {
//			String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//					+ File.separator + "luence" + File.separator + "goods";
//			File file = new File(goods_lucene_path);
//			if (!file.exists()) {
//				CommUtil.createFolder(goods_lucene_path);
//			}
			elasticsearchUtil.indexDelete(IndexName.GOODS, IndexType.GOODS, CommUtil.null2String(id));
		}
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

	@SecurityMapping(title = "商品审核", value = "/admin/goods_audit.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_audit.htm")
	public String goods_audit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String status)
			throws ClassNotFoundException {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null) {
				Goods obj = this.goodsService
						.getObjById(CommUtil.null2Long(id));
				obj.setGoods_status(obj.getPublish_goods_status());// 设置商品发布审核后状态
				goodsService.update(obj);
			}
		}
		//商品审核触发静态页面重新生成
		//generatorTools.generator(request, GeneratorType.INDEX, "运营中心商品审核触发首页静态页面生成");
		return "redirect:goods_list.htm?status=" + status;
	}
	
	@SecurityMapping(title = "填写商品违规下架原因", value = "/admin/goods_reason.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_reason.htm")
	public ModelAndView order_shipping(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/goods_reason.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		mv.addObject("obj", obj);
		return mv;
	}
}