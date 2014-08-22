package com.example.tracerecorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSRecorder extends Service{
	
	//log period
	private static final int log_period_min =3;
	private static final int log_period_hour =1;
	
	//alarm area
	public double area_center_lat;
	public double area_center_long;
	public double area_radius;
	
	private LocationListener myLocationListener;
	private LocationManager myLocationManager;
	private String PROVIDER = LocationManager.GPS_PROVIDER;
	private Handler handler = new Handler();
	
	//trace log file
	private File outputFile;
	private Writer logfilewriter;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
        handler.postDelayed(showTime, 1000);
    	//Init alarm area, default in Taipei HSR, 5 meter
    	area_center_lat = 25.0484563;
    	area_center_long = 121.5146434;
    	area_radius = 5;
    	//location information
    	myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	//GPS Listener
        myLocationListener = new LocationListener(){
            
        	@Override
        	public void onLocationChanged(Location location) {
        		//check in specific area
        		if(isAlarmArea(location)){
        			//check mode
        			//change to frequent log mode
        		}else{
        			//check mode
        			//change to slow log mode
        		}
        	}
        
        	@Override
        	public void onProviderDisabled(String provider) {
        		// TODO Auto-generated method stub
           
        	}
        
        	@Override
        	public void onProviderEnabled(String provider) {
        		// TODO Auto-generated method stub
           
        	}
        
    		@Override
    		public void onStatusChanged(String provider, int status, Bundle extras) {
    			// TODO Auto-generated method stub
    			
    		}};
    	
    	int minTime = 5000;//ms
    	int minDist = 5;//meter
    	myLocationManager.requestLocationUpdates(PROVIDER, minTime, minDist, myLocationListener);
    	
    	//Log file in rootPath = "/storage/emulated/0/TraceRecorderLog"
    	File rootPath = Environment.getExternalStorageDirectory();
    	File outDir = new File(rootPath.getAbsolutePath() + File.separator + "TraceRecorderLog");

        if (!outDir.isDirectory()) {
          outDir.mkdir();
        }
        // (1) get today's date
        Date today = Calendar.getInstance().getTime();
        // (2) create our date "format"
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss", Locale.TAIWAN);
        // (3) create file
    	String filename = formatter.format(today);
    	outputFile = new File(outDir, filename);
    	Log.i("Test",rootPath.getPath());
    	Log.i("Test",filename);
    	
    	try {
			logfilewriter = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("Error", "Creat log file writer fail");
			e.printStackTrace();
		}
    	
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        try {
        	logfilewriter.flush();;
			logfilewriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //MTP re-scan the directory to show on windows explorer immediately
    	MediaScannerConnection.scanFile(this.getApplicationContext(), new String[] { outputFile.getAbsolutePath() }, null, null);
        super.onDestroy();
    }
    
    private Boolean isAlarmArea(Location current){
    	float[] results=new float[1];
    	Location.distanceBetween(current.getLatitude(), current.getLatitude(), 
    			                          area_center_lat, area_center_long, results);
    	if(results[0] < area_radius){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private void showMyLocation(Location l){
    	String timelog = new Date().toString();
        Log.i("time:", timelog);
    	if(l == null){
    		Log.i("location:", "Null");
    		String log = timelog+","+"null\n";
    		try {
				logfilewriter.write(log);
				logfilewriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else{
    		Log.i("location:", "Latitude: "+ l.getLatitude());
    		Log.i("location:", "Longitude: "+ l.getLongitude());
    		String log = timelog+","+l.getLatitude()+","+l.getLongitude()+"\n";
    		try {
				logfilewriter.write(log);
				logfilewriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    		   
    }
	
    private Runnable showTime = new Runnable() {
        public void run() {
        	//get last known location, if available
        	Location location = myLocationManager.getLastKnownLocation(PROVIDER);
  
        	//check in specific period
            //log every one second
        	showMyLocation(location);
            handler.postDelayed(this, 1000);
        }
    };
}
