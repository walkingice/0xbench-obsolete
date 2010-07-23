package org.opensolaris.hub.libmicro;

import org.zeroxlab.benchmark.*;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class NativeTesterMicro extends NativeTester {

    public static final String REPORT = "REPORT";
    public static final String RESULT = "RESULT";
    private static final String Opts = "-E -C 200 -L -S -W";
    private static final String Path = "/system/bin/bench_";
    public static final String [] COMMANDS  = {

        Path + "getpid " + Opts + " -N getpid -I 5",
        Path + "getenv " + Opts + " -N getenv   -s 100 -I 100",
        Path + "gettimeofday " + Opts + " -N gettimeofday",
        Path + "log " + Opts + " -N log  -I 20",
        Path + "exp " + Opts + " -N exp  -I 20",
        Path + "lrand48 " + Opts + " -N lrand48",

    };

    
    @Override
    protected String getTag() {
        return "Native Micro";
    };
    protected final String[] getCommands() {
        return COMMANDS;
    }

    @Override
    protected boolean saveResult(Intent intent) {
        Bundle bundle = new Bundle();
//        StringBuilder report = new StringBuilder();
        for (String command: getCommands()) {
//            report.append(mStdErrs.get(command));
//            report.append("---------------------------\n");
//            report.append(mStdOuts.get(command));
//            report.append("---------------------------\n");
            String [] lines = mSockets.get(command).split("\n");
            String name = lines[0].trim().split("\t")[0];
            float [] list = new float[lines.length];
            int i = 0;
            for(String line: lines) {
                String [] sp = line.trim().split("\t");
                if(!name.equals(sp[0]))
                    Log.i(TAG, "Incompatible bench name in socket out: " + name + " v.s. " + sp[0]);
                list[i] = Float.parseFloat(sp[1]);
                i = i+1;
            }
            bundle.putString(command+"S", name);
            bundle.putFloatArray(command+"FA", list);
        }
//        bundle.putString(REPORT, report.toString());
        intent.putExtra(RESULT, bundle);
        return true;
    }
}
