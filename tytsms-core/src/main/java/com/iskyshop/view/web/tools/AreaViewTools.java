package com.iskyshop.view.web.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.service.IAreaService;

/**
 * 区域工具类
 * 
 * @author erikzhang
 * 
 */
@Component
public class AreaViewTools {
	@Autowired
	private IAreaService areaService;

	/**
	 * 根据区域生成区域信息字符串
	 * 
	 * @param area
	 * @return
	 */
	public String generic_area_info(String area_id) {
		String area_info = "";
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		if (area != null) {
			area_info = area.getAreaName() + " ";
			if (area.getParent() != null) {
				String info = generic_area_info(area.getParent().getId()
						.toString());
				area_info = info + area_info;
			}
		}
		return area_info;
	}
}
