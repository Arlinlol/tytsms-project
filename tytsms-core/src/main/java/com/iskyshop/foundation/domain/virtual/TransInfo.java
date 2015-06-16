package com.iskyshop.foundation.domain.virtual;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>
 * Title: TransInfo.java
 * </p>
 * 
 * <p>
 * Description:快递查询信息返回值，该类不对应任何数据表，用在解析快递接口数据使用
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-26
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class TransInfo {
	private String express_company_name;// 快递公司信息
	private String express_ship_code;// 快递单号
	private String message;// 查询失败返回信息
	private String status;// 查询返回状态
	List<TransContent> data = new ArrayList<TransContent>();// 正确返回后的详细信息

	public String getExpress_company_name() {
		return express_company_name;
	}

	public void setExpress_company_name(String express_company_name) {
		this.express_company_name = express_company_name;
	}

	public String getExpress_ship_code() {
		return express_ship_code;
	}

	public void setExpress_ship_code(String express_ship_code) {
		this.express_ship_code = express_ship_code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<TransContent> getData() {
		return data;
	}

	public void setData(List<TransContent> data) {
		this.data = data;
	}

}
