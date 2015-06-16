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
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.service.IArticleService;

@Service
@Transactional
public class ArticleServiceImpl implements IArticleService {
	@Resource(name = "articleDAO")
	private IGenericDAO<Article> articleDao;

	public boolean save(Article article) {
		/**
		 * init other field here
		 */
		try {
			this.articleDao.save(article);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Article getObjById(Long id) {
		Article article = this.articleDao.get(id);
		if (article != null) {
			return article;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.articleDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> articleIds) {
		// TODO Auto-generated method stub
		for (Serializable id : articleIds) {
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
		GenericPageList pList = new GenericPageList(Article.class, query,
				params, this.articleDao);
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

	public boolean update(Article article) {
		try {
			this.articleDao.update(article);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Article> query(String query, Map params, int begin, int max) {
		return this.articleDao.query(query, params, begin, max);

	}

	@Override
	public Article getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		try {
			return this.articleDao.getBy(propertyName, value);
		} catch (Exception e) {
			Article obj = new Article();
			obj.setTitle("文章错误");
			return obj;
		}
	}
}
