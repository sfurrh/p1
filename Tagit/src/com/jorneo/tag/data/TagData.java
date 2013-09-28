package com.jorneo.tag.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TagData {
	private static final Logger log = Logger.getLogger(TagData.class.getName());
	IConnectionFactory dataFactory = GoogleConnectionFactory.getInstance();
	
	public ArrayList<Tag> getTags(String user){
		return getTags(user,null);
	}
	public ArrayList<Tag> getTags(String user, String date){
		Connection c = null;
		int success = -1;
		int count = 0;
		ArrayList<Tag> tags=new ArrayList<Tag>();
		try {
			c = dataFactory.getConnection();
			String sql = IConnectionFactory.listTagsByUser;
			if(date!=null) {
				sql=IConnectionFactory.listTagsByUserAndDate;
			}
			System.out.println("sql="+sql);
			PreparedStatement statement = c.prepareStatement(sql);
			statement.setString(1, "hrruf");
			if(date!=null) {
				statement.setString(2, date);
			}
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Tag t = new Tag(rs);
				tags.add(t);
			}
		}catch(Exception e){
			log.severe(e.getMessage());
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException ignore) {
				}
			}
		}
		return tags;
	}
}
