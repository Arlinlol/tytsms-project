package com.iskyshop.foundation.domain;

import java.util.Date;

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
* <p>Title: Consult.java</p>

* <p>Description:产品咨询管理类,用来管理用户对卖家商品的咨询及卖家对咨询的回复 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "consult")
public class Consult extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;// 咨询对应的商品
	@Column(columnDefinition = "LongText")
	private String consult_content;// 咨询内容
	private boolean reply;// 是否回复
	@Column(columnDefinition = "LongText")
	private String consult_reply;// 回复内容
	@ManyToOne(fetch = FetchType.LAZY)
	private User consult_user;// 咨询用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User reply_user;// 回复用户
	private Date reply_time;// 回复时间
	private String consult_email;// 回复人email

	public boolean isReply() {
		return reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public String getConsult_email() {
		return consult_email;
	}

	public void setConsult_email(String consult_email) {
		this.consult_email = consult_email;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getConsult_content() {
		return consult_content;
	}

	public void setConsult_content(String consult_content) {
		this.consult_content = consult_content;
	}

	public String getConsult_reply() {
		return consult_reply;
	}

	public void setConsult_reply(String consult_reply) {
		this.consult_reply = consult_reply;
	}

	public User getConsult_user() {
		return consult_user;
	}

	public void setConsult_user(User consult_user) {
		this.consult_user = consult_user;
	}

	public User getReply_user() {
		return reply_user;
	}

	public void setReply_user(User reply_user) {
		this.reply_user = reply_user;
	}

	public Date getReply_time() {
		return reply_time;
	}

	public void setReply_time(Date reply_time) {
		this.reply_time = reply_time;
	}
}
