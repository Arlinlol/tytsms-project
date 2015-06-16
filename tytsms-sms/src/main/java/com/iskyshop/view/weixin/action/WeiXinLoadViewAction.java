package com.iskyshop.view.weixin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.service.IAreaService;

/**
 * 
 * <p>
 * Title: MobileLoadViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端地区加载控制器
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
 * @author hezeng
 * 
 * @date 2014-8-5
 * 
 * @version 1.0
 */
@Controller
public class WeiXinLoadViewAction  extends WeiXinBaseAction{
	@Autowired
	private IAreaService areaService;

	/**
	 * 手机客户端用户登录
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/weiXin/area_load.htm")
	public void area_load(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map json_map = new HashMap();
		List area_list = new ArrayList();
		List<Area> areas = null;
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		if (verify) {// 头文件验证成功
			if (id != null && !id.equals("")) {
				Area parent = this.areaService.getObjById(CommUtil
						.null2Long(id));
				areas = parent.getChilds();
			} else {
				areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id is null",
						null, -1, -1);
			}
			for (Area area : areas) {
				Map map = new HashMap();
				map.put("id", area.getId());
				map.put("name", area.getAreaName());
				area_list.add(map);
			}
			json_map.put("area_list", area_list);
		}
		json_map.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
