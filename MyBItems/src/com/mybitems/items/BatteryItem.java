package com.mybitems.items;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.BatteryManager;

/**
 * Item representing the battery sensor of phones.
 * 
 * @author Marcel Cunha
 * 
 */
public class BatteryItem extends BaseItem {

	/** Indicate the battery level */
	private int mBatteryLevel;
	/** Indicate the battery scale */
	private int mBatteryScale;
	/** Indicate if the battery is plugged or not */
	private int mIsPlugged;
	/** Indicate the battery health */
	private int mBatteryHealth;
	/** Indicate the battery technology */
	private String mBatteryTec;
	/** Indicate the battery temperature */
	private int mBatteryTemp;
	/** Indicate the battery voltage */
	private int mBatteryVoltage;
	/** Indicate the timestamp for the created object */
	private long mTimestamp;
	/** Indicate the charge plug of the device */
	private int chargePlug;
	/** Indicate if the device is currently charging */
	private int isCharging;
	/** Indicate the id of the user */
	private String mId;

	public static final String TABLE_NAME = "batteryItem";

	public BatteryItem(String id) {
		mId = id;
		mTimestamp = System.currentTimeMillis();
	}

	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}

	@Override
	public long getTimestamp() {
		return mTimestamp;
	}

	public int getBatteryLevel() {
		return mBatteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.mBatteryLevel = batteryLevel;
	}

	@Override
	public int getType() {
		return ItemType.BATTERY.ordinal();
	}

	public int getBatteryScale() {
		return mBatteryScale;
	}

	public void setBatteryScale(int batteryScale) {
		this.mBatteryScale = batteryScale;
	}

	public int getIsPlugged() {
		return mIsPlugged;
	}

	public void setIsPlugged(int isPlugged) {
		this.mIsPlugged = isPlugged;
	}

	public int getBatteryHealth() {
		return mBatteryHealth;
	}

	public void setBatteryHealth(int batteryHealth) {
		this.mBatteryHealth = batteryHealth;
	}

	public String getBatteryTec() {
		return mBatteryTec;
	}

	public void setBatteryTec(String batteryTec) {
		this.mBatteryTec = batteryTec;
	}

	public int getBatteryTemp() {
		return mBatteryTemp;
	}

	public void setBatteryTemp(int batteryTemp) {
		this.mBatteryTemp = batteryTemp;
	}

	public int getBatteryVoltage() {
		return mBatteryVoltage;
	}

	public void setBatteryVoltage(int batteryVoltage) {
		this.mBatteryVoltage = batteryVoltage;
	}

	public void setChargePlug(int plug) {
		chargePlug = plug;
	}

	public void setIsCharging(int charging) {
		isCharging = charging;
	}

	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimestamp);
		object.put(ApplicationConstants.level, mBatteryLevel);
		object.put(ApplicationConstants.scale, mBatteryScale);
		object.put(ApplicationConstants.plugged, mIsPlugged);
		object.put(ApplicationConstants.health, mBatteryHealth);
		object.put(ApplicationConstants.tec, mBatteryTec);
		object.put(ApplicationConstants.voltage, mBatteryVoltage);
		object.put(ApplicationConstants.temp, mBatteryTemp);
		object.put(ApplicationConstants.charging, isCharging);
		object.put(ApplicationConstants.plug, chargePlug);

		return object;
	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = object.getString(ApplicationConstants.id);
			mTimestamp = object.getLong(ApplicationConstants.timestamp);
			mBatteryLevel = object.getInt(ApplicationConstants.level);
			mBatteryScale = object.getInt(ApplicationConstants.scale);
			mIsPlugged = object.getInt(ApplicationConstants.plugged);
			mBatteryHealth = object.getInt(ApplicationConstants.health);
			mBatteryVoltage = object.getInt(ApplicationConstants.voltage);
			mBatteryTemp = object.getInt(ApplicationConstants.temp);
			isCharging = object.getInt(ApplicationConstants.charging);
			chargePlug = object.getInt(ApplicationConstants.plug);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			mBatteryTec = object.getString(ApplicationConstants.tec);
		} catch (JSONException e2) {
			mBatteryTec = "Not found";
		}
	}

	@Override
	/** 
	 * We will consider that the battery is being used if it is not currently charging. 
	 * By doing this, later we can create a function o discharging rate, prediction of how long it will last.
	 */
	public boolean isUsed() {
		return isCharging != BatteryManager.BATTERY_STATUS_CHARGING;
	}

	@Override
	public String createInsert() {
		String insert = " ('" + mId + "'," + mBatteryLevel + ","
				+ mBatteryScale + "," + mIsPlugged + "," + mBatteryHealth
				+ ",'" + mBatteryTec + "'," + mBatteryTemp + ","
				+ mBatteryVoltage + "," + chargePlug + "," + isCharging + ","
				+ mTimestamp + ")";
		return insert;
	}

}
