package com.iskyshop.core.tools.database;

import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <p>
 * Title: IBackup.java
 * </p>
 * 
 * <p>
 * Description: 备份数据库和还原接口
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
 * @author erikzhang
 * 
 * @date 011-6-30
 * 
 * @version iskyshop_b2b2c 1.0
 */
@SuppressWarnings("serial")
public interface IBackup {

	/**
	 * 创建数据库脚本
	 */
	public boolean createSqlScript(HttpServletRequest request, String path,
			String name, String size, String tables) throws Exception;

	/**
	 * 执行数据库脚本
	 */
	public boolean executSqlScript(String filePath) throws Exception;

	/**
	 * 获取数据表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> getTables() throws Exception;

	/**
	 * 获取mysql数据库版本号
	 * 
	 * @return
	 */
	public String queryDatabaseVersion();

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 * @return
	 */
	public boolean execute(String sql);

	/**
	 * 导出指定表到path路径
	 * 
	 * @param tables
	 * @param path
	 * @return
	 */
	public boolean export(String tables, String path);

	/**
	 * 执行指定SQL查询
	 * 
	 * @param sql
	 * @return
	 */
	public ResultSet query(String sql);
}
