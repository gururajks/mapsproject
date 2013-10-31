package com.support.ngsmapsproject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class NgsDataDownloader {
	
	String retrievalType;
	String radius;
	String controlType; 
	String pid;
	String ngsLat;
	String ngsLng;
	
	/*
	 * For radius request
	 */
	public NgsDataDownloader(double lat,double lng, String radius, String controlType) {
		this.controlType = controlType;
		this.radius = radius;
		this.retrievalType = "RADIUS";
		String suffix;
		ngsLat = getNgsCoordFormat(lat);
		ngsLng = getNgsCoordFormat(lng);		

		if(lat > 0) suffix="N"; 
		else suffix ="S";
		ngsLat = suffix + ngsLat;
		if(lng > 0) suffix="E";
		else suffix ="W";
		if(lng < 100) suffix = suffix + "0";
		ngsLng = suffix + ngsLng;		
		
	}
	
	/*
	 * for PID request
	 */
	public NgsDataDownloader(String pid, String controlType) {
		this.retrievalType = "PIDS"; 
		this.pid = pid;
		this.controlType = controlType;
	}
	

    //Downloading the url
    public ArrayList<NgsParameters> downloadUrl() throws IOException {
    	
		
    	
    	InputStream is = null;
    	int len = 50000; 
    	HttpURLConnection conn = null;
    	try {
    		//office coordinates: 42.362444,-71.162334    		    		
    		//System.out.println("http://www.ngs.noaa.gov/cgi-bin/get_mark_list.prl?1=F-" + retrievalType + "&2=" + ngsLat + "&3="+ ngsLng + "&4="+radius+"&5=" + controlType);
    		//URL url = new URL("http://www.ngs.noaa.gov/cgi-bin/get_mark_list.prl?1=F-" + retrievalType + "&2=" + ngsParam[0] + "&3="+ ngsParam[1] + "&4="+radius+"&5=" + controlType);
    		URL url = ngsUrlMaker();
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setReadTimeout(10000);
    		conn.setConnectTimeout(10000);
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);
    		
    		//Starting the query
    		conn.connect();
    		int responseCode = conn.getResponseCode();
    		System.out.println("The response is: " + responseCode);    		
    		is = conn.getInputStream();
    		
    		NgsHtmlParser ngsParser = new NgsHtmlParser(is);
        	ArrayList<NgsParameters> latLngList = ngsParser.getLatLntList();
        	
        	return latLngList;   		
    		
    	}
    	catch(IOException e) { 
    		int responseCode = conn.getResponseCode();
    		System.out.println("The response is: " + responseCode);    		
    	}    	
    	
		return null;    	
    }
    
    
    //Makes the NGS url depending on the attributes of the query
    //ngsParam[0] - lat, ngsParam[1] - lng of the center for radius retrieval
    //ngsParam[1] - minLat, ngsParam[1]- minLng, ngsParam[2] - maxLat, ngsParam[3] - maxLng for MINMAX retrieval
    //ngsParam[0] - One PID for PIDS retrieval
    private URL ngsUrlMaker() throws MalformedURLException {
    	String ngsUrl=null; 
    	String urlHead = "http://www.ngs.noaa.gov/cgi-bin/get_mark_list.prl?";
    	if(retrievalType.equals("RADIUS")) {
    		ngsUrl = urlHead + "1=F-" + retrievalType + "&2=" + ngsLat + "&3="+ ngsLng + "&4="+radius+"&5=" + controlType;
    	}
    	
    	if(retrievalType.equals("PIDS")) {
    		ngsUrl = urlHead + "1=F-" + retrievalType + "&2=" + pid + "&3="+ controlType;
    	}
    	    	
    	return new URL(ngsUrl);
    }
    
    private String getNgsCoordFormat(double coord) {    	
		String stringMinutes="";
		String stringSeconds="";
		double minutesFract = coord % 1;
		int deg = (int) Math.abs(coord - minutesFract);
		double tempFract = minutesFract * 60;
		double secFract = tempFract % 1; 
		int min = (int) Math.abs(tempFract - secFract);
		int sec = (int) Math.abs(secFract * 60);
		if(min < 10) stringMinutes = "0" + String.valueOf(min);
		else stringMinutes = String.valueOf(min);
		if(sec < 10) stringSeconds = "0" + String.valueOf(sec);
		else stringSeconds = String.valueOf(sec);
		String ngsCoord = String.valueOf(deg) + stringMinutes + stringSeconds;
		return ngsCoord;
	}
    
    //Setter
    public void setRetrievalType(String retrievalType) {
    	this.retrievalType = retrievalType;
    }


}
