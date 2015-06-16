package com.iskyshop.foundation.test;

import java.awt.Font;

import com.iskyshop.core.tools.CommUtil;

public class TestWaterMark {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pressImg = "D:\\logo.jpg";
		String targetImg = "D:\\2.jpg";
		int pos =9;
		float alpha = 0.9f;
		try {
			CommUtil.waterMarkWithText(targetImg, "D:\\2.jpg", "iskyshop",
					"#FF0000", new Font("宋体", Font.BOLD, 30), pos, 100f);
			System.out.println("图片水印完成！");
		} catch (Exception e) {

		}

	}
}
