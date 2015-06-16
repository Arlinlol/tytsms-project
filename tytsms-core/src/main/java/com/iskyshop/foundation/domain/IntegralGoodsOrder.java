package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: IntegralGoodsOrder.java
 * </p>
 * 
 * <p>
 * Description: 积分兑换订单
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integral_goodsorder")
public class IntegralGoodsOrder extends IdEntity {
	private String igo_order_sn;// 订单编号，以igo开头
	@ManyToOne(fetch = FetchType.LAZY)
	private User igo_user;// 订单用户
	@OneToMany(mappedBy = "order", cascade = { CascadeType.REMOVE,
			CascadeType.PERSIST })
	private List<IntegralGoodsCart> igo_gcs = new ArrayList<IntegralGoodsCart>();// 订单对应的购物车
	@Column(precision = 12, scale = 2)
	private BigDecimal igo_trans_fee;// 购物车运费
	private int igo_status;// 订单状态，0为已提交未付款，10为线下支付已付款(待审核)，20为付款成功，30为已发货，40为已收货完成,-1为已经取消，此时不归还用户积分
	private int igo_total_integral;// 总共消费积分
	@Column(columnDefinition = "LongText")
	private String igo_msg;// 兑换附言
	private String igo_payment;// 支付方式，使用mark标识
	@Column(columnDefinition = "LongText")
	private String igo_pay_msg;// 支付说明
	private Date igo_pay_time;// 支付时间
	@Column(columnDefinition = "LongText")
	private String igo_express_info;// 物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String igo_ship_code;// 物流单号
	@Temporal(TemporalType.DATE)
	private Date igo_ship_time;// 发货时间
	@Column(columnDefinition = "LongText")
	private String igo_ship_content;// 发货说明
	private String receiver_Name;// 收货人姓名,确认订单后，将买家的收货地址所有信息添加到订单中，该订单与买家收货地址没有任何关联
	private String receiver_area;// 收货人地区,例如：辽宁省沈阳市铁西区
	private String receiver_area_info;// 收货人详细地址，例如：凌空二街56-1号，4单元2楼1号
	private String receiver_zip;// 收货人邮政编码
	private String receiver_telephone;// 收货人联系电话
	private String receiver_mobile;// 收货人手机号码

	public String getReceiver_Name() {
		return receiver_Name;
	}

	public void setReceiver_Name(String receiver_Name) {
		this.receiver_Name = receiver_Name;
	}

	public String getReceiver_area() {
		return receiver_area;
	}

	public void setReceiver_area(String receiver_area) {
		this.receiver_area = receiver_area;
	}

	public String getReceiver_area_info() {
		return receiver_area_info;
	}

	public void setReceiver_area_info(String receiver_area_info) {
		this.receiver_area_info = receiver_area_info;
	}

	public String getReceiver_zip() {
		return receiver_zip;
	}

	public void setReceiver_zip(String receiver_zip) {
		this.receiver_zip = receiver_zip;
	}

	public String getReceiver_telephone() {
		return receiver_telephone;
	}

	public void setReceiver_telephone(String receiver_telephone) {
		this.receiver_telephone = receiver_telephone;
	}

	public String getReceiver_mobile() {
		return receiver_mobile;
	}

	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}

	public String getIgo_ship_content() {
		return igo_ship_content;
	}

	public void setIgo_ship_content(String igo_ship_content) {
		this.igo_ship_content = igo_ship_content;
	}

	public String getIgo_ship_code() {
		return igo_ship_code;
	}

	public void setIgo_ship_code(String igo_ship_code) {
		this.igo_ship_code = igo_ship_code;
	}

	public Date getIgo_ship_time() {
		return igo_ship_time;
	}

	public void setIgo_ship_time(Date igo_ship_time) {
		this.igo_ship_time = igo_ship_time;
	}

	public Date getIgo_pay_time() {
		return igo_pay_time;
	}

	public void setIgo_pay_time(Date igo_pay_time) {
		this.igo_pay_time = igo_pay_time;
	}

	public String getIgo_order_sn() {
		return igo_order_sn;
	}

	public void setIgo_order_sn(String igo_order_sn) {
		this.igo_order_sn = igo_order_sn;
	}

	public User getIgo_user() {
		return igo_user;
	}

	public void setIgo_user(User igo_user) {
		this.igo_user = igo_user;
	}

	public List<IntegralGoodsCart> getIgo_gcs() {
		return igo_gcs;
	}

	public void setIgo_gcs(List<IntegralGoodsCart> igo_gcs) {
		this.igo_gcs = igo_gcs;
	}

	public BigDecimal getIgo_trans_fee() {
		return igo_trans_fee;
	}

	public void setIgo_trans_fee(BigDecimal igo_trans_fee) {
		this.igo_trans_fee = igo_trans_fee;
	}

	public int getIgo_status() {
		return igo_status;
	}

	public void setIgo_status(int igo_status) {
		this.igo_status = igo_status;
	}

	public int getIgo_total_integral() {
		return igo_total_integral;
	}

	public void setIgo_total_integral(int igo_total_integral) {
		this.igo_total_integral = igo_total_integral;
	}

	public String getIgo_msg() {
		return igo_msg;
	}

	public void setIgo_msg(String igo_msg) {
		this.igo_msg = igo_msg;
	}

	public String getIgo_payment() {
		return igo_payment;
	}

	public void setIgo_payment(String igo_payment) {
		this.igo_payment = igo_payment;
	}

	public String getIgo_pay_msg() {
		return igo_pay_msg;
	}

	public void setIgo_pay_msg(String igo_pay_msg) {
		this.igo_pay_msg = igo_pay_msg;
	}

	public String getIgo_express_info() {
		return igo_express_info;
	}

	public void setIgo_express_info(String igo_express_info) {
		this.igo_express_info = igo_express_info;
	}

}
