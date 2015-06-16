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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.annotation.Lock;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: OrderForm.java
 * </p>
 * 
 * <p>
 * Description: 系统订单管理类，包括购物订单、手机充值订单、团购订单等等，该类对应的数据表是商城系统中最大也是最重要的数据库。
 * 系統接入殴飞充值，充值按照以下流程完成：1、运营商向殴飞申请开通接口并在b2b2c系统中配置对应的用户名 、密码；2、
 * 运营商在殴飞平台充值一定款项；3、运营商配置充值金额，比如充值100元，殴飞收取98.3，运营商配置为98.5，一单赚2毛；4、用户从平台充值，
 * 首先查询殴飞账户余额，余额足够则跳转到付款界面，向运营商账户付款(目前只支持预存款付款)，付款成功后调用殴飞接口完成充值；
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
 * @author erikzhang、hezeng
 * 
 * @date 2014-4-25
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "orderform")
public class OrderForm extends IdEntity {
	private String trade_no;// 交易流水号,在线支付时每次随机生成唯一的号码，重复提交时替换当前订单的交易流水号
	private String order_id;// 订单号
	@Column(columnDefinition = "int default 0")
	private int order_main;// 是否为主订单，1为主订单，主订单用在买家用户中心显示订单内容
	@Column(columnDefinition = "LongText")
	private String child_order_detail;// 子订单详情，如果该订单为主订单，则记录
	private String out_order_id;// 外部单号
	private String order_type;// 订单类型，分为web:PC网页订单，weixin:手机网页订单,android:android手机客户端订单，ios:iOS手机客户端订单
	@Column(columnDefinition = "int default 0")
	private int order_cat;// 订单分类，0为购物订单，1为手机充值订单 2为生活类团购订单
	private Long shipper_type; //发货类型 1国内 2国际 用来查询物流信息
	@Column(columnDefinition = "LongText")
	private String goods_info;// 使用json管理"[{"goods_id":1,"goods_name":"佐丹奴男装翻领T恤
								// 条纹修身商务男POLO纯棉短袖POLO衫","goods_type":"group","goods_choice_type":1,"goods_cat":0,"goods_commission_rate":"0.8","goods_commission_price":"16.00","goods_payoff_price":"234""goods_type":"combin","goods_count":2,"goods_price":100,"goods_all_price":200,"goods_gsp_ids":"/1/3","goods_gsp_val":"尺码：XXL","goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg","goods_return_status",1 商品退货状态 当有此字段并且为1时为可退货状态 5为已申请退货,6为商家已同意退货，买家未提交退货物流，7为买家已提交退货物流信息，8为退货流程完毕，提交平台退款 -1已经超出可退货时间,"goods_complaint_status" 没有此字段时可投诉 投诉后的状态为1不可投诉}]"
	@Column(columnDefinition = "LongText")
	private String return_goods_info;// 退货商品详细，return_goods_id:退货商品id,
										// return_goods_count：退货数量，return_goods_commission_rate：退货商品佣金率，return_goods_price：退货商品单价，return_goods_content:退货商品说明。使用json管理"[{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100},{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100}]"
	@Column(columnDefinition = "LongText")
	private String group_info;// 生活类团购 团购详情 GroupLifeGoods
								// 使用json管理"[{"goods_id":1,"goods_name":"最新电影票xx影视城
								// 团购优惠","goods_type":"0为经销商发布
								// 1为自营发布","goods_price","10
								// 单价","goods_count","1数量","goods_total_price","10
								// 总价"，"goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"]"
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal totalPrice;// 订单总价格
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_amount;// 商品总价格
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal commission_amount;// 该订单佣金总费用，
	@Column(columnDefinition = "LongText")
	private String msg;// 订单附言
	private String payType;// 支付类型，手机端支付时选设置订单支付类型，根据支付类型再进行支付
	@ManyToOne(fetch = FetchType.LAZY)
	private Payment payment;// 支付方式
	private String transport;// 配送方式
	private String shipCode;// 物流单号
	private Date return_shipTime;// 买家退货发货截止时间
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal ship_price;// 配送价格
	@Lock
	private int order_status;// 订单状态，0为订单取消，10为已提交待付款，15为线下付款提交申请(已经取消该付款方式)，16为货到付款，20为已付款待发货，30为已发货待收货，40为已收货
	// 50买家评价完毕 ,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能
	private String user_id;// 买家用户id
	private String user_name;// 买家用户姓名
	private String store_id;// 订单对应的卖家店铺
	private String store_name;// 订单对应的卖家店铺名称
	private Date payTime;// 付款时间
	private Date shipTime;// 发货时间
	private Date confirmTime;// 确认收货时间
	private Date finishTime;// 完成时间
	@Column(columnDefinition = "LongText")
	private String express_info;// 物流公司信息json{"express_company_id":1,"express_company_name":"顺丰快递","express_company_mark":"shunfeng","express_company_type":"EXPRESS"}
	private String receiver_Name;// 收货人姓名,确认订单后，将买家的收货地址所有信息添加到订单中，该订单与买家收货地址没有任何关联
	private String receiver_area;// 收货人地区,例如：辽宁省沈阳市铁西区
	private String receiver_area_info;// 收货人详细地址，例如：凌空二街56-1号，4单元2楼1号
	private String receiver_zip;// 收货人邮政编码
	private String receiver_telephone;// 收货人联系电话
	private String receiver_mobile;// 收货人手机号码

