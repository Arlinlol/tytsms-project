package com.iskyshop.manage.admin.tools;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.tools.PopupAuthenticator;
import com.iskyshop.core.tools.SmsBase;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: MsgTools.java<／p>
 * 
 * <p>
 * Description: 系统手机短信、邮件发送工具类，手机短信发送需要运营商购买短信平台提供的相关接口信息，邮件发送需要正确配置邮件服务器，
 * 运营商管理后台均有相关配置及发送测试 <／p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014<／p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com<／p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-24
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Component
public class MsgTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;

	public boolean sendSMS(String mobile, String content)
			throws UnsupportedEncodingException {
		boolean result = true;
		String url = this.configService.getSysConfig().getSmsURL();
		String userName = this.configService.getSysConfig().getSmsUserName();
		String password = this.configService.getSysConfig().getSmsPassword();
		SmsBase sb = new SmsBase(Globals.DEFAULT_SMS_URL, userName, password);// 固定硬编码短信发送接口
		String unescapeContent = HtmlUtils.htmlUnescape(content);
		String ret = sb.SendSms(mobile, unescapeContent);
		if (!ret.substring(0, 3).equals("000")) {
			result = false;
		}
		return result;
	}
	
	public InternetAddress getFromInternetAddress(String from){
		String regex1 = ".*[<][^>]*[>].*";    //判断是 xxxx <xxx>格式文本  
		String regex2 = "<([^>]*)>";   //尖括号匹配  
		String personal = null;  
	    String address = null;  
	    if(from.matches(regex1)){  
	        personal = from.replaceAll(regex2,"").trim();  
	        Matcher m = Pattern.compile(regex2).matcher(from);  
	        if(m.find()){  
	            address = m.group(1).trim();  
	        }  
	        try {  
	            return new InternetAddress(address, personal, "gb2312");  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        }  
	    }else{  
	        try {  
	            return new InternetAddress(from);  
	        } catch (AddressException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    return null;  
	}

	public boolean sendEmail(String email, String subject, String content) {
		boolean ret = true;
		String nickName = "";
		String password = "";
		String smtp_server = "";
		String from_mail_address = "";
		String userName="";
		nickName = this.configService.getSysConfig().getEmailUserName();
		userName=this.configService.getSysConfig().getEmailUser();
		password = this.configService.getSysConfig().getEmailPws();
		smtp_server = this.configService.getSysConfig().getEmailHost();
		from_mail_address =nickName+"<"+userName+">";
		String to_mail_address =email;
		if (userName != null && password != null && !userName.equals("")
				&& !password.equals("") && smtp_server != null
				&& !smtp_server.equals("") && to_mail_address != null
				&& !to_mail_address.trim().equals("")) {
			Authenticator auth = new PopupAuthenticator(userName, password);
			Properties mailProps = new Properties();
			mailProps.put("mail.smtp.auth", "true");
			mailProps.put("username", userName);
			mailProps.put("password", password);
			mailProps.put("mail.smtp.host", smtp_server);
			String unescapeContent = HtmlUtils.htmlUnescape(content);
			Session mailSession = Session.getInstance(mailProps, auth);
			MimeMessage message = new MimeMessage(mailSession);
			try {
				message.setFrom(getFromInternetAddress(from_mail_address));
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(to_mail_address));
				message.setSubject(subject);
				MimeMultipart multi = new MimeMultipart("related");
				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setDataHandler(new DataHandler(unescapeContent,
						"text/html;charset=UTF-8"));// 网页格式
				// bodyPart.setText(content);
				multi.addBodyPart(bodyPart);
				message.setContent(multi);
				message.saveChanges();
				Transport.send(message);
				ret = true;
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				ret = false;
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				ret = false;
				e.printStackTrace();
			}
		} else {
			ret = false;
		}
		return ret;
	}
}
