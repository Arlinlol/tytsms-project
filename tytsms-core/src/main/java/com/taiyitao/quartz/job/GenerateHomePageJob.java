package com.taiyitao.quartz.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nutz.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.ActFileTools;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.ArticleClass;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsFloor;
import com.iskyshop.foundation.domain.Partner;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IArticleClassService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsFloorService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.INavigationService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPartnerService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.moudle.chatting.service.IChattingLogService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.view.web.tools.ArticleViewTools;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsFloorViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.taiyitao.generator.GenerateHTMLFactory;

public class GenerateHomePageJob {

	private static Log log = LogFactory.getLog(GenerateHomePageJob.class);
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IPartnerService partnerService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private INavigationService navigationService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsFloorViewTools gf_tools;
	@Autowired
	private GoodsClassViewTools gcViewTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IChattingLogService chattinglogService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private ArticleViewTools articleTools;

	private int index_recommend_count = 5;// 首页推荐商品及推荐用户喜欢的商品个数，所有在这个页面位置的商品都以该数量作为查询基准，定义为一个参数，便于修改
	
	
	public  String webappRoot="";
	
	protected Logger _log = LoggerFactory.getLogger(getClass());
	
	
/*	private void indexHTML() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("webPath", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("navTools", navTools);
		String templateFile ="index.vm";
		String language = Strings.isEmpty(ConfigContants.I18N_LANGUAGE)?"zh_cn":ConfigContants.I18N_LANGUAGE.toLowerCase();
		String outputHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/indexOutput-new.html";//request.getSession().getServletContext().getRealPath("") + "/WEB-INF/templates/zh_cn/shop";
		String replacedHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/indexOutput.html";
		GenerateHTMLFactory.getInstance().GenerateHTML(dataMap, webappRoot, templateFile, outputHTMLPath, replacedHTMLPath);
	}*/
	
	
	/**
	 * 商城首页静态页面生成
	 */
	private void indexHTML() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("webPath", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("navTools", navTools);
		dataMap.put("gcViewTools", gcViewTools);
		dataMap.put("imageWebServer", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("CommUtil", new CommUtil());
		dataMap.put("config", configService.getSysConfig());
		dataMap.put("MIDDLE_NAME", ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME);
		
		//head
		dataMap.put("current_webPath",  ConfigContants.TYTSMS_WEB_SITE);
		
		//nav
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		dataMap.put("gcs", gcs);
		
		//recommend
		params.clear();
		params.put("store_recommend", true);
		params.put("goods_status", 0);
		List<Goods> store_recommend_goods_list = this.goodsService
				.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc",
						params, -1, -1);
		List<Goods> store_recommend_goods = new ArrayList<Goods>();
		int max = store_recommend_goods_list.size() >= this.index_recommend_count ? this.index_recommend_count
				: (store_recommend_goods_list.size() - 1);
		for (int i = 0; i < max; i++) {
			store_recommend_goods.add(store_recommend_goods_list.get(i));
		}
		int store_recommend_goods_count = (int) Math.ceil(CommUtil.div(store_recommend_goods_list.size(),
						this.index_recommend_count));
		dataMap.put("store_recommend_goods", store_recommend_goods);
		dataMap.put("store_recommend_goods_count",store_recommend_goods_count);
		
		//footer
		dataMap.put("config", configService.getSysConfig());
		
		//article
		params.clear();
		Set<String> marks = new TreeSet<String>();// 排除首页有商家4个分类及商家独享的文章信息，erikzhang
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		marks.add("shangjiaxuzhi");
		marks.add("chatting_article");
		marks.add("new_func");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
						params, 0, 8);
		dataMap.put("acs", acs);
	