	private int invoiceType;// 发票类型，0为个人，1为单位
	private String invoice;// 发票信息
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<OrderFormLog> ofls = new ArrayList<OrderFormLog>();// 订单日志
	@Column(columnDefinition = "LongText")
	private String pay_msg;// 支付相关说明，比如汇款账号、时间等
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_email;// 自动收款的邮件提示
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_sms;// 自动收款的短信提示
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Evaluate> evas = new ArrayList<Evaluate>();// 订单对应的评价
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Complaint> complaints = new ArrayList<Complaint>();// 投诉管理类
	@Column(columnDefinition = "LongText")
	private String coupon_info;// 订单使用的优惠券信息json{"couponinfo_id":1,"couponinfo_sn":"ljsa-123l-8weo-s28s","coupon_amount":123,"coupon_goods_rate":0.98},coupon_goods_rate对于当前订单商品价格的比例
	@Column(columnDefinition = "LongText")
	private String order_seller_intro;// 订单卖家给予的说明，用在虚拟商品信息，比如购买充值卡，卖家发货时在这里给出对应的卡号和密钥
	@Column(columnDefinition = "int default 0")
	private int order_form;// 订单种类，0为商家商品订单，1为平台自营商品订单
	private Long eva_user_id;// 保存点击确认发货的管理员id，
	// 以下为手机充值订单信息
	private String rc_mobile;// 充值手机号
	@Column(columnDefinition = "int default 0")
	private int rc_amount;// 充值金额,只能为整数
	@Column(precision = 12, scale = 2)
	private BigDecimal rc_price;// 充值rc_amount金额需要缴纳的费用
	@Column(precision = 12, scale = 2)
	private BigDecimal out_price;// 充值rc_amount金额第三方平台收取的价格，通过接口自动获取
	@Column(columnDefinition = "varchar(255) default 'mobile'")
	private String rc_type;// 充值类型，默认为mobile，手机直充，目前仅支持该类型
	@Column(columnDefinition = "int default 0")
	private int operation_price_count;// 商家手动调整费用次数，0为未调整过，用于显示调整过订单费用的账单显示
	private Long push_status; // 推送状态 1成功 2失败
	
	public Long getPush_status() {
		return push_status;
	}

	public void setPush_status(Long push_status) {
		this.push_status = push_status;
	}

	public Long getShipper_type() {
		return shipper_type;
	}

