package com.iskyshop.view.weixin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.IndexVoTools;

/**
 * 
 * <p>
 * Title: FavoriteBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 收藏管理控制器，用来显示买家收藏的商品信息、店铺信息
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
 * @date 2014-8-8
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class WeiXinBuyerFavoriteAction extends WeiXinBaseAction{
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;
	
	@Autowired
	private IUserService userService;

	/**
	 * Favorite列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@RequestMapping("/weiXin/buyer_favorite_goods.htm")
	public ModelAndView favorite_goods(HttpServletRequest request,
			HttpServletResponse response , String beginCount) {
		ModelAndView mv = new JModelAndView(
				"weiXin/view/buyer/favorite_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		verify = true ;
		Map json_map = new HashMap();
		List favorite_goods_list = new ArrayList();
		
		String user_id = "";
		Map userMap = checkLogin(request, user_id);
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	User user = null ;
		if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
		
		if (verify && user_id != null && !user_id.equals("")) {
			user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(user_id));
					params.put("type", 0);
					List<Favorite> favs = this.favoriteService
							.query("select obj from Favorite obj where obj.user.id=:user_id and obj.type=:type",
									params, 0 , 0 );
					String url = CommUtil.getURL(request);
					for (Favorite fav : favs) {
						Map map = new HashMap();
						map.put("goods_id", fav.getGoods().getId());
						String goods_main_photo = url
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName();
						if (fav.getGoods().getGoods_main_photo() != null) {// 商品主图片
							goods_main_photo = url
									+ "/"
									+ fav.getGoods().getGoods_main_photo()
											.getPath()
									+ "/"
									+ fav.getGoods().getGoods_main_photo()
											.getName()
									+ "_small."
									+ fav.getGoods().getGoods_main_photo()
											.getExt();
						}
						map.put("goods_photo", goods_main_photo);
						map.put("id", fav.getGoods().getId());
						map.put("name", fav.getGoods().getGoods_name());
						map.put("price", fav.getGoods()
								.getGoods_current_price());
						map.put("addTime", fav.getAddTime());
						favorite_goods_list.add(map);
					}
					json_map.put("favorite_goods_list", favorite_goods_list);
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
		
		mv.addObject("json_map", json_map);
		System.out.println("json_map :" + json_map);
		return mv;
	}

	@RequestMapping("/weiXin/buyer_favorite_del.htm")
	public String favorite_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			int type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Favorite favorite = this.favoriteService.getObjById(Long
						.parseLong(id));
				Goods goods = favorite.getGoods();
				if (goods != null) {
					goods.setGoods_collect(goods.getGoods_collect() - 1);
					this.goodsService.update(goods);
					// 更新lucene索引
//					String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//							+ File.separator + "luence" + File.separator
//							+ "goods";
//					File file = new File(goods_lucene_path);
//					if (!file.exists()) {
//						CommUtil.createFolder(goods_lucene_path);
//					}
//					LuceneUtil lucene = LuceneUtil.instance();
//					lucene.setIndex_path(goods_lucene_path);
//					lucene.update(
//							CommUtil.null2String(favorite.getGoods().getId()),
//							luceneVoTools.updateGoodsIndex(favorite.getGoods()));
					
					elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS,
							CommUtil.null2String(favorite.getGoods().getId()), IndexVoTools.goodsToIndexVo(favorite.getGoods()));
					
				}
				this.favoriteService.delete(Long.parseLong(id));
			}
		}
		if (type == 0) {
			return "redirect:favorite_goods.htm?currentPage=" + currentPage;
		} else {
			return "redirect:favorite_store.htm?currentPage=" + currentPage;
		}
	}

	

	@RequestMapping("/weiXin/add_goods_favorite.htm")
	public void add_goods_favorite(HttpServletRequest request,HttpServletResponse response, String id) {
		Map params = new HashMap();
		
		
		String user_id = "" ;
		Map userMap = checkLogin(request, user_id);
		User user = null ;
	   	if(userMap.get("user_id") != null){
	   		user_id = userMap.get("user_id").toString();
	   	}
	   	if(userMap.get("user") != null){
	   		user =  (User) userMap.get("user");
	   	}
		
	   	if (user == null) {
		   	user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
	   	}
		
	   	
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("goods_id", CommUtil.null2Long(id));
		List<Favorite> list = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.user.id=:user_id and obj.goods.id=:goods_id",
						params, -1, -1);
		int ret = 0;
		if (list.size() == 0) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			Favorite obj = new Favorite();
			obj.setAddTime(new Date());
			obj.setType(0);
			obj.setUser(user);
			obj.setGoods(goods);
			this.favoriteService.save(obj);
			goods.setGoods_collect(goods.getGoods_collect() + 1);
			this.goodsService.update(goods);
			// 更新lucene索引
//			String goods_lucene_path = ConfigContants.LUCENE_DIRECTORY
//					+ File.separator
//					+ "luence" + File.separator + "goods";
//			File file = new File(goods_lucene_path);
//			if (!file.exists()) {
//				CommUtil.createFolder(goods_lucene_path);
//			}
//			LuceneUtil lucene = LuceneUtil.instance();
//			lucene.setIndex_path(goods_lucene_path);
//			lucene.update(CommUtil.null2String(goods.getId()),
//					luceneVoTools.updateGoodsIndex(goods));
			
			elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS,
					CommUtil.null2String(goods.getId()), IndexVoTools.goodsToIndexVo(goods));
		} else {
			ret = 1;
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