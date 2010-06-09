package org.zeroxlab.benchmark;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Message;

/* code adapted from Caliper Project */

class MicroBenchmark extends Thread {
    final static int DONE = 0;
    final static int RUNNING = 1;
    final static String STATE = "STATE";

    Handler mHandler;

    String xml;
    String postUrl;
    String apiKey;
    String benchmarkName;

	MicroBenchmark(String _xml, String _postUrl, String _apiKey, String _benchmarkName, Handler h) {
    xml = _xml;
    postUrl = _postUrl;
    apiKey = _apiKey;
    benchmarkName = _benchmarkName;
    mHandler = h;
    }

    private void updateState(int state) {
        Bundle b = new Bundle();
        b.putInt(STATE, state);
        Message msg = mHandler.obtainMessage();
        msg.setData(b);
        mHandler.sendMessage(msg);

        Log.e("bzlog", "set state: " + state);
    }

	public void upload() {
    updateState(RUNNING);
    try {
		URL url = new URL(postUrl + apiKey + "/" + benchmarkName);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);

		OutputStream post = urlConnection.getOutputStream();
		Log.e("bzlog", xml);
		post.write(xml.getBytes());

		int responseCode = urlConnection.getResponseCode();
		Log.e("bzlog", ""+responseCode);

		if (responseCode != 200) {
            updateState(DONE);
            throw new RuntimeException("Connection failed with response code " + responseCode);
        }
    } catch (IOException e) {
        updateState(DONE);
		throw new RuntimeException(e);
    }
    updateState(DONE);

	}

    public void run() {
    upload();
    }
}

