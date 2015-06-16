package com.taiyitao.elasticsearch;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;

public class IndexVoTools {
	
	/**
	 * 商品对象转换为IndexVo
	 * @param goods
	 * @return
	 */
	public static IndexVo goodsToIndexVo(Goods goods) {
		IndexVo vo = new IndexVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGoods_name());
		vo.setVo_content(goods.getGoods_details());
		vo.setVo_store_price(CommUtil.null2Double(goods.getGoods_current_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getGoods_salenum());
		vo.setVo_goods_collect(goods.getGoods_collect());
		vo.setVo_well_evaluate(CommUtil.null2Double(goods.getWell_evaluate()));
		vo.setVo_goods_inventory(goods.getGoods_inventory());
		vo.setVo_goods_type(goods.getGoods_type());
		if (goods.getGoods_store() != null
				&& goods.getGoods_store().getUser() != null) {
			vo.setVo_store_username(goods.getGoods_store().getUser()
					.getUserName());
		}
		if (goods.getGoods_main_photo() != null) {
			vo.setVo_main_photo_url(goods.getGoods_main_photo().getPath() + "/"
					+ goods.getGoods_main_photo().getName()+"_middle."+goods.getGoods_main_photo().getExt());
		}
		List<String> list = new ArrayList<String>();
		for (Accessory obj : goods.getGoods_photos()) {
			list.add(obj.getPath() + "/" + obj.getName()+"_middle."+obj.getExt());
		}
		String str = Json.toJson(list, JsonFormat.compact());
		vo.setVo_photos_url(str);
		vo.setVo_goods_evas(goods.getEvaluates().size());
		if (goods.getGoods_brand() != null) {
			vo.setVo_goods_brand(goods.getGoods_brand().getId().toString());
		}
		vo.setVo_goods_class(CommUtil.null2String(goods.getGc().getParent()
				.getId())
				+ "_" + CommUtil.null2String(goods.getGc().getId()));
		return vo;
	}
	
	
	

	
	
	/**
	 * 商品对象转换为IndexVo
	 * @param goodsList
	 * @return
	 */
	public static List<IndexVo> goodsToIndexVo(List<Goods> goodsList) {
		List<IndexVo> indexVoList = new ArrayList<IndexVo>();
		for(Goods goods:goodsList){
			IndexVo vo = new IndexVo();
			vo.setVo_id(goods.getId());
			vo.setVo_title(goods.getGoods_name());
			vo.setVo_content(goods.getGoods_details());
			vo.setVo_store_price(CommUtil.null2Double(goods.getGoods_current_price()));
			vo.setVo_add_time(goods.getAddTime().getTime());
			vo.setVo_goods_salenum(goods.getGoods_salenum());
			vo.setVo_goods_collect(goods.getGoods_collect());
			vo.setVo_well_evaluate(CommUtil.null2Double(goods.getWell_evaluate()));
			vo.setVo_goods_inventory(goods.getGoods_inventory());
			vo.setVo_goods_type(goods.getGoods_type());
			if (goods.getGoods_store() != null
					&& goods.getGoods_store().getUser() != null) {
				vo.setVo_store_username(goods.getGoods_store().getUser()
						.getUserName());
			}
			if (goods.getGoods_main_photo() != null) {
				vo.setVo_main_photo_url(goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName()+"_middle."+goods.getGoods_main_photo().getExt());
			}
			List<String> list = new ArrayList<String>();
			for (Accessory obj : goods.getGoods_photos()) {
				list.add(obj.getPath() + "/" + obj.getName()+"_middle."+obj.getExt());
			}
			String str = Json.toJson(list, JsonFormat.compact());
			vo.setVo_photos_url(str);
			vo.setVo_goods_evas(goods.getEvaluates().size());
			if (goods.getGoods_brand() != null) {
				vo.setVo_goods_brand(goods.getGoods_brand().getId().toString());
			}
			vo.setVo_goods_class(CommUtil.null2String(goods.getGc().getParent()
					.getId())
					+ "_" + CommUtil.null2String(goods.getGc().getId()));
			indexVoList.add(vo);
		}
		return indexVoList;
	}
	
	
	public static List<IndexVo> goodsToIndexVo2(List<Goods> goodsList) {
		List<IndexVo> goods_vo_list = new ArrayList<IndexVo>();
		for(Goods goods:goodsList){
			IndexVo vo = new IndexVo();
			vo.setVo_id(goods.getId());
			vo.setVo_title(goods.getGoods_name());
			vo.setVo_content(goods.getGoods_details());
			vo.setVo_type("goods");
			vo.setVo_store_price(CommUtil.null2Double(goods.getGoods_current_price()));
			vo.setVo_add_time(goods.getAddTime().getTime());
			vo.setVo_goods_salenum(goods.getGoods_salenum());
			vo.setVo_goods_collect(goods.getGoods_collect());
			vo.setVo_well_evaluate(CommUtil.null2Double(goods
					.getWell_evaluate()));
			vo.setVo_goods_inventory(goods.getGoods_inventory());
			vo.setVo_goods_type(goods.getGoods_type());
			if (goods.getGoods_brand() != null) {
				vo.setVo_goods_brand(goods.getGoods_brand().getId().toString());
			}
			if (goods.getGoods_main_photo() != null) {
				vo.setVo_main_photo_url(goods.getGoods_main_photo().getPath()
						+ "/" + goods.getGoods_main_photo().getName());
			}
			if (goods.getGoods_store() != null
					&& goods.getGoods_store().getUser() != null) {
				vo.setVo_store_username(goods.getGoods_store().getUser()
						.getUserName());
			}
			List<String> list = new ArrayList<String>();
			for (Accessory obj : goods.getGoods_photos()) {
				list.add(obj.getPath() + "/" + obj.getName());
			}
			String str = Json.toJson(list, JsonFormat.compact());
			vo.setVo_photos_url(str);
			vo.setVo_goods_evas(goods.getEvaluates().size());
			if (goods.getGc() != null && goods.getGc().getParent() != null) {
				vo.setVo_goods_class(CommUtil.null2String(goods.getGc()
						.getParent().getId())
						+ "_" + CommUtil.null2String(goods.getGc().getId()));
			}
			goods_vo_list.add(vo);
		}
		return goods_vo_list;
	}
	
