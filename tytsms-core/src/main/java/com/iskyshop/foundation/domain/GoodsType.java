package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: GoodsType.java</p>

* <p>Description:商品类型管理控制类，用来描述商品类型信息 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodstype")
public class GoodsType extends IdEntity {
	private String name;// 类型名称
	private int sequence;// 排序
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goodstype_brand", joinColumns = @JoinColumn(name = "type_id"), inverseJoinColumns = @JoinColumn(name = "brand_id"))
	private List<GoodsBrand> gbs = new ArrayList<GoodsBrand>();
	@OneToMany(mappedBy = "goodsType", cascade = CascadeType.REMOVE)
	@OrderBy(value="sequence asc")
	private List<GoodsTypeProperty> properties = new ArrayList<GoodsTypeProperty>();
	
	@OneToMany(mappedBy = "goodsType")
	private List<GoodsClass> gcs = new ArrayList<GoodsClass>();

	public List<GoodsClass> getGcs() {
		return gcs;
	}

	public void setGcs(List<GoodsClass> gcs) {
		this.gcs = gcs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<GoodsBrand> getGbs() {
		return gbs;
	}

	public void setGbs(List<GoodsBrand> gbs) {
		this.gbs = gbs;
	}

	public List<GoodsTypeProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<GoodsTypeProperty> properties) {
		this.properties = properties;
	}

}
