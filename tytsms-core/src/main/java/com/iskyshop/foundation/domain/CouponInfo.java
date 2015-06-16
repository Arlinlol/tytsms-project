package com.iskyshop.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: CouponInfo.java
 * </p>
 * 
 * <p>
 * Description:系统优惠券详情类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "coupon_info")
public class CouponInfo extends IdEntity {
	private String coupon_sn;// 优惠券编号
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 优惠券拥有的用户
	@ManyToOne(fetch = FetchType.LAZY)
	private Coupon coupon;// 对应的优惠券信息
	@Column(columnDefinition = "int default 0")
	private int status;// 优惠券信息状态，默认为0，,使用后为1,-1为过期
    private String remark; //备注
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCoupon_sn() {
		return coupon_sn;
	}

	public void setCoupon_sn(String coupon_sn) {
		this.coupon_sn = coupon_sn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
