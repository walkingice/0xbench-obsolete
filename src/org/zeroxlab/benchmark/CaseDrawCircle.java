package org.zeroxlab.benchmark;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import java.nio.*;

public class CaseDrawCircle extends Case{

    public static int CircleRound = 300;

    CaseDrawCircle() {
	super("CaseDrawCircle", "org.zeroxlab.graphics.DrawCircle", 3, CircleRound);
    }

    public String getTitle() {
	return "Draw Circle";
    }

    public String getDescription() {
	return "call canvas.drawCircle to draw circle for " + CircleRound + " times";
    }

    @Override
    public String getBenchmark() {
	if (!couldFetchReport()) {
	    return "DrawCircle has no report";
	}

	String result = "";
	long total = 0;
	int length = mResult.length;

	for (int i = 0; i < length; i++) {
	    float second = (mResult[i] / 1000f);
	    float fps = (float)mCaseRound / second; // milliseconds to seconds
	    result += "Round " + i +": fps = " + fps + "\n";
	    total  += fps;
	}

	result += "Average: fps = " + ((float)total/length) + "\n";
	return result;
    }

    @Override
    public String getXMLBenchmark() {
	if (!couldFetchReport()) {
	    return "";
	}

	String result = "";
	long total = 0;
	int length = mResult.length;

	result += "<scenario benchmark=\"Draw Circle\" unit=\"2Dfps\">";
	for (int i = 0; i < length; i++) {
	    float second = (mResult[i] / 1000f);
	    float fps = (float)mCaseRound / second; // milliseconds to seconds
        result += "" + fps + " ";
	}
	result += "</scenario>";

	return result;
    }
}
