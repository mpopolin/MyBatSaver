package com.mysaver.service;

import org.json.JSONException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mysaver.activity.MainActivity;
import com.mysaver.receivers.AppReceiver;
import com.mysaver.receivers.AudioReceiver;
import com.mysaver.receivers.BaseReceiver;
import com.mysaver.receivers.BatteryReceiver;
import com.mysaver.receivers.BluetoothReceiver;
import com.mysaver.receivers.EventReceiver;
import com.mysaver.receivers.GPSReceiver;
import com.mysaver.receivers.MobileReceiver;
import com.mysaver.receivers.ScreenReceiver;
import com.mysaver.receivers.WifiReceiver;

/**
 * Logger service responsible for creating all receivers and also for starting
 * the alarm manager that will collect the data regarding the sensors every
 * 150000 milliseconds.
 * 
 * @author Marcel Cunha
 * 
 */
public class LoggerService extends Service {

	private final IBinder mConnection = new LocalBinder();

	private final String TAG = "LoggerService";

	private AlarmManager mAlarmManager;

	private PendingIntent mPendingIntent;

	private final int DEF_TYPE = 999;

	private final String MESSAGE_UPDATE = "update";

	private int mCounter = 0;

	private int mCount = 0;

	private static final int MAX_RETRIES = 10;

	private final int NUM_RECEIVERS = 5;

	private BaseReceiver[] mReceivers = new BaseReceiver[NUM_RECEIVERS];

	private final long RETRY_SECS = 30000;

	private boolean mInitiated = false;

	// Receivers that do not use the broadcast manager scheme
	private MobileReceiver mTelephone;
	private GPSReceiver mGPS;
	private AudioReceiver mAudio;
	private AppReceiver mApp;

	public class LocalBinder extends Binder {

		public LoggerService getLoggerService() {
			return LoggerService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");

		try {
			Log.d(TAG, "onStartCommand - update");
			updateSensors();
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
		}

		return Service.START_STICKY;
	}

	// Register the Bluetooth receiver
	private void registerBluetooth() {
		Log.d(TAG, "registerBluetooth");
		BluetoothReceiver bluetooth = new BluetoothReceiver(
				getApplicationContext());
		IntentFilter btfilter = new IntentFilter();
		// The current connection state
		btfilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		// The current power state
		btfilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(bluetooth, btfilter);
		mReceivers[mCounter] = bluetooth;
		mCounter++;
	}

	// Register the Screen receiver
	private void registerScreen() {
		Log.d(TAG, "registerScreen");
		ScreenReceiver screen = new ScreenReceiver(getApplicationContext());
		IntentFilter screenFilter = new IntentFilter();
		screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
		screenFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(screen, screenFilter);
		mReceivers[mCounter] = screen;
		mCounter++;
	}

	// Register the Battery receiver
	private void registerBattery() {
		Log.d(TAG, "registerBattery");
		BatteryReceiver battery = new BatteryReceiver(getApplicationContext());
		IntentFilter batteryFilter = new IntentFilter();
		batteryFilter.addAction("android.intent.action.BATTERY_CHANGED");
		registerReceiver(battery, batteryFilter);
		mReceivers[mCounter] = battery;

	}

	// Register the Event receiver
	private void registerEvent() {
		Log.d(TAG, "registerEvent");
		EventReceiver event = new EventReceiver(getApplicationContext());
		IntentFilter eventFilter = new IntentFilter();
		eventFilter.addAction(MainActivity.LOG_ON_SEVER);
		registerReceiver(event, eventFilter);
		mReceivers[mCounter] = event;
		mCounter++;
	}

	// Register the Wifi receiver
	private void registerWifi() {
		Log.d(TAG, "registerWifi");
		WifiReceiver wifi = new WifiReceiver(getApplicationContext());
		IntentFilter wifiFilter = new IntentFilter();

		// Enabled, disabled, etc..
		wifiFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

		// Connected, disconnected, etc..
		wifiFilter.addAction("android.net.wifi.STATE_CHANGE");

		wifiFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");

		registerReceiver(wifi, wifiFilter);

		mReceivers[mCounter] = wifi;
		mCounter++;
	}

	/**
	 * Create and register all the components of the service
	 */
	public void startService() {
		Log.d(TAG, "startService");

		mReceivers = new BaseReceiver[NUM_RECEIVERS];

		try {

			mCounter = 0;
			// Create receivers that work with broadcast
			registerWifi();
			registerBluetooth();
			registerScreen();
			registerEvent();
			registerBattery();

			// Create receiver that work without broadcast
			mTelephone = new MobileReceiver(getApplicationContext());
			mGPS = new GPSReceiver(getApplicationContext());
			mAudio = new AudioReceiver(getApplicationContext());
			mApp = new AppReceiver(getApplicationContext());

			stopAlarmManager();
			startAlarmManager();

			mInitiated = true;

		} catch (Exception e) {
			mInitiated = false;
			Log.e(TAG, e.toString());
		}

	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind");
		return mConnection;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "onDestroy");
		try {
			for (int i = 0; i < NUM_RECEIVERS; i++) {
				unregisterReceiver(mReceivers[i]);
			}
		} catch (Exception e) {
			Log.e(TAG, "onDestroy : " + e.toString());
		}

		mTelephone = null;
		mGPS = null;
		mAudio = null;
		mApp = null;
	}

	/**
	 * This method is used to start AlarmManager that will handle the time
	 * between actions to create the logs.
	 */
	public void startAlarmManager() {
		try {
			Log.d(TAG, "startAlarmManager");

			final Intent intent = new Intent(getApplicationContext(),
					LoggerService.class);
			intent.putExtra(MESSAGE_UPDATE, true);

			mPendingIntent = PendingIntent.getService(getApplicationContext(),
					DEF_TYPE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			mAlarmManager = (AlarmManager) getApplicationContext()
					.getSystemService(Context.ALARM_SERVICE);

			mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(), RETRY_SECS, mPendingIntent);
		} catch (Exception e) {
			Log.e(TAG, "Start Alarm error : " + e.toString());
		}
	}

	/**
	 * This method is used to stop AlarmManager when the current time is greater
	 * than end time.
	 */
	public void stopAlarmManager() {
		Log.d(TAG, "stopAlarmManager");
		try {
			final AlarmManager alarmManager = (AlarmManager) getApplicationContext()
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(mPendingIntent);
		} catch (Exception e) {
			Log.e(TAG, "Stop Alarm Manager error: " + e.toString());
		}
	}

	/**
	 * Function called every RETRY_SECS seconds to persist the data collected by
	 * the sensors.
	 * 
	 * @throws JSONException
	 *             exception thrown when creating the objects
	 */
	private void updateSensors() throws JSONException {
		Log.d(TAG, "updateSensors");
		try {

			if (!mInitiated) {
				Log.d(TAG, "For some reason the service was not started");
				if (mCount < MAX_RETRIES) {
					mCount++;
					startService();
				} else {
					// TODO: WHaaaaaaat
				}
			} else {

				if (mReceivers != null) {
					Log.d(TAG, "Updating receivers");
					for (int i = 0; i < NUM_RECEIVERS; i++) {
						((BaseReceiver) mReceivers[i]).saveSensor();
					}
				}

				if (mTelephone != null) {
					mTelephone.saveSensor();
				}

				if (mGPS != null) {
					mGPS.saveSensor();
				}

				if (mAudio != null) {
					mAudio.saveSensor();
				}

				if (mApp != null) {
					mApp.saveSensor();
				}

			}

		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
}
