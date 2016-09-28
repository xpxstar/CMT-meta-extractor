package iscas.xpx.devops.dao;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class BaseDao {
	Connection conn;
	public void connect(){
		String url="jdbc:mysql://localhost/puppet";
	      String user="root";
	      String pwd="";
	      
	      //加载驱动，这一句也可写为：Class.forName("com.mysql.jdbc.Driver");
	     try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	      //建立到MySQL的连接
	     try {
			conn = DriverManager.getConnection(url,user, pwd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void close(){
		try {
			if (conn !=null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
