package com.iskyshop.foundation.domain;

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
 * 功能描述：用户抽奖类，用来管理用户消费积分抽奖
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author cty 新增日期：2015年2月26日
 * @author cty 修改日期：2015年2月26日
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "award")
public class Award extends IdEntity{

	private static final long serialVersionUID = 229189959961883458L;

	private String award_name; //奖品名称
	@Column(columnDefinition = "int default 0")
	private Long award_number; //奖品等级 
	@Column(columnDefinition = "int default 0")
	private Long award_type; //奖品类型 1虚拟商品 2实体商品
	private int award_integral; //用户消费的积分
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User user; // 抽奖对应的用户

	public String getAward_name() {
		return award_name;
	}

	public void setAward_name(String award_name) {
		this.award_name = award_name;
	}

	public Long getAward_number() {
		return award_number;
	}

	public void setAward_number(Long award_number) {
		this.award_number = award_number;
	}

	public Long getAward_type() {
		return award_type;
	}

	public void setAward_type(Long award_type) {
		this.award_type = award_type;
	}

	public int getAward_integral() {
		return award_integral;
	}

	public void setAward_integral(int award_integral) {
		this.award_integral = award_integral;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
