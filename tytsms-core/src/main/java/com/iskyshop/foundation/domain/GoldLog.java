package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: GoldLog.java</p>

* <p>Description: 金币日志类，记录用户所有的金币日志记录信息，包括金币增加、金币扣除</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "gold_log")
public class GoldLog extends IdEntity {
	private int gl_type;// 类型，分为0增加、减少-1
	private int gl_money;// 支付金额
	private int gl_count;// 数量
	private String gl_payment;// 支付方式
	@Column(columnDefinition = "LongText")
	private String gl_content;// 说明
	@ManyToOne(fetch = FetchType.LAZY)
	private User gl_user;// 操作用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User gl_admin;// 审核管理员
	private Date gl_admin_time;// 审核时间
	@Column(columnDefinition = "LongText")
	private String gl_admin_content;// 审核意见
	@OneToOne(fetch = FetchType.LAZY)
	private GoldRecord gr;//金币充值记录管理

	public int getGl_type() {
		return gl_type;
	}

	public void setGl_type(int gl_type) {
		this.gl_type = gl_type;
	}

	public int getGl_count() {
		return gl_count;
	}

	public void setGl_count(int gl_count) {
		this.gl_count = gl_count;
	}

	public String getGl_content() {
		return gl_content;
	}

	public void setGl_content(String gl_content) {
		this.gl_content = gl_content;
	}

	public User getGl_user() {
		return gl_user;
	}

	public void setGl_user(User gl_user) {
		this.gl_user = gl_user;
	}

	public User getGl_admin() {
		return gl_admin;
	}

	public void setGl_admin(User gl_admin) {
		this.gl_admin = gl_admin;
	}

	public Date getGl_admin_time() {
		return gl_admin_time;
	}

	public void setGl_admin_time(Date gl_admin_time) {
		this.gl_admin_time = gl_admin_time;
	}

	public String getGl_admin_content() {
		return gl_admin_content;
	}

	public void setGl_admin_content(String gl_admin_content) {
		this.gl_admin_content = gl_admin_content;
	}

	public GoldRecord getGr() {
		return gr;
	}

	public void setGr(GoldRecord gr) {
		this.gr = gr;
	}

	public String getGl_payment() {
		return gl_payment;
	}

	public void setGl_payment(String gl_payment) {
		this.gl_payment = gl_payment;
	}

	public int getGl_money() {
		return gl_money;
	}

	public void setGl_money(int gl_money) {
		this.gl_money = gl_money;
	}

}
