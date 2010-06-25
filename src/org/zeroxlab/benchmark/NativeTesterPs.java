package org.zeroxlab.benchmark;

import android.os.Bundle;

public class NativeTesterPs extends NativeTester {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTester();
    }

    @Override
    protected String getTag() {
        return "Native PS";
    };
    protected String getCommand() {
        return "/system/bin/ps";
    }
    
}
