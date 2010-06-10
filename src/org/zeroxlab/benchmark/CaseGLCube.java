package org.zeroxlab.benchmark;

import android.util.Log;

import org.zeroxlab.kubench.Kubench;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import java.nio.*;
import java.util.ArrayList;

public class CaseGLCube extends Case{

    public static int CubeRound = 1000;

    public static String mType = "Render";
    public static String mUnit = "3d-fps";
    public static String[] mTags = {};

    CaseGLCube() {
	super("CaseGLCube", Kubench.getFullClassName(), 3, CubeRound);
    }

    public String getTitle() {
	return "OpenGL Cube";
    }

    public String getDescription() {
	return "use OpenGL to draw a magic cube.";
    }

    @Override
    public String getBenchmark() {
	if (!couldFetchReport()) {
	    return "GLCube has no report";
	}

	String result = "";
	long total = 0;
	int length = mResult.length;

	for (int i = 0; i < length; i++) {
	    float fps = mCaseRound / (mResult[i] / 1000f); // milliseconds to seconds
	    result += "Round " + i + ": fps = " + fps + "\n";
	    total  += fps;
	}

	result += "Average: fps = " + ((float)total/length) + "\n";
	return result;
    }


    @Override
    public ArrayList<Scenario> getScenarios () {
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

    Scenario s = new Scenario(getTitle(), mType, mTags, mUnit);
    s.mLog = getBenchmark();
	for (int i = 0; i < mResult.length; i++) {
	    float fps = (float)mCaseRound /  (mResult[i] / 1000f);
	    s.mResults.add(((Float)fps).doubleValue());
	}

    scenarios.add(s);
    return scenarios;
    }

}
