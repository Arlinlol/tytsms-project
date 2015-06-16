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
import com.iskyshop.foundation.domain.ArticleClass;
import com.iskyshop.foundation.service.IArticleClassService;

@Service
@Transactional
public class ArticleClassServiceImpl implements IArticleClassService{
	@Resource(name = "articleClassDAO")
	private IGenericDAO<ArticleClass> articleClassDao;
	
	public boolean save(ArticleClass articleClass) {
		/**
		 * init other field here
		 */
		try {
			this.articleClassDao.save(articleClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ArticleClass getObjById(Long id) {
		ArticleClass articleClass = this.articleClassDao.get(id);
		if (articleClass != null) {
			return articleClass;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.articleClassDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> articleClassIds) {
		// TODO Auto-generated method stub
		for (Serializable id : articleClassIds) {
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
		GenericPageList pList = new GenericPageList(ArticleClass.class, query,
				params, this.articleClassDao);
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
	
	public boolean update(ArticleClass articleClass) {
		try {
			this.articleClassDao.update( articleClass);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<ArticleClass> query(String query, Map params, int begin, int max){
		return this.articleClassDao.query(query, params, begin, max);
		
	}

	@Override
	public ArticleClass getObjByPropertyName(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.articleClassDao.getBy(propertyName, value);
	}
}
