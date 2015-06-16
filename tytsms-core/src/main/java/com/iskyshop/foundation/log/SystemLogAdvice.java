package com.iskyshop.foundation.log;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.LogFieldType;
import com.iskyshop.foundation.domain.LogType;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysLogService;
import com.iskyshop.foundation.service.IUserService;

/**
 * @info 系统日志管理类，这里使用Spring环绕通知和异常通知进行动态管理
 * @since V1.0
 * @author 沈阳网之商科技有限公司 www.iskyshop.com erikzhang
 * 
 */

@Aspect
@Component
public class SystemLogAdvice {
	@Autowired
	private ISysLogService sysLogService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userSerivce;
	@Autowired
	private IAccessoryService accessoryService;

	// 记录用户操作日志
	@AfterReturning(value = "execution(* com.iskyshop.manage..*.*(..))&& @annotation(annotation)&&args(request,..) ")
	public void userLog(JoinPoint joinPoint, Log annotation,
			HttpServletRequest request) throws Exception {
		if (Globals.SAVE_LOG) {
			saveLog(joinPoint, annotation, request);
		}
	}

	private void saveLog(JoinPoint joinPoint, Log annotation,
			HttpServletRequest request) throws Exception {
		if (SecurityUserHolder.getCurrentUser() != null) {
			String title = annotation.title();
			// 获取方法名
			String methodName = joinPoint.getSignature().getName();
			// 获取操作内容
			String description = SecurityUserHolder.getCurrentUser()
					.getTrueName()
					+ "于"
					+ CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date());
			if (annotation.type().equals(LogType.LOGIN)) {
				description = description + "登录系统";
			}
			if (annotation.type().equals(LogType.LIST)) {
				description = description + "查阅" + annotation.entityName();
			}
			if (annotation.type().equals(LogType.REMOVE)) {
				String mulitId = request.getParameter("mulitId");
				String[] ids = mulitId.split(",");
				if (ids.length > 1) {
					description = description + "批量删除"
							+ annotation.entityName() + "数据，Id为：" + mulitId;
				} else
					description = description + "删除Id为：" + mulitId + "的"
							+ annotation.entityName();
			}
			if (annotation.type().equals(LogType.SAVE)) {
				String id = request.getParameter("id");
				if (id.equals("")) {
					description = description + "新建并保存"
							+ annotation.entityName();
				} else {
					description = description + "修改并更新Id为：" + id + "的"
							+ annotation.entityName();
				}
			}
			if (annotation.type().equals(LogType.VIEW)) {
				String id = request.getParameter("id");
				description = description + "查阅Id为:" + id + "的"
						+ annotation.entityName();
			}
			if (annotation.type().equals(LogType.RESTORE)) {
				String id = request.getParameter("id");
				description = description + "还原系统数据为Id是" + id + "的备份数据";
			}
			if (annotation.type().equals(LogType.IMPORT)) {
				description = description + "从本地导入数据覆盖系统数据库";
			}
			if (annotation.type().equals(LogType.UPDATEPWS)) {
				String id = request.getParameter("id");
				String pws = request.getParameter("pws");
				description = description + "修改密码为" + pws.substring(0, 1)
						+ "*****";
			}
			if (annotation.type().equals(LogType.SEND)) {
				String toUser = request.getParameter("toUser");
				description = description + "向" + toUser + "发送站内短信息";
			}

			SysLog log = new SysLog();
			log.setTitle(title);
			log.setType(0);
			log.setAddTime(new Date());
			log.setUser(SecurityUserHolder.getCurrentUser());
			log.setContent(description);
			log.setIp(CommUtil.getIpAddr(request));
			this.sysLogService.save(log);
		}
	}

	// 异常日志记录
	@AfterThrowing(value = "execution(* com.iskyshop.manage..*.*(..))&&args(request,..) ", throwing = "exception")
	public void exceptionLog(HttpServletRequest request, Throwable exception) {
		if (Globals.SAVE_LOG) {
			SysLog log = new SysLog();
			log.setTitle("系统异常");
			log.setType(1);
			log.setAddTime(new Date());
			log.setUser(SecurityUserHolder.getCurrentUser());
			log.setContent("执行" + request.getRequestURI() == null ? ""
					: request.getRequestURI() + "时出现异常，异常代码为:"
							+ exception.getMessage());
			log.setIp(CommUtil.getIpAddr(request));
			this.sysLogService.save(log);
		}
	}

	// 记录用户登录日志

	public void loginLog() {
		System.out.println("用户登录");
	}

	private Method getMethod(ProceedingJoinPoint joinPoint) {
		// 获取连接点的方法签名对象
		MethodSignature joinPointObject = (MethodSignature) joinPoint
				.getSignature();
		// 连接点对象的方法
		Method method = joinPointObject.getMethod();
		// 连接点方法方法名
		String name = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		// 获取连接点所在的目标对象
		Object target = joinPoint.getTarget();
		// 获取目标方法
		try {
			method = target.getClass().getMethod(name, parameterTypes);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			method = null;
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}

	public String adminOptionContent(Object[] args, String mName)
			throws Exception {

		if (args == null) {
			return null;
		}
		StringBuffer rs = new StringBuffer();
		rs.append(mName);
		String className = null;
		int index = 1;
		// 遍历参数对象
		for (Object info : args) {
			// 获取对象类型
			className = info.getClass().getName();
			className = className.substring(className.lastIndexOf(".") + 1);
			boolean cal = false;
			LogFieldType[] types = LogFieldType.values();
			for (LogFieldType type : types) {
				if (type.toString().equals(className)) {
					cal = true;
				}
			}
			if (cal) {
				rs.append("[参数" + index + "，类型：" + className + "，值：");
				// 获取对象的所有方法
				Method[] methods = info.getClass().getDeclaredMethods();
				// 遍历方法，判断get方法
				for (Method method : methods) {
					String methodName = method.getName();
					// 判断是不是get方法
					if (methodName.indexOf("get") == -1) {// 不是get方法
						continue;// 不处理
					}
					Object rsValue = null;
					try {
						// 调用get方法，获取返回值
						rsValue = method.invoke(info);
						if (rsValue == null) {// 没有返回值
							continue;
						}
					} catch (Exception e) {
						continue;
					}
					// 将值加入内容中
					rs.append("(" + methodName + " : " + rsValue.toString()
							+ ")");
				}
				rs.append("]");
				index++;
			}
		}
		return rs.toString();
	}
}
