package com.iskyshop.view.web.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.service.IArticleService;

/**
 * @info 文章查询工具类
 * @since v1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */
@Component
public class ArticleViewTools {
	@Autowired
	private IArticleService articleService;

	/**
	 * 根据postion参数查询谋id文章的上一篇、下一篇文章，position为-1为上一篇，position为1为下一篇
	 * 
	 * @param id
	 * @param position
	 * @return
	 */
	public Article queryArticle(Long id, int position) {
		String query = "select obj from Article obj where obj.articleClass.id=:class_id and obj.display=:display and obj.type=:type and ";
		Article article = this.articleService.getObjById(id);
		if (article != null) {
			Map params = new HashMap();
			params.put("addTime", article.getAddTime());
			params.put("class_id", article.getArticleClass().getId());
			params.put("type", "user");
			params.put("display", true);
			if (position > 0) {
				query = query
						+ "obj.addTime>:addTime order by obj.addTime desc";
			} else {
				query = query
						+ "obj.addTime<:addTime order by obj.addTime desc";
			}
			List<Article> objs = this.articleService.query(query, params, 0, 1);
			if (objs.size() > 0) {
				objs.get(0).setContent(HtmlUtils.htmlUnescape(objs.get(0).getContent()));//反转义
				return objs.get(0);
			} else {
				Article obj = new Article();
				obj.setTitle("没有了");
				return obj;
			}
		} else {
			Article obj = new Article();
			obj.setTitle("没有了");
			return obj;
		}
	}
}
