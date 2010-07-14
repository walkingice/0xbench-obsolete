package org.zeroxlab.benchmark;

import java.lang.Runtime;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

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

    public String TAG = "NativeTester";
    public int UNFINISHED = -999;

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

    public void oneRound() {
        try {
            mServerSocket = new ServerSocket(0);
        } catch (IOException e) {
            Log.d(TAG, "cannot create ServerSocket. " + e.toString());
            interruptTester();
        } 

        mBindPort = mServerSocket.getLocalPort();

        String[] envp = {
            "ZXBENCH_PORT="+mBindPort,
        };
        try {
            P = mRuntime.exec(getCommand(), envp);
        } catch (Exception e) {
            Log.e(TAG, "Cannot execute command: " + getCommand() + ". " + e.toString());
            mNow = 0;
            interruptTester();
        }

        try {
            mClientSocket = mServerSocket.accept();
        } catch (IOException e) {
            Log.e(TAG, "cannot acception incoming connection. " + e.toString());
            interruptTester();
        }

        try {
            xmlOutReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
        } catch (IOException e) {
            Log.e(TAG, "cannot create input stream, lost connection? " + e.toString());
            interruptTester();
        }

        stdOutReader = new BufferedReader(new InputStreamReader(P.getInputStream()));
        stdErrReader = new BufferedReader(new InputStreamReader(P.getErrorStream()));

        updateBuffer stdOutThread = new updateBuffer(stdOutReader, stdOut);
        updateBuffer stdErrThread = new updateBuffer(stdErrReader, stdErr);
        stdOutThread.start();
        stdErrThread.start();

        // wait here
        try {
            P.waitFor();
        } catch (InterruptedException e) {
            Log.d(TAG, "caller inturrupted");
            interruptTester();
        }
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

