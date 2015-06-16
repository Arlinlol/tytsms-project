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
import com.iskyshop.foundation.domain.InviteRegister;
import com.iskyshop.foundation.service.IInviteRegisterService;
import com.taiyitao.user.UserIp;
@Service("inviteRegisterService")
@Transactional
public class InviteRegisterServiceImpl implements IInviteRegisterService {
	
	@Resource(name = "inviteRegisterDAO")
	private IGenericDAO<InviteRegister> inviteRegisterDAO;

	@Override
	public boolean save(InviteRegister inviteRegister) {
				try {
					this.inviteRegisterDAO.save(inviteRegister);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
	}

	@Override
	public InviteRegister getObjById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean batchDelete(List<Serializable> ids) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(UserIp.class, query,
				params, this.inviteRegisterDAO);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}

	@Override
	public boolean update(InviteRegister inviteRegister) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<InviteRegister> query(String query, Map params, int begin,
			int max) {
		return this.inviteRegisterDAO.query(query, params, begin, max);
	}

}
