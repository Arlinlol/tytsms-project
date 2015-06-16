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
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.service.IGoodsSpecificationService;

@Service
@Transactional
public class GoodsSpecificationServiceImpl implements IGoodsSpecificationService{
	@Resource(name = "goodsSpecificationDAO")
	private IGenericDAO<GoodsSpecification> goodsSpecificationDao;
	
	public boolean save(GoodsSpecification goodsSpecification) {
		/**
		 * init other field here
		 */
		try {
			this.goodsSpecificationDao.save(goodsSpecification);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsSpecification getObjById(Long id) {
		GoodsSpecification goodsSpecification = this.goodsSpecificationDao.get(id);
		if (goodsSpecification != null) {
			return goodsSpecification;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsSpecificationDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsSpecificationIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsSpecificationIds) {
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
		GenericPageList pList = new GenericPageList(GoodsSpecification.class, query,
				params, this.goodsSpecificationDao);
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
	
	public boolean update(GoodsSpecification goodsSpecification) {
		try {
			this.goodsSpecificationDao.update( goodsSpecification);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsSpecification> query(String query,Map params, int begin, int max){
		return this.goodsSpecificationDao.query(query, params, begin, max);
		
	}
}
