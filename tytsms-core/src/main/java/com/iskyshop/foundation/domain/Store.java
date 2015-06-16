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
import javax.persistence.ManyToOne;
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
 * Title: Store.java
 * </p>
 * 
 * <p>
 * Description:商家店铺信息管理类，描述商家店铺信息
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store")
public class Store extends IdEntity {
	private String store_name;// 店铺名称
	private String store_ower;// 店主真实姓名
	private String store_ower_card;// 店主身份证号
	private String store_telephone;// 店铺电话号码
	private String store_qq;// 店铺qq
	private String store_msn;// 店铺msn
	private String store_ww;// 店铺淘宝旺旺
	private String store_address;// 店铺详细地址
	private String store_zip;// 店铺邮编
	@Column(columnDefinition = "int default 0")
	private int store_status;// 店铺状态，0为未提交开店申请，5公司等待信息审核,6公司信息审核失败,10店铺等待信息审核,11店铺信息审核失败，15为正常营业（审核成功）,20违规关闭，25到期关闭,30快速入驻
	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
	private User user;// 店铺对应的用户，反向映射
	@ManyToOne(fetch = FetchType.LAZY)
	private StoreGrade grade;// 店铺等级
	private Long gc_main_id;// 主营类目Id
	@Column(columnDefinition = "LongText")
	private String gc_detail_info;// 店铺经营类，[{"gc_list":[2, 8, 3, 6, 7,
									// 5],"m_id":1}]
	@ManyToOne(fetch = FetchType.LAZY)
	private Area area;// 店铺地址，这里保存的是最底层地址
	private boolean store_recommend;// 是否被推荐
	private Date store_recommend_time;// 店铺推荐时间,根据推荐时间倒序显示明星店铺
	private Date validity;// 店铺有效期，用在收费店铺等级，超过有效期，自动将为免费店铺,为空时表示无限期
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory store_logo;// 店铺logo
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory store_banner;// 店铺banner
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory card;// 认证身份证
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory store_license;// 店铺营业执照
	@OneToMany(mappedBy = "goods_store")
	private List<Goods> goods_list = new ArrayList<Goods>();// 店铺所属商品
	private int store_credit;// 店铺等级，根据好评数累加
	private String template;// 店铺模板，根据名称进行管理
	@Lob
	@Column(columnDefinition = "LongText")
	private String violation_reseaon;// 违规原因
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_seo_keywords;// 店铺SEO关键字
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_seo_description; // 店铺SEO描述
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_info;// 店铺介绍
	@OneToOne(fetch = FetchType.LAZY)
	private StoreGrade update_grade;// 申请升级的店铺等级
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<StoreSlide> slides = new ArrayList<StoreSlide>();// 店铺幻灯
	@Lock
	private String store_second_domain;// 店铺二级域名
	@Column(columnDefinition = "int default 0")
	private int domain_modify_count;// 店铺二级域名修改次数，受商城设置影响
	@Column(columnDefinition = "int default 0")
	private int favorite_count;// 店铺收藏人气
	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private StorePoint point;// 店铺评分统计
	private Date combin_begin_time;// 组合销售套餐开始日期
	private Date combin_end_time;// 组合销售套餐结束日期
	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private StorePoint sp;// 店铺评分统计类
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<StoreNavigation> navs = new ArrayList<StoreNavigation>();// 店铺对应的自定义导航
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Favorite> favs = new ArrayList<Favorite>();// 店铺被用户所收藏
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Transport> transport_list = new ArrayList<Transport>();// 运费模板信息
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();// 店铺添加的商品规格
	private String license_c_name;// 营业执照上公司名称
	private String license_num;// 营业执照号
	private String license_legal_name;// 法人姓名
	private String license_legal_idCard;// 法人身份证号
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory license_legal_idCard_image;// 法人身份证号
	@ManyToOne(fetch = FetchType.LAZY)
	private Area license_area;// 营业执照所在地
	private String license_address;// 营业执照详细地址
	private Date license_establish_date;// 营业执照上成立日期
	private Date license_start_date;// 营业期限开始时间
	private Date license_end_date;// 营业期限结束时间
	private String license_reg_capital;// 注册资金
	private String license_business_scope;// 经营范围
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory license_image;// 营业执照副本电子版
	@ManyToOne(fetch = FetchType.LAZY)
	private Area license_c_area;// 公司所在地
	private String license_c_address;// 公司详细地址
	private String license_c_telephone;// 公司电话
	private String license_c_contact;// 公司紧急联系人
	private String license_c_mobile;// 公司紧急联系人手机
	private String organization_code;// 组织机构代码证
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory organization_image;// 组织机构代码证电子版
	private String tax_code;// 纳税人识别号
	private String tax_type;// 纳税人类型
	private String tax_type_code;// 纳税人类型识别码
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory tax_reg_card;// 税务登记证电子版
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory tax_general_card;// 一般纳税人资格证电子版
	private String bank_account_name;// 银行开户名
	private String bank_c_account;// 公司银行账号
	private String bank_name;// 开户行支行名称
	private String bank_line_num;// 银行联行号
	@ManyToOne(fetch = FetchType.LAZY)
	private Area bank_area;// 开户行所在地
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory bank_permit_image;// 银行开户许可证电子版
	private Date store_start_time;// 管理员审核店铺通过的时间，即店铺开店时间
	private Date group_meal_endTime;// 团购套餐结束时间
	@Column(precision = 12, scale = 2)
	private BigDecimal store_sale_amount;// 店铺本次结算总销售金额，生成账单时总销售金额增加，账单结算完成后总结算金额减少
	@Column(precision = 12, scale = 2)
	private BigDecimal store_commission_amount;// 店铺本次结算总佣金,生成账单时增加，结算账单完成时减少
	@Column(precision = 12, scale = 2)
	private BigDecimal store_payoff_amount;// 店铺本次结算总结算金额,生成账单时增加，结算账单完成时减少
	@Column(precision = 10, scale = 2)
	private BigDecimal store_free_price;// 免邮额度

	
	public List<GoodsSpecification> getSpecs() {
		return specs;
	}

