package com.iskyshop.moudle.chatting.domain;

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
 * Title: ChattingLog.java
 * </p>
 * 
 * <p>
 * Description: 聊天记录管理类,用户向平台或者商家发送消息，
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
 * @date 2014-5-22
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chattinglog")
public class ChattingLog extends IdEntity {
	private Long store_id;// 该记录店铺客服发言人id
	private Long user_id;// 该记录用户发言人id
	@Column(columnDefinition = "int default 0")
	private int user_read;// 用户是否已读标记，0为未读，1为已读，如该条信息由商家客服发送，该字段表示用户是否已读商家信息，
	@Column(columnDefinition = "int default 0")
	private int store_read;// 商家客服是否已读标记，0为未读，1为已读，如该条信息用户发送，该字段表示商家是否已读用户信息，
	@Column(columnDefinition = "int default 0")
	private int plat_read;// 平台客服是否已读标记，0为未读，1为已读，如该条信息用户发送，该字段表示平台是否已读用户信息，
	private String font;// 保存聊天记录时字体信息
	private String font_size;// 保存聊天记录时字体大小信息
	private String font_colour;//  保存聊天记录时字体颜色信息
	@ManyToOne(fetch = FetchType.LAZY)
	private Chatting chatting;// 对应会话管理类
	@Column(columnDefinition = "LongText")
	private String content;// 聊天内容

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

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Chatting getChatting() {
		return chatting;
	}

	public void setChatting(Chatting chatting) {
		this.chatting = chatting;
	}

	public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public int getUser_read() {
		return user_read;
	}

	public void setUser_read(int user_read) {
		this.user_read = user_read;
	}

	public int getStore_read() {
		return store_read;
	}

	public void setStore_read(int store_read) {
		this.store_read = store_read;
	}

	public int getPlat_read() {
		return plat_read;
	}

	public void setPlat_read(int plat_read) {
		this.plat_read = plat_read;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
