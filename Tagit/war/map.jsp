<%@page import="java.util.Iterator"%>
<%@page import="com.jorneo.tag.data.Tag"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.jorneo.tag.data.TagData"%>


<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%

TagData tagData = new TagData();
ArrayList<Tag> tags = tagData.getTags("hrruf");
%>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />

<style type="text/css">
html {
	height: 100%;
}

body {
	height: 100%;
	margin: 0;
	padding: 0
}

#map-canvas {
	height: 480px;
	width: 640px;
	top:100px;
	left:120px;
	
}
#calendars{
	position:absolute;
	width:120px;
	height:480px;
	top:100px;
	left:0px;

}

</style>
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAi_V0oQNiR1JVEBSkM-EnvQ_rxZFEvOVg&sensor=false">
    </script>
<script type="text/javascript">
var map;
var tags = new Array();
	var itemsByDate=new Array();
	<%
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		HashMap<String, ArrayList<Tag>> tagsByDate = new HashMap<String, ArrayList<Tag>>();
		for(Iterator<Tag> i = tags.iterator();i.hasNext();){
			Tag tag = i.next();
			out.println("tag={lat:"+tag.getLatitude()+",lon:"+tag.getLongitude()+",txt:\""+tag.getText().replaceAll("\"","\\\"")+"\", ts:'"+tag.getTimestampString()+"'};");
			out.println("tags[tags.length]=tag");
			Calendar c = tag.getTimestamp();
			String date = format.format(c.getTime());
			if(! tagsByDate.containsKey(date)){
				ArrayList<Tag> l=new ArrayList<Tag>();
				tagsByDate.put(date,l);
				%>
					itemsByDate["<%=date%>"]=new Array();
				<%
			}
			ArrayList<Tag> dailyTags = tagsByDate.get(date);
			dailyTags.add(tag);
			%>
				itemsByDate["<%=date%>"].push(tag);
			<%
		}
	%>
      function initialize() {
    	  var tag = {lat:44.588471,lon:-0.240952,txt:'default'};
    	  var curTags = tags;
    	  if(window.location.hash){
    		  var date = window.location.hash.substring(1);
    		  alert(date);
    		  curTags = itemsByDate[date];
    	  }
    	  
    	  setPoints(curTags);
        
      }
      google.maps.event.addDomListener(window, 'load', initialize);
      
      function setPoints(tags){
    	  var tag=tags[0];
    	  var mapOptions = {
    	          center: new google.maps.LatLng(tag.lat, tag.lon),
    	          zoom: 8,
    	          mapTypeId: google.maps.MapTypeId.ROADMAP
    	        };
    	        map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
    	        for(var i=0;i<tags.length;i++){       	
    	        	addMarker(tags[i]);
    	        }
      }
      function addMarker(tag) {
    	  var location = new google.maps.LatLng(tag.lat,tag.lon);
    	  var title = tag.txt;
    	  
          marker = new google.maps.Marker({
              position: location,
              title:title,
              map: map
          });
          tag.marker=marker;
          
          tag.map=map;
          
          google.maps.event.addListener(marker, 'click', function() {
        	    if(!this.info){
	        	    var infowindow = new google.maps.InfoWindow();
	                infowindow.setContent(title+"<br/>"+tag.ts);
	                infowindow.setPosition(location);
	                this.info=infowindow;
          		}
                this.info.open(this.map);
        	});
          
      }

      function setPoint(){
	      // Testing the addMarker function
	      CentralPark = new google.maps.LatLng(44.588471, -0.240952);
	      addMarker(CentralPark);
      }
    </script>
</head>
<body>
<a href="hello">hello</a>
	<div id="calendars">
			<%
				String[] dates = tagsByDate.keySet().toArray(new String[tagsByDate.size()]);
				Arrays.sort(dates);
				for(int i=dates.length;i>0;--i){
					String date = dates[i-1];
					ArrayList<Tag> dailyTags = tagsByDate.get(date);
					%>
						<a href="map.jsp#<%=date%>" onclick="setPoints(itemsByDate['<%=date%>'])"><%=date%> (<%=dailyTags.size() %>)</a>
					<%
				}
			%>
	</div>
	<div id="map-canvas" />
	
</body>


</html>