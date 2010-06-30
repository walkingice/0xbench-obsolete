package org.zeroxlab.benchmark;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import org.zeroxlab.benchmark.TesterScimark2;

public class CaseScimark2 extends Case{

    public static String LIN_RESULT = "LIN_RESULT";
    protected Bundle mInfo[];

    public static int Repeat = 1;
    public static int Round  = 1;

    CaseScimark2() {
	super("CaseScimark2", "org.zeroxlab.benchmark.TesterScimark2", Repeat, Round);

    mUnit = "mflops";
    String [] _tmp = {
        "mflops",
        "numeric",
        "scientific",
    };
    mTags = _tmp;

	generateInfo();
    }

    public String getTitle() {
	return "Scimark2";
    }

    public String getDescription() {
	return "SciMark 2.0 is a Java benchmark for scientific and numerical computing. It measures several computational kernels and reports a composite score in approximate Mflops.";
    }

    private void generateInfo() {
	mInfo = new Bundle[Repeat];
	for (int i = 0; i < mInfo.length; i++) {
	    mInfo[i] = new Bundle();
	}
    }

    @Override
    public void clear() {
	super.clear();
	generateInfo();
    }

    @Override
    public void reset() {
	super.reset();
	generateInfo();
    }

    @Override
    public String getBenchmark() {
	if (!couldFetchReport()) {
	    return "No benchmark report";
	}

	String result = "\n";
	for (int i = 0; i < mInfo.length; i++) {
	    result += TesterScimark2.bundleToString(mInfo[i]);
	    result += "\n";
	}
	return result;
    }

    @Override
    public ArrayList<Scenario> getScenarios () {
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

    ArrayList<String> subBenchmarks = new ArrayList<String>();
    subBenchmarks.add(TesterScimark2.COMPOSITE    );
    subBenchmarks.add(TesterScimark2.FFT          );
    subBenchmarks.add(TesterScimark2.SOR          );
    subBenchmarks.add(TesterScimark2.MONTECARLO   );
    subBenchmarks.add(TesterScimark2.SPARSEMATMULT);
    subBenchmarks.add(TesterScimark2.LU           );

    for (int i=0; i<subBenchmarks.size(); i++) {
        String benchName = subBenchmarks.get(i);
        Scenario s = new Scenario(getTitle()+":"+benchName, mTags, mUnit);

        for(int j=0; j<mInfo.length; j++) 
            s.mResults.add(mInfo[j].getDouble(benchName));

        scenarios.add(s);
    }

    return scenarios;
    }

    @Override
    protected boolean saveResult(Intent intent, int index) {
	Bundle info = intent.getBundleExtra(LIN_RESULT);
	if (info == null) {
	    Log.i(TAG, "Weird! cannot find Scimark2Info");
	    return false;
	} else {
	    mInfo[index] = info;
	}

	return true;
    }
}
