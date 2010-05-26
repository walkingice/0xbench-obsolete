package org.itri.teapot;

import org.zeroxlab.benchmark.Tester;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TeapotES extends Tester implements SensorEventListener {

	public final static String FullName = "org.itri.teapot.TeapotES";

	/** Our own OpenGL View overridden */
    private MyGLSurfaceView mGLSurfaceView;

	@Override
	public String getTag() {
	    return "Teapot";
	}

	@Override
	public int sleepBeforeStart() {
	    return 1200; // 1.2 second
	}

	@Override
	public int sleepBetweenRound() {
	    return 15; // 15 ms
	}

	@Override
	protected void oneRound() {
	    mGLSurfaceView.requestRender();
	}
		
	private SensorManager sensorManager;
	private float[] sensorValues;
	private int sensorMode;
	
	public static final int ACCEL_ID = Menu.FIRST;
	public static final int COMPASS_ID = Menu.FIRST + 1;
    
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create our Preview view and set it as the content of our
		// Activity
		mGLSurfaceView = new MyGLSurfaceView(this);
		mGLSurfaceView.setRenderer(new TeapotRenderer(5,1,1,this));
		setContentView(mGLSurfaceView);
		startTester();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorValues = new float[3];
        sensorMode = ACCEL_ID;
		mGLSurfaceView.setSensor(sensorMode);
	}

	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		sensorManager.registerListener(this, 
	        		sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
	        		SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, 
	        		sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
	        		SensorManager.SENSOR_DELAY_NORMAL);
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		sensorManager.unregisterListener(this);
		mGLSurfaceView.onPause();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, ACCEL_ID, 0, "accelerometer");
        menu.add(0, COMPASS_ID, 0, "compass");
        return result;
    }
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		sensorMode  = item.getItemId();
		mGLSurfaceView.setSensor(sensorMode);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		/*
		switch (sensorMode) {
		case ACCEL_ID:
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				sensorValues[0] = (float) (event.values[0] / SensorManager.GRAVITY_EARTH);
				sensorValues[1] = (float) (event.values[1] / SensorManager.GRAVITY_EARTH);
				sensorValues[2] = (float) (event.values[2] / SensorManager.GRAVITY_EARTH);
				mGLSurfaceView.onSensorChanged(sensorValues);
			}
			return;
		case COMPASS_ID:
			if (event.sensor.getType()  == Sensor.TYPE_ORIENTATION) {
				if (Math.abs(event.values[0]) > 10)
					mGLSurfaceView.onSensorChanged(event.values);
			}
			return;
		}
		*/
	}
}
