/*
 * Copyright (C) 2010 0xlab - http://0xlab.org/
 * Authored by: Joseph Chang (bizkit) <bizkit@0xlab.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensolaris.hub.libmicro;

import org.zeroxlab.benchmark.*;

import android.util.Log;

import android.os.SystemClock;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import org.opensolaris.hub.libmicro.NativeTesterMicro;

public class NativeCaseMicro  extends Case {

    public static String LIN_RESULT = "LIN_RESULT";
    protected Bundle mInfo[];

    public static int Repeat = 1;
    public static int Round  = 1;

    public NativeCaseMicro() {
        super("NativeCaseMicro", "org.opensolaris.hub.libmicro.NativeTesterMicro", Repeat, Round);

        mType = "syscall-nsec";
        String [] _tmp = {
            "syscall", 
        };
        mTags = _tmp;

        generateInfo();
    }

    public String getTitle() {
        return "LibMicro";
    }

    public String getDescription() {
        return "LibMicro is a portable set of microbenchmarks that many Solaris engineers used during Solaris 10 development to measure the performance of various system and library calls.";
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

        return "";
//    return mInfo[0].getString(NativeTesterMicro.REPORT);
    }

    @Override
    public ArrayList<Scenario> getScenarios () {
        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();

        Bundle bundle = mInfo[0]; // only 1 run
        for(String command: NativeTesterMicro.COMMANDS) {
            String name = bundle.getString(command+"S");
            float [] results = bundle.getFloatArray(command+"FA");
            if(name == null || results == null)
                continue;
            Scenario s = new Scenario(name, mType, mTags);
            for(float result: results) 
                s.mResults.add(new Double(result));
            scenarios.add(s);

        }

        return scenarios;
    }

    @Override
    protected boolean saveResult(Intent intent, int index) {
        Bundle info = intent.getBundleExtra(NativeTesterMicro.RESULT);
        if (info == null) {
            Log.i(TAG, "Cannot find LibMicroInfo");
            return false;
        } else {
            mInfo[index] = info;
        }

        return true;
    }
}
