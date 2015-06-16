package com.iskyshop.foundation.test;

public class TestShip {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double weight = 3.2;
		int shipping_weight = 1;// 配送首重
		double shipping_fee = 12;// 配送首重费用
		int shipping_add_weight = 2;// 配送增重
		double shipping_add_fee = 2;// 增重费用
		System.out.println(Math.round(Math.ceil(weight-shipping_weight)));
		double price=shipping_fee+Math.round(Math.ceil(weight-shipping_weight))*shipping_add_fee/shipping_add_weight;
		System.out.println("总价为:"+price);

	}
}
