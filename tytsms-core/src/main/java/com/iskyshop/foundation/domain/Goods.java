package com.iskyshop.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
 * Title: Goods.java
 * </p>
 * 
 * <p>
 * Description:商品实体类,用来描述系统商品信息，系统核心实体类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods")
public class Goods extends IdEntity {
	private String seo_keywords;// 关键字
	@Column(columnDefinition = "LongText")
	private String seo_description;// 描述
	private String goods_name;// 商品名称
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_price;// 商品原价
	@Column(precision = 12, scale = 2)
	private BigDecimal store_price;// 店铺价格
	private int goods_inventory;// 库存数量
	private String inventory_type;// 库存方式，分为all全局库存，spec按规格库存
	private int goods_salenum;// 商品售出数量
	private String goods_serial;// 商品货号
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_weight;// 商品重量
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_volume;// 商品体积
	private String goods_fee;// 运费
	@Column(columnDefinition = "LongText")
	private String goods_details;// 详细说明
	private boolean store_recommend;// 商城推荐
	private Date store_recommend_time;// 商品商城推荐时间
	private boolean goods_recommend;// 是否店铺推荐，推荐后在店铺首页推荐位显示
	private int goods_click;// 商品浏览次数
	@Column(columnDefinition = "int default 0")
	private int goods_collect;// 商品收藏次数
	@ManyToOne(fetch = FetchType.LAZY)
	private Store goods_store;// 所属店铺
	private int goods_status;// 商品当前状态，-5为平台未审核，
								// 0为上架，1为在仓库中，2为定时自动上架，3为店铺过期自动下架，-1为手动下架状态，-2为违规下架状态,-3被举报禁售
	private int publish_goods_status;// 商品发布审核后状态，
										// 0为发布后上架，1为发布后在仓库中，2为发布后定时自动上架，平台审核商品后根据该字段设定商品当前状态
	private Date goods_seller_time;// 商品上架时间，系统根据商品上架时间倒序排列
	private int goods_transfee;// 商品运费承担方式，0为买家承担，1为卖家承担
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass gc;// 商品对应的大分类
	@ManyToOne
	private Accessory goods_main_photo;// 商品主图片
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_photo", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "photo_id"))
	private List<Accessory> goods_photos = new ArrayList<Accessory>();// 商品其他图片，目前只允许4张,图片可以重复使用
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_ugc", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "class_id"))
	private List<UserGoodsClass> goods_ugcs = new ArrayList<UserGoodsClass>();// 店铺中商品所在分类
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_spec", joinColumns = @JoinColumn(name = "goods_id"), inverseJoinColumns = @JoinColumn(name = "spec_id"))
	@OrderBy(value = "sequence asc")
	private List<GoodsSpecProperty> goods_specs = new ArrayList<GoodsSpecProperty>();// 商品对应的规格值
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsBrand goods_brand;// 商品品牌
	/**
	 * 使用json管理数据 [{"val":"中长款（衣长50-70CM）","id":"32769","name":"衣长"},
	 * {"val":"纯棉","id":"32768","name":"材质"}, {"val":"短袖","id":"1","name":"款式"}]
	 */
	@Column(columnDefinition = "LongText")
	private String goods_property;
	/**
	 * 商品规格详细库存,使用json管理，[{"id":"131072_131080_","price":"144","count":"106"}]
	 */
	@Column(columnDefinition = "LongText")
	private String goods_inventory_detail;
	private int ztc_status;// 直通车状态，1为开通申请待审核，2为审核通过,-1为审核失败,3为已经开通
	private int ztc_pay_status;// 直通车金币支付状态，1为支付成功，0为待支付
	private int ztc_price;// 直通车价格,按照金币计算，单位为个
	private int ztc_dredge_price;// 已经开通的直通车价格,和ztc_price值一样，由系统定制器控制该值，在用户设定的开始日期后该值才会存在
	private Date ztc_apply_time;// 直通车申请时间
	@Temporal(TemporalType.DATE)
	private Date ztc_begin_time;// 直通车开始时间
	private int ztc_gold;// 直通车开通金币，扣除完以后自动取消直通车状态
	private int ztc_click_num;// 直通车商品浏览数
	@ManyToOne(fetch = FetchType.LAZY)
	private User ztc_admin;// 直通车审核管理员
	@Column(columnDefinition = "LongText")
	private String ztc_admin_content;// 直通车审核信息
	@OneToMany(mappedBy = "gg_goods", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<GroupGoods> group_goods_list = new ArrayList<GroupGoods>();// 商品对应的团购信息，一个商品可以参加多个团购，但是团购同一时间段只能发起一个
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;// 商品对应的团购,通过该字段判断商品当前参与的团购，团购商品审核通过后，给该字段赋值，同一时间段，一款商品只能保持一个团购活动状态
	@Column(columnDefinition = "int default 0")
	private int group_buy;// 团购状态，0为无团购，1为待审核，2为审核通过,3为团购商品已经卖完
	@OneToMany(mappedBy = "goods", cascade = CascadeType.REMOVE)
	private List<Consult> consults = new ArrayList<Consult>();// 商品的咨询
	@OneToMany(mappedBy = "evaluate_goods", cascade = CascadeType.REMOVE)
	private List<Evaluate> evaluates = new ArrayList<Evaluate>();// 商品对应的用户评价，映射评价实体中的evaluate_goods
	@OneToMany(mappedBy = "goods", cascade = CascadeType.REMOVE)
	private List<Favorite> favs = new ArrayList<Favorite>();// 商品对应的用户收藏
	@Column(columnDefinition = "int default 0")
	private int goods_choice_type;// 0实体商品，1为虚拟商品
	@Column(columnDefinition = "int default 0")
	private int activity_status;// 活动状态，0为无活动，1为待审核，2为审核通过，3为活动商品已经卖完
	@OneToMany(mappedBy = "ag_goods", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ActivityGoods> activity_goods_list = new ArrayList<ActivityGoods>();// 对应的活动商品列表
	@Column(precision = 12, scale = 2)
	private BigDecimal activity_price;// 铜牌会员商品价格，该商品为活动商品时
	@Column(precision = 12, scale = 2)
	private BigDecimal activity_price1;// 银牌会员商品价格，该商品为活动商品时
	@Column(precision = 12, scale = 2)
	private BigDecimal activity_price2;// 金牌会员商品价格，该商品为活动商品时
	@Column(precision = 12, scale = 2)
	private BigDecimal activity_price3;// 超级会员商品价格，该商品为活动商品时
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_current_price;// 商品当前价格,默认等于store_price，参加团购即为团购价，如团购、活动结束后,改价格自动变为store_price
	@Column(columnDefinition = "int default 0")
	private int combin_status;// 组合销售商品，0为无组合销售，1为待审核，2为审核通过,-1为拒绝通过
	@Temporal(TemporalType.DATE)
	private Date combin_begin_time;// 组合销售商品开始时间
	@Temporal(TemporalType.DATE)
	private Date combin_end_time;// 组合销售商品结束时间
	@Column(precision = 12, scale = 2)
	private BigDecimal combin_price;// 组合商品整体价格
	@Column(columnDefinition = "LongText")
	private String combin_goods;// 对应的组合商品，使用json管理{"goods_id1":1,"goods_id2":2}
	@Column(precision = 12, scale = 2)
	private BigDecimal mail_trans_fee;// 平邮费用
	@Column(precision = 12, scale = 2)
	private BigDecimal express_trans_fee;// 快递费用
	@Column(precision = 12, scale = 2)
	private BigDecimal ems_trans_fee;// EMS费用
	@ManyToOne(fetch = FetchType.LAZY)
	private Transport transport;// 调用的运费模板信息
	@Column(precision = 4, scale = 1, columnDefinition = "Decimal default 5.0")
	private BigDecimal description_evaluate;// 商品描述相符评分，默认为5分

	@Column(precision = 3, scale = 2)
	private BigDecimal well_evaluate;// 商品好评率,例如：该值为0.96，好评率即为96%
	@Column(precision = 3, scale = 2)
	private BigDecimal middle_evaluate;// 商品中评率
	@Column(precision = 3, scale = 2)
	private BigDecimal bad_evaluate;// 商品差评率
	@OneToMany(mappedBy = "ag_goods", cascade = CascadeType.REMOVE)
	private List<ActivityGoods> ag_goods_list = new ArrayList<ActivityGoods>();// 商城活动商品

	@Column(columnDefinition = "bit default false")
	private boolean weixin_shop_recommend;// 微信商城推荐，推荐后出现在微信商城（平台大商城）首页，最多显示3个，按照推荐时间先后排序
	@Temporal(TemporalType.DATE)
	private Date weixin_shop_recommendTime;// 微信商城推荐时间
	@Column(columnDefinition = "bit default false")
	private boolean weixin_shop_hot;// 微信商城热卖，设置后出现在微信商城（平台大商城）首页，最多显示3个，按照设置时间先后排序
	@Temporal(TemporalType.DATE)
	private Date weixin_shop_hotTime;// 微信商城热卖设置时间
	@Column(columnDefinition = "int default 0")
	private int goods_type;// 商品类型，0为自营商品，1为第三方经销商
	@ManyToOne(fetch = FetchType.LAZY)
	private User user_admin;// 当商品类型为自营商品时，对应的商品发布管理员
	@OneToMany(mappedBy = "goods")
	private List<GoodsCart> carts = new ArrayList<GoodsCart>();// 商品对应的购物车，反向映射
	@OneToMany(mappedBy = "goods")
	private List<ComplaintGoods> cgs = new ArrayList<ComplaintGoods>();// 商品对应的投诉，反向映射
	@Column(columnDefinition = "int default 0")
	private int mobile_recommend;// 手机客户端推荐， 1为推荐，推荐后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_recommendTime;// 手机推荐时间，

	@Column(columnDefinition = "int default 0")
	private int mobile_hot;// 手机客户端热卖， 1为热卖，设置后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_hotTime;// 手机热卖时间，

	//@author cty 2015-3-4
	private Long plateid_top;//顶部关联板式
	private Long plateid_bottom; //底部关联板式
	
	//@author cty 2015-4-15
	private String undercarriage_reasons;//违规下架原因
	
	public String getUndercarriage_reasons() {
		return undercarriage_reasons;
	}

	public void setUndercarriage_reasons(String undercarriage_reasons) {
		this.undercarriage_reasons = undercarriage_reasons;
	}

	public Long getPlateid_top() {
		return plateid_top;
	}

	public void setPlateid_top(Long plateid_top) {
		this.plateid_top = plateid_top;
	}

	public Long getPlateid_bottom() {
		return plateid_bottom;
	}

	public void setPlateid_bottom(Long plateid_bottom) {
		this.plateid_bottom = plateid_bottom;
	}
	
	public int getPublish_goods_status() {
		return publish_goods_status;
	}

	public void setPublish_goods_status(int publish_goods_status) {
		this.publish_goods_status = publish_goods_status;
	}

	public List<ActivityGoods> getActivity_goods_list() {
		return activity_goods_list;
	}

	public void setActivity_goods_list(List<ActivityGoods> activity_goods_list) {
		this.activity_goods_list = activity_goods_list;
	}

	public int getMobile_recommend() {
		return mobile_recommend;
	}

	public void setMobile_recommend(int mobile_recommend) {
		this.mobile_recommend = mobile_recommend;
	}

	public Date getMobile_recommendTime() {
		return mobile_recommendTime;
	}

	public void setMobile_recommendTime(Date mobile_recommendTime) {
		this.mobile_recommendTime = mobile_recommendTime;
	}

	public int getMobile_hot() {
		return mobile_hot;
	}

	public void setMobile_hot(int mobile_hot) {
		this.mobile_hot = mobile_hot;
	}

	public Date getMobile_hotTime() {
		return mobile_hotTime;
	}

	public void setMobile_hotTime(Date mobile_hotTime) {
		this.mobile_hotTime = mobile_hotTime;
	}

	public List<ComplaintGoods> getCgs() {
		return cgs;
	}

	public void setCgs(List<ComplaintGoods> cgs) {
		this.cgs = cgs;
	}

	public List<GoodsCart> getCarts() {
		return carts;
	}

	public void setCarts(List<GoodsCart> carts) {
		this.carts = carts;
	}

	public BigDecimal getActivity_price() {
		return activity_price;
	}

	public void setActivity_price(BigDecimal activity_price) {
		this.activity_price = activity_price;
	}

	public BigDecimal getActivity_price1() {
		return activity_price1;
	}

	public void setActivity_price1(BigDecimal activity_price1) {
		this.activity_price1 = activity_price1;
	}

	public BigDecimal getActivity_price2() {
		return activity_price2;
	}

	public void setActivity_price2(BigDecimal activity_price2) {
		this.activity_price2 = activity_price2;
	}

	public BigDecimal getActivity_price3() {
		return activity_price3;
	}

	public void setActivity_price3(BigDecimal activity_price3) {
		this.activity_price3 = activity_price3;
	}

	public BigDecimal getWell_evaluate() {
		return well_evaluate;
	}

	public void setWell_evaluate(BigDecimal well_evaluate) {
		this.well_evaluate = well_evaluate;
	}

	public BigDecimal getMiddle_evaluate() {
		return middle_evaluate;
	}

	public void setMiddle_evaluate(BigDecimal middle_evaluate) {
		this.middle_evaluate = middle_evaluate;
	}

	public BigDecimal getBad_evaluate() {
		return bad_evaluate;
	}

	public void setBad_evaluate(BigDecimal bad_evaluate) {
		this.bad_evaluate = bad_evaluate;
	}

	public User getUser_admin() {
		return user_admin;
	}

	public void setUser_admin(User user_admin) {
		this.user_admin = user_admin;
	}

	public int getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public boolean isWeixin_shop_recommend() {
		return weixin_shop_recommend;
	}

	public void setWeixin_shop_recommend(boolean weixin_shop_recommend) {
		this.weixin_shop_recommend = weixin_shop_recommend;
	}

	public Date getWeixin_shop_recommendTime() {
		return weixin_shop_recommendTime;
	}

	public void setWeixin_shop_recommendTime(Date weixin_shop_recommendTime) {
		this.weixin_shop_recommendTime = weixin_shop_recommendTime;
	}

	public boolean isWeixin_shop_hot() {
		return weixin_shop_hot;
	}

	public void setWeixin_shop_hot(boolean weixin_shop_hot) {
		this.weixin_shop_hot = weixin_shop_hot;
	}

	public Date getWeixin_shop_hotTime() {
		return weixin_shop_hotTime;
	}

	public void setWeixin_shop_hotTime(Date weixin_shop_hotTime) {
		this.weixin_shop_hotTime = weixin_shop_hotTime;
	}

	public BigDecimal getDescription_evaluate() {
		return description_evaluate;
	}

	public void setDescription_evaluate(BigDecimal description_evaluate) {
		this.description_evaluate = description_evaluate;
	}

	public List<ActivityGoods> getAg_goods_list() {
		return ag_goods_list;
	}

	public void setAg_goods_list(List<ActivityGoods> ag_goods_list) {
		this.ag_goods_list = ag_goods_list;
	}

	public List<Consult> getConsults() {
		return consults;
	}

	public void setConsults(List<Consult> consults) {
		this.consults = consults;
	}

	public int getGroup_buy() {
		return group_buy;
	}

	public void setGroup_buy(int group_buy) {
		this.group_buy = group_buy;
	}

	public int getZtc_status() {
		return ztc_status;
	}

	public void setZtc_status(int ztc_status) {
		this.ztc_status = ztc_status;
	}

	public int getZtc_price() {
		return ztc_price;
	}

	public void setZtc_price(int ztc_price) {
		this.ztc_price = ztc_price;
	}

	public Date getZtc_begin_time() {
		return ztc_begin_time;
	}

	public void setZtc_begin_time(Date ztc_begin_time) {
		this.ztc_begin_time = ztc_begin_time;
	}

	public int getZtc_gold() {
		return ztc_gold;
	}

	public void setZtc_gold(int ztc_gold) {
		this.ztc_gold = ztc_gold;
	}

	public String getSeo_keywords() {
		return seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public int getGoods_inventory() {
		return goods_inventory;
	}

	public void setGoods_inventory(int goods_inventory) {
		this.goods_inventory = goods_inventory;
	}

	public String getInventory_type() {
		return inventory_type;
	}

	public void setInventory_type(String inventory_type) {
		this.inventory_type = inventory_type;
	}

	public int getGoods_salenum() {
		return goods_salenum;
	}

	public void setGoods_salenum(int goods_salenum) {
		this.goods_salenum = goods_salenum;
	}

	public String getGoods_serial() {
		return goods_serial;
	}

	public void setGoods_serial(String goods_serial) {
		this.goods_serial = goods_serial;
	}

	public BigDecimal getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(BigDecimal goods_price) {
		this.goods_price = goods_price;
	}

	public BigDecimal getStore_price() {
		return store_price;
	}

	public void setStore_price(BigDecimal store_price) {
		this.store_price = store_price;
	}

	public BigDecimal getGoods_weight() {
		return goods_weight;
	}

	public void setGoods_weight(BigDecimal goods_weight) {
		this.goods_weight = goods_weight;
	}

	public String getGoods_fee() {
		return goods_fee;
	}

	public void setGoods_fee(String goods_fee) {
		this.goods_fee = goods_fee;
	}

	public String getGoods_details() {
		return goods_details;
	}

	public void setGoods_details(String goods_details) {
		this.goods_details = goods_details;
	}

	public boolean isStore_recommend() {
		return store_recommend;
	}

	public void setStore_recommend(boolean store_recommend) {
		this.store_recommend = store_recommend;
	}

	public Date getStore_recommend_time() {
		return store_recommend_time;
	}

	public void setStore_recommend_time(Date store_recommend_time) {
		this.store_recommend_time = store_recommend_time;
	}

	public boolean isGoods_recommend() {
		return goods_recommend;
	}

	public void setGoods_recommend(boolean goods_recommend) {
		this.goods_recommend = goods_recommend;
	}

	public int getGoods_click() {
		return goods_click;
	}

	public void setGoods_click(int goods_click) {
		this.goods_click = goods_click;
	}

	public Store getGoods_store() {
		return goods_store;
	}

	public void setGoods_store(Store goods_store) {
		this.goods_store = goods_store;
	}

	public int getGoods_status() {
		return goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public Date getGoods_seller_time() {
		return goods_seller_time;
	}

	public void setGoods_seller_time(Date goods_seller_time) {
		this.goods_seller_time = goods_seller_time;
	}

	public int getGoods_transfee() {
		return goods_transfee;
	}

	public void setGoods_transfee(int goods_transfee) {
		this.goods_transfee = goods_transfee;
	}

	public GoodsClass getGc() {
		if (gc != null)
			return gc;
		else
			return new GoodsClass();
	}

	public void setGc(GoodsClass gc) {
		this.gc = gc;
	}

	public Accessory getGoods_main_photo() {
		return goods_main_photo;
	}

	public void setGoods_main_photo(Accessory goods_main_photo) {
		this.goods_main_photo = goods_main_photo;
	}

	public List<Accessory> getGoods_photos() {
		return goods_photos;
	}

	public void setGoods_photos(List<Accessory> goods_photos) {
		this.goods_photos = goods_photos;
	}

	public List<UserGoodsClass> getGoods_ugcs() {
		return goods_ugcs;
	}

	public void setGoods_ugcs(List<UserGoodsClass> goods_ugcs) {
		this.goods_ugcs = goods_ugcs;
	}

	public List<GoodsSpecProperty> getGoods_specs() {
		return goods_specs;
	}

	public void setGoods_specs(List<GoodsSpecProperty> goods_specs) {
		this.goods_specs = goods_specs;
	}

	public GoodsBrand getGoods_brand() {
		return goods_brand;
	}

	public void setGoods_brand(GoodsBrand goods_brand) {
		this.goods_brand = goods_brand;
	}

	public String getGoods_property() {
		return goods_property;
	}

	public void setGoods_property(String goods_property) {
		this.goods_property = goods_property;
	}

	public String getGoods_inventory_detail() {
		return goods_inventory_detail;
	}

	public void setGoods_inventory_detail(String goods_inventory_detail) {
		this.goods_inventory_detail = goods_inventory_detail;
	}

	public int getZtc_pay_status() {
		return ztc_pay_status;
	}

	public void setZtc_pay_status(int ztc_pay_status) {
		this.ztc_pay_status = ztc_pay_status;
	}

	public User getZtc_admin() {
		return ztc_admin;
	}

	public void setZtc_admin(User ztc_admin) {
		this.ztc_admin = ztc_admin;
	}

	public String getZtc_admin_content() {
		return ztc_admin_content;
	}

	public void setZtc_admin_content(String ztc_admin_content) {
		this.ztc_admin_content = ztc_admin_content;
	}

	public Date getZtc_apply_time() {
		return ztc_apply_time;
	}

	public void setZtc_apply_time(Date ztc_apply_time) {
		this.ztc_apply_time = ztc_apply_time;
	}

	public int getZtc_click_num() {
		return ztc_click_num;
	}

	public void setZtc_click_num(int ztc_click_num) {
		this.ztc_click_num = ztc_click_num;
	}

	public int getZtc_dredge_price() {
		return ztc_dredge_price;
	}

	public void setZtc_dredge_price(int ztc_dredge_price) {
		this.ztc_dredge_price = ztc_dredge_price;
	}

	public int getGoods_collect() {
		return goods_collect;
	}

	public void setGoods_collect(int goods_collect) {
		this.goods_collect = goods_collect;
	}

	public List<GroupGoods> getGroup_goods_list() {
		return group_goods_list;
	}

	public void setGroup_goods_list(List<GroupGoods> group_goods_list) {
		this.group_goods_list = group_goods_list;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<Evaluate> getEvaluates() {
		return evaluates;
	}

	public void setEvaluates(List<Evaluate> evaluates) {
		this.evaluates = evaluates;
	}

	public int getGoods_choice_type() {
		return goods_choice_type;
	}

	public void setGoods_choice_type(int goods_choice_type) {
		this.goods_choice_type = goods_choice_type;
	}

	public int getActivity_status() {
		return activity_status;
	}

	public void setActivity_status(int activity_status) {
		this.activity_status = activity_status;
	}

	public BigDecimal getGoods_current_price() {
		return goods_current_price;
	}

	public void setGoods_current_price(BigDecimal goods_current_price) {
		this.goods_current_price = goods_current_price;
	}

	public List<Favorite> getFavs() {
		return favs;
	}

	public void setFavs(List<Favorite> favs) {
		this.favs = favs;
	}

	public BigDecimal getGoods_volume() {
		return goods_volume;
	}

	public void setGoods_volume(BigDecimal goods_volume) {
		this.goods_volume = goods_volume;
	}

	public BigDecimal getMail_trans_fee() {
		return mail_trans_fee;
	}

	public void setMail_trans_fee(BigDecimal mail_trans_fee) {
		this.mail_trans_fee = mail_trans_fee;
	}

	public BigDecimal getExpress_trans_fee() {
		return express_trans_fee;
	}

	public void setExpress_trans_fee(BigDecimal express_trans_fee) {
		this.express_trans_fee = express_trans_fee;
	}

	public BigDecimal getEms_trans_fee() {
		return ems_trans_fee;
	}

	public void setEms_trans_fee(BigDecimal ems_trans_fee) {
		this.ems_trans_fee = ems_trans_fee;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public String getCombin_goods() {
		return combin_goods;
	}

	public void setCombin_goods(String combin_goods) {
		this.combin_goods = combin_goods;
	}

	public int getCombin_status() {
		return combin_status;
	}

	public void setCombin_status(int combin_status) {
		this.combin_status = combin_status;
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

	public BigDecimal getCombin_price() {
		return combin_price;
	}

	public void setCombin_price(BigDecimal combin_price) {
		this.combin_price = combin_price;
	}

}
