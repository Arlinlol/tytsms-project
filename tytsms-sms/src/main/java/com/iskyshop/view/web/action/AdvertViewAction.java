package com.iskyshop.view.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * @info 广告调用控制器,
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Controller
public class AdvertViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAdvertPositionService advertPositionService;
	@Autowired
	private IAdvertService advertService;

	/**
	 * 广告调用方法
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * 
	 * <p>
	 * 调用广告块，根据广告ID，调用广告，系统中只保存5种广告，
	 * </p>
	 */
	@RequestMapping("/advert_invoke.htm")
	public ModelAndView advert_invoke(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("advert_invoke.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (id != null && !id.equals("")) {
			AdvertPosition ap = this.advertPositionService.getObjById(CommUtil
					.null2Long(id));
			if (ap != null) {
				AdvertPosition obj = new AdvertPosition();
				obj.setAp_type(ap.getAp_type());
				obj.setAp_status(ap.getAp_status());
				obj.setAp_show_type(ap.getAp_show_type());
				obj.setAp_width(ap.getAp_width());
				obj.setAp_height(ap.getAp_height());
				obj.setAp_location(ap.getAp_location());
				List<Advert> advs = new ArrayList<Advert>();
				for (Advert temp_adv : ap.getAdvs()) {
					if (temp_adv.getAd_status() == 1
							&& temp_adv.getAd_begin_time().before(new Date())
							&& temp_adv.getAd_end_time().after(new Date())) {
						advs.add(temp_adv);
					}
				}
				if (advs.size() > 0) {
					if (obj.getAp_type().equals("text")) {//文字广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAp_text(advs.get(0).getAd_text());
							obj.setAp_acc_url(advs.get(0).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(0)
									.getId()));
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							int i = random.nextInt(advs.size());
							obj.setAp_text(advs.get(i).getAd_text());
							obj.setAp_acc_url(advs.get(i).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(i)
									.getId()));
						}
					}
					if (obj.getAp_type().equals("img")) {//图片广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAp_acc(advs.get(0).getAd_acc());
							obj.setAp_acc_url(advs.get(0).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(0)
									.getId()));
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							int i = random.nextInt(advs.size());
							obj.setAp_acc(advs.get(i).getAd_acc());
							obj.setAp_acc_url(advs.get(i).getAd_url());
							obj.setAdv_id(CommUtil.null2String(advs.get(i)
									.getId()));
						}
					}
					if (obj.getAp_type().equals("slide")) {//幻灯广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									8);
							for (int i : list) {
								obj.getAdvs().add(advs.get(i));
							}
						}
					}
					if (obj.getAp_type().equals("scroll")) {//滚动广告
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									12);
							for (int i : list) {
								obj.getAdvs().add(advs.get(i));
							}
						}
					}
					if(obj.getAp_type().equals("bg_slide")){
						if (obj.getAp_show_type() == 0) {// 固定广告
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {// 随机广告
							Random random = new Random();
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									4);
							for (int i : list) {
								obj.getAdvs().add(advs.get(i));
							}
						}
					}
				} else {
					obj.setAp_acc(ap.getAp_acc());
					obj.setAp_text(ap.getAp_text());
					obj.setAp_acc_url(ap.getAp_acc_url());
					Advert adv = new Advert();
					adv.setAd_url(obj.getAp_acc_url());
					adv.setAd_acc(ap.getAp_acc());
					obj.getAdvs().add(adv);
					obj.setAp_location(ap.getAp_location());
				}
				if (obj.getAp_status() == 1) {
					mv.addObject("obj", obj);
				} else {
					mv.addObject("obj", new AdvertPosition());
				}
			}
		}
		return mv;
	}

	/**
	 * 广告URL跳转方法
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @param id
	 * 
	 * <p>
	 * 根据广告链接进行跳转，搜索广告ID，如果广告存在，跳转到广告地址，并且修改广告点击数量
	 * 每点击一次新增1个点击量，如果根据ID搜索的广告为空，跳转到当前页面
	 * @author nickey
	 * </p>
	 */
	@RequestMapping("/advert_redirect.htm")
	public void advert_redirect(HttpServletRequest request,
			HttpServletResponse response, String id) {
		try {
			Advert adv = this.advertService.getObjById(CommUtil.null2Long(id));
			if (adv != null) {
				adv.setAd_click_num(adv.getAd_click_num() + 1);
				this.advertService.update(adv);
			}
			if (adv != null) {
				String url = adv.getAd_url();
				response.sendRedirect(url);
			} else {
				response.sendRedirect(CommUtil.getURL(request));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
