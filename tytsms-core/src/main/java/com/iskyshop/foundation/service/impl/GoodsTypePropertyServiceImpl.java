package com.iskyshop.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.GoodsTypeProperty;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;

@Service
@Transactional
public class GoodsTypePropertyServiceImpl implements IGoodsTypePropertyService{
	@Resource(name = "goodsTypePropertyDAO")
	private IGenericDAO<GoodsTypeProperty> goodsTypePropertyDao;
	
	public boolean save(GoodsTypeProperty goodsTypeProperty) {
		/**
		 * init other field here
		 */
		try {
			this.goodsTypePropertyDao.save(goodsTypeProperty);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsTypeProperty getObjById(Long id) {
		GoodsTypeProperty goodsTypeProperty = this.goodsTypePropertyDao.get(id);
		if (goodsTypeProperty != null) {
			return goodsTypeProperty;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsTypePropertyDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsTypePropertyIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsTypePropertyIds) {
			delete((Long) id);
		}
		return true;
	}
	
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(GoodsTypeProperty.class, query,
				params, this.goodsTypePropertyDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update(GoodsTypeProperty goodsTypeProperty) {
		try {
			this.goodsTypePropertyDao.update( goodsTypeProperty);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsTypeProperty> query(String query,Map params, int begin, int max){
		return this.goodsTypePropertyDao.query(query, params, begin, max);
		
	}

}
