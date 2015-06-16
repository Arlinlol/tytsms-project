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
* <p>Title: Favorite.java</p>

* <p>Description:用户收藏类,用来描述用户收藏的商品、收藏的店铺 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "favorite")
public class Favorite extends IdEntity {
	private int type;// 收藏类型，0为商品收藏、1为店铺收藏
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;// 收藏的商品
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 收藏的店铺
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 收藏对应的用户

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
