package com.isotope11.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.util.Log;

public class GetsUrlContents {
	protected URL mUrl;
	public static final String TAG = MainListActivity.class.getSimpleName();


	public GetsUrlContents(URL url) {
		mUrl = url;
	}

	public String execute() {
		int responseCode = -1;
		StringWriter writer = new StringWriter();
		try{
			HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
			connection.connect();

			responseCode = connection.getResponseCode();
			Log.i(TAG, "Code: " + responseCode);

			if(responseCode == HttpURLConnection.HTTP_OK){
				InputStream inputStream = connection.getInputStream();
				IOUtils.copy(inputStream, writer, "UTF-8");
			} else {
				Log.i(TAG, "Non-200 response: " + responseCode);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "There was a malformed url.", e);
		} catch (IOException e){
			Log.e(TAG, "There was an IOException.", e);
		}

		return writer.toString();
	}
}
