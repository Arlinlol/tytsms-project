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
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.service.IPayoffLogService;

@Service
@Transactional
public class PayoffLogServiceImpl implements IPayoffLogService{
	@Resource(name = "payoffLogDAO")
	private IGenericDAO<PayoffLog> payoffLogDao;
	
	public boolean save(PayoffLog payoffLog) {
		/**
		 * init other field here
		 */
		try {
			this.payoffLogDao.save(payoffLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public PayoffLog getObjById(Long id) {
		PayoffLog payoffLog = this.payoffLogDao.get(id);
		if (payoffLog != null) {
			return payoffLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.payoffLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> payoffLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : payoffLogIds) {
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
		GenericPageList pList = new GenericPageList(PayoffLog.class, query,
				params, this.payoffLogDao);
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
	
	public boolean update(PayoffLog payoffLog) {
		try {
			this.payoffLogDao.update( payoffLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<PayoffLog> query(String query, Map params, int begin, int max){
		return this.payoffLogDao.query(query, params, begin, max);
		
	}
}
