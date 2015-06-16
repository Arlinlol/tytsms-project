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
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;

@Service
@Transactional
public class GoodsSpecPropertyServiceImpl implements IGoodsSpecPropertyService{
	@Resource(name = "goodsSpecPropertyDAO")
	private IGenericDAO<GoodsSpecProperty> goodsSpecPropertyDao;
	
	public boolean save(GoodsSpecProperty goodsSpecProperty) {
		/**
		 * init other field here
		 */
		try {
			this.goodsSpecPropertyDao.save(goodsSpecProperty);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsSpecProperty getObjById(Long id) {
		GoodsSpecProperty goodsSpecProperty = this.goodsSpecPropertyDao.get(id);
		if (goodsSpecProperty != null) {
			return goodsSpecProperty;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsSpecPropertyDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsSpecPropertyIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsSpecPropertyIds) {
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
		GenericPageList pList = new GenericPageList(GoodsSpecProperty.class, query,
				params, this.goodsSpecPropertyDao);
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
	
	public boolean update(GoodsSpecProperty goodsSpecProperty) {
		try {
			this.goodsSpecPropertyDao.update( goodsSpecProperty);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsSpecProperty> query(String query, Map params, int begin, int max){
		return this.goodsSpecPropertyDao.query(query, params, begin, max);
		
	}
}
