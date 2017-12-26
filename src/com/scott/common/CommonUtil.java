package com.scott.common;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CommonUtil {
	
	public static ApplicationContext getSpringApplicationContext() {

		String[] locations = new String[] { "conf/applicationContext.xml", "conf/dbConfigure.xml" };
		return new FileSystemXmlApplicationContext(locations);
	}
	
	
	
	public static Connection getConnection(ApplicationContext context) {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			DBParam param = (DBParam) context.getBean("dbParam");
			conn = DriverManager.getConnection(param.getDriverUrl(), param.getUsername(), param.getPassword());
		} catch (Exception e) {
		}
		return conn;
	}

}
