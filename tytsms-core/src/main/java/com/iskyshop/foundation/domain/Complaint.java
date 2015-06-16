package com.iskyshop.foundation.domain;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: Complaint.java</p>

* <p>Description:投诉管理类,管理系统投诉 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "complaint")
public class Complaint extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User from_user;// 投诉者
	@ManyToOne(fetch = FetchType.LAZY)
	private User to_user;// 被投诉
	private String type;// buyer为买家投诉，seller为卖家投诉
	private int status;// 投诉状态，0为新投诉，1为待申诉，2为沟通中，3为待仲裁，4投诉结束
	@OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
	private List<ComplaintGoods> cgs = new ArrayList<ComplaintGoods>();// 投诉商品
	@ManyToOne(fetch = FetchType.LAZY)
	private ComplaintSubject cs;// 投诉主题
	@Column(columnDefinition = "LongText")
	private String from_user_content;// 投诉人意见
	@Column(columnDefinition = "LongText")
	private String to_user_content;// 被投诉人意见
	private Date appeal_time;// 申诉时间
	@Column(columnDefinition = "LongText")
	private String handle_content;// 仲裁意见
	private Date handle_time;// 仲裁时间
	@ManyToOne(fetch = FetchType.LAZY)
	private User handle_user;// 仲裁管理员
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory from_acc1;// 投诉人取证图片1
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory from_acc2;// 投诉人取证图片2
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory from_acc3;// 投诉人取证图片3

	@OneToOne(fetch = FetchType.LAZY)
	private Accessory to_acc1;// 被投诉人申诉图片1
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory to_acc2;// 被投诉人申诉图片2
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory to_acc3;// 被投诉人申诉图片3
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;// 投诉对应的订单
	@Column(columnDefinition = "LongText")
	private String talk_content;// 对话记录，使用分行管理

	public OrderForm getOf() {
		return of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public User getFrom_user() {
		return from_user;
	}

	public void setFrom_user(User from_user) {
		this.from_user = from_user;
	}

	public User getTo_user() {
		return to_user;
	}

	public void setTo_user(User to_user) {
		this.to_user = to_user;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ComplaintSubject getCs() {
		return cs;
	}

	public void setCs(ComplaintSubject cs) {
		this.cs = cs;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ComplaintGoods> getCgs() {
		return cgs;
	}

	public void setCgs(List<ComplaintGoods> cgs) {
		this.cgs = cgs;
	}

	public String getFrom_user_content() {
		return from_user_content;
	}

	public void setFrom_user_content(String from_user_content) {
		this.from_user_content = from_user_content;
	}

	public String getTo_user_content() {
		return to_user_content;
	}

	public void setTo_user_content(String to_user_content) {
		this.to_user_content = to_user_content;
	}

	public Accessory getFrom_acc1() {
		return from_acc1;
	}

	public void setFrom_acc1(Accessory from_acc1) {
		this.from_acc1 = from_acc1;
	}

	public Accessory getFrom_acc2() {
		return from_acc2;
	}

	public void setFrom_acc2(Accessory from_acc2) {
		this.from_acc2 = from_acc2;
	}

	public Accessory getFrom_acc3() {
		return from_acc3;
	}

	public void setFrom_acc3(Accessory from_acc3) {
		this.from_acc3 = from_acc3;
	}

	public Accessory getTo_acc1() {
		return to_acc1;
	}

	public void setTo_acc1(Accessory to_acc1) {
		this.to_acc1 = to_acc1;
	}

	public Accessory getTo_acc2() {
		return to_acc2;
	}

	public void setTo_acc2(Accessory to_acc2) {
		this.to_acc2 = to_acc2;
	}

	public Accessory getTo_acc3() {
		return to_acc3;
	}

	public void setTo_acc3(Accessory to_acc3) {
		this.to_acc3 = to_acc3;
	}

	public String getHandle_content() {
		return handle_content;
	}

	public void setHandle_content(String handle_content) {
		this.handle_content = handle_content;
	}

	public Date getHandle_time() {
		return handle_time;
	}

	public void setHandle_time(Date handle_time) {
		this.handle_time = handle_time;
	}

	public Date getAppeal_time() {
		return appeal_time;
	}

	public void setAppeal_time(Date appeal_time) {
		this.appeal_time = appeal_time;
	}

	public String getTalk_content() {
		return talk_content;
	}

	public void setTalk_content(String talk_content) {
		this.talk_content = talk_content;
	}

	public User getHandle_user() {
		return handle_user;
	}

	public void setHandle_user(User handle_user) {
		this.handle_user = handle_user;
	}

}
