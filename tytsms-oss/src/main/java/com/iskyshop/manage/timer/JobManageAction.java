package com.iskyshop.manage.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.taiyitao.elasticsearch.ElasticsearchUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;

/**
 * 
 * <p>
 * Title: JobManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统定时任务控制器，每天00:00:01秒执行
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
 * @date 2014-5-13
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component(value = "shop_job")
public class JobManageAction {
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IZTCGoldLogService ztcGoldLogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

	public void execute() {
		// TODO Auto-generated method stub
		// 处理直通车信息
		Map params = new HashMap();
		params.put("ztc_status", 2);
		List<Goods> goods_audit_list = this.goodsService.query(
				"select obj from Goods obj where obj.ztc_status=:ztc_status",
				params, -1, -1);// 审核通过但未开通的直通车商品
		for (Goods goods : goods_audit_list) {
			if (goods.getZtc_begin_time().before(new Date())) {
				goods.setZtc_dredge_price(goods.getZtc_price());
				goods.setZtc_status(3);
				this.goodsService.update(goods);
			}
		}
		params.clear();
		params.put("ztc_status", 3);
		goods_audit_list = this.goodsService.query(
				"select obj from Goods obj where obj.ztc_status=:ztc_status",
				params, -1, -1);
		for (Goods goods : goods_audit_list) {// 已经开通的商品扣除当日金币，金币不足时关闭直通车
			if (goods.getZtc_gold() >= goods.getZtc_price()) {
				goods.setZtc_gold(goods.getZtc_gold() - goods.getZtc_price());
				goods.setZtc_dredge_price(goods.getZtc_price());
				this.goodsService.update(goods);
				ZTCGoldLog log = new ZTCGoldLog();
				log.setAddTime(new Date());
				log.setZgl_content("直通车消耗金币");
				log.setZgl_gold(goods.getZtc_price());
				log.setZgl_goods(goods);
				log.setZgl_type(1);
				this.ztcGoldLogService.save(log);
			} else {
				goods.setZtc_status(0);
				goods.setZtc_dredge_price(0);
				goods.setZtc_pay_status(0);
				goods.setZtc_apply_time(null);
				this.goodsService.update(goods);
			}
		}
		// 处理店铺到期
		List<Store> stores = this.storeService.query(
				"select obj from Store obj where obj.validity is not null",
				null, -1, -1);
		for (Store store : stores) {
			if (store.getValidity().before(new Date())) {// 处理已经过期的店铺
				store.setStore_status(25);// 设定店铺状态为25，到期自动关闭
				this.storeService.update(store);
				Template template = this.templateService.getObjByProperty(
						"mark", "msg_toseller_store_auto_closed_notify");
				if (template != null && template.isOpen()) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(template.getContent());
					msg.setFromUser(this.userService.getObjByProperty(
							"userName", "admin"));
					msg.setStatus(0);
					msg.setTitle(template.getTitle());
					msg.setToUser(store.getUser());
					msg.setType(0);
					this.messageService.save(msg);
				}
				// 同时处理商品过期
				params.clear();
				params.put("store_id", store.getId());
				List<Goods> goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id",
								params, -1, -1);
				for (Goods goods : goods_list) {
					goods.setGoods_status(3);// 设置商品状态为过期
					this.goodsService.update(goods);
				}
			}
		}
		// 处理超过1天未提交订单的购物车信息
		params.clear();
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		params.put("addTime", cal.getTime());
		params.put("sc_status", 0);
		List<GoodsCart> cart_list = this.goodsCartService
				.query("select obj from GoodsCart obj where obj.user.id is null and obj.addTime<=:addTime and obj.cart_status=:sc_status",
						params, -1, -1);
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartService.delete(gc.getId());
		}
		// 处理超过7天已经登录用户未提交订单的购物车信息
		params.clear();
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		params.put("addTime", cal.getTime());
		params.put("sc_status", 0);
		cart_list = this.goodsCartService
				.query("select obj from GoodsCart obj where obj.user.id is not null and obj.addTime<=:addTime and obj.cart_status=:sc_status",
						params, -1, -1);
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartService.delete(gc.getId());
		}
		
		params.clear();
		params.put("status", 1);
		List<PayoffLog> payofflogs_1 = this.payoffLogService
				.query("select obj.id from PayoffLog obj where obj.status=:status order by addTime desc",
						params, -1, -1);// 查询所有可结算账单，设置为未结算账单，可以防止上次结算日没有结算的账单在结算日之后的日期结算
		for (int i = 0; i < payofflogs_1.size(); i++) {
			PayoffLog payofflog = this.payoffLogService.getObjById(CommUtil
					.null2Long(payofflogs_1.get(i)));
			payofflog.setStatus(0);
			this.payoffLogService.update(payofflog);
		}
		// 处理过期的生活团购
		params.clear();
		params.put("status", 1);
		params.put("end_time", new Date());
		List<GroupLifeGoods> groups = this.groupLifeGoodsService
				.query("select obj from GroupLifeGoods obj where obj.group_status=:status and obj.endTime<=:end_time",
						params, -1, -1);
		for (GroupLifeGoods group : groups) {
			group.setGroup_status(-2);
			groupLifeGoodsService.update(group);
			// 删除索引
			elasticsearchUtil.indexDelete(IndexName.GOODS, IndexType.LIFEGOODS, CommUtil.null2String(group.getId()));
//			String goodslife_lucene_path = ConfigContants.LUCENE_DIRECTORY
//					+ File.separator + "luence" + File.separator + "lifegoods";
//			File filelife = new File(goodslife_lucene_path);
//			if (!filelife.exists()) {
//				CommUtil.createFolder(goodslife_lucene_path);
//			}
//			LuceneUtil lucene = LuceneUtil.instance();
//			lucene.setIndex_path(goodslife_lucene_path);
//			lucene.delete_index(CommUtil.null2String(group.getId()));
		}
		// 处理过期的团购
		params.clear();
		params.put("status", 1);
		params.put("end_time", new Date());
		List<GroupGoods> groupgoodes = this.groupGoodsService
				.query("select obj from GroupGoods obj where obj.gg_status=:status and obj.endTime<=:end_time",
						params, -1, -1);
		for (GroupGoods group : groupgoodes) {
			group.setGg_status(-2);
			groupGoodsService.update(group);
			// 删除索引
			elasticsearchUtil.indexDelete(IndexName.GOODS, IndexType.GROUPGOODS, CommUtil.null2String(group.getId()));
//			String goodsgroup_lucene_path = ConfigContants.LUCENE_DIRECTORY
//					+ File.separator + "luence" + File.separator + "groupgoods";
//			File filegroup = new File(goodsgroup_lucene_path);
//			if (!filegroup.exists()) {
//				CommUtil.createFolder(goodsgroup_lucene_path);
//			}
//			LuceneUtil lucene = LuceneUtil.instance();
//			lucene.setIndex_path(goodsgroup_lucene_path);
//			lucene.delete_index(CommUtil.null2String(group.getId()));
		}
		// 处理已经过期的团购券
		params.clear();
		params.put("status", 0);
		params.put("end_time", new Date());
		List<GroupInfo> groupInfos = this.groupInfoService
				.query("select obj from GroupInfo obj where obj.status=:status and obj.lifeGoods.endTime<=:end_time",
						params, -1, -1);
		for (GroupInfo info : groupInfos) {
			info.setStatus(-1);
			groupInfoService.update(info);
		}
		// 处理已经过期的优惠券
		params.clear();
		params.put("status", 0);
		params.put("end_time", new Date());
		List<CouponInfo> couponInfos = this.couponInfoService
				.query("select obj from CouponInfo obj where obj.status=:status and obj.coupon.coupon_end_time<=:end_time",
						params, -1, -1);
		for (CouponInfo couponInfo : couponInfos) {
			couponInfo.setStatus(-1);
			couponInfoService.update(couponInfo);
		}
	}

}
