package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IGoodsClassService;

/**
 * 
 * <p>
 * Title: GoodsClassViewTools.java
 * </p>
 * 
 * <p>
 * Description:
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
 * @date 2014-8-25
 * 
 * @version iskyshop_b2b2c 2014
 */
@Component
public class GoodsClassViewTools {
	@Autowired
	private IGoodsClassService gcService;

	/**
	 * 查询三级分类的推荐分类
	 * 
	 * @param count
	 * @return
	 */
	public List<GoodsClass> query_third_rec(String pid, int count) {
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Map params = new HashMap();
		params.put("pid", CommUtil.null2Long(pid));
		params.put("display", true);
		params.put("recommend", true);
		gcs = this.gcService
				.query("select obj from GoodsClass obj where obj.parent.parent.id=:pid and obj.display=:display and obj.recommend=:recommend order by obj.sequence asc",
						params, 0, count);
		return gcs;
	}
}
