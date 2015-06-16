package com.iskyshop.foundation.service.impl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;

import com.iskyshop.core.query.PageObject;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.query.support.IQueryObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.query.GenericPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.service.IAdvertPositionService;

@Service
@Transactional
public class AdvertPositionServiceImpl implements IAdvertPositionService{
	@Resource(name = "advertPositionDAO")
	private IGenericDAO<AdvertPosition> advertPositionDao;
	
	public boolean save(AdvertPosition advertPosition) {
		/**
		 * init other field here
		 */
		try {
			this.advertPositionDao.save(advertPosition);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public AdvertPosition getAdvertPositionById(Long id) {
		AdvertPosition ap = this.advertPositionDao.get(id);
		AdvertPosition obj = new AdvertPosition();
		if (ap != null) {
			if (ap != null) {
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
				if (obj.getAp_status() != 1) {
					obj = new AdvertPosition();
				}
			}
		}
		return obj;
	}
	
	
	public AdvertPosition getObjById(Long id) {
		AdvertPosition advertPosition = this.advertPositionDao.get(id);
		if (advertPosition != null) {
			return advertPosition;
		}
		return null;
	}
	
	
	
	
	public boolean delete(Long id) {
		try {
			this.advertPositionDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> advertPositionIds) {
		// TODO Auto-generated method stub
		for (Serializable id : advertPositionIds) {
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
		GenericPageList pList = new GenericPageList(AdvertPosition.class, query,
				params, this.advertPositionDao);
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
	
	public boolean update(AdvertPosition advertPosition) {
		try {
			this.advertPositionDao.update( advertPosition);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<AdvertPosition> query(String query, Map params, int begin, int max){
		return this.advertPositionDao.query(query, params, begin, max);
		
	}
}
