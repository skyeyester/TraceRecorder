package com.example.tracerecorder;

import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
    	
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
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
        Log.i("time:", new Date().toString());
    	if(l == null){
    		Log.i("location:", "Null");
    	}else{
    		Log.i("location:", "Latitude: "+ l.getLatitude());
    		Log.i("location:", "Longitude: "+ l.getLongitude());
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
