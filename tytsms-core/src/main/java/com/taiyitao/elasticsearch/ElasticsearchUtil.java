package com.taiyitao.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.xml.builders.BooleanFilterBuilder;
import org.apache.lucene.search.FilteredQuery;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.exists.ExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.nutz.lang.util.ArraySet;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;
import com.taiyitao.elasticsearch.SearchOption.DataFilter;
import com.taiyitao.elasticsearch.SearchOption.MySearchType;
import com.taiyitao.elasticsearch.SearchOption.RangeType;
import com.taiyitao.elasticsearch.SearchOption.SearchLogic;

@Component
public class ElasticsearchUtil {
	private static final Client  client = ESTransportClient.getEsClient();
	protected static Logger log = LoggerFactory.getInstance(ElasticsearchUtil.class, Level.DEBUG); 
    private String highlightCSS = "em style=\"color: red;font-style: normal;\",em";  //
    private int pageSize = 24;
    private String[] indexNames={IndexName.GOODS.toString().toLowerCase()};
    
    /**
     * 
     * @param indexName
     * @param indexType
     * @param indexVo
     * @return
     */
    public boolean index(IndexName indexName,IndexType indexType, IndexVo indexVo){
    	long start = System.currentTimeMillis();
    	BulkRequestBuilder bulkRequest = client.prepareBulk();
    	
    	XContentBuilder xContentBuilder = null;
        try {
            xContentBuilder = XContentFactory.jsonBuilder().startObject();
            String source = JacksonUtils.obj2Json(indexVo);
            bulkRequest.add(client.prepareIndex(indexName.toString().toLowerCase(), indexType.toString().toLowerCase(), indexVo.getVo_id().toString())
            			.setSource(source));
            xContentBuilder = xContentBuilder.endObject();
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (!bulkResponse.hasFailures()) {
                return true;
            } else {
            	log.error(bulkResponse.buildFailureMessage());
            }
            long end  = System.currentTimeMillis();
            log.info("创建索引用时："+(end-start));
        }catch (IOException e) {
        	log.error(e.getMessage());
            return false;
        }  
	    return false;  
    }
    
    
    /**
     * 创建索引并插入document
     * @param indexName
     * @param ingdexType
     * @return
     */
    public boolean index(IndexName indexName,IndexType indexType,List<IndexVo> indexVoList){
    	long start = System.currentTimeMillis();
    	BulkRequestBuilder bulkRequest = client.prepareBulk();
        try {
        	if(indexVoList!=null && indexVoList.size()>0){
        		 for(IndexVo vo:indexVoList){
                 	String source = JacksonUtils.obj2Json(vo);
                 	bulkRequest.add(client.prepareIndex(indexName.toString().toLowerCase(), indexType.toString().toLowerCase(), vo.getVo_id().toString())
                 			.setSource(source));
     	        }
                 BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                 if (!bulkResponse.hasFailures()) {
                     return true;
                 } else {
                     log.error(bulkResponse.buildFailureMessage());
                 }
        	}
            long end  = System.currentTimeMillis();
            log.info("创建索引用时："+(end-start));
        }catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }  
	    return false;  
    }
    
    
    
    
    /**
     * 验证索引是否存在
     * @param indexName
     * @return
     */
    public boolean indexExists(String indexName){
    	boolean exists = false;
        try {
        	ExistsResponse  existsResponse = client.prepareExists(indexName.toString().toLowerCase()).execute().actionGet();
        	exists = existsResponse.exists();
        }catch (Exception e) {
            log.error(e.getMessage());
            exists =  false;
        }  
	    return exists;  
    }  
    
    
    public boolean indexExists(IndexName indexName,IndexType indexType,String indexId){
    	boolean exists = false;
        try {
        	GetResponse respon = client.prepareGet(indexName.toString().toLowerCase(),indexType.toString().toLowerCase(),indexId).execute().actionGet();
        	if(respon.isExists()){
        		exists = true;
        	}
        }catch (Exception e) {
            log.error(e.getMessage());
            exists =  false;
        }  
	    return exists;  
    }  
    
    /**
     * 
     * @param indexName
     * @return
     */
    public Boolean indexExist(String indexName) {
        IndicesExistsRequestBuilder req = client.admin().indices().prepareExists(indexName);
        try {
            IndicesExistsResponse resp = req.execute().get();
            return resp.isExists();
        } catch (InterruptedException e) {
           log.error(e.getMessage());
        } catch (ExecutionException e) {
        	log.error(e.getMessage());
        }
        return false;
    }
    

    
    
    /**
     * 
     * @param indexName
     * @param insertContentMap
     * @return
     */
    public boolean indexDelete(IndexName indexName,IndexType indexType,String indexId){
    	boolean flag = true;
    	try {
    		if(indexExists(indexName, indexType, indexId)){
        		client.prepareDelete(indexName.toString().toLowerCase(),indexType.toString().toLowerCase(),indexId).execute().actionGet();
    		}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
    	return flag;
    }
    
    /**
     * 删除索引
     * @param indexName
     * @param indexType
     * @param indexId
     * @return
     */
    public boolean indexAllDelete(String indexName){
    	boolean flag = true;
    	try {
    		if(indexExists(indexName)){
    			MatchAllQueryBuilder allQueryBuilder = QueryBuilders.matchAllQuery();
        		client.prepareDeleteByQuery(indexName).setQuery(allQueryBuilder).execute().actionGet();
    		}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
    	return flag;
    }
    
    /**
     * 
     * @param indexName
     * @param indexType
     * @param indexId
     * @param updateFiles
     * @return
     */
    public boolean indexUpdate(IndexName indexName,IndexType indexType,String indexId,IndexVo indexVo){
    	try {
    		String source = JacksonUtils.obj2Json(indexVo);
    		if(indexExists(indexName, indexType, indexId)){
    			UpdateRequest updateRequest = new UpdateRequest(indexName.toString().toLowerCase(),indexType.toString().toLowerCase(),indexId);
        		updateRequest.doc(source);
    	        client.update(updateRequest).get();
    		}else{
    			index(indexName, indexType, indexVo);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    
    
    /**
     * 索引字段更新
     * @param indexName	索引名
     * @param indexType	索引类型
     * @param indexId	索引Id
     * @param updateFiles 更新字段集
     * @return
     */
    public boolean indexUpdate(IndexName indexName,IndexType indexType,String indexId,Map<String,Object> updateFiles){
    	XContentBuilder xContentBuilder = null;
    	try {
    		UpdateRequest updateRequest = new UpdateRequest(indexName.toString().toLowerCase(),indexType.toString().toLowerCase(),indexId);
    		xContentBuilder = XContentFactory.jsonBuilder().startObject();
 	       	Iterator<Entry<String, Object>> iterator = updateFiles.entrySet().iterator();
	        while (iterator.hasNext()) {
	            Entry<String, Object> entry = iterator.next();
	            String field = entry.getKey();
	            Object value = entry.getValue();
	            xContentBuilder = xContentBuilder.field(field, value);
	        }
	        xContentBuilder = xContentBuilder.endObject();
	        updateRequest.doc(xContentBuilder);
	        UpdateResponse d = client.update(updateRequest).get();
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
    	return true;
    }
    
    
    /**
	 * 
	 * @param keywords 查询关键字
	 * @param pageNo 当前页数
	 * @param begin_price 价格区间起始
	 * @param end_price 价格区间结束
	 * @param after 文档（暂时未使用 null）
	 * @param sort 排序 
	 * @param goods_inventory 库存数 -1全部 0仅显示有货
	 * @param goods_type 商品类型（-1全部 /0商城自营/1商家商品）
	 * @param goods_cat 折扣（暂时未用到 ""）
	 * @param goods_area 商品地区
	 * @param goods_brand 商品品牌
	 * @param query_gc 商品分类查询
	 * @return
	 */
    public SearchResult search1(String keywords, int pageNo, double begin_price,
			double end_price, String sortField,String sortType,
			String goods_inventory, String goods_type, String goods_cat,
			String goods_area, String goods_brand, String query_gc) {
    	SearchResult searcheResult = new SearchResult();
    	try {  
    		sortField = IndexVoTools.getSortField(sortField);
    		sortType = StringUtils.isEmpty(sortType)?"desc":sortType;
        	HashMap<String, Object[]> searchContentMap = new HashMap<String, Object[]>();
        	//搜索内容处理
        	if(StringUtils.isNotEmpty(keywords)){
        		Object[] values = {keywords, new SearchOption(MySearchType.querystring, SearchLogic.must, "100", DataFilter.exists, 1.0f, 1)};
            	searchContentMap.put(IndexVo.VO_TITLE, values);	
        	}
        	//过滤内容处理
        	HashMap<String, Object[]> filterContentMap = new HashMap<String, Object[]>();
        	if (begin_price >= 0 && end_price >=0) {//加过区间过滤
        		Object[] value = {begin_price,end_price,new SearchOption(MySearchType.range, SearchLogic.must, "100", DataFilter.all, 1.0f, 0)};
        		filterContentMap.put(IndexVo.VO_COST_PRICE, value);
        	}
        	//库存过滤
        	if(StringUtils.isNotEmpty(goods_inventory) && goods_inventory.equals("0")){//-1显示全部，0仅显示有货
        		Object[] value = {goods_inventory,RangeType.gt,new SearchOption(MySearchType.range, SearchLogic.must, "100", DataFilter.exists, 1.0f, 0)};
        		searchContentMap.put(IndexVo.VO_GOODS_INVENTORY, value);
        	}
        	//商品类型过滤
        	if(StringUtils.isNotEmpty(goods_type)&&!goods_type.equals("-1")){//商品类型（-1全部 /0商城自营/1商家商品）
        		Object[] value = {goods_type,new SearchOption(MySearchType.querystring, SearchLogic.must, "100", DataFilter.exists, 1.0f, 0)};
            	searchContentMap.put(IndexVo.VO_GOODS_TYPE, value);
        	}
        	//地区过滤
        	if(StringUtils.isNotEmpty(goods_area)){//地区过滤
        		Object[] value = {goods_area,new SearchOption(MySearchType.querystring, SearchLogic.must, "100", DataFilter.exists, 1.0f, 0)};
            	searchContentMap.put(IndexVo.VO_GOODS_AREA, value);
        	}
        	//品牌过滤
        	if(StringUtils.isNotEmpty(goods_brand)){//品牌过滤
        		Object[] value = {goods_brand,new SearchOption(MySearchType.querystring, SearchLogic.must, "100", DataFilter.exists, 1.0f, 0)};
            	searchContentMap.put(IndexVo.VO_GOODS_BRAND, value);
        	}
        	//商品分类
        	if(StringUtils.isNotEmpty(query_gc)){//商品分类
        		if(query_gc.contains("_*")){
        			Object[] value = {query_gc,new SearchOption(MySearchType.wildcard, SearchLogic.must, "100", DataFilter.exists, 1.0f, 0)};
                	searchContentMap.put(IndexVo.VO_GOODS_CLASS, value);
        		}else{
        			Object[] value = {query_gc,new SearchOption(MySearchType.querystring, SearchLogic.must, "100", DataFilter.exists, 1.0f, 0)};
                	searchContentMap.put(IndexVo.VO_GOODS_CLASS, value);
        		}
        	}
        	
        	
    	    long count = this.getCount(indexNames, searchContentMap, null);
        	int pages = ((int)count+ this.pageSize - 1) / this.pageSize; // 记算总页数
			int intPageNo = (pageNo > pages ? pages : pageNo);
			if (intPageNo < 1)intPageNo = 1;
        	
			List<Map<String, Object>> list = this.simpleSearch(indexNames, searchContentMap, filterContentMap, (intPageNo-1)*pageSize, pageSize, sortField, sortType);
        	if(list!=null && list.size()>0){
        		List<IndexVo> indexList = new ArrayList<IndexVo>();
            	for(Map<String, Object> obj:list){
            		IndexVo indexVo = JacksonUtils.json2Obj(JacksonUtils.obj2Json(obj), IndexVo.class);
            		indexList.add(indexVo);
            	}  
            	searcheResult.setCurrentPage(intPageNo);
            	searcheResult.setPages(pages);
            	searcheResult.setPageSize(pageSize);
            	searcheResult.setRows((int)count);
            	searcheResult.setVo_list(indexList);
        	}
		} catch (Exception e) {
			log.equals(e.getMessage());
		}
    	return searcheResult;
    }
    
    
    public SearchResult search(String keywords, int pageNo, double begin_price,
			double end_price, String sortField,String sortType,
			String goods_inventory, String goods_type, String goods_cat,
			String goods_area, String goods_brand, String query_gc) {
    	SearchResult searcheResult = new SearchResult();
    	try {  
    		sortField = IndexVoTools.getSortField(sortField);
    		sortType = StringUtils.isEmpty(sortType)?"desc":sortType;
        	
        	BoolQueryBuilder query = QueryBuilders.boolQuery();
        	//搜索内容处理
        	if(StringUtils.isNotEmpty(keywords)){
        		BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
        		shouldQuery.should(QueryBuilders.wildcardQuery(IndexVo.VO_TITLE,"*"+keywords+"*"));
        		shouldQuery.should(QueryBuilders.wildcardQuery(IndexVo.VO_CONTENT,"*"+keywords+"*"));
        		query.must(shouldQuery);
        	}
        	//过滤内容处理
//        	BoolFilterBuilder filter = FilterBuilders.boolFilter();
        	if (begin_price >= 0 && end_price >=0) {//加过区间过滤
//        		filter.must(FilterBuilders.rangeFilter(IndexVo.VO_COST_PRICE).from(begin_price).to(end_price).cache(true));
        		query.must(QueryBuilders.rangeQuery(IndexVo.VO_COST_PRICE).from(begin_price).to(end_price));
        	}
        	//库存过滤
        	if(StringUtils.isNotEmpty(goods_inventory) && goods_inventory.equals("0")){//-1显示全部，0仅显示有货
//        		filter.must(FilterBuilders.rangeFilter(IndexVo.VO_GOODS_INVENTORY).gt(0).cache(true));
        		query.must(QueryBuilders.rangeQuery(IndexVo.VO_GOODS_INVENTORY).gt(0));
        	}
        	//商品类型过滤
        	if(StringUtils.isNotEmpty(goods_type)&&!goods_type.equals("-1")){//商品类型（-1全部 /0商城自营/1商家商品）
        		query.must(QueryBuilders.matchQuery(IndexVo.VO_GOODS_TYPE,goods_type));
        	}
        	//地区过滤
        	if(StringUtils.isNotEmpty(goods_area)){//地区过滤
        		query.must(QueryBuilders.matchQuery(IndexVo.VO_GOODS_AREA,goods_area));
        	}
        	//品牌过滤
        	if(StringUtils.isNotEmpty(goods_brand)){//品牌过滤
        		query.must(QueryBuilders.matchQuery(IndexVo.VO_GOODS_BRAND,goods_brand));
        	}
        	//商品分类
        	if(StringUtils.isNotEmpty(query_gc)){//商品分类
        		if(query_gc.contains("_*")){
        			query.must(QueryBuilders.wildcardQuery(IndexVo.VO_GOODS_CLASS, query_gc));
        		}else{
        			query.must(QueryBuilders.matchQuery(IndexVo.VO_GOODS_CLASS, query_gc));
        		}
        	}
//        	FilteredQueryBuilder  filters = QueryBuilders.filteredQuery(query, filter);
        	
    	    long count = getCount(IndexName.GOODS, query);
        	int pages = ((int)count+ this.pageSize - 1) / this.pageSize; // 记算总页数
			int intPageNo = (pageNo > pages ? pages : pageNo);
			if (intPageNo < 1)intPageNo = 1;
			
			SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setQuery(query).setFrom((intPageNo-1)*pageSize).setSize(pageSize).setExplain(true);
        	
			if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
               
            } else {
               SortOrder sortOrder = sortType.equals("desc") ? SortOrder.DESC : SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
			
			searchRequestBuilder = searchRequestBuilder.addHighlightedField(IndexVo.VO_TITLE, 1000)
                       .setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                       .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">");
			
			System.out.println(searchRequestBuilder.toString());
			SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
			
			List<Map<String, Object>> list  = this.getSearchResult(searchResponse);
			//List<Map<String, Object>> list = this.simpleSearch(indexNames, searchContentMap, filterContentMap, (intPageNo-1)*pageSize, pageSize, sortField, sortType);
        	if(list!=null && list.size()>0){
        		List<IndexVo> indexList = new ArrayList<IndexVo>();
            	for(Map<String, Object> obj:list){
            		IndexVo indexVo = JacksonUtils.json2Obj(JacksonUtils.obj2Json(obj), IndexVo.class);
            		indexList.add(indexVo);
            	}  
            	searcheResult.setCurrentPage(intPageNo);
            	searcheResult.setPages(pages);
            	searcheResult.setPageSize(pageSize);
            	searcheResult.setRows((int)count);
            	searcheResult.setVo_list(indexList);
        	}
		} catch (Exception e) {
			log.equals(e.getMessage());
		}
    	return searcheResult;
    }
    
    
//    public SearchResult filterSearch(String keywords, int pageNo, double begin_price,
//			double end_price, String sortField,String sortType,
//			String goods_inventory, String goods_type, String goods_cat,
//			String goods_area, String goods_brand, String query_gc) {
//    	SearchResult searcheResult = new SearchResult();
//    	try {  
//    		sortField = IndexVoTools.getSortField(sortField);
//    		sortType = StringUtils.isEmpty(sortType)?"desc":sortType;
////        	HashMap<String, Object[]> searchContentMap = new HashMap<String, Object[]>();
//        	
//        	BoolFilterBuilder query = FilterBuilders.boolFilter();
//        	//搜索内容处理
//        	if(StringUtils.isNotEmpty(keywords)){
//        		BoolFilterBuilder shouldQuery = FilterBuilders.boolFilter();
//        		shouldQuery.should(FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(IndexVo.VO_TITLE,keywords)).cache(true));
//        		shouldQuery.should(FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(IndexVo.VO_CONTENT,keywords)).cache(true));
//        		query.must(shouldQuery);
//        	}
//        	//过滤内容处理
////        	HashMap<String, Object[]> filterContentMap = new HashMap<String, Object[]>();
//        	if (begin_price >= 0 && end_price >=0) {//加过区间过滤
//        		query.must(FilterBuilders.rangeFilter(IndexVo.VO_COST_PRICE).from(begin_price).to(end_price).cache(true));
//        	}
//        	//库存过滤
//        	if(StringUtils.isNotEmpty(goods_inventory) && goods_inventory.equals("0")){//-1显示全部，0仅显示有货
//        		query.must(FilterBuilders.rangeFilter(IndexVo.VO_GOODS_INVENTORY).gt(0).cache(true));
//        	}
//        	//商品类型过滤
//        	if(StringUtils.isNotEmpty(goods_type)&&!goods_type.equals("-1")){//商品类型（-1全部 /0商城自营/1商家商品）
//        		query.must(FilterBuilders.queryFilter(QueryBuilders.matchQuery(IndexVo.VO_GOODS_TYPE,goods_type)).cache(true));
//        	}
//        	//地区过滤
//        	if(StringUtils.isNotEmpty(goods_area)){//地区过滤
//        		query.must(FilterBuilders.queryFilter(QueryBuilders.matchQuery(IndexVo.VO_GOODS_AREA,goods_area)).cache(true));
//        	}
//        	//品牌过滤
//        	if(StringUtils.isNotEmpty(goods_brand)){//品牌过滤
//        		query.must(FilterBuilders.queryFilter(QueryBuilders.matchQuery(IndexVo.VO_GOODS_BRAND,goods_brand)).cache(true));
//        	}
//        	//商品分类
//        	if(StringUtils.isNotEmpty(query_gc)){//商品分类
//        		if(query_gc.contains("_*")){
//        			query.must(FilterBuilders.queryFilter(QueryBuilders.wildcardQuery(IndexVo.VO_GOODS_CLASS,query_gc)).cache(true));
//        		}else{
//            		query.must(FilterBuilders.queryFilter(QueryBuilders.matchQuery(IndexVo.VO_GOODS_CLASS,goods_type)).cache(true));
//        		}
//        	}
//        	
//    	    long count = 30;
//        	int pages = ((int)count+ this.pageSize - 1) / this.pageSize; // 记算总页数
//			int intPageNo = (pageNo > pages ? pages : pageNo);
//			if (intPageNo < 1)intPageNo = 1;
//			
//			SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
//                    .setQuery(QueryBuilders.f).setFrom((intPageNo-1)*pageSize).setSize(pageSize).setExplain(true);
//        	
//			if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
//               
//            } else {
//               SortOrder sortOrder = sortType.equals("desc") ? SortOrder.DESC : SortOrder.ASC;
//                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
//            }
//			
//			searchRequestBuilder = searchRequestBuilder.addHighlightedField(IndexVo.VO_TITLE, 1000)
//                       .setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
//                       .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">");
//			
//			System.out.println(searchRequestBuilder.toString());
//			SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
//			
//			List<Map<String, Object>> list  = this.getSearchResult(searchResponse);
//			//List<Map<String, Object>> list = this.simpleSearch(indexNames, searchContentMap, filterContentMap, (intPageNo-1)*pageSize, pageSize, sortField, sortType);
//        	if(list!=null && list.size()>0){
//        		List<IndexVo> indexList = new ArrayList<IndexVo>();
//            	for(Map<String, Object> obj:list){
//            		IndexVo indexVo = JacksonUtils.json2Obj(JacksonUtils.obj2Json(obj), IndexVo.class);
//            		indexList.add(indexVo);
//            	}  
//            	searcheResult.setCurrentPage(intPageNo);
//            	searcheResult.setPages(pages);
//            	searcheResult.setPageSize(pageSize);
//            	searcheResult.setRows((int)count);
//            	searcheResult.setVo_list(indexList);
//        	}
//		} catch (Exception e) {
//			log.equals(e.getMessage());
//		}
//    	return searcheResult;
//    }
    
   
    
    

    /**
     * 简单检索（简单分页查询）
     * @param indexNames索引名称
     * @param searchContentMap搜索内容
     * @param filterContentMap过滤内容
     * @param from起始点
     * @param offset偏移量
     * @return
     */
    public List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap, int from, int offset) {
        return this.simpleSearch(indexNames, searchContentMap, filterContentMap, from, offset, null, null);
    }
    
    /**
     * 简单检索（新增排序）
     * @param indexNames索引名称
     * @param searchContentMap搜索内容
     * @param filterContentMap过滤内容
     * @param from起始点
     * @param offset偏移量
     * @param sortField排序字段
     * @param sortType排序
     * @return
     */
    public List<Map<String, Object>> simpleSearch(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap, int from, int offset, String sortField, String sortType)
    {
        SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
        return this.simpleSearch(indexNames, searchContentMap, searchLogic, filterContentMap, searchLogic, from, offset, sortField, sortType);
    }
    
    /**
     * 简单搜索
     * @param indexNames
     * @param queryString
     * @param from
     * @param offset
     * @param sortField
     * @param sortType
     * @return
     */
    public List<Map<String, Object>> simpleSearch(String[] indexNames, byte[] queryString, int from, int offset, String sortField, String sortType) {
        if (offset <= 0) {
            return null;
        }
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }
            else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }

            String query = new String(queryString);
            searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
            log.debug(query);

            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return this.getSearchResult(searchResponse);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    /**
     * 复杂检索
     * @param indexNames
     * @param mustSearchContentMap
     * @param shouldSearchContentMap
     * @param from
     * @param offset
     * @param sortField
     * @param sortType
     * @return
     */
    public List<Map<String, Object>> complexSearch(String[] indexNames
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap
            , int from, int offset, @Nullable String sortField, @Nullable String sortType) {
        if (offset <= 0) {
            return null;
        }
        /*创建must搜索条件*/
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            SearchRequestBuilder searchRequestBuilder = null;
            searchRequestBuilder = client.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
                    .setQuery(boolQueryBuilder).setFrom(from).setSize(offset).setExplain(true);
            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
                /*如果不需要排序*/
            }else {
                /*如果需要排序*/
                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
            }
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, mustSearchContentMap);
            searchRequestBuilder = this.createHighlight(searchRequestBuilder, shouldSearchContentMap);
            log.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            return this.getSearchResult(searchResponse);
        }
        catch (Exception e) {
        	e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }
    
    
    /**
     * 分组检索
     * @param indexName
     * @param mustSearchContentMap
     * @param shouldSearchContentMap
     * @param groupFields
     * @return
     */
    public Map<String, String> groupSearch(String indexName
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap
            , String[] groupFields) {
        /*创建must搜索条件*/
        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
        /*创建should搜索条件*/
        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (mustQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
        }
        if (shouldQueryBuilder != null) {
            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
        }
        try {
            return this._group(indexName, boolQueryBuilder, groupFields);
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    
    /**
	 * 批量添加数据
	 * @param indexName
	 * @param insertContentMap
	 * @return
	 */
	 public boolean bulkInsertData(String indexName,String indexType, HashMap<String, Object[]> insertContentMap) {
	        XContentBuilder xContentBuilder = null;
	        try {
	            xContentBuilder = XContentFactory.jsonBuilder().startObject();
	        }
	        catch (IOException e) {
	            log.error(e.getMessage());
	            return false;
	        }
	        Iterator<Entry<String, Object[]>> iterator = insertContentMap.entrySet().iterator();
	        while (iterator.hasNext()) {
	            Entry<String, Object[]> entry = iterator.next();
	            String field = entry.getKey();
	            Object[] values = entry.getValue();
	            String formatValue = this.formatInsertData(values);
	            try {
	                xContentBuilder = xContentBuilder.field(field, formatValue);
	            }
	            catch (IOException e) {
	                log.error(e.getMessage());
	                return false;
	            }
	        }
	        try {
	            xContentBuilder = xContentBuilder.endObject();
	        }
	        catch (IOException e) {
	            log.error(e.getMessage());
	            return false;
	        }
	        try {
	            log.debug("[" + indexName + "]" + xContentBuilder.string());
	        }
	        catch (IOException e) {
	            log.error(e.getMessage());
	        }
	        return this._bulkInsertData(indexName, xContentBuilder);
	    }
	 
	 
	/**
	 * 批量删除索引中数据
	 * @param indexName
	 * @param contentMap
	 * @return
	 */
	 
	 public boolean bulkDeleteData(String indexName, HashMap<String, Object[]> contentMap) {
        try {
            QueryBuilder queryBuilder = null;
            queryBuilder = this.createQueryBuilder(contentMap,SearchLogic.must);
            log.warn("[" + indexName + "]" + queryBuilder.toString());
            client.prepareDeleteByQuery(indexName).setQuery(queryBuilder).execute().actionGet();
            return true;
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
	 
	 
	/**
	 * 批量更新数据
	 * @param indexName
	 * @param oldContentMap
	 * @param newContentMap
	 * @return
	 */
	 public boolean bulkUpdateData(String indexName,String indexType, HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap) {
	        if (this.bulkDeleteData(indexName, oldContentMap)) {
	            return this.bulkInsertData(indexName,indexType, newContentMap);
	        }
	        log.warn("删除数据失败");
	        return false;
	    }
	 
	 
	 /**
		 * 批量更新数据
		 * @param indexName
		 * @param oldContentMap
		 * @param newContentMap
		 * @return
		 */
		public boolean autoBulkUpdateData(String indexName,String indexType, HashMap<String, Object[]> oldContentMap, HashMap<String, Object[]> newContentMap) {
	        try {
	            List<Map<String, Object>> searchResult = this.simpleSearch(new String[] { indexName }, oldContentMap, null, 0, 1, null, null);
	            if (searchResult == null || searchResult.size() == 0) {
	                log.warn("未找到需要更新的数据");
	                return false;
	            }
	            if (!this.bulkDeleteData(indexName, oldContentMap)) {
	                log.warn("删除数据失败");
	                return false;
	            }
	            HashMap<String, Object[]> insertContentMap = new HashMap<String, Object[]>();
	            for (Map<String, Object> contentMap : searchResult) {
	                Iterator<Entry<String, Object>> oldContentIterator = contentMap.entrySet().iterator();
	                while (oldContentIterator.hasNext()) {
	                    Entry<String, Object> entry = oldContentIterator.next();
	                    insertContentMap.put(entry.getKey(), new Object[] { entry.getValue() });
	                }
	            }
	            Iterator<Entry<String, Object[]>> newContentIterator = newContentMap.entrySet().iterator();
	            while (newContentIterator.hasNext()) {
	                Entry<String, Object[]> entry = newContentIterator.next();
	                insertContentMap.put(entry.getKey(), entry.getValue());
	            }
	            return this.bulkInsertData(indexName,indexType, insertContentMap);
	        }
	        catch (Exception e) {
	            log.error(e.getMessage());
	        }
	        return false;
	    }
		
		
		/**
		 * 获取检索结果数
		 * @param indexNames
		 * @param searchContentMap
		 * @param filterContentMap
		 * @return
		 */
		public long getCount(String[] indexNames, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) {
	        SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
	        return this.getCount(indexNames, searchContentMap, searchLogic, filterContentMap, searchLogic);
	    }
		
		/**
	     * 获取检索结果数量
	     * @param indexNames
	     * @param searchContentMap
	     * @param searchLogic
	     * @param filterContentMap
	     * @param filterLogic
	     * @return
	     */
	    public long getCount(String[] indexNames
	            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
	            , @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchLogic filterLogic)
	    {
	        QueryBuilder queryBuilder = null;
	        try {
	            queryBuilder = this.createQueryBuilder(searchContentMap, searchLogic);
	            queryBuilder = this.createFilterBuilder(searchLogic, queryBuilder, searchContentMap, filterContentMap);
	            SearchResponse searchResponse = this.searchCountRequest(indexNames, queryBuilder);
	            return searchResponse.getHits().totalHits();
	        }
	        catch (Exception e) {
	            log.error(e.getMessage());
	        }
	        return 0;
	    }
	    
	    
	    public long getCount(IndexName indexName,QueryBuilder query)
	    {
	        try {
	            SearchResponse searchResponse = this.searchCountRequest(indexNames, query);
	            return searchResponse.getHits().totalHits();
	        }
	        catch (Exception e) {
	            log.error(e.getMessage());
	        }
	        return 0;
	    }
	    
	    
	 
	    
	    
	    /**
	     * 获取检索结果数量
	     * @param indexNames
	     * @param queryString
	     * @return
	     */
	    public long getCount(String[] indexNames, byte[] queryString) {
	        try {
	            SearchResponse searchResponse = this.searchCountRequest(indexNames, queryString);
	            return searchResponse.getHits().totalHits();
	        }
	        catch (Exception e) {
	            log.error(e.getMessage());
	        }
	        return 0;
	    }
	    
	    
	    /**
	     * 获取复杂检索结果
	     * @param indexNames
	     * @param mustSearchContentMap
	     * @param shouldSearchContentMap
	     * @return
	     */
	    public long getComplexCount(String[] indexNames
	            , HashMap<String, Object[]> mustSearchContentMap
	            , HashMap<String, Object[]> shouldSearchContentMap) {
	        /*创建must搜索条件*/
	        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
	        /*创建should搜索条件*/
	        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
	        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
	            return 0;
	        }
	        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
	        if (mustQueryBuilder != null) {
	            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
	        }
	        if (shouldQueryBuilder != null) {
	            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
	        }
	        try {
	            SearchResponse searchResponse = this.searchCountRequest(indexNames, boolQueryBuilder);
	            return searchResponse.getHits().totalHits();
	        }
	        catch (Exception e) {
	            log.error(e.getMessage());
	        }
	        return 0;
	    }
	 
	    
	    /**
	     * 
	     * @param indexNames 索引名称
	     * @param searchContentMap 搜索内容
	     * @param searchLogic 搜索逻辑（跨索引使用）
	     * @param filterContentMap 过滤内容
	     * @param filterLogic 过滤逻辑（跨索引使用现在设置为searchLogic的值） 
	     * @param from 索引起始位（分页使用）
	     * @param offset 索引偏移量（分也是用）
	     * @param sortField 排序字段
	     * @param sortType	排序类型
	     * @return
	     */
		private List<Map<String, Object>> simpleSearch(String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic
            , int from, int offset, String sortField, String sortType){
	        if (offset <= 0) {
	            return null;
	        }
	        try {
	            QueryBuilder queryBuilder = null;
	            queryBuilder = this.createQueryBuilder(searchContentMap, searchLogic);
	            queryBuilder = this.createFilterBuilder(filterLogic, queryBuilder, searchContentMap, filterContentMap);
	            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexNames).setSearchType(SearchType.DEFAULT)
	                    .setQuery(queryBuilder).setFrom(from).setSize(offset).setExplain(true);
	            if (sortField == null || sortField.isEmpty() || sortType == null || sortType.isEmpty()) {
	                /*如果不需要排序*/
	            }
	            else {
	                /*如果需要排序*/
	                org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? org.elasticsearch.search.sort.SortOrder.DESC : org.elasticsearch.search.sort.SortOrder.ASC;
	                searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
	            }
	            searchRequestBuilder = this.createHighlight(searchRequestBuilder, searchContentMap);
	            log.debug(searchRequestBuilder.toString());
	            System.out.println(searchRequestBuilder.toString());
	            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
	            return this.getSearchResult(searchResponse);
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	            log.error(e.getMessage());
	        }
	        return null;
    }
    
   
	
	/**
	 * 批量向指定索引添加数据
	 * @param indexName
	 * @param xContentBuilder
	 * @return
	 */
	private boolean _bulkInsertData(String indexName, XContentBuilder xContentBuilder) {
	        try {
	            BulkRequestBuilder bulkRequest = client.prepareBulk();
	            bulkRequest.add(client.prepareIndex(indexName,indexName).setSource(xContentBuilder));
	            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
	            if (!bulkResponse.hasFailures()) {
	                return true;
	            }
	            else {
	                log.error(bulkResponse.buildFailureMessage());
	            }
	        }
	        catch (Exception e) {
	            log.error(e.getMessage());
	        }
	        return false;
	    }
	

	
	
	
	   /*简单的值校验*/
    private boolean checkValue(Object[] values) {
        if (values == null) {
            return false;
        }
        else if (values.length == 0) {
            return false;
        }
        else if (values[0] == null) {
            return false;
        }
        else if (values[0].toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }


    /**
     * range查询创建
     * @param field
     * @param values
     * @return
     */
    private RangeQueryBuilder createRangeQueryBuilder(String field, Object[] values) {
    	String begin="",end="";
    	try {    		
    		if (values.length == 1 || values[1] == null || values[1].toString().trim().isEmpty()) {
                log.warn("[区间搜索]必须传递两个值，但是只传递了一个值，所以返回null");
                return null;
            }
    		if(values[1].equals(RangeType.gt)){
    			return QueryBuilders.rangeQuery(field).gt(Integer.valueOf(values[0].toString()));
    		}
    		if(values[1].equals(RangeType.gte)){
    			return QueryBuilders.rangeQuery(field).gte(Integer.valueOf(values[0].toString()));
    		}
    		if(values[1].equals(RangeType.lt)){
    			return QueryBuilders.rangeQuery(field).lt(Integer.valueOf(values[0].toString()));
    		}
    		if(values[1].equals(RangeType.lte)){
    			return QueryBuilders.rangeQuery(field).lte(Integer.valueOf(values[0].toString()));
    		}
            begin = DateUtil.isDate(values[0])?DateUtil.formatDate(values[0]):values[0].toString();
            end = DateUtil.isDate(values[1])?DateUtil.formatDate(values[1]): values[1].toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return QueryBuilders.rangeQuery(field).from(begin).to(end);
    }


    /**
     * 创建过滤条件
     * @param searchLogic
     * @param queryBuilder
     * @param searchContentMap
     * @param filterContentMap
     * @return
     * @throws Exception
     */
//    private QueryBuilder createFilterBuilder(SearchLogic searchLogic, QueryBuilder queryBuilder, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) throws Exception
//    {
//        try {
//            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
//            AndFilterBuilder andFilterBuilder = null;
//            while (iterator.hasNext()) {
//                Entry<String, Object[]> entry = iterator.next();
//                Object[] values = entry.getValue();
//                /*排除非法的搜索值*/
//                if (!this.checkValue(values)) {
//                    continue;
//                }
//                SearchOption mySearchOption = this.getSearchOption(values);
//                if (mySearchOption.getDataFilter() == DataFilter.exists) {
//                    /*被搜索的条件必须有值*/
//                    ExistsFilterBuilder existsFilterBuilder = FilterBuilders.existsFilter(entry.getKey());
//                    if (andFilterBuilder == null) {
//                        andFilterBuilder = FilterBuilders.andFilter(existsFilterBuilder);
//                    }
//                    else {
//                        andFilterBuilder = andFilterBuilder.add(existsFilterBuilder);
//                    }
//                }
//            }
//            if (filterContentMap == null || filterContentMap.isEmpty()) {
//                /*如果没有其它过滤条件，返回*/
//                return QueryBuilders.filteredQuery(queryBuilder, andFilterBuilder);
//            }
//            /*构造过滤条件*/
//            QueryFilterBuilder queryFilterBuilder = FilterBuilders.queryFilter(this.createQueryBuilder(filterContentMap, searchLogic));
//            /*构造not过滤条件，表示搜索结果不包含这些内容，而不是不过滤*/
//            NotFilterBuilder notFilterBuilder = FilterBuilders.notFilter(queryFilterBuilder);
//            return QueryBuilders.filteredQuery(queryBuilder, FilterBuilders.andFilter(andFilterBuilder, notFilterBuilder));
//        }
//        catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }
    
    
    /*
     * 创建过滤条件
     * */
    private QueryBuilder createFilterBuilder(SearchLogic searchLogic, QueryBuilder queryBuilder, HashMap<String, Object[]> searchContentMap, HashMap<String, Object[]> filterContentMap) throws Exception
    {
        try {
            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
            AndFilterBuilder andFilterBuilder = null;
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                Object[] values = entry.getValue();
                /*排除非法的搜索值*/
                if (!this.checkValue(values)) {
                    continue;
                }
                SearchOption mySearchOption = this.getSearchOption(values);
                if (mySearchOption.getDataFilter() == DataFilter.exists) {
                    /*被搜索的条件必须有值*/
                    ExistsFilterBuilder existsFilterBuilder = FilterBuilders.existsFilter(entry.getKey());
                    if (andFilterBuilder == null) {
                        andFilterBuilder = FilterBuilders.andFilter(existsFilterBuilder);
                    }
                    else {
                        andFilterBuilder = andFilterBuilder.add(existsFilterBuilder);
                    }
                }
            }
            if (filterContentMap == null || filterContentMap.isEmpty()) {
                /*如果没有其它过滤条件，返回*/
                return QueryBuilders.filteredQuery(queryBuilder, andFilterBuilder);
            }
            /*构造过滤条件*/
            QueryFilterBuilder queryFilterBuilder = FilterBuilders.queryFilter(this.createQueryBuilder(filterContentMap, searchLogic));
            /*构造not过滤条件，表示搜索结果不包含这些内容，而不是不过滤*/
            //NotFilterBuilder notFilterBuilder = FilterBuilders.notFilter(queryFilterBuilder);
            return QueryBuilders.filteredQuery(queryBuilder, queryFilterBuilder);
        }
        catch (Exception e) {
        	e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }
    
    
    /**
     * 单字段查询构建
     * @param field
     * @param values
     * @param searchOption
     * @return
     */
    @SuppressWarnings("unused")
//	private QueryBuilder createSingleFieldQueryBuilder(String field, Object[] values, SearchOption searchOption) {
//        try {
//            if (searchOption.getSearchType() == SearchOption.MySearchType.range) {//区间搜索
//                return this.createRangeQueryBuilder(field, values);
//            }
//            //String[] fieldArray = field.split(",");/*暂时不处理多字段[field1,field2,......]搜索情况*/
//            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//            for (Object valueItem : values) {
//                if (valueItem instanceof SearchOption) {
//                    continue;
//                }
//                QueryBuilder queryBuilder = null;
//                String formatValue = valueItem.toString().trim().replace("*", "");//格式化搜索数据
//                if (searchOption.getSearchType() == SearchOption.MySearchType.term) {
//                    queryBuilder = QueryBuilders.termQuery(field, formatValue).boost(searchOption.getBoost());
//                }else if (searchOption.getSearchType() == SearchOption.MySearchType.querystring) {
//                    if (formatValue.length() == 1) {
//                        /*如果搜索长度为1的非数字的字符串，格式化为通配符搜索，暂时这样，以后有时间改成multifield搜索，就不需要通配符了*/
//                        if (!Pattern.matches("[0-9]", formatValue)) {
//                            formatValue = "*"+formatValue+"*";
//                        }
//                    }
//                    QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryString(formatValue).minimumShouldMatch(searchOption.getQueryStringPrecision());
//                    queryBuilder = queryStringQueryBuilder.field(field).boost(searchOption.getBoost());
//                }
//                if (searchOption.getSearchLogic() == SearchLogic.should) {
//                    boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
//                }
//                else {
//                    boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
//                }
//            }
//            return boolQueryBuilder;
//        }
//        catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }
    
    
    
    /**
     * 
     * @param field
     * @param values
     * @param mySearchOption
     * @return
     */
    private QueryBuilder createSingleFieldQueryBuilder(String field, Object[] values, SearchOption mySearchOption) {
        try {
            if (mySearchOption.getSearchType() == SearchOption.MySearchType.range) {
                /*区间搜索*/
                return this.createRangeQueryBuilder(field, values);
            }
            if(mySearchOption.getSearchType() == SearchOption.MySearchType.wildcard){
            	 return this.createWildcardQueryBuilder(field, values);
            }
            //        String[] fieldArray = field.split(",");/*暂时不处理多字段[field1,field2,......]搜索情况*/
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (Object valueItem : values) {
                if (valueItem instanceof SearchOption) {
                    continue;
                }
                QueryBuilder queryBuilder = null;
                String formatValue = valueItem.toString().trim().replace("*", "");//格式化搜索数据
                if (mySearchOption.getSearchType() == SearchOption.MySearchType.term) {
                    queryBuilder = QueryBuilders.termQuery(field, formatValue).boost(mySearchOption.getBoost());
                }
                else if (mySearchOption.getSearchType() == SearchOption.MySearchType.querystring) {
                    if (formatValue.length() == 1) {
                        /*如果搜索长度为1的非数字的字符串，格式化为通配符搜索，暂时这样，以后有时间改成multifield搜索，就不需要通配符了*/
                        if (!Pattern.matches("[0-9]", formatValue)) {
                            formatValue = "*"+formatValue+"*";
                        }
                    }
                    QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryString(formatValue).minimumShouldMatch(mySearchOption.getQueryStringPrecision());
                    queryBuilder = queryStringQueryBuilder.field(field).boost(mySearchOption.getBoost());
                }
                if (mySearchOption.getSearchLogic() == SearchLogic.should) {
                    boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                }
                else {
                    boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                }
            }
            return boolQueryBuilder;
        }
        catch (Exception e) {
        	e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

 
    /**
     * 通配符查询
     * @param field
     * @param values
     * @return
     */
    private QueryBuilder createWildcardQueryBuilder(String field,Object[] values) {
		if(values == null || values[0]==null){
			log.warn("统配符匹配，匹配模式值为空");
			return null;
		}
		return QueryBuilders.wildcardQuery(field,values[0].toString());
	}


	/**
     *  创建搜索条件
     * @param searchContentMap
     * @param searchLogic
     * @return
     */
//    private QueryBuilder createQueryBuilder(HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic) {
//        try {
//            if (searchContentMap == null || searchContentMap.size() ==0) {
//                return null;
//            }
//            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
//            /*循环每一个需要搜索的字段和值*/
//            while (iterator.hasNext()) {
//                Entry<String, Object[]> entry = iterator.next();
//                String field = entry.getKey();
//                Object[] values = entry.getValue();
//                /*排除非法的搜索值*/
//                if (!this.checkValue(values)) {
//                    continue;
//                }
//                /*获得搜索类型*/
//                SearchOption mySearchOption = this.getSearchOption(values);
//                QueryBuilder queryBuilder = this.createSingleFieldQueryBuilder(field, values, mySearchOption);
//                if (queryBuilder != null) {
//                    if (searchLogic == SearchLogic.should) {
//                        /*should关系，也就是说，在A索引里有或者在B索引里有都可以*/
//                        boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
//                    }
//                    else {
//                        /*must关系，也就是说，在A索引里有，在B索引里也必须有*/
//                        boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
//                    }
//                }
//            }
//            return boolQueryBuilder;
//        }
//        catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }
    
    
    private QueryBuilder createQueryBuilder(HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic) {
        try {
            if (searchContentMap == null || searchContentMap.size() ==0) {
                return null;
            }
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
            /*循环每一个需要搜索的字段和值*/
            while (iterator.hasNext()) {
                Entry<String, Object[]> entry = iterator.next();
                String field = entry.getKey();
                Object[] values = entry.getValue();
                /*排除非法的搜索值*/
                if (!this.checkValue(values)) {
                    continue;
                }
                /*获得搜索类型*/
                SearchOption mySearchOption = this.getSearchOption(values);
                QueryBuilder queryBuilder = this.createSingleFieldQueryBuilder(field, values, mySearchOption);
                if (queryBuilder != null) {
                    if (searchLogic == SearchLogic.should) {
                        /*should关系，也就是说，在A索引里有或者在B索引里有都可以*/
                        boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
                    }
                    else {
                        /*must关系，也就是说，在A索引里有，在B索引里也必须有*/
                        boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
                    }
                }
            }
            return boolQueryBuilder;
        }
        catch (Exception e) {
        	e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

  

    private String formatInsertData(Object[] values) {
        if (!this.checkValue(values)) {
            return "";
        }
        if (DateUtil.isDate(values[0])) {
            log.warn("[" + values[0].toString() + "] formatDate");
            return DateUtil.formatDate(values[0]);
        }
        String formatValue = values[0].toString();
        for (int index = 1; index < values.length; ++index) {
            formatValue += "," + values[index].toString();
        }
        return formatValue.trim();
    }

    
    
    

    
    
    private SearchResponse searchCountRequest(String[] indexNames, Object queryBuilder) {
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexNames).setSearchType(SearchType.COUNT);
            if (queryBuilder instanceof QueryBuilder) {
                searchRequestBuilder = searchRequestBuilder.setQuery((QueryBuilder)queryBuilder);
                log.debug(searchRequestBuilder.toString());
            }
            if (queryBuilder instanceof byte[]) {
                String query = new String((byte[])queryBuilder);
                searchRequestBuilder = searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
                log.debug(query);
            }
            return searchRequestBuilder.execute().actionGet();
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    
    

    /**
     * 获取搜索结果
     * @param searchResponse
     * @return
     */
    private List<Map<String, Object>> getSearchResult(SearchResponse searchResponse) {
        try {
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (SearchHit searchHit : searchResponse.getHits()) {
                Iterator<Entry<String, Object>> iterator = searchHit.getSource().entrySet().iterator();
                HashMap<String, Object> resultMap = new HashMap<String, Object>();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    resultMap.put(entry.getKey(), entry.getValue());
                }
                Map<String, HighlightField> highlightMap = searchHit.highlightFields();
                Iterator<Entry<String, HighlightField>> highlightIterator = highlightMap.entrySet().iterator();
                while (highlightIterator.hasNext()) {
                    Entry<String, HighlightField> entry = highlightIterator.next();
                    Object[] contents = entry.getValue().fragments();
                    if (contents.length == 1) {
                        resultMap.put(entry.getKey(), contents[0].toString());
                        System.out.println(contents[0].toString());
                    }
                    else {
                        log.warn("搜索结果中的高亮结果出现多数据contents.length = " + contents.length);
                    }
                }
                resultList.add(resultMap);
            }
            return resultList;
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    
    /*获得搜索选项*/
    private SearchOption getSearchOption(Object[] values) {
        try {
            for (Object item : values) {
                if (item instanceof SearchOption) {
                    return (SearchOption) item;
                }
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return new SearchOption();
    }

    /*获得搜索建议
     * 服务器端安装elasticsearch-plugin-suggest
     * 客户端加入elasticsearch-plugin-suggest的jar包
     * https://github.com/spinscale/elasticsearch-suggest-plugin
     * */
//	    public List<String> getSuggest(String[] indexNames, String fieldName, String value, int count) {
//        try {
//            SuggestRequestBuilder suggestRequestBuilder = new SuggestRequestBuilder(this.searchClient);
//            suggestRequestBuilder = suggestRequestBuilder.setIndices(indexNames).field(fieldName).term(value).size(count);//.similarity(0.5f);
//            SuggestResponse suggestResponse = suggestRequestBuilder.execute().actionGet();
//            return suggestResponse.suggestions();
//        }
//        catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }

    
    
    
    private Map<String, String> _group(String indexName, QueryBuilder queryBuilder, String[] groupFields) {
        try {
            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("group").fields(groupFields).size(9999);
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setSearchType(SearchType.DEFAULT)
                    .addFacet(termsFacetBuilder).setQuery(queryBuilder).setFrom(0).setSize(1).setExplain(true);
            log.debug(searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            TermsFacet termsFacet = searchResponse.getFacets().facet("group");
            HashMap<String, String> result = new HashMap<String, String>();
            for (org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : termsFacet.getEntries()) {
                result.put(entry.getTerm().toString(), entry.getCount() + "");
            }
            return result;
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

   
    
    /**
     * 高亮显示检索结果
     * @param searchRequestBuilder
     * @param searchContentMap
     * @return
     */
    private SearchRequestBuilder createHighlight(SearchRequestBuilder searchRequestBuilder, HashMap<String, Object[]> searchContentMap) {
        Iterator<Entry<String, Object[]>> iterator = searchContentMap.entrySet().iterator();
        /*循环每一个需要搜索的字段和值*/
        while (iterator.hasNext()) {
            Entry<String, Object[]> entry = iterator.next();
            String field = entry.getKey();
            Object[] values = entry.getValue();
            /*排除非法的搜索值*/
            if (!this.checkValue(values)) {
                continue;
            }
            /*获得搜索类型*/
            SearchOption searchOption = this.getSearchOption(values);
            if (searchOption.isHighlight()) {
                /*
                 * http://www.elasticsearch.org/guide/reference/api/search/highlighting.html
                 *
                 * fragment_size设置成1000，默认值会造成返回的数据被截断
                 * */
                searchRequestBuilder = searchRequestBuilder.addHighlightedField(field, 1000)
                        .setHighlighterPreTags("<"+this.highlightCSS.split(",")[0]+">")
                        .setHighlighterPostTags("</"+this.highlightCSS.split(",")[1]+">");
            }
        }
        return searchRequestBuilder;
    }
    
    
    @SuppressWarnings("unchecked")
	public List<String> LoadData_goods_class(String keywords) {
    	SearchResult result = search(keywords, 0, -1, -1, null, null, "", "", "", "", "", "");
    	List<IndexVo> pList = result.getVo_list();
    	List<String> list = new ArrayList<String>();
		if(pList!=null && pList.size()>0){
			for(IndexVo vo :pList){
				String gc = vo.getVo_goods_class();
				if (null != gc && gc.contains("_")) {
					list.add(gc);
				}
			}
			HashSet tem = new HashSet(list);
			list.clear();
			list.addAll(tem);
			Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				String str1[] = CommUtil.null2String(o1).split("_");
				String str2[] = CommUtil.null2String(o2).split("_");
				if (CommUtil.null2Int(str1[0]) > CommUtil.null2Int(str2[0])) {
					return 1;
				}
				if (CommUtil.null2Int(str1[0]) == CommUtil
						.null2Int(str2[0])) {
					if (CommUtil.null2Int(str1[1]) > CommUtil
							.null2Int(str2[1])) {
						return 1;
					}
				}
				return -1;
			}
		});
		}
		return list;
	}
    
    
    public static void main(String[] args){

    	List<String> list = new ArrayList<String>();
    	list.add("aaa");
    	list.add("bbb");
    	list.add("aaa");
    	
    	Set set = new HashSet(list);
    	System.out.println(set);
    	
    }
    
	
	
	
	
}
