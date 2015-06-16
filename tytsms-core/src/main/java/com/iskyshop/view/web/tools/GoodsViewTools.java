package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;

@Component
public class GoodsViewTools {
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IntegralViewTools IntegralViewTools;

	/**
	 * 将商品属性归类,便于前台显示
	 * 
	 * @param id
	 * @return
	 */
	public List<GoodsSpecification> generic_spec(String id) {
		List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
		if (id != null && !id.equals("")) {
			Goods goods = this.goodsService.getObjById(Long.parseLong(id));
			for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
				GoodsSpecification spec = gsp.getSpec();
				if (!specs.contains(spec)) {
					specs.add(spec);
				}
			}
		}
		java.util.Collections.sort(specs, new Comparator<GoodsSpecification>() {

			@Override
			public int compare(GoodsSpecification gs1, GoodsSpecification gs2) {
				// TODO Auto-generated method stub
				return gs1.getSequence() - gs2.getSequence();
			}
		});

		return specs;
	}

	/**
	 * 查询用户商品分类信息
	 * 
	 * @param pid
	 * @return
	 */
	public List<UserGoodsClass> query_user_class(String pid) {
		List<UserGoodsClass> list = new ArrayList<UserGoodsClass>();
		if (pid == null || pid.equals("")) {
			Map map = new HashMap();
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			list = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id = :uid order by obj.sequence asc",
							map, -1, -1);
		} else {
			Map params = new HashMap();
			params.put("pid", Long.parseLong(pid));
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			list = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.parent.id=:pid and obj.user.id = :uid order by obj.sequence asc",
							params, -1, -1);
		}
		return list;
	}

	/**
	 * 根据商城分类查询对应的商品
	 * 
	 * @param gc_id
	 *            商城分类id
	 * @param count
	 *            需要查询的数量
	 * @return
	 */
	public List<Goods> query_with_gc(String gc_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		if (gc != null) {
			Set<Long> ids = this.genericIds(gc);
			Map params = new HashMap();
			params.put("ids", ids);
			params.put("goods_status", 0);
			list = this.goodsService
					.query("select obj from Goods obj where obj.gc.id in (:ids) and obj.goods_status=:goods_status order by obj.goods_click desc",
							params, 0, count);
		}
		return list;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	public List<Goods> sort_sale_goods(String store_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", 0);
		list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, count);
		return list;
	}

	public List<Goods> sort_collect_goods(String store_id, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", 0);
		list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
						params, 0, count);
		return list;
	}

	public List<Goods> self_goods_sale(int goods_type, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("goods_type", goods_type);
		params.put("goods_status", 0);
		list = this.goodsService
				.query("select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, count);
		return list;
	}

	public List<Goods> self_goods_collect(int goods_type, int count) {
		List<Goods> list = new ArrayList<Goods>();
		Map params = new HashMap();
		params.put("goods_type", goods_type);
		params.put("goods_status", 0);
		list = this.goodsService
				.query("select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_collect desc",
						params, 0, count);
		return list;
	}

	/**
	 * 直通车商品查询，查询当天的直通车商品，
	 */
	public List<Goods> query_Ztc_Goods(int size) {
		List<Goods> ztc_goods = new ArrayList<Goods>();
		if (this.configService.getSysConfig().isZtc_status()) {
			ztc_goods = this.randomZtcGoods(CommUtil.null2Int(size));
		} else {
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			ztc_goods = this.goodsService
					.query("select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, CommUtil.null2Int(size));
		}
		return ztc_goods;
	}

	public List<Goods> randomZtcGoods(int count) {
		Map ztc_map = new HashMap();
		ztc_map.put("ztc_status", 3);
		ztc_map.put("now_date", new Date());
		ztc_map.put("ztc_gold", 0);
		List<Goods> goods = this.goodsService
				.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
						+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
						ztc_map, -1, -1);
		Random random = new Random();
		int random_num = 0;
		int num = 0;
		if (goods.size() - count > 0) {
			num = goods.size() - count;
			random_num = random.nextInt(num);
		}
		ztc_map.clear();
		ztc_map.put("ztc_status", 3);
		ztc_map.put("now_date", new Date());
		ztc_map.put("ztc_gold", 0);
		List<Goods> ztc_goods = this.goodsService
				.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
						+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
						ztc_map, random_num, count);
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	public List<Goods> randomZtcGoods2(List<Goods> goods_list, int count) {
		List<Goods> ztc_goods = new ArrayList<Goods>();
		Random ran = new Random();
		for (int i = 0; i < count; i++) {
			if (i < goods_list.size()) {
				int ind = ran.nextInt(goods_list.size());
				boolean flag = true;
				for (Goods obj : ztc_goods) {
					if (obj.getId().equals(goods_list.get(ind).getId())) {
						flag = false;
					}
				}
				if (flag) {
					ztc_goods.add(goods_list.get(ind));
				} else {
					i--;
				}
			}
		}
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	/**
	 * 根据当前会员的会员等级，显示相应等级的名称
	 */
	public String query_user_level_name(String user_id) {
		String level_name = "";
		if (user_id != null && !user_id.equals("")) {
			level_name = this.IntegralViewTools.query_user_level_name(user_id);
		}
		return level_name;
	}

	/**
	 * 查询LuceneVo的图片路径
	 */
	public List<String> query_LuceneVo_photos_url(String json) {
		List<String> list = new ArrayList();
		if (!CommUtil.null2String(json).equals("")) {
			list = Json.fromJson(ArrayList.class, json);
		}
		return list;
	}

	public Store query_LuceneVo_goods_store(String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Store store = goods.getGoods_store();
		return store;
	}
	
	public List<GoodsClass> query_GC_third(String gc_id,List<String> list_gc){
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		for (String gc_str : list_gc) {
			if(gc_str.split("_")[0].equals(gc_id)){
				gcs.add(this.goodsClassService.getObjById(CommUtil.null2Long(gc_str.split("_")[1])));
			}
		}
		return gcs;
	}
}
