package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: Role.java</p>

* <p>Description: 角色管理类，用来存储系统角色信息，角色直接和权限相关</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "role")
public class Role extends IdEntity implements java.lang.Comparable {
	private String roleName;// 角色名称
	private String roleCode;// 角色编码，ss根据该编码来识别角色
	private String type;// ADMIN为超级后台权限，SELLER为卖家权限,BULLER为买家
	private String info;// 角色说明
	@Column(columnDefinition = "bit default true")
	private boolean display;// 是否显示角色
	private int sequence;// 排序
	@ManyToOne(fetch = FetchType.LAZY)
	private RoleGroup rg;
	@ManyToMany(targetEntity = Res.class, fetch = FetchType.LAZY)
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "role_res", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "res_id"))
	private List<Res> reses = new ArrayList<Res>();

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<Res> getReses() {
		return reses;
	}

	public void setReses(List<Res> reses) {
		this.reses = reses;
	}

	@Override
	public int compareTo(Object obj) {
		// TODO Auto-generated method stub
		Role role = (Role) obj;
		if (super.getId().equals(role.getId())) {
			return 0;
		}
		return 1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public RoleGroup getRg() {
		return rg;
	}

	public void setRg(RoleGroup rg) {
		this.rg = rg;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}
}
