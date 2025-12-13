package br.com.vidadesuporte.database.helper;

import br.com.vidadesuporte.database.model.Favorites;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.com.vidadesuporte.*;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;


	// Table Names
	private static final String TABLE_FAVORITES = "favorites";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_CREATED_AT = "created_at";

	// NOTES Table - column nmaes
	private static final String KEY_POSTID = "postid";
	private static final String KEY_JSON = "json";
	private static final String KEY_CONTENT = "content";

	// Table Create Statements
	// Todo table create statement
	private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE "
			+ TABLE_FAVORITES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_POSTID
	+ " TEXT," + KEY_JSON + " MEDIUMTEXT," + KEY_CONTENT + " LONGTEXT," + KEY_CREATED_AT
			+ " DATETIME" + ")";

	public DatabaseHelper(Context context) {
		super(context, context.getString(R.string.app_name).replace(" ", ""), null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_FAVORITES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

		// create new tables
		onCreate(db);
	}

	// ------------------------ "todos" table methods ----------------//

	/*
	 * Creating a todo
	 */
	public long createFavorite(Favorites favorites) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_POSTID, favorites.getPostId());
		values.put(KEY_JSON, favorites.getJson());
		values.put(KEY_CONTENT, favorites.getContent());
		values.put(KEY_CREATED_AT, getDateTime());

		// insert row
		long todo_id = db.insert(TABLE_FAVORITES, null, values);

		return todo_id;
	}

	/*
	 * get single todo
	 */
	public Favorites getFavorite(String todo_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " WHERE "
				+ KEY_POSTID + " = " + todo_id;

		Cursor c = db.rawQuery(selectQuery, null);
		
		if (c != null) {
			if(c.moveToFirst()) {
		
		Favorites td  = new Favorites();
		td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		td.setPostId((c.getString(c.getColumnIndex(KEY_POSTID))));
		td.setJson((c.getString(c.getColumnIndex(KEY_JSON))));
		td.setContent((c.getString(c.getColumnIndex(KEY_CONTENT))));
		td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
		
		return td;
		}
		}
		return null;
	}

	/**
	 * getting all todos
	 * */
	public List<Favorites> getAllFavorites() {
		List<Favorites> favorites = new ArrayList<Favorites>();
		String selectQuery = "SELECT * FROM " + TABLE_FAVORITES;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Favorites td = new Favorites();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				td.setPostId((c.getString(c.getColumnIndex(KEY_POSTID))));
				td.setJson((c.getString(c.getColumnIndex(KEY_JSON))));
				td.setContent((c.getString(c.getColumnIndex(KEY_CONTENT))));
				td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

				// adding to todo list
				favorites.add(td);
			} while (c.moveToNext());
		}
		Log.e(LOG, String.valueOf(favorites.size()));

		return favorites;
	}

	/*
	 * getting todo count
	 */
	public int getFavoriteCount() {
		String countQuery = "SELECT * FROM " + TABLE_FAVORITES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a todo
	 */
	public int updateFavorite(Favorites favorite) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_POSTID, favorite.getPostId());
		values.put(KEY_JSON, favorite.getJson());
		values.put(KEY_CONTENT, favorite.getContent());

		// updating row
		return db.update(TABLE_FAVORITES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(favorite.getId()) });
	}

	/*
	 * Deleting a todo
	 */
	public void deleteFavorite(String tado_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FAVORITES, KEY_POSTID + " = ?",
				new String[] { tado_id });
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
