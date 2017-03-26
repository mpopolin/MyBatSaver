package com.mysaver.receivers;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mybitems.items.ErrorItem;
import com.mybitems.items.WiFiItem;
import com.mysaver.utils.Utils;

/**
 * Receiver for the WiFi sensor
 * 
 * @author Marcel Cunha
 * 
 */
public class WifiReceiver extends BaseReceiver {

	private final String LOG_TAG = "WifiReceiver";

	private WifiManager mWifiManager;

	private WifiInfo mInfo;

	// Used to create the item
	/* Indicate the WiFi connection state */
	private int mWiFiConnectionState = NetworkInfo.DetailedState.DISCONNECTED
			.ordinal();
	/* Indicate if the WiFi is enabled or disabled */
	private int mWiFiState = WifiManager.WIFI_STATE_UNKNOWN;
	/* Indicate if the WiFi is enabled or disabled */
	private int mWiFiPreviousState = WifiManager.WIFI_STATE_UNKNOWN;
	/* Indicate if the WiFi MAC address */
	private String mMacAddress;
	/* The user ID */
	private String mId;

	@Override
	public void onReceive(Context arg0, Intent paramIntent) {
		try {
			String str = paramIntent.getAction();
			log("onReceive : " + str);

			// Get the WiFi Scan results
			if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(str)) {

			}

			// Get the supplicant state if needed
			if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(str)) {

			}

			// Get the WiFi states
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(str)) {
				mWiFiState = paramIntent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN);

				mWiFiPreviousState = paramIntent.getIntExtra(
						WifiManager.EXTRA_PREVIOUS_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN);
			}

			// Get the connection state
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(str)) {

				NetworkInfo ninfo = ((NetworkInfo) paramIntent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO));

				NetworkInfo.DetailedState localDetailedState = null;

				if (ninfo != null) {
					localDetailedState = ninfo.getDetailedState();
				}

				if (localDetailedState != null) {

					mWiFiConnectionState = localDetailedState.ordinal();
					log("ConnectionState : " + localDetailedState.toString());

					if (localDetailedState == NetworkInfo.DetailedState.CONNECTED) {
						// If we are connected, we can access more information
						// about the network. As we are not interested yet, do
						// nothing.
					}
				}
			}

			saveSensor();
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(LOG_TAG, e.toString());
		}
	}

	@Override
	public void saveSensor() {
		WiFiItem item = null;
		Log.d(LOG_TAG, "saveSensor");
		try {
			item = new WiFiItem(mId);
			item.setMacAddress(mMacAddress);
			item.setRxBytes(getRxBytes());
			item.setTxBytes(getTxBytes());
			item.setConnectionState(mWiFiConnectionState);
			item.setWiFiPreviousState(mWiFiPreviousState);
			item.setWiFiState(mWiFiState);
			item.setLinkSpeed(getLinkSpeed());
			item.setSinalStrenght(getSignalStrength());
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			item = null;
			Log.e(LOG_TAG, e.toString());
		}

		addItem(item);
	}

	public WifiReceiver(Context context) {
		super(context);
		mId = Utils.getId(context);
		log("Creating WifiReceiver");

		try {
			mWifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
				log("Wifi enabled");

				// Get the WiFi state
				mWiFiState = mWifiManager.getWifiState();
				mWiFiPreviousState = WifiManager.WIFI_STATE_UNKNOWN;

				// Get the info about the wifi interface
				mInfo = mWifiManager.getConnectionInfo();
				if (mInfo != null) {
					SupplicantState state = mInfo.getSupplicantState();
					if (state != null) {
						if ((state.toString().compareTo("COMPLETED") == 0)
								|| (state.toString().compareTo("ASSOCIATED") == 0)
								|| (state.toString().compareTo("INACTIVE") == 0)) {
							mMacAddress = mInfo.getMacAddress();
							mMacAddress = mMacAddress.replace(":", "");
							log("mac : " + mMacAddress);
						}
					}
				}
				saveSensor();
			}
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			Log.e(LOG_TAG, e.toString());
		}
	}

	/**
	 * Get the not mobile (Wifi) TX bytes
	 * 
	 * @return long indicating the tx bytes of WiFi
	 */
	private long getTxBytes() {
		long totalTxBytes = 0;
		long mobileTxBytes = 0;
		try {
			totalTxBytes = TrafficStats.getTotalTxBytes();
			mobileTxBytes = TrafficStats.getMobileTxBytes();

			if ((totalTxBytes == TrafficStats.UNSUPPORTED)
					|| (mobileTxBytes == TrafficStats.UNSUPPORTED))
				return TrafficStats.UNSUPPORTED;
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			log(e.toString());
		}
		return totalTxBytes - mobileTxBytes;
	}

	/**
	 * Get the not mobile (Wifi) RX bytes
	 * 
	 * @return long indicating the tx bytes of WiFi
	 */
	private long getRxBytes() {

		long totalRxBytes = 0;
		long mobileRxBytes = 0;
		try {
			mobileRxBytes = TrafficStats.getMobileRxBytes();
			totalRxBytes = TrafficStats.getTotalRxBytes();

			if ((totalRxBytes == TrafficStats.UNSUPPORTED)
					|| (mobileRxBytes == TrafficStats.UNSUPPORTED))
				return TrafficStats.UNSUPPORTED;
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			log(e.toString());
		}

		return totalRxBytes - mobileRxBytes;
	}

	private void log(String s) {
		Log.d(LOG_TAG, s);
	}

	/**
	 * Get the WiFi signal strength
	 * 
	 * @return the WiFi signal strength
	 */
	public final int getSignalStrength() {
		int signal = -1;
		try {
			if (mInfo != null) {
				signal = mInfo.getRssi();
			}
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			signal = -1;
			return signal;
		}
		return signal;
	}

	public final int getLinkSpeed() {
		int linkSpeed = -1;
		try {
			if (mInfo != null) {
				linkSpeed = mInfo.getLinkSpeed();
			}
		} catch (Exception e) {
			ErrorItem eitem = new ErrorItem(mId, e.toString());
			addItem(eitem);
			linkSpeed = -1;
			return linkSpeed;
		}
		return linkSpeed;
	}

	public final boolean isConnected() {
		if (isEnabled()
				&& (mWiFiConnectionState == NetworkInfo.DetailedState.CONNECTED
						.ordinal())) {
			return true;
		}

		return false;
	}

	public final boolean isEnabled() {
		if (mWiFiState == WifiManager.WIFI_STATE_ENABLED)
			return true;
		return false;
	}

	public final String getMacAddress() {
		return mMacAddress;
	}

}
