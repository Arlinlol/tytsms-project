package com.iskyshop.core.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * <p>
 * Title: IQueryService.java
 * </p>
 * 
 * <p>
 * Description: 基础查询service接口
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
public interface IQueryService {
	List query(String scope, Map params, int page, int pageSize);

}
