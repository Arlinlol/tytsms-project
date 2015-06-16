package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.annotation.Lock;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: Advert.java</p>

* <p>Description: 商城广告管理类，用来管理商城广告信息，商城广告使用js调用管理，目前支持幻灯广告、滚动广告、图片广告、文字广告，可以在商城任意位置调用</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "advert")
public class Advert extends IdEntity {
	private String ad_title;// 广告名称
	@Lock
	@Temporal(TemporalType.DATE)
	private Date ad_begin_time;// 开始时间
	@Lock
	@Temporal(TemporalType.DATE)
	private Date ad_end_time;// 结束时间
	@ManyToOne(fetch = FetchType.LAZY)
	private AdvertPosition ad_ap;
	@Lock
	private int ad_status;// 广告状态,0为待审核，1为审核通过,-1未审核失败
	@OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Accessory ad_acc;// 广告图片
	private String ad_text;// 广告文字
	private int ad_slide_sequence;// 幻灯图片排序，按照升序
	@ManyToOne(fetch = FetchType.LAZY)
	private User ad_user;// 广告投放人
	private String ad_url;// 广告链接
	private int ad_click_num;// 广告点击数
	private int ad_gold;// 广告消耗金币数

	public int getAd_gold() {
		return ad_gold;
	}

	public void setAd_gold(int ad_gold) {
		this.ad_gold = ad_gold;
	}

	public int getAd_status() {
		return ad_status;
	}

	public void setAd_status(int ad_status) {
		this.ad_status = ad_status;
	}

	public String getAd_title() {
		return ad_title;
	}

	public void setAd_title(String ad_title) {
		this.ad_title = ad_title;
	}

	public Date getAd_begin_time() {
		return ad_begin_time;
	}

	public void setAd_begin_time(Date ad_begin_time) {
		this.ad_begin_time = ad_begin_time;
	}

	public Date getAd_end_time() {
		return ad_end_time;
	}

	public void setAd_end_time(Date ad_end_time) {
		this.ad_end_time = ad_end_time;
	}

	public AdvertPosition getAd_ap() {
		return ad_ap;
	}

	public void setAd_ap(AdvertPosition ad_ap) {
		this.ad_ap = ad_ap;
	}

	public int getAd_click_num() {
		return ad_click_num;
	}

	public void setAd_click_num(int ad_click_num) {
		this.ad_click_num = ad_click_num;
	}

	public Accessory getAd_acc() {
		return ad_acc;
	}

	public void setAd_acc(Accessory ad_acc) {
		this.ad_acc = ad_acc;
	}

	public User getAd_user() {
		return ad_user;
	}

	public void setAd_user(User ad_user) {
		this.ad_user = ad_user;
	}

	public String getAd_text() {
		return ad_text;
	}

	public void setAd_text(String ad_text) {
		this.ad_text = ad_text;
	}

	public String getAd_url() {
		return ad_url;
	}

	public void setAd_url(String ad_url) {
		this.ad_url = ad_url;
	}

	public int getAd_slide_sequence() {
		return ad_slide_sequence;
	}

	public void setAd_slide_sequence(int ad_slide_sequence) {
		this.ad_slide_sequence = ad_slide_sequence;
	}
}
