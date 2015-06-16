package com.iskyshop.view.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsBrandCategory;
import com.iskyshop.foundation.service.IGoodsBrandCategoryService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.SearchResult;

/**
 * 
 * <p>
 * Title: BrandViewAction.java
 * </p>
 * 
 * <p>
 * Description: 品牌相关控制器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-28
 * 
 * @version iskyshop_b2b2c V1.0
 */

@Controller
public class BrandViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategorySerivce;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

	/**
	 * 品牌首页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/brand/index.htm")
	public ModelAndView brand(HttpServletRequest request,
			HttpServletResponse response, String gbc_id) {
		ModelAndView mv = new JModelAndView("brand.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsBrandCategory> gbcs = this.goodsBrandCategorySerivce
				.query("select obj from GoodsBrandCategory obj  order by obj.addTime asc",
						null, -1, -1);
		Map params = new HashMap();
		params.put("recommend", true);
		params.put("audit", 1);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc",
						params, 0, 12);
		mv.addObject("gbs", gbs);
		mv.addObject("gbcs", gbcs);
		List<GoodsBrand> brands = new ArrayList<GoodsBrand>();
		if (gbc_id != null && !gbc_id.equals("")) {
			mv.addObject("gbc_id", gbc_id);
			params.clear();
			params.put("gbc_id", CommUtil.null2Long(gbc_id));
			params.put("audit", 1);
			brands = this.goodsBrandService
					.query("select obj from GoodsBrand obj where obj.category.id=:gbc_id and obj.audit=:audit order by obj.sequence asc",
							params, -1, -1);
		} else {
			params.clear();
			params.put("audit", 1);
			brands = this.goodsBrandService
					.query("select obj from GoodsBrand obj where obj.audit=:audit order by obj.sequence asc",
							params, -1, -1);
		}
		List all_list = new ArrayList();
		String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String words[] = list_word.split(",");
		for (String word : words) {
			Map brand_map = new HashMap();
			List brand_list = new ArrayList();
			for (GoodsBrand gb : brands) {
				if (!CommUtil.null2String(gb.getFirst_word()).equals("")
						&& word.equals(gb.getFirst_word().toUpperCase())) {
					brand_list.add(gb);
				}
			}
			brand_map.put("brand_list", brand_list);
			brand_map.put("word", word);
			all_list.add(brand_map);
		}
		mv.addObject("all_list", all_list);
		return mv;
	}


	/**
	 * 根据品牌查询商品
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param store_price_begin
	 * @param store_price_end
	 * @param op
	 * @param goods_name
	 * @param keyword
	 * @param goods_inventory
	 * @param goods_type
	 * @return
	 */
