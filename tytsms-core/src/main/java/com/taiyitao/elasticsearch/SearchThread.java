package com.taiyitao.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.taiyitao.elasticsearch.IndexVo.IndexName;
import com.taiyitao.elasticsearch.IndexVo.IndexType;

/**
 * 
* <p>Title: LuceneThread.java</p>

* <p>Description: lucene搜索工具类，该类使用线程处理索引的建立，默认每天凌晨更新一次商城索引文件</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class SearchThread implements Runnable {
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;
	
	
	private IndexName indexName;
	private IndexType indexType;
	private List<IndexVo> vo_list = new ArrayList<IndexVo>();

	public SearchThread(IndexName indexName,IndexType indexType, List<IndexVo> vo_list) {
		super();
		this.indexName = indexName;
		this.indexType = indexType;
		this.vo_list = vo_list;
	}

	@Override
	public void run() {
		try {
			System.out.println("################3"+indexName.toString());
			boolean flag = elasticsearchUtil.indexExists(indexName.toString());
			System.out.println(flag);
			if(elasticsearchUtil.indexExists(indexName.toString())){
				elasticsearchUtil.indexAllDelete(indexName.toString());
			}
			elasticsearchUtil.index(indexName, indexType, vo_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
