package com.iskyshop.uc.api;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * UC操作工具类，这里只有一个方法，商城注册用户同步在UC中添加用户后激活论坛用户，就是说在商城注册用户马上就可以在论坛登录 www.iskyshop.com
 * 
 * @author erikzhang
 * 
 */
@Component
public class UCTools {
	@Autowired
	private ISysConfigService configService;

	public boolean active_user(String userName, String pws, String email) {
		boolean ret = true;
		Connection conn = this.getConnection();
		Statement stmt;
		try {
			stmt = conn.createStatement();
			String sql = "insert into "
					+ this.configService.getSysConfig().getUc_table_preffix()
					+ "common_member (`email`,`username`,`password`,`regdate`,`groupid`) value ('"
					+ email + "','" + userName + "','" + pws + "','"
					+ (System.currentTimeMillis() / 1000) + "','10')";
			ret = stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.closeAll();
		}
		return ret;
	}

	// 线程安全
	public static final ThreadLocal<Connection> thread = new ThreadLocal<Connection>();

	public Connection getConnection() {
		Connection conn = thread.get();
		if (conn == null) {
			SysConfig config = this.configService.getSysConfig();
			String UC_DATABASE = config.getUc_database();
			String UC_TABLE_PREFFIX = config.getUc_table_preffix();
			String UC_DATABASE_URL = config.getUc_database_url();
			String UC_DATABASE_PORT = config.getUc_database_port();
			String UC_DATABASE_USERNAME = config.getUc_database_username();
			String UC_DATABASE_PWS = config.getUc_database_pws();
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://"
						+ UC_DATABASE_URL + ":" + UC_DATABASE_PORT + "/"
						+ UC_DATABASE, UC_DATABASE_USERNAME, UC_DATABASE_PWS);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			thread.set(conn);
		}
		return conn;
	}

	/**
	 * 关闭链接
	 * 
	 * @throws Exception
	 */
	public void closeAll() {
		try {
			Connection conn = thread.get();
			if (conn != null) {
				conn.close();
				thread.set(null);
			}
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		UCTools tools = new UCTools();
		tools.active_user("test", "122", "333@test.com");
	}
}
