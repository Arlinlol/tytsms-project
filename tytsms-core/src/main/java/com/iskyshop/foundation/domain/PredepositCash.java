package com.iskyshop.foundation.domain;

import java.math.BigDecimal;

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
* <p>Title: PredepositCash.java</p>

* <p>Description:会员提现管理 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "predeposit_cash")
public class PredepositCash extends IdEntity {
	private String cash_sn;// 提现编号以cash开头
	private String cash_payment;// 提现方式
	@Column(precision = 12, scale = 2)
	private BigDecimal cash_amount;// 提现金额
	@ManyToOne(fetch = FetchType.LAZY)
	private User cash_user;// 申请用户
	private String cash_userName;// 收款人姓名
	private String cash_bank;// 收款银行
	private String cash_account;// 收款账号
	private int cash_pay_status;// 收款支付状态，0为等待支付，1为支付完成
	private int cash_status;// 提现状态 0为审核中，1为已经完成
	@Column(columnDefinition = "LongText")
	private String cash_info;// 提现备注
	@ManyToOne(fetch = FetchType.LAZY)
	private User cash_admin;// 充值请求处理的管理员
	@Column(columnDefinition = "LongText")
	private String cash_admin_info;// 请求处理备注

	public String getCash_payment() {
		return cash_payment;
	}

	public void setCash_payment(String cash_payment) {
		this.cash_payment = cash_payment;
	}

	public BigDecimal getCash_amount() {
		return cash_amount;
	}

	public void setCash_amount(BigDecimal cash_amount) {
		this.cash_amount = cash_amount;
	}

	public User getCash_user() {
		return cash_user;
	}

	public void setCash_user(User cash_user) {
		this.cash_user = cash_user;
	}

	public String getCash_userName() {
		return cash_userName;
	}

	public void setCash_userName(String cash_userName) {
		this.cash_userName = cash_userName;
	}

	public String getCash_bank() {
		return cash_bank;
	}

	public void setCash_bank(String cash_bank) {
		this.cash_bank = cash_bank;
	}

	public String getCash_account() {
		return cash_account;
	}

	public void setCash_account(String cash_account) {
		this.cash_account = cash_account;
	}

	public int getCash_pay_status() {
		return cash_pay_status;
	}

	public void setCash_pay_status(int cash_pay_status) {
		this.cash_pay_status = cash_pay_status;
	}

	public int getCash_status() {
		return cash_status;
	}

	public void setCash_status(int cash_status) {
		this.cash_status = cash_status;
	}

	public String getCash_sn() {
		return cash_sn;
	}

	public void setCash_sn(String cash_sn) {
		this.cash_sn = cash_sn;
	}

	public String getCash_info() {
		return cash_info;
	}

	public void setCash_info(String cash_info) {
		this.cash_info = cash_info;
	}

	public User getCash_admin() {
		return cash_admin;
	}

	public void setCash_admin(User cash_admin) {
		this.cash_admin = cash_admin;
	}

	public String getCash_admin_info() {
		return cash_admin_info;
	}

	public void setCash_admin_info(String cash_admin_info) {
		this.cash_admin_info = cash_admin_info;
	}
}
