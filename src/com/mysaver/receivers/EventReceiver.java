package com.mysaver.receivers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mybitems.items.ErrorItem;
import com.mysaver.persistence.DatabaseManager;
import com.mysaver.utils.Utils;

/** Receiver responsible for sending the data to the server */
public class EventReceiver extends BaseReceiver {

	// private static final String URL =
	// "http://1.vntfinalnew.appspot.com/loggermarcelfinalnew";
	private static final String NEW_URL = "http://marcel-mybsaver.rhcloud.com/mybservlet";
	private Context mContext;
	private static final String TAG = "EventReceiver";
	private static String mId;

	public EventReceiver(Context context) {
		super(context);
		mContext = context;
		mId = Utils.getId(mContext);
	}

	@Override
	public void onReceive(Context arg0, Intent it) {
		Log.d(TAG, "onReceive");
		if (it != null) {
			try {
				sendDataToServer();
			} catch (Exception e) {
				ErrorItem eitem = new ErrorItem(mId, e.toString());
				addItem(eitem);
				Log.e(getClass().getSimpleName(), e.toString());
			}
		}
	}

	private synchronized void sendDataToServer() {
		try {
			SendDataTask task = new SendDataTask();
			task.execute();
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(getClass().getSimpleName(), e.toString());
		}
	}

	private class SendDataTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			if (Utils.isWiFiConnected(mContext)) {
				RequestQueue queue = Volley.newRequestQueue(mContext);

				Log.d(TAG, "Sending data...");
				final DatabaseManager db = DatabaseManager
						.getInstance(mContext);
				ArrayList<JSONObject> jsonList = null;

				if (db != null) {
					try {
						jsonList = db.convertDatabaseToJSON();
					} catch (JSONException e1) {
						ErrorItem eitem = new ErrorItem(mId, e1.toString());
						addItem(eitem);
						Log.e(getClass().getSimpleName(), e1.toString());
						jsonList = null;
					}
					Log.d(TAG, "Converted : " + jsonList.size());
					if (jsonList != null && jsonList.size() > 0) {
						final JSONArray jsonArray = new JSONArray(jsonList);
						final JSONObject object = new JSONObject();
						try {
							object.put("array", jsonArray);
						} catch (JSONException e1) {
							ErrorItem eitem = new ErrorItem(mId, e1.toString());
							addItem(eitem);
							Log.e(getClass().getSimpleName(), e1.toString());
						}

						Log.d(TAG, "JSON Object: " + object.toString());

						JsonObjectRequest jsObjRequest = new JsonObjectRequest(
								Request.Method.POST, NEW_URL, object,
								new Response.Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) {

										if (response != null) {
											Log.d(TAG, "onResponse : "
													+ response.toString());

											int count = 0;
											try {
												count = response
														.getInt("count");
											} catch (JSONException e) {
												count = 0;
												Log.d(TAG,
														"onError 1 : "
																+ e.toString());
												db.insertArray(jsonArray);
												db.close();
												e.printStackTrace();
											} finally {
												// db.delete(count);
												// db.close();
											}
										}

									}
								}, new Response.ErrorListener() {

									@Override
									public void onErrorResponse(
											VolleyError error) {
										if (error != null) {
											Log.d(TAG,
													"onError 2: "
															+ error.toString());
											db.insertArray(jsonArray);
											db.close();
										}
									}
								});

						queue.add(jsObjRequest);

					}
				}

			} else {
				Log.d(TAG, "Not connected to WiFi");
			}
			return null;
		}
	}

	@Override
	public void saveSensor() {
		// Do nothing

	}

}