//	@RequestMapping("/brand_goods.htm")
//	public ModelAndView brand_goods(HttpServletRequest request,
//			HttpServletResponse response, String id, String currentPage,
//			String orderBy, String orderType, String store_price_begin,
//			String store_price_end, String op, String goods_name,
//			String keyword, String goods_inventory, String goods_type) {
//		ModelAndView mv = new JModelAndView("brand_goods.html",
//				configService.getSysConfig(),
//				this.userConfigService.getUserConfig(), 1, request, response);
//		if (op != null && !op.equals("")) {
//			mv.addObject("op", op);
//		}
//		GoodsBrand gb = this.goodsBrandService.getObjById(CommUtil
//				.null2Long(id));
//		mv.addObject("gb", gb);
//		Map params = new HashMap();
//		params.put("recommend", true);
//		params.put("audit", 1);
//		List<GoodsBrand> gbs = this.goodsBrandService
//				.query("select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc",
//						params, 0, 10);
//		mv.addObject("gbs", gbs);
//		mv.addObject("storeViewTools", storeViewTools);
//
//		String path = ConfigContants.LUCENE_DIRECTORY + File.separator
//				+ "luence" + File.separator + "goods";
//		LuceneUtil lucene = LuceneUtil.instance();
//		lucene.setIndex_path(path);
//		boolean order_type = true;
//		String order_by = "";
//		Sort sort = null;
//		if (CommUtil.null2String(orderType).equals("asc")) {
//			order_type = false;
//		}
//		if (CommUtil.null2String(orderType).equals("")) {
//			orderType = "desc";
//		}
//		// 处理查询条件
//		if (CommUtil.null2String(orderBy).equals("addTime") || orderBy == null) {
//			order_by = "add_time";
//			orderBy = "addTime";
//			sort = new Sort(new SortField(order_by, SortField.LONG, order_type));// 排序false升序,true降序
//		}
//		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
//			order_by = "goods_salenum";
//			sort = new Sort(new SortField(order_by, SortField.INT, order_type));
//		}
//		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
//			order_by = "goods_collect";
//			sort = new Sort(new SortField(order_by, SortField.INT, order_type));
//		}
//		if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
//			order_by = "well_evaluate";
//			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
//					order_type));
//		}
//		if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
//			order_by = "store_price";
//			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
//					order_type));
//		}
//		
//		LuceneResult pList = lucene.search(keyword,
//				CommUtil.null2Int(currentPage), 0, 0, null, sort,
//				goods_inventory, goods_type, "", "", id,"");
//		
//		// for (LuceneVo vo : pList.getVo_list()) {
//		// Goods goods = this.goodsService.getObjById(vo.getVo_id());
//		// goods.setGoods_name(vo.getVo_title());
//		// pList.getGoods_list().add(goods);
//		// }
//		CommUtil.saveLucene2ModelAndView(pList, mv);
//		System.out.println(pList.getRows());
//		mv.addObject("allCount", pList.getVo_list().size());
//		mv.addObject("keyword", keyword);
//		mv.addObject("orderBy", orderBy);
//		mv.addObject("orderType", orderType);
//		mv.addObject("goods_type", goods_type);
//		mv.addObject("goods_inventory", goods_inventory);
//		mv.addObject("goodsViewTools", goodsViewTools);
//		mv.addObject("id", id);
//
//		// 如果系统开启直通车，商品列表页顶部推荐热卖商品及左侧推广商品均显示直通车商品
//		if (this.configService.getSysConfig().isZtc_status()) {
//			// 页面左侧10条数据，从第3位开始查询
//			List<Goods> left_ztc_goods = null;
//			Map ztc_map = new HashMap();
//			ztc_map.put("ztc_status", 3);
//			ztc_map.put("now_date", new Date());
//			ztc_map.put("ztc_gold", 0);
//			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
//			List<Goods> all_left_ztc_goods = this.goodsService
//					.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
//							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
//							+ "order by obj.ztc_dredge_price desc", ztc_map,
//							-1, -1);
//			left_ztc_goods = this.goodsService
//					.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
//							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
//							+ "order by obj.ztc_dredge_price desc", ztc_map, 3,
//							all_left_ztc_goods.size());
//			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
//					left_ztc_goods, 10);
//			mv.addObject("left_ztc_goods", left_ztc_goods);
//			// 页面顶部,直通车前3个商品
//			List<Goods> top_ztc_goods = null;
//			Map ztc_map2 = new HashMap();
//			ztc_map2.put("ztc_status", 3);
//			ztc_map2.put("now_date", new Date());
//			ztc_map2.put("ztc_gold", 0);
//			top_ztc_goods = this.goodsService
//					.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
//							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
//							+ "order by obj.ztc_dredge_price desc", ztc_map2,
//							0, 3);
//			mv.addObject("top_ztc_goods", top_ztc_goods);
//		} else {
//			Map params2 = new HashMap();
//			params2.put("store_recommend", true);
//			params2.put("goods_status", 0);
//			List<Goods> top_ztc_goods = this.goodsService
//					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
//							params2, 0, 3);
//			mv.addObject("top_ztc_goods", top_ztc_goods);
//			params2.clear();
//			params2.put("store_recommend", true);
//			params2.put("goods_status", 0);
//			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
//			List<Goods> all_goods = this.goodsService
//					.query("select obj.id from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
//							params2, -1, -1);
//			List<Goods> left_ztc_goods = this.goodsService
//					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
//							params2, 3, all_goods.size());
//			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
//					left_ztc_goods, 10);
//			mv.addObject("left_ztc_goods", left_ztc_goods);
//		}
//		return mv;
//	}
//	
	
	/**
	 * 根据品牌查询商品
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param store_price_begin
	 * @param store_price_end
	 * @param op
	 * @param goods_name
	 * @param keyword
	 * @param goods_inventory
	 * @param goods_type
	 * @return
	 */
	
	@RequestMapping("/brand_goods.htm")
	public ModelAndView brand_goods(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType, String store_price_begin,
			String store_price_end, String op, String goods_name,
			String keyword, String goods_inventory, String goods_type) {
		ModelAndView mv = new JModelAndView("brand_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (op != null && !op.equals("")) {
			mv.addObject("op", op);
		}
		GoodsBrand gb = this.goodsBrandService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("gb", gb);
		Map params = new HashMap();
		params.put("recommend", true);
		params.put("audit", 1);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc",
						params, 0, 10);
		mv.addObject("gbs", gbs);
		mv.addObject("storeViewTools", storeViewTools);

	
