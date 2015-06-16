package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: StorePoint.java</p>

* <p>Description: 店铺评分统计类,通过定时器类计算店铺评分信息，默认定时器每隔半小时计算一次</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_point")
public class StorePoint extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private Store store;// 对应的店铺
	@OneToOne(fetch = FetchType.LAZY)
	private User user;// 评价自营商品时，会评价点击确认发货的管理员
	private Date statTime;// 统计时间
	@Column(precision = 4, scale = 1)
	private BigDecimal store_evaluate1;// 店铺好评率
	@Column(precision = 4, scale = 1)
	private BigDecimal description_evaluate;// 描述相符评价
	@Column(precision = 4, scale = 1)
	private BigDecimal service_evaluate;// 服务态度评价
	@Column(precision = 4, scale = 1)
	private BigDecimal ship_evaluate;// 发货速度评价
	@Column(precision = 4, scale = 1)
	private BigDecimal description_evaluate_halfyear;// 店铺半年内描述相符评价
	@Column(precision = 4, scale = 1)
	private BigDecimal service_evaluate_halfyear;// 店铺半年内服务态度评价
	@Column(precision = 4, scale = 1)
	private BigDecimal ship_evaluate_halfyear;// 店铺半年内发货速度评价
	private int description_evaluate_halfyear_count5;// 半年内描述评价4-5分人数
	private int description_evaluate_halfyear_count4;// 半年内描述评价3-4分人数
	private int description_evaluate_halfyear_count3;// 半年内描述评价2-3分人数
	private int description_evaluate_halfyear_count2;// 半年内描述评价1-2分人数
	private int description_evaluate_halfyear_count1;// 半年内描述评价0-1分人数
	private int service_evaluate_halfyear_count5;// 半年内服务态度评价4-5分人数
	private int service_evaluate_halfyear_count4;// 半年内服务态度评价3-4分人数
	private int service_evaluate_halfyear_count3;// 半年内服务态度评价2-3分人数
	private int service_evaluate_halfyear_count2;// 半年内服务态度评价1-2分人数
	private int service_evaluate_halfyear_count1;// 半年内服务态度评价0-1分人数
	private int ship_evaluate_halfyear_count5;// 半年内发货速度评价4-5分人数
	private int ship_evaluate_halfyear_count4;// 半年内发货速度评价3-4分人数
	private int ship_evaluate_halfyear_count3;// 半年内发货速度评价2-3分人数
	private int ship_evaluate_halfyear_count2;// 半年内发货速度评价1-2分人数
	private int ship_evaluate_halfyear_count1;// 半年内发货速度评价0-1分人数

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getDescription_evaluate_halfyear() {
		return description_evaluate_halfyear;
	}

	public void setDescription_evaluate_halfyear(
			BigDecimal description_evaluate_halfyear) {
		this.description_evaluate_halfyear = description_evaluate_halfyear;
	}

	public BigDecimal getService_evaluate_halfyear() {
		return service_evaluate_halfyear;
	}

	public void setService_evaluate_halfyear(
			BigDecimal service_evaluate_halfyear) {
		this.service_evaluate_halfyear = service_evaluate_halfyear;
	}

	public BigDecimal getShip_evaluate_halfyear() {
		return ship_evaluate_halfyear;
	}

	public void setShip_evaluate_halfyear(BigDecimal ship_evaluate_halfyear) {
		this.ship_evaluate_halfyear = ship_evaluate_halfyear;
	}

	public int getDescription_evaluate_halfyear_count5() {
		return description_evaluate_halfyear_count5;
	}

	public void setDescription_evaluate_halfyear_count5(
			int description_evaluate_halfyear_count5) {
		this.description_evaluate_halfyear_count5 = description_evaluate_halfyear_count5;
	}

	public int getDescription_evaluate_halfyear_count4() {
		return description_evaluate_halfyear_count4;
	}

	public void setDescription_evaluate_halfyear_count4(
			int description_evaluate_halfyear_count4) {
		this.description_evaluate_halfyear_count4 = description_evaluate_halfyear_count4;
	}

	public int getDescription_evaluate_halfyear_count3() {
		return description_evaluate_halfyear_count3;
	}

	public void setDescription_evaluate_halfyear_count3(
			int description_evaluate_halfyear_count3) {
		this.description_evaluate_halfyear_count3 = description_evaluate_halfyear_count3;
	}

	public int getDescription_evaluate_halfyear_count2() {
		return description_evaluate_halfyear_count2;
	}

	public void setDescription_evaluate_halfyear_count2(
			int description_evaluate_halfyear_count2) {
		this.description_evaluate_halfyear_count2 = description_evaluate_halfyear_count2;
	}

	public int getDescription_evaluate_halfyear_count1() {
		return description_evaluate_halfyear_count1;
	}

	public void setDescription_evaluate_halfyear_count1(
			int description_evaluate_halfyear_count1) {
		this.description_evaluate_halfyear_count1 = description_evaluate_halfyear_count1;
	}

	public int getService_evaluate_halfyear_count5() {
		return service_evaluate_halfyear_count5;
	}

	public void setService_evaluate_halfyear_count5(
			int service_evaluate_halfyear_count5) {
		this.service_evaluate_halfyear_count5 = service_evaluate_halfyear_count5;
	}

	public int getService_evaluate_halfyear_count4() {
		return service_evaluate_halfyear_count4;
	}

	public void setService_evaluate_halfyear_count4(
			int service_evaluate_halfyear_count4) {
		this.service_evaluate_halfyear_count4 = service_evaluate_halfyear_count4;
	}

	public int getService_evaluate_halfyear_count3() {
		return service_evaluate_halfyear_count3;
	}

	public void setService_evaluate_halfyear_count3(
			int service_evaluate_halfyear_count3) {
		this.service_evaluate_halfyear_count3 = service_evaluate_halfyear_count3;
	}

	public int getService_evaluate_halfyear_count2() {
		return service_evaluate_halfyear_count2;
	}

	public void setService_evaluate_halfyear_count2(
			int service_evaluate_halfyear_count2) {
		this.service_evaluate_halfyear_count2 = service_evaluate_halfyear_count2;
	}

	public int getService_evaluate_halfyear_count1() {
		return service_evaluate_halfyear_count1;
	}

	public void setService_evaluate_halfyear_count1(
			int service_evaluate_halfyear_count1) {
		this.service_evaluate_halfyear_count1 = service_evaluate_halfyear_count1;
	}

	public int getShip_evaluate_halfyear_count5() {
		return ship_evaluate_halfyear_count5;
	}

	public void setShip_evaluate_halfyear_count5(
			int ship_evaluate_halfyear_count5) {
		this.ship_evaluate_halfyear_count5 = ship_evaluate_halfyear_count5;
	}

	public int getShip_evaluate_halfyear_count4() {
		return ship_evaluate_halfyear_count4;
	}

	public void setShip_evaluate_halfyear_count4(
			int ship_evaluate_halfyear_count4) {
		this.ship_evaluate_halfyear_count4 = ship_evaluate_halfyear_count4;
	}

	public int getShip_evaluate_halfyear_count3() {
		return ship_evaluate_halfyear_count3;
	}

	public void setShip_evaluate_halfyear_count3(
			int ship_evaluate_halfyear_count3) {
		this.ship_evaluate_halfyear_count3 = ship_evaluate_halfyear_count3;
	}

	public int getShip_evaluate_halfyear_count2() {
		return ship_evaluate_halfyear_count2;
	}

	public void setShip_evaluate_halfyear_count2(
			int ship_evaluate_halfyear_count2) {
		this.ship_evaluate_halfyear_count2 = ship_evaluate_halfyear_count2;
	}

	public int getShip_evaluate_halfyear_count1() {
		return ship_evaluate_halfyear_count1;
	}

	public void setShip_evaluate_halfyear_count1(
			int ship_evaluate_halfyear_count1) {
		this.ship_evaluate_halfyear_count1 = ship_evaluate_halfyear_count1;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public BigDecimal getStore_evaluate1() {
		return store_evaluate1;
	}

	public void setStore_evaluate1(BigDecimal store_evaluate1) {
		this.store_evaluate1 = store_evaluate1;
	}

	public BigDecimal getDescription_evaluate() {
		return description_evaluate;
	}

	public void setDescription_evaluate(BigDecimal description_evaluate) {
		this.description_evaluate = description_evaluate;
	}

	public BigDecimal getService_evaluate() {
		return service_evaluate;
	}

	public void setService_evaluate(BigDecimal service_evaluate) {
		this.service_evaluate = service_evaluate;
	}

	public BigDecimal getShip_evaluate() {
		return ship_evaluate;
	}

	public void setShip_evaluate(BigDecimal ship_evaluate) {
		this.ship_evaluate = ship_evaluate;
	}

	public Date getStatTime() {
		return statTime;
	}

	public void setStatTime(Date statTime) {
		this.statTime = statTime;
	}
}
