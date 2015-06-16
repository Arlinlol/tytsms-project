package com.iskyshop.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.FootPoint;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FootPointQueryObject;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.manage.buyer.Tools.FootPointTools;

/**
 * 
 * <p>
 * Title: FootPointBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心，足迹管理控制器，显示、删除所有浏览过的足迹信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-10-16
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class FootPointBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private FootPointTools footPointTools;

	@SecurityMapping(title = "用户足迹记录", value = "/buyer/foot_point.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/foot_point.htm")
	public ModelAndView foot_point(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/foot_point.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		FootPointQueryObject qo = new FootPointQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
			qo.addQuery("obj.fp_user_id", new SysMap("fp_user_id", user.getId()),
					"=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		IPageList pList = this.footPointService.list(qo);
		if(pList.getResult()!=null&&pList.getResult().size()>0){
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}else{
			mv.addObject("objs", "0");
		}
		mv.addObject("footPointTools", this.footPointTools);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		/*List<Goods> your_like_goods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (goods == null)
						break;
					your_like_GoodsClass = goods.getGc().getId();
					your_like_goods = this.goodsService.query(
							"select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
									+ your_like_GoodsClass
									+ " and obj.id is not " + goods.getId()
									+ " order by obj.goods_salenum desc", null,
							0, 20);
					int gcs_size = your_like_goods.size();
					if (gcs_size < 20) {
						List<Goods> like_goods = this.goodsService.query(
								"select obj from Goods obj where obj.goods_status=0 and obj.id is not "
										+ goods.getId()
										+ " order by obj.goods_salenum desc",
								null, 0, 20 - gcs_size);
						for (int i = 0; i < like_goods.size(); i++) {
							// 去除重复商品
							int k = 0;
							for (int j = 0; j < your_like_goods.size(); j++) {
								if (like_goods.get(i).getId()
										.equals(your_like_goods.get(j).getId())) {
									k++;
								}
							}
							if (k == 0) {
								your_like_goods.add(like_goods.get(i));
							}
						}
					}
					break;
				} else {
					your_like_goods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 20);
				}
			}
		} else {
			your_like_goods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
							null, 0, 20);
		}
		mv.addObject("your_like_goods", your_like_goods);*/
		return mv;
	}
	@Transactional
	@SecurityMapping(title = "用户足迹记录删除", value = "/buyer/foot_point_remove.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/foot_point_remove.htm")
	public void foot_point_remove(HttpServletRequest request,
			HttpServletResponse response, String date, String goods_id) {
		boolean ret = true;
		if (!CommUtil.null2String(date).equals("")
				&& CommUtil.null2String(goods_id).equals("")) {// 删除当日所有足迹
			Map params = new HashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			List<FootPoint> fps = this.footPointService
					.query("select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id",
							params, -1, -1);
			for (FootPoint fp : fps) {
				this.footPointService.delete(fp.getId());
			}
		}

		if (!CommUtil.null2String(date).equals("")
				&& !CommUtil.null2String(goods_id).equals("")) {// 删除某一个足迹
			Map params = new HashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			List<FootPoint> fps = this.footPointService
					.query("select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id",
							params, -1, -1);
			for (FootPoint fp : fps) {
				List<Map> list = Json.fromJson(List.class,
						fp.getFp_goods_content());
				for (Map map : list) {
					if (CommUtil.null2String(map.get("goods_id")).equals(
							goods_id)) {
						list.remove(map);
						break;
					}
				}
				fp.setFp_goods_content(Json.toJson(list, JsonFormat.compact()));
				fp.setFp_goods_count(list.size());
				this.footPointService.update(fp);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
