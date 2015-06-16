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
import com.iskyshop.foundation.domain.ReportSubject;
import com.iskyshop.foundation.service.IReportSubjectService;

@Service
@Transactional
public class ReportSubjectServiceImpl implements IReportSubjectService{
	@Resource(name = "reportSubjectDAO")
	private IGenericDAO<ReportSubject> reportSubjectDao;
	
	public boolean save(ReportSubject reportSubject) {
		/**
		 * init other field here
		 */
		try {
			this.reportSubjectDao.save(reportSubject);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ReportSubject getObjById(Long id) {
		ReportSubject reportSubject = this.reportSubjectDao.get(id);
		if (reportSubject != null) {
			return reportSubject;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.reportSubjectDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> reportSubjectIds) {
		// TODO Auto-generated method stub
		for (Serializable id : reportSubjectIds) {
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
		GenericPageList pList = new GenericPageList(ReportSubject.class, query,
				params, this.reportSubjectDao);
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
	
	public boolean update(ReportSubject reportSubject) {
		try {
			this.reportSubjectDao.update( reportSubject);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<ReportSubject> query(String query, Map params, int begin, int max){
		return this.reportSubjectDao.query(query, params, begin, max);
		
	}
}
