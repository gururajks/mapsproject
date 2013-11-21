package com.support.ngsmapsproject;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;



@ReportsCrashes(
		formUri="http://www.bugsense.com/api/acra?api_key=d2d665cd", 
		formKey = ""
)
public class CrashReport extends Application{
	 @Override
     public void onCreate() {
         super.onCreate();

         // The following line triggers the initialization of ACRA
         ACRA.init(this);
         
     }
}
