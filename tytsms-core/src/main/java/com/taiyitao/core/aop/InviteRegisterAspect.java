package com.taiyitao.core.aop;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iskyshop.core.dao.IGenericDAO;
import com.iskyshop.core.tools.Base64;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.InviteRegister;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IInviteRegisterService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

@Service
@Transactional
public class InviteRegisterAspect {
	private static Log _log = LogFactory.getLog(InviteRegisterAspect.class.toString());
	
	@Resource(name = "inviteRegisterDAO")
	private IGenericDAO<InviteRegister> inviteRegisterDAO;
	
	@Autowired
	private IInviteRegisterService inviteRegisterService ;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IUserService userService;
	
	public void doBefore(JoinPoint jp){
		Object[] objs = jp.getArgs();
		HttpServletRequest request = (HttpServletRequest)objs[0];
		try {
			String qstr = request.getParameter("info");
			if(!StringUtils.isEmpty(qstr)){
				request.getSession().setAttribute("info", qstr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			_log.error("监控用户登录异常！异常信息："+e.getLocalizedMessage());
		}
		
		
	}
	
	
	public void doAfterRegister(JoinPoint jp){
		Object[] objs = jp.getArgs();
		HttpServletRequest request = (HttpServletRequest)objs[0];
		if(request.getSession().getAttribute("info")!=null){
			try {
				String info = Base64.getURLDecoderdecode((String)request.getSession().getAttribute("info"));
				if(!StringUtils.isEmpty(info)){
					String userName = request.getParameter("userName");
					InviteRegister inviteRegister  = new InviteRegister();
					inviteRegister.setInviterId(Long.parseLong(info.split("#")[0]));
					inviteRegister.setInviterName(info.split("#")[1]);
					inviteRegister.setInviteeName(userName);
					inviteRegister.setInviteDate(info.split("#")[2]);
					inviteRegister.setAddTime(new Date());
					inviteRegister.setInviteRule("");
					inviteRegisterService.save(inviteRegister);
					request.getSession().setAttribute("info", null);
					//邀请注册成功赠送积分
					if (this.configService.getSysConfig().isIntegral()) {
						User user = this.userService.getObjById(inviteRegister.getInviterId());
						user.setIntegral(user.getIntegral()+ this.configService.getSysConfig().getInviteRegister());
						this.userService.update(user);
						//记录日志
						IntegralLog log = new IntegralLog();
						log.setAddTime(new Date());
						log.setContent("邀请用户注册赠送 "
								+ this.configService.getSysConfig().getInviteRegister()
								+ "分");
						log.setIntegral(this.configService.getSysConfig()
								.getInviteRegister());
						log.setIntegral_user(user);
						log.setType("inviteRegister");
						this.integralLogService.save(log);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				_log.error("异常信息："+e.getLocalizedMessage());
			}
		}
	}
}
