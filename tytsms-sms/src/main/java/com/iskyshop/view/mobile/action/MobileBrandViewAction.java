package com.iskyshop.view.mobile.action;

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
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: MobileClassViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端商城前台商品品牌列表
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
 * @date 2014-7-25
 * 
 * @version 1.0
 */
@Controller
public class MobileBrandViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsService goodsService;

	/**
	 * 手机客户端商城首页分类请求
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/mobile/brand_list.htm")
	public void brand_list(HttpServletRequest request,
			HttpServletResponse response) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map map_list = new HashMap();
		if (verify) {
			Map params = new HashMap();
			params.put("audit", 1);
			List<GoodsBrand> brands = this.goodsBrandService
					.query("select obj from GoodsBrand obj where obj.audit=:audit order by obj.sequence asc",
							params, -1, -1);
			List all_list = new ArrayList();
			String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
			String words[] = list_word.split(",");
			for (String word : words) {
				Map brand_map = new HashMap();
				List brand_list = new ArrayList();
				for (GoodsBrand gb : brands) {
					if (!CommUtil.null2String(gb.getFirst_word()).equals("")
							&& word.equals(gb.getFirst_word().toUpperCase())) {
						Map map = new HashMap();
						map.put("name", gb.getName());
						map.put("id", gb.getId());
						map.put("photo", CommUtil.getURL(request) + "/"
								+ gb.getBrandLogo().getPath() + "/"
								+ gb.getBrandLogo().getName());
						brand_list.add(map);
					}
				}
				if (brand_list.size() > 0) {
					brand_map.put("brand_list", brand_list);
					brand_map.put("word", word);
					all_list.add(brand_map);
				}
			}
			map_list.put("brand_list", all_list);
		}
		map_list.put("code", CommUtil.null2String(verify));
		String json = Json.toJson(map_list, JsonFormat.compact());
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
