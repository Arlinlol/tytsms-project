/**
 * 
 */
package com.iskyshop.core.security;

import java.util.Map;

/**
 * 
* <p>Title: SecurityManager.java</p>

* <p>Description: 权限管理接口</p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public interface SecurityManager {

	public Map<String, String> loadUrlAuthorities();

}
