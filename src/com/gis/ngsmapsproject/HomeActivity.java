package com.gis.ngsmapsproject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity {
	
	GoogleMap gMapFragment;
	private final String GPS_MENU_KEY = "gps_setting";
	boolean gps_menu_setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if(gMapFragment == null) {
	    	gMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    	if(gMapFragment != null) {
	    		gMapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	    		gps_menu_setting = sharedPref.getBoolean(GPS_MENU_KEY, true);	    			    		
	    		gMapFragment.setMyLocationEnabled(gps_menu_setting);	      		
	    	}
	    }	 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
