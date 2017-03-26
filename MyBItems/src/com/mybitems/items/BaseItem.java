package com.mybitems.items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * BaseObject that all the other items inherit. Contains the abstract methods to
 * convert the object to to JSON and to get the type .
 * 
 * @author Marcel Cunha
 */
public abstract class BaseItem {
	public abstract JSONObject toJson() throws JSONException;

	public abstract int getType();

	public abstract void fromJson(JSONObject object);

	public abstract boolean isUsed();

	public abstract String createInsert();

	public abstract long getTimestamp();

	/** Enum to indicate the possible item types that we have on the project */
	public enum ItemType {
		APP, AUDIO, BATTERY, BLUETOOTH, ERROR, GPS, SCREEN, TEL, WIFI;
	}
}
