package com.iskyshop.moudle.chatting.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Chatting.java
 * </p>
 * 
 * <p>
 * Description: 每个店铺或者平台的聊天组件信息设置类，可以设置的内容包括客服名称，自动回复内容、字体、字体大小、字体颜色
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
 * @date 2014年5月26日
 * 
 * @version 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chatting_config")
public class ChattingConfig extends IdEntity {
	private Long store_id;// 对应店铺id，每个店铺只对应一个ChattingConfig
	private String kf_name;// 客服自定义名称，
	@Column(columnDefinition = "LongText")
	private String quick_reply_content;// 客服快速回复信息
	@Column(columnDefinition = "int default 0")
	private int quick_reply_open;// 自动回复是否开启，0为未开启，1为开启
	@Column(columnDefinition = "int default 0")
	private int config_type;// 类型，0为商家，1为平台，当为1时store_id为空
	private String font;// 字体，商家发布保存信息是保存该信息
	private String font_size;// 字体大小
	private String font_colour;// 字体颜色

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFont_size() {
		return font_size;
	}

	public void setFont_size(String font_size) {
		this.font_size = font_size;
	}

	public String getFont_colour() {
		return font_colour;
	}

	public void setFont_colour(String font_colour) {
		this.font_colour = font_colour;
	}

	public String getQuick_reply_content() {
		return quick_reply_content;
	}

	public void setQuick_reply_content(String quick_reply_content) {
		this.quick_reply_content = quick_reply_content;
	}

	public int getConfig_type() {
		return config_type;
	}

	public void setConfig_type(int config_type) {
		this.config_type = config_type;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public String getKf_name() {
		return kf_name;
	}

	public void setKf_name(String kf_name) {
		this.kf_name = kf_name;
	}

	public int getQuick_reply_open() {
		return quick_reply_open;
	}

	public void setQuick_reply_open(int quick_reply_open) {
		this.quick_reply_open = quick_reply_open;
	}

}
