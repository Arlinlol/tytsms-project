package com.iskyshop.foundation.service.impl;

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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.service.IAccessoryService;

@Service
@Transactional
public class AccessoryService implements IAccessoryService {
	@Resource(name = "accessoryDAO")
	private IGenericDAO<Accessory> accessoryDAO;

	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.accessoryDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Accessory getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.accessoryDAO.get(id);
	}

	public boolean save(Accessory acc) {
		// TODO Auto-generated method stub
		try {
			this.accessoryDAO.save(acc);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean update(Accessory acc) {
		// TODO Auto-generated method stub
		try {
			this.accessoryDAO.update(acc);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<Accessory> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.accessoryDAO.query(query, params, begin, max);

	}

	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Accessory.class, query,
				params, this.accessoryDAO);
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

	@Override
	public Accessory getObjByProperty(String propertyName, String value) {
		// TODO Auto-generated method stub
		return this.accessoryDAO.getBy(propertyName, value);
	}

}