		//goodsBrands
		params.clear();
		params.put("show_index", true);
		params.put("audit", 1);
		List<GoodsBrand> goodsBrands = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.show_index=:show_index and obj.audit=:audit order by obj.sequence asc",
						params, 0, 4);
		dataMap.put("goodsBrands", goodsBrands);
		
		//友情链接
		params.clear();
		List<Partner> img_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc",
						params, -1, -1);
		dataMap.put("img_partners", img_partners);
		List<Partner> text_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc",
						params, -1, -1);
		dataMap.put("text_partners", text_partners);
	
		//floor
		params.clear();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
						params, -1, -1);
		dataMap.put("floors", floors);
		dataMap.put("gf_tools", this.gf_tools);
		dataMap.put("web_url", ConfigContants.TYTSMS_WEB_SITE);
		
		//广告
		AdvertPosition ap = this.advertPositionService.getObjById(CommUtil
				.null2Long("1"));
		if (ap != null) {
			AdvertPosition obj = new AdvertPosition();
			obj.setAp_type(ap.getAp_type());
			obj.setAp_status(ap.getAp_status());
			obj.setAp_show_type(ap.getAp_show_type());
			obj.setAp_width(ap.getAp_width());
			obj.setAp_height(ap.getAp_height());
			obj.setAp_location(ap.getAp_location());
			List<Advert> advs = new ArrayList<Advert>();
			for (Advert temp_adv : ap.getAdvs()) {
				if (temp_adv.getAd_status() == 1
						&& temp_adv.getAd_begin_time().before(new Date())
						&& temp_adv.getAd_end_time().after(new Date())) {
					advs.add(temp_adv);
				}
			}
			if (advs.size() > 0) {
				if (obj.getAp_type().equals("text")) {//文字广告
					if (obj.getAp_show_type() == 0) {// 固定广告
						obj.setAp_text(advs.get(0).getAd_text());
						obj.setAp_acc_url(advs.get(0).getAd_url());
						obj.setAdv_id(CommUtil.null2String(advs.get(0)
								.getId()));
					}
					if (obj.getAp_show_type() == 1) {// 随机广告
						Random random = new Random();
						int i = random.nextInt(advs.size());
						obj.setAp_text(advs.get(i).getAd_text());
						obj.setAp_acc_url(advs.get(i).getAd_url());
						obj.setAdv_id(CommUtil.null2String(advs.get(i)
								.getId()));
					}
				}
				if (obj.getAp_type().equals("img")) {//图片广告
					if (obj.getAp_show_type() == 0) {// 固定广告
						obj.setAp_acc(advs.get(0).getAd_acc());
						obj.setAp_acc_url(advs.get(0).getAd_url());
						obj.setAdv_id(CommUtil.null2String(advs.get(0)
								.getId()));
					}
					if (obj.getAp_show_type() == 1) {// 随机广告
						Random random = new Random();
						int i = random.nextInt(advs.size());
						obj.setAp_acc(advs.get(i).getAd_acc());
						obj.setAp_acc_url(advs.get(i).getAd_url());
						obj.setAdv_id(CommUtil.null2String(advs.get(i)
								.getId()));
					}
				}
				if (obj.getAp_type().equals("slide")) {//幻灯广告
					if (obj.getAp_show_type() == 0) {// 固定广告
						obj.setAdvs(advs);
					}
					if (obj.getAp_show_type() == 1) {// 随机广告
						Random random = new Random();
						Set<Integer> list = CommUtil.randomInt(advs.size(),
								8);
						for (int i : list) {
							obj.getAdvs().add(advs.get(i));
						}
					}
				}
				if (obj.getAp_type().equals("scroll")) {//滚动广告
					if (obj.getAp_show_type() == 0) {// 固定广告
						obj.setAdvs(advs);
					}
					if (obj.getAp_show_type() == 1) {// 随机广告
						Random random = new Random();
						Set<Integer> list = CommUtil.randomInt(advs.size(),
								12);
						for (int i : list) {
							obj.getAdvs().add(advs.get(i));
						}
					}
				}
				if(obj.getAp_type().equals("bg_slide")){
					if (obj.getAp_show_type() == 0) {// 固定广告
						obj.setAdvs(advs);
					}
					if (obj.getAp_show_type() == 1) {// 随机广告
						Random random = new Random();
						Set<Integer> list = CommUtil.randomInt(advs.size(),
								4);
						for (int i : list) {
							obj.getAdvs().add(advs.get(i));
						}
					}
				}
			} else {
				obj.setAp_acc(ap.getAp_acc());
				obj.setAp_text(ap.getAp_text());
				obj.setAp_acc_url(ap.getAp_acc_url());
				Advert adv = new Advert();
				adv.setAd_url(obj.getAp_acc_url());
				adv.setAd_acc(ap.getAp_acc());
				obj.getAdvs().add(adv);
				obj.setAp_location(ap.getAp_location());
			}
			if (obj.getAp_status() == 1) {
				dataMap.put("obj", obj);
			} else {
				dataMap.put("obj", new AdvertPosition());
			}
		}
		
		String templateFile ="index.vm";
