package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MsgTools.java<／p>
 * 
 * <p>
 * Description: 结算账单工具类 <／p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014<／p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com<／p>
 * 
 * @author hezeng
 * 
 * @date 2014-5-6
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class PayoffLogTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService ofService;
	@Autowired
	private IGoodsService goodsService;

	/**
	 * 解析json数据
	 * 
	 * @param
	 * @return
	 */
	public List<Map> queryGoodsInfo(String json) {
		List<Map> map_list = Json.fromJson(ArrayList.class, json);
		return map_list;
	}

	/**
	 * 查询账单中的对应的订单
	 * 
	 * @param order_id
	 * @return
	 */
	public OrderForm queryOrderInfo(String order_id) {
		OrderForm of = new OrderForm();
		if (order_id != null && !order_id.equals("")) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			Map params = new HashMap();
			params.put("order_id", order_id);
			params.put("store_id", user.getStore().getId().toString());
			List<OrderForm> ofs = this.ofService
					.query("select obj from OrderForm obj where obj.order_id=:order_id and obj.store_id=:store_id order by addTime asc",
							params, 0, 1);
			if (ofs.size() > 0) {
				of = ofs.get(0);
			}
		}
		return of;
	}

	/**
	 * 查询账单中的对应的订单
	 * 
	 * @param order_id
	 * @return
	 */
	public OrderForm adminqueryOrderInfo(String order_id) {
		OrderForm of = new OrderForm();
		if (order_id != null && !order_id.equals("")) {
			Map params = new HashMap();
			params.put("order_id", order_id);
			List<OrderForm> ofs = this.ofService
					.query("select obj from OrderForm obj where obj.order_id=:order_id order by addTime asc",
							params, 0, 1);
			if (ofs.size() > 0) {
				of = ofs.get(0);
			}
		}
		return of;
	}

}
