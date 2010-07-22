package org.zeroxlab.benchmark;

import android.os.Bundle;

public class NativeTesterPs extends NativeTester {

    @Override
    protected String getTag() {
        return "Native PS";
    };
    protected String[] getCommands() {
        String [] _tmp = {
            "/system/bin/hello",
        };
        return _tmp;
    }
    
}
