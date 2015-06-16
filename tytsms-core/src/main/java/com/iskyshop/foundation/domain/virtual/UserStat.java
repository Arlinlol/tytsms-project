package com.iskyshop.foundation.domain.virtual;

import java.util.Date;

/**
 * 
 * <p>
 * Title: UserStat.java
 * </p>
 * 
 * <p>
 * Description:用户统计管理类，该类仅仅为了前端实时统计显示，不和数据表关联，不保存到数据库
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
 * @date 2014-6-4
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class UserStat {
	private Date stat_time;// 统计日期
	private int user_count;// 用户总数
	private int user_increase_count;// 当日新增用户数
	private int user_active_count;// 当日活跃用户数
	private int user_order_count;// 当日总共下单用户数
	private int user_day_order_count;// 当日注册即下单用户数

	public Date getStat_time() {
		return stat_time;
	}

	public void setStat_time(Date stat_time) {
		this.stat_time = stat_time;
	}

	public int getUser_count() {
		return user_count;
	}

	public void setUser_count(int user_count) {
		this.user_count = user_count;
	}

	public int getUser_increase_count() {
		return user_increase_count;
	}

	public void setUser_increase_count(int user_increase_count) {
		this.user_increase_count = user_increase_count;
	}

	public int getUser_active_count() {
		return user_active_count;
	}

	public void setUser_active_count(int user_active_count) {
		this.user_active_count = user_active_count;
	}

	public int getUser_order_count() {
		return user_order_count;
	}

	public void setUser_order_count(int user_order_count) {
		this.user_order_count = user_order_count;
	}

	public int getUser_day_order_count() {
		return user_day_order_count;
	}

	public void setUser_day_order_count(int user_day_order_count) {
		this.user_day_order_count = user_day_order_count;
	}

}
