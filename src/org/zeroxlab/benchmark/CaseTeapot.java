package org.zeroxlab.benchmark;

import android.util.Log;

import org.itri.teapot.TeapotES;
import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

public class CaseTeapot extends Case {

    public static int mTeapotRepeat = 2;
    public static int mTeapotRound  = 1000;

    CaseTeapot() {
	super("Teapot", TeapotES.FullName, mTeapotRepeat, mTeapotRound);
    }

    public String getTitle() {
	return "Rotating Teapot";
    }

    public String getDescription() {
	return "A rotating standard Utah Teapot";
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
	    float fps = mCaseRound / (mResult[i] / 1000); // milliseconds to seconds
	    result += "Round " + i + ": fps = " + fps + "\n";
	    total  += fps;
	}

	result += "Average: fps = " + (total/length) + "\n";
	return result;
    }
}
