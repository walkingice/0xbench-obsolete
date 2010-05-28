package org.zeroxlab.benchmark;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/* code adapted from Caliper Project */

class MicroBenchmark {
	public static String upload(String xml, String postUrl, String apiKey, String benchmarkName) {
    try {
		URL url = new URL(postUrl + apiKey + "/" + benchmarkName);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);

		OutputStream post = urlConnection.getOutputStream();
		Log.e("bzlog", xml);
		post.write(xml.getBytes());

		int responseCode = urlConnection.getResponseCode();
		Log.e("bzlog", ""+responseCode);

		if (responseCode == 200) {
			Log.e("bzlog", "View current and previous benchmark results online:");
			BufferedReader in = new BufferedReader(
				new InputStreamReader(urlConnection.getInputStream()));
			Log.e("bzlog", "  " + in.readLine());
			return "";
		}

		Log.e("bzlog", "Posting to " + postUrl + " failed: "
		  + urlConnection.getResponseMessage());
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			Log.e("bzlog", line);
		}

		return "";
    } catch (IOException e) {
		throw new RuntimeException(e);
    }
	}
}

