package com.mysaver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Utility class that has many methods used in others part of the project.
 * 
 * @author Marcel Cunha
 * */
public class Utils {

	private static final String TAG = "Utils";

	private static String ID = null;

	private static String PREF_NAME = "MySaveAppPref";

	private static String USER_ID = "user_id";

	/**
	 * Return the user ID, based on device. This method will create the ID for
	 * the first time, save it on preferences and also on a local variable that
	 * will be accessible for the life cycle of the application.
	 * 
	 * @param context
	 *            the application context
	 * @return the user id
	 */
	public static String getId(Context context) {

		if (TextUtils.isEmpty(ID)) {
			Log.d(TAG, "getId - ID null");

			// First, try to find it in the Shared Prefences, case when we
			// are opening again the application.
			SharedPreferences pref = context.getApplicationContext()
					.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

			ID = pref.getString(USER_ID, null);

			// If there is no User ID, create a new one
			if (ID == null) {
				Log.d(TAG, "getId - ID null on shared");

				try {
					TelephonyManager manager = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					if (manager != null) {
						ID = manager.getDeviceId();
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					// If for some reason we could not retrieve the user ID, use
					// a
					// random ID, and save it to the shared preferences, to the
					// next
					// time
					ID = "" + System.currentTimeMillis();
				}
			}

			// Save the User ID for the next time
			Log.d(TAG, "getId - saving ID : " + ID);
			Editor editor = pref.edit();
			editor.putString(USER_ID, ID);
			editor.commit();
		}
		Log.d(TAG, "getId : " + ID);

		return ID;
	}

	/**
	 * This method checks if WI-FI is connected.
	 * 
	 * @return true if is connected, false otherwise
	 */
	public static boolean isWiFiConnected(Context context) {
		// Get WI-FI information
		final ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		final NetworkInfo wifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi != null) {
			return wifi.isConnected();
		}
		return false;
	}
}
