package com.iskyshop.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IGoodsClassService;

@Service
@Transactional
public class GoodsClassServiceImpl implements IGoodsClassService{
	@Resource(name = "goodsClassDAO")
	private IGenericDAO<GoodsClass> goodsClassDao;
	
	public boolean save(GoodsClass goodsClass) {
		/**
		 * init other field here
		 */
		try {
			this.goodsClassDao.save(goodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsClass getObjById(Long id) {
		GoodsClass goodsClass = this.goodsClassDao.get(id);
		if (goodsClass != null) {
			return goodsClass;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsClassDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsClassIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsClassIds) {
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
		GenericPageList pList = new GenericPageList(GoodsClass.class, query,
				params, this.goodsClassDao);
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
	
	public boolean update(GoodsClass goodsClass) {
		try {
			this.goodsClassDao.update( goodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	

	@Override
	public List<GoodsClass> query(String query, Map params,int begin,int max) {
		// TODO Auto-generated method stub
		return this.goodsClassDao.query(query, params, begin, max);
	}

	@Override
	public GoodsClass getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.goodsClassDao.getBy(propertyName, value);
	}
}
