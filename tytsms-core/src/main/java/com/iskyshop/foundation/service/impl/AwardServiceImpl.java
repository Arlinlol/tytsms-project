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
import com.iskyshop.foundation.domain.Award;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.IAwardService;

@Service
@Transactional
public class AwardServiceImpl implements IAwardService {
	@Resource(name = "awardDAO")
	private IGenericDAO<Award> awardDAO;
	
	@Override
	public boolean save(Award award) {
		try {
			this.awardDAO.save(award);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Award getObjById(Long id) {
		Award award = this.awardDAO.get(id);
		if (award != null) {
			return award;
		}
		return null;
	}

	@Override
	public List<Award> query(String query, Map params, int begin, int max) {
		return this.awardDAO.query(query, params, begin, max);
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Award.class, query,
				params, this.awardDAO);
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
