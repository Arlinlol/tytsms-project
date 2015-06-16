package com.taiyitao.elasticsearch;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	private List<IndexVo> vo_list = new ArrayList<IndexVo>();
	private int pages;// 总页数
	private int rows;// 总记录数
	private int currentPage;// 当前页码
	private int pageSize;// 每页大小


	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<IndexVo> getVo_list() {
		return vo_list;
	}

	public void setVo_list(List<IndexVo> vo_list) {
		this.vo_list = vo_list;
	}
	
}
