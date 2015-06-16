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
import com.iskyshop.foundation.domain.Partner;
import com.iskyshop.foundation.service.IPartnerService;

@Service
@Transactional
public class PartnerServiceImpl implements IPartnerService{
	@Resource(name = "partnerDAO")
	private IGenericDAO<Partner> partnerDao;
	
	public boolean save(Partner partner) {
		/**
		 * init other field here
		 */
		try {
			this.partnerDao.save(partner);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Partner getObjById(Long id) {
		Partner partner = this.partnerDao.get(id);
		if (partner != null) {
			return partner;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.partnerDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> partnerIds) {
		// TODO Auto-generated method stub
		for (Serializable id : partnerIds) {
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
		GenericPageList pList = new GenericPageList(Partner.class, query,
				params, this.partnerDao);
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
	
	public boolean update(Partner partner) {
		try {
			this.partnerDao.update( partner);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Partner> query(String query,Map params, int begin, int max){
		return this.partnerDao.query(query, params, begin, max);
		
	}
}
