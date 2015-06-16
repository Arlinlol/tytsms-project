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
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.service.IMessageService;

@Service
@Transactional
public class MessageServiceImpl implements IMessageService{
	@Resource(name = "messageDAO")
	private IGenericDAO<Message> messageDao;
	
	public boolean save(Message message) {
		/**
		 * init other field here
		 */
		try {
			this.messageDao.save(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Message getObjById(Long id) {
		Message message = this.messageDao.get(id);
		if (message != null) {
			return message;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.messageDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> messageIds) {
		// TODO Auto-generated method stub
		for (Serializable id : messageIds) {
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
		GenericPageList pList = new GenericPageList(Message.class, query,
				params, this.messageDao);
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
	
	public boolean update(Message message) {
		try {
			this.messageDao.update( message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Message> query(String query, Map params, int begin, int max){
		return this.messageDao.query(query, params, begin, max);
		
	}
}
