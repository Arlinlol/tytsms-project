package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.annotation.Lock;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: SysConfig.java
 * </p>
 * 
 * <p>
 * Description: 系统配置管理类,包括系统基础信息、系统邮箱发送配置、手机短息发送配置、殴飞充值接口配置等所有系统基础相关内容
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sysconfig")
public class SysConfig extends IdEntity {
	private String title;// 商城标题
	private String keywords;// 商城SEO关键字
	private String description;// 商城SEO描述
	private String address;// 商城访问地址，填写商城网址
	private String poweredby;// 前端poweredby
	private String copyRight;// 版权信息
	private String meta_generator;// meta的generator信息
	private String meta_author;// meta的author信息
	private String sysLanguage;// 系统语言
	private int integralRate;// 充值积分兑换比率
	private boolean smsEnbale;// 短信平台开启
	private String smsURL;// 短信平台发送地址
	private String smsUserName;// 短信平台用户名
	private String smsPassword;// 短信平台用户密码
	private String smsTest;// 短信测试发送账号
	private boolean emailEnable;// 邮件是否开启
	private String emailHost;// stmp服务器
	private int emailPort;// stmp端口
	private String emailUser;// 发件人
	private String emailUserName;// 邮箱用户名
	private String emailPws;// 邮箱密码
	private String emailTest;// 邮件发送测试
	private String websiteName;// 网站名称
	private String hotSearch;// 热门搜索
	@Column(columnDefinition = "varchar(255) default 'blue' ")
	private String websiteCss;// 当前网站平台样式，默认为蓝色
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory websiteLogo;// 网站logo
	@Lob
	@Column(columnDefinition = "LongText")
	private String codeStat;// 三方代码统计
	private boolean websiteState;// 网站状态(开/关)
	private boolean visitorConsult;// 游客咨询
	@Lob
	@Column(columnDefinition = "LongText")
	private String closeReason;// 网站关闭原因
	private String securityCodeType;// 验证码类型
	private boolean securityCodeRegister;// 前台注册验证
	private boolean securityCodeLogin;// 前台登陆验证
	private boolean securityCodeConsult;// 商品咨询验证
	private String imageSuffix;// 图片的后缀名
	private String imageWebServer;// 图片服务器地址
	private int imageFilesize;// 允许图片上传的最大值
	private int smallWidth;// 最小尺寸像素宽
	private int smallHeight;
	private int middleWidth;// 中尺寸像素宽
	private int middleHeight;
	private int bigWidth;// 大尺寸像素高
	private int bigHeight;
	private boolean integral;// 积分
	private boolean integralStore;// 开启积分商城
	private boolean voucher;// 代金券
	private boolean deposit;// 预存款
	private boolean groupBuy;// 团购
	@Column(columnDefinition = "int default 300")
	private int group_meal_gold;// 团购套餐价格 按每30天多少金币算,默认是300个金币每30天
	private boolean gold;// 金币
	@Column(columnDefinition = "int default 1")
	private int goldMarketValue;// 金币市值，默认是一个金币抵制1元
	private int memberRegister;// 会员注册(赠送积分)
	@Column(columnDefinition = "int default 0")
	private int inviteRegister;// 邀请注册（赠送积分）
	@Column(columnDefinition = "int default 0")
	private int shareRegister;  //分享注册（赠送积分）
	@Column(columnDefinition = "int default 0")
	private int luckDraw;	    //抽奖积分
	private String awardOne;    //奖品一
	private String awardTwo;    //奖品二
	private String awardThree;  //奖品三
	private String awardFour;   //奖品四
	private String awardFive;   //奖品五
	private String awardSix;    //奖品六
	private String awardSeven;  //奖品七
	private String awardEight;  //奖品八
	private String awardNine;   //奖品九
	private String awardTen;    //奖品十
	private int memberDayLogin;// 会员每日登陆(赠送积分)
	private int indentComment;// 订单评论(赠送积分)
	private int consumptionRatio;// 消费比例(赠送积分)
	private int everyIndentLimit;// 每个订单(赠送积分)
	private String imageSaveType;// 图片保存类型
	private int complaint_time;// 举报失效，以订单完成时间开始计算，单位为天
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory storeImage;// 默认店铺标志
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory goodsImage;// 默认商品图片
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Accessory memberIcon;// 默认用户图标
	private boolean store_allow;// 允许店铺申请
	@Lob
	@Column(columnDefinition = "LongText")
	private String user_level;// 会员等级数据，使用json管理
	@Lob
	@Column(columnDefinition = "LongText")
	private String templates;// 店铺样式管理使用字符串管理
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_payment;// 平台支付方式启用情况，使用json管理，如{"alipay":true,"99bill":false}
	@Lob
	@Column(columnDefinition = "LongText")
	private String share_code;// 百度分享代码
	private boolean ztc_status;// 直通车状态
	@Column(columnDefinition = "int default 0")
	private int ztc_goods_view;// 直通车商品显示方式，0为没有任何限制，1为按照商品分类显示，在商品分类搜索页中是否按照该页中分类进行显示，
	private int ztc_price;// 直通车起价，用户可以任意设定价格，必须大于该价格，价格越高排序也靠前
	@Column(columnDefinition = "bit default 0")
	private boolean second_domain_open;// 是否开通二级域名
	@Column(columnDefinition = "int default 0")
	@Lock
	private int domain_allow_count;// 店铺二级域名运行修改次数，0为无限制
	@Column(columnDefinition = "LongText")
	@Lock
	private String sys_domain;// 系统保留二级域名
	@Column(columnDefinition = "bit default 0")
	private boolean qq_login;// 是否允许QQ登录
	private String qq_login_id;// QQ登录Id
	private String qq_login_key;// QQ登录key
	@Column(columnDefinition = "LongText")
	private String qq_domain_code;// QQ域名验证信息
	@Column(columnDefinition = "bit default 0")
	private boolean sina_login;// 是否允许新浪微博登录
	private String sina_login_id;// 新浪微博Id
	private String sina_login_key;// 新浪微博key
	@Column(columnDefinition = "LongText")
	private String sina_domain_code;// 新浪微博域名验证信息
	private Date lucene_update;// 全文索引更新时间
	@Column(columnDefinition = "int default 50")
	private int combin_amount;// 组合销售月收费，单位为金币
	@Column(columnDefinition = "int default 3")
	private int combin_count;// 组合销售中的最大商品数量
	@OneToMany(mappedBy = "config")
	private List<Accessory> login_imgs = new ArrayList<Accessory>();// 登录页面的左侧图片
	@Column(columnDefinition = "LongText")
	private String service_telphone_list;// 平台客服电话，一行一个
	@Column(columnDefinition = "LongText")
	private String service_qq_list;// 平台客服QQ，一行一个
	@Column(columnDefinition = "bit default 0")
	private boolean uc_bbs;// 是否开启discuz论坛整合
	private String uc_database = "";// uc数据库名称
	private String uc_table_preffix = "";// uc数据表前缀
	private String uc_database_url = "";// uc数据库地址
	private String uc_database_port = "";// uc数据库端口
	private String uc_database_username = "";// uc数据库用户名
	private String uc_database_pws = "";// uc数据库链接密码
	private String uc_api;// uc server url
	private String uc_ip;// uc ip address
	private String uc_key;// UC_KEY
	private String uc_appid;// UC_APPID
	@Column(columnDefinition = "int default 3")
	@Lock
	private int auto_order_notice;// 卖家发货后达到该时长，给买家发送即将自动确认收货的短信、邮件提醒
	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_confirm;// 卖家发货后，达到该时间系统自动确认收货

	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_return;// 买家申请退货，到达该时间未能输入退货单号及物流公司，退货失败并且订单变为待评价，订单状态为49

