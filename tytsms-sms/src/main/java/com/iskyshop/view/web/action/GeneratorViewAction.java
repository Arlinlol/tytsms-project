package com.iskyshop.view.web.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.ArticleClass;
import com.iskyshop.foundation.domain.GeneratorType;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.Partner;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IArticleClassService;
import com.iskyshop.foundation.service.IArticleService;
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
import com.iskyshop.pay.wechatpay.util.HttpTool;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsFloorViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.taiyitao.quartz.job.GenerateHomePageJob;

/**
 * 
 * <p>
 * Title: GeneratorViewAction.java
 * </p>
 * 
 * <p>
 * Description:商城首页页面静态化控制器
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
 * @author 
 * 
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class GeneratorViewAction {
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
	private GenerateHomePageJob generateHomePageJob;

	private int index_recommend_count = 5;// 首页推荐商品及推荐用户喜欢的商品个数，所有在这个页面位置的商品都以该数量作为查询基准，定义为一个参数，便于修改

	
	
	/**
	 * 商城首页推荐商品页面，使用自定义标签httpInclude.include("/generator/top.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping("/generator/top.htm")
//	public ModelAndView top(HttpServletRequest request,
//			HttpServletResponse response) {
//		ModelAndView mv = new JModelAndView("top.html",
//				configService.getSysConfig(),
//				this.userConfigService.getUserConfig(), 1, request, response);
//		mv.addObject("navTools", navTools);
//		return mv;
//	}
	
	
	@RequestMapping("/generator/head.htm")
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/headOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}


	
	/**
	 * 商城首页推荐商品页面，使用自定义标签httpInclude.include("/generator/recommend.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/generator/recommend.htm")
	public ModelAndView recommendHTML(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/recommendOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	
	/**
	 * 商城首页楼层页面，使用自定义标签httpInclude.include("/generator/floor.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/generator/floor.htm")
	public ModelAndView floorHTML(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/floorOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	/**
	 * 商城首页品牌页面，使用自定义标签httpInclude.include("/generator/goodsBrands.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/generator/goodsBrands.htm")
	public ModelAndView goodsBrandsHTML(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/goodsBrandsOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	/**
	 * 商城首页文章页面，使用自定义标签httpInclude.include("/generator/articleClass.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/generator/articleClass.htm")
	public ModelAndView articleClassHTML(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/articleClassOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	
	/**
	 * 商城首页页脚页面，使用自定义标签httpInclude.include("/generator/footer.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/generator/footer.htm")
	public ModelAndView footerHTML(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/footerOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", navTools);
		return mv;
	}
	
	/**
	 * 商城首页导航页面，使用自定义标签httpInclude.include("/generator/nav.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/generator/nav.htm")
	public ModelAndView navHTML(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/navOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	
	/**
	 * 商城首页导航页面，使用自定义标签httpInclude.include("/generator/nav1.htm")完成页面读取
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/generator/nav1.htm")
	public ModelAndView nav1HTML(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("generator/nav1Output.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	

	/**
	 * 商城首页,调试使用
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/original_index.htm")
	public ModelAndView original_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("original_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("audit", 1);
		params.put("recommend", true);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.recommend=:recommend order by obj.sequence",
						params, -1, -1);
		mv.addObject("gbs", gbs);
		params.clear();
		List<Partner> img_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is not null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("img_partners", img_partners);
		List<Partner> text_partners = this.partnerService
				.query("select obj from Partner obj where obj.image.id is null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("text_partners", text_partners);
		params.clear();
		Set marks = new TreeSet();// 排除首页有商家4个分类及商家独享的文章信息，erikzhang
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
		mv.addObject("acs", acs);
		params.clear();
		String[] class_marks = { "gonggao", "guize", "anquan", "zhinan" }; // 首页右上角公告区分类的标识，通过后台添加
		Map articles = new LinkedHashMap();
		for (String m : class_marks) {
			params.put("class_mark", m);
			params.put("display", true);
			List<Article> article = this.articleService
					.query("select obj from Article obj where obj.articleClass.parent.mark=:class_mark and obj.display=:display order by obj.addTime desc",
							params, 0, 3);
			articles.put(m, article);
		}
		mv.addObject("articles", articles);
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
		mv.addObject("store_recommend_goods", store_recommend_goods);
		mv.addObject("store_recommend_goods_count", (int) Math.ceil(CommUtil
				.div(store_recommend_goods_list.size(),
						this.index_recommend_count)));
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId()));
		}
		params.clear();

		params.put("show_index", true);
		params.put("audit", 1);
		List<GoodsBrand> goodsBrands = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.show_index=:show_index and obj.audit=:audit order by obj.sequence asc",
						params, 0, 4);
		mv.addObject("goodsBrands", goodsBrands);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		int you_like_goods_count = 0;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (goods != null && !goods.equals("")&&goods.getGc()!=null) {
						your_like_GoodsClass = goods.getGc().getId();
						your_like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
										+ your_like_GoodsClass
										+ " and obj.id is not " + goods.getId()
										+ " order by obj.goods_salenum desc",
								null, 0, 5);
						int gcs_size = your_like_goods.size();
						you_like_goods_count = (int) Math.ceil(CommUtil.div(
								gcs_size, this.index_recommend_count));
						if (gcs_size < 5) {
							List<Goods> like_goods = this.goodsService
									.query("select obj from Goods obj where obj.goods_status=0 and obj.id is not "
											+ goods.getId()
											+ " order by obj.goods_salenum desc",
											null, 0, 5 - gcs_size);
							for (int i = 0; i < like_goods.size(); i++) {
								// 去除重复商品
								int k = 0;
								for (int j = 0; j < your_like_goods.size(); j++) {
									if (like_goods
											.get(i)
											.getId()
											.equals(your_like_goods.get(j)
													.getId())) {
										k++;
									}
								}
								if (k == 0) {
									your_like_goods.add(like_goods.get(i));
								}
							}
						}
						break;
					}
				} else {
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 5);
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, 0, 5);
		}
		mv.addObject("your_like_goods", your_like_goods);
		mv.addObject("navTools", navTools);
		//mv.addObject("v",ConfigContants.JS_CSS_V);
		
		return mv;
	}
	
	
	@RequestMapping("/generator/advert.htm")
	public ModelAndView advert(HttpServletRequest request,
			HttpServletResponse response, String data_cache, String page_cache) {
		ModelAndView mv = new JModelAndView("generator/advertOutput.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	
	@RequestMapping("/generator.htm")
	public void generator(HttpServletRequest request,
			HttpServletResponse response,String type,String ids,String classIds) {
			String path = request.getSession().getServletContext().getRealPath("")+File.separator;
			generateHomePageJob.webappRoot=path;
			if(type.equals(GeneratorType.ALL.name())){
				generateHomePageJob.toHTML();
			}else if(type.equals(GeneratorType.ARTICLE.name())){
				generateHomePageJob.articleToHTML(ids,classIds);
			}else{
				generateHomePageJob.toHTML();
			}
	}
	

 public static void main(String[] args){
	 String str = HttpTool.sendPost("http://127.0.0.1:8080/taiyitao/generator.htm", null, "UTF-8");
	 System.out.println(str);
 }

	
}
