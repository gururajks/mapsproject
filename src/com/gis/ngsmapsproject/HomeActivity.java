package com.gis.ngsmapsproject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.gis.ngsmapsproject.PidDialog.PidDialogListener;
import com.gis.ngsmapsproject.RadiusDialog.RadiusDialogListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.support.ngsmapsproject.NgsDataDownloader;
import com.support.ngsmapsproject.NgsHtmlParser;
import com.support.ngsmapsproject.NgsParameters;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity implements RadiusDialogListener, PidDialogListener {
	
	GoogleMap gMapFragment;
	private final String GPS_MENU_KEY = "gps_setting";
	private final String CONTROL_TYPE = "control_type";
	public static double EARTH_RADIUS_IN_METERS = 6378.137;
	boolean gps_menu_setting;
	private String retrievalType = "RADIUS";
	private String controlType = "X-0-0";
	private String radius = "1";
	private double radius_doubleForm = 1;
	Circle circle;
	public static double MILES_TO_KILOMETERS = 1.60934;
	TextView radiusText;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);		
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);		
		controlType = sharedPref.getString(CONTROL_TYPE, "X-0-0");
		
		radiusText = (TextView) findViewById(R.id.radiusText);
		if(gMapFragment == null) {
	    	gMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    	if(gMapFragment != null) {
	    		gMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	    		gps_menu_setting = sharedPref.getBoolean(GPS_MENU_KEY, true);	    			    		
	    		gMapFragment.setMyLocationEnabled(gps_menu_setting);	
	    		getMapViewSettings();
	    		//getNgsDataByRadius(42.365647,-71.147171);
	    		//gMapFragment.setOnCameraChangeListener(new MapChangeListener());
	    	}
	    }		
	}

	private void getNgsDataByRadius(double lat, double lng) {
		gMapFragment.clear();
		setProgressBarIndeterminateVisibility(true);
		retrievalType = "RADIUS";
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);		
		controlType = sharedPref.getString(CONTROL_TYPE, "X-0-0");
		LatLng coordinates  = new LatLng(lat ,lng);
		LatLngBounds minMax = minMaxBounds(lat, lng, radius_doubleForm);
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 13);
		//CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(minMax, 2);
		gMapFragment.animateCamera(cameraUpdate); 
		CircleOptions circleOptions = new CircleOptions().center(coordinates).radius(radius_doubleForm * MILES_TO_KILOMETERS * 1000);
		circleOptions.strokeColor(Color.BLUE);	
		circleOptions.strokeWidth(4);
		circle = gMapFragment.addCircle(circleOptions);
		setRadiusText();		
		new DownloadWebpageText(coordinates).execute();		 
	}
	
	
	private LatLngBounds minMaxBounds(double lat, double lng,double radius_doubleForm) {
		LatLng northeast = haversineLatLng(lat, lng, radius_doubleForm, 45);
		LatLng southwest = haversineLatLng(lat, lng, radius_doubleForm, 225);
		//LatLngBounds minMax = new LatLngBounds(southwest, northeast);				
		return null; 
	}
	
	//Uses the haversine formula to calculate the lat lng from a point at a given distance
	//Ref: http://www.movable-type.co.uk/scripts/latlong.html
	private LatLng haversineLatLng(double lat, double lng,double radius_doubleForm, double angle) {
		angle = angle * (Math.PI / 180);		// convert to radians
		double transformedLat = Math.asin(Math.sin(lat) * Math.cos(radius_doubleForm * MILES_TO_KILOMETERS * 1000/ EARTH_RADIUS_IN_METERS) + (Math.cos(lat) * Math.sin(radius_doubleForm * MILES_TO_KILOMETERS * 1000/ EARTH_RADIUS_IN_METERS) * Math.cos(angle)));   
		double transformedLng = lng + Math.atan2(Math.sin(angle) * Math.sin(radius_doubleForm * MILES_TO_KILOMETERS * 1000/ EARTH_RADIUS_IN_METERS) * Math.cos(lat), Math.cos(radius_doubleForm * MILES_TO_KILOMETERS * 1000/ EARTH_RADIUS_IN_METERS) - (Math.sin(lat) * Math.sin(transformedLat)));
		System.out.println("lat: " + transformedLat + " lng: " + transformedLng + " radius" + radius_doubleForm + " angle" + angle);
		return new LatLng(transformedLat, transformedLng);
	}
	
	
	private void getNgsDataByPid(String pid) {
		gMapFragment.clear();
		setProgressBarIndeterminateVisibility(true);
		retrievalType = "PIDS";
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);		
		controlType = sharedPref.getString(controlType, "X-0-0");
		new DownloadWebpageText(pid).execute();
	}
	
	//Takes care of the background threads 
	class DownloadWebpageText extends AsyncTask<LatLng, Integer, ArrayList<NgsParameters>> {
    	String ngsLat ="";
    	String ngsLng =""; 
    	double lat;
    	double lng;
    	String pid = "";
    	
    	public DownloadWebpageText(String pid) {
    		this.pid = pid;
    	}
    	
    	public DownloadWebpageText(LatLng params) {
    		lat = (params).latitude;
			lng = (params).longitude;
		}
    	
    	protected ArrayList<NgsParameters> doInBackground(LatLng... params) {            
    		try {    			
    			if(retrievalType.equals("RADIUS")) {
    				
    				NgsDataDownloader downloader = new NgsDataDownloader(lat,lng, radius, controlType);    				
    				return downloader.downloadUrl(); 
    			}
    			if(retrievalType.equals("PIDS")) {
    				NgsDataDownloader downloader = new NgsDataDownloader(pid, controlType);
    				return downloader.downloadUrl(); 
    			}

    		} catch (IOException e) {
    			setProgressBarIndeterminateVisibility(false);
    			System.out.println("Unable to retrieve web page. URL may be invalid.");
    		}
    		return null;
    	}
    	
		// onPostExecute displays the results of the AsyncTask.        
        protected void onPostExecute(ArrayList<NgsParameters> latLngList) {            	     	    	
        	if(latLngList.size() != 0) {
        		
         		for(int i = 0; i < latLngList.size(); i++) {
        			NgsParameters ngsParam = latLngList.get(i);
        			LatLng monumentLocation = new LatLng(ngsParam.getLatitude(), ngsParam.getLongitude());
        			gMapFragment.addMarker(new MarkerOptions().position(monumentLocation).
        					title("PID: " + ngsParam.getPid()).snippet("Designation: " + ngsParam.getDesignation()));
        			
        			if(retrievalType.equals("PIDS")) {
        				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(monumentLocation, 13);
        				gMapFragment.animateCamera(cameraUpdate);
        			}
        			gMapFragment.setOnInfoWindowClickListener(new InfoWindowClickListener());
        		} 
        		setProgressBarIndeterminateVisibility(false);  
        		Toast.makeText(getApplicationContext(), "Monuments Displayed", Toast.LENGTH_LONG).show();
        	}
        	else {
        		setProgressBarIndeterminateVisibility(false);
        		Toast.makeText(getApplicationContext(), "No Monuments found", Toast.LENGTH_LONG).show();
        	}
        	
        }	

        protected void onCancelled() {
        	
        }        	
    }
	
	class InfoWindowClickListener implements OnInfoWindowClickListener {

		@Override
		public void onInfoWindowClick(Marker marker) {
			String pidFull = marker.getTitle().substring(5);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ngs.noaa.gov/cgi-bin/ds_mark.prl?PidBox="+pidFull)); 
            startActivity(browserIntent);     			
		}		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(gMapFragment != null) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);		
			gps_menu_setting = sharedPref.getBoolean(GPS_MENU_KEY, true);
			gMapFragment.setMyLocationEnabled(gps_menu_setting);
			getMapViewSettings();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getMapViewSettings();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saveMapViewSettings();
	}
	
	public void onStop() {
		super.onStop();
		saveMapViewSettings();
	}
	
	//Gets the last state the application was left off
	private void getMapViewSettings() {
		if(gMapFragment != null) {			
			SharedPreferences mapViewSettings = getSharedPreferences("Map_View", MODE_PRIVATE); 
			float latitude = mapViewSettings.getFloat("mapview_latitude", (float)39.8282);
			float longitude = mapViewSettings.getFloat("mapview_longitude",(float) -98.5795);
			float zoom = mapViewSettings.getFloat("mapview_zoom", 3);
			LatLng coordinates = new LatLng(latitude, longitude);			
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, zoom);
			gMapFragment.moveCamera(cameraUpdate);
		}
	}
	
	//Saves the current state of map view
	private void saveMapViewSettings() {
		if(gMapFragment != null) {
			LatLng mapCenter = gMapFragment.getCameraPosition().target;
			float zoom = gMapFragment.getCameraPosition().zoom;
			SharedPreferences mapViewSettings = getSharedPreferences("Map_View", MODE_PRIVATE); 
			SharedPreferences.Editor mapViewSettingsEditor = mapViewSettings.edit();
			mapViewSettingsEditor.putFloat("mapview_latitude", (float) mapCenter.latitude);
			mapViewSettingsEditor.putFloat("mapview_longitude", (float) mapCenter.longitude);
			mapViewSettingsEditor.putFloat("mapview_zoom",  zoom);
			mapViewSettingsEditor.commit();
		}
	}
	
	    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) { 
		if(item.getItemId() == R.id.radius) {
			if(gMapFragment != null) {
				DialogFragment radiusDialog = new RadiusDialog();
				LatLng mapCenter = gMapFragment.getCameraPosition().target;
				Bundle radiusDialogArgs = new Bundle();
				radiusDialogArgs.putDouble("mapcenter_lat", mapCenter.latitude);
				radiusDialogArgs.putDouble("mapcenter_lng", mapCenter.longitude);
				radiusDialog.setArguments(radiusDialogArgs);			
				radiusDialog.show(getSupportFragmentManager(), "Radius_Dialog");
			}
			else {
				Toast.makeText(this, "Google maps not available", Toast.LENGTH_SHORT).show();
			}
		}
		if(item.getItemId() == R.id.pid) {
			DialogFragment pidDialog = new PidDialog();
			pidDialog.show(getSupportFragmentManager(), "Pid_Dialog");
		} 
		if(item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(getApplicationContext(), Settings.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		}
		if(item.getItemId() == R.id.refresh_button) {
			LatLng mapCenter = gMapFragment.getCameraPosition().target;
			getNgsDataByRadius(mapCenter.latitude, mapCenter.longitude);
		}
		
		return super.onMenuItemSelected(featureId, item);
		
	}

	/*
	 * WHen the map center changes
	 */
	class MapChangeListener implements OnCameraChangeListener {

		@Override
		public void onCameraChange(CameraPosition position) {
			LatLng mapCenter = position.target;
			getNgsDataByRadius(mapCenter.latitude, mapCenter.longitude);
		}
		
	}
	
	
	//Interface implemented methods
	
	@Override
	public void onRadiusDialogClickOk(RadiusDialog dialog) {		
		radius_doubleForm = dialog.getRadius();
		radius = String.valueOf(dialog.getRadius());
		LatLng radiusCenter = new LatLng(dialog.getLat(), dialog.getLng());
		CircleOptions circleOptions = new CircleOptions().center(radiusCenter).radius(radius_doubleForm * MILES_TO_KILOMETERS * 1000);
		circleOptions.strokeColor(Color.BLUE);	
		circleOptions.strokeWidth(4);
		circle = gMapFragment.addCircle(circleOptions);
		
		if(dialog.getLat() != 0 && dialog.getLng() != 0) {
			getNgsDataByRadius(dialog.getLat(), dialog.getLng());
		}
		
	} 
	
	private void setRadiusText() {
		radiusText.setText("Radius: " + radius + " miles"); 
	}

	@Override
	public void onRadiusDialogClickCancel(RadiusDialog dialog) {}

	@Override
	public void onPidDialogClickOk(PidDialog dialog) {
		String pid = dialog.getPid();
		getNgsDataByPid(pid);
	}
 
	@Override
	public void onPidDialogClickCancel(PidDialog dialog) {}

}
