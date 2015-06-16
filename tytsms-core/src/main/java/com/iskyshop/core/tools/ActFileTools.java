package com.iskyshop.core.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 功能描述：活动配置
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 
 * @author kingbox 新增日期：2015年4月13日
 * @author kingbox 修改日期：2015年4月13日
 *
 */
public class ActFileTools {
	
	private static Log log = LogFactory.getLog(ActFileTools.class);
	
	private static Properties prop = null;
	//活动商品id
	public static String GOODS_IDS ="";
	//包邮店铺id
	public static String STORE_IDS ="";
	//活动开始时间
	public static String START_TIME ="";
	//活动结束时间
	public static String END_TIME ="";
	//包邮金额
	public static double FREE_SHIPPING_VALUE =0.00;
	//商品ID列表
	public static List<String> GOODS_IDS_LIST =new ArrayList<String>();
	//店铺ID列表
	public static List<String> STORE_IDS_LIST =new ArrayList<String>();
	//10元优惠劵ID
	public static Long COUPON_ID = 0L;
	//5元优惠劵ID
	public static Long SCRATCH_COUPON = 0L;
	
	
	static {
		InputStream is = ActFileTools.class.getClassLoader().getResourceAsStream("act-goods.properties");
		try {
			prop = new Properties();
			prop.load(is);
		} catch (IOException e) {
			log.error(e);
		}
		GOODS_IDS = prop.getProperty("goods.ids");
		String [] GOODS_IDS_STRING = GOODS_IDS.split(",");
		for (int i = 0; i < GOODS_IDS_STRING.length; i++) {
			GOODS_IDS_LIST.add(GOODS_IDS_STRING[i]);
		}
		STORE_IDS = prop.getProperty("store.ids");
		String [] STORE_IDSS_STRING = STORE_IDS.split(",");
		for (int i = 0; i < STORE_IDSS_STRING.length; i++) {
			STORE_IDS_LIST.add(STORE_IDSS_STRING[i]);
		}
		START_TIME = prop.getProperty("start.time");
		END_TIME = prop.getProperty("end.time");
		FREE_SHIPPING_VALUE = Double.parseDouble(prop.getProperty("free.shipping.value"));
		COUPON_ID = Long.parseLong(prop.getProperty("coupon.id"));
		SCRATCH_COUPON = Long.parseLong(prop.getProperty("scratch.coupon"));
	}
	

	
}
