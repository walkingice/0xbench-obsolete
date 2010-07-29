package org.zeroxlab.benchmark;

import java.lang.Runtime;
import java.lang.IllegalThreadStateException;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.ScrollView;

import android.content.pm.ActivityInfo;


public abstract class NativeTester extends Tester {

    private TextView mTextView;
    private ScrollView mScrollView;
    
    private Runtime mRuntime;
    private Process mProcess;

    public final String TAG = "NativeTester";
    public final String PING_MSG = "PING";
    public final String ENV_VAR = "ZXBENCH_PORT";

    public final int CHECK_FREQ = 1000;
    public final int IDLE_KILL = 1000 * 60;

    public String mCommand;
    public Handler mHandler;
    public static final int GUINOTIFIER = 0x1234;

    public Map<String, String> mStdOuts = new HashMap<String, String>();
    public Map<String, String> mStdErrs = new HashMap<String, String>();
    public Map<String, String> mSockets = new HashMap<String, String>();

    private BufferedReader stdOutReader;
    private BufferedReader stdErrReader;
    private BufferedReader sckOutReader;

    private StringBuilder stdOut = new StringBuilder();
    private StringBuilder stdErr = new StringBuilder();
    private StringBuilder sckOut = new StringBuilder();

    private ServerSocket mServerSocket;
    private Socket mClientSocket = null;
    private int mBindPort = -1;

    public NativeTester() {
        mRuntime = Runtime.getRuntime();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc);
        mTextView = (TextView) findViewById(R.id.myTextView1);
        mScrollView = (ScrollView) findViewById(R.id.myScrollView);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GUINOTIFIER:
                        mTextView.setText("stderr -->\n" + stdErr.toString() + "\nstdout -->\n" + stdOut.toString());
                        mScrollView.post(new Runnable() { 
                            public void run() { 
                                mScrollView.fullScroll(ScrollView.FOCUS_DOWN); 
                            } 
                        }); 
                        break;
                }
            }
        };

        startTester();
    }

    protected abstract String[] getCommands();

    protected abstract boolean saveResult(Intent intent);

    @Override
    protected int sleepBeforeStart() {
        return 0;
    };
    @Override
    protected int sleepBetweenRound(){
        return 0;
    };

    private void reportOutputs() {
        Log.i(TAG, stdOut.toString() );
        Log.i(TAG, stdErr.toString() );
        Log.i(TAG, sckOut.toString() );
    }

    public void oneRound() {
        new ProcessRunner().start();
    }

    public int exitValue() throws IllegalThreadStateException {
        return mProcess.exitValue();
    }

    public void kill() {
        mProcess.destroy();
    }

    class ProcessRunner extends Thread {
        public void run() {
            for(String command: getCommands()) {
                Log.i(TAG, "------------------------ process " + command + " start ------------------------ ");
                try {
                    mServerSocket = new ServerSocket(0);
                } catch (IOException e) {
                    Log.e(TAG, "cannot create ServerSocket. " + e.toString());
                    interruptTester();
                } 
                Log.i(TAG, "server socket created");

                mBindPort = mServerSocket.getLocalPort();

                String[] envp = {
                    ENV_VAR + "=" + mBindPort,
                };
                try {
                    mProcess = mRuntime.exec(command, envp);
                } catch (Exception e) {
                    Log.e(TAG, "Cannot execute command: `" + command + "`. " + e.toString());
                    mNow = 0;
                    interruptTester();
                }
                Log.i(TAG, "command executed");
                stdOutReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                stdErrReader = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
                updateBuffer stdOutThread = new updateBuffer(stdOutReader, stdOut);
                updateBuffer stdErrThread = new updateBuffer(stdErrReader, stdErr);
                stdOutThread.start();
                stdErrThread.start();

                try {
                    mClientSocket = mServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "cannot acception incoming connection. " + e.toString());
                    mProcess.destroy();
                    interruptTester();
                }
                Log.i(TAG, "connection accepted");

                try {
                    sckOutReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                } catch (IOException e) {
                    Log.e(TAG, "cannot create input stream, lost connection? " + e.toString());
                    mProcess.destroy();
                    interruptTester();
                }
                Log.i(TAG, "stream created");

                updateBuffer socketThread = new updateBuffer(sckOutReader, sckOut);
                socketThread.start();

                ProcessMonitor monitor = new ProcessMonitor(stdOutThread, stdErrThread, socketThread);
                monitor.start();
                try {
                    monitor.join();
                } catch (InterruptedException e) {
                    Log.e(TAG, "inturrupted before process monitor joins: " + e.toString());
                    mProcess.destroy();
                    interruptTester();
                }
                reportOutputs();
                mStdOuts.put(command, stdOut.toString());
                mStdErrs.put(command, stdErr.toString());
                mSockets.put(command, sckOut.toString());
                stdOut = new StringBuilder();
                stdErr = new StringBuilder();
                sckOut = new StringBuilder();
                Log.i(TAG, "------------------------ process " + command + " finish ------------------------ ");
            }
            decreaseCounter();
            Log.i(TAG, "counter decreased by 1 to " + mNow);
        }
    }

    class ProcessMonitor extends Thread {
        updateBuffer stdOutThread;
        updateBuffer stdErrThread;
        updateBuffer sckOutThread;
        ProcessMonitor (updateBuffer stdOutThread, updateBuffer stdErrThread, updateBuffer sckOutThread) {
            this.stdOutThread = stdOutThread;
            this.stdErrThread = stdErrThread;
            this.sckOutThread = sckOutThread;
        }
        public void run() {
            int value = -1024;
            while (true) {
                try {
                    value = mProcess.exitValue();
                } catch (IllegalThreadStateException e) {
                    if ( Math.min(Math.min(stdOutThread.idleTime(), stdErrThread.idleTime()), sckOutThread.idleTime()) > IDLE_KILL ) {
                        Log.e(TAG, "Native process idle for over " + IDLE_KILL/60 + " Seconds, killing.");
                        reportOutputs();
                        mProcess.destroy();
                        interruptTester();
                    }
                    SystemClock.sleep(CHECK_FREQ);
                    continue;
                }
                Log.i(TAG, "Process exited with value = " + value);
                try {
                    sckOutThread.join();
                } catch (InterruptedException e) {
                    Log.w(TAG, "socket update thread cannot join");
                }
                try {
                    stdOutThread.join();
                } catch (InterruptedException e) {
                    Log.w(TAG, "stdout update thread cannot join");
                }
                try {
                    stdErrThread.join();
                } catch (InterruptedException e) {
                    Log.w(TAG, "stderr update thread cannot join");
                }
                break;
            }

        }
    }

    class updateBuffer extends Thread {
        long mLastRead;
        BufferedReader is;
        StringBuilder mBuffer;
        final int UNREAD = -1;

        updateBuffer(BufferedReader is, StringBuilder targetBuffer) {
            this.is = is;
            mBuffer = targetBuffer;
            mLastRead = UNREAD;
        }

        public void run() {
            char[] c = new char[36];
            int count;
            try {
                while ( (count = is.read(c,0,36)) >= 0 ) {
                    mLastRead = SystemClock.uptimeMillis();
                    mBuffer.append(c, 0, count);
                    Message m = new Message();
                    m.what = GUINOTIFIER;
                    mHandler.sendMessage(m);
                    SystemClock.sleep(100);
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
