package com.iskyshop.foundation.domain;

import java.util.ArrayList;
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

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: Album.java</p>

* <p>Description: 系统相册类，分相册管理系统图片</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "album")
public class Album extends IdEntity {
	private String album_name;// 相册名称
	private int album_sequence;// 相册序号
	@OneToMany(mappedBy = "album", cascade = CascadeType.REMOVE)
	private List<Accessory> photos = new ArrayList<Accessory>();// 相册内的图片
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory album_cover;// 相册封面
	private boolean album_default;// 是否默认相册，系统只有一个默认相册
	@Lob
	@Column(columnDefinition = "LongText")
	private String alblum_info;// 相册说明
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 相册用户

	public String getAlbum_name() {
		return album_name;
	}

	public void setAlbum_name(String album_name) {
		this.album_name = album_name;
	}

	public List<Accessory> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Accessory> photos) {
		this.photos = photos;
	}

	public String getAlblum_info() {
		return alblum_info;
	}

	public void setAlblum_info(String alblum_info) {
		this.alblum_info = alblum_info;
	}

	public int getAlbum_sequence() {
		return album_sequence;
	}

	public void setAlbum_sequence(int album_sequence) {
		this.album_sequence = album_sequence;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isAlbum_default() {
		return album_default;
	}

	public void setAlbum_default(boolean album_default) {
		this.album_default = album_default;
	}

	public Accessory getAlbum_cover() {
		return album_cover;
	}

	public void setAlbum_cover(Accessory album_cover) {
		this.album_cover = album_cover;
	}
}