	@Column(columnDefinition = "int default 7")
	@Lock
	private int auto_order_evaluate;// 订单确认收货后到达该时间，系统自动管理订单评价功能

	@Column(columnDefinition = "int default 7")
	@Lock
	private int grouplife_order_return;// 生活类团购退货时效，如电影票，购买付款后，在该天数内可以申请退款
	@Column(columnDefinition = "LongText")
	private String kuaidi_id;// kuaidi100快递查询Id，卖家需自行向http://www.kuaidi100.com申请接口id，下个版本公司内部出版接口查询
	@Column(columnDefinition = "varchar(255)")
	private String currency_code;// 货币符号，默认为人民币¥
	@Lock
	@Column(columnDefinition = "bit default 0")
	private boolean weixin_store;// 微信商城的状态，默认系统是未开启微信商城，开启后，大平台支持微信商城
	@OneToOne
	private Accessory weixin_qr_img;// 微信二维码图片
	@Lock
	private String weixin_account;// 微信账号
	@Lock
	private String weixin_token;// 微信token，申请开发者时自己填写的token
	@Lock
	private String weixin_appId;// 微信App Id，申请开发者成功后微信生成的AppId
	@Lock
	private String weixin_appSecret;// 微信AppSecret，申请开发者成功后微信生成的AppSecret
	@Lock
	@Column(columnDefinition = "LongText")
	private String weixin_welecome_content;// 关注微信时候的欢迎词
	@Lock
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_weixin_logo;// 平台微信商城ogo
	@Column(columnDefinition = "int default 1")
	private int payoff_count;// 月结算次数，可以设置为1次、2次、3次、4次
	private Date payoff_date;// 下次结算日期，每天0:00计算系统下次结算时间
	@Column(columnDefinition = "int default 0")
	private int payoff_mag_type;// 结算通知消息类型，0为系统默认消息，1为自定义消息
	@Column(columnDefinition = "LongText")
	private String payoff_mag_default;// 系统默认结算通知
	@Column(columnDefinition = "LongText")
	private String payoff_mag_auto;// 自定义结算通知
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_sale;// 系统总销售金额
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_commission;// 系统总销售佣金
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_amount;// 系统总结算金额(应结算)
	@Column(precision = 12, scale = 2)
	private BigDecimal payoff_all_amount_reality;// 系统总结算金额(实际结算)
	@Lock
	private boolean ofcard;// 是否开通殴飞充值接口
	@Lock
	private String ofcard_userid;// 殴飞充值接口用户名
	@Lock
	private String ofcard_userpws;// 殴飞充值接口密码
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal ofcard_mobile_profit;// 手机充值的利润值，用户充值手机时候，系统会查询殴飞接口实际收费，根据殴飞的收费加上该利润值，作为用户缴纳的最终金额
	@Lock
	@Column(columnDefinition = "int default 0")
	private int app_download;// 是否启用App下载，默认为0不启用，1为启用
	@Lock
	private String android_download;// 安卓客户端下载地址
	private String android_version;// 安卓客户端版本号
	@Lock
	private String ios_download;// Iphone客户端下载地址
	private String ios_version;// Iphone客户端版本号
	
	

