package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: IntegralGoods.java</p>

* <p>Description:积分商城礼品 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integral_goods")
public class IntegralGoods extends IdEntity {
	private String ig_goods_name;// 礼品名称
	@Column(precision = 12, scale = 2)
	private BigDecimal ig_goods_price;// 礼品原价
	private int ig_goods_integral;// 礼品兑换积分
	private int ig_user_Level;//礼品兑换所需的用户等级，0—铜牌1—银牌2—金牌3—超级
	private String ig_goods_sn;// 礼品编号
	private int ig_goods_count;// 礼品库存数量
	private String ig_goods_tag;// 礼品标签
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory ig_goods_img;// 礼品图片
	private boolean ig_limit_type;// 是否限制用户兑换数量
	private int ig_limit_count;// 限制兑换数量
	private int ig_transfee_type;// 运费承担方式，0为卖家承担，1为买家承担
	@Column(precision = 12, scale = 2)
	private BigDecimal ig_transfee;// 运费
	private boolean ig_time_type;// 是否限制兑换时间
	private Date ig_begin_time;// 兑换开始时间
	private Date ig_end_time;// 兑换结束时间
	private boolean ig_show;// 是否上架
	private boolean ig_recommend;// 是否推荐
	private int ig_sequence;// 礼品序号，按照升序排序
	private String ig_seo_keywords;// 礼品seo关键字
	private String ig_seo_description;// 礼品seo描述
	@Lob
	@Column(columnDefinition = "LongText")
	private String ig_content;// 礼品详情
	private int ig_exchange_count;// 礼品兑出数量
	private int ig_click_count;// 礼品点击次数
	@OneToMany(mappedBy = "goods", cascade = CascadeType.REMOVE)
	private List<IntegralGoodsCart> gcs = new ArrayList<IntegralGoodsCart>();// 对应的积分兑换购物车

	
	public int getIg_user_Level() {
		return ig_user_Level;
	}

	public void setIg_user_Level(int ig_user_Level) {
		this.ig_user_Level = ig_user_Level;
	}

	public List<IntegralGoodsCart> getGcs() {
		return gcs;
	}

	public void setGcs(List<IntegralGoodsCart> gcs) {
		this.gcs = gcs;
	}

	public String getIg_goods_name() {
		return ig_goods_name;
	}

	public void setIg_goods_name(String ig_goods_name) {
		this.ig_goods_name = ig_goods_name;
	}

	public BigDecimal getIg_goods_price() {
		return ig_goods_price;
	}

	public void setIg_goods_price(BigDecimal ig_goods_price) {
		this.ig_goods_price = ig_goods_price;
	}

	public int getIg_goods_integral() {
		return ig_goods_integral;
	}

	public void setIg_goods_integral(int ig_goods_integral) {
		this.ig_goods_integral = ig_goods_integral;
	}

	public String getIg_goods_sn() {
		return ig_goods_sn;
	}

	public void setIg_goods_sn(String ig_goods_sn) {
		this.ig_goods_sn = ig_goods_sn;
	}

	public int getIg_goods_count() {
		return ig_goods_count;
	}

	public void setIg_goods_count(int ig_goods_count) {
		this.ig_goods_count = ig_goods_count;
	}

	public String getIg_goods_tag() {
		return ig_goods_tag;
	}

	public void setIg_goods_tag(String ig_goods_tag) {
		this.ig_goods_tag = ig_goods_tag;
	}

	public Accessory getIg_goods_img() {
		return ig_goods_img;
	}

	public void setIg_goods_img(Accessory ig_goods_img) {
		this.ig_goods_img = ig_goods_img;
	}

	public boolean isIg_limit_type() {
		return ig_limit_type;
	}

	public void setIg_limit_type(boolean ig_limit_type) {
		this.ig_limit_type = ig_limit_type;
	}

	public int getIg_limit_count() {
		return ig_limit_count;
	}

	public void setIg_limit_count(int ig_limit_count) {
		this.ig_limit_count = ig_limit_count;
	}

	public int getIg_transfee_type() {
		return ig_transfee_type;
	}

	public void setIg_transfee_type(int ig_transfee_type) {
		this.ig_transfee_type = ig_transfee_type;
	}

	public BigDecimal getIg_transfee() {
		return ig_transfee;
	}

	public void setIg_transfee(BigDecimal ig_transfee) {
		this.ig_transfee = ig_transfee;
	}

	public boolean isIg_time_type() {
		return ig_time_type;
	}

	public void setIg_time_type(boolean ig_time_type) {
		this.ig_time_type = ig_time_type;
	}

	public Date getIg_begin_time() {
		return ig_begin_time;
	}

	public void setIg_begin_time(Date ig_begin_time) {
		this.ig_begin_time = ig_begin_time;
	}

	public Date getIg_end_time() {
		return ig_end_time;
	}

	public void setIg_end_time(Date ig_end_time) {
		this.ig_end_time = ig_end_time;
	}

	public boolean isIg_show() {
		return ig_show;
	}

	public void setIg_show(boolean ig_show) {
		this.ig_show = ig_show;
	}

	public boolean isIg_recommend() {
		return ig_recommend;
	}

	public void setIg_recommend(boolean ig_recommend) {
		this.ig_recommend = ig_recommend;
	}

	public int getIg_sequence() {
		return ig_sequence;
	}

	public void setIg_sequence(int ig_sequence) {
		this.ig_sequence = ig_sequence;
	}

	public String getIg_seo_keywords() {
		return ig_seo_keywords;
	}

	public void setIg_seo_keywords(String ig_seo_keywords) {
		this.ig_seo_keywords = ig_seo_keywords;
	}

	public String getIg_seo_description() {
		return ig_seo_description;
	}

	public void setIg_seo_description(String ig_seo_description) {
		this.ig_seo_description = ig_seo_description;
	}

	public String getIg_content() {
		return ig_content;
	}

	public void setIg_content(String ig_content) {
		this.ig_content = ig_content;
	}

	public int getIg_exchange_count() {
		return ig_exchange_count;
	}

	public void setIg_exchange_count(int ig_exchange_count) {
		this.ig_exchange_count = ig_exchange_count;
	}

	public int getIg_click_count() {
		return ig_click_count;
	}

	public void setIg_click_count(int ig_click_count) {
		this.ig_click_count = ig_click_count;
	}

}