//		boolean order_type = true;
//		String order_by = "";
//		Sort sort = null;
//		if (CommUtil.null2String(orderType).equals("asc")) {
//			order_type = false;
//		}
//		if (CommUtil.null2String(orderType).equals("")) {
//			orderType = "desc";
//		}
//		// 处理查询条件
//		if (CommUtil.null2String(orderBy).equals("addTime") || orderBy == null) {
//			order_by = "add_time";
//			orderBy = "addTime";
//			sort = new Sort(new SortField(order_by, SortField.LONG, order_type));// 排序false升序,true降序
//		}
//		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
//			order_by = "goods_salenum";
//			sort = new Sort(new SortField(order_by, SortField.INT, order_type));
//		}
//		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
//			order_by = "goods_collect";
////			sort = new Sort(new SortField(order_by, SortField.INT, order_type));
//		}
//		if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
//			order_by = "well_evaluate";
////			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
////					order_type));
//		}
//		if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
//			order_by = "store_price";
////			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
////					order_type));
//		}
		
		SearchResult pList = elasticsearchUtil.search(keyword,
				CommUtil.null2Int(currentPage), -1, -1, orderBy, orderType,
				goods_inventory, goods_type, "", "", id,"");

		// for (LuceneVo vo : pList.getVo_list()) {
		// Goods goods = this.goodsService.getObjById(vo.getVo_id());
		// goods.setGoods_name(vo.getVo_title());
		// pList.getGoods_list().add(goods);
		// }
		CommUtil.saveSearchResult2ModelAndView(pList, mv);
		System.out.println(pList.getRows());
		mv.addObject("allCount", pList.getVo_list().size());
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goods_type", goods_type);
		mv.addObject("goods_inventory", goods_inventory);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("id", id);

		// 如果系统开启直通车，商品列表页顶部推荐热卖商品及左侧推广商品均显示直通车商品
		if (this.configService.getSysConfig().isZtc_status()) {
			// 页面左侧10条数据，从第3位开始查询
			List<Goods> left_ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List<Goods> all_left_ztc_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map,
							-1, -1);
			left_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map, 3,
							all_left_ztc_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
			// 页面顶部,直通车前3个商品
			List<Goods> top_ztc_goods = null;
			Map ztc_map2 = new HashMap();
			ztc_map2.put("ztc_status", 3);
			ztc_map2.put("now_date", new Date());
			ztc_map2.put("ztc_gold", 0);
			top_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map2,
							0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map params2 = new HashMap();
			params2.put("store_recommend", true);
			params2.put("goods_status", 0);
			List<Goods> top_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params2, 0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params2.clear();
			params2.put("store_recommend", true);
			params2.put("goods_status", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List<Goods> all_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params2, -1, -1);
			List<Goods> left_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params2, 3, all_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		return mv;
	}
	
}
