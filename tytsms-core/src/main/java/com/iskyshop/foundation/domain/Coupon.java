package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Coupon.java
 * </p>
 * 
 * <p>
 * Description: 系统优惠券，优惠券分为平台优惠券和商家优惠券，
 * 平台优惠券由管理员发放，抵消订单中的自营商品价格
 * 商家优惠券由商家发放，抵消订单中该商家的商品价格
 * 买家确认订单时只查询该订单中所有商品店铺发放的优惠券，若订单中存在自营商品将查询出平台优惠券
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
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "coupon")
public class Coupon extends IdEntity {
	private String coupon_name;// 优惠券名称
	@Column(precision = 12, scale = 0)
	private BigDecimal coupon_amount;// 优惠券金额
	@Temporal(TemporalType.DATE)
	private Date coupon_begin_time;// 优惠券使用开始时间
	@Temporal(TemporalType.DATE)
	private Date coupon_end_time;// 优惠券使用结束时间
	private int coupon_count;// 优惠券发行数量
	@Column(precision = 12, scale = 0)
	private BigDecimal coupon_order_amount;// 优惠券使用的订单金额，订单满足该金额时才可以使用该优惠券
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory coupon_acc;// 优惠券图片
	@OneToMany(mappedBy = "coupon", cascade = CascadeType.REMOVE)
	private List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();
	@Column(columnDefinition = "int default 0")
	private int coupon_type;// 优惠券类型，0为平台优惠券，抵消自营商品订单金额，1为商家优惠券，抵消订单中该商家商品部分金额

	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 当优惠券类型为商家优惠券时对应的商家店铺

	public int getCoupon_type() {
		return coupon_type;
	}

	public void setCoupon_type(int coupon_type) {
		this.coupon_type = coupon_type;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getCoupon_name() {
		return coupon_name;
	}

	public void setCoupon_name(String coupon_name) {
		this.coupon_name = coupon_name;
	}

	public BigDecimal getCoupon_amount() {
		return coupon_amount;
	}

	public void setCoupon_amount(BigDecimal coupon_amount) {
		this.coupon_amount = coupon_amount;
	}

	public Date getCoupon_begin_time() {
		return coupon_begin_time;
	}

	public void setCoupon_begin_time(Date coupon_begin_time) {
		this.coupon_begin_time = coupon_begin_time;
	}

	public Date getCoupon_end_time() {
		return coupon_end_time;
	}

	public void setCoupon_end_time(Date coupon_end_time) {
		this.coupon_end_time = coupon_end_time;
	}

	public int getCoupon_count() {
		return coupon_count;
	}

	public void setCoupon_count(int coupon_count) {
		this.coupon_count = coupon_count;
	}

	public BigDecimal getCoupon_order_amount() {
		return coupon_order_amount;
	}

	public void setCoupon_order_amount(BigDecimal coupon_order_amount) {
		this.coupon_order_amount = coupon_order_amount;
	}

	public Accessory getCoupon_acc() {
		return coupon_acc;
	}

	public void setCoupon_acc(Accessory coupon_acc) {
		this.coupon_acc = coupon_acc;
	}

	public List<CouponInfo> getCouponinfos() {
		return couponinfos;
	}

	public void setCouponinfos(List<CouponInfo> couponinfos) {
		this.couponinfos = couponinfos;
	}

}
