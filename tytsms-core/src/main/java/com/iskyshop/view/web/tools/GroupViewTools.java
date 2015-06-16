package com.iskyshop.view.web.tools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;

@Component
public class GroupViewTools {
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupGoodsService groupGoodsService;

	public List<GroupGoods> query_goods(String group_id, int count) {
		List<GroupGoods> list = new ArrayList<GroupGoods>();
		Map params = new HashMap();
		params.put("group_id", CommUtil.null2Long(group_id));
		list = this.groupGoodsService
				.query(
						"select obj from GroupGoods obj where obj.group.id=:group_id order by obj.addTime desc",
						params, 0, count);
		return list;
	}
	public static Double getRate(Double group_price, Double cost_price) {
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
}