	public String getAndroid_version() {
		return android_version;
	}

	public void setAndroid_version(String android_version) {
		this.android_version = android_version;
	}

	public String getIos_version() {
		return ios_version;
	}

	public void setIos_version(String ios_version) {
		this.ios_version = ios_version;
	}

	public String getPoweredby() {
		return poweredby;
	}

	public void setPoweredby(String poweredby) {
		this.poweredby = poweredby;
	}

	public String getMeta_generator() {
		return meta_generator;
	}

	public void setMeta_generator(String meta_generator) {
		this.meta_generator = meta_generator;
	}

	public String getMeta_author() {
		return meta_author;
	}

	public void setMeta_author(String meta_author) {
		this.meta_author = meta_author;
	}

	public int getApp_download() {
		return app_download;
	}

	public void setApp_download(int app_download) {
		this.app_download = app_download;
	}

	public String getAndroid_download() {
		return android_download;
	}

	public void setAndroid_download(String android_download) {
		this.android_download = android_download;
	}

	public String getIos_download() {
		return ios_download;
	}

	public void setIos_download(String ios_download) {
		this.ios_download = ios_download;
	}

	public int getGrouplife_order_return() {
		return grouplife_order_return;
	}

	public void setGrouplife_order_return(int grouplife_order_return) {
		this.grouplife_order_return = grouplife_order_return;
	}

	public BigDecimal getPayoff_all_amount_reality() {
		return payoff_all_amount_reality;
	}

	public void setPayoff_all_amount_reality(
			BigDecimal payoff_all_amount_reality) {
		this.payoff_all_amount_reality = payoff_all_amount_reality;
	}

	public BigDecimal getPayoff_all_sale() {
		return payoff_all_sale;
	}