	public void setShipper_type(Long shipper_type) {
		this.shipper_type = shipper_type;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public String getGroup_info() {
		return group_info;
	}

	public void setGroup_info(String group_info) {
		this.group_info = group_info;
	}

	public int getOperation_price_count() {
		return operation_price_count;
	}

	public void setOperation_price_count(int operation_price_count) {
		this.operation_price_count = operation_price_count;
	}

	public int getOrder_main() {
		return order_main;
	}

	public void setOrder_main(int order_main) {
		this.order_main = order_main;
	}

	public String getChild_order_detail() {
		return child_order_detail;
	}

	public void setChild_order_detail(String child_order_detail) {
		this.child_order_detail = child_order_detail;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getRc_mobile() {
		return rc_mobile;
	}

	public void setRc_mobile(String rc_mobile) {
		this.rc_mobile = rc_mobile;
	}

	public int getRc_amount() {
		return rc_amount;
	}

	public void setRc_amount(int rc_amount) {
		this.rc_amount = rc_amount;
	}

	public BigDecimal getRc_price() {
		return rc_price;
	}

	public void setRc_price(BigDecimal rc_price) {
		this.rc_price = rc_price;
	}

	public BigDecimal getOut_price() {
		return out_price;
	}

	public void setOut_price(BigDecimal out_price) {
		this.out_price = out_price;
	}

	public String getRc_type() {
		return rc_type;
	}

	public void setRc_type(String rc_type) {
		this.rc_type = rc_type;
	}

	public int getOrder_cat() {
		return order_cat;
	}

	public void setOrder_cat(int order_cat) {
		this.order_cat = order_cat;
	}

	public String getReturn_goods_info() {
		return return_goods_info;
	}

	public void setReturn_goods_info(String return_goods_info) {
		this.return_goods_info = return_goods_info;
	}

	public Long getEva_user_id() {
		return eva_user_id;
	}

	public void setEva_user_id(Long eva_user_id) {
		this.eva_user_id = eva_user_id;
	}

	public BigDecimal getCommission_amount() {
		return commission_amount;
	}

	public void setCommission_amount(BigDecimal commission_amount) {
		this.commission_amount = commission_amount;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCoupon_info() {
		return coupon_info;
	}

	public void setCoupon_info(String coupon_info) {
		this.coupon_info = coupon_info;
	}

	public String getExpress_info() {
		return express_info;
	}

	public void setExpress_info(String express_info) {
		this.express_info = express_info;
	}

	public String getGoods_info() {
		return goods_info;
	}

	public void setGoods_info(String goods_info) {
		this.goods_info = goods_info;
	}

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

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public int getOrder_form() {
		return order_form;
	}

	public void setOrder_form(int order_form) {
		this.order_form = order_form;
	}

	public Date getReturn_shipTime() {
		return return_shipTime;
	}

	public void setReturn_shipTime(Date return_shipTime) {
		this.return_shipTime = return_shipTime;
	}

	public List<Complaint> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<Complaint> complaints) {
		this.complaints = complaints;
	}

	public List<Evaluate> getEvas() {
		return evas;
	}

	public void setEvas(List<Evaluate> evas) {
		this.evas = evas;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getShip_price() {
		return ship_price;
	}

	public void setShip_price(BigDecimal ship_price) {
		this.ship_price = ship_price;
	}

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getShipCode() {
		return shipCode;
	}

	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
	}

	public Date getShipTime() {
		return shipTime;
	}

	public void setShipTime(Date shipTime) {
		this.shipTime = shipTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public int getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public List<OrderFormLog> getOfls() {
		return ofls;
	}

	public void setOfls(List<OrderFormLog> ofls) {
		this.ofls = ofls;
	}

	public String getPay_msg() {
		return pay_msg;
	}

	public void setPay_msg(String pay_msg) {
		this.pay_msg = pay_msg;
	}

	public BigDecimal getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(BigDecimal goods_amount) {
		this.goods_amount = goods_amount;
	}

	public boolean isAuto_confirm_email() {
		return auto_confirm_email;
	}

	public void setAuto_confirm_email(boolean auto_confirm_email) {
		this.auto_confirm_email = auto_confirm_email;
	}

	public boolean isAuto_confirm_sms() {
		return auto_confirm_sms;
	}

	public void setAuto_confirm_sms(boolean auto_confirm_sms) {
		this.auto_confirm_sms = auto_confirm_sms;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getOut_order_id() {
		return out_order_id;
	}

	public void setOut_order_id(String out_order_id) {
		this.out_order_id = out_order_id;
	}

	public String getOrder_seller_intro() {
		return order_seller_intro;
	}

	public void setOrder_seller_intro(String order_seller_intro) {
		this.order_seller_intro = order_seller_intro;
	}
}
