package com.taiyitao.elasticsearch;

/**
 * 
 * <p>
 * Title: IndexVo.java
 * </p>
 * 
 * <p>
 * Description: 全文检索控制类，该类完成商品索引建立、索引更新、索引删除，用户可以根据关键字分词搜索
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
 * @author erikzhang,jy,jxz
 * 
 * @date 2014-6-5
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class IndexVo {
//	public static final String ID = "id";
//	public static final String TITLE = "title";
//	public static final String TYPE = "type";
//	public static final String Cat = "cat";
//	public static final String CONTENT = "content";
//	public static final String URL = "url";
//	public static final String ADD_TIME = "add_time";
//	public static final String GOODS_SALENUM = "goods_salenum";
//	public static final String GOODS_COLLECT = "goods_collect";
//	public static final String WELL_EVALUATE = "well_evaluate";
//	public static final String STORE_PRICE = "store_price";
//	public static final String COST_PRICE = "cost_price";
//	public static final String GOODS_INVENTORY = "goods_inventory";
//	public static final String GOODS_TYPE = "goods_type";
//	public static final String GOODS_EVAS = "goods_evas";
//	public static final String MAIN_PHOTO_URL = "main_photo_url";
//	public static final String PHOTOS_URL = "photos_url";
//	public static final String GOODS_RATE = "goods_rate";
//	public static final String GOODS_AREA = "goods_area";
//	public static final String GOODS_BRAND = "goods_brand";
//	public static final String GOODS_CLASS = "goods_class";
	
	
	public static final String VO_ID = "vo_id";
	public static final String VO_TITLE = "vo_title";
	public static final String VO_TYPE = "vo_type";
	public static final String VO_CAT = "vo_cat";
	public static final String VO_CONTENT = "vo_content";
	public static final String VO_URL = "vo_url";
	public static final String VO_ADD_TIME = "vo_add_time";
	public static final String VO_GOODS_SALENUM = "vo_goods_salenum";
	public static final String VO_GOODS_COLLECT = "vo_goods_collect";
	public static final String VO_WELL_EVALUATE = "vo_well_evaluate";
	public static final String VO_STORE_PRICE = "vo_store_price";
	public static final String VO_COST_PRICE = "vo_cost_price";
	public static final String VO_GOODS_INVENTORY = "vo_goods_inventory";
	public static final String VO_GOODS_TYPE = "vo_goods_type";
	public static final String VO_GOODS_EVAS = "vo_goods_evas";
	public static final String VO_MAIN_PHOTO_URL = "main_photo_url";
	public static final String VO_PHOTOS_URL = "photos_url";
	public static final String VO_GOODS_RATE = "goods_rate";
	public static final String VO_GOODS_AREA = "vo_goods_area";
	public static final String VO_GOODS_BRAND = "vo_goods_brand";
	public static final String VO_GOODS_CLASS = "vo_goods_class";
	public static final String VO_STORE_USERNAME ="vo_store_username";

	private Long vo_id;// 商品ID
	private String vo_title;// 商品名称
	private String vo_content;// 商品描述，这里将会过滤html字符
	private String vo_type;// 索引类型，商品为goods，店铺为store ,团购为lifegoods
	private String vo_cat;// 商品分类，对应大商城商品分类
	private String vo_url;// 商品访问URL
	private String vo_rate;// 折扣率
	private long vo_add_time;// 商品添加时间
	private int vo_goods_salenum;// 商品销售数量
	private int vo_goods_collect;// 商品收藏数量
	private double vo_well_evaluate;// 商品好评率
	private double vo_store_price;// 商品店铺售价
	private double vo_cost_price;// 商品原价
	private int vo_goods_inventory;// 商品库存
	private int vo_goods_type;// 商品类型 1-全部 0-自营 1-第三方经营
	private int vo_goods_evas;// 商品评价数量
	private String vo_main_photo_url;// 主图片路径
	private String vo_photos_url;// 图片路径
	private String vo_goods_area;// 地区
	private String vo_goods_brand;// 商品所属品牌id
	private String vo_goods_class;// 商品所属分类,格式为：二级分类id+"_"+三级分类id
	private String vo_store_username;// 入驻商家的用户名
	private IndexName indexName;
	private IndexType indexType;
	
	public enum IndexName {
		GOODS
	}
	
	public enum IndexType {
		GOODS,
		LIFEGOODS,
		GROUPLIFEGOODS,
		GROUPGOODS
	}
	
	public IndexName getIndexName() {
		return indexName;
	}

	public void setIndexName(IndexName indexName) {
		this.indexName = indexName;
	}

	public IndexType getIndexType() {
		return indexType;
	}

	public void setIndexType(IndexType indexType) {
		this.indexType = indexType;
	}

	public String getVo_store_username() {
		return vo_store_username;
	}

	public void setVo_store_username(String vo_store_username) {
		this.vo_store_username = vo_store_username;
	}

	public String getVo_goods_brand() {
		return vo_goods_brand;
	}

	public void setVo_goods_brand(String vo_goods_brand) {
		this.vo_goods_brand = vo_goods_brand;
	}

	public String getVo_goods_area() {
		return vo_goods_area;
	}

	public void setVo_goods_area(String vo_goods_area) {
		this.vo_goods_area = vo_goods_area;
	}

	public double getVo_cost_price() {
		return vo_cost_price;
	}

	public void setVo_cost_price(double vo_cost_price) {
		this.vo_cost_price = vo_cost_price;
	}

	public String getVo_rate() {
		return vo_rate;
	}

	public void setVo_rate(String vo_rate) {
		this.vo_rate = vo_rate;
	}

	public String getVo_photos_url() {
		return vo_photos_url;
	}

	public void setVo_photos_url(String vo_photos_url) {
		this.vo_photos_url = vo_photos_url;
	}

	public int getVo_goods_evas() {
		return vo_goods_evas;
	}

	public void setVo_goods_evas(int vo_goods_evas) {
		this.vo_goods_evas = vo_goods_evas;
	}

	public String getVo_main_photo_url() {
		return vo_main_photo_url;
	}

	public void setVo_main_photo_url(String vo_main_photo_url) {
		this.vo_main_photo_url = vo_main_photo_url;
	}

	public int getVo_goods_type() {
		return vo_goods_type;
	}

	public void setVo_goods_type(int vo_goods_type) {
		this.vo_goods_type = vo_goods_type;
	}

	public String getVo_cat() {
		return vo_cat;
	}

	public void setVo_cat(String vo_cat) {
		this.vo_cat = vo_cat;
	}

	public long getVo_add_time() {
		return vo_add_time;
	}

	public void setVo_add_time(long vo_add_time) {
		this.vo_add_time = vo_add_time;
	}

	public int getVo_goods_salenum() {
		return vo_goods_salenum;
	}

	public void setVo_goods_salenum(int vo_goods_salenum) {
		this.vo_goods_salenum = vo_goods_salenum;
	}

	public double getVo_store_price() {
		return vo_store_price;
	}

	public void setVo_store_price(double vo_store_price) {
		this.vo_store_price = vo_store_price;
	}

	public Long getVo_id() {
		return vo_id;
	}

	public void setVo_id(Long vo_id) {
		this.vo_id = vo_id;
	}

	public String getVo_type() {
		return vo_type;
	}

	public void setVo_type(String vo_type) {
		this.vo_type = vo_type;
	}

	public String getVo_title() {
		return vo_title;
	}

	public void setVo_title(String vo_title) {
		this.vo_title = vo_title;
	}

	public String getVo_content() {
		return vo_content;
	}

	public void setVo_content(String vo_content) {
		this.vo_content = vo_content;
	}

	public String getVo_url() {
		return vo_url;
	}

	public void setVo_url(String vo_url) {
		this.vo_url = vo_url;
	}

	public int getVo_goods_collect() {
		return vo_goods_collect;
	}

	public void setVo_goods_collect(int vo_goods_collect) {
		this.vo_goods_collect = vo_goods_collect;
	}

	public double getVo_well_evaluate() {
		return vo_well_evaluate;
	}

	public void setVo_well_evaluate(double vo_well_evaluate) {
		this.vo_well_evaluate = vo_well_evaluate;
	}

	public int getVo_goods_inventory() {
		return vo_goods_inventory;
	}

	public void setVo_goods_inventory(int vo_goods_inventory) {
		this.vo_goods_inventory = vo_goods_inventory;
	}

	public String getVo_goods_class() {
		return vo_goods_class;
	}

	public void setVo_goods_class(String vo_goods_class) {
		this.vo_goods_class = vo_goods_class;
	}

}
