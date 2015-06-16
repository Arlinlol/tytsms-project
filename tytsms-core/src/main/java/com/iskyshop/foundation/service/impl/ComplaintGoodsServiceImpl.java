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
import com.iskyshop.foundation.domain.ComplaintGoods;
import com.iskyshop.foundation.service.IComplaintGoodsService;

@Service
@Transactional
public class ComplaintGoodsServiceImpl implements IComplaintGoodsService{
	@Resource(name = "complaintGoodsDAO")
	private IGenericDAO<ComplaintGoods> complaintGoodsDao;
	
	public boolean save(ComplaintGoods complaintGoods) {
		/**
		 * init other field here
		 */
		try {
			this.complaintGoodsDao.save(complaintGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ComplaintGoods getObjById(Long id) {
		ComplaintGoods complaintGoods = this.complaintGoodsDao.get(id);
		if (complaintGoods != null) {
			return complaintGoods;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.complaintGoodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> complaintGoodsIds) {
		// TODO Auto-generated method stub
		for (Serializable id : complaintGoodsIds) {
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
		GenericPageList pList = new GenericPageList(ComplaintGoods.class, query,
				params, this.complaintGoodsDao);
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
	
	public boolean update(ComplaintGoods complaintGoods) {
		try {
			this.complaintGoodsDao.update( complaintGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<ComplaintGoods> query(String query, Map params, int begin, int max){
		return this.complaintGoodsDao.query(query, params, begin, max);
		
	}
}
