package com.iskyshop.core.tools.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.iskyshop.core.tools.UnicodeReader;

/**
 * 
 * <p>
 * Title: PublicMethod.java
 * </p>
 * 
 * <p>
 * Description: 备份和还原公共的方法
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
 * @date 2011-09-18
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Repository
@SuppressWarnings("serial")
public class PublicMethod {
	@Autowired
	private DbConnection dbConnectoin;
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;

	/**
	 * 
	 * 取数据库链接
	 * 
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		try {
			conn = this.dbConnectoin.getConnection();
		} catch (Exception e) {
			throw new Exception("数据链接错误,请检查用户输入的信息!");
		}
		return conn;
	}

	/**
	 * 关闭连接
	 * 
	 * @throws Exception
	 */
	public void closeConn() {
		this.dbConnectoin.closeAll();
	}

	/**
	 * 执行SQL返回查询结果集
	 * 
	 * @return ResultSet
	 * @throws Exception
	 */
	public ResultSet queryResult(String sqlStr) throws Exception {
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 查询所有的表或者是表结构
	 * 
	 * @throws Exception
	 */
	public List<String> getAllTableName(String sqlStr) throws Exception {
		List<String> list = null;
		try {
			list = new ArrayList<String>();
			rs = this.queryResult(sqlStr);
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dbConnectoin.closeAll();
		}
		return list;
	}

	/**
	 * 生成建表结构
	 * 
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public List<String> getAllColumns(String sqlStr) throws Exception {
		List<String> list = null;
		try {
			list = new ArrayList<String>();
			rs = queryResult(sqlStr);
			while (rs.next()) {
				list.add(rs.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnectoin.closeAll();
		}
		return list;
	}

	/**
	 * 查询表的列名，以及当前列对应的类型
	 * 
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public List<TableColumn> getDescribe(String sqlStr) throws Exception {
		List<TableColumn> list = null;
		try {
			list = new ArrayList<TableColumn>();
			rs = this.queryResult(sqlStr);
			while (rs.next()) {
				TableColumn dbColumns = new TableColumn();
				dbColumns.setColumnsFiled(rs.getString(1));
				dbColumns.setColumnsType(rs.getString(2));
				dbColumns.setColumnsNull(rs.getString(3));
				dbColumns.setColumnsKey(rs.getString(4));
				dbColumns.setColumnsDefault(rs.getString(5));
				dbColumns.setColumnsExtra(rs.getString(6));
				list.add(dbColumns);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnectoin.closeAll();
		}
		return list;
	}

	/**
	 * 读取指定路径的sql脚本
	 * 
	 * @param filePath
	 * @return List<String>
	 * @throws Exception
	 */
	public List<String> loadSqlScript(String filePath) throws Exception {
		List<String> sqlList = null;
		UnicodeReader inReader = null;
		StringBuffer sqlStr = null;
		try {
			sqlList = new ArrayList<String>();
			sqlStr = new StringBuffer();
			// inReader = new InputStreamReader(new
			// FileInputStream(filePath),"UTF-8");
			inReader = new UnicodeReader(new FileInputStream(filePath), "UTF-8");
			char[] buff = new char[1024];
			int byteRead = 0;
			while ((byteRead = inReader.read(buff)) != -1) {
				sqlStr.append(new String(buff, 0, byteRead));
			}
			// Windows下换行是\r\n, Linux 下是 \n
			String[] sqlStrArr = sqlStr.toString().split(
					"(;\\s*\\r\\n)|(;\\s*\\n)");
			for (String str : sqlStrArr) {
				String sql = str.replaceAll("--.*", "").trim();
				if (!sql.equals("")) {
					sqlList.add(sql);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlList;
	}

	/**
	 * 去掉字符串中的换行
	 * 
	 * @return
	 * @throws Exception
	 */
	public String trim(String obj) throws Exception {
		Matcher matcher = null;
		Pattern pattern = null;
		try {
			pattern = Pattern.compile("\\s*\n");
			matcher = pattern.matcher(obj.toString());
		} catch (Exception e) {
			throw e;
		}
		return matcher.replaceAll("");
	}

	public String genericQueryField(String table) {
		String query_sql = "";
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			// 生成字段字符串
			String sql = "select * from " + table;
			ResultSetMetaData rsmds = stmt.executeQuery(sql).getMetaData();
			for (int j = 1; j < rsmds.getColumnCount(); j++) {
				query_sql += rsmds.getColumnName(j) + ",";
			}
			query_sql += rsmds.getColumnName(rsmds.getColumnCount());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnectoin.closeAll();
		}
		return query_sql;
	}
}
