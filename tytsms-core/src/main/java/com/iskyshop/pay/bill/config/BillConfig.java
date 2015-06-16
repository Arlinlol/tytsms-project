package com.iskyshop.pay.bill.config;

public class BillConfig {
	private String merchantAcctId;
	private String key;
	private String pid;

	public String getMerchantAcctId() {
		return merchantAcctId;
	}

	public void setMerchantAcctId(String merchantAcctId) {
		this.merchantAcctId = merchantAcctId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public BillConfig(String merchantAcctId, String key, String pid) {
		super();
		this.merchantAcctId = merchantAcctId;
		this.key = key;
		this.pid = pid;
	}
}
