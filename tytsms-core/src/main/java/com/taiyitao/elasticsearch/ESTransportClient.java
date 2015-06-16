package com.taiyitao.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.iskyshop.pay.wechatpay.util.ConfigContants;

public class ESTransportClient {
	private static Logger logger = LoggerFactory.getInstance(ESTransportClient.class,Level.INFO);
	private static Client client;
	
	
	/**
	* 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引 ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	* @param indices 索引名称；
	* @param mappingType 索引类型
	* @throws Exception
	*/
	public static void createMapping(String indices,String mappingType) throws IOException{
		XContentBuilder builder=XContentFactory.jsonBuilder()
				.startObject()
				.startObject(indices)
				.startObject("properties")
				.startObject(IndexVo.VO_MAIN_PHOTO_URL).field("type", "string").endObject()
				.startObject(IndexVo.VO_GOODS_COLLECT).field("type", "double").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_GOODS_EVAS).field("type", "long").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_STORE_USERNAME).field("type", "string").field("store", "yes").field("index","analyzied").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_CONTENT).field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_GOODS_CLASS).field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_GOODS_INVENTORY).field("type", "long").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_COST_PRICE).field("type", "double").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_GOODS_SALENUM).field("type", "long").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_TITLE).field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_TYPE).field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_WELL_EVALUATE).field("type", "double").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_PHOTOS_URL).field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_ADD_TIME).field("type", "long").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_GOODS_TYPE).field("type", "long").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.startObject(IndexVo.VO_GOODS_BRAND).field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
				.endObject()
				.endObject()
				.endObject();
				PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
				client.admin().indices().putMapping(mapping).actionGet();
				client.close();
	}
	
	
	
	/**
	* 创建索引名称
	* @param indices 索引名称
	*/
	public static void createPrepareIndex(String indices){
		client.admin().indices().prepareCreate(indices).execute().actionGet();
		client.close();
	}
	
	
	

	/**
	 * 用TransportClient方式与es集群通信的话只要开启了client.transport.sniff功能就可以了
	 * 或者指定集群的名称也行，两者选其一就可以了 但是setting设置的其他参数却没有起效果
	 *
	 */
	public static Client getEsClient() {
		if (null == client) {
			
			String eshost = ConfigContants.ELASTICSEARCH_TRANSPORT_HOST;
			eshost = StringUtils.isEmpty(eshost)?"127.0.0.1":eshost;
			String esport =ConfigContants.ELASTICSEARCH_TRANSPORT_PORT;
			esport  = StringUtils.isEmpty(esport)?"9300":esport;
			String clusterName = ConfigContants.ELASTICSEARCH_CLUSTER_NAME;
			String cluster = ConfigContants.ELASTICSEARCH_CLUSTER_LIST;
			
//			String eshost = PropertyUtil.getProperty("ELASTICSEARCH_TRANSPORT_HOST");
//			eshost = StringUtils.isEmpty(eshost)?"127.0.0.1":eshost;
//			String esport =PropertyUtil.getProperty("ELASTICSEARCH_TRANSPORT_PORT");
//			esport  = StringUtils.isEmpty(esport)?"9300":esport;
//			String clusterName =PropertyUtil.getProperty("ELASTICSEARCH_CLUSTER_NAME");
//			String cluster = PropertyUtil.getProperty("ELASTICSEARCH_CLUSTER_LIST");
//			
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name",clusterName.trim()).build();
			//首先使用集群配置信息，如果没有配置则使用集群嗅探功能
			
			if(StringUtils.isNotEmpty(cluster)){
				@SuppressWarnings("resource")
				TransportClient tem =  new TransportClient(settings);
				List<String> clusterList = Arrays.asList(cluster.split(","));
				for (String item : clusterList) {
	                String address = item.split(":")[0];
	                int port = Integer.parseInt(item.split(":")[1]);
	                /*通过tcp连接搜索服务器，如果连接不上，有一种可能是服务器端与客户端的jar包版本不匹配*/
	                client= tem.addTransportAddress(new InetSocketTransportAddress(address,port));
	            }
			}else{
				if(StringUtils.isEmpty(clusterName)){
					logger.error("elasticsearch.cluster.name is empty in config");
				}
				settings = ImmutableSettings.settingsBuilder()
							.put("client.transport.sniff", true) // 开启嗅探es集群状态,把集群中所有节点ip地址添加到客户端中去,并且会自动添加新加入到集群中的es节点
							// .put("number_of_shards",3) 
							// .put("number_of_replicas",0)
							// .put("client.transport.ping_timeout","5");//超时时间默认5s
							.put("cluster.name", clusterName).build();
				client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(eshost.trim(),Integer.valueOf(esport)));
			}
		}
		return client;
	}

	
	public void shutdown() {
		client.close();
		client = null;
	}
}
