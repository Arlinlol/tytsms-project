package com.iskyshop.view.web.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.iskyshop.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: MoneyUtil.java
 * </p>
 * 
 * <p>
 * Description:货币显示处理工具类,包含以下内容： 1、四舍五入求值 2、针对不同的格式化要求：万，百万，亿等
 * 3、会计格式的货币值：添加','符号 4、非科学计数法的货币值
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
 * @date 2014-8-26
 * 
 * @version iskyshop_b2b2c 1.0
 */
public class MoneyUtil {

	/**
	 * @title 获取格式化的人民币（四舍五入）
	 * @author chanson
	 * @param money
	 *            待处理的人民币
	 * @param scale
	 *            小数点后保留的位数
	 * @param divisor
	 *            格式化值（万，百万，亿等等）
	 * @return
	 */
	public String getFormatMoney(Object money, int scale, double divisor) {
		if (divisor == 0) {
			return "0.00";
		}
		if (scale < 0) {
			return "0.00";
		}
		BigDecimal moneyBD = new BigDecimal(CommUtil.null2Double(money));
		BigDecimal divisorBD = new BigDecimal(divisor);
		// RoundingMode.HALF_UP = 2
		return moneyBD.divide(divisorBD, scale, RoundingMode.HALF_UP)
				.toString();
	}

	/**
	 * @title 获取会计格式的人民币（四舍五入）——添加会计标识：','
	 * @author chanson
	 * @param money
	 *            待处理的人民币
	 * @param scale
	 *            小数点后保留的位数
	 * @param divisor
	 *            格式化值（万，百万，亿等等）
	 * @return
	 */
	public String getAccountantMoney(Object money, int scale, double divisor) {
		String disposeMoneyStr = getFormatMoney(money, scale, divisor);
		// 小数点处理
		int dotPosition = disposeMoneyStr.indexOf(".");
		String exceptDotMoeny = null;// 小数点之前的字符串
		String dotMeony = null;// 小数点之后的字符串
		if (dotPosition > 0) {
			exceptDotMoeny = disposeMoneyStr.substring(0, dotPosition);
			dotMeony = disposeMoneyStr.substring(dotPosition);
		} else {
			exceptDotMoeny = disposeMoneyStr;
		}
		// 负数处理
		int negativePosition = exceptDotMoeny.indexOf("-");
		if (negativePosition == 0) {
			exceptDotMoeny = exceptDotMoeny.substring(1);
		}
		StringBuffer reverseExceptDotMoney = new StringBuffer(exceptDotMoeny);
		reverseExceptDotMoney.reverse();// 字符串倒转
		// reverse(reverseExceptDotMoeny);
		char[] moneyChar = reverseExceptDotMoney.toString().toCharArray();
		StringBuffer returnMeony = new StringBuffer();// 返回值
		for (int i = 0; i < moneyChar.length; i++) {
			if (i != 0 && i % 3 == 0) {
				returnMeony.append(",");// 每隔3位加','
			}
			returnMeony.append(moneyChar[i]);
		}
		returnMeony.reverse();// 字符串倒转
		// reverse(returnMeony);
		if (dotPosition > 0) {
			returnMeony.append(dotMeony);
		}
		if (negativePosition == 0) {
			return "-" + returnMeony.toString();
		} else {
			return returnMeony.toString();
		}
	}

	/**
	 * @title 字符串倒转方法
	 * @detail 字符串倒转方法
	 * @author chanson
	 * @param oldStr
	 */
	// public void reverse(StringBuffer oldStr){
	// char[] oldStrChar = oldStr.toString().toCharArray();
	// StringBuffer newStr = new StringBuffer();
	// for(int i = oldStrChar.length - 1; i > -1; i--){
	// newStr.append(oldStrChar[i]);
	// }
	// oldStr = newStr;
	// }
	public static void main(String[] args) {
		BigDecimal money = BigDecimal.valueOf(85214521.8);
		int scale = 2;
		double divisor = 10000.00;
		System.out.println("原货币值: " + money);
		MoneyUtil util = new MoneyUtil();
		// System.out.println("货币值: "+util.getAccountantMoney(money, scale, 1));
		String formatMeony = util.getFormatMoney(money, scale, divisor);
		System.out.println("格式化货币值: " + formatMeony + "万元");
		String accountantMoney = util.getAccountantMoney(money, scale, divisor);
		System.out.println("会计货币值: " + accountantMoney + "万元");
		System.out.println(CommUtil.null2Float(8000.8));
	}
}