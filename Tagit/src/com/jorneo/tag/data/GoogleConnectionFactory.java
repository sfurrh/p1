package com.jorneo.tag.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.appengine.api.rdbms.AppEngineDriver;

public class GoogleConnectionFactory implements IConnectionFactory {
	private static final String connectionString = "jdbc:google:rdbms://jorneotagit:jorneotagit/TAGIT";
	private static GoogleConnectionFactory instance=null;
	
	private GoogleConnectionFactory() {
		
	}
	
	public static GoogleConnectionFactory getInstance() {
		if(instance==null) {
			instance=new GoogleConnectionFactory();
		}
		return instance;
	}
	
	@Override
	public Connection getConnection() {
		Connection c = null;
		
		try {
			DriverManager.registerDriver(new AppEngineDriver());
			c = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return c;
	}

}
