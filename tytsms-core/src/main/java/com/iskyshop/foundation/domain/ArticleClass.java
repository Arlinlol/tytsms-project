package com.iskyshop.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 
* <p>Title: ArticleClass.java</p>

* <p>Description: 系统文章分类</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-25

* @version iskyshop_b2b2c 1.0
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "articleclass")
public class ArticleClass extends IdEntity {
	private String className;// 名称
	private int sequence;// 排序
	private int level;// 层级
	private String mark;// 分类标识，用来标注系统分类
	private boolean sysClass;// 是否系统分类
	@ManyToOne(fetch = FetchType.LAZY)
	private ArticleClass parent;
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<ArticleClass> childs = new ArrayList<ArticleClass>();
	@OneToMany(mappedBy = "articleClass", cascade = CascadeType.REMOVE)
	@OrderBy(value = "sequence asc")
	private List<Article> articles = new ArrayList<Article>();

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isSysClass() {
		return sysClass;
	}

	public void setSysClass(boolean sysClass) {
		this.sysClass = sysClass;
	}

	public ArticleClass getParent() {
		return parent;
	}

	public void setParent(ArticleClass parent) {
		this.parent = parent;
	}

	public List<ArticleClass> getChilds() {
		return childs;
	}

	public void setChilds(List<ArticleClass> childs) {
		this.childs = childs;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

}