	public void setSpecs(List<GoodsSpecification> specs) {
		this.specs = specs;
	}

	public BigDecimal getStore_sale_amount() {
		return store_sale_amount;
	}

	public void setStore_sale_amount(BigDecimal store_sale_amount) {
		this.store_sale_amount = store_sale_amount;
	}

	public BigDecimal getStore_commission_amount() {
		return store_commission_amount;
	}

	public void setStore_commission_amount(BigDecimal store_commission_amount) {
		this.store_commission_amount = store_commission_amount;
	}

	public BigDecimal getStore_payoff_amount() {
		return store_payoff_amount;
	}

	public void setStore_payoff_amount(BigDecimal store_payoff_amount) {
		this.store_payoff_amount = store_payoff_amount;
	}

	public Date getGroup_meal_endTime() {
		return group_meal_endTime;
	}

	public void setGroup_meal_endTime(Date group_meal_endTime) {
		this.group_meal_endTime = group_meal_endTime;
	}

	public Long getGc_main_id() {
		return gc_main_id;

	}

	public void setGc_main_id(Long gc_main_id) {
		this.gc_main_id = gc_main_id;
	}

	public String getGc_detail_info() {
		return gc_detail_info;
	}

	public void setGc_detail_info(String gc_detail_info) {
		this.gc_detail_info = gc_detail_info;
	}

	public List<Transport> getTransport_list() {
		return transport_list;
	}

	public void setTransport_list(List<Transport> transport_list) {
		this.transport_list = transport_list;
	}

	public List<Favorite> getFavs() {
		return favs;
	}

	public void setFavs(List<Favorite> favs) {
		this.favs = favs;
	}

	public List<StoreNavigation> getNavs() {
		return navs;
	}

	public void setNavs(List<StoreNavigation> navs) {
		this.navs = navs;
	}

	public StorePoint getSp() {
		return sp;
	}

	public void setSp(StorePoint sp) {
		this.sp = sp;
	}

