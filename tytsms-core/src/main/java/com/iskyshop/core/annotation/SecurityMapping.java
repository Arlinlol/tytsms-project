package com.iskyshop.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
* <p>Title: SecurityMapping.java</p>

* <p>Description:系统权限资源标签，iskyshop商城系统使用springSecurity作为权限框架，该注解用在需要纳入权限管理的action中，
 *       通过AdminManageAction中init_role方法完成权限资源基础数据的导入以及不同用户角色权限的分配 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SecurityMapping {
	String title() default "";// 权限名称

	String value() default "";// 权限值

	String rname() default "";// 角色名称

	String rcode() default "";// 角色编码

	int rsequence() default 0;// 角色编码序号

	String rgroup() default "";// 角色分组

	String rtype() default "";// 角色类型

	boolean display() default true;// 是否显示该角色，默认1为显示，特殊情况设置为0不显示，不显的角色，添加管理员时候自动赋予

}
