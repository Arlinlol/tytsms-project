package com.iskyshop.manage.buyer.Tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.virtual.FootPointView;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IStoreService;

/**
 * 
 * <p>
 * Title: FootPointTools.java
 * </p>
 * 
 * <p>
 * Description:足迹处理控制器，用来解析足迹数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-10-17
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class FootPointTools {
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;

	public List<FootPointView> generic_fpv(String json) {
		List<FootPointView> fpvs = new ArrayList<FootPointView>();
		if (!CommUtil.null2String(json).equals("")) {
			try {
				List<Map> list = Json.fromJson(List.class, json);
				for (Map map : list) {
					FootPointView fpv = new FootPointView();
					fpv.setFpv_goods_id(CommUtil.null2Long(map.get("goods_id")));
					fpv.setFpv_goods_img_path(CommUtil.null2String(map
							.get("goods_img_path")));
					fpv.setFpv_goods_name(CommUtil.null2String(map
							.get("goods_name")));
					fpv.setFpv_goods_sale(CommUtil.null2Int(map
							.get("goods_sale")));
					fpv.setFpv_goods_price(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("goods_price"))));
					fpv.setFpv_goods_class_id(CommUtil.null2Long(map
							.get("goods_class_id")));
					fpv.setFpv_goods_class_name(CommUtil.null2String(map
							.get("goods_class_name")));
					fpvs.add(fpv);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fpvs;
	}

	/**
	 * 根据店铺id查询是否开启了二级域名。
	 * 
	 * @param id为参数
	 *            type为store时查询store type为goods时查询商品
	 * @return
	 */
	public Store goods_second_domain(String id, String type) {
		Store store = null;
		if (type.equals("store")) {
			store = this.storeService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("goods")) {
			store = this.goodsService.getObjById(CommUtil.null2Long(id))
					.getGoods_store();
		}
		return store;
	}
}
