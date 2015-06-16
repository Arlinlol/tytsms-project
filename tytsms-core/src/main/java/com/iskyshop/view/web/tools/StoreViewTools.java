package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * @info 店铺工具类
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Component
public class StoreViewTools {
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsService goodsService;

	/**
	 * 根据店铺等级属性，生成文字说明
	 * 
	 * @param grade
	 * @return
	 */
	public String genericFunction(StoreGrade grade) {
		String fun = "";
		if (grade.getAdd_funciton().equals(""))
			fun = "无";
		String[] list = grade.getAdd_funciton().split(",");
		for (String s : list) {
			if (s.equals("editor_multimedia")) {
				fun = "富文本编辑器" + fun;
			}
		}
		return fun;
	}

	/**
	 * 转换商品后缀名
	 * 
	 * @param imageSuffix
	 * @return
	 */
	public String genericImageSuffix(String imageSuffix) {
		String suffix = "";
		String[] list = imageSuffix.split("\\|");
		for (String l : list) {
			suffix = "*." + l + ";" + suffix;
		}
		return suffix.substring(0, suffix.length() - 1);
	}

	/**
	 * 按照数量查询推荐店铺(明星店铺)
	 * 
	 * @param count
	 * @return
	 */
	public List<Store> query_recommend_store(int count) {
		List<Store> list = new ArrayList<Store>();
		Map params = new HashMap();
		params.put("recommend", true);
		list = this.storeService
				.query("select obj from Store obj where obj.store_recommend=:recommend order by obj.store_recommend_time desc",
						params, 0, count);
		return list;
	}

	/**
	 * 搜索店铺时在店铺列表页显示相应店铺的推荐商品,不足5个自动补5个Null值
	 * 
	 * @param begin
	 *            :推荐商品查询开始位置
	 * @param max
	 *            查询商品数量
	 * @return 商品列表
	 */
	public List<Goods> query_recommend_store_goods(Store store, int begin,
			int max) {
		Map params = new HashMap();
		params.put("recommend", true);
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_recommend=:recommend and obj.goods_status=:goods_status",
						params, begin, max);
		if (goods.size() < 5) {
			int count = 5 - goods.size();
			for (int i = 0; i < count; i++) {
				goods.add(null);
			}
		}
		return goods;
	}

	/**
	 * 查询店铺评价
	 * 
	 * @param store_id
	 * @param evaluate_val
	 * @param type
	 * @param date_symbol
	 * @param date_count
	 * @return
	 */
	public int query_evaluate(String store_id, int evaluate_val, String type,
			String date_symbol, int date_count) {
		Calendar cal = Calendar.getInstance();
		if (type.equals("date")) {
			cal.add(Calendar.DAY_OF_YEAR, date_count);
		}
		if (type.equals("week")) {
			cal.add(Calendar.WEEK_OF_YEAR, date_count);
		}
		if (type.equals("month")) {
			cal.add(Calendar.MONTH, date_count);
		}
		String symbol = ">=";
		if (date_symbol.equals("before")) {
			symbol = "<=";
		}
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("addTime", cal.getTime());
		params.put("evaluate_buyer_val", CommUtil.null2Int(evaluate_val));
		List<Evaluate> evas = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_goods.goods_store.id=:store_id and obj.evaluate_buyer_val=:evaluate_buyer_val and obj.addTime"
						+ symbol + ":addTime", params, -1, -1);
		return evas.size();
	}

	public String queryStoreNameById(String store_id) {
		String store_name = "";
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		if (store != null) {
			store_name = store.getStore_name();
		}
		return store_name;
	}

	public String queryStoreQQById(String store_id) {
		String store_qq = "";
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		if (store != null) {
			store_qq = store.getStore_qq();
		}
		return store_qq;
	}

}
