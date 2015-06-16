package com.iskyshop.manage.buyer.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.FavoriteQueryObject;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
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
public class FavoriteBuyerAction {
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

	/**
	 * Favorite列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "用户商品收藏", value = "/buyer/favorite_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/favorite_goods.htm")
	public ModelAndView favorite_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/favorite_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.type", new SysMap("type", 0), "=");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.favoriteService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_goods.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户店铺收藏", value = "/buyer/favorite_store.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/favorite_store.htm")
	public ModelAndView favorite_store(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/favorite_store.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.type", new SysMap("type", 1), "=");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.favoriteService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store.htm",
				"", params, pList, mv);
		return mv;
	}

	
	@Transactional
	@SecurityMapping(title = "用户收藏删除", value = "/buyer/favorite_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/favorite_del.htm")
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
					elasticsearchUtil.indexUpdate(IndexName.GOODS, IndexType.GOODS,
							goods.getId().toString(), IndexVoTools.goodsToIndexVo(goods));
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

}