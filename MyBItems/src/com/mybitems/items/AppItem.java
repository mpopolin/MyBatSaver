package com.mybitems.items;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Item representing the apps running on the phone.
 * 
 * @author Marcel Cunha
 * 
 */
public class AppItem extends BaseItem {

	/** Indicate the timestamp for the created object */
	public long mTimeStamp;
	/** Indicate the id of the user */
	private String mId;
	/** The list of apps */
	public List<String> mApps = new ArrayList<String>();

	public String mAppsString;

	public static final String TABLE_NAME = "appItem";

	public AppItem(String id) {
		mId = id;
		mTimeStamp = System.currentTimeMillis();
		mAppsString = null;
	}

	public void setTimestamp(long timestamp) {
		this.mTimeStamp = timestamp;
	}

	@Override
	public long getTimestamp() {
		return mTimeStamp;
	}

	public void setApps(List<String> list) {
		mApps = list;
	}

	public void setAppsString(String string) {
		this.mAppsString = string;
	}

	@Override
	public int getType() {
		return ItemType.APP.ordinal();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		final JSONObject object = new JSONObject();
		object.put(ApplicationConstants.id, mId);
		object.put(ApplicationConstants.timestamp, mTimeStamp);
		object.put(ApplicationConstants.apps, mApps);
		return object;
	}

	@Override
	public void fromJson(JSONObject object) {
		try {
			mId = (object.getString(ApplicationConstants.id));
			mTimeStamp = (object.getLong(ApplicationConstants.timestamp));
			mAppsString = (object.getString(ApplicationConstants.apps));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	/**
	 * If we have some app name logged, we can consider that it was used.
	 */
	public boolean isUsed() {
		if (mApps != null && mApps.size() > 0) {
			return true;
		}
		return false;
	}

	private String getApps() {
		String ret = "";
		if (mAppsString != null) {
			return mAppsString;
		} else {
			if (mApps != null) {
				for (String app : mApps) {
					ret += app + " ";
				}
			}
		}
		return ret;
	}

	@Override
	public String createInsert() {
		String apps = getApps();
		String insert = "('" + mId + "','" + apps + "'," + mTimeStamp + ")";
		return insert;
	}
}
