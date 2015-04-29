/* Student name: Declan Moran
 * Student id:0145076
 * Partner name:Alex Bastor-Alvarez
 * Partner id:14127202
 */

package ie.ul.android.lab_week8;


import ie.ul.android.lab_week8.R;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private SensorManager sensorMan;
	private boolean isFreeFalling = false;
	private boolean isPlaying = false;
	private boolean isServiceRunning = false;
	Menu mOptionsMenu;
	
	private static final float FREE_FALLING = 1; // below 1 m/s^2 is free falling  
	private static String FREEFALLINGSNIPPET = "fall.wav";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensorMan = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Sensor accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMan.registerListener(accSensorListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	
	@Override
	protected void onPause() {
		sensorMan.unregisterListener(accSensorListener);
		super.onPause();
		
	}


	final SensorEventListener accSensorListener = new SensorEventListener() {

		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// do nothing
			
		}

		@Override
		public void onSensorChanged(SensorEvent sensorEvent) {
			float values[] = sensorEvent.values;
			
			double MagG = 0;
			for(int i=0; i<3; i++)
			{
				MagG = MagG+Math.pow(values[i], 2);
			}
			MagG = Math.sqrt(MagG);
			if (MagG < FREE_FALLING)
			{
				if(!isFreeFalling)
				{
					isFreeFalling = true;
					playFreeFalling();
				}
			}
			else
			{
				isFreeFalling = false;
			}
		}
		
	};
	
	private void playFreeFalling() {
		//set up MediaPlayer    
		MediaPlayer mp = new MediaPlayer();
		if (!isPlaying) {
			try {
				AssetFileDescriptor descriptor = getAssets().openFd(FREEFALLINGSNIPPET);
			    mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			        descriptor.close();
				mp.prepare();
				mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						isPlaying = false;
						
					}
					
				});
				mp.start();
				isPlaying = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		mOptionsMenu = menu;   
		return super.onCreateOptionsMenu(menu);
	}

	
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		
		//if (menu.equals(mOptionsMenu)) {
			findServiceIfAlreadyRunning();	
			 if(isServiceRunning == true)
	         {
	        	 mOptionsMenu.findItem(R.id.startstop).setTitle(R.string.stop_service);
	        	 //menu.findItem(R.id.startstop).setTitle(R.string.stop_service);
	        	 //isServiceRunning = false;
	         }
	         else {
	        	 mOptionsMenu.findItem(R.id.startstop).setTitle(R.string.start_service);
	        	 //menu.findItem(R.id.startstop).setTitle(R.string.start_service);
	        	 //isServiceRunning = true;
	        	 
	         }
		//}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.startstop:
        	
        	Intent serviceIntent = new Intent(getBaseContext(),FreeFallService.class);
        	
        	findServiceIfAlreadyRunning();	
			 if(isServiceRunning == true)
	         {
	        	 stopService(serviceIntent);
	        	 //isServiceRunning = false;
	         }
	         else
	         {
	        	 startService(serviceIntent);
	        	 //isServiceRunning = true;
	         }
            //break;
        }		
		return super.onMenuItemSelected(featureId, item);
	}

	/**
     * Verifies if the service is already running. If this is 
     * the case, we should update the UI
     */
    private void findServiceIfAlreadyRunning()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(50);

        for (int i = 0; i < runningServices.size(); i++) 
        {
            ActivityManager.RunningServiceInfo runningService = runningServices.get(i);

            if(runningService.service.getClassName().equalsIgnoreCase(FreeFallService.class.getName()))
            {
                isServiceRunning = true;
            }
        }
    }


}
