package com.iskyshop.manage.admin.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

/**
 * 
 * <p>
 * Title: StoreTools.java
 * </p>
 * 
 * <p>
 * Description: 后台管理店铺、商品工具类
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
 * @date 2014-5-21
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class StoreTools {
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ISysConfigService configService;

	/**
	 * 生成商品属性字符串
	 * 
	 * @param spec
	 * @return
	 */
	public String genericProperty(GoodsSpecification spec) {
		String val = "";
		for (GoodsSpecProperty gsp : spec.getProperties()) {
			val = val + "," + gsp.getValue();
		}
		if (!val.equals(""))
			return val.substring(1);
		else
			return "";
	}

	/**
	 * 根据系统规则建立图片存储文件夹
	 * 
	 * @param request
	 * @param config
	 * @param store
	 * @return
	 */
	public String createUserFolder(HttpServletRequest request, Store store) {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		if (config.getImageSaveType().equals("sidImg")) {// 按照文件名存放(例:/店铺id/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId();
		}
		if (config.getImageSaveType().equals("sidYearImg")) {// 按照年份存放(例:/店铺id/年/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {// 按照年月存放(例:/店铺id/年/月/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {// 按照年月日存放(例:/店铺id/年/月/日/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date()) + File.separator
					+ CommUtil.formatTime("dd", new Date());
		}
		CommUtil.createFolder(path);
		return path;
	}

	public String createUserFolderURL(Store store) {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		if (config.getImageSaveType().equals("sidImg")) {// 按照文件名存放(例:/店铺id/图片)
			path = uploadFilePath + "/store/" + store.getId().toString();

		}
		if (config.getImageSaveType().equals("sidYearImg")) {// 按照年份存放(例:/店铺id/年/图片)
			path = uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {// 按照年月存放(例:/店铺id/年/月/图片)
			path = uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {// 按照年月日存放(例:/店铺id/年/月/日/图片)
			path = uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date()) + "/"
					+ CommUtil.formatTime("dd", new Date());
		}
		return path;
	}

	/**
	 * 根据分类生成分类信息字符串
	 * 
	 * @param gc
	 * @return
	 */
	public String generic_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = this.generic_the_goods_class_info(gc);
			return goods_class_info.substring(0, goods_class_info.length() - 1);
		} else
			return "";
	}

	private String generic_the_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = gc.getClassName() + ">";
			if (gc.getParent() != null) {
				String class_info = generic_the_goods_class_info(gc.getParent());
				goods_class_info = class_info + goods_class_info;
			}
			return goods_class_info;
		} else
			return "";
	}

	public int query_store_with_user(String user_id) {
		int status = 0;
		Store store = this.storeService.getObjByProperty("user.id",
				CommUtil.null2Long(user_id));
		if (store != null)
			status = 1;
		return status;
	}

	/**
	 * 解析json数据，传入m_id,返回该m_id存在的map,如果map不存在则返回空的map
	 * 
	 * @param json
	 * @return
	 */
	public Map query_MainGc_Map(String m_id, String json) {
		Map map_temp = null;
		if (json != null && !json.equals("")) {
			List<Map> list_map = Json.fromJson(ArrayList.class, json);
			for (Map map : list_map) {
				if (m_id.equals(CommUtil.null2String(map.get("m_id")))) {
					map_temp = map;
					break;
				}
			}
		}
		return map_temp;
	}

	/**
	 * 解析store的店铺经营json，返回店铺主营类目类目
	 * 
	 * @param json
	 * @return
	 */
	public List<GoodsClass> query_store_MainGc(String json) {
		List<GoodsClass> gc_list = new ArrayList<GoodsClass>();
		if (json != null && !json.equals("")) {
			List<Map> list_map = Json.fromJson(ArrayList.class, json);
			for (Map map : list_map) {
				GoodsClass gc = this.goodsClassService.getObjById(CommUtil
						.null2Long(map.get("m_id")));
				if (gc != null) {
					gc_list.add(gc);
				}
			}
		}
		return gc_list;
	}

	/**
	 * 解析store的店铺经营json，返回店铺详细类目
	 * 
	 * @param json
	 * @return
	 */
	public Set<GoodsClass> query_store_DetailGc(String json) {
		Set<GoodsClass> gc_list = new TreeSet<GoodsClass>();
		if (json != null && !json.equals("")) {
			List<Map> all_list = Json.fromJson(ArrayList.class, json);
			for (Map map : all_list) {
				List<Integer> ls = (List) map.get("gc_list");
				if (ls != null && !ls.equals("")) {
					for (Integer l : ls) {
						GoodsClass gc = this.goodsClassService
								.getObjById(CommUtil.null2Long(l));
						gc_list.add(gc);
					}
				}
			}
		}
		return gc_list;
	}

	/**
	 * 根据系统规则建立图片存储文件夹
	 * 
	 * @param request
	 * @param config
	 * @param store
	 * @return
	 */
	public String createAdminFolder(HttpServletRequest request) {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		if (config.getImageSaveType().equals("sidImg")) {// 按照文件名存放(例:/system/self_goods/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods";
		}
		if (config.getImageSaveType().equals("sidYearImg")) {// 按照年份存放(例:/system/self_goods/年/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods" + File.separator
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {// 按照年月存放(例:/system/self_goods/年/月/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods" + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {// 按照年月日存放(例:/system/self_goods/年/月/日/图片)
			path = TytsmsStringUtils.generatorImagesFolderServerPath(request)
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods" + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date()) + File.separator
					+ CommUtil.formatTime("dd", new Date());
		}
		CommUtil.createFolder(path);
		return path;
	}

	/**
	 * 自营商品图片保存路径
	 * 
	 * @return
	 */
	public String createAdminFolderURL() {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME;
		if (config.getImageSaveType().equals("sidImg")) {// 按照文件名存放(例:/system/self_goods/图片)
			path = uploadFilePath + "/system/self_goods";

		}
		if (config.getImageSaveType().equals("sidYearImg")) {// 按照年份存放(例:/system/self_goods/年/图片)
			path = uploadFilePath + "/system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {// 按照年月存放(例:/system/self_goods/年/月/图片)
			path = uploadFilePath + "/system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {// 按照年月日存放(例:/system/self_goods/年/月/日/图片)
			path = uploadFilePath + "/system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date()) + "/"
					+ CommUtil.formatTime("dd", new Date());
		}
		return path;
	}
}
