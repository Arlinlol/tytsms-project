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
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.IVerifyCodeService;

@Service
@Transactional
public class VerifyCodeServiceImpl implements IVerifyCodeService{
	@Resource(name = "mobileVerifyCodeDAO")
	private IGenericDAO<VerifyCode> mobileVerifyCodeDao;
	
	public boolean save(VerifyCode mobileVerifyCode) {
		/**
		 * init other field here
		 */
		try {
			this.mobileVerifyCodeDao.save(mobileVerifyCode);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public VerifyCode getObjById(Long id) {
		VerifyCode mobileVerifyCode = this.mobileVerifyCodeDao.get(id);
		if (mobileVerifyCode != null) {
			return mobileVerifyCode;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.mobileVerifyCodeDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> mobileVerifyCodeIds) {
		// TODO Auto-generated method stub
		for (Serializable id : mobileVerifyCodeIds) {
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
		GenericPageList pList = new GenericPageList(VerifyCode.class, query,
				params, this.mobileVerifyCodeDao);
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
	
	public boolean update(VerifyCode mobileVerifyCode) {
		try {
			this.mobileVerifyCodeDao.update( mobileVerifyCode);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<VerifyCode> query(String query, Map params, int begin, int max){
		return this.mobileVerifyCodeDao.query(query, params, begin, max);
		
	}

	@Override
	public VerifyCode getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.mobileVerifyCodeDao.getBy(propertyName, value);
	}
}
