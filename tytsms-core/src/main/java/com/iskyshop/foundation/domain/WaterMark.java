package com.iskyshop.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: WaterMark.java</p>

* <p>Description: 水印管理类 每个店铺，可以管理自己的水印</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "watermark")
public class WaterMark extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private Store store;// 水印对应的店铺
	private boolean wm_image_open;// 是否开启图片水印
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory wm_image;// 水印图片
	private float wm_image_alpha;// 水印图片透明度
	private int wm_image_pos;// 水印图片位置
	private boolean wm_text_open;// 是否开启
	private String wm_text;// 水印文字
	private int wm_text_pos;// 水印文字位置
	private int wm_text_font_size;// 水印文字字号
	private String wm_text_font;// 水印文字字体
	private String wm_text_color;// 水印文字颜色
	@OneToOne(fetch = FetchType.LAZY)
	private User user;// 水印对应的自营管理员用户

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Accessory getWm_image() {
		return wm_image;
	}

	public void setWm_image(Accessory wm_image) {
		this.wm_image = wm_image;
	}

	public float getWm_image_alpha() {
		return wm_image_alpha;
	}

	public void setWm_image_alpha(float wm_image_alpha) {
		this.wm_image_alpha = wm_image_alpha;
	}

	public int getWm_image_pos() {
		return wm_image_pos;
	}

	public void setWm_image_pos(int wm_image_pos) {
		this.wm_image_pos = wm_image_pos;
	}

	public String getWm_text() {
		return wm_text;
	}

	public void setWm_text(String wm_text) {
		this.wm_text = wm_text;
	}

	public int getWm_text_pos() {
		return wm_text_pos;
	}

	public void setWm_text_pos(int wm_text_pos) {
		this.wm_text_pos = wm_text_pos;
	}

	public String getWm_text_font() {
		return wm_text_font;
	}

	public void setWm_text_font(String wm_text_font) {
		this.wm_text_font = wm_text_font;
	}

	public String getWm_text_color() {
		return wm_text_color;
	}

	public void setWm_text_color(String wm_text_color) {
		this.wm_text_color = wm_text_color;
	}

	public boolean isWm_image_open() {
		return wm_image_open;
	}

	public void setWm_image_open(boolean wm_image_open) {
		this.wm_image_open = wm_image_open;
	}

	public boolean isWm_text_open() {
		return wm_text_open;
	}

	public void setWm_text_open(boolean wm_text_open) {
		this.wm_text_open = wm_text_open;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public int getWm_text_font_size() {
		return wm_text_font_size;
	}

	public void setWm_text_font_size(int wm_text_font_size) {
		this.wm_text_font_size = wm_text_font_size;
	}
}
