package org.zeroxlab.benchmark;

import android.util.Log;

import com.nea.nehe.lesson16.Run;
import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

public class CaseNeheLesson16 extends Case {

    public static int mNeheRepeat = 2;
    public static int mNeheRound  = 1000;

    CaseNeheLesson16() {
	super("NeheLesson16", Run.FullName, mNeheRepeat, mNeheRound);
    }

    public String getTitle() {
	return "OpenGL Fog";
    }

    public String getDescription() {
	return "A very famous OpenGL tutorial to demo OpenGL fog";
    }

    @Override
    public String getBenchmark() {
	if (!couldFetchReport()) {
	    return "Nehe Lesson 16 has no report";
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