	public void setPayoff_all_sale(BigDecimal payoff_all_sale) {
		this.payoff_all_sale = payoff_all_sale;
	}

	public BigDecimal getPayoff_all_commission() {
		return payoff_all_commission;
	}

	public void setPayoff_all_commission(BigDecimal payoff_all_commission) {
		this.payoff_all_commission = payoff_all_commission;
	}

	public BigDecimal getPayoff_all_amount() {
		return payoff_all_amount;
	}

	public void setPayoff_all_amount(BigDecimal payoff_all_amount) {
		this.payoff_all_amount = payoff_all_amount;
	}

	public BigDecimal getOfcard_mobile_profit() {
		return ofcard_mobile_profit;
	}

	public void setOfcard_mobile_profit(BigDecimal ofcard_mobile_profit) {
		this.ofcard_mobile_profit = ofcard_mobile_profit;
	}

	public boolean isOfcard() {
		return ofcard;
	}

	public void setOfcard(boolean ofcard) {
		this.ofcard = ofcard;
	}

	public String getOfcard_userid() {
		return ofcard_userid;
	}

	public void setOfcard_userid(String ofcard_userid) {
		this.ofcard_userid = ofcard_userid;
	}

	public String getOfcard_userpws() {
		return ofcard_userpws;
	}

	public void setOfcard_userpws(String ofcard_userpws) {
		this.ofcard_userpws = ofcard_userpws;
	}

	public Date getPayoff_date() {
		return payoff_date;
	}

	public void setPayoff_date(Date payoff_date) {
		this.payoff_date = payoff_date;
	}

	public int getPayoff_mag_type() {
		return payoff_mag_type;
	}

	public void setPayoff_mag_type(int payoff_mag_type) {
		this.payoff_mag_type = payoff_mag_type;
	}

	public String getPayoff_mag_default() {
		return payoff_mag_default;
	}

	public void setPayoff_mag_default(String payoff_mag_default) {
		this.payoff_mag_default = payoff_mag_default;
	}

	public String getPayoff_mag_auto() {
		return payoff_mag_auto;
	}

	public void setPayoff_mag_auto(String payoff_mag_auto) {
		this.payoff_mag_auto = payoff_mag_auto;
	}

	public int getGroup_meal_gold() {
		return group_meal_gold;
	}

	public void setGroup_meal_gold(int group_meal_gold) {
		this.group_meal_gold = group_meal_gold;
	}

	public int getPayoff_count() {
		return payoff_count;
	}

	public void setPayoff_count(int payoff_count) {
		this.payoff_count = payoff_count;
	}

	public Accessory getWeixin_qr_img() {
		return weixin_qr_img;
	}

	public void setWeixin_qr_img(Accessory weixin_qr_img) {
		this.weixin_qr_img = weixin_qr_img;
	}

	public String getWeixin_account() {
		return weixin_account;
	}

	public void setWeixin_account(String weixin_account) {
		this.weixin_account = weixin_account;
	}

	public String getWeixin_token() {
		return weixin_token;
	}

	public void setWeixin_token(String weixin_token) {
		this.weixin_token = weixin_token;
	}

	public String getWeixin_appId() {
		return weixin_appId;
	}

	public void setWeixin_appId(String weixin_appId) {
		this.weixin_appId = weixin_appId;
	}

	public String getWeixin_appSecret() {
		return weixin_appSecret;
	}

	public void setWeixin_appSecret(String weixin_appSecret) {
		this.weixin_appSecret = weixin_appSecret;
	}

	public String getWeixin_welecome_content() {
		return weixin_welecome_content;
	}

	public void setWeixin_welecome_content(String weixin_welecome_content) {
		this.weixin_welecome_content = weixin_welecome_content;
	}

	public Accessory getStore_weixin_logo() {
		return store_weixin_logo;
	}

	public void setStore_weixin_logo(Accessory store_weixin_logo) {
		this.store_weixin_logo = store_weixin_logo;
	}

	public boolean isWeixin_store() {
		return weixin_store;
	}

	public void setWeixin_store(boolean weixin_store) {
		this.weixin_store = weixin_store;
	}

	public int getAuto_order_return() {
		return auto_order_return;
	}

