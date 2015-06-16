package com.iskyshop.manage.seller.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Plate;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreNavigation;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.PlateQueryObiect;
import com.iskyshop.foundation.domain.query.StoreNavigationQueryObject;
import com.iskyshop.foundation.service.IPlateService;
import com.iskyshop.foundation.service.IStoreNavigationService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * 功能描述：商家设置关联板式
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author cty 新增日期：2015年3月3日
 * @author cty 修改日期：2015年3月3日
 *
 */
@Controller
public class PlateSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPlateService plateService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	
	/**
	 * 关联板式列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "卖家关联板式", value = "/seller/goods_plate_list.htm*", rtype = "seller", rname = "关联板式", rcode = "goods_plate", rgroup = "商品管理")
	@RequestMapping("/seller/goods_plate_list.htm")
	public ModelAndView store_nav(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_plate.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PlateQueryObiect qo = new PlateQueryObiect(
				currentPage, mv, orderBy, orderType);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.store.id", new SysMap("store_id", user.getStore()
				.getId()), "=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		IPageList pList = this.plateService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 跳转到新增板式页面
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家板式添加", value = "/seller/goods_plate_add.htm*", rtype = "seller", rname = "关联板式", rcode = "goods_plate", rgroup = "商品管理")
	@RequestMapping("/seller/goods_plate_add.htm")
	public ModelAndView store_nav_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_plate_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 关联板式保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @return
	 */
	@SecurityMapping(title = "卖家关联板式保存", value = "/seller/goods_plate_save.htm*", rtype = "seller", rname = "关联板式", rcode = "goods_plate", rgroup = "商品管理")
	@RequestMapping("/seller/goods_plate_save.htm")
	public String store_nav_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd) {
		WebForm wf = new WebForm();
		Plate plate = null;
		if (id.equals("")) {
			plate = wf.toPo(request, Plate.class);
			plate.setAddTime(new Date());
		} else {
			Plate obj = this.plateService.getObjById(Long
					.parseLong(id));
			plate = (Plate) wf.toPo(request, obj);
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		plate.setStore(store);
		if (id.equals("")) {
			this.plateService.save(plate);
		} else
			this.plateService.update(plate);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/goods_plate_list.htm");
		request.getSession(false).setAttribute("op_title", "保存板式成功");
		return "redirect:/seller/success.htm";
	}
	
	@SecurityMapping(title = "卖家板式编辑", value = "/seller/goods_plate_edit.htm*", rtype = "seller", rname = "关联板式", rcode = "goods_plate", rgroup = "商品管理")
	@RequestMapping("/seller/goods_plate_edit.htm")
	public ModelAndView store_nav_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_plate_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Plate plate = this.plateService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", plate);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}
	
    /**
     * 删除关联板式
     * @param request
     * @param response
     * @param mulitId
     * @param currentPage
     * @return
     */
	@SecurityMapping(title = "卖家板式删除", value = "/seller/goods_plate_del.htm*", rtype = "seller", rname = "关联板式", rcode = "goods_plate", rgroup = "商品管理")
	@RequestMapping("/seller/goods_plate_del.htm")
	public String store_nav_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Plate plate = this.plateService.getObjById(Long.parseLong(id));
				this.plateService.delete(Long.parseLong(id));
			}
		}
		return "redirect:goods_plate_list.htm?currentPage=" + currentPage;
	}
	
	
}
