package com.iskyshop.view.web.tools;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IGoodsService;

@Component
public class ActivityViewTools {
	@Autowired
	private IGoodsService goodspService;
	@Autowired
	private IActivityGoodsService actgoodspService;
	@Autowired
	private IntegralViewTools IntegralViewTools;

	/**
	 * 活动专题页中，每个商品显示其四个会员等级的价格，
	 * 
	 * @param goods_id
	 * @return
	 */
	public Map getActivityPrices(String goods_id) {
		Goods obj = this.goodspService.getObjById(CommUtil.null2Long(goods_id));
		Map map = new HashMap();
		if (obj != null && obj.getActivity_status() == 2) {
			ActivityGoods actGoods = null;
			for (ActivityGoods ag : obj.getActivity_goods_list()) {
				if (CommUtil.null2String(ag.getAg_goods().getId()).equals(
						goods_id)) {
					actGoods = ag;
					break;
				}
			}
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				map.put("price1", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate(), obj.getGoods_current_price())));
				map.put("price2", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate1(), obj.getGoods_current_price())));
				map.put("price3", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate2(), obj.getGoods_current_price())));
				map.put("price4", CommUtil.formatMoney(CommUtil.mul(
						act.getAc_rebate3(), obj.getGoods_current_price())));
			}
		}
		return map;
	}

	/**
	 * 商品详情页，显示商品的所有活动信息，包括活动商品价格、活动折扣，当前登录用户的用户等级
	 * 
	 * @param goods_id
	 * @param user_id
	 * @return
	 */
	public Map getActivityGoodsInfo(String goods_id, String user_id) {
		Goods obj = this.goodspService.getObjById(CommUtil.null2Long(goods_id));
		Map map = new HashMap();
		if (obj != null && obj.getActivity_status() == 2) {
			ActivityGoods actGoods = null;
			for (ActivityGoods ag : obj.getActivity_goods_list()) {
				if (CommUtil.null2String(ag.getAg_goods().getId()).equals(
						goods_id)) {
					actGoods = ag;
					break;
				}
			}
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				String rate = "0.00";
				String level_name = "铜牌会员";
				int level = this.IntegralViewTools.query_user_level(user_id);
				if (level == 0) {
					rate = CommUtil.formatMoney(act.getAc_rebate());
				} else if (level == 1) {
					level_name = "银牌会员";
					rate = CommUtil.formatMoney(act.getAc_rebate1());
				} else if (level == 2) {
					level_name = "金牌会员";
					rate = CommUtil.formatMoney(act.getAc_rebate2());
				} else if (level == 3) {
					level_name = "超级会员";
					rate = CommUtil.formatMoney(act.getAc_rebate3());
				}
				map.put("rate", rate);
				map.put("level_name", level_name);
				map.put("rate_price",
						CommUtil.formatMoney(CommUtil.mul(rate,
								obj.getGoods_current_price())));
			}
		}
		return map;
	}
}
