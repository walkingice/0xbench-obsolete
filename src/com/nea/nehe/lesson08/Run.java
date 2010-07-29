package com.nea.nehe.lesson08;

import org.zeroxlab.benchmark.Tester;

import android.app.Activity;
import android.os.Bundle;

/**
 * The initial Android Activity, setting and initiating
 * the OpenGL ES Renderer Class @see Lesson08.java
 * 
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class Run extends Tester {

    public final static String FullName = "com.nea.nehe.lesson08.Run";

    /** Our own OpenGL View overridden */
    private Lesson08 lesson08;

    @Override
    public String getTag() {
        return "Nehe08";
    }

    @Override
    public int sleepBeforeStart() {
        return 1200; // 1.2 second
    }

    @Override
    public int sleepBetweenRound() {
        return 1000; // let gl run by it self, check periodically
    }

    @Override
    protected void oneRound() {
//        lesson08.requestRender();
    }

    /**
     * Initiate our @see Lesson08.java,
     * which is GLSurfaceView and Renderer
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Initiate our Lesson with this Activity Context handed over
        lesson08 = new Lesson08(this);
        lesson08.setSpeedAndTester(1, 1, this);
        //Set the lesson as View to the Activity
        setContentView(lesson08);
        startTester();
    }

    /**
     * Remember to resume our Lesson
     */
    @Override
    protected void onResume() {
        super.onResume();
        lesson08.onResume();
    }

    /**
     * Also pause our Lesson
     */
    @Override
    protected void onPause() {
        super.onPause();
        lesson08.onPause();
    }

}

