package org.zeroxlab.benchmark;

import java.util.Date;
import java.util.ArrayList;

class Scenario {
    String mName;
    String mUnit;
    String [] mTags;
    Date mTime;

    ArrayList<Double> mResults;
    String mLog;

    Scenario(String name, String [] tags, String unit) {
    mName = name;
    mTags = tags;
    mUnit = unit;

    mTime = new Date();
    mResults = new ArrayList<Double>();
    }
}

