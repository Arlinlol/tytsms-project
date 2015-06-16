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
import com.iskyshop.foundation.domain.Res;
import com.iskyshop.foundation.service.IResService;

@Service
@Transactional
public class ResService implements IResService {
	@Resource(name = "resDAO")
	private IGenericDAO<Res> resDAO;

	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.resDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean save(Res res) {
		// TODO Auto-generated method stub
		try {
			this.resDAO.save(res);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(Res res) {
		// TODO Auto-generated method stub
		try {
			this.resDAO.update(res);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Res getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.resDAO.get(id);
	}

	public List<Res> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.resDAO.query(query, params, begin, max);
	}

	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Res.class, query, params,
				this.resDAO);
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

}