	public int getFavorite_count() {
		return favorite_count;
	}

	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}

	public String getStore_second_domain() {
		return store_second_domain;
	}

	public void setStore_second_domain(String store_second_domain) {
		this.store_second_domain = store_second_domain;
	}

	public int getDomain_modify_count() {
		return domain_modify_count;
	}

	public void setDomain_modify_count(int domain_modify_count) {
		this.domain_modify_count = domain_modify_count;
	}

	public List<StoreSlide> getSlides() {
		return slides;
	}

	public void setSlides(List<StoreSlide> slides) {
		this.slides = slides;
	}

	public String getViolation_reseaon() {
		return violation_reseaon;
	}

	public void setViolation_reseaon(String violation_reseaon) {
		this.violation_reseaon = violation_reseaon;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getStore_credit() {
		return store_credit;
	}

	public void setStore_credit(int store_credit) {
		this.store_credit = store_credit;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getStore_address() {
		return store_address;
	}

	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getStore_ower() {
		return store_ower;
	}

	public void setStore_ower(String store_ower) {
		this.store_ower = store_ower;
	}

	public String getStore_ower_card() {
		return store_ower_card;
	}

	public void setStore_ower_card(String store_ower_card) {
		this.store_ower_card = store_ower_card;
	}

	public StoreGrade getGrade() {
		return grade;
	}

	public void setGrade(StoreGrade grade) {
		this.grade = grade;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isStore_recommend() {
		return store_recommend;
	}

	public void setStore_recommend(boolean store_recommend) {
		this.store_recommend = store_recommend;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public Accessory getCard() {
		return card;
	}

	public void setCard(Accessory card) {
		this.card = card;
	}

	public Accessory getStore_license() {
		return store_license;
	}

	public void setStore_license(Accessory store_license) {
		this.store_license = store_license;
	}

	public int getStore_status() {
		return store_status;
	}

	public void setStore_status(int store_status) {
		this.store_status = store_status;
	}

	public String getStore_telephone() {
		return store_telephone;
	}

	public void setStore_telephone(String store_telephone) {
		this.store_telephone = store_telephone;
	}

	public String getStore_zip() {
		return store_zip;
	}

	public void setStore_zip(String store_zip) {
		this.store_zip = store_zip;
	}

	public Accessory getStore_logo() {
		return store_logo;
	}

	public void setStore_logo(Accessory store_logo) {
		this.store_logo = store_logo;
	}

	public Accessory getStore_banner() {
		return store_banner;
	}

	public void setStore_banner(Accessory store_banner) {
		this.store_banner = store_banner;
	}

	public String getStore_seo_keywords() {
		return store_seo_keywords;
	}

	public void setStore_seo_keywords(String store_seo_keywords) {
		this.store_seo_keywords = store_seo_keywords;
	}

	public String getStore_seo_description() {
		return store_seo_description;
	}

	public void setStore_seo_description(String store_seo_description) {
		this.store_seo_description = store_seo_description;
	}

	public String getStore_info() {
		return store_info;
	}

	public void setStore_info(String store_info) {
		this.store_info = store_info;
	}

	public StoreGrade getUpdate_grade() {
		return update_grade;
	}

	public void setUpdate_grade(StoreGrade update_grade) {
		this.update_grade = update_grade;
	}

	public Date getStore_recommend_time() {
		return store_recommend_time;
	}

	public void setStore_recommend_time(Date store_recommend_time) {
		this.store_recommend_time = store_recommend_time;
	}

	public String getStore_qq() {
		return store_qq;
	}

	public void setStore_qq(String store_qq) {
		this.store_qq = store_qq;
	}

	public String getStore_msn() {
		return store_msn;
	}

	public void setStore_msn(String store_msn) {
		this.store_msn = store_msn;
	}

	public StorePoint getPoint() {
		return point;
	}

	public void setPoint(StorePoint point) {
		this.point = point;
	}

	public String getStore_ww() {
		return store_ww;
	}

	public void setStore_ww(String store_ww) {
		this.store_ww = store_ww;
	}

	public Date getCombin_begin_time() {
		return combin_begin_time;
	}

	public void setCombin_begin_time(Date combin_begin_time) {
		this.combin_begin_time = combin_begin_time;
	}

	public Date getCombin_end_time() {
		return combin_end_time;
	}

	public void setCombin_end_time(Date combin_end_time) {
		this.combin_end_time = combin_end_time;
	}

	public String getLicense_c_name() {
		return license_c_name;
	}

	public void setLicense_c_name(String license_c_name) {
		this.license_c_name = license_c_name;
	}

	public String getLicense_num() {
		return license_num;
	}

	public void setLicense_num(String license_num) {
		this.license_num = license_num;
	}

	public String getLicense_legal_name() {
		return license_legal_name;
	}

	public void setLicense_legal_name(String license_legal_name) {
		this.license_legal_name = license_legal_name;
	}

	public String getLicense_legal_idCard() {
		return license_legal_idCard;
	}

	public void setLicense_legal_idCard(String license_legal_idCard) {
		this.license_legal_idCard = license_legal_idCard;
	}

	public Accessory getLicense_legal_idCard_image() {
		return license_legal_idCard_image;
	}

	public void setLicense_legal_idCard_image(
			Accessory license_legal_idCard_image) {
		this.license_legal_idCard_image = license_legal_idCard_image;
	}

	public Area getLicense_area() {
		return license_area;
	}

	public void setLicense_area(Area license_area) {
		this.license_area = license_area;
	}

	public String getLicense_address() {
		return license_address;
	}

	public void setLicense_address(String license_address) {
		this.license_address = license_address;
	}

	public Date getLicense_establish_date() {
		return license_establish_date;
	}

	public void setLicense_establish_date(Date license_establish_date) {
		this.license_establish_date = license_establish_date;
	}

	public Date getLicense_start_date() {
		return license_start_date;
	}

	public void setLicense_start_date(Date license_start_date) {
		this.license_start_date = license_start_date;
	}

	public Date getLicense_end_date() {
		return license_end_date;
	}

	public void setLicense_end_date(Date license_end_date) {
		this.license_end_date = license_end_date;
	}

	public String getLicense_reg_capital() {
		return license_reg_capital;
	}

	public void setLicense_reg_capital(String license_reg_capital) {
		this.license_reg_capital = license_reg_capital;
	}

	public String getLicense_business_scope() {
		return license_business_scope;
	}

	public void setLicense_business_scope(String license_business_scope) {
		this.license_business_scope = license_business_scope;
	}

	public Accessory getLicense_image() {
		return license_image;
	}

	public void setLicense_image(Accessory license_image) {
		this.license_image = license_image;
	}

	public Area getLicense_c_area() {
		return license_c_area;
	}

	public void setLicense_c_area(Area license_c_area) {
		this.license_c_area = license_c_area;
	}

	public String getLicense_c_address() {
		return license_c_address;
	}

	public void setLicense_c_address(String license_c_address) {
		this.license_c_address = license_c_address;
	}

	public String getLicense_c_telephone() {
		return license_c_telephone;
	}

	public void setLicense_c_telephone(String license_c_telephone) {
		this.license_c_telephone = license_c_telephone;
	}

	public String getLicense_c_contact() {
		return license_c_contact;
	}

	public void setLicense_c_contact(String license_c_contact) {
		this.license_c_contact = license_c_contact;
	}

	public String getLicense_c_mobile() {
		return license_c_mobile;
	}

	public void setLicense_c_mobile(String license_c_mobile) {
		this.license_c_mobile = license_c_mobile;
	}

	public String getOrganization_code() {
		return organization_code;
	}

	public void setOrganization_code(String organization_code) {
		this.organization_code = organization_code;
	}

	public Accessory getOrganization_image() {
		return organization_image;
	}

	public void setOrganization_image(Accessory organization_image) {
		this.organization_image = organization_image;
	}

	public String getTax_code() {
		return tax_code;
	}

	public void setTax_code(String tax_code) {
		this.tax_code = tax_code;
	}

	public String getTax_type() {
		return tax_type;
	}

	public void setTax_type(String tax_type) {
		this.tax_type = tax_type;
	}

	public String getTax_type_code() {
		return tax_type_code;
	}

	public void setTax_type_code(String tax_type_code) {
		this.tax_type_code = tax_type_code;
	}

	public Accessory getTax_reg_card() {
		return tax_reg_card;
	}

	public void setTax_reg_card(Accessory tax_reg_card) {
		this.tax_reg_card = tax_reg_card;
	}

	public Accessory getTax_general_card() {
		return tax_general_card;
	}

	public void setTax_general_card(Accessory tax_general_card) {
		this.tax_general_card = tax_general_card;
	}

	public String getBank_account_name() {
		return bank_account_name;
	}

	public void setBank_account_name(String bank_account_name) {
		this.bank_account_name = bank_account_name;
	}

	public String getBank_c_account() {
		return bank_c_account;
	}

	public void setBank_c_account(String bank_c_account) {
		this.bank_c_account = bank_c_account;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getBank_line_num() {
		return bank_line_num;
	}

	public void setBank_line_num(String bank_line_num) {
		this.bank_line_num = bank_line_num;
	}

	public Area getBank_area() {
		return bank_area;
	}

	public void setBank_area(Area bank_area) {
		this.bank_area = bank_area;
	}

	public Accessory getBank_permit_image() {
		return bank_permit_image;
	}

	public void setBank_permit_image(Accessory bank_permit_image) {
		this.bank_permit_image = bank_permit_image;
	}

	public Date getStore_start_time() {
		return store_start_time;
	}

	public void setStore_start_time(Date store_start_time) {
		this.store_start_time = store_start_time;
	}

	public BigDecimal getStore_free_price() {
		return store_free_price;
	}

	public void setStore_free_price(BigDecimal store_free_price) {
		this.store_free_price = store_free_price;
	}

}
