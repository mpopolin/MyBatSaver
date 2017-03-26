package com.mysaver.receivers;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;

import com.mybitems.items.ErrorItem;
import com.mybitems.items.GPSItem;
import com.mysaver.utils.Utils;

/**
 * Receiver for the GPS sensor
 * 
 * @author Marcel Cunha
 * 
 */
public class GPSReceiver extends BaseReceiver implements GpsStatus.Listener {
	private LocationManager mManager;
	private int mStatus;
	private String mId;
	private static final String TAG = "GPSReceiver";

	public GPSReceiver(Context context) {
		super(context);
		try {
			Log.d(TAG, "Creating GPSReceiver");
			mId = Utils.getId(context);

			mManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

			// Create the first item, since we do not know if we will receive
			// any changed notification to update its values
			if (mManager != null) {
				if (mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					mStatus = 1;
				} else
					mStatus = 0;
			}

			saveSensor();
			mManager.addGpsStatusListener(this);

		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void saveSensor() {
		Log.d(TAG, "saveSensor : " + mStatus);
		GPSItem item = new GPSItem(mId);
		item.setmState(mStatus);
		addItem(item);
	}

	@Override
	public void onGpsStatusChanged(int arg0) {
		Log.d(TAG, "GPS Status Changed " + arg0);
		mStatus = arg0;
		saveSensor();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Do nothing

	}

}
