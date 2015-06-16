package com.iskyshop.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.base.GenericEntityDao;
import com.iskyshop.core.service.IQueryService;

/**
 * 
 * <p>
 * Title: QueryService.java
 * </p>
 * 
 * <p>
 * Description: 基础sevice接口的实现类，系统中暂时未使用该类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Service
@Transactional
public class QueryService implements IQueryService {
	@Autowired
	@Qualifier("genericEntityDao")
	private GenericEntityDao geDao;

	public GenericEntityDao getGeDao() {
		return geDao;
	}

	public void setGeDao(GenericEntityDao geDao) {
		this.geDao = geDao;
	}

	public List query(String scope, Map params, int page, int pageSize) {
		// TODO Auto-generated method stub
		return this.geDao.query(scope, params, page, pageSize);
	}

}
