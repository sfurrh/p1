package com.jorneo.tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.jorneo.tag.data.Tag;
import com.jorneo.util.ZParm;


/**
 * Servlet implementation class Tagit
 */

public class Tagit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Tagit.class.getName());
	private static final String connectionString = "jdbc:google:rdbms://jorneotagit:jorneotagit/TAGIT";
	private static final String insertStatement = "INSERT INTO tags (status,ts,latitude,longitude,rating,eventid,type,user,description) VALUES(?,?,?,?,?,?,?,?,? )";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Tagit() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//	PrintWriter out = response.getWriter();
		String token = request.getHeader("wmh");
		token="hrruf";
		if(token!=null && token.equals("hrruf")) {
			log.info("received request");
		
			try {

				String user = token;

				String data = request.getQueryString();
				if(data==null || data.length()==0) {
					data = request.getParameter("data");
					if(data != null) {
						if(data.startsWith("?")) {
							data=data.substring(1);						
						}
						data=URLDecoder.decode(data, "UTF-8");
					}					
				}else if(data.startsWith("data=")) {
					data=data.substring(5);
				}
				data=URLDecoder.decode(data, "UTF-8");
				log.info("user="+user);
				log.info("data="+data);
				if(data!=null && data.length()>0) {
					ZParm root = new ZParm("root",data);
					List<ZParm> tags = root.getChildren("tag");
					log.info("tags.length="+tags.size());
					int tagsWritten = writeTags(tags,user);
					int respCode = 0;
					
					if(tagsWritten<tags.size()) {
						respCode = -1;
					}
					ZParm resp = new ZParm("response",new ZParm("code",""+respCode));
					resp.addChild(new ZParm("msg",tagsWritten + " tags saved"));
					response.getWriter().println(resp.toString());
				}

			} catch (Exception e) {
				StackTraceElement[] stack = e.getStackTrace();
				String location = "";
				for(int i=0;i<stack.length;i++) {
					String className = stack[i].getClassName();
					
					if(className.indexOf("com.jorneo.tag")>=0) {
						log.info("Stack className="+className);
						location+="(" + stack[i].getLineNumber() + ")" + className + "." + stack[i].getMethodName() + "\n"; 
					}
				}
				
				log.severe(e.getMessage()+"- "+location);
				e.printStackTrace();
			} finally {

			} 
		}else {
			
			String msg = "not valid request";
			ZParm code = new ZParm("code", "-1");
			ZParm rsp = new ZParm("response",code.toString());
			rsp.addChild(new ZParm("msg",msg));
			response.getWriter().write(rsp.toString());
			System.out.println(rsp.toString());
		}
	}

	private int writeTags(List<ZParm> tags, String user) {
		Connection c = null;
		int success = -1;
		int count = 0;
		try {
			DriverManager.registerDriver(new AppEngineDriver());
			c = DriverManager.getConnection(connectionString);
			for(Iterator<ZParm> i = tags.iterator();i.hasNext();) {
				PreparedStatement stmt = c.prepareStatement(insertStatement);
				ZParm ztag = i.next();
				String lat = ztag.getChild("lat").getValue();
				String lon = ztag.getChild("lon").getValue();
				String ts = ztag.getChild("ts").getValue();
				ts=ts.replace('~', '+');
				String description = ztag.getChild("tag").getValue();
				int rating = ztag.hasChild("rating")?Integer.parseInt(ztag.getChild("rating").getValue("50")):-1;
				int type = ztag.hasChild("type")?Integer.parseInt(ztag.getChild("type").getValue("1")):1;
				int groupid = ztag.hasChild("groupid")?Integer.parseInt(ztag.getChild("groupid").getValue("1")):-1;
				Tag tag = new Tag(lat, lon, ts, description);
//INSERT INTO tags (status,ts,lat,lon,rating,eventid,type,user,description) VALUES(?,?,?,?,?,?,?,?,? )				
				int parmCt=1;
				stmt.setInt(parmCt++, 1);
				stmt.setTimestamp(parmCt++, new Timestamp(tag.getTimestamp().getTime().getTime()),tag.getTimestamp());
				stmt.setDouble(parmCt++, Double.parseDouble(tag.getLatitude()));
				stmt.setDouble(parmCt++, Double.parseDouble(tag.getLongitude()));				
				stmt.setInt(parmCt++, rating);
				stmt.setInt(parmCt++, groupid);
				stmt.setInt(parmCt++, type);
				stmt.setString(parmCt++, user);
				stmt.setString(parmCt++,description);
				success = stmt.executeUpdate();
				if(success>=0) {
					count++;
				}else {
					log.severe("Error adding tag: "+success);
				}
			}
		}catch(Exception e){
			StringWriter eBuffer = new StringWriter();
			
			
			PrintWriter eWriter = new PrintWriter(eBuffer);
			log.severe(e.getMessage());
			e.printStackTrace(eWriter);
			eWriter.close();
			log.info("stack:\n"+eBuffer.toString());
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException ignore) {
				}
			}
		}
		return count;
	}

}
