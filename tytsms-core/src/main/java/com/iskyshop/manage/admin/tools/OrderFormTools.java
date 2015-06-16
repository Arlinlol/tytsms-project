package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.virtual.TransContent;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.taiyitao.logistics.httpclient.HttpclientLogisticsUtil;

/**
 * 
 * <p>
 * Title: MsgTools.java
 * </p>
 * 
 * <p>
 * Description: 订单解析工具，解析订单中json数据
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
 * @date 2014-5-4
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class OrderFormTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService gspService;

	/**
	 * 解析订单商品信息json数据
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> queryGoodsInfo(String json) {
		List<Map> map_list = new ArrayList<Map>();
		if (json != null && !json.equals("")) {
			map_list = Json.fromJson(ArrayList.class, json);
		}
		return map_list;
	}

	/**
	 * 根据订单id查询该订单中所有商品
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Goods> queryOfGoods(String order_id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		List<Goods> goods_list = new ArrayList<Goods>();
		for (Map map : map_list) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map
					.get("goods_id")));
			goods_list.add(goods);
		}
		if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {// 查询子订单中的商品信息
			List<Map> maps = this.queryGoodsInfo(of.getChild_order_detail());
			for (Map map : maps) {
				OrderForm child_order = this.orderFormService
						.getObjById(CommUtil.null2Long(map.get("order_id")));
				map_list.clear();
				map_list = this.queryGoodsInfo(child_order.getGoods_info());
				for (Map map1 : map_list) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(map1.get("goods_id")));
					goods_list.add(goods);
				}
			}
		}
		return goods_list;
	}

	/**
	 * 根据订单id查询该订单中所有商品的价格总和
	 * 
	 * @param order_id
	 * @return
	 */
	public double queryOfGoodsPrice(String order_id) {
		double price = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			price = price + CommUtil.null2Double(map.get("goods_all_price"));
		}
		return price;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int queryOfGoodsCount(String order_id, String goods_id) {
		int count = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				count = CommUtil.null2Int(map.get("goods_count"));
				break;
			}
		}
		if (count == 0) {// 主订单无数量信息，继续从子订单中查询
			if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {
				List<Map> maps = this
						.queryGoodsInfo(of.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map1 : map_list) {
						if (CommUtil.null2String(map1.get("goods_id")).equals(
								goods_id)) {
							count = CommUtil.null2Int(map1.get("goods_count"));
							break;
						}
					}
				}
			}
		}
		return count;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的规格
	 * 
	 * @param order_id
	 * @return
	 */
	public List<GoodsSpecProperty> queryOfGoodsGsps(String order_id,
			String goods_id) {
		List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>();
		String goods_gsp_ids = "";
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		boolean add = false;
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				goods_gsp_ids = CommUtil.null2String(map.get("goods_gsp_ids"));
				break;
			}
		}
		String gsp_ids[] = goods_gsp_ids.split("/");
		for (String id : gsp_ids) {
			if (!id.equals("")) {
				GoodsSpecProperty gsp = this.gspService.getObjById(CommUtil
						.null2Long(id));
				list.add(gsp);
				add = true;
			}
		}
		if (!add) {// 如果主订单中添加失败，则从子订单中添加
			if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {
				List<Map> maps = this
						.queryGoodsInfo(of.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map : map_list) {
						if (CommUtil.null2String(map.get("goods_id")).equals(
								goods_id)) {
							goods_gsp_ids = CommUtil.null2String(map
									.get("goods_gsp_ids"));
							break;
						}
					}
					String child_gsp_ids[] = goods_gsp_ids.split("/");
					for (String id : child_gsp_ids) {
						if (!id.equals("")) {
							GoodsSpecProperty gsp = this.gspService
									.getObjById(CommUtil.null2Long(id));
							list.add(gsp);
							add = true;
						}
					}
				}
			}

		}
		return list;
	}

	/**
	 * 解析订单物流信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public String queryExInfo(String json, String key) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		// System.out.println(CommUtil.null2String(map.get(key)));
		return CommUtil.null2String(map.get(key));
	}

	
	
	/**
	 * 解析订单物流信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public List getbuyer_order_ship(OrderForm order) {
		List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
		HttpclientLogisticsUtil logistics = new HttpclientLogisticsUtil();
		List json_list = new ArrayList();
		Map jsonMap = new HashMap();
		jsonMap.put("tytordertyte", order.getShipper_type()); //订单类型
		jsonMap.put("pickexpressno", order.getShipCode()); //物流单号
		jsonMap.put("pickexpresscom", queryExInfo(order.getExpress_info(),"express_company_mark")); //快递代码
		JSONObject jsonObject = new JSONObject().fromObject(jsonMap);
		TransInfo transInfo = logistics.searchLogisticsInfo(jsonObject.toString());
		transInfo.setExpress_company_name(queryExInfo(
				order.getExpress_info(), "express_company_name"));
		transInfo.setExpress_ship_code(order.getShipCode());
		transInfo_list.add(transInfo);
		for (TransInfo transinfo : transInfo_list) {
			Map map = new HashMap();
			map.put("message", transinfo.getMessage());
			map.put("status", transinfo.getStatus());
			List list = new ArrayList();
			for (TransContent con : transinfo.getData()) {
				Map map_con = new HashMap();
				map_con.put("content", con.getContext());
				map_con.put("time", con.getTime());
				list.add(map_con);
			}
			map.put("content", list);
			json_list.add(map);
		}
		return json_list;
	}

	
	/**
	 * 解析订单优惠券信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryCouponInfo(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 解析生活类团购订单json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryGroupInfo(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 根据订单id查询订单信息
	 * 
	 * @param id
	 * @return
	 */
	public OrderForm query_order(String id) {
		return this.orderFormService.getObjById(CommUtil.null2Long(id));
	}

	/**
	 * 查询订单的状态，用在买家中心的订单列表中，多商家复合订单中只有全部商家都已经发货，卖家中心才会出现确认收货按钮
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_order_status(String order_id,String num) {
		int order_status = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			order_status = order.getOrder_status();
			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					if (child_order.getOrder_status() < Integer.valueOf(num)) {
						order_status = child_order.getOrder_status();
					}
				}
			}
		}
		return order_status;
	}

	/**
	 * 查询订单总价格（如果包含子订单，将子订单价格与主订单价格相加）
	 * 
	 * @param order_id
	 * @return
	 */
	public double query_order_price(String order_id) {
		double all_price = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getTotalPrice());
			if (order.getChild_order_detail() != null
					&& !order.getChild_order_detail().equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_price = all_price
							+ CommUtil.null2Double(child_order.getTotalPrice());
				}

			}
		}
		return all_price;
	}
	
	
	/**
	 * 查询订单总价格（如果包含子订单，将子订单价格与主订单价格相加）
	 * 
	 * @param order_id
	 * @return
	 */
	public double query_ship_price(String order_id) {
		double all_price = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getShip_price());
			if (order.getChild_order_detail() != null
					&& !order.getChild_order_detail().equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_price = all_price
							+ CommUtil.null2Double(child_order.getShip_price());
				}

			}
		}
		return all_price;
	}
	
	
	public Object getGoodsReturnStatus(String id,String goods_id){
		Object goods_return_status = "";
		OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(id));
		if(order!=null){
			List<Map> goods_info = this.queryGoodsInfo(order.getGoods_info());
			for (Map info : goods_info) {
				if (info.get("goods_id").toString().equals(goods_id)) {
					if(info.containsKey("goods_return_status")){
						goods_return_status =info.get("goods_return_status");
					}
				}
			}
		}
		return goods_return_status;
	}
	
}
