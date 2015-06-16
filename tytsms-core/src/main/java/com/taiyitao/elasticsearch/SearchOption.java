package com.taiyitao.elasticsearch;

import java.io.Serializable;

public class SearchOption implements Serializable{
	private static final long serialVersionUID = 1L;
	private SearchLogic searchLogic = SearchLogic.must;
	private MySearchType searchType = MySearchType.querystring;
	private DataFilter dataFilter = DataFilter.exists;
	
	/* querystring精度，取值[1-100]的整数 */
	private String queryStringPrecision = "100";
	/* 排名权重 */
	private float boost = 1.0f;
	private boolean highlight = false;

	public SearchOption() {
	}
	
	public SearchOption(MySearchType searchType, SearchLogic searchLogic,
			String queryStringPrecision, DataFilter dataFilter, float boost,
			int highlight) {
		this.setSearchLogic(searchLogic);
		this.setSearchType(searchType);
		this.setQueryStringPrecision(queryStringPrecision);
		this.setDataFilter(dataFilter);
		this.setBoost(boost);
		this.setHighlight(highlight > 0 ? true : false);
	}


	
	public enum MySearchType {
		querystring,	// 按照quert_string搜索，搜索非词组时候使用 
		range,			// 按照区间搜索
		term,			// 按照词组搜索，搜索一个词时候使用 
		wildcard		//统配符查询
		
	}
	
	public enum RangeType{
		gt,	//大于
		gte, //大于等于
		lt,	//小于
		lte //小于等于
	}

	public enum SearchLogic {
		must,	//逻辑must关系 
		should	//逻辑should关系 
	}

	public enum DataFilter {
		exists,	//只显示有值的
		notExists,	//显示没有值的
		all	//显示全部
	}


	public DataFilter getDataFilter() {
		return this.dataFilter;
	}

	public void setDataFilter(DataFilter dataFilter) {
		this.dataFilter = dataFilter;
	}

	public boolean isHighlight() {
		return this.highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public float getBoost() {
		return this.boost;
	}

	public void setBoost(float boost) {
		this.boost = boost;
	}

	public SearchLogic getSearchLogic() {
		return this.searchLogic;
	}

	public void setSearchLogic(SearchLogic searchLogic) {
		this.searchLogic = searchLogic;
	}

	public MySearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(MySearchType searchType) {
		this.searchType = searchType;
	}

	public String getQueryStringPrecision() {
		return this.queryStringPrecision;
	}

	public void setQueryStringPrecision(String queryStringPrecision) {
		this.queryStringPrecision = queryStringPrecision;
	}
}
