package com.gis.ngsmapsproject;

import com.gis.ngsmapsproject.RadiusDialog.RadiusDialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

public class PidDialog extends DialogFragment {
	
	public int EDITTEXT_PID = 111;
	private String pidValue;
	EditText pidEntry;
	PidDialogListener pidListener;
	SharedPreferences pidDialogSettings;
	
	public interface PidDialogListener {
		public void onPidDialogClickOk(PidDialog dialog);
		public void onPidDialogClickCancel(PidDialog dialog);
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstance) {
		AlertDialog.Builder pidDialog = new AlertDialog.Builder(getActivity());
		pidDialogSettings = getActivity().getSharedPreferences("Pid_Dialog", getActivity().MODE_PRIVATE);
		String pidValue = pidDialogSettings.getString("pid", "");
		pidEntry = new EditText(getActivity());
		pidEntry.setId(EDITTEXT_PID);
		pidEntry.setHint("PID");
		pidEntry.setText(pidValue);
		pidDialog.setView(pidEntry);
		
				
		pidDialog.setPositiveButton("OK", new PidDialogOkListener());
		pidDialog.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pidListener.onPidDialogClickCancel(PidDialog.this);
			}
		});
		
		return pidDialog.create();
	}
	
	class PidDialogOkListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			pidValue = pidEntry.getText().toString();
			SharedPreferences.Editor pidDialogSettingsEditor = pidDialogSettings.edit();
			pidDialogSettingsEditor.putString("pid", pidValue);
			pidDialogSettingsEditor.commit();
			pidListener.onPidDialogClickOk(PidDialog.this);
		}
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			pidListener = (PidDialogListener) activity;
		} catch(ClassCastException c) {
			System.out.println("Impement the interface");
		}		
	}
	
	//Getter
	public String getPid() {
		return pidValue;
	}

}
