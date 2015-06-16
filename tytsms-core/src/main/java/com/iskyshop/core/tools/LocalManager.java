/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iskyshop.core.tools;

import java.util.Locale;
/**
 * 
* <p>Title: LocalManager.java</p>

* <p>Description:管理线程的本地化对象服务，和国际化功能配合使用。 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class LocalManager {
	private static Locale defaultLocal=Locale.getDefault();
	private static ThreadLocal<Locale> locale;
	private static ThreadLocal<Locale> customLocale;
	/**
	 * 得到当前线程的本地化信息对象
	 * @return 当前local
	 */
	public static Locale getCurrentLocal()
	{		
		//if(customLocale)
		return locale!=null?locale.get():Locale.getDefault();
	}
	/**
	 * 设置当前本地化对象。
	 * 应该在调用getCurrentLocal（）方法之前设置，否则得到的将是服务器端的本地化对象。
	 * @param newLocale
	 */
	public static void setLocale(Locale newLocale) {
		if(locale==null)locale=new ThreadLocal<Locale>();
		locale.set(newLocale);
	}	
	public static void setCustomLocale(Locale newLocale)
	{
		if(customLocale==null)customLocale=new  ThreadLocal<Locale>();
		customLocale.set(newLocale);
	}
	public static Locale getDefaultLocal() {
		return defaultLocal;
	}
	public static void setDefaultLocal(Locale defaultLocal) {
		LocalManager.defaultLocal = defaultLocal;
	}

}
