package com.iskyshop.manage.admin.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.GeneratorType;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.taiyitao.quartz.job.GenerateHomePageJob;
import com.taiyitao.quartz.job.GeneratorTools;

/**
 * 
* <p>Title: CacheManageAction.java</p>

* <p>Description: 系统缓存管理控制器</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014年5月27日

* @version 1.0
 */
@Controller
public class CacheManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private GenerateHomePageJob generateHomePageJob;
	@Autowired
	private GeneratorTools generatorTools;
	
	protected Logger _log = LoggerFactory.getLogger(getClass());

	@SecurityMapping(title = "缓存列表", value = "/admin/cache_list.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
	@RequestMapping("/admin/cache_list.htm")
	public ModelAndView cache_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/cache_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		CacheManager manager = CacheManager.create();
		BlockingCache cache = new BlockingCache(manager
				.getEhcache("SimplePageFragmentCachingFilter"));
		int data_cache_size = 0;
		long cache_memory_size = 0;
		for (String name : manager.getCacheNames()) {
			data_cache_size = data_cache_size
					+ (manager.getCache(name) != null ? manager.getCache(name)
							.getSize() : 0);
			cache_memory_size = cache_memory_size
					+ (manager.getCache(name) != null ? manager.getCache(name)
							.getMemoryStoreSize() : 0);
		}
		mv.addObject("cache_memory_size", cache_memory_size);
		mv.addObject("data_cache_size", data_cache_size);
		mv.addObject("page_cache_size", cache.getSize());
		return mv;
	}

	@SecurityMapping(title = "更新缓存", value = "/admin/update_cache.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
	@RequestMapping("/admin/update_cache.htm")
	public ModelAndView update_cache(HttpServletRequest request,
			HttpServletResponse response, String data_cache, String page_cache) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		CacheManager manager = CacheManager.create();
		String[] names = manager.getCacheNames();
		if (CommUtil.null2Boolean(data_cache)) {
			for (String name : names) {
				// System.out.println("缓存名为:" + name);
				if (!name.equalsIgnoreCase("SimplePageCachingFilter")) {
					manager.clearAllStartingWith(name);
				}
			}
		}
		// manager.clearAll();
		if (CommUtil.null2Boolean(page_cache)) {
			Ehcache cache = manager.getEhcache("SimplePageCachingFilter");
			manager.clearAllStartingWith("SimplePageCachingFilter");
		}
		//重新生成首页静态化文件
		generatorTools.generator(request,GeneratorType.ALL,null,null, "手动触发生成静态页面");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/cache_list.htm");
		mv.addObject("op_title", "更新缓存成功");
		return mv;
	}
	

	
	
	
}
