package com.iskyshop.view.web.action;

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
 *@info 系统ajax数据加载控制器
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class LoadAction {
	@Autowired
	private IAreaService areaService;

	/**
	 * 根据父id加载下级区域，返回json格式数据，这里只返回id和areaName，根据需要可以修改返回数据
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping("/load_area.htm")
	public void load_area(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		Map params = new HashMap();
		params.put("pid", CommUtil.null2Long(pid));
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id=:pid", params,
				-1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Area area : areas) {
			Map map = new HashMap();
			map.put("id", area.getId());
			map.put("areaName", area.getAreaName());
			list.add(map);
		}
		String temp = Json.toJson(list, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
