package com.nea.nehe.lesson16;

import org.zeroxlab.benchmark.Tester;

import android.app.Activity;
import android.os.Bundle;

/**
 * The initial Android Activity, setting and initiating
 * the OpenGL ES Renderer Class @see Lesson16.java
 *
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class Run extends Tester {

	public final static String FullName = "com.nea.nehe.lesson16.Run";

	/** Our own OpenGL View overridden */
	private Lesson16 lesson16;

	@Override
	public String getTag() {
	    return "Nehe16";
	}

	@Override
	public int sleepBeforeStart() {
	    return 1200; //1.2 second
	}

	@Override
	public int sleepBetweenRound() {
	    return 1000; // let gl run by itself, check periodically
	}

	@Override
	protected void oneRound() {
//	    lesson16.requestRender();
	}

	/**
	 * Initiate our @see Lesson16.java,
	 * which is GLSurfaceView and Renderer
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Initiate our Lesson with this Activity Context handed over
		lesson16 = new Lesson16(this);
		lesson16.setSpeedAndTester(1, 1, this);
		//Set the lesson as View to the Activity
		setContentView(lesson16);
		startTester();
	}

	/**
	 * Remember to resume our Lesson
	 */
	@Override
	protected void onResume() {
		super.onResume();
		lesson16.onResume();
	}

	/**
	 * Also pause our Lesson
	 */
	@Override
	protected void onPause() {
		super.onPause();
		lesson16.onPause();
	}

}
