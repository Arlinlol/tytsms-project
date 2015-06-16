package com.iskyshop.pay;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.iskyshop.core.tools.Md5Encrypt;

public class SignatureHelper {
	public static String sign(Map params, String privateKey) {
		Properties properties = new Properties();

		for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			Object value = params.get(name);

			if (name == null || name.equalsIgnoreCase("sign")
					|| name.equalsIgnoreCase("sign_type")) {
				continue;
			}

			properties.setProperty(name, value.toString());

		}

		String content = getSignatureContent(properties);
		return sign(content, privateKey);
	}

	public static String getSignatureContent(Properties properties) {
		StringBuffer content = new StringBuffer();
		List keys = new ArrayList(properties.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = properties.getProperty(key);
			content.append((i == 0 ? "" : "&") + key + "=" + value);
		}

		return content.toString();
	}

	public static String sign(String content, String privateKey) {
		if (privateKey == null) {
			return null;
		}
		String signBefore = content + privateKey;
		// System.out.print("nsignBefore="+signBefore);
		// *****************************************************************
		// 当alipay收到信息，会把接受的信息写程日志
		// 该文件存在于和应用服务器 启动文件同一目录下，文件名是alipay log加服务器时间
		try {
			FileWriter writer = new FileWriter("D:/alipay_log/" + "alipay_log"
					+ "alipay_log" + System.currentTimeMillis() + ".txt");
			writer.write(signBefore);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// *********************************************************************
		return Md5Encrypt.md5(signBefore);

	}
}
