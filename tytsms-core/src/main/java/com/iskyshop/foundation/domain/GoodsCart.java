package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * <p>
 * Title: GoodsCart.java
 * </p>
 * <p>
 * Description: * 商城购物车类， ，购物车信息直接保存到数据库中
 * ，未登录用户根据随机唯一Id保存（包括手机端），已经登录的用户根据User来保存
 * ，未登录用户购物车间隔1天自动删除（包括手机端），已经登录用户购物车保存7天 ，7天未提交为订单自动删除,购物车信息存在及时性，不加入缓存管理
 * 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author hezeng
 * 
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodscart")
public class GoodsCart extends IdEntity {
	@ManyToOne
	private Goods goods;// 对应的商品
	private int count;// 数量
	@Column(precision = 12, scale = 2)
	private BigDecimal price;// 价格
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "cart_gsp", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "gsp_id"))
	private List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();// 对应的规格
	@Lob
	@Column(columnDefinition = "LongText")
	private String spec_info;// 规格内容
	private String cart_type;// 默认为空，组合销售时候为"combin"
	private String cart_gsp;// 购物车中商品规格id
	@ManyToOne
	private User user;// 对应的购物车用户
	private String cart_session_id;// 未登录用户会话Id
	private String cart_mobile_id;// 手机端未登录用户会话Id
	@Column(columnDefinition = "int default 0")
	private int cart_status;// 用户购物车状态，0表示没有提交为订单，1表示已经提交为订单，已经提交为订单信息的不再为缓存购物车，同时定时器也不进行删除操作

	public String getCart_gsp() {
		return cart_gsp;
	}

	public void setCart_gsp(String cart_gsp) {
		this.cart_gsp = cart_gsp;
	}

	public String getCart_mobile_id() {
		return cart_mobile_id;
	}

	public void setCart_mobile_id(String cart_mobile_id) {
		this.cart_mobile_id = cart_mobile_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCart_session_id() {
		return cart_session_id;
	}

	public void setCart_session_id(String cart_session_id) {
		this.cart_session_id = cart_session_id;
	}

	public int getCart_status() {
		return cart_status;
	}

	public void setCart_status(int cart_status) {
		this.cart_status = cart_status;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<GoodsSpecProperty> getGsps() {
		return gsps;
	}

	public void setGsps(List<GoodsSpecProperty> gsps) {
		this.gsps = gsps;
	}

	public String getSpec_info() {
		return spec_info;
	}

	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCart_type() {
		return cart_type;
	}

	public void setCart_type(String cart_type) {
		this.cart_type = cart_type;
	}
}
