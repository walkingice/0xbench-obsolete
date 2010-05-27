package org.itri.teapot;

import org.zeroxlab.benchmark.Tester;

import android.app.Activity;
/*
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
*/
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

//public class TeapotES extends Tester implements SensorEventListener {
public class TeapotES extends Tester {

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
	
	public static final int ACCEL_ID = Menu.FIRST;
	public static final int COMPASS_ID = Menu.FIRST + 1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLSurfaceView = new MyGLSurfaceView(this);
		mGLSurfaceView.setRenderer(new TeapotRenderer(5,1,1,this));
		setContentView(mGLSurfaceView);
		startTester();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}
	
}
