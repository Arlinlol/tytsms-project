package com.iskyshop.moudle.chatting.domain;

import java.util.ArrayList;
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

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Chatting.java
 * </p>
 * 
 * <p>
 * Description: 系统客服与用户会话管理类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chatting")
public class Chatting extends IdEntity {
	private Long user_id;// 与store对话的用户id
	private String user_name;// 与store对话的用户名称
	@ManyToOne(fetch = FetchType.LAZY)
	private ChattingConfig config;// 对应商家或者平台会话信息设置管理类
	@OneToMany(mappedBy = "chatting", cascade = CascadeType.REMOVE)
	private List<ChattingLog> logs = new ArrayList<ChattingLog>();// 聊天记录
	@Column(columnDefinition = "int default 0")
	private int chatting_display;// 是否显示，0为显示、-1为不显示、当商家客服在其对话窗口将对话用户关闭后，该用户不再显示，除非该用户发送新消息
	private String font;// 用户的字体信息，用户保存聊天记录时保存该信息
	private String font_size;// 用户字体大小
	private String font_colour;// 用户字体颜色
	private Long goods_id;// 最近咨询商品id

	public Long getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

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

	public ChattingConfig getConfig() {
		return config;
	}

	public void setConfig(ChattingConfig config) {
		this.config = config;
	}

	public int getChatting_display() {
		return chatting_display;
	}

	public void setChatting_display(int chatting_display) {
		this.chatting_display = chatting_display;
	}

	public List<ChattingLog> getLogs() {
		return logs;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public void setLogs(List<ChattingLog> logs) {
		this.logs = logs;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

}
