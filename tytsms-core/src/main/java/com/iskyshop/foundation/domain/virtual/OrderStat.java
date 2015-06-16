package com.iskyshop.foundation.domain.virtual;

import java.util.Date;

/**
 * 
 * <p>
 * Title: OrderStat.java
 * </p>
 * 
 * <p>
 * Description:订单统计管理类，该类用来在后台显示统计数据，不和数据表对应，根据时间区间查询数据并以图形、图表显示
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
 * @author erikzhang
 * 
 * @date 2014-6-5
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class OrderStat {
	private Date stat_time;// 统计时间
	private int order_count;// 当日订单总数
	private int order_pay_count;// 当日付款订单数
	private int order_ship_count;// 当日发货订单数
	private float order_amount;// 当日付完订单总金额

	public Date getStat_time() {
		return stat_time;
	}

	public void setStat_time(Date stat_time) {
		this.stat_time = stat_time;
	}

	public int getOrder_count() {
		return order_count;
	}

	public void setOrder_count(int order_count) {
		this.order_count = order_count;
	}

	public int getOrder_pay_count() {
		return order_pay_count;
	}

	public void setOrder_pay_count(int order_pay_count) {
		this.order_pay_count = order_pay_count;
	}

	public int getOrder_ship_count() {
		return order_ship_count;
	}

	public void setOrder_ship_count(int order_ship_count) {
		this.order_ship_count = order_ship_count;
	}

	public float getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}

}
