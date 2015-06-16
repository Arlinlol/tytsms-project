package com.iskyshop.foundation.domain.virtual;

import java.math.BigDecimal;

public class FootPointView {
	private String fpv_goods_name;
	private Long fpv_goods_id;
	private String fpv_goods_img_path;
	private int fpv_goods_sale;
	private BigDecimal fpv_goods_price;
	private Long fpv_goods_class_id;
	private String fpv_goods_class_name;

	
	public Long getFpv_goods_class_id() {
		return fpv_goods_class_id;
	}

	public void setFpv_goods_class_id(Long fpv_goods_class_id) {
		this.fpv_goods_class_id = fpv_goods_class_id;
	}

	public String getFpv_goods_class_name() {
		return fpv_goods_class_name;
	}

	public void setFpv_goods_class_name(String fpv_goods_class_name) {
		this.fpv_goods_class_name = fpv_goods_class_name;
	}

	public BigDecimal getFpv_goods_price() {
		return fpv_goods_price;
	}

	public void setFpv_goods_price(BigDecimal fpv_goods_price) {
		this.fpv_goods_price = fpv_goods_price;
	}

	public int getFpv_goods_sale() {
		return fpv_goods_sale;
	}

	public void setFpv_goods_sale(int fpv_goods_sale) {
		this.fpv_goods_sale = fpv_goods_sale;
	}

	public String getFpv_goods_name() {
		return fpv_goods_name;
	}

	public void setFpv_goods_name(String fpv_goods_name) {
		this.fpv_goods_name = fpv_goods_name;
	}

	public Long getFpv_goods_id() {
		return fpv_goods_id;
	}

	public void setFpv_goods_id(Long fpv_goods_id) {
		this.fpv_goods_id = fpv_goods_id;
	}

	public String getFpv_goods_img_path() {
		return fpv_goods_img_path;
	}

	public void setFpv_goods_img_path(String fpv_goods_img_path) {
		this.fpv_goods_img_path = fpv_goods_img_path;
	}

}