//		String language = Strings.isEmpty(ConfigContants.I18N_LANGUAGE)?"zh_cn":ConfigContants.I18N_LANGUAGE.toLowerCase();
		String outputHTMLPath = webappRoot +"index-new.html";
		String replacedHTMLPath = webappRoot +"index.html";
//		String outputHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/indexOutput-new.html";
//		String replacedHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/indexOutput.html";
		GenerateHTMLFactory.getInstance().GenerateHTML(dataMap, webappRoot, templateFile, outputHTMLPath, replacedHTMLPath);
	}
	
	
	/**
	 * 活动页面静态化
	 */
	private void activityIndexHTML() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("webPath", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("navTools", navTools);
		dataMap.put("gcViewTools", gcViewTools);
		dataMap.put("imageWebServer", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("CommUtil", new CommUtil());
		dataMap.put("config", configService.getSysConfig());
		dataMap.put("MIDDLE_NAME", ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME);
		
		
		//head
		dataMap.put("current_webPath",  ConfigContants.TYTSMS_WEB_SITE);
		
		//nav
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		dataMap.put("gcs", gcs);
		
		//活动页面逻辑
		List<Goods> mouth_travel_goods = this.goodsService.queryActionGoods();
		List<Goods> goods_list_return = new ArrayList();
		if(mouth_travel_goods !=null && mouth_travel_goods.size() > 0){
			for(int i = 0; i < ActFileTools.GOODS_IDS_LIST.size(); i++){
				Long id = Long.parseLong(ActFileTools.GOODS_IDS_LIST.get(i));
				for(Goods good : mouth_travel_goods){
					if(id.equals(good.getId())) {
						goods_list_return.add(good);
					}
				}
				if(goods_list_return.size() < i+1){
					goods_list_return.add(i, null);
				}
			}
		}
		
		User user = SecurityUserHolder.getCurrentUser();
		if(user != null){
			Coupon coupon = this.couponService.getObjById(ActFileTools.COUPON_ID);
			Map map = new HashMap();
			params.put("user_id", user.getId());
			params.put("coupon_id", coupon.getId());
			List<CouponInfo> couponList = this.couponInfoService.query(""
					+ "select obj from CouponInfo obj where obj.user.id=:user_id and obj.coupon.id=:coupon_id", map, -1, -1);
			if(couponList.size()>0){
				dataMap.put("eggCoupon", "eggCoupon");
			}
		}
		dataMap.put("mouth_travel_goods", goods_list_return);

		//footer
		dataMap.put("config", configService.getSysConfig());
		
		//article
		params.clear();
		Set<String> marks = new TreeSet<String>();// 排除首页有商家4个分类及商家独享的文章信息，erikzhang
		marks.add("gonggao");
		marks.add("guize");
		marks.add("anquan");
		marks.add("zhinan");
		marks.add("shangjiaxuzhi");
		marks.add("chatting_article");
		marks.add("new_func");
		params.put("marks", marks);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null and obj.mark not in (:marks) order by obj.sequence asc",
						params, 0, 8);
		dataMap.put("acs", acs);
		
		//友情链接
		params.clear();
		List<Partner> img_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc",
						params, -1, -1);
		dataMap.put("img_partners", img_partners);
		List<Partner> text_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc",
						params, -1, -1);
		dataMap.put("text_partners", text_partners);
	
	
		//floor
		params.clear();
		params.put("gf_display", true);
		List<GoodsFloor> floors = this.goodsFloorService
				.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
						params, -1, -1);
		dataMap.put("floors", floors);
		dataMap.put("gf_tools", this.gf_tools);
		dataMap.put("web_url", ConfigContants.TYTSMS_WEB_SITE);
				
		String templateFile ="activity_index.vm";
		String language = Strings.isEmpty(ConfigContants.I18N_LANGUAGE)?"zh_cn":ConfigContants.I18N_LANGUAGE.toLowerCase();
		String outputHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/activityIndex-new.html";
		String replacedHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/activityIndex.html";
		GenerateHTMLFactory.getInstance().GenerateHTML(dataMap, webappRoot, templateFile, outputHTMLPath, replacedHTMLPath);
	}
	
	
	/**
	 * 系统文章静态化
	 */
	private void article(String ids,String classIds){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		Map<String,Object> params = new HashMap<String, Object>();
		//文章分类
		List<ArticleClass> acs = this.articleClassService.query("select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",null, -1, -1);
		params.put("type", "user");
		//最新新闻（6条）
		List<Article> news = this.articleService.query("select obj from Article obj where obj.type=:type order by obj.addTime desc",params, 0, 6);
		//文章
		StringBuffer buf = new StringBuffer();
		buf.append("select obj from Article obj where obj.type=:type");
//		String sql = "select obj from Article obj where obj.type=:type order by obj.addTime desc";
		if(StringUtils.isNotEmpty(ids)){//
			List<Long> tem = new ArrayList<Long>();
			for(int i=0;i<ids.split(",").length;i++){
				tem.add(Long.parseLong(ids.split(",")[i]));
			}
			params.put("id", tem);
			buf.append(" and obj.id in (:id)");
//			sql = "select obj from Article obj where obj.type=:type and obj.id in (:id) order by obj.addTime desc";
		}
		
		if(StringUtils.isNotEmpty(classIds)){//文章分类
			List<Long> arclassIds = new ArrayList<Long>();
			for(int i=0;i<classIds.split(",").length;i++){
				arclassIds.add(Long.parseLong(classIds.split(",")[i]));
			}
			params.put("articleClass", arclassIds);
			buf.append(" and obj.articleClass.id in (:articleClass)");
//			sql = "select obj from Article obj where obj.type=:type and obj.id in (:id) order by obj.addTime desc";
		}
		buf.append(" order by obj.addTime desc");
		List<Article> articles = this.articleService.query(buf.toString(),params, -1, -1);
		dataMap.put("articles", news);
		dataMap.put("acs", acs);
		dataMap.put("articleTools", articleTools);
		dataMap.put("config", configService.getSysConfig());
		dataMap.put("webPath", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("current_webPath",  ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("imageWebServer", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("MIDDLE_NAME", ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME);
		dataMap.put("navTools", navTools);
		dataMap.put("gcViewTools", gcViewTools);
		dataMap.put("CommUtil", new CommUtil());
		
		
		
		//导航分类
		params.clear();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		dataMap.put("gcs", gcs);
		for(Article arc:articles){
			arc.setContent(HtmlUtils.htmlUnescape(arc.getContent()));//反转义
			dataMap.put("obj",arc);
			String templateFile = "article.vm";
			String language = Strings.isEmpty(ConfigContants.I18N_LANGUAGE)?"zh_cn":ConfigContants.I18N_LANGUAGE.toLowerCase();
			String outputHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/articles/article_"+arc.getId()+"_new.html";
			String replacedHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/articles/article_"+arc.getId()+".html";
			GenerateHTMLFactory.getInstance().GenerateHTML(dataMap, webappRoot, templateFile, outputHTMLPath, replacedHTMLPath);
		}
	}
	
	
	private void article(){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		Map<String,Object> params = new HashMap<String, Object>();
		//文章分类
		List<ArticleClass> acs = this.articleClassService.query("select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",null, -1, -1);
		params.put("type", "user");
		//最新新闻（6条）
		List<Article> news = this.articleService.query("select obj from Article obj where obj.type=:type order by obj.addTime desc",params, 0, 6);
		//文章
		List<Article> articles = this.articleService.query("select obj from Article obj where obj.type=:type order by obj.addTime desc",params, -1, -1);
		dataMap.put("articles", news);
		dataMap.put("acs", acs);
		dataMap.put("articleTools", articleTools);
		dataMap.put("config", configService.getSysConfig());
		dataMap.put("webPath", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("current_webPath",  ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("imageWebServer", ConfigContants.TYTSMS_WEB_SITE);
		dataMap.put("MIDDLE_NAME", ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME);
		dataMap.put("navTools", navTools);
		dataMap.put("gcViewTools", gcViewTools);
		dataMap.put("CommUtil", new CommUtil());
		
		
		
		//导航分类
		params.clear();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		dataMap.put("gcs", gcs);
		for(Article arc:articles){
			arc.setContent(HtmlUtils.htmlUnescape(arc.getContent()));//反转义
			dataMap.put("obj",arc);
			String templateFile = "article.vm";
			String language = Strings.isEmpty(ConfigContants.I18N_LANGUAGE)?"zh_cn":ConfigContants.I18N_LANGUAGE.toLowerCase();
			String outputHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/articles/article_"+arc.getId()+"_new.html";
			String replacedHTMLPath = webappRoot +"WEB-INF/templates/"+language+"/shop/generator/articles/article_"+arc.getId()+".html";
			GenerateHTMLFactory.getInstance().GenerateHTML(dataMap, webappRoot, templateFile, outputHTMLPath, replacedHTMLPath);
		}
	}
	
	/**
	 * 文章分类列表静态化（分页）
	 */
	
	
	
	/**
	 * 系统初始化和后台手动生成
	 */
	public void toHTML() {
		String language = configService.getSysConfig().getSysLanguage();
		if(webappRoot == null) {
			log.info("获取不到WebPath，退出生成静态页生成");
			return;
		}
		
		try {
			this.indexHTML();
			this.article();
			if(language!=null && language.equalsIgnoreCase("zh_cn")){
				this.activityIndexHTML();
			}
		} catch (Exception e) {
			log.error("静态页面生成失败，失败原因："+e.getMessage());
		}
	}
	
	/**
	 * schedul
	 */
	public void schedulHTML() {
		String language = configService.getSysConfig().getSysLanguage();
		if(webappRoot == null) {
			log.info("获取不到WebPath，退出生成静态页生成");
			return;
		}
		
		try {
			this.indexHTML();
			if(language!=null && language.equalsIgnoreCase("zh_cn")){
				this.activityIndexHTML();
			}
			//清除页面缓存
//			CacheManager manager = CacheManager.create();
//			Ehcache cache = manager.getEhcache("SimplePageCachingFilter");
//			manager.clearAllStartingWith("SimplePageCachingFilter");
		} catch (Exception e) {
			log.error("静态页面生成失败，失败原因："+e.getMessage());
		}
	}

	
	public void articleToHTML(String ids,String classIds) {
		if(webappRoot == null) {
			log.info("获取不到WebPath，退出生成静态页生成");
			return;
		}
		try {
			this.article(ids,classIds);
		} catch (Exception e) {
			log.error("静态页面生成失败，失败原因："+e.getMessage());
		}
	}
	
	

	public void setWebappRoot(String webappRoot) {
		this.webappRoot = webappRoot;
	}
	
	
	

}
