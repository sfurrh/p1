package com.jorneo.tag.data;

import java.sql.Connection;

public interface IConnectionFactory {
	final String listTagsByUser = Tag.getRetrieveSQL()+" WHERE user=? order by ts desc";
	final String listTagsByUserAndDate = Tag.getRetrieveSQL()+" WHERE user=? and date= ? order by ts desc";
	public Connection getConnection();
}
