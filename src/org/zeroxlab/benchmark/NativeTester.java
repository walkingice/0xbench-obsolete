package org.zeroxlab.benchmark;

import java.lang.Runtime;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public abstract class NativeTester extends Tester {

    private TextView mTextView;
    
    private Runtime mRuntime;
    private BufferedReader stdout;
    private BufferedReader stderr;
    private Process P;

    public String TAG = "NativeTester";
    public int UNFINISHED = -999;

    public String mCommand;

    NativeTester() {
        mRuntime = Runtime.getRuntime();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc);
        mTextView = (TextView) findViewById(R.id.myTextView1);
    }

    protected abstract String getCommand();
    @Override
    protected int sleepBeforeStart() {
        return 0;
    };
    @Override
    protected int sleepBetweenRound(){
        return 0;
    };

    public void oneRound() {
        try {
            P = mRuntime.exec(getCommand());
        } catch (Exception e) {
            Log.e(TAG, "Cannot execute");
        }
        stdout = new BufferedReader(new InputStreamReader(P.getInputStream()));
        stderr = new BufferedReader(new InputStreamReader(P.getErrorStream()));

        try {
            P.waitFor();
        } catch (InterruptedException e) {
            Log.d(TAG, "caller inturrupted");
            interruptTester();
        }
        try {
            Log.i(TAG, "stdout: " + getStdOut() );
            Log.i(TAG, "stderr: " + getErrOut() );
        } catch (IOException e) {
            Log.e(TAG, "pipe error: " + e.toString());
        }
        decreaseCounter();
    }

    public boolean isFinish() {
        try {
            P.exitValue();
        } catch (IllegalThreadStateException e) {
            return false;
        }
        return true;
    }

    public int exitValue() {
        try {
            return P.exitValue();
        } catch (IllegalThreadStateException e) {
            return UNFINISHED;
        }
    }

    public void kill() {
        P.destroy();
    }
    
    public String getStdOut () throws IOException {
        String buff = "";
        String line = "";
        while ( (line = stdout.readLine()) != null ) {
            buff = buff + "\n" + line;
        }
        return buff;
    }
    
    public String getErrOut () throws IOException {
        String buff = "";
        String line = "";
        while ( (line = stdout.readLine()) != null ) {
            buff = buff + "\n" + line;
        }
        return buff;
    }

    class TimedReader extends Thread {
        long mLastRead;
        String mBuffer = "";
        TimedReader(InputStreamReader is) {
        }
    }
}

