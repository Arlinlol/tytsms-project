package com.taiyitao.elasticsearch;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.iskyshop.pay.wechatpay.util.ConfigContants;

public class ESNodeClient {
	 private Logger logger = LoggerFactory.getLogger(ESNodeClient.class);
	 private Node node;

	/**
	 * 指定es的集群名称,当启动该节点时,会自动加入指定的es集群
	 * NodeBuilder自带了client、data、local、clusterName方法
	 * ，不过NodeBuilder自带的方法有限 推荐使用ImmutableSettings.settingsBuilder(Map
	 * map)或 NodeBuilder.nodeBuilder().loadConfigSettings(true)
	 * 加载yml配置文件方式来实现 需求丰富的配置选项
	 * 注意：本地jvm级别的节点,不加入es集群 node = new NodeBuilder().local(true).node();
	 */
	 public  Client getEsClient() {
		if (null == node) {
			
			// setting设置的值居然都不起作用
			final Settings settings = ImmutableSettings.settingsBuilder()
			// .put("index.number_of_shards",2)
			// .put("index.number_of_replicas",0)
			// .put("network.bind_host", "localhost")
			// .put("network.publish_host", "localhost")
			// .put("node.name", "test-node")
			.build();
			
			String clusterName =  ConfigContants.ELASTICSEARCH_CLUSTER_NAME;
			if(StringUtils.isEmpty(clusterName)){
				logger.error("elasticsearch.cluster.name is empty in config");
			}
			node = new NodeBuilder().settings(settings).clusterName(clusterName).client(true) // node.client设置为true表示该节点仅仅作为一个客户端但不保存数据
					.node();
		}
		return node.client();
	}

	
	
	public void shutdown() {
		getEsClient().close();
		node.close();
		node = null;
	}

	public static void main(String[] args) {
		ESNodeClient esNodeClient = new ESNodeClient();
		Client client = esNodeClient.getEsClient();
		System.out.println(client == null);
		// client.prepareIndex("nodet","tp")
		// .setSource("{\"a\":10,\"b\":\"Test\"}")
		// .execute().actionGet();
	}
}
