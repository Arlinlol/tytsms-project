package com.iskyshop.core.tools.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.iskyshop.core.tools.CommUtil;

/**
 * 
 * <p>
 * Title: DatabaseTools.java
 * </p>
 * 
 * <p>
 * Description: MySql的备份和还原，不依赖本地mysql安装，不使用mysql命令完成数据库备份及还原，仅仅支持mysql数据库
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
 * @date 2012-08-28
 * 
 * @version iskyshop_b2b2c 1.0
 */
@Repository
@SuppressWarnings("serial")
public class DatabaseTools implements IBackup {

	@Autowired
	private PublicMethod publicMethod;

	public DatabaseTools() {
	}

	/**
	 * 创建mysql备份数据
	 * 
	 * @param path
	 *            备份路径
	 * @param tables
	 *            将要备份的数据表列表
	 * @param size
	 *            分卷备份大小
	 * @return
	 * @throws Exception
	 */
	public boolean createSqlScript(HttpServletRequest request, String path,
			String name, String size, String tables) throws Exception {
		int count = 1;
		boolean ret = true;
		float psize = CommUtil.null2Float(size);
		List<String> tablelists = publicMethod.getAllTableName("show tables");
		List<String> backup_list = new ArrayList<String>();
		if (tables != null && !tables.equals("")) {
			backup_list = Arrays.asList(tables.split(","));
		} else {
			backup_list = tablelists;
		}
		// 先写建表语句
		try {
			File file = new File(path + File.separator + name + "_" + count
					+ ".sql");
			PrintWriter pwrite;
			pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "UTF-8"), true);
			// 写入头部信息
			pwrite.println(AppendMessage.headerMessage());
			pwrite.println("SET FOREIGN_KEY_CHECKS=0;" + "\n");
			for (String table : backup_list) {
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append("show create table ").append(table);
				List<String> list = publicMethod.getAllColumns(strBuilder
						.toString());
				for (String line : list) {
					// 及时计算分卷文件大小
					double fsize = CommUtil.div(file.length(), 1024);
					if (fsize > psize) {
						pwrite.flush();
						// pwrite.close();
						count++;
						file = new File(path + File.separator + name + "_"
								+ count + ".sql");
						pwrite = new PrintWriter(new OutputStreamWriter(
								new FileOutputStream(file, true), "UTF-8"),
								true);
						// 写入头部信息
						pwrite.println(AppendMessage.headerMessage());
					}
					request.getSession(false).setAttribute("db_mode", "backup");
					request.getSession(false).setAttribute("db_bound", count);
					request.getSession(false).setAttribute("db_error", 0);
					request.getSession(false).setAttribute("db_result", 0);
					// 在建表前加说明
					pwrite.println(AppendMessage.tableHeaderMessage(table));
					// 生成建表语句
					pwrite.println("DROP TABLE IF EXISTS " + " `" + table
							+ "`;");
					pwrite.println(line + ";" + "\n");
				}
			}
			pwrite.flush();
			pwrite.close();
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
			throw new Exception("出现错误,创建备份文件失败!");
		}
		// 后写数据插入语句
		count++;
		try {
			File file = new File(path + File.separator + name + "_" + count
					+ ".sql");
			PrintWriter pwrite;
			pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "UTF-8"), true);
			// 写入头部信息
			pwrite.println(AppendMessage.headerMessage());
			pwrite.println(AppendMessage.insertHeaderMessage());
			for (String table : backup_list) {
				if (!CommUtil.null2String(table).equals("")) {
					// 生成insert语句
					List<String> insertList = getAllDatas(table.toString());
					for (int i = 0; i < insertList.size(); i++) {
						double fsize = CommUtil.div(file.length(), 1024);
						if (fsize > psize) {
							pwrite.flush();
							// pwrite.close();
							count++;
							file = new File(path + File.separator + name + "_"
									+ count + ".sql");
							pwrite = new PrintWriter(new OutputStreamWriter(
									new FileOutputStream(file, true), "UTF-8"),
									true);
							// 写入头部信息
							pwrite.println(AppendMessage.headerMessage());
						}
						request.getSession(false).setAttribute("db_mode",
								"backup");
						request.getSession(false).setAttribute("db_bound",
								count);
						request.getSession(false).setAttribute("db_error", 0);
						request.getSession(false).setAttribute("db_result", 0);
						pwrite.flush();
						pwrite.println(insertList.get(i));
					}
				}
			}
			pwrite.flush();
			pwrite.close();
			// 备份完成
			request.getSession(false).setAttribute("db_result", 1);
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
			throw new Exception("出现错误,创建备份文件失败!");
		}
		return ret;
	}

	/**
	 * 还原mysql备份
	 */
	public boolean executSqlScript(String filePath) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		List<String> sqlStrList = null;
		boolean ret = true;
		String ex_sql="";
		try {
			sqlStrList = publicMethod.loadSqlScript(filePath);
			conn = publicMethod.getConnection();
			stmt = conn.createStatement();
			// 禁止自动提交
			conn.setAutoCommit(false);
			for (String sqlStr : sqlStrList) {
				int index = sqlStr.indexOf("INSERT");
				if (-1 == index) {
					stmt.addBatch(sqlStr);
					System.out.println("Create语句：");
					System.out.println(sqlStr);
				}
			}
			stmt.executeBatch();
			// INSERT语句跟建表语句分开执行，防止未建表先INSERT
				for (String sqlStr : sqlStrList) {
				int index = sqlStr.indexOf("INSERT");
				if (-1 != index) {
					ex_sql=sqlStr;
					try{
						int status = stmt.executeUpdate(sqlStr);
					}catch(Exception ex){
						System.out.println("异常语句："+ex_sql);
						System.out.println(ex.getMessage());
						ex.printStackTrace();
					}
					
					//System.out.println("执行结果：" + status);
				}
			}
			stmt.executeBatch();
			conn.commit();
		} catch (Exception ex) {
			ret = false;
			conn.rollback();
		}
		return ret;
	}

	/**
	 * 根据数据表名称生成insert语句
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public List<String> getAllDatas(String tableName) throws Exception {
		List<String> list = new ArrayList<String>();
		StringBuilder typeStr = null;
		List<TableColumn> columnList;
		StringBuilder sqlStr;
		ResultSet rs = null;
		StringBuilder columnsStr;
		try {
			// 生成查询语句
			typeStr = new StringBuilder();
			sqlStr = new StringBuilder();
			columnsStr = new StringBuilder().append("describe ").append(
					tableName);
			columnList = publicMethod.getDescribe(columnsStr.toString());
			sqlStr.append("SELECT ");
			for (TableColumn bColumn : columnList) {
				// 处理BLOB类型的数据
				String columnsType = bColumn.getColumnsType();
				if ("longblob".equals(columnsType)
						|| "blob".equals(columnsType)
						|| "tinyblob".equals(columnsType)
						|| "mediumblob".equals(columnsType)) {
					typeStr.append("hex(" + "`" + bColumn.getColumnsFiled()
							+ "`" + ") as " + "`" + bColumn.getColumnsFiled()
							+ "`" + " ,");
				} else {
					typeStr.append("`" + bColumn.getColumnsFiled() + "`" + " ,");
				}
			}
			sqlStr.append(typeStr.substring(0, typeStr.length() - 1));
			sqlStr.append(" FROM ").append("`" + tableName + "`;");
			rs = publicMethod.queryResult(sqlStr.toString());
			while (rs.next()) {
				// 查询insert语句所需的数据
				StringBuffer insert_sql = new StringBuffer();
				insert_sql.append("INSERT INTO " + tableName + " ("
						+ typeStr.substring(0, typeStr.length() - 1)
						+ ") VALUES (");
				Vector<Object> vector = new Vector<Object>();
				for (TableColumn dbColumn : columnList) {
					String columnsType = dbColumn.getColumnsType();
					String columnsFile = dbColumn.getColumnsFiled();
					if (null == rs.getString(columnsFile)) {
						vector.add(rs.getString(columnsFile));
						// 处理BIT类型的数据
					} else if ("bit".equals(columnsType.substring(0, 3))) {
						vector.add(Integer.valueOf(rs.getString(columnsFile))
								.intValue());
					} else if ("bit".equals(columnsType.substring(0, 3))
							&& 0 == Integer.valueOf(rs.getString(columnsFile))
									.intValue()) {
						vector.add("\'" + "\'");
					} else if ("longblob".equals(columnsType)
							|| "blob".equals(columnsType)
							|| "tinyblob".equals(columnsType)
							|| "mediumblob".equals(columnsType)) {
						vector.add("0x" + rs.getString(columnsFile));
						// 处理
					} else if ("text".equals(columnsType)
							|| "longtext".equals(columnsType)
							|| "tinytext".equals(columnsType)
							|| "mediumtext".equals(columnsType)) {
						String tempStr = rs.getString(columnsFile);
						tempStr = tempStr.replaceAll("\'", "\\'").replaceAll(
								"'", "''");
						tempStr = tempStr.replaceAll("\"", "\\\"")
								.replaceAll("\r", "\\\\r")
								.replaceAll("\n", "\\\\n")
								.replaceAll("<!--[\\w\\W\\r\\n]*?-->", "");
						vector.add("\'" + tempStr + "\'");
					} else {
						String tempStr = rs.getString(columnsFile);
						tempStr = tempStr.replaceAll("\'", "\\'").replaceAll(
								"'", "''");
						tempStr = tempStr.replaceAll("\"", "\\\"")
								.replaceAll("\r", "\\\\r")
								.replaceAll("\n", "\\\\n")
								.replaceAll("<!--[\\w\\W\\r\\n]*?-->", "");
						vector.add("\'" + tempStr + "\'");
					}
				}
				String tempStr = vector.toString();
				tempStr = tempStr.substring(1, tempStr.length() - 1) + ");";
				insert_sql.append(tempStr);
				list.add(insert_sql.toString());
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}

	/**
	 * 查询生成Insert语句所需的数据
	 * 
	 * @param tableName
	 * @return List<Vector<Object>>
	 * @throws Exception
	 */
	public List<Vector<Object>> getAllDatas1(String tableName) throws Exception {
		List<Vector<Object>> list;
		Vector<Object> vector;
		StringBuilder typeStr = null;
		List<TableColumn> columnList;
		StringBuilder sqlStr;
		ResultSet rs = null;
		StringBuilder columnsStr;
		try {
			// 生成查询语句
			typeStr = new StringBuilder();
			sqlStr = new StringBuilder();
			columnsStr = new StringBuilder().append("describe ").append(
					tableName);
			columnList = publicMethod.getDescribe(columnsStr.toString());
			sqlStr.append("SELECT ");
			for (TableColumn bColumn : columnList) {
				// 处理BLOB类型的数据
				String columnsType = bColumn.getColumnsType();
				if ("longblob".equals(columnsType)
						|| "blob".equals(columnsType)
						|| "tinyblob".equals(columnsType)
						|| "mediumblob".equals(columnsType)) {
					typeStr.append("hex(" + "`" + bColumn.getColumnsFiled()
							+ "`" + ") as " + "`" + bColumn.getColumnsFiled()
							+ "`" + " ,");
				} else {
					typeStr.append("`" + bColumn.getColumnsFiled() + "`" + " ,");
				}
			}
			sqlStr.append(typeStr.substring(0, typeStr.length() - 1));
			sqlStr.append(" FROM ").append("`" + tableName + "`;");

			// 查询insert语句所需的数据
			list = new ArrayList<Vector<Object>>();
			rs = publicMethod.queryResult(sqlStr.toString());
			while (rs.next()) {
				vector = new Vector<Object>();
				for (TableColumn dbColumn : columnList) {
					String columnsType = dbColumn.getColumnsType();
					String columnsFile = dbColumn.getColumnsFiled();
					if (null == rs.getString(columnsFile)) {
						vector.add(rs.getString(columnsFile));
						// 处理BIT类型的数据
					} else if ("bit".equals(columnsType.substring(0, 3))) {
						vector.add(Integer.valueOf(rs.getString(columnsFile))
								.intValue());
					} else if ("bit".equals(columnsType.substring(0, 3))
							&& 0 == Integer.valueOf(rs.getString(columnsFile))
									.intValue()) {
						vector.add("\'" + "\'");
					} else if ("longblob".equals(columnsType)
							|| "blob".equals(columnsType)
							|| "tinyblob".equals(columnsType)
							|| "mediumblob".equals(columnsType)) {
						vector.add("0x" + rs.getString(columnsFile));
						// 处理
					} else if ("text".equals(columnsType)
							|| "longtext".equals(columnsType)
							|| "tinytext".equals(columnsType)
							|| "mediumtext".equals(columnsType)) {
						String tempStr = rs.getString(columnsFile);
						tempStr = tempStr.replace("\'", "\\'");
						tempStr = tempStr.replace("\"", "\\\"");
						vector.add("\'" + tempStr + "\'");
					} else {
						vector.add("\'" + rs.getString(columnsFile) + "\'");
					}
				}
				list.add(vector);
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}

	public List<String> getTables() throws Exception {
		List<String> tables = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = this.publicMethod.getConnection();
			ResultSet rs = conn.getMetaData().getTables("", "", "", null);
			while (rs.next()) {
				tables.add(rs.getString("TABLE_NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.publicMethod.closeConn();
		}
		return tables;
	}

	/**
	 * 获取mysql数据库版本号
	 * 
	 * @return
	 */
	public String queryDatabaseVersion() {
		java.sql.Connection conn = null;
		String version = "未知版本号";
		try {
			conn = this.publicMethod.getConnection();
			DatabaseMetaData md = conn.getMetaData();
			return md.getDatabaseProductName() + " "
					+ md.getDatabaseProductVersion().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			this.publicMethod.closeConn();
		}
		return version;
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 * @return
	 */
	public boolean execute(String sql) {
		Connection conn = null;
		boolean ret = true;
		try {
			conn = this.publicMethod.getConnection();
			java.sql.Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		} finally {
			this.publicMethod.closeConn();
		}
		return ret;
	}

	@Override
	public boolean export(String tables, String path) {
		// TODO Auto-generated method stub
		boolean ret = true;
		try {
			File file = new File(path);
			PrintWriter pwrite;
			pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "UTF-8"), true);
			// 写入头部信息
			pwrite.println(AppendMessage.headerMessage());
			pwrite.println(AppendMessage.insertHeaderMessage());
			List<String> list = Arrays.asList(tables.split(","));
			for (String table : list) {
				// 生成insert语句
				List<String> insertList = getAllDatas(table.toString());
				for (int i = 0; i < insertList.size(); i++) {
					pwrite.flush();
					pwrite.println(insertList.get(i));
				}
			}
			pwrite.flush();
			pwrite.close();
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public ResultSet query(String sql) {
		// TODO Auto-generated method stub
		Connection conn = null;
		ResultSet rs = null;
		boolean ret = true;
		try {
			conn = this.publicMethod.getConnection();
			java.sql.Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		} finally {
			this.publicMethod.closeConn();
		}
		return rs;
	}

}
