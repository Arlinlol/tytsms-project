package com.iskyshop.foundation.service.impl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.service.IGoodsCartService;

@Service
@Transactional
public class GoodsCartServiceImpl implements IGoodsCartService{
	@Resource(name = "goodsCartDAO")
	private IGenericDAO<GoodsCart> goodsCartDao;
	
	public boolean save(GoodsCart goodsCart) {
		/**
		 * init other field here
		 */
		try {
			this.goodsCartDao.save(goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsCart getObjById(Long id) {
		GoodsCart goodsCart = this.goodsCartDao.get(id);
		if (goodsCart != null) {
			return goodsCart;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsCartDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsCartIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsCartIds) {
			delete((Long) id);
		}
		return true;
	}
	
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(GoodsCart.class, query,
				params, this.goodsCartDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update(GoodsCart goodsCart) {
		try {
			this.goodsCartDao.update( goodsCart);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsCart> query(String query, Map params, int begin, int max){
		return this.goodsCartDao.query(query, params, begin, max);
		
	}
	
	
	/**
	 * 合并购物车信息
	 * 
	 * @return
	 */
	public List<GoodsCart> cart_calc(List<GoodsCart> carts_list) {
		List<GoodsCart> carts_user_list = new ArrayList<GoodsCart>();// 用户整体购物车
		for (int i = 0 ; i < carts_list.size() ; i++){
			GoodsCart gc  =  carts_list.get(i);
			for (int j = 0 ; j < carts_list.size() ; j++){
				GoodsCart gc1  =  carts_list.get(j);
				if(gc1 != null){
					if(gc.getGoods() != null && gc1.getGoods() != null && gc.getGoods().getId().equals(gc1.getGoods().getId())&&!gc.getId().equals(gc1.getId())){
						 //手机端存在GoodsSpecProperty 表中 ，PC段规格存在Gsps 字段  判断产品规格合并
						String cart_gsp = CommUtil.null2String(gc.getCart_gsp());
						String cart_gsp1 = CommUtil.null2String(gc1.getCart_gsp());
						
						if("".equals(cart_gsp) && gc.getGsps() != null){
							for(GoodsSpecProperty goodsSpec : gc.getGsps()){
								cart_gsp +=  goodsSpec.getId() + ",";
							}
						}
						
						if("".equals(cart_gsp1) && gc1.getGsps() != null){
							for(GoodsSpecProperty goodsSpec : gc1.getGsps()){
								cart_gsp1 +=  goodsSpec.getId() + "," ;
							}
						}
						
						if( CommUtil.compareString(cart_gsp,cart_gsp1,",")){
							gc.setCount(gc.getCount() + gc1.getCount());
							this.delete(gc1.getId());
							carts_list.remove(gc1);
						}
					}
				}
			}
			carts_user_list.add(gc);
		}
		
		
		return carts_user_list;
		
	}
}
