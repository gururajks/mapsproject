package com.gis.ngsmapsproject;

import com.support.ngsmapsproject.NgsParameters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class InfoWindowClickDialog extends DialogFragment {
	
		
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		AlertDialog.Builder infoDialog = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.infowindow_dialog, null);
		
		infoDialog.setView(view);
		
		
		
		
		return infoDialog.create();
	}
	
	
	
	

}
