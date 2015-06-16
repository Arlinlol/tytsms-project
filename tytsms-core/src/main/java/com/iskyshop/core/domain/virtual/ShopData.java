package com.iskyshop.core.domain.virtual;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: ShopData.java</p>

* <p>Description: 数据备份信息，该信息对应备份文件夹，和数据库表无关联 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class ShopData {
	private String name;// 备份名称
	private String phyPath;// 存储的物理路径
	private double size;// 数据大小
	private int boundSize;// 分卷数
	private Date addTime;// 备份时间

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhyPath() {
		return phyPath;
	}

	public void setPhyPath(String phyPath) {
		this.phyPath = phyPath;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getBoundSize() {
		return boundSize;
	}

	public void setBoundSize(int boundSize) {
		this.boundSize = boundSize;
	}
}
