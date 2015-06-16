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
import com.iskyshop.foundation.domain.Plate;
import com.iskyshop.foundation.service.IPlateService;

@Service
@Transactional
public class PlateServiceImpl implements IPlateService {
	@Resource(name = "plateDAO")
	private IGenericDAO<Plate> plateDao;
	

	public boolean save(Plate plate) {
		/**
		 * init other field here
		 */
		try {
			this.plateDao.save(plate);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Plate getObjById(Long id) {
		Plate plate = this.plateDao.get(id);
		if (plate != null) {
			return plate;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.plateDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> plateIds) {
		// TODO Auto-generated method stub
		for (Serializable id : plateIds) {
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
		GenericPageList pList = new GenericPageList(Plate.class, query,
				params, this.plateDao);
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
	
	public boolean update(Plate plate) {
		try {
			this.plateDao.update(plate);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Plate> query(String query, Map params, int begin, int max){
		return this.plateDao.query(query, params, begin, max);
		
	}

}
