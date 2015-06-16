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
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.service.IStoreService;

@Service
@Transactional
public class StoreServiceImpl implements IStoreService{
	@Resource(name = "storeDAO")
	private IGenericDAO<Store> storeDao;
	
	public boolean save(Store store) {
		/**
		 * init other field here
		 */
		try {
			this.storeDao.save(store);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Store getObjById(Long id) {
		Store store = this.storeDao.get(id);
		if (store != null) {
			return store;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.storeDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> storeIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeIds) {
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
		GenericPageList pList = new GenericPageList(Store.class, query,
				params, this.storeDao);
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
	
	public boolean update(Store store) {
		try {
			this.storeDao.update( store);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Store> query(String query, Map params, int begin, int max){
		return this.storeDao.query(query, params, begin, max);
		
	}

	@Override
	public Store getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.storeDao.getBy(propertyName, value);
	}
}
