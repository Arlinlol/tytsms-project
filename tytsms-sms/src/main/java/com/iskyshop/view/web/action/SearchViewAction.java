package com.iskyshop.view.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupClassService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupPriceRangeService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.GroupViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.SearchResult;

/**
 * 
 * <p>
 * Title: SearchViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品搜索控制器，商城搜索支持关键字全文搜索
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
 * @author erikzhang,jy
 * 
 * @date 2014-6-5
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class SearchViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGroupPriceRangeService groupPriceRangeService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupClassService groupClassService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_id 商品分类id
	 * @param currentPage 当前页
	 * @param orderBy	排序字段
	 * @param orderType	排序类型 desc asc
	 * @param goods_type 商品类型
	 * @param goods_inventory 库存数量
	 * @param keyword	关键字
	 * @return
	 */
	@RequestMapping("/search.htm")
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type,
			String goods_inventory, String keyword) {
		ModelAndView mv = new JModelAndView("search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
//		String path = ConfigContants.LUCENE_DIRECTORY + File.separator
//				+ "luence" + File.separator + "goods";
//		LuceneUtil lucene = LuceneUtil.instance();
//		lucene.setIndex_path(path);
//		lucene.setGc_size(this.goodsClassService.query("select obj from GoodsClass obj",null,-1, -1).size());
//		boolean order_type = true;
//		String order_by = "";
//		Sort sort = null;
		String query_gc = "";
//		if (CommUtil.null2String(orderType).equals("asc")) {
//			order_type = false;
//		}
//		if (CommUtil.null2String(orderType).equals("")) {
//			orderType = "desc";
//		}
		// 处理查询条件
//		if (CommUtil.null2String(orderBy).equals("addTime") || orderBy == null) {
//			order_by = "add_time";
//			orderBy = "addTime";
//			sort = new Sort(new SortField(order_by, SortField.LONG, order_type));// 排序false升序,true降序
//		}
/*		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
			order_by = "goods_salenum";
			sort = new Sort(new SortField(order_by, SortField.INT, order_type));
		}
		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
			order_by = "goods_collect";
			sort = new Sort(new SortField(order_by, SortField.INT, order_type));
		}
		if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
			order_by = "well_evaluate";
			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
					order_type));
		}
		if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
			order_by = "store_price";
			sort = new Sort(new SortField(order_by, SortField.DOUBLE,
					order_type));
		}*/
		//说明 商品分类现在有三级（0/1/2） 商品只能在第二和第三级上
		if (gc_id!=null&&!gc_id.equals("")) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
			if(gc.getLevel()==0 || gc.getLevel()==1){
				query_gc = gc_id+"_*";
			}else{
				query_gc = CommUtil.null2String(gc.getParent().getId())+"_"+gc_id;
			}
			mv.addObject("gc_id", gc_id);
		}
		SearchResult pList = elasticsearchUtil.search(keyword, 
				CommUtil.null2Int(currentPage), -1, -1, orderBy, orderType, 
				goods_inventory, goods_type, "", "", "", query_gc);
		CommUtil.saveSearchResult2ModelAndView(pList, mv);
//		LuceneResult pList = lucene.search(keyword,
//				CommUtil.null2Int(currentPage), 0, 0, null, sort,
//				goods_inventory, goods_type, "", "", "",query_gc);
//		CommUtil.saveLucene2ModelAndView(pList, mv);
		
		//对关键字命中的商品进行分类提取
		List<String> list_gcs = elasticsearchUtil.LoadData_goods_class(keyword);
		
		//对商品分类数据进行分析加载（二级分类）
		List<GoodsClass> gcs = this.query_GC_second(list_gcs);
		Map<Long,List<GoodsClass>> gcs3Map = this.navProcess(list_gcs);
		
//		mv.addObject("list_gc", list_gcs);
		mv.addObject("gcs3Map",gcs3Map);
		mv.addObject("gcs", gcs);
		mv.addObject("allCount", pList.getRows());
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goods_type", goods_type);
		mv.addObject("goods_inventory", goods_inventory);
		mv.addObject("goodsViewTools", goodsViewTools);

		// 如果系统开启直通车，商品列表页顶部推荐热卖商品及左侧推广商品均显示直通车商品
		if (this.configService.getSysConfig().isZtc_status()) {
			// 页面左侧8条数据，从第3位开始查询
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
					left_ztc_goods, 8);
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
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			List<Goods> top_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List<Goods> all_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, -1, -1);
			List<Goods> left_ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 3, all_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 8);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		mv.addObject("userTools", userTools);
		return mv;
	}
	
	
	private Map<Long,List<GoodsClass>> navProcess(List<String> list_gcs) {
		Map<Long,List<GoodsClass>> result = new HashMap<Long, List<GoodsClass>>();
		if(list_gcs!=null && list_gcs.size()>0){
			for(String gc:list_gcs){
				String parent = gc.split("_")[0];
				String child =  gc.split("_")[1];
				GoodsClass classParent = goodsClassService.getObjById(CommUtil.null2Long(parent));
				GoodsClass classChild = goodsClassService.getObjById(CommUtil.null2Long(child));
				if(result.containsKey(parent)){
					result.get(classParent.getId()).add(classChild);
				}else{
					List<GoodsClass> list = new ArrayList<GoodsClass>();
					list.add(classChild);
					result.put(classParent.getId(), list);
				}
			}
		}
		return result;
	}

	/**
	 * 对商品分类数据进行处理去重，返回页面用以显示的二级分类
	 * @param lucenc商品分类数据
	 * @return
	 */
	public List<GoodsClass> query_GC_second(List<String> list_gcs){
		Set<GoodsClass> set_gcs = new LinkedHashSet<GoodsClass>();
		String sid = new String();
		for (String str : list_gcs) {
			sid = str.split("_")[0];
			set_gcs.add(this.goodsClassService.getObjById(CommUtil.null2Long(sid)));
		}
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		gcs.addAll(set_gcs);
		return gcs;
	}
	
	
    
	
	
}
