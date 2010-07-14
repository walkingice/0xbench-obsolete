package org.zeroxlab.benchmark;

import java.lang.Runtime;
import java.lang.IllegalThreadStateException;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Math;

import java.net.Socket;
import java.net.ServerSocket;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.widget.TextView;

public abstract class NativeTester extends Tester {

    private TextView mTextView;
    
    private Runtime mRuntime;
    private Process P;

    public final String TAG = "NativeTester";
    public final String PING_MSG = "PING";
    public final String ENV_VAR = "ZXBENCH_PORT";

    public final int CHECK_FREQ = 500;
    public final int IDLE_KILL = 1000 * 60;

    public String mCommand;
    public Handler mHandler;
    public static final int GUINOTIFIER = 0x1234;

    private BufferedReader stdOutReader;
    private BufferedReader stdErrReader;

    private StringBuffer stdOut = new StringBuffer("stdout:\n");
    private StringBuffer stdErr = new StringBuffer("stderr:\n");

    private ServerSocket mServerSocket;
    private Socket mClientSocket = null;
    private int mBindPort = -1;
    private BufferedReader xmlOutReader;

    NativeTester() {
        mRuntime = Runtime.getRuntime();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc);
        mTextView = (TextView) findViewById(R.id.myTextView1);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GUINOTIFIER:
                        mTextView.setText(stdErr.toString() + stdOut.toString());
                        break;
                }
            }
        };
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

    private void reportOutputs() {
        Log.i(TAG, "stdout: " + stdOut );
        Log.i(TAG, "stderr: " + stdErr );

        try {
            String line;
            while ( (line = xmlOutReader.readLine()) != null ) {
                Log.i(TAG, "XML: " + line);
            }
        } catch (IOException e) {
            Log.e(TAG, "error reading xml from buffer. " + e.toString());
        }

        SystemClock.sleep(5000); //remove me
    }

    public void oneRound() {
        try {
            mServerSocket = new ServerSocket(0);
        } catch (IOException e) {
            Log.d(TAG, "cannot create ServerSocket. " + e.toString());
            interruptTester();
        } 

        mBindPort = mServerSocket.getLocalPort();

        String[] envp = {
            ENV_VAR + "=" + mBindPort,
        };
        try {
            P = mRuntime.exec(getCommand(), envp);
        } catch (Exception e) {
            Log.e(TAG, "Cannot execute command: " + getCommand() + ". " + e.toString());
            mNow = 0;
            P.destroy();
            interruptTester();
        }

        try {
            mClientSocket = mServerSocket.accept();
        } catch (IOException e) {
            Log.e(TAG, "cannot acception incoming connection. " + e.toString());
            P.destroy();
            interruptTester();
        }

        try {
            xmlOutReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
        } catch (IOException e) {
            Log.e(TAG, "cannot create input stream, lost connection? " + e.toString());
            P.destroy();
            interruptTester();
        }

        stdOutReader = new BufferedReader(new InputStreamReader(P.getInputStream()));
        stdErrReader = new BufferedReader(new InputStreamReader(P.getErrorStream()));

        updateBuffer stdOutThread = new updateBuffer(stdOutReader, stdOut);
        updateBuffer stdErrThread = new updateBuffer(stdErrReader, stdErr);
        stdOutThread.start();
        stdErrThread.start();

        // wait here
        while (true) {
            try {
                P.exitValue();
            } catch (IllegalThreadStateException e) {
                if ( Math.min(stdOutThread.idleTime(), stdErrThread.idleTime()) > IDLE_KILL ) {
                    Log.e(TAG, "Native process idle for over " + IDLE_KILL/60 + " Seconds, killing.");
                    reportOutputs();
                    P.destroy();
                    interruptTester();
                }
                SystemClock.sleep(CHECK_FREQ);
                continue;
            }
            break;
        }

        reportOutputs();
        decreaseCounter();
    }

    public int exitValue() throws IllegalThreadStateException {
        return P.exitValue();
    }

    public void kill() {
        P.destroy();
    }

    class updateBuffer extends Thread {
        long mLastRead;
        BufferedReader is;
        StringBuffer mBuffer;
        final int UNREAD = -1;

        updateBuffer(BufferedReader is, StringBuffer targetBuffer) {
            this.is = is;
            mBuffer = targetBuffer;
            mLastRead = UNREAD;
        }

        public void run() {
            String line;
            try {
                while ( (line = is.readLine()) != null ) {
                    mLastRead = SystemClock.uptimeMillis();
                    if(line.equals(PING_MSG))
                        continue;
                    mBuffer.append(line + '\n');
                    Message m = new Message();
                    m.what = GUINOTIFIER;
                    mHandler.sendMessage(m);
                }
            } catch (IOException e) {
                Log.e(TAG, "update buffer failed. " + e.toString());
            }
        }

        public long idleTime() {
            return SystemClock.uptimeMillis() - mLastRead;
        }
    }
}

