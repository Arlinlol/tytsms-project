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
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.service.IAreaService;

@Service
@Transactional
public class AreaServiceImpl implements IAreaService{
	@Resource(name = "areaDAO")
	private IGenericDAO<Area> areaDao;
	
	public boolean save(Area area) {
		/**
		 * init other field here
		 */
		try {
			this.areaDao.save(area);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Area getObjById(Long id) {
		Area area = this.areaDao.get(id);
		if (area != null) {
			return area;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.areaDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> areaIds) {
		// TODO Auto-generated method stub
		for (Serializable id : areaIds) {
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
		GenericPageList pList = new GenericPageList(Area.class, query,
				params, this.areaDao);
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
	
	public boolean update(Area area) {
		try {
			this.areaDao.update( area);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Area> query(String query, Map params, int begin, int max){
		return this.areaDao.query(query, params, begin, max);
		
	}
	
	/***
	 *	查询运费地区
	 */
	public List<Area> queryAllAreas(){
		Map params = new HashMap();
		params.put("level", 1);
		List<Area> areas = this
				.query("select obj from Area obj where obj.level=:level order by obj.sequence asc",
						params, -1, -1);
		return areas;
	}
	
}