	/**
	 * 生活购商品转货为indexVo
	 * @param goods
	 * @return
	 */
	public static IndexVo groupLifeGoodsToIndexVo(GroupLifeGoods goods) {
		IndexVo vo = new IndexVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGg_name());
		vo.setVo_content(goods.getGroup_details());
		vo.setVo_type("lifegoods");
		vo.setVo_store_price(CommUtil.null2Double(goods.getGroup_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getGroupInfos().size());
		vo.setVo_cost_price(CommUtil.null2Double(goods.getCost_price()));
		if (goods.getGroup_acc() != null) {
			vo.setVo_main_photo_url(goods.getGroup_acc().getPath() + "/"
					+ goods.getGroup_acc().getName());
		}
		vo.setVo_cost_price(CommUtil.null2Double(goods.getCost_price()));
		vo.setVo_cat(goods.getGg_gc().getId().toString());
		String rate = getRate(
				CommUtil.null2Double(goods.getGroup_price()),
				CommUtil.null2Double(goods.getCost_price())).toString();
		vo.setVo_rate(rate);
		if (goods.getGg_ga() != null) {
			vo.setVo_goods_area(goods.getGg_ga().getId().toString());
		}
		return vo;
	}
	
	public static List<IndexVo> groupLifeGoodsToIndexVo(List<GroupLifeGoods> groupLifeGoods) {
		List<IndexVo> indexVoList = new ArrayList<IndexVo>();
		for(GroupLifeGoods goods:groupLifeGoods){
			IndexVo vo = new IndexVo();
			vo.setVo_id(goods.getId());
			vo.setVo_title(goods.getGg_name());
			vo.setVo_content(goods.getGroup_details());
			vo.setVo_type("lifegoods");
			vo.setVo_store_price(CommUtil.null2Double(goods.getGroup_price()));
			vo.setVo_add_time(goods.getAddTime().getTime());
			vo.setVo_goods_salenum(goods.getGroupInfos().size());
			vo.setVo_cost_price(CommUtil.null2Double(goods.getCost_price()));
			if (goods.getGroup_acc() != null) {
				vo.setVo_main_photo_url(goods.getGroup_acc().getPath() + "/"
						+ goods.getGroup_acc().getName());
			}
			vo.setVo_cost_price(CommUtil.null2Double(goods.getCost_price()));
			vo.setVo_cat(goods.getGg_gc().getId().toString());
			String rate = getRate(
					CommUtil.null2Double(goods.getGroup_price()),
					CommUtil.null2Double(goods.getCost_price())).toString();
			vo.setVo_rate(rate);
			if (goods.getGg_ga() != null) {
				vo.setVo_goods_area(goods.getGg_ga().getId().toString());
			}
			indexVoList.add(vo);
		}
		return indexVoList;
	}
	
	
	/**
	 * 将GroupGoods转换为indexVo
	 * @param gg
	 * @return
	 */
	public static IndexVo groupGoodsToIndexVo(GroupGoods gg) {
		IndexVo vo = new IndexVo();
		vo.setVo_id(gg.getId());
		vo.setVo_title(gg.getGg_name());
		vo.setVo_content(gg.getGg_content());
		vo.setVo_type("lifegoods");
		vo.setVo_store_price(CommUtil.null2Double(gg.getGg_price()));
		vo.setVo_add_time(gg.getAddTime().getTime());
		vo.setVo_goods_salenum(gg.getGg_selled_count());
		if (gg.getGg_img() != null) {
			vo.setVo_main_photo_url(gg.getGg_img().getPath() + "/"
					+ gg.getGg_img().getName());
		}
		vo.setVo_cat(gg.getGg_gc().getId().toString());
		vo.setVo_rate(CommUtil.null2String(gg.getGg_rebate()));
		vo.setVo_goods_area(gg.getGg_ga().getId().toString());
		return vo;
		
	}
	
	
	public static List<IndexVo> groupGoodsToIndexVo(List<GroupGoods> groupGoods) {
		List<IndexVo> indexVoList = new ArrayList<IndexVo>();
		for(GroupGoods goods:groupGoods){
			IndexVo vo = new IndexVo();
			vo.setVo_id(goods.getId());
			vo.setVo_title(goods.getGg_name());
			vo.setVo_content(goods.getGg_content());
			vo.setVo_type("lifegoods");
			vo.setVo_store_price(CommUtil.null2Double(goods.getGg_price()));
			vo.setVo_add_time(goods.getAddTime().getTime());
			vo.setVo_goods_salenum(goods.getGg_selled_count());
			vo.setVo_cost_price(CommUtil.null2Double(goods.getGg_goods()
					.getGoods_price()));
			if (goods.getGg_img() != null) {
				vo.setVo_main_photo_url(goods.getGg_img().getPath() + "/"
						+ goods.getGg_img().getName());
			}
			vo.setVo_cat(goods.getGg_gc().getId().toString());
			vo.setVo_rate(CommUtil.null2String(goods.getGg_rebate()));
			if (goods.getGg_ga() != null) {
				vo.setVo_goods_area(goods.getGg_ga().getId().toString());
			}
			
			indexVoList.add(vo);
		}
		return indexVoList;
	}
	
	
	/**
	 * 将GroupLifeGoods对象转换为LuceneVo对象
	 * @param goods
	 * @return
	 */
	public IndexVo lifeGoodsToIndexVo(GroupLifeGoods goods) {
		IndexVo vo = new IndexVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGg_name());
		vo.setVo_content(goods.getGroup_details());
		vo.setVo_type("lifegoods");
		vo.setVo_store_price(CommUtil.null2Double(goods.getGroup_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getGroupInfos().size());
		if (goods.getGroup_acc() != null) {
			vo.setVo_main_photo_url(goods.getGroup_acc().getPath() + "/"
					+ goods.getGroup_acc().getName());
		}
		vo.setVo_cost_price(CommUtil.null2Double(goods.getCost_price()));
		vo.setVo_cat(goods.getGg_gc().getId().toString());
		String rate =getRate(
				CommUtil.null2Double(goods.getGroup_price()),
				CommUtil.null2Double(goods.getCost_price())).toString();
		vo.setVo_rate(rate);
		if (goods.getGg_ga() != null) {
			vo.setVo_goods_area(goods.getGg_ga().getId().toString());
		}

		return vo;
	}
	
	
	
