package com.iskyshop.pay.wechatpay.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.iskyshop.foundation.domain.Payment;

public class WeiXinSignAndPackage {
	
	/**
	 * 创建支付包Package
	 * @param treeMap
	 * @return
	 */
	public static String createPackage(TreeMap<String,String> treeMap,Payment payment){
		String string1=originalString(treeMap);
		String stringSignTemp = string1 + "key="+payment.getPartner_key();
		String sign=MD5Util.MD5Encode(stringSignTemp, "UTF-8").toUpperCase();		
		return sign;
	}
	/**
	 * 创建支付签名paysign
	 * @param wxpackage
	 * @return
	 */
	public static String createPaySign(TreeMap<String,String> param,Payment payment){
		String string1=originalString(param);
		String stringSignTemp = string1 + "key="+payment.getPartner_key();
		System.out.println("签名调试输出："+stringSignTemp);
		String paysign=MD5Util.MD5Encode(stringSignTemp, "UTF-8").toUpperCase();
		return paysign;
	}
	
	
	public static String createSignature(TreeMap<String,String> param) throws Exception{
		String sign = Sha1Util.createSHA1Sign(param);
		System.out.println("sign="+sign);
		return sign;
	}
	
	
	
	/**
	 * wxpackage组装原始串
	 * @param treeMap
	 * @return
	 */
	private static String originalString(TreeMap<String,String> treeMap){
		Set<Entry<String, String>> entry = treeMap.entrySet();
		StringBuffer sb = new StringBuffer();
		for(Entry<String,String> obj : entry){
			String k = obj.getKey();
			String v = obj.getValue();
			if(v == null || v.equals(""))
				continue;
			sb.append(k+"="+v+"&");
		}
		return sb.toString();
	}
	private static String originalURLString(TreeMap<String,String> treeMap){
		Set<Entry<String, String>> entry = treeMap.entrySet();
		StringBuffer sb = new StringBuffer();
		try {
			for (Entry<String, String> obj : entry) {
				String k = obj.getKey();
				String v = obj.getValue();
				if (v == null && v.equals(""))
					continue;
				sb.append(k.toLowerCase() + "=" + URLEncoder.encode(v, "UTF-8") + "&");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sb.toString();
	}
	/**
	 * 创建微信支付订单号
	 * @return
	 */
	public static String getOutTradeNo(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String body=String.valueOf((int)(Math.random()*100000000));
		String outTradeNo="WXP"+sdf.format(new Date())+body;
		System.out.println("创建支付订单号："+outTradeNo);
		return outTradeNo;
	}
	/**
	 * 创建格式为yyyyMMddHHmmss的当前时间串
	 * @return
	 */
	public static String getNowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
	public static void main(String[] args) {
		
	}
	/**
	 * 是否财付通签名,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 */
	public synchronized static boolean isValidSign(TreeMap<String,String> treeMap){
		Set<Entry<String, String>> entry = treeMap.entrySet();
		StringBuffer sb = new StringBuffer();
		String signback = null;
		String input_charset=null;
		for(Entry<String,String> obj : entry){
			String k = obj.getKey();
			String v = obj.getValue();
			if(v == null && v.equals(""))
				continue;
			if(k.equals("sign")){
				signback = v;
				continue;
			}
			sb.append(k.toLowerCase()+"="+v+"&");
		}
		String string1=sb.toString();
		String stringSignTemp = string1 + "key="+ConfigUtils.PATERNER_KEY;
		System.out.println("程序计算签名串："+stringSignTemp);
		String sign=MD5Util.MD5Encode(stringSignTemp, input_charset).toUpperCase();
		System.out.println("程序计算财付通签名："+sign);
		System.out.println("系统返回签名："+signback);
		if(sign.equals(signback)){
			System.out.println("DeBug财付通签名比对结果：TRUE");
			return true;
		}else{
			System.out.println("DeBug财付通签名比对结果：FALSE");
			return false;
		}
	} 
	/**
	 * 判断微信签名
	 * @param treeMap
	 * @return
	 */
	public synchronized static boolean isWXSign(TreeMap<String,String> treeMap){
		Set<Entry<String, String>> entry = treeMap.entrySet();
		StringBuffer sb = new StringBuffer();
		String appSignature = null;
		for(Entry<String,String> obj : entry){
			String k = obj.getKey();
			String v = obj.getValue();
			if(v == null && v.equals(""))
				continue;
			if(k.equals("AppSignature")){
				appSignature = v;
				continue;
			}
			if(k.equals("SignMethod")){
				continue;
			}
			sb.append(k.toLowerCase()+"="+v+"&");
		}
		String paysign = sb.toString();
		paysign = paysign.substring(0, paysign.length()-1);
		System.out.println("\n\n程序计算微信签名串："+paysign);
		paysign = Sha1Util.getSha1(paysign);
		System.out.println("程序计算微信签名结果："+paysign);
		System.out.println("微信返回签名结果："+appSignature);
		if(paysign.equals(appSignature)){
			System.out.println("DeBug微信签名比对结果：TRUE");
			return true;
		}else{
			System.out.println("DeBug微信签名比对结果：FALSE");
			return false;
		}
	}
	public static TreeMap<String,String> mapToTreeMap(Map<Object,Object> map){
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		Set<Entry<Object, Object>> entry = map.entrySet();
		for (Entry<Object, Object> key : entry) {
			treeMap.put(key.getKey().toString(), ((String[])key.getValue())[0].toString());
		}
		return treeMap;
	}
	public static TreeMap<String,String> strmapToTreeMap(Map<String,String> map){
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		Set<Entry<String, String>> entry = map.entrySet();
		for (Entry<String, String> key : entry) {
			treeMap.put(key.getKey().toString(), key.getValue().toString());
		}
		return treeMap;
	}
	
	public static String getPrepayId(TreeMap<String, String> payParamMap,Payment payment){
		String result=HttpTool.sendPost(payment.getPrepay_id_url(), payParamMap, "UTF-8");
		return result;
	}
}
