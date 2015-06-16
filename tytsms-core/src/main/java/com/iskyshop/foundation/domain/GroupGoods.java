package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
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
 * Title: GroupGoods.java
 * </p>
 * 
 * <p>
 * Description: 团购商品管理控制类，用来管理团购商品信息
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_goods")
public class GroupGoods extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;// 对应的团购
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupClass gg_gc;// 团购类型
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupArea gg_ga;// 团购区域
	private String gg_name;// 团购商品名称
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods gg_goods;// 团购商品
	@Column(precision = 12, scale = 2)
	private BigDecimal gg_price;// 团购价格
	private int gg_count;// 团购商品数量
	private int gg_group_count;// 成团数量
	private int gg_def_count;// 当前团购数量
	private int gg_vir_count;// 虚拟团购数量
	private int gg_min_count;// 最小团购数
	private int gg_max_count;// 最大团购数
	@Column(columnDefinition = "int default 0")
	private int gg_selled_count;// 已经售出的数量
	@Column(precision = 12, scale = 2)
	private BigDecimal gg_rebate;// 团购折扣率
	private int gg_status;// 团购状态，0为待审核，1为审核通过，-1为审核拒绝 -2为过期
	private Date gg_audit_time;// 审核时间
	private boolean gg_recommend;// 推荐状态，0为为推荐，1为推荐，推荐团购在首页显示
	private Date gg_recommend_time;// 团购推荐时间
	@Column(columnDefinition = "LongText")
	private String gg_content;// 团购商品描述
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory gg_img;// 团购商品图片

	@Column(columnDefinition = "bit default false")
	private boolean weixin_shop_recommend;// 微信商城推荐，推荐后出现在微信商城（平台大商城）首页，
	@Temporal(TemporalType.DATE)
	private Date weixin_shop_recommendTime;// 微信商城推荐时间

	@Column(columnDefinition = "int default 0")
	private int mobile_recommend;// 手机客户端推荐， 1为推荐，推荐后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_recommendTime;// 手机推荐时间，

	public int getMobile_recommend() {
		return mobile_recommend;
	}

	public void setMobile_recommend(int mobile_recommend) {
		this.mobile_recommend = mobile_recommend;
	}

	public Date getMobile_recommendTime() {
		return mobile_recommendTime;
	}

	public void setMobile_recommendTime(Date mobile_recommendTime) {
		this.mobile_recommendTime = mobile_recommendTime;
	}

	public int getGg_selled_count() {
		return gg_selled_count;
	}

	public void setGg_selled_count(int gg_selled_count) {
		this.gg_selled_count = gg_selled_count;
	}

	public boolean isGg_recommend() {
		return gg_recommend;
	}

	public void setGg_recommend(boolean gg_recommend) {
		this.gg_recommend = gg_recommend;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isWeixin_shop_recommend() {
		return weixin_shop_recommend;
	}

	public void setWeixin_shop_recommend(boolean weixin_shop_recommend) {
		this.weixin_shop_recommend = weixin_shop_recommend;
	}

	public Date getWeixin_shop_recommendTime() {
		return weixin_shop_recommendTime;
	}

	public void setWeixin_shop_recommendTime(Date weixin_shop_recommendTime) {
		this.weixin_shop_recommendTime = weixin_shop_recommendTime;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getGg_name() {
		return gg_name;
	}

	public void setGg_name(String gg_name) {
		this.gg_name = gg_name;
	}

	public Goods getGg_goods() {
		return gg_goods;
	}

	public void setGg_goods(Goods gg_goods) {
		this.gg_goods = gg_goods;
	}

	public BigDecimal getGg_price() {
		return gg_price;
	}

	public void setGg_price(BigDecimal gg_price) {
		this.gg_price = gg_price;
	}

	public int getGg_group_count() {
		return gg_group_count;
	}

	public void setGg_group_count(int gg_group_count) {
		this.gg_group_count = gg_group_count;
	}

	public int getGg_def_count() {
		return gg_def_count;
	}

	public void setGg_def_count(int gg_def_count) {
		this.gg_def_count = gg_def_count;
	}

	public int getGg_min_count() {
		return gg_min_count;
	}

	public void setGg_min_count(int gg_min_count) {
		this.gg_min_count = gg_min_count;
	}

	public int getGg_max_count() {
		return gg_max_count;
	}

	public void setGg_max_count(int gg_max_count) {
		this.gg_max_count = gg_max_count;
	}

	public BigDecimal getGg_rebate() {
		return gg_rebate;
	}

	public void setGg_rebate(BigDecimal gg_rebate) {
		this.gg_rebate = gg_rebate;
	}

	public int getGg_status() {
		return gg_status;
	}

	public void setGg_status(int gg_status) {
		this.gg_status = gg_status;
	}

	public String getGg_content() {
		return gg_content;
	}

	public void setGg_content(String gg_content) {
		this.gg_content = gg_content;
	}

	public GroupClass getGg_gc() {
		return gg_gc;
	}

	public void setGg_gc(GroupClass gg_gc) {
		this.gg_gc = gg_gc;
	}

	public GroupArea getGg_ga() {
		return gg_ga;
	}

	public void setGg_ga(GroupArea gg_ga) {
		this.gg_ga = gg_ga;
	}

	public int getGg_count() {
		return gg_count;
	}

	public void setGg_count(int gg_count) {
		this.gg_count = gg_count;
	}

	public int getGg_vir_count() {
		return gg_vir_count;
	}

	public void setGg_vir_count(int gg_vir_count) {
		this.gg_vir_count = gg_vir_count;
	}

	public Accessory getGg_img() {
		return gg_img;
	}

	public void setGg_img(Accessory gg_img) {
		this.gg_img = gg_img;
	}

	public Date getGg_audit_time() {
		return gg_audit_time;
	}

	public void setGg_audit_time(Date gg_audit_time) {
		this.gg_audit_time = gg_audit_time;
	}

	public Date getGg_recommend_time() {
		return gg_recommend_time;
	}

	public void setGg_recommend_time(Date gg_recommend_time) {
		this.gg_recommend_time = gg_recommend_time;
	}
}
