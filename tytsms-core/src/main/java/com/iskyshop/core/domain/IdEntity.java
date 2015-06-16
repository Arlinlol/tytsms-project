package com.iskyshop.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.iskyshop.core.annotation.Lock;

/**
 * 
 * <p>
 * Title: IdEntity.java
 * </p>
 * 
 * <p>
 * Description:
 * 系统域模型基类，该类包含3个常用字段，其中id为自增长类型，该类实现序列化，只有序列化后才可以实现tomcat集群配置session共享
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
@MappedSuperclass
public class IdEntity implements Serializable {
	/**
	 * 序列化接口，自动生成序列号
	 */
	private static final long serialVersionUID = -7741168269971132706L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	private Long id;// 域模型id，这里为自增类型
	private Date addTime;// 添加时间，这里为长时间格式
	@Lock
	@Column(columnDefinition = "int default 0")
	private int deleteStatus;// 是否删除,默认为0未删除，-1表示删除状态
	
	public IdEntity() {
		super();
	}

	public IdEntity(Long id, Date addTime) {
		super();
		this.id = id;
		this.addTime = addTime;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(int deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

}
