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
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.service.IUserGoodsClassService;

@Service
@Transactional
public class UserGoodsClassServiceImpl implements IUserGoodsClassService{
	@Resource(name = "userGoodsClassDAO")
	private IGenericDAO<UserGoodsClass> userGoodsClassDao;
	
	public boolean save(UserGoodsClass userGoodsClass) {
		/**
		 * init other field here
		 */
		try {
			this.userGoodsClassDao.save(userGoodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public UserGoodsClass getObjById(Long id) {
		UserGoodsClass userGoodsClass = this.userGoodsClassDao.get(id);
		if (userGoodsClass != null) {
			return userGoodsClass;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.userGoodsClassDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> userGoodsClassIds) {
		// TODO Auto-generated method stub
		for (Serializable id : userGoodsClassIds) {
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
		GenericPageList pList = new GenericPageList(UserGoodsClass.class, query,
				params, this.userGoodsClassDao);
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
	
	public boolean update(UserGoodsClass userGoodsClass) {
		try {
			this.userGoodsClassDao.update( userGoodsClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<UserGoodsClass> query(String query, Map params, int begin, int max){
		return this.userGoodsClassDao.query(query, params, begin, max);
		
	}
}
