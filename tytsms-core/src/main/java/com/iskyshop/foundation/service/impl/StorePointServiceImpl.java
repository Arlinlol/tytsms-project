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
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.service.IStorePointService;

@Service
@Transactional
public class StorePointServiceImpl implements IStorePointService{
	@Resource(name = "storePointDAO")
	private IGenericDAO<StorePoint> storePointDao;
	
	public boolean save(StorePoint storePoint) {
		/**
		 * init other field here
		 */
		try {
			this.storePointDao.save(storePoint);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public StorePoint getObjById(Long id) {
		StorePoint storePoint = this.storePointDao.get(id);
		if (storePoint != null) {
			return storePoint;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.storePointDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> storePointIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storePointIds) {
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
		GenericPageList pList = new GenericPageList(StorePoint.class, query,
				params, this.storePointDao);
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
	
	public boolean update(StorePoint storePoint) {
		try {
			this.storePointDao.update( storePoint);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<StorePoint> query(String query, Map params, int begin, int max){
		return this.storePointDao.query(query, params, begin, max);
		
	}
}
