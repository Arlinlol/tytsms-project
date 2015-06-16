package com.iskyshop.foundation.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.foundation.domain.Share;
import com.iskyshop.foundation.service.IShareService;

@Service
@Transactional
public class ShareServiceImpl implements IShareService {
	@Resource(name = "shareDAO")
	private IGenericDAO<Share> shareDAO;

	@Override
	public boolean save(Share share) {
		try {
			this.shareDAO.save(share);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Share getObjById(Long id) {
		Share share = this.shareDAO.get(id);
		if (share != null) {
			return share;
		}
		return null;
	}

	@Override
	public List<Share> query(String query, Map params, int begin, int max) {
		return this.shareDAO.query(query, params, begin, max);
	}

}
