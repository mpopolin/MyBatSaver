package com.mysaver.persistence;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mybitems.items.BaseItem;
import com.mysaver.activity.MainActivity;

/**
 * Utility class used to persist locally the data
 * 
 * @author Marcel Cunha
 * 
 * 
 * */

public class DatabaseManager extends SQLiteOpenHelper {

	public static DatabaseManager dbManager;

	private static final String LOG_TAG = "DatabaseManager";

	private static final String DB_NAME = "logger_db";

	// Updated on 04-29-14
	private static final int DB_VERSION = 3;

	private static final String TABLE_NAME = "logs";

	public static final String ID = "_id";

	public static final String TYPE = "type";

	public static final String DATA = "data";

	private final int MAX_SIZE = 200;

	private static Context mContext;

	private long mLastSendToServerTime = 0;

	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TYPE + " INTEGER, " + DATA + " TEXT);";

	public DatabaseManager(final Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public synchronized static DatabaseManager getInstance(Context context) {
		Log.d(LOG_TAG, "getInstance");
		if (dbManager == null) {
			mContext = context;
			dbManager = new DatabaseManager(context);
		}

		return dbManager;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "onCreate");
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "onUpgrade");
		if (newVersion > oldVersion) {
			// Drop the tables
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "onDowngrade");
		if (oldVersion > newVersion) {
			// Drop the tables
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	/**
	 * Insert the array of Base Objects on the local database.
	 * 
	 * @param objects
	 *            the array of base objects
	 * @return the index of the last inserted item. -1 if some error occurred.
	 */
	public synchronized long insert(ArrayList<BaseItem> objects) {
		Log.d(LOG_TAG, "insert : " + objects.size());
		long ret = -1;
		if (objects != null) {
			SQLiteDatabase db = dbManager.getWritableDatabase();
			if (db != null) {
				db.beginTransaction();
				try {
					for (BaseItem object : objects) {
						ContentValues value = new ContentValues();
						value.put(TYPE, object.getType());
						value.put(DATA, object.toJson().toString());
						ret = db.insertOrThrow(TABLE_NAME, null, value);
						Log.d(LOG_TAG, "Inserted : " + ret);
					}
					db.setTransactionSuccessful();
					db.endTransaction();
				} catch (JSONException e) {
					ret = -1;
					Log.e(LOG_TAG, e.toString());
				} finally {
					db.close();
				}
			}
		}

		try {
			if (getCount() > MAX_SIZE
					&& (System.currentTimeMillis() - mLastSendToServerTime > TimeUnit.SECONDS
							.toMillis(10))) {
				mLastSendToServerTime = System.currentTimeMillis();
				sendToServer();
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Error on insert sendToServer");
		}
		return ret;
	}

	/** Send the local data to the remote server */
	public synchronized void sendToServer() {
		Log.d(LOG_TAG, "sendToServer");
		// Send to server
		if (mContext != null) {
			try {
				Log.d(LOG_TAG, "Sending data to server..");
				mContext.sendBroadcast(new Intent(MainActivity.LOG_ON_SEVER));
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}
		}

	}

	/**
	 * Delete an amount of items from the local database
	 * 
	 * @param size
	 *            the amount to be removed.
	 * @return how many items were removed
	 */
	public synchronized void delete(int size) {
		final SQLiteDatabase db = dbManager.getWritableDatabase();
		try {
			String where = "rowid IN (select rowid from " + TABLE_NAME
					+ " limit " + size + " )";
			long i = db.delete(TABLE_NAME, where, null);

			Log.d(LOG_TAG, "Deleted : " + i);
		} catch (Exception e) {
			Log.e(LOG_TAG, "delete error!");
		} finally {
			db.close();
		}
	}

	public synchronized ArrayList<JSONObject> convertDatabaseToJSON()
			throws JSONException {
		Log.d(LOG_TAG, "convertDatabaseToJSON");
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		Cursor cursor = null;
		SQLiteDatabase db = dbManager.getReadableDatabase();
		try {
			if (db != null) {
				cursor = db.query(TABLE_NAME, null, null, null, null, null,
						null, MAX_SIZE +"");
			}

			if (cursor != null && !cursor.isClosed()) {
				int size = cursor.getCount();
				Log.d("Marcel", "Loaded size : " + size);

				while (cursor.moveToNext()) {
					String type = cursor.getString(cursor.getColumnIndex(TYPE));
					String data = cursor.getString(cursor.getColumnIndex(DATA));

					JSONObject object = new JSONObject(data);
					object.put(TYPE, type);
					list.add(object);
				}

				// Clear the database to avoid duplicated items to be sent - if
				// it fails, we should insert later
				delete(size);
			}

		} catch (Exception e) {
			Log.d(LOG_TAG, e.toString());
		} finally {
			db.close();
		}

		return list;
	}

	public synchronized void insertArray(JSONArray jsonArray) {
		if (jsonArray != null) {
			Log.d(LOG_TAG, "insertArray : " + jsonArray.length());
			long ret = -1;
			SQLiteDatabase db = dbManager.getWritableDatabase();
			if (db != null) {
				db.beginTransaction();
				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						if (object != null) {
							Log.d(LOG_TAG, "insertArray Object 1 toString : "
									+ object.toString());
							ContentValues value = new ContentValues();
							value.put(TYPE, object.getString(TYPE));
							object.remove(TYPE);
							Log.d(LOG_TAG, "insertArray Object 2 toString : "
									+ object.toString());
							value.put(DATA, object.toString());
							ret = db.insertOrThrow(TABLE_NAME, null, value);
							Log.d(LOG_TAG, " insertArray Inserted : " + ret);
						}
					}
					db.setTransactionSuccessful();
					db.endTransaction();
				} catch (JSONException e) {
					ret = -1;
					Log.e(LOG_TAG, e.toString());
				} finally {
					db.close();
				}
			}
		}
	}

	public synchronized int getCount() throws JSONException {
		int size = 0;

		Cursor cursor = null;
		SQLiteDatabase db = dbManager.getReadableDatabase();
		try {
			if (db != null) {
				cursor = db.query(TABLE_NAME, null, null, null, null, null,
						null, null);
			}

			if (cursor != null && !cursor.isClosed()) {
				size = cursor.getCount();
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "getCount error");
		}

		Log.d(LOG_TAG, "getCount() : " + size);
		return size;
	}
}
