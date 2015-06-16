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
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.service.IRoleService;

@Service
@Transactional
public class RoleService implements IRoleService {
	@Resource(name = "roleDAO")
	private IGenericDAO<Role> roleDAO;

	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.roleDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Role getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.roleDAO.get(id);
	}

	public List<Role> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.roleDAO.query(query, params, begin, max);
	}

	public boolean save(Role role) {
		// TODO Auto-generated method stub
		try {
			this.roleDAO.save(role);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(Role role) {
		// TODO Auto-generated method stub
		try {
			this.roleDAO.update(role);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Role.class, query, params,
				this.roleDAO);
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
	public Role getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.roleDAO.getBy(propertyName, value);
	}

}
