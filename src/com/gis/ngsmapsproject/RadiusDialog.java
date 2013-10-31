package com.gis.ngsmapsproject;

import com.gis.ngsmapsproject.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;



public class RadiusDialog extends DialogFragment {
	
	View view;
	double radius_latitude;
	double radius_longitude;
	double radius;
	boolean radiusToggle;
	int radiusUnit;
	EditText latitudeEditView;
	EditText longitudeEditView;
	EditText radiusEditView;
	Spinner radiusUnitEditView;
	public static double MILES_TO_METERS = 1.60934;
	SharedPreferences radiusDialogSettings;
	
	 
	public interface RadiusDialogListener {
		public void onRadiusDialogClickOk(RadiusDialog dialog);
		public void onRadiusDialogClickCancel(RadiusDialog dialog);		
	}
	
	RadiusDialogListener radiusListener;
	 
	public static RadiusDialog dialogInstance() {
		RadiusDialog dialog = new RadiusDialog();
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.radius_dialog, null);
		builder.setView(view);
		
		radiusDialogSettings = getActivity().getSharedPreferences("Radius_Dialog", getActivity().MODE_PRIVATE);
						
		latitudeEditView = (EditText) view.findViewById(R.id.radius_latitude);
		latitudeEditView.setText(radiusDialogSettings.getString("latitude", ""));
		longitudeEditView = (EditText) view.findViewById(R.id.radius_longitude);
		longitudeEditView.setText(radiusDialogSettings.getString("longitude", ""));
		radiusEditView = (EditText) view.findViewById(R.id.radius);
		radiusEditView.setText(radiusDialogSettings.getString("radius", ""));
		radiusUnitEditView = (Spinner) view.findViewById(R.id.radius_unit); 
		radiusUnitEditView.setSelection(radiusDialogSettings.getInt("radius_unit", 0)); 
						
		builder.setPositiveButton("OK", new RadiusDialogClickListener());
		
		builder.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				radiusListener.onRadiusDialogClickCancel(RadiusDialog.this);				
			}
		});
		
		return builder.create();		
	}
	
	class RadiusDialogClickListener implements OnClickListener {
		
		@Override
		public void onClick(DialogInterface dialog, int id) {
			SharedPreferences.Editor radiusSettingsEditor = radiusDialogSettings.edit();
			if(!latitudeEditView.getText().toString().equalsIgnoreCase("")) {
				radius_latitude = Double.parseDouble(latitudeEditView.getText().toString());
			}			
			radiusSettingsEditor.putString("latitude", latitudeEditView.getText().toString());
			
			if(!longitudeEditView.getText().toString().equalsIgnoreCase("")) {
				radius_longitude = Double.parseDouble(longitudeEditView.getText().toString());
			}
			radiusSettingsEditor.putString("longitude", longitudeEditView.getText().toString());
						
			radiusUnit = radiusUnitEditView.getSelectedItemPosition();
			radiusSettingsEditor.putInt("radius_unit", radiusUnit);			
			
			if(!radiusEditView.getText().toString().equalsIgnoreCase("")) {
				radius = Double.parseDouble(radiusEditView.getText().toString()); 
				if(radiusUnit == 1) { 		//Km unit  (need to convert to miles)
					radius = radius * MILES_TO_METERS;
				}
			}	
			radiusSettingsEditor.putString("radius", radiusEditView.getText().toString());
			
			radiusSettingsEditor.commit();
			radiusListener.onRadiusDialogClickOk(RadiusDialog.this);			
		}		
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			radiusListener = (RadiusDialogListener) activity;
		} catch(ClassCastException c) {
			System.out.println("Impement the interface");
		}		
	}
	
	//Getters
	
	public double getLat() {
		return radius_latitude;
	}
	
	public double getLng() {
		return radius_longitude;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public boolean getRadiusToggle() {
		return radiusToggle;
	}
	
	public int getRadiusUnit() {
		return radiusUnit;
	}
	
	

}
