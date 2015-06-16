package com.iskyshop.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.IOrderFormService;

@Service
@Transactional
public class OrderFormServiceImpl implements IOrderFormService {
	@Resource(name = "orderFormDAO")
	private IGenericDAO<OrderForm> orderFormDao;
	@Resource(name = "goodsDAO")
	private IGenericDAO<Goods> goodsDao;
	
	

	public boolean save(OrderForm orderForm) {
		/**
		 * init other field here
		 */
		try {
			this.orderFormDao.save(orderForm);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public OrderForm getObjById(Long id) {
		OrderForm orderForm = this.orderFormDao.get(id);
		if (orderForm != null) {
			return orderForm;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.orderFormDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> orderFormIds) {
		// TODO Auto-generated method stub
		for (Serializable id : orderFormIds) {
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
		GenericPageList pList = new GenericPageList(OrderForm.class, query,
				params, this.orderFormDao);
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

	public boolean update(OrderForm orderForm) {
		try {
			this.orderFormDao.update(orderForm);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<OrderForm> query(String query, Map params, int begin, int max) {
		return this.orderFormDao.query(query, params, begin, max);

	}

	@Override
	public List  queryFromOrderForm(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.orderFormDao.query(query, params, begin, max);
	}
    
	/**
	 * 主图修改时候修改订单中JSON的图片路径
	 * @param orderForms
	 */
	@Override
	public void changPhotoByJson(List<OrderForm> orderForms) {
		if(orderForms != null && !orderForms.isEmpty()){
			for(OrderForm orderForm : orderForms){
				JSONArray jsonArray = JSONArray.fromObject(orderForm.getGoods_info());
				for(int i = 0 ; i < jsonArray.size(); i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String img = String.valueOf(jsonObject.get("goods_mainphoto_path"));//订单中保存的图片路径
					Goods goods = goodsDao.get(Long.valueOf(jsonObject.get("goods_id").toString()));//获得订单的商品
					//拼写新的图片路径
					if(goods != null && !"".equals(goods)){
						Accessory accessory = goods.getGoods_main_photo();
						if(accessory != null && !"".equals(accessory)){
							String photo = goods.getGoods_main_photo().getName()+"_small."+goods.getGoods_main_photo().getExt();
							String newPhoto = img.substring(0,img.lastIndexOf("/")+1)+photo;
							//设置新的图片
							jsonObject.put("goods_mainphoto_path", newPhoto);
						}
					}
				}
				orderForm.setGoods_info(jsonArray.toString());
			}
		}		
	}
}
