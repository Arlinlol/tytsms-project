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
* <p>Title: Predeposit.java</p>

* <p>Description: 预存款充值管理类</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "predeposit")
public class Predeposit extends IdEntity {
	private String pd_no;// 交易流水号,在线支付时每次随机生成唯一的号码，重复提交时替换当前订单的交易流水号
	@ManyToOne(fetch = FetchType.LAZY)
	private User pd_user;// 充值会员
	@Column(precision = 12, scale = 2)
	private BigDecimal pd_amount;// 充值金额
	private String pd_sn;// 充值唯一编号记录，使用pd为前缀
	private String pd_payment;// 充值方式
	private String pd_remittance_user;// 汇款人姓名
	private String pd_remittance_bank;// 汇款银行
	@Temporal(TemporalType.DATE)
	private Date pd_remittance_time;// 汇款日期
	@Column(columnDefinition = "LongText")
	private String pd_remittance_info;// 汇款备注
	@ManyToOne(fetch = FetchType.LAZY)
	private User pd_admin;// 充值请求处理的管理员
	@Column(columnDefinition = "LongText")
	private String pd_admin_info;// 请求处理备注
	private int pd_status;// 预存款状态，0为未完成，1为成功，-1已关闭
	private int pd_pay_status;// 支付状态，0为等待支付，1为线下提交支付完成申请，2为支付成功
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "predeposit", cascade = CascadeType.REMOVE)
	private PredepositLog log;// 对应的操作日志

	
	public String getPd_no() {
		return pd_no;
	}

	public void setPd_no(String pd_no) {
		this.pd_no = pd_no;
	}

	public int getPd_status() {
		return pd_status;
	}

	public void setPd_status(int pd_status) {
		this.pd_status = pd_status;
	}

	public int getPd_pay_status() {
		return pd_pay_status;
	}

	public void setPd_pay_status(int pd_pay_status) {
		this.pd_pay_status = pd_pay_status;
	}

	public User getPd_user() {
		return pd_user;
	}

	public void setPd_user(User pd_user) {
		this.pd_user = pd_user;
	}

	public BigDecimal getPd_amount() {
		return pd_amount;
	}

	public void setPd_amount(BigDecimal pd_amount) {
		this.pd_amount = pd_amount;
	}

	public String getPd_sn() {
		return pd_sn;
	}

	public void setPd_sn(String pd_sn) {
		this.pd_sn = pd_sn;
	}

	public String getPd_payment() {
		return pd_payment;
	}

	public void setPd_payment(String pd_payment) {
		this.pd_payment = pd_payment;
	}

	public String getPd_remittance_user() {
		return pd_remittance_user;
	}

	public void setPd_remittance_user(String pd_remittance_user) {
		this.pd_remittance_user = pd_remittance_user;
	}

	public String getPd_remittance_bank() {
		return pd_remittance_bank;
	}

	public void setPd_remittance_bank(String pd_remittance_bank) {
		this.pd_remittance_bank = pd_remittance_bank;
	}

	public Date getPd_remittance_time() {
		return pd_remittance_time;
	}

	public void setPd_remittance_time(Date pd_remittance_time) {
		this.pd_remittance_time = pd_remittance_time;
	}

	public String getPd_remittance_info() {
		return pd_remittance_info;
	}

	public void setPd_remittance_info(String pd_remittance_info) {
		this.pd_remittance_info = pd_remittance_info;
	}

	public User getPd_admin() {
		return pd_admin;
	}

	public void setPd_admin(User pd_admin) {
		this.pd_admin = pd_admin;
	}

	public String getPd_admin_info() {
		return pd_admin_info;
	}

	public void setPd_admin_info(String pd_admin_info) {
		this.pd_admin_info = pd_admin_info;
	}

	public PredepositLog getLog() {
		return log;
	}

	public void setLog(PredepositLog log) {
		this.log = log;
	}

}
