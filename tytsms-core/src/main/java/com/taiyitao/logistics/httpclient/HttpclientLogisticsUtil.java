package com.taiyitao.logistics.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.iskyshop.foundation.domain.virtual.TransContent;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.pay.wechatpay.util.ConfigContants;

/**
 * 
 * 功能描述： 物流接口工具类
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author cty 新增日期：2015年3月17日
 * @author cty 修改日期：2015年3月17日
 *
 */
public class HttpclientLogisticsUtil {

	private static Log log = LogFactory.getLog(HttpclientLogisticsUtil.class);
	private static final String SEARCH_LOGISTICS_INTERFACE_INFO ="/expressInfoForSearch";
	private static final String PUSH_LOGISTICS_INTERFACE_INFO ="/confirmDeliveryServlet";
	private static final String UPDATE_LOGISTICS_INTERFACE_INFO ="/updateExpressInfoServlet";

	public static final String MESSAGE_0="0";//推送数据失败
	public static final String MESSAGE_200="200";//订阅成功
	public static final String MESSAGE_500="500";//服务器错误
	public static final String MESSAGE_501="501";//重复订阅
	public static final String MESSAGE_600="600";//您不是合法的订阅者（即授权Key出错）
	public static final String MESSAGE_700="700";//订阅方的订阅数据存在错误（如不支持的快递公司、单号为空、单号超长等）
	public static final String MESSAGE_701="701";//拒绝订阅的快递公司

	/**
	 * 查询物流信息
	 * @param json
	 * @return
	 */
	public TransInfo searchLogisticsInfo(String json){
		TransInfo transinfo = new TransInfo();
		try {
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter("charset", "UTF-8");
		    HttpConnectionParams.setConnectionTimeout(httpParams,2000);  //毫秒
		    HttpConnectionParams.setSoTimeout(httpParams,30000);    
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

			HttpPost httpPost = new HttpPost(ConfigContants.LOGISTICS_INTERFACE_URL+SEARCH_LOGISTICS_INTERFACE_INFO);
			httpPost.setEntity(new StringEntity(json.toString(),"UTF-8"));
			HttpResponse responses;
			responses = (HttpResponse) httpClient.execute(httpPost);
			//检验状态码，如果成功接收数据
			int code = responses.getStatusLine().getStatusCode();
			List<TransContent> conList = new ArrayList<TransContent>();
		    if(code ==200){
		    	HttpEntity entity = responses.getEntity();
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
	            String lines;
	            StringBuffer sb = new StringBuffer("");
	            while ((lines = reader.readLine()) != null) {
	                lines = new String(lines.getBytes(), "utf-8");
	                sb.append(lines);
	            }
	            JSONArray jsonarray = new JSONArray().fromObject(sb.toString());
	            log.info("返回结果========"+jsonarray);
	            if(jsonarray.size() > 0){
		            for (int i = 0; i < jsonarray.size(); i++) {
		            	TransContent tcon = new TransContent();
						JSONObject jot = jsonarray.getJSONObject(i);
						tcon.setTime(jot.getString("time"));
						tcon.setContext(jot.getString("context"));
						conList.add(tcon);
					}
	            }
	            reader.close();
		    }
		    transinfo.setData(conList);
		} catch (IOException e) {
			log.info(e);
		} catch (Exception e) {
			log.info(e);
		}
		return transinfo;
	}
	
	/**
	 * 数据推送到TPI中
	 */
	public boolean pushLogisticsInfo(String json){
		try{
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter("charset", "UTF-8");
		    HttpConnectionParams.setConnectionTimeout(httpParams,2000);  //毫秒
		    HttpConnectionParams.setSoTimeout(httpParams,30000);    
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

			HttpPost httpPost = new HttpPost(ConfigContants.LOGISTICS_INTERFACE_URL+PUSH_LOGISTICS_INTERFACE_INFO);
			httpPost.setEntity(new StringEntity(json.toString(),"UTF-8"));
			HttpResponse responses;
			responses = (HttpResponse) httpClient.execute(httpPost);
			//检验状态码，如果成功接收数据
			int code = responses.getStatusLine().getStatusCode();
			if(code == 200){
				HttpEntity entity = responses.getEntity();
            	System.out.println("entity.getContent()接受数据============="+entity.getContent().read());
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
	            String status;
	            StringBuffer sb = new StringBuffer("");
	            while ((status = reader.readLine()) != null) {
	            	status = new String(status.getBytes(), "utf-8");
	            	sb.append(status);
	            }
	            log.info("推送状态=============="+sb.toString());
	            if(MESSAGE_200.equals(sb.toString())){
	            	return true;
	            }
			}
		} catch (Exception e) {
			log.info(e);
		}
        return false;
	}
	
	/**
	 * 修改物流信息
	 */
	public boolean UpdateLogisticsInfo(String json){
		try{
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter("charset", "UTF-8");
		    HttpConnectionParams.setConnectionTimeout(httpParams,2000);  //毫秒
		    HttpConnectionParams.setSoTimeout(httpParams,30000);    
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

			HttpPost httpPost = new HttpPost(ConfigContants.LOGISTICS_INTERFACE_URL+UPDATE_LOGISTICS_INTERFACE_INFO);
			httpPost.setEntity(new StringEntity(json.toString(),"UTF-8"));
			HttpResponse responses;
			responses = (HttpResponse) httpClient.execute(httpPost);
			//检验状态码，如果成功接收数据
			int code = responses.getStatusLine().getStatusCode();
			if(code == 200){
				HttpEntity entity = responses.getEntity();
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
	            String stauts;
	            StringBuffer sb = new StringBuffer("");
	            while ((stauts = reader.readLine()) != null) {
	            	stauts = new String(stauts.getBytes(), "utf-8");
	            	sb.append(stauts);
	            }
	            log.info("推送状态============"+sb.toString());
	            if(MESSAGE_200.equals(sb.toString())){
	            	return true;
	            }
			}
		} catch (Exception e) {
			log.info(e);
		}
		return false;
	}
}
