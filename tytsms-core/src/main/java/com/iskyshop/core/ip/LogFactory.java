package com.iskyshop.core.ip;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
* <p>Title: LogFactory.java</p>

* <p>Description:纯真ip查询日志记录 </p>

* <p>Copyright: Copyright (c) 2014</p>

* <p>Company: 沈阳网之商科技有限公司 www.iskyshop.com</p>

* @author erikzhang

* @date 2014-4-24

* @version iskyshop_b2b2c 1.0
 */
public class LogFactory {

	private static final Logger logger;
	static {
		logger = Logger.getLogger("stdout");
		logger.setLevel(Level.INFO);
	}

	public static void log(String info, Level level, Throwable ex) {
		logger.log(level, info, ex);
	}

	public static Level getLogLevel() {
		return logger.getLevel();
	}

}
