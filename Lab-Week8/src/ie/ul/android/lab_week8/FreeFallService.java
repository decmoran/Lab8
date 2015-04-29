package ie.ul.android.lab_week8;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.view.Menu;

public class FreeFallService extends Service {
	private SensorManager sensorMan;
	private boolean isFreeFalling = false;
	private boolean isPlaying = false;
	private static final float FREE_FALLING = 1; // below 1 m/s^2 is free falling  
	private static String FREEFALLINGSNIPPET = "fall.wav";

	
	@Override
	public void onCreate() {

		super.onCreate();
		sensorMan = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
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
	public void onDestroy() {
		super.onDestroy();
		sensorMan.unregisterListener(accSensorListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// replace below return statement with correct one
		//startBackgroundTask();
		Sensor accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMan.registerListener(accSensorListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		return Service.START_STICKY;
	}

	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
