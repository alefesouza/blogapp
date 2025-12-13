package net.aloogle.dropandoideias.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.database.model.Favorites;
import net.aloogle.dropandoideias.database.model.Jsons;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Versao da base de dados
	private static final int DATABASE_VERSION = 1;

	// Nomes das tabelas
	private static final String TABLE_FAVORITES = "favorites";
	private static final String TABLE_JSONS = "jsons";

	// Nomes comuns das colunas da tabela
	private static final String KEY_ID = "id";
	private static final String KEY_CREATED_AT = "created_at";

	// Nomes das colunas da tabela
	private static final String KEY_POSTID = "postid";
	private static final String KEY_WHAT = "what";
	private static final String KEY_JSON = "json";
	private static final String KEY_CONTENT = "content";

	private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE "
		 + TABLE_FAVORITES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_POSTID
		 + " TEXT," + KEY_JSON + " MEDIUMTEXT," + KEY_CONTENT + " LONGTEXT," + KEY_CREATED_AT
		 + " DATETIME" + ")";

	private static final String CREATE_TABLE_JSONS = "CREATE TABLE "
		 + TABLE_JSONS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WHAT
		 + " TEXT," + KEY_JSON + " MEDIUMTEXT," + KEY_CREATED_AT
		 + " DATETIME" + ")";

	public DatabaseHelper(Context context) {
		super(context, context.getString(R.string.app_name).replace(" ", ""), null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_FAVORITES);
		db.execSQL(CREATE_TABLE_JSONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_JSONS);

		onCreate(db);
	}

	public long createFavorite(Favorites favorites) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_POSTID, favorites.getPostId());
		values.put(KEY_JSON, favorites.getJson());
		values.put(KEY_CONTENT, favorites.getContent());
		values.put(KEY_CREATED_AT, getDateTime());

		long todo_id = db.insert(TABLE_FAVORITES, null, values);

		return todo_id;
	}

	public long createJson(Jsons json) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_WHAT, json.getWhat());
		values.put(KEY_JSON, json.getJson());
		values.put(KEY_CREATED_AT, getDateTime());

		long json_id = db.insert(TABLE_JSONS, null, values);

		return json_id;
	}

	public Favorites getFavorite(String todo_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT*FROM " + TABLE_FAVORITES + " WHERE "
			 + KEY_POSTID + " = " + todo_id;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null) {
			if (c.moveToFirst()) {

				Favorites td = new Favorites();
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

	public Jsons getJson(String what) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT*FROM " + TABLE_JSONS + " WHERE "
			 + KEY_WHAT + " = '" + what + "'";

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null) {
			if (c.moveToFirst()) {

				Jsons td = new Jsons();
				td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				td.setWhat((c.getString(c.getColumnIndex(KEY_WHAT))));
				td.setJson((c.getString(c.getColumnIndex(KEY_JSON))));
				td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

				return td;
			}
		}
		return null;
	}
	
	public List <Favorites> getAllFavorites() {
		List <Favorites> favorites = new ArrayList <Favorites> ();
		String selectQuery = "SELECT*FROM " + TABLE_FAVORITES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Favorites td = new Favorites();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				td.setPostId((c.getString(c.getColumnIndex(KEY_POSTID))));
				td.setJson((c.getString(c.getColumnIndex(KEY_JSON))));
				td.setContent((c.getString(c.getColumnIndex(KEY_CONTENT))));
				td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

				favorites.add(td);
			} while (c.moveToNext());
		}

		return favorites;
	}

	public int getFavoriteCount() {
		String countQuery = "SELECT * FROM " + TABLE_FAVORITES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public int updateFavorite(Favorites favorite) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_POSTID, favorite.getPostId());
		values.put(KEY_JSON, favorite.getJson());
		values.put(KEY_CONTENT, favorite.getContent());

		return db.update(TABLE_FAVORITES, values, KEY_ID + " = ?", 		new String[]{
			String.valueOf(favorite.getId())
		});
	}

	public int updateJson(Jsons json) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_WHAT, json.getWhat());
		values.put(KEY_JSON, json.getJson());

		return db.update(TABLE_JSONS, values, KEY_WHAT + " = ?", 		new String[]{
			String.valueOf(json.getWhat())
		});
	}

	public void deleteFavorite(String postid) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete (TABLE_FAVORITES, KEY_POSTID + " = ?", 		new String[]{
			postid
		});
	}

	public void deleteJson(String what) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete (TABLE_JSONS, KEY_WHAT + " = ?", 		new String[]{
			what
		});
	}

	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
