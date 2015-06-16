package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Report.java
 * </p>
 * 
 * <p>
 * Description:
 * 举报类，记录所有商品举报信息，举报信息超级管理可以看到，管理员根据举报情况进行相关处理，可以将举报商品下架，如果是恶意举报也可以将该举报用户禁止举报
 * ，禁止后可以在用户管理中恢复用户举报功能
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "report")
public class Report extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 举报人
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;// 被举报商品
	private int status;// 举报状态，0为未处理，1为已经处理,-1为取消状态
	@ManyToOne(fetch = FetchType.LAZY)
	private ReportSubject subject;// 举报主题
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory acc1;// 举报图片1
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory acc2;// 举报图片2
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory acc3;// 举报图片3
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;// 举报内容
	private int result;// 处理结果 -1为无效举报，1为有效举报,-2为恶意举报
	@Lob
	@Column(columnDefinition = "LongText")
	private String handle_info;// 处理意见
	private Date handle_Time;// 处理时间

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public Date getHandle_Time() {
		return handle_Time;
	}

	public void setHandle_Time(Date handle_Time) {
		this.handle_Time = handle_Time;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ReportSubject getSubject() {
		return subject;
	}

	public void setSubject(ReportSubject subject) {
		this.subject = subject;
	}

	public Accessory getAcc1() {
		return acc1;
	}

	public void setAcc1(Accessory acc1) {
		this.acc1 = acc1;
	}

	public Accessory getAcc2() {
		return acc2;
	}

	public void setAcc2(Accessory acc2) {
		this.acc2 = acc2;
	}

	public Accessory getAcc3() {
		return acc3;
	}

	public void setAcc3(Accessory acc3) {
		this.acc3 = acc3;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHandle_info() {
		return handle_info;
	}

	public void setHandle_info(String handle_info) {
		this.handle_info = handle_info;
	}
}
