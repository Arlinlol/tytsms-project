package com.iskyshop.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: IntegralLog.java</p>

* <p>Description:用户积分操作日志记录 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integrallog")
public class IntegralLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User integral_user;// 积分用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User operate_user;// 操作用户
	private int integral;// 操作积分数
	private String type;// 操作类型，包括reg：注册赠送，system：管理员操作,login:用户登录,order:订单获得,integral_order:积分兑换,inviteRegister:邀请用户注册,award:抽奖获得,share:分享获得
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;// 操作说明

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getIntegral_user() {
		return integral_user;
	}

	public void setIntegral_user(User integral_user) {
		this.integral_user = integral_user;
	}

	public User getOperate_user() {
		return operate_user;
	}

	public void setOperate_user(User operate_user) {
		this.operate_user = operate_user;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
