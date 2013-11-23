package co.mojito.challenge10;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final int CONNECTION_TIMEOUT = 1000;
	private static final int DATARETRIEVAL_TIMEOUT = 2000;
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "" + findAllItems().size());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public List<Play> findAllItems() {
		JSONObject serviceResult = WebServiceUtil
				.requestWebService("https://playhouse-fitb.appspot.com/_ah/api/play/v1/play");

		List<Play> foundItems = new ArrayList<Play>(20);

		try {
			Log.i(this.getClass().getName(), serviceResult.names().toString());
			Log.i(this.getClass().getName(), serviceResult.getJSONArray("items").toString());			
			Log.i(this.getClass().getName(), serviceResult.getJSONArray("items").length() + "");
			JSONArray items = serviceResult.getJSONArray("items");

			for (int i = 0; i < items.length(); i++) {
				JSONObject obj = items.getJSONObject(i);
				Play play = new Play(obj.getLong("id"));
				play.setName(obj.optString("name"));
				play.setCountry(obj.optString("country"));
				play.setCompany(obj.optString("company"));
				foundItems.add(play);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return foundItems;
	}

}
