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
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.service.IActivityService;

@Service
@Transactional
public class ActivityServiceImpl implements IActivityService{
	@Resource(name = "activityDAO")
	private IGenericDAO<Activity> activityDao;
	
	public boolean save(Activity activity) {
		/**
		 * init other field here
		 */
		try {
			this.activityDao.save(activity);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Activity getObjById(Long id) {
		Activity activity = this.activityDao.get(id);
		if (activity != null) {
			return activity;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.activityDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> activityIds) {
		// TODO Auto-generated method stub
		for (Serializable id : activityIds) {
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
		GenericPageList pList = new GenericPageList(Activity.class, query,
				params, this.activityDao);
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
	
	public boolean update(Activity activity) {
		try {
			this.activityDao.update( activity);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Activity> query(String query, Map params, int begin, int max){
		return this.activityDao.query(query, params, begin, max);
		
	}
}
