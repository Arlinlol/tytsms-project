package com.iskyshop.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: Navigation.java</p>

* <p>Description:系统导航类,显示在前端页面的导航位置，包括顶部导航、中间位置、底部导航 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "navigation")
public class Navigation extends IdEntity {
	private String title;// 导航标题
	private int location;// 导航位置,-1为顶部、0为中间、1为底部
	private String url;// 导航URL及静态化后的url
	private String original_url;// 原始url
	private int sequence;// 导航序号
	private boolean display;// 是否显示
	private int new_win;// 是否新窗口打开，1为新窗口打开，0为默认页面打开
	private String type;// 导航类型，diy为自定义导航、goodsclass为商品分类、articleclass为文章分类、activity为系统活动，、sparegoods为闲置商品页面导航
	private Long type_id;// 导航对应的导航类里面商品分类、文章分类、系统活动的ID值
	private boolean sysNav;// 是否系统默认导航，默认导航不可删除

	public boolean isSysNav() {
		return sysNav;
	}

	public void setSysNav(boolean sysNav) {
		this.sysNav = sysNav;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getType_id() {
		return type_id;
	}

	public void setType_id(Long type_id) {
		this.type_id = type_id;
	}

	public int getNew_win() {
		return new_win;
	}

	public void setNew_win(int new_win) {
		this.new_win = new_win;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public String getOriginal_url() {
		return original_url;
	}

	public void setOriginal_url(String original_url) {
		this.original_url = original_url;
	}

}
