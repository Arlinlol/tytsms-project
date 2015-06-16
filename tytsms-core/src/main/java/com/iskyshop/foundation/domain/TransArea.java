package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
* <p>Title: TransArea.java</p>

* <p>Description:运费区域管理类，用来管理配送模板的配送区域，如：华东->安徽->安庆 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "trans_area")
public class TransArea extends IdEntity {
	private String areaName;// 区域名称
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<TransArea> childs = new ArrayList<TransArea>();// 下级区域
	@ManyToOne(fetch = FetchType.LAZY)
	private TransArea parent;// 上级区域
	private int sequence;// 序号
	private int level;// 层级

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<TransArea> getChilds() {
		return childs;
	}

	public void setChilds(List<TransArea> childs) {
		this.childs = childs;
	}

	public TransArea getParent() {
		return parent;
	}

	public void setParent(TransArea parent) {
		this.parent = parent;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
