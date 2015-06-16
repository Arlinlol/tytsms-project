package com.iskyshop.manage.admin.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.pay.wechatpay.util.ConfigContants;

/**
 * 
 * <p>
 * Title: SendMsgAndEmTools.java
 * </p>
 * 
 * <p>
 * Description:发送短信邮件工具类 参数json数据 buyer_id:如果有买家，则买家user.id
 * seller_id:如果有卖家,卖家的user.id sender_id:发送者的user.id receiver_id:接收者的user.id
 * order_id:如果有订单 订单order.id childorder_id：如果有子订单id goods_id:商品的id self_goods:
 * 如果是自营商品 则在邮件或者短信显示 平台名称 SysConfig.title
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
 * @author jinxinzhe
 * 
 * @date 2014-5-28
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class SendMsgAndEmTools {
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;

	@Async
	public void sendMsg(String url, String path, String mark, String mobile,
			String json) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			Map map = this.queryJson(json);
			if (mobile != null && !mobile.equals("")) {
				path=path+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator;
				//创建vm文件夹路径
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + mark+".vm", false), "UTF-8"));
				pwrite.print(template.getContent());
				pwrite.flush();
				pwrite.close();
				// 生成模板
				Properties p = new Properties();
				p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
				p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
				p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
				Velocity.init(p);
				org.apache.velocity.Template blank = Velocity.getTemplate(
						mark+".vm", "UTF-8");
				VelocityContext context = new VelocityContext();
				if (map.get("receiver_id") != null) {
					Long receiver_id = CommUtil.null2Long(map
							.get("receiver_id"));
					User receiver = this.userService.getObjById(receiver_id);
					context.put("receiver", receiver);
				}
				if(map.get("user_id") != null){
					Long user_id = CommUtil.null2Long(map.get("user_id"));
					User userObject= this.userService.getObjById(user_id);
					context.put("userObject", userObject);
				}
				if(map.get("store_id") != null){
					Long store_id = CommUtil.null2Long(map.get("store_id"));
					Store storeObject= this.storeService.getObjById(store_id);
					context.put("storeObject", storeObject);
					if(storeObject.getViolation_reseaon()!=null){
						context.put("reason", storeObject.getViolation_reseaon());
					}
				}
				if (map.get("sender_id") != null) {
					Long sender_id = CommUtil.null2Long(map.get("sender_id"));
					User sender = this.userService.getObjById(sender_id);
					context.put("sender", sender);
				}
				if (map.get("buyer_id") != null) {
					Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
					User buyer = this.userService.getObjById(buyer_id);
					context.put("buyer", buyer);
				}
				if (map.get("seller_id") != null) {
					Long seller_id = CommUtil.null2Long(map.get("seller_id"));
					User seller = this.userService.getObjById(seller_id);
					context.put("seller", seller);
				}
				if (map.get("order_id") != null) {
					Long order_id = CommUtil.null2Long(map.get("order_id"));
					OrderForm orderForm = this.orderFormService
							.getObjById(order_id);
					context.put("orderForm", orderForm);
				}
				if (map.get("childorder_id") != null) {
					Long childorder_id = CommUtil.null2Long(map
							.get("childorder_id"));
					OrderForm orderForm = this.orderFormService
							.getObjById(childorder_id);
					context.put("child_orderForm", orderForm);
				}
				if (map.get("goods_id") != null) {
					Long goods_id = CommUtil.null2Long(map.get("goods_id"));
					Goods goods = this.goodsService.getObjById(goods_id);
					context.put("goods", goods);
				}
				if (map.get("self_goods") != null) {
					context.put("seller", map.get("self_goods").toString());
				}
				context.put("config", this.configService.getSysConfig());
				context.put("send_time", CommUtil.formatLongDate(new Date()));
				context.put("webPath",url);
				StringWriter writer = new StringWriter();
				blank.merge(context, writer);
				// System.out.println(writer.toString());
				String content = writer.toString();
				this.msgTools.sendSMS(mobile, content);
				System.out.println("发送短信成功");
			}
		}
	}

	@Async
	public void sendEmail(String url, String path, String mark, String email,
			String json) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			Map map = this.queryJson(json);
			String subject = template.getTitle();
			path=path+ ConfigContants.GENERATOR_FILES_MIDDLE_NAME + File.separator;
			//创建vm文件夹路径
			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + mark+".vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate(mark+".vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			if (map.get("receiver_id") != null) {
				Long receiver_id = CommUtil.null2Long(map.get("receiver_id"));
				User receiver = this.userService.getObjById(receiver_id);
				context.put("receiver", receiver);
			}
			if(map.get("user_id") != null){
				Long user_id = CommUtil.null2Long(map.get("user_id"));
				User userObject= this.userService.getObjById(user_id);
				context.put("userObject", userObject);
			}
			if(map.get("store_id") != null){
				Long store_id = CommUtil.null2Long(map.get("store_id"));
				Store storeObject= this.storeService.getObjById(store_id);
				context.put("storeObject", storeObject);
				if(storeObject.getViolation_reseaon()!=null){
					context.put("reason", storeObject.getViolation_reseaon());
				}
			}
			if (map.get("sender_id") != null) {
				Long sender_id = CommUtil.null2Long(map.get("sender_id"));
				User sender = this.userService.getObjById(sender_id);
				context.put("sender", sender);
			}
			if (map.get("buyer_id") != null) {
				Long buyer_id = CommUtil.null2Long(map.get("buyer_id"));
				User buyer = this.userService.getObjById(buyer_id);
				context.put("buyer", buyer);
			}
			if (map.get("seller_id") != null) {
				Long seller_id = CommUtil.null2Long(map.get("seller_id"));
				User seller = this.userService.getObjById(seller_id);
				context.put("seller", seller);
			}
			if (map.get("order_id") != null) {
				Long order_id = CommUtil.null2Long(map.get("order_id"));
				OrderForm orderForm = this.orderFormService
						.getObjById(order_id);
				context.put("orderForm", orderForm);
			}
			if (map.get("childorder_id") != null) {
				Long childorder_id = CommUtil.null2Long(map
						.get("childorder_id"));
				OrderForm orderForm = this.orderFormService
						.getObjById(childorder_id);
				context.put("child_orderForm", orderForm);
			}
			if (map.get("goods_id") != null) {
				Long goods_id = CommUtil.null2Long(map.get("goods_id"));
				Goods goods = this.goodsService.getObjById(goods_id);
				context.put("goods", goods);
			}
			if (map.get("self_goods") != null) {
				context.put("seller", map.get("self_goods").toString());
			}
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", url);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
			System.out.println("发送邮件成功");
		}
	}

	private Map queryJson(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}
}
