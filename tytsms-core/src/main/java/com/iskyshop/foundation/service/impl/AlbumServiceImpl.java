package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
import java.util.HashMap;
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
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAlbumService;

@Service
@Transactional
public class AlbumServiceImpl implements IAlbumService {
	@Resource(name = "albumDAO")
	private IGenericDAO<Album> albumDao;
	@Resource(name = "userDAO")
	private IGenericDAO<User> userDAO;

	public boolean save(Album album) {
		/**
		 * init other field here
		 */
		try {
			this.albumDao.save(album);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Album getObjById(Long id) {
		Album album = this.albumDao.get(id);
		if (album != null) {
			return album;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.albumDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> albumIds) {
		// TODO Auto-generated method stub
		for (Serializable id : albumIds) {
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
		GenericPageList pList = new GenericPageList(Album.class, query, params,
				this.albumDao);
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

	public boolean update(Album album) {
		try {
			this.albumDao.update(album);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Album> query(String query, Map params, int begin, int max) {
		return this.albumDao.query(query, params, begin, max);

	}

	@Override
	public Album getDefaultAlbum(Long id) {
		// TODO Auto-generated method stub
		User user = this.userDAO.get(id);
		if (user.getParent() == null) {
			Map params = new HashMap();
			params.put("user_id", id);
			params.put("album_default", true);
			List<Album> list = this.albumDao
					.query(
							"select obj from Album obj where obj.user.id=:user_id and obj.album_default=:album_default",
							params, -1, -1);
			if (list.size() > 0)
				return list.get(0);
			else
				return null;
		} else {
			Map params = new HashMap();
			params.put("user_id", user.getParent().getId());
			params.put("album_default", true);
			List<Album> list = this.albumDao
					.query(
							"select obj from Album obj where obj.user.id=:user_id and obj.album_default=:album_default",
							params, -1, -1);
			if (list.size() > 0)
				return list.get(0);
			else
				return null;
		}
	}
}
