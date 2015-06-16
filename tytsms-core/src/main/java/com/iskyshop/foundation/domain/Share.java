package com.iskyshop.foundation.domain;

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
 * 功能描述：用户分享类 ，用来管理用户分享信息
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author cty 新增日期：2015年3月2日
 * @author cty 修改日期：2015年3月2日
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "share")
public class Share extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3918770064785265770L;

	private String share_code; //分享标识

	@ManyToOne(fetch = FetchType.LAZY)
	private User user; // 分享对应的用户

	private String share_title; //分享标题
	private Long share_type; //分享类型  1抽奖活动 2让嘴巴去旅行活动专题
	
	public String getShare_code() {
		return share_code;
	}

	public void setShare_code(String share_code) {
		this.share_code = share_code;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getShare_title() {
		return share_title;
	}

	public void setShare_title(String share_title) {
		this.share_title = share_title;
	}

	public Long getShare_type() {
		return share_type;
	}

	public void setShare_type(Long share_type) {
		this.share_type = share_type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
