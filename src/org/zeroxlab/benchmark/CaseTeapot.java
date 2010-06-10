package org.zeroxlab.benchmark;

import android.util.Log;

import org.itri.teapot.TeapotES;
import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;

public class CaseTeapot extends Case {

    public static int mTeapotRepeat = 2;
    public static int mTeapotRound  = 1000;

    public static String mType = "Render";
    public static String mUnit = "3d-fps";
    public static String[] mTags = {};

    CaseTeapot() {
	super("Teapot", TeapotES.FullName, mTeapotRepeat, mTeapotRound);
    }

    public String getTitle() {
	return "Flying Teapot";
    }

    public String getDescription() {
	return "A flying standard Utah Teapot";
    }

    @Override
    public String getBenchmark() {
	if (!couldFetchReport()) {
	    return "Teapot has no report";
	}

	String result = "";
	float total = 0;
	int length = mResult.length;

	for (int i = 0; i < length; i++) {
	    float fps = mCaseRound / (mResult[i] / 1000f); // milliseconds to seconds
	    result += "Round " + i + ": fps = " + fps + "\n";
	    total  += fps;
	}

	result += "Average: fps = " + (total/length) + "\n";
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
