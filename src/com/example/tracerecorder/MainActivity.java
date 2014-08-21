package com.example.tracerecorder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	//location information
        LocationManager myLocationManager;
    	myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //check GPS enable
        boolean enabled = myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
        	  openDialog();
        } 
        
        final TextView servicestate = (TextView) findViewById(R.id.rec_state);
        //check GPSRecorder service state
        if(isServiceRunning(GPSRecorder.class.getName())){
        	//if running set correct status
        	servicestate.setText(R.string.start_rec);
        }
        //for start service
        Button startbutton = (Button) findViewById(R.id.button_start);
        startbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	servicestate.setText(R.string.start_rec);
            	//Start Service
                Intent intent = new Intent(MainActivity.this, GPSRecorder.class);
                startService(intent);
            }
        });
        
        //for stop service
        Button stopbutton = (Button) findViewById(R.id.button_stop);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	servicestate.setText(R.string.stop_rec);
            	//Stop Service
            	Intent intent = new Intent(MainActivity.this, GPSRecorder.class);
                stopService(intent);
            }
        });

    }
    
    private Boolean isServiceRunning(String serviceName) {
		
		 ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		    for (RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
		        if (serviceName.equals(runningServiceInfo.service.getClassName())) {
		        	return true;
		        }
		    }
		    return false;
	}
    
    private void openDialog() {
    	new AlertDialog.Builder(this)
			.setTitle("Alarm")
			.setMessage("Please Enable GPS")
			.setPositiveButton("OK",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
		        	Intent activegps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		        	startActivity(activegps);
				}
			}
		)
		.show();
}
}
