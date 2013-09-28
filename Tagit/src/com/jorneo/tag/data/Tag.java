package com.jorneo.tag.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.jorneo.util.StringUtil;

public class Tag {
	private static final Logger log = Logger.getLogger(Tag.class.getName());
	private static final SimpleDateFormat tsFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String[] tagFields = new String[]{"tagid","ts","latitude","longitude","eventid","type","rating","user","description"};
	public static final String getRetrieveSQL() {
		String out = "SELECT "+StringUtil.join(tagFields, ",")+" FROM tags";
		
		return out;
	}
	public static final String getInsertSQL() {
		String out = "INSERT INTO tags (";
		String cols ="";
		String vals ="";
		for(int i=0;i<tagFields.length;i++) {
			if(i>0) {
				cols+=",";
				vals+=",";
			}
			cols+=tagFields[i];
			vals+="?";
		}
		return out+") VALUES ("+vals+")";
	}

	private int _id;
	private String _rating;
	private String _type;
	private String _lat;
	private String _lon;
	private Calendar _ts;
	private String _text;
	private String _event;
	public Tag(String lat, String lon, String ts, String text) {
		this.setLatitude(lat);
		this.setLongitude(lon);
		this.setTimestamp(ts);
		this.setText(text);
	}
	
	public Tag(ResultSet rs) {
		//"id","ts","latitude","longitude","event","type","rating","user","description"
		int col = 1;
		try {
			int id = rs.getInt(col++);
			Timestamp ts = rs.getTimestamp(col++);
			String lat = rs.getString(col++);
			String lon = rs.getString(col++);
			String evt = rs.getString(col++);
			String typ = rs.getString(col++);
			String rat = rs.getString(col++);
			String usr = rs.getString(col++);
			String des = rs.getString(col++);
			this.setType(typ);
			this.setEvent(evt);
			this.setRating(rat);
			this.setId(id);
			this.setLatitude(lat);
			this.setLongitude(lon);
			this.setText(des);
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(ts.getTime());
			this.setTimestamp(cal);
		} catch (SQLException e) {
			log.severe("Error creating tag from RS: "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	public Calendar getUTCsTimestamp() {
		Calendar c = Calendar.getInstance();
		c.setTime(this.getTimestamp().getTime());
		TimeZone z = c.getTimeZone();
	    int offset = z.getRawOffset();
	    if(z.inDaylightTime(new Date())){
	        offset = offset + z.getDSTSavings();
	    }
	    int offsetHrs = offset / 1000 / 60 / 60;
	    int offsetMins = offset / 1000 / 60 % 60;

	    c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
	    c.add(Calendar.MINUTE, (-offsetMins));
	    return c;
	}
	
	public String getLatitude() {
		return _lat;
	}
	public void setLatitude(String _lat) {
		this._lat = _lat;
	}
	public String getLongitude() {
		return _lon;
	}
	public void setLongitude(String _lon) {
		this._lon = _lon;
	}
	public Calendar getTimestamp() {
		return _ts;
	}
	public String getTimestampString(String fmt) {
		String output = _ts.getTime().toString();
		
		try {
			if(fmt!=null) {
				SimpleDateFormat formatter = new SimpleDateFormat(fmt);
				output=formatter.format(_ts.getTime());
			}else {
				output=tsFormatter.format(_ts.getTime());
			}
		} catch (Exception e) {
			log.severe("Error timestamp string: "+e.getMessage());
		}
		return output;
	}
	public String getTimestampString() {
		String output = _ts.getTime().toString();
		
		try {
			output=tsFormatter.format(_ts.getTime());
		} catch (Exception e) {
			log.severe("Error in getTimestampString(): "+e.getMessage());	
		}
		return output;
	}
	public void setTimestamp(Calendar _ts) {
		this._ts = _ts;
	}
	public void setTimestamp(Date _ts) {
		Calendar c = Calendar.getInstance();
		c.setTime(_ts);
		this.setTimestamp(c);
	}
	public void setTimestamp(String ts) {
		try {
			Date d = tsFormatter.parse(ts);
			setTimestamp(d);
		} catch (ParseException e) {
			log.severe("Error setting timestamp with : "+ts+": "+e.getMessage());
			e.printStackTrace();
		}
	}
	public String getText() {
		return _text;
	}
	public void setText(String _text) {
		this._text = _text;
	}
	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	public String getRating() {
		return _rating;
	}
	public void setRating(String _rating) {
		this._rating = _rating;
	}
	public String getType() {
		return _type;
	}
	public void setType(String _type) {
		this._type = _type;
	}
	public String getEvent() {
		return _event;
	}
	public void setEvent(String _event) {
		this._event = _event;
	}
	
}
