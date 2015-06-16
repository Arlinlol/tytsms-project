package com.iskyshop.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.csvreader.CsvReader;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.pay.wechatpay.util.ConfigContants;
import com.iskyshop.pay.wechatpay.util.TytsmsStringUtils;

/**
 * 
 * <p>
 * Title: TaobaoSellerAction.java
 * </p>
 * 
 * <p>
 * Description:淘宝数据控制器 1.0版本 功能包括：淘宝csv导入
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
 * @date 2014-7-17
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Controller
public class TaobaoSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private StoreTools storeTools;

	@SecurityMapping(title = "导入淘宝CSV", value = "/seller/taobao.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao.htm")
	public ModelAndView taobao(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/taobao.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String taobao_upload_status = CommUtil.null2String(request.getSession(
				false).getAttribute("taobao_upload_status"));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (taobao_upload_status.equals("")) {
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
			mv.addObject("ugcs", ugcs);
			List<GoodsClass> gcs = this.storeTools.query_store_MainGc(user
					.getStore().getGc_detail_info());
			if (gcs.size() > 0) {// 店铺详细类目
				mv.addObject("gcs", gcs);
			} else {
				GoodsClass parent = this.goodsClassService.getObjById(user
						.getStore().getGc_main_id());
				mv.addObject("gcs", parent.getChilds());
			}
		}
		if (taobao_upload_status.equals("upload_img")) {
			mv = new JModelAndView(
					"user/default/sellercenter/taobao_import_img.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			HashMap params = new HashMap();
			params.put("user_id", user.getId());
			List<Album> alubms = this.albumService.query(
					"select obj from Album obj where obj.user.id=:user_id",
					params, -1, -1);
			mv.addObject("alubms", alubms);
			mv.addObject("already_import_count", request.getSession(false)
					.getAttribute("already_import_count"));
			mv.addObject("no_import_count", request.getSession(false)
					.getAttribute("no_import_count"));
			mv.addObject("jsessionid", request.getSession().getId());
		}
		if (taobao_upload_status.equals("upload_finish")) {
			mv = new JModelAndView(
					"user/default/sellercenter/taobao_import_finish.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		return mv;
	}

	@SecurityMapping(title = "导入淘宝CSV", value = "/seller/taobao_import_csv.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao_import_csv.htm")
	public ModelAndView taobao_import_csv(HttpServletRequest request,
			HttpServletResponse response, String gc_id3, String gc_id2,
			String ugc_ids) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/taobao_import_img.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String taobao_upload_status = CommUtil.null2String(request.getSession(
				false).getAttribute("taobao_upload_status"));
		String path = TytsmsStringUtils.generatorImagesFolderServerPath(request)+ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME
				+ File.separator + "csv" + File.separator + user.getStore().getId();
		CommUtil.createFolder(path);
		int already_import_count = 0;
		int no_import_count = 0;
		List<Goods> taobao_goods_list = new ArrayList<Goods>();
		try {
			Map map = CommUtil.saveFileToServer(configService,request, "taobao_cvs", path,
					"taobao.cvs", null);
			if (!map.get("fileName").equals("")) {
				String csvFilePath = path + File.separator + "taobao.cvs";
				CsvReader reader = new CsvReader(csvFilePath, '\t',
						Charset.forName("UTF-16LE")); // 一般用这编码读就可以了
				reader.readHeaders();// 跳过表头 如果需要表头的话，不要写这句。
				reader.readHeaders();
				reader.readHeaders();//此处针对淘宝助理5.7版本特别处理。
				int goods_name_pos = 0;// 商品名称位置
				int goods_price_pos = 7;// 商品价格位置
				int goods_count_pos = 9;// 商品数量位置
				int goods_detail_pos = 20;// 商品描述位置
				int goods_transfee_pos = 11;// 商品运费承担
				int goods_recommend_pos = 18;// 商品推荐位置
				Album album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册");
					album.setAlbum_sequence(-10000);
					album.setAlbum_default(true);
					this.albumService.save(album);
				}
				while (reader.readRecord()) { // 逐行读入除表头的数据
					Goods goods = new Goods();
					goods.setGoods_name(reader.get(goods_name_pos));
					goods.setStore_price(BigDecimal.valueOf(CommUtil
							.null2Double(reader.get(goods_price_pos))));
					goods.setGoods_price(goods.getStore_price());
					goods.setGoods_inventory(CommUtil.null2Int(reader
							.get(goods_count_pos)));
					goods.setGoods_status(-5);
					goods.setGoods_recommend(CommUtil.null2Boolean(reader
							.get(goods_recommend_pos)));
					goods.setGoods_details(reader.get(goods_detail_pos));
					goods.setGoods_type(1);
					goods.setGoods_transfee(CommUtil.null2Int(reader
							.get(goods_transfee_pos)) - 1);
					goods.setGoods_current_price(goods.getStore_price());
					goods.setAddTime(new Date());
					goods.setGoods_seller_time(new Date());
					goods.setGoods_type(1);
					goods.setGoods_store(user.getStore());
					goods.setInventory_type("all");
					GoodsClass gc = null;
					if (gc_id3 != null) {
						gc = this.goodsClassService.getObjById(CommUtil
								.null2Long(gc_id3));
					}
					if (gc_id2 != null) {
						gc = this.goodsClassService.getObjById(CommUtil
								.null2Long(gc_id2));
					}
					goods.setGc(gc);
					String[] ugc_id_list = ugc_ids.split(",");
					for (String ugc_id : ugc_id_list) {
						UserGoodsClass ugc = this.userGoodsClassService
								.getObjById(CommUtil.null2Long(ugc_id));
						goods.getGoods_ugcs().add(ugc);
					}
					this.goodsService.save(goods);
					taobao_goods_list.add(goods);
					already_import_count++;
				}
				reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (already_import_count > 0) {
			HashMap params = new HashMap();
			params.put("user_id", user.getId());
			List<Album> alubms = this.albumService.query(
					"select obj from Album obj where obj.user.id=:user_id",
					params, -1, -1);
			mv.addObject("alubms", alubms);
			mv.addObject("jsessionid", request.getSession().getId());
			request.getSession(false).setAttribute("taobao_goods_list",
					taobao_goods_list);
			request.getSession(false).setAttribute("taobao_upload_status",
					"upload_img");
			request.getSession(false).setAttribute("already_import_count",
					already_import_count);
			request.getSession(false).setAttribute("no_import_count",
					no_import_count);
		}
		mv.addObject("already_import_count", already_import_count);
		mv.addObject("no_import_count", no_import_count);
		return mv;
	}

	@SecurityMapping(title = "上传淘宝图片", value = "/seller/taobao_img_upload.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao_img_upload.htm")
	public void taobao_img_upload(HttpServletRequest request,
			HttpServletResponse response, String user_id, String album_id) {
		User user = this.userService
				.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		String csv_path = TytsmsStringUtils.generatorImagesFolderServerPath(request)+ConfigContants.UPLOAD_IMAGE_MIDDLE_NAME
				+ File.separator + "csv"+ File.separator + user.getStore().getId();;
		try {
			String csvFilePath = csv_path + File.separator + "taobao.cvs";
			CsvReader reader = new CsvReader(csvFilePath, '\t',
					Charset.forName("UTF-16LE")); // 一般用这编码读就可以了
			reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。
			reader.readHeaders();
			reader.readHeaders();//此处针对淘宝助理5.7版本特别处理。
			int goods_name_pos = 0;// 商品名称位置
			int goods_price_pos = 7;// 商品价格位置
			int goods_photo_pos = 28;// 商品图片位置
			String photo_path = this.storeTools.createUserFolder(request,
					user.getStore())+File.separator+"taobao";
			String photo_url = this.storeTools.createUserFolderURL(user
					.getStore())+"/taobao";
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest
					.getFile("imgFile");
			String upload_img_name = file.getOriginalFilename();
			upload_img_name = upload_img_name.substring(0,
					upload_img_name.indexOf("."));
			double fileSize = Double.valueOf(file.getSize());// 返回文件大小，单位为byte
			fileSize = fileSize / 1048576;
			double csize = CommUtil.fileSize(new File(photo_path));
			double remainSpace = 0;
			if (user.getStore().getGrade().getSpaceSize() != 0) {
				remainSpace = (user.getStore().getGrade().getSpaceSize() * 1024 - csize) * 1024;
			} else {
				remainSpace = 10000000;
			}
			Map json_map = new HashMap();
			List<Goods> goods_list = (List<Goods>) request.getSession(false)
					.getAttribute("taobao_goods_list");
			Goods goods = new Goods();
			Boolean ret = false;
			while (reader.readRecord()) { // 逐行读入除表头的数据
				if (reader.get(goods_photo_pos).indexOf(upload_img_name) >= 0) {
					String goods_name = reader.get(goods_name_pos);
					double goods_price = CommUtil.null2Double(reader
							.get(goods_price_pos));
					for (Goods temp_goods : goods_list) {
						if (temp_goods.getGoods_name().equals(goods_name)
								&& CommUtil.null2Double(temp_goods
										.getGoods_price()) == goods_price) {
							goods = temp_goods;
							if(reader.get(goods_photo_pos).indexOf(upload_img_name)==0){
								ret = true;
							}
						}
					}
				}
			}
			reader.close();
			if (goods != null) {
				// 上传指定图片
				if (remainSpace > fileSize) {
					try {
						Map map = CommUtil.saveFileToServer(configService,request, "imgFile",
								photo_path, upload_img_name + ".tbi", null);
						Map params = new HashMap();
						params.put("store_id", user.getStore().getId());
						List<WaterMark> wms = this.waterMarkService
								.query("select obj from WaterMark obj where obj.store.id=:store_id",
										params, -1, -1);
						if (wms.size() > 0) {
							WaterMark mark = wms.get(0);
							if (mark.isWm_image_open()) {
								String pressImg =TytsmsStringUtils.generatorImagesFolderServerPath(request)
										+ mark.getWm_image().getPath()
										+ File.separator
										+ mark.getWm_image().getName();
								String targetImg = photo_path + File.separator
										+ map.get("fileName");
								int pos = mark.getWm_image_pos();
								float alpha = mark.getWm_image_alpha();
								CommUtil.waterMarkWithImage(pressImg,
										targetImg, pos, alpha);
							}
							if (mark.isWm_text_open()) {
								String targetImg = photo_path + File.separator
										+ map.get("fileName");
								int pos = mark.getWm_text_pos();
								String text = mark.getWm_text();
								String markContentColor = mark
										.getWm_text_color();
								CommUtil.waterMarkWithText(
										targetImg,
										targetImg,
										text,
										markContentColor,
										new Font(mark.getWm_text_font(),
												Font.BOLD, mark
														.getWm_text_font_size()),
										pos, 100f);
							}
						}
						Accessory image = new Accessory();
						image.setAddTime(new Date());
						image.setExt((String) map.get("mime"));
						image.setPath(photo_url);
						image.setWidth(CommUtil.null2Int(map.get("width")));
						image.setHeight(CommUtil.null2Int(map.get("height")));
						image.setName(CommUtil.null2String(map.get("fileName")));
						image.setUser(user);
						Album album = null;
						if (album_id != null && !album_id.equals("")) {
							album = this.albumService.getObjById(CommUtil
									.null2Long(album_id));
						} else {
							album = this.albumService.getDefaultAlbum(CommUtil
									.null2Long(user_id));
							if (album == null) {
								album = new Album();
								album.setAddTime(new Date());
								album.setAlbum_name("默认相册");
								album.setAlbum_sequence(-10000);
								album.setAlbum_default(true);
								this.albumService.save(album);
							}
						}
						image.setAlbum(album);
						this.accessoryService.save(image);
						if (ret) {
							goods.setGoods_main_photo(image);
						} else {
							goods.getGoods_photos().add(image);
						}
						this.goodsService.update(goods);
						json_map.put("url", CommUtil.getURL(request) + "/"
								+ photo_url + "/" + image.getName());
						json_map.put("id", image.getId());
						json_map.put("remainSpace", remainSpace == 10000000 ? 0
								: remainSpace);
						// 同步生成小图片
						String ext = image.getExt().indexOf(".") < 0 ? "."
								+ image.getExt() : image.getExt();
						String source =  TytsmsStringUtils.generatorImagesFolderServerPath(request)
								+ image.getPath()
								+ File.separator
								+ image.getName();
						String target = source + "_small" + ext;
						CommUtil.createSmall(source, target, this.configService
								.getSysConfig().getSmallWidth(),
								this.configService.getSysConfig()
										.getSmallHeight());
						// 同步生成中等图片
						String midext = image.getExt().indexOf(".") < 0 ? "."
								+ image.getExt() : image.getExt();
						String midtarget = source + "_middle" + ext;
						CommUtil.createSmall(source, midtarget, this.configService
								.getSysConfig().getMiddleWidth(),
								this.configService.getSysConfig().getMiddleHeight());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					json_map.put("url", "");
					json_map.put("id", "");
					json_map.put("remainSpace", 0);

				}
				// 图片上传完毕
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.getSession(false).setAttribute("taobao_upload_status",
				"upload_finish");
	}

	@SecurityMapping(title = "淘宝导入完成", value = "/seller/taobao_import_finish.htm*", rtype = "seller", rname = "淘宝导入", rcode = "taobao_seller", rgroup = "商品管理")
	@RequestMapping("/seller/taobao_import_finish.htm")
	public ModelAndView taobao_import_finish(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/taobao_import_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		request.getSession(false).removeAttribute("taobao_upload_status");
		request.getSession(false).removeAttribute("taobao_goods_list");
		request.getSession(false).removeAttribute("already_import_count");
		request.getSession(false).removeAttribute("no_import_count");
		return mv;
	}

	/**
	 * 淘宝授权返回处理
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @param state
	 * @return
	 */
	@RequestMapping("/seller/taobao_authorize.htm")
	public ModelAndView taobao_authorize(HttpServletRequest request,
			HttpServletResponse response, String code, String state) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/taobao_import_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
}
