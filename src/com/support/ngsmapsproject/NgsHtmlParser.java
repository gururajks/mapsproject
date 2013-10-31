package com.support.ngsmapsproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;




public class NgsHtmlParser {
	
	String tagName;
	String tagText;
	ArrayList<NgsParameters> latLngList;
	
	//Html parser constructor
	public NgsHtmlParser(InputStream xmlString) {
		latLngList = new ArrayList<NgsParameters>();
		try {
			XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			XmlPullParser xmlParser = parserFactory.newPullParser();			
			xmlParser.setInput(xmlString, null);
			int eventType = xmlParser.getEventType();
			int lineCounter = 0;
			while(eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_DOCUMENT) System.out.println("START");
				else if(eventType == XmlPullParser.END_DOCUMENT) System.out.println("END");
				else if(eventType == XmlPullParser.START_TAG) {					
					tagName = xmlParser.getName();
					if(tagName.equalsIgnoreCase("option")) {
						//DONT USE ANY .nextTag functions as that will screw up the xmlparser 
						if(lineCounter > 1) {
							String optionValue = xmlParser.nextText();							
							if(optionValue != null) {
								if(optionValue.substring(0, 1).equalsIgnoreCase("|")){									
									NgsParameters coordinates = parseNgsInfo(optionValue);
									latLngList.add(coordinates);
								}
							}												
						}
						lineCounter++;
					}
				}	
				else if(eventType == XmlPullParser.END_TAG) System.out.println("END_TAG");
				eventType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	

	/* Parsing the ngs data to get the coordinates */
	private NgsParameters parseNgsInfo(String ngsInfo) {
		NgsParameters ngsParam = new NgsParameters();			
		String[] ngsInfoParts = ngsInfo.split("\\|");
		if(ngsInfoParts != null) {
			String pid = ngsInfoParts[2];
			String designation = ngsInfoParts[9];
			double latDeg = Double.parseDouble(ngsInfoParts[5].substring(1,3));
			double latMin = Double.parseDouble(ngsInfoParts[5].substring(3,5));
			double latSec = Double.parseDouble(ngsInfoParts[5].substring(5,7));
			double lngDeg = Double.parseDouble(ngsInfoParts[6].substring(1,4));
			double lngMin = Double.parseDouble(ngsInfoParts[6].substring(4,6));
			double lngSec = Double.parseDouble(ngsInfoParts[6].substring(6,8));		
			double lat = latDeg + latMin/60 + latSec/3600;
			double lng = lngDeg + lngMin/60 + lngSec/3600;
			if(ngsInfoParts[5].substring(0,1).equals("S")) lat = -lat;
			if(ngsInfoParts[6].substring(0,1).equals("W")) lng = -lng;
			ngsParam.latitude = lat;
			ngsParam.longitude = lng;		
			ngsParam.pid = pid;
			ngsParam.designation = designation;			
		}
		return ngsParam;		
	}
	
	
	
	//gettters
	public String getTagName() {
		return tagName;
	}
	
	public String getTagText() {
		return tagText;
	}
	
	public ArrayList<NgsParameters> getLatLntList() {
		return latLngList;
	}

}
