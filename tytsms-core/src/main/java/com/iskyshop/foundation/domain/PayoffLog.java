package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
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
 * <p>
 * Title: Payoff.java
 * </p>
 * 
 * <p>
 * Description: 系统结算账单管理类，商家与平台之间进行结算，平台可以设置月结算次数，系统自动分配每月结算日期，且最后一次结算均为当月最后一天
 * 用户确认收货后生成结算账单，商家只能在结算日提交结算申请，用户退货和退款，商家的账单佣金不予返还
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
 * @author hezeng
 * 
 * @date 2014年5月5日
 * 
 * @version 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "payoff_log")
public class PayoffLog extends IdEntity {
	private String pl_sn;// 结算结算账单唯一编号记录，使用pl为前缀
	private String pl_info;// 结算账单说明
	@ManyToOne(fetch = FetchType.LAZY)
	private User seller;// 结算账单对应的店铺商家
	private String order_id;// 结算账单对应的订单号
	private String o_id;// 结算账单对应的订单id
	@Column(precision = 12, scale = 2)
	private BigDecimal total_amount;// 结算金额=订单总金额-总佣金费用
	@Column(precision = 12, scale = 2)
	private BigDecimal reality_amount;// 实际结算金额，
	@Column(precision = 12, scale = 2)
	private BigDecimal commission_amount;// 结算账单总佣金
	@Column(precision = 12, scale = 2)
	private BigDecimal order_total_price;// 对应订单商品总金额
	@Column(columnDefinition = "LongText")
	private String goods_info;// 商品信息json数据[{"goods_id":1,"goods_name":"佐丹奴男装翻领T恤
	// 条纹修身商务男POLO纯棉短袖POLO衫","deliveryGoods":true,"goods_commission_rate":"0.8","goods_commission_price":"16.00","goods_payoff_price":"123123","goods_type":"combin","goods_count":2,"goods_price":100,"goods_all_price":200,"goods_gsp_ids":"/1/3","goods_gsp_val":"尺码：XXL","goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"]"
	private Date apply_time;// 结算申请时间
	private Date complete_time;// 结算完成时间
	@Column(columnDefinition = "int default 0")
	private int status;// 结算账单状态，0为未结算，1为可结算、3为结算中，6为已结算已完成。
	@ManyToOne(fetch = FetchType.LAZY)
	private User admin;// 结算账单操作管理员
	@Column(columnDefinition = "int default 0")
	private int payoff_type;// 结算账单类型，0为进账，-1为出账
	private String finance_userName;// 结算该账单的财务人员姓名
	@Column(columnDefinition = "LongText")
	private String payoff_remark;// 结算说明
	private String refund_userName;// 退货完成后进行退款时的收款人姓名
	private Long refund_user_id;// 退货完成后进行退款时的收款人id
	private String return_service_id;// 退货服务单的服务号 ReturnGoodsLog.java private
	@Column(columnDefinition = "LongText")
	private String return_goods_info;// 退货商品详细信息json数据[{"goods_id":1,"goods_name":"佐丹奴男装翻领T恤

	// 条纹修身商务男POLO纯棉短袖POLO衫","goods_price":500."goods_mainphoto_path":"upload/d7fd8fdf686-sdf86s.jps"}]"

	public String getRefund_userName() {
		return refund_userName;
	}

	public BigDecimal getReality_amount() {
		return reality_amount;
	}

	public void setReality_amount(BigDecimal reality_amount) {
		this.reality_amount = reality_amount;
	}

	public String getReturn_goods_info() {
		return return_goods_info;
	}

	public void setReturn_goods_info(String return_goods_info) {
		this.return_goods_info = return_goods_info;
	}

	public void setRefund_userName(String refund_userName) {
		this.refund_userName = refund_userName;
	}

	public Long getRefund_user_id() {
		return refund_user_id;
	}

	public void setRefund_user_id(Long refund_user_id) {
		this.refund_user_id = refund_user_id;
	}

	public String getReturn_service_id() {
		return return_service_id;
	}

	public void setReturn_service_id(String return_service_id) {
		this.return_service_id = return_service_id;
	}

	public String getPayoff_remark() {
		return payoff_remark;
	}

	public void setPayoff_remark(String payoff_remark) {
		this.payoff_remark = payoff_remark;
	}

	public String getFinance_userName() {
		return finance_userName;
	}

	public void setFinance_userName(String finance_userName) {
		this.finance_userName = finance_userName;
	}

	public String getO_id() {
		return o_id;
	}

	public void setO_id(String o_id) {
		this.o_id = o_id;
	}

	public int getPayoff_type() {
		return payoff_type;
	}

	public void setPayoff_type(int payoff_type) {
		this.payoff_type = payoff_type;
	}

	public String getPl_sn() {
		return pl_sn;
	}

	public void setPl_sn(String pl_sn) {
		this.pl_sn = pl_sn;
	}

	public BigDecimal getOrder_total_price() {
		return order_total_price;
	}

	public void setOrder_total_price(BigDecimal order_total_price) {
		this.order_total_price = order_total_price;
	}

	public String getGoods_info() {
		return goods_info;
	}

	public void setGoods_info(String goods_info) {
		this.goods_info = goods_info;
	}

	public String getPl_info() {
		return pl_info;
	}

	public void setPl_info(String pl_info) {
		this.pl_info = pl_info;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public BigDecimal getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(BigDecimal total_amount) {
		this.total_amount = total_amount;
	}

	public BigDecimal getCommission_amount() {
		return commission_amount;
	}

	public void setCommission_amount(BigDecimal commission_amount) {
		this.commission_amount = commission_amount;
	}

	public Date getApply_time() {
		return apply_time;
	}

	public void setApply_time(Date apply_time) {
		this.apply_time = apply_time;
	}

	public Date getComplete_time() {
		return complete_time;
	}

	public void setComplete_time(Date complete_time) {
		this.complete_time = complete_time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

}