	/**
	 * 将GroupGoods对象转换为LuceneVo对象
	 * @param goods
	 * @return
	 */
	public static IndexVo updateGroupGoodsIndex(GroupGoods goods) {
		IndexVo vo = new IndexVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGg_name());
		vo.setVo_content(goods.getGg_content());
		vo.setVo_type("lifegoods");
		vo.setVo_store_price(CommUtil.null2Double(goods.getGg_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getGg_selled_count());
		vo.setVo_cost_price(CommUtil.null2Double(goods.getGg_goods()
				.getGoods_price()));
		if (goods.getGg_img() != null) {
			vo.setVo_main_photo_url(goods.getGg_img().getPath() + "/"
					+ goods.getGg_img().getName());
		}
		vo.setVo_cat(goods.getGg_gc().getId().toString());
		vo.setVo_rate(CommUtil.null2String(goods.getGg_rebate()));
		if (goods.getGg_ga() != null) {
			vo.setVo_goods_area(goods.getGg_ga().getId().toString());
		}
		return vo;
	}
	
	
	
	private static Double getRate(Double group_price, Double cost_price) {
		double ret = 0.0;
		if (!CommUtil.null2String(group_price).equals("")
				&& !CommUtil.null2String(cost_price).equals("")) {
			BigDecimal e = new BigDecimal(CommUtil.null2String(group_price));
			BigDecimal f = new BigDecimal(CommUtil.null2String(cost_price));
			if (CommUtil.null2Float(f) > 0)
				ret = e.divide(f, 3, BigDecimal.ROUND_DOWN).doubleValue();
		}
		DecimalFormat df = new DecimalFormat("0.00");
		Double re = CommUtil.mul(Double.valueOf(df.format(ret)), 10);
		return re;
	}





	/**
	 * 排序字段处理
	 * @param sortField
	 * @return
	 */
	public static String getSortField(String sortField) {
		if(StringUtils.isNotEmpty(sortField)){
			if("addTime".equals(sortField)){
				return IndexVo.VO_ADD_TIME;
			}else if(sortField.equals("goods_salenum")){
				return IndexVo.VO_GOODS_SALENUM;
			}else if(sortField.equals("goods_collect")){
				return IndexVo.VO_GOODS_COLLECT;
			}else if(sortField.equals("well_evaluate")){
				return IndexVo.VO_WELL_EVALUATE;
			}else if(sortField.equals("goods_current_price")){
				return IndexVo.VO_STORE_PRICE;
			}
		}
		return IndexVo.VO_ADD_TIME;
	}
	

}
