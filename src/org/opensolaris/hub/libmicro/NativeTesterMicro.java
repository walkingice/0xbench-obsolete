package org.opensolaris.hub.libmicro;

import org.zeroxlab.benchmark.*;

import android.os.Bundle;

public class NativeTesterMicro extends NativeTester {

    private final String Opts = "-E -C 200 -L -S -W";
    private final String Path = "/system/bin/bench_";

    @Override
    protected String getTag() {
        return "Native PS";
    };
    protected String[] getCommands() {
        String [] _tmp = {
            Path + "getpid " + Opts + " -N getpid -I 5",
            Path + "getenv " + Opts + " -N getenv   -s 100 -I 100",
            Path + "gettimeofday " + Opts + " -N gettimeofday",
            Path + "log " + Opts + " -N log  -I 20",
            Path + "exp " + Opts + " -N exp  -I 20",
            Path + "lrand48 " + Opts + " -N lrand48",
            "sleep 60",
        };
        return _tmp;
    }
    
}
