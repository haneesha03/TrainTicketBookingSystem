package com.train.config;

import java.sql.*;

public class DatabaseConfig {
	private static final String url="jdbc:mysql://localhost:3306/trainmanagementSystem";
	private static final String user="root";
	private static final String password="Haneesha@030805";
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url,user,password);
	}
}
