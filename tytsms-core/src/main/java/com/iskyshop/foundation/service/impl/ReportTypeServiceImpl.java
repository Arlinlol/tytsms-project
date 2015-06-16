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
import com.iskyshop.foundation.domain.ReportType;
import com.iskyshop.foundation.service.IReportTypeService;

@Service
@Transactional
public class ReportTypeServiceImpl implements IReportTypeService{
	@Resource(name = "reportTypeDAO")
	private IGenericDAO<ReportType> reportTypeDao;
	
	public boolean save(ReportType reportType) {
		/**
		 * init other field here
		 */
		try {
			this.reportTypeDao.save(reportType);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ReportType getObjById(Long id) {
		ReportType reportType = this.reportTypeDao.get(id);
		if (reportType != null) {
			return reportType;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.reportTypeDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> reportTypeIds) {
		// TODO Auto-generated method stub
		for (Serializable id : reportTypeIds) {
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
		GenericPageList pList = new GenericPageList(ReportType.class, query,
				params, this.reportTypeDao);
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
	
	public boolean update(ReportType reportType) {
		try {
			this.reportTypeDao.update( reportType);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<ReportType> query(String query, Map params, int begin, int max){
		return this.reportTypeDao.query(query, params, begin, max);
		
	}
}
