package com.mysaver.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.mybitems.items.BatteryItem;
import com.mybitems.items.ErrorItem;
import com.mysaver.utils.Utils;

/**
 * Receiver that collects data related to device's battery
 * 
 * @author Marcel Cunha
 * 
 */
public class BatteryReceiver extends BaseReceiver {

	private final String TAG = "BatteryReceiver";;
	private int batteryLevel;
	private int batteryScale;
	private int isPlugged;
	private int batteryHealth;
	private String batteryTec;
	private int batteryTemp;
	private int batteryVoltage;
	private String mId;
	private int isCharging;
	private int chargePlug;

	public BatteryReceiver(Context context) {
		super(context);
		Log.d(TAG, "Creating BatteryReceiver");
		mId = Utils.getId(context);

	}

	@Override
	public void saveSensor() {
		BatteryItem item = null;
		Log.d(TAG, "saveSensor");
		try {
			item = new BatteryItem(mId);
			item.setBatteryHealth(batteryHealth);
			item.setBatteryLevel(batteryLevel);
			item.setBatteryTec(batteryTec);
			item.setBatteryScale(batteryScale);
			item.setBatteryTemp(batteryTemp);
			item.setBatteryVoltage(batteryVoltage);
			item.setIsPlugged(isPlugged);
			item.setIsCharging(isCharging);
			item.setChargePlug(chargePlug);
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			item = null;
			Log.e(TAG, e.toString());
		}
		addItem(item);
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.d(TAG, "onReceive");
		try {

			// Are we charging / charged?
			isCharging = arg1.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

			// How are we charging?
			chargePlug = arg1.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

			batteryLevel = arg1.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			batteryScale = arg1.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			isPlugged = arg1.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			batteryHealth = arg1.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
			batteryTec = arg1.getExtras().getString(
					BatteryManager.EXTRA_TECHNOLOGY);

			batteryTemp = arg1
					.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
			batteryVoltage = arg1.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
		} catch (Exception e) {
			ErrorItem item = new ErrorItem(mId, e.toString());
			addItem(item);
			Log.e(TAG, e.toString());
		}
		saveSensor();
	}
}
