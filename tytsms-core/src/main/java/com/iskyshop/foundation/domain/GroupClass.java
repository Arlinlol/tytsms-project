package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: GroupClass.java</p>

* <p>Description: 团购分类管理类 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_class")
public class GroupClass extends IdEntity {
	private String gc_name;// 团购分类名称
	private int gc_sequence;// 分了序号，按照升序排列
	private int gc_type;//0为商品类分类，1为生活类分类
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupClass parent;// 父级分类
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy(value = "gc_sequence asc")
	private List<GroupClass> childs = new ArrayList<GroupClass>();// 子级分类
	@OneToMany(mappedBy = "gg_gc", cascade = CascadeType.REMOVE)
	private List<GroupGoods> ggs = new ArrayList<GroupGoods>();// 团购分类中对应的团购商品

	public int getGc_type() {
		return gc_type;
	}

	public void setGc_type(int gc_type) {
		this.gc_type = gc_type;
	}

	public List<GroupGoods> getGgs() {
		return ggs;
	}

	public void setGgs(List<GroupGoods> ggs) {
		this.ggs = ggs;
	}
	private int gc_level;// 层级

	public int getGc_level() {
		return gc_level;
	}

	public void setGc_level(int gc_level) {
		this.gc_level = gc_level;
	}

	public String getGc_name() {
		return gc_name;
	}

	public void setGc_name(String gc_name) {
		this.gc_name = gc_name;
	}

	public int getGc_sequence() {
		return gc_sequence;
	}

	public void setGc_sequence(int gc_sequence) {
		this.gc_sequence = gc_sequence;
	}

	public GroupClass getParent() {
		return parent;
	}

	public void setParent(GroupClass parent) {
		this.parent = parent;
	}

	public List<GroupClass> getChilds() {
		return childs;
	}

	public void setChilds(List<GroupClass> childs) {
		this.childs = childs;
	}

}