	public void setAuto_order_return(int auto_order_return) {
		this.auto_order_return = auto_order_return;
	}

	public int getAuto_order_evaluate() {
		return auto_order_evaluate;
	}

	public void setAuto_order_evaluate(int auto_order_evaluate) {
		this.auto_order_evaluate = auto_order_evaluate;
	}

	public int getZtc_goods_view() {
		return ztc_goods_view;
	}

	public void setZtc_goods_view(int ztc_goods_view) {
		this.ztc_goods_view = ztc_goods_view;
	}

	public String getWebsiteCss() {
		return websiteCss;
	}

	public void setWebsiteCss(String websiteCss) {
		this.websiteCss = websiteCss;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public boolean isUc_bbs() {
		return uc_bbs;
	}

	public void setUc_bbs(boolean uc_bbs) {
		this.uc_bbs = uc_bbs;
	}

	public List<Accessory> getLogin_imgs() {
		return login_imgs;
	}

	public void setLogin_imgs(List<Accessory> login_imgs) {
		this.login_imgs = login_imgs;
	}

	public Date getLucene_update() {
		return lucene_update;
	}

	public void setLucene_update(Date lucene_update) {
		this.lucene_update = lucene_update;
	}

	public boolean isSina_login() {
		return sina_login;
	}

	public void setSina_login(boolean sina_login) {
		this.sina_login = sina_login;
	}

	public String getSina_login_id() {
		return sina_login_id;
	}

	public void setSina_login_id(String sina_login_id) {
		this.sina_login_id = sina_login_id;
	}

	public String getSina_login_key() {
		return sina_login_key;
	}

	public void setSina_login_key(String sina_login_key) {
		this.sina_login_key = sina_login_key;
	}

	public String getSina_domain_code() {
		return sina_domain_code;
	}

	public void setSina_domain_code(String sina_domain_code) {
		this.sina_domain_code = sina_domain_code;
	}

	public boolean isQq_login() {
		return qq_login;
	}

	public void setQq_login(boolean qq_login) {
		this.qq_login = qq_login;
	}

	public String getQq_login_id() {
		return qq_login_id;
	}

	public void setQq_login_id(String qq_login_id) {
		this.qq_login_id = qq_login_id;
	}

	public String getQq_login_key() {
		return qq_login_key;
	}

	public void setQq_login_key(String qq_login_key) {
		this.qq_login_key = qq_login_key;
	}

	public int getDomain_allow_count() {
		return domain_allow_count;
	}

	public void setDomain_allow_count(int domain_allow_count) {
		this.domain_allow_count = domain_allow_count;
	}

	public String getSys_domain() {
		return sys_domain;
	}

	public void setSys_domain(String sys_domain) {
		this.sys_domain = sys_domain;
	}

	public boolean isZtc_status() {
		return ztc_status;
	}

	public void setZtc_status(boolean ztc_status) {
		this.ztc_status = ztc_status;
	}

	public int getZtc_price() {
		return ztc_price;
	}

	public void setZtc_price(int ztc_price) {
		this.ztc_price = ztc_price;
	}

	public String getTemplates() {
		return templates;
	}

	public void setTemplates(String templates) {
		this.templates = templates;
	}

	public boolean isStore_allow() {
		return store_allow;
	}

	public void setStore_allow(boolean store_allow) {
		this.store_allow = store_allow;
	}

	public Accessory getStoreImage() {
		return storeImage;
	}

	public void setStoreImage(Accessory storeImage) {
		this.storeImage = storeImage;
	}

	public Accessory getGoodsImage() {
		return goodsImage;
	}

	public void setGoodsImage(Accessory goodsImage) {
		this.goodsImage = goodsImage;
	}

	public Accessory getMemberIcon() {
		return memberIcon;
	}

	public void setMemberIcon(Accessory memberIcon) {
		this.memberIcon = memberIcon;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public int getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(int emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public String getEmailUserName() {
		return emailUserName;
	}

	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}

	public String getEmailPws() {
		return emailPws;
	}

	public void setEmailPws(String emailPws) {
		this.emailPws = emailPws;
	}

	public String getSysLanguage() {
		return sysLanguage;
	}

	public void setSysLanguage(String sysLanguage) {
		this.sysLanguage = sysLanguage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getSmsURL() {
		return smsURL;
	}

	public void setSmsURL(String smsURL) {
		this.smsURL = smsURL;
	}

	public String getSmsUserName() {
		return smsUserName;
	}

	public void setSmsUserName(String smsUserName) {
		this.smsUserName = smsUserName;
	}

	public String getSmsPassword() {
		return smsPassword;
	}

	public void setSmsPassword(String smsPassword) {
		this.smsPassword = smsPassword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIntegralRate() {
		return integralRate;
	}

	public void setIntegralRate(int integralRate) {
		this.integralRate = integralRate;
	}

	public String getCopyRight() {
		return copyRight;
	}

	public void setCopyRight(String copyRight) {
		this.copyRight = copyRight;
	}

	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getHotSearch() {
		return hotSearch;
	}

	public void setHotSearch(String hotSearch) {
		this.hotSearch = hotSearch;
	}

	public Accessory getWebsiteLogo() {
		return websiteLogo;
	}

	public void setWebsiteLogo(Accessory websiteLogo) {
		this.websiteLogo = websiteLogo;
	}

	public String getCodeStat() {
		return codeStat;
	}

	public void setCodeStat(String codeStat) {
		this.codeStat = codeStat;
	}

	public boolean isWebsiteState() {
		return websiteState;
	}

	public void setWebsiteState(boolean websiteState) {
		this.websiteState = websiteState;
	}

	public String getCloseReason() {
		return closeReason;
	}

	public void setCloseReason(String closeReason) {
		this.closeReason = closeReason;
	}

	public boolean isEmailEnable() {
		return emailEnable;
	}

	public void setEmailEnable(boolean emailEnable) {
		this.emailEnable = emailEnable;
	}

	public String getEmailTest() {
		return emailTest;
	}

	public void setEmailTest(String emailTest) {
		this.emailTest = emailTest;
	}

	public boolean isSecurityCodeRegister() {
		return securityCodeRegister;
	}

	public void setSecurityCodeRegister(boolean securityCodeRegister) {
		this.securityCodeRegister = securityCodeRegister;
	}

	public boolean isSecurityCodeLogin() {
		return securityCodeLogin;
	}

	public void setSecurityCodeLogin(boolean securityCodeLogin) {
		this.securityCodeLogin = securityCodeLogin;
	}

	public boolean isSecurityCodeConsult() {
		return securityCodeConsult;
	}

	public void setSecurityCodeConsult(boolean securityCodeConsult) {
		this.securityCodeConsult = securityCodeConsult;
	}

	public boolean isVisitorConsult() {
		return visitorConsult;
	}

	public void setVisitorConsult(boolean visitorConsult) {
		this.visitorConsult = visitorConsult;
	}

	public String getImageSuffix() {
		return imageSuffix;
	}

	public void setImageSuffix(String imageSuffix) {
		this.imageSuffix = imageSuffix;
	}

	public int getImageFilesize() {
		return imageFilesize;
	}

	public void setImageFilesize(int imageFilesize) {
		this.imageFilesize = imageFilesize;
	}

	public int getSmallWidth() {
		return smallWidth;
	}

	public void setSmallWidth(int smallWidth) {
		this.smallWidth = smallWidth;
	}

	public int getSmallHeight() {
		return smallHeight;
	}

	public void setSmallHeight(int smallHeight) {
		this.smallHeight = smallHeight;
	}

	public int getMiddleWidth() {
		return middleWidth;
	}

	public void setMiddleWidth(int middleWidth) {
		this.middleWidth = middleWidth;
	}

	public int getMiddleHeight() {
		return middleHeight;
	}

	public void setMiddleHeight(int middleHeight) {
		this.middleHeight = middleHeight;
	}

	public int getBigWidth() {
		return bigWidth;
	}

	public void setBigWidth(int bigWidth) {
		this.bigWidth = bigWidth;
	}

	public int getBigHeight() {
		return bigHeight;
	}

	public void setBigHeight(int bigHeight) {
		this.bigHeight = bigHeight;
	}

	public String getImageSaveType() {
		return imageSaveType;
	}

	public void setImageSaveType(String imageSaveType) {
		this.imageSaveType = imageSaveType;
	}

	public String getSecurityCodeType() {
		return securityCodeType;
	}

	public void setSecurityCodeType(String securityCodeType) {
		this.securityCodeType = securityCodeType;
	}

	public boolean isIntegral() {
		return integral;
	}

	public void setIntegral(boolean integral) {
		this.integral = integral;
	}

	public boolean isIntegralStore() {
		return integralStore;
	}

	public void setIntegralStore(boolean integralStore) {
		this.integralStore = integralStore;
	}

	public boolean isVoucher() {
		return voucher;
	}

	public void setVoucher(boolean voucher) {
		this.voucher = voucher;
	}

	public boolean isDeposit() {
		return deposit;
	}

	public void setDeposit(boolean deposit) {
		this.deposit = deposit;
	}

	public boolean isGroupBuy() {
		return groupBuy;
	}

	public void setGroupBuy(boolean groupBuy) {
		this.groupBuy = groupBuy;
	}

	public boolean isGold() {
		return gold;
	}

	public void setGold(boolean gold) {
		this.gold = gold;
	}

	public int getGoldMarketValue() {
		return goldMarketValue;
	}

	public void setGoldMarketValue(int goldMarketValue) {
		this.goldMarketValue = goldMarketValue;
	}

	public int getMemberRegister() {
		return memberRegister;
	}

	public void setMemberRegister(int memberRegister) {
		this.memberRegister = memberRegister;
	}

	public int getMemberDayLogin() {
		return memberDayLogin;
	}

	public void setMemberDayLogin(int memberDayLogin) {
		this.memberDayLogin = memberDayLogin;
	}

	public int getIndentComment() {
		return indentComment;
	}

	public void setIndentComment(int indentComment) {
		this.indentComment = indentComment;
	}

	public int getConsumptionRatio() {
		return consumptionRatio;
	}

	public void setConsumptionRatio(int consumptionRatio) {
		this.consumptionRatio = consumptionRatio;
	}

	public int getEveryIndentLimit() {
		return everyIndentLimit;
	}

	public void setEveryIndentLimit(int everyIndentLimit) {
		this.everyIndentLimit = everyIndentLimit;
	}

	public boolean isSmsEnbale() {
		return smsEnbale;
	}

	public void setSmsEnbale(boolean smsEnbale) {
		this.smsEnbale = smsEnbale;
	}

	public String getSmsTest() {
		return smsTest;
	}

	public void setSmsTest(String smsTest) {
		this.smsTest = smsTest;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStore_payment() {
		return store_payment;
	}

	public void setStore_payment(String store_payment) {
		this.store_payment = store_payment;
	}

	public String getShare_code() {
		return share_code;
	}

	public void setShare_code(String share_code) {
		this.share_code = share_code;
	}

	public String getUser_level() {
		return user_level;
	}

	public void setUser_level(String user_level) {
		this.user_level = user_level;
	}

	public int getComplaint_time() {
		return complaint_time;
	}

	public void setComplaint_time(int complaint_time) {
		this.complaint_time = complaint_time;
	}

	public boolean isSecond_domain_open() {
		return second_domain_open;
	}

	public void setSecond_domain_open(boolean second_domain_open) {
		this.second_domain_open = second_domain_open;
	}

	public String getQq_domain_code() {
		return qq_domain_code;
	}

	public void setQq_domain_code(String qq_domain_code) {
		this.qq_domain_code = qq_domain_code;
	}

	public String getImageWebServer() {
		return imageWebServer;
	}

	public void setImageWebServer(String imageWebServer) {
		this.imageWebServer = imageWebServer;
	}

	public String getService_telphone_list() {
		return service_telphone_list;
	}

	public void setService_telphone_list(String service_telphone_list) {
		this.service_telphone_list = service_telphone_list;
	}

	public String getService_qq_list() {
		return service_qq_list;
	}

	public void setService_qq_list(String service_qq_list) {
		this.service_qq_list = service_qq_list;
	}

	public int getAuto_order_confirm() {
		return auto_order_confirm;
	}

	public void setAuto_order_confirm(int auto_order_confirm) {
		this.auto_order_confirm = auto_order_confirm;
	}

	public int getAuto_order_notice() {
		return auto_order_notice;
	}

	public void setAuto_order_notice(int auto_order_notice) {
		this.auto_order_notice = auto_order_notice;
	}

	public String getKuaidi_id() {
		return kuaidi_id;
	}

	public void setKuaidi_id(String kuaidi_id) {
		this.kuaidi_id = kuaidi_id;
	}

	public String getUc_database() {
		return uc_database;
	}

	public void setUc_database(String uc_database) {
		this.uc_database = uc_database;
	}

	public String getUc_table_preffix() {
		return uc_table_preffix;
	}

	public void setUc_table_preffix(String uc_table_preffix) {
		this.uc_table_preffix = uc_table_preffix;
	}

	public String getUc_database_url() {
		return uc_database_url;
	}

	public void setUc_database_url(String uc_database_url) {
		this.uc_database_url = uc_database_url;
	}

	public String getUc_database_port() {
		return uc_database_port;
	}

	public void setUc_database_port(String uc_database_port) {
		this.uc_database_port = uc_database_port;
	}

	public String getUc_database_username() {
		return uc_database_username;
	}

	public void setUc_database_username(String uc_database_username) {
		this.uc_database_username = uc_database_username;
	}

	public String getUc_database_pws() {
		return uc_database_pws;
	}

	public void setUc_database_pws(String uc_database_pws) {
		this.uc_database_pws = uc_database_pws;
	}

	public String getUc_api() {
		return uc_api;
	}

	public void setUc_api(String uc_api) {
		this.uc_api = uc_api;
	}

	public String getUc_ip() {
		return uc_ip;
	}

	public void setUc_ip(String uc_ip) {
		this.uc_ip = uc_ip;
	}

	public String getUc_key() {
		return uc_key;
	}

	public void setUc_key(String uc_key) {
		this.uc_key = uc_key;
	}

	public String getUc_appid() {
		return uc_appid;
	}

	public void setUc_appid(String uc_appid) {
		this.uc_appid = uc_appid;
	}

	public int getCombin_amount() {
		return combin_amount;
	}

	public void setCombin_amount(int combin_amount) {
		this.combin_amount = combin_amount;
	}

	public int getCombin_count() {
		return combin_count;
	}

	public void setCombin_count(int combin_count) {
		this.combin_count = combin_count;
	}

	public int getInviteRegister() {
		return inviteRegister;
	}

	public void setInviteRegister(int inviterRegister) {
		this.inviteRegister = inviterRegister;
	}

	public int getShareRegister() {
		return shareRegister;
	}

	public void setShareRegister(int shareRegister) {
		this.shareRegister = shareRegister;
	}

	public int getLuckDraw() {
		return luckDraw;
	}

	public void setLuckDraw(int luckDraw) {
		this.luckDraw = luckDraw;
	}

	public String getAwardOne() {
		return awardOne;
	}

	public void setAwardOne(String awardOne) {
		this.awardOne = awardOne;
	}

	public String getAwardTwo() {
		return awardTwo;
	}

	public void setAwardTwo(String awardTwo) {
		this.awardTwo = awardTwo;
	}

	public String getAwardThree() {
		return awardThree;
	}

	public void setAwardThree(String awardThree) {
		this.awardThree = awardThree;
	}

	public String getAwardFour() {
		return awardFour;
	}

	public void setAwardFour(String awardFour) {
		this.awardFour = awardFour;
	}

	public String getAwardFive() {
		return awardFive;
	}

	public void setAwardFive(String awardFive) {
		this.awardFive = awardFive;
	}

	public String getAwardSix() {
		return awardSix;
	}

	public void setAwardSix(String awardSix) {
		this.awardSix = awardSix;
	}

	public String getAwardSeven() {
		return awardSeven;
	}

	public void setAwardSeven(String awardSeven) {
		this.awardSeven = awardSeven;
	}

	public String getAwardEight() {
		return awardEight;
	}

	public void setAwardEight(String awardEight) {
		this.awardEight = awardEight;
	}

	public String getAwardNine() {
		return awardNine;
	}

	public void setAwardNine(String awardNine) {
		this.awardNine = awardNine;
	}

	public String getAwardTen() {
		return awardTen;
	}

	public void setAwardTen(String awardTen) {
		this.awardTen = awardTen;
	}
	
	

}