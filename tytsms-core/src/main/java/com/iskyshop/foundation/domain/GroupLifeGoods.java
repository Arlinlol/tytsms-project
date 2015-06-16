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
 * Title: GroupLifeGoods.java
 * </p>
 * 
 * <p>
 * Description: 生活类团购模型，电影票，使用消费码类的团购发布时候相当于发布一个新商品
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
 * @author jinxinzhe
 * 
 * @date 2014-5-19
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_lifegoods")
public class GroupLifeGoods extends IdEntity {
	private String gg_name;// 商品名称
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;// 对应的团购
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupClass gg_gc;// 团购类别 例如：电影
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupArea gg_ga;// 团购区域
	@Column(columnDefinition = "LongText")
	private String group_details; // 团购详情
	@Column(columnDefinition = "int default 0")
	private int group_status; // 团购审核状态 1为通过 -1为未通过 0为待审核 -2为过期
	@Column(precision = 12, scale = 2)
	private BigDecimal group_price;// 团购商品价格
	@OneToOne
	private Accessory group_acc;// 团购图片
	@Column(precision = 12, scale = 2)
	private BigDecimal cost_price;// 团购商品的原价
	@ManyToOne
	private User user; // 团购发放者
	@OneToMany(mappedBy = "lifeGoods", cascade = CascadeType.REMOVE)
	private List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();// 团购消费码类
	private int group_count; // 团购数量;
	@Column(columnDefinition = "int default 0")
	private int selled_count;// 已经售出的数量
	private boolean group_recommend; // 是否进行推荐，推荐后在团购商品详细页右侧的推荐栏显示
	@Column(columnDefinition = "int default 0")
	private int goods_type;// 默认为0商家商品类团购商品 1为自营生活类团购商品
	@Column(precision = 12, scale = 2)
	private BigDecimal gg_rebate;// 团购折扣率

	@Column(columnDefinition = "bit default false")
	private boolean weixin_shop_recommend;// 微信商城推荐，推荐后出现在微信商城（平台大商城）首页，
	@Temporal(TemporalType.DATE)
	private Date weixin_shop_recommendTime;// 微信商城推荐时间 这两个字段还没做，以后能用

	public int getSelled_count() {
		return selled_count;
	}

	public void setSelled_count(int selled_count) {
		this.selled_count = selled_count;
	}

	public BigDecimal getGg_rebate() {
		return gg_rebate;
	}

	public void setGg_rebate(BigDecimal gg_rebate) {
		this.gg_rebate = gg_rebate;
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

	public String getGg_name() {
		return gg_name;
	}

	public void setGg_name(String gg_name) {
		this.gg_name = gg_name;
	}

	public int getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
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

	public String getGroup_details() {
		return group_details;
	}

	public void setGroup_details(String group_details) {
		this.group_details = group_details;
	}

	public int getGroup_status() {
		return group_status;
	}

	public void setGroup_status(int group_status) {
		this.group_status = group_status;
	}

	public BigDecimal getGroup_price() {
		return group_price;
	}

	public void setGroup_price(BigDecimal group_price) {
		this.group_price = group_price;
	}

	public Accessory getGroup_acc() {
		return group_acc;
	}

	public void setGroup_acc(Accessory group_acc) {
		this.group_acc = group_acc;
	}

	public BigDecimal getCost_price() {
		return cost_price;
	}

	public void setCost_price(BigDecimal cost_price) {
		this.cost_price = cost_price;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<GroupInfo> getGroupInfos() {
		return groupInfos;
	}

	public void setGroupInfos(List<GroupInfo> groupInfos) {
		this.groupInfos = groupInfos;
	}

	public int getGroup_count() {
		return group_count;
	}

	public void setGroup_count(int group_count) {
		this.group_count = group_count;
	}

	public boolean isGroup_recommend() {
		return group_recommend;
	}

	public void setGroup_recommend(boolean group_recommend) {
		this.group_recommend = group_recommend;
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

}
