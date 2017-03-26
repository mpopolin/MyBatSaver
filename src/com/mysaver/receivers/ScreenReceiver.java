package com.mysaver.receivers;

/**
 * Receiver for the Screen sensor
 * 
 * @author Marcel Cunha
 * 
 */
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.mybitems.items.ErrorItem;
import com.mybitems.items.ScreenItem;
import com.mysaver.utils.Utils;

public class ScreenReceiver extends BaseReceiver {
	private final String TAG = "ScreenSenseReceiver";

	private PowerManager mPowerManager;
	private ContentResolver mContentResolver;
	private Display mDisplay;

	// Used to create the item
	private float mHeight;
	private float mWidth;
	private int mScreenState;
	private String mId;

	public ScreenReceiver(Context context) {
		super(context);
		Log.d(TAG, "Creating ScreenReceiver");
		try {
			mId = Utils.getId(context);
			// Get the PowerManager
			this.mPowerManager = ((PowerManager) context
					.getSystemService(Context.POWER_SERVICE));

			this.mContentResolver = context.getContentResolver();

			// Get the Window service
			this.mDisplay = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();

			// Create the first item, since we do not know if we will receive
			// any changed notification to update its values
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			this.mHeight = metrics.heightPixels;
			this.mWidth = metrics.widthPixels;

			if (mPowerManager != null && mPowerManager.isScreenOn()) {
				mScreenState = 1;
			} else {
				mScreenState = 0;
			}

			saveSensor();

		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void saveSensor() {
		Log.d(TAG, "saveSensor");
		ScreenItem item = null;
		try {
			item = new ScreenItem(mId);
			item.setBrightness(getBrightness());
			item.setHeight(getDisplayHeight());
			item.setWidth(getDisplayWidth());
			item.setRefreshRate(getDisplayRefreshRate());
			item.setOrientation(getOrientation());
			item.setScreenState(getScreenState());
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			item = null;
			Log.e(TAG, e.toString());
		}
		addItem(item);
	}

	public final int getScreenState() {
		return mScreenState;
	}

	public final float getDisplayHeight() {
		return mHeight;
	}

	public final float getDisplayWidth() {
		return mWidth;
	}

	public final float getDisplayRefreshRate() {
		float rate = 0;
		try {
			if (mDisplay != null) {
				rate = mDisplay.getRefreshRate();
			}
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
			rate = 0;
		}

		return rate;
	}

	public final int getOrientation() {
		int orientation = Surface.ROTATION_0;
		try {
			if (mDisplay != null) {
				orientation = mDisplay.getRotation();
			}
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
			orientation = Surface.ROTATION_0;
		}
		return orientation;
	}

	public final int getBrightness() {
		int brightness = 0;
		try {
			if (mScreenState == 0) {
				brightness = 0;
			} else {

				brightness = android.provider.Settings.System.getInt(
						mContentResolver,
						android.provider.Settings.System.SCREEN_BRIGHTNESS, 0);
			}
			
			Log.e(TAG,"Brightness : " + brightness);
			return brightness;
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
			brightness = 0;
		}
		return brightness;
	}

	public final boolean isScreenOn() {
		if (mScreenState == 1)
			return true;
		return false;
	}

	public void onReceive(Context paramContext, Intent paramIntent) {

		Log.d(TAG, "onReceive");
		try {
			String str = paramIntent.getAction();
			if (str.equals(Intent.ACTION_SCREEN_OFF)) {
				mScreenState = 0;
			}
			if (str.equals(Intent.ACTION_SCREEN_ON)) {
				mScreenState = 1;
			}

			saveSensor();
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(TAG, e.toString());
		}
	}

}