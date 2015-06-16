package com.iskyshop.foundation.domain;

import java.math.BigDecimal;

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
* <p>Title: RefundLog.java</p>

* <p>Description:退款日志类,用来记录店铺对买家的退款日志信息 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "refund_log")
public class RefundLog extends IdEntity {
	private String refund_id;// 退款编号
	private Long returnLog_id;// 对应退货服务单id 
	private String returnService_id;//对应退货服务单的服务号 ReturnGoodsLog.java private String return_service_id;
	private String returnLog_userName;//收款人用户名
	private Long returnLog_userId;//收款人id
	private String refund_log;// 日志信息
	private String refund_type;// 退款方式
	@Column(precision = 12, scale = 2)
	private BigDecimal refund;// 退款金额
	@ManyToOne(fetch = FetchType.LAZY)
	private User refund_user;// 日志操作员

	public Long getReturnLog_userId() {
		return returnLog_userId;
	}

	public void setReturnLog_userId(Long returnLog_userId) {
		this.returnLog_userId = returnLog_userId;
	}

	public String getReturnLog_userName() {
		return returnLog_userName;
	}

	public void setReturnLog_userName(String returnLog_userName) {
		this.returnLog_userName = returnLog_userName;
	}

	public Long getReturnLog_id() {
		return returnLog_id;
	}

	public void setReturnLog_id(Long returnLog_id) {
		this.returnLog_id = returnLog_id;
	}

	public String getReturnService_id() {
		return returnService_id;
	}

	public void setReturnService_id(String returnService_id) {
		this.returnService_id = returnService_id;
	}

	public String getRefund_log() {
		return refund_log;
	}

	public void setRefund_log(String refund_log) {
		this.refund_log = refund_log;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public User getRefund_user() {
		return refund_user;
	}

	public void setRefund_user(User refund_user) {
		this.refund_user = refund_user;
	}

	public String getRefund_type() {
		return refund_type;
	}

	public void setRefund_type(String refund_type) {
		this.refund_type = refund_type;
	}

	public String getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}
}
