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
import com.iskyshop.foundation.domain.Report;
import com.iskyshop.foundation.service.IReportService;

@Service
@Transactional
public class ReportServiceImpl implements IReportService{
	@Resource(name = "reportDAO")
	private IGenericDAO<Report> reportDao;
	
	public boolean save(Report report) {
		/**
		 * init other field here
		 */
		try {
			this.reportDao.save(report);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Report getObjById(Long id) {
		Report report = this.reportDao.get(id);
		if (report != null) {
			return report;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.reportDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> reportIds) {
		// TODO Auto-generated method stub
		for (Serializable id : reportIds) {
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
		GenericPageList pList = new GenericPageList(Report.class, query,
				params, this.reportDao);
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
	
	public boolean update(Report report) {
		try {
			this.reportDao.update( report);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Report> query(String query, Map params, int begin, int max){
		return this.reportDao.query(query, params, begin, max);
		
	}
}
