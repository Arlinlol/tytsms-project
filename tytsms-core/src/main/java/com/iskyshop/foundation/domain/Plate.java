package com.iskyshop.foundation.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
 * 功能描述：商家关联板式类 商家用来管理商品详情信息
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author cty 新增日期：2015年3月3日
 * @author cty 修改日期：2015年3月3日
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "plate")
public class Plate extends IdEntity {

	private static final long serialVersionUID = 8357177711861947088L;

	private String plate_name;     //关联板式名称
	private Long plate_position; //关联板式位置 1顶部，0底部
	private String plate_content;  //关联板式内容

	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 导航对应的store

	public String getPlate_name() {
		return plate_name;
	}

	public void setPlate_name(String plate_name) {
		this.plate_name = plate_name;
	}

	public Long getPlate_position() {
		return plate_position;
	}

	public void setPlate_position(Long plate_position) {
		this.plate_position = plate_position;
	}

	public String getPlate_content() {
		return plate_content;
	}

	public void setPlate_content(String plate_content) {
		this.plate_content = plate_content;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
