package org.zeroxlab.benchmark;

import java.util.Date;
import java.util.ArrayList;

public class Scenario {
    String mName;
    String mType;
    String [] mTags;
    Date mTime;

    public ArrayList<Double> mResults;
    String mLog;

    public Scenario(String name, String type, String [] tags) {
    mName = name;
    mType = type;
    mTags = tags;

    mTime = new Date();
    mResults = new ArrayList<Double>();
    }
}

