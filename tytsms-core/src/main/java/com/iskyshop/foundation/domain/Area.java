package com.iskyshop.foundation.domain;

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
* <p>Title: Area.java</p>

* <p>Description:  系统区域类，默认导入全国区域省、市、县（区）三级数据</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "area")
public class Area extends IdEntity {
	private String areaName;// 区域名称
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<Area> childs = new ArrayList<Area>();// 下级区域
	@ManyToOne(fetch = FetchType.LAZY)
	private Area parent;// 上级区域
	private int sequence;// 序号
	private int level;// 层级
	@Column(columnDefinition = "bit default false")
	private boolean common;// 常用地区，设置常用地区后该地区出现在在店铺搜索页常用地区位置

	public boolean isCommon() {
		return common;
	}

	public void setCommon(boolean common) {
		this.common = common;
	}

	public List<Area> getChilds() {
		return childs;
	}

	public void setChilds(List<Area> childs) {
		this.childs = childs;
	}

	public Area getParent() {
		return parent;
	}

	public void setParent(Area parent) {
		this.parent = parent;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	/*  2015-04-03 支持任意地址层级*/
	public static String getAreaInfo(Area area ,String sign) {
		String areaInfo = "";
		if(area !=null && area.getId() != null){
			areaInfo =  area.getAreaName();
			Area areaParent =  area.getParent();
			if(areaParent != null && areaParent.getId() != null){
				areaInfo =   getAreaInfo(areaParent , sign) + sign + areaInfo ;
			}
		}
		return areaInfo ;
	}

	
	public static String getAreaID(Area area ,String sign) {
		String areaId = "";
		if(area !=null && area.getId() != null){
			areaId =  area.getId().toString();
			Area areaParent =  area.getParent();
			if(areaParent != null && areaParent.getId() != null){
				areaId =   getAreaID(areaParent , sign) + sign + areaId ;
			}
		}
		return areaId ;
	}
	
}
