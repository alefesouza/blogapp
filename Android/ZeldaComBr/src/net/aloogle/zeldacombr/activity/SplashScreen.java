package net.aloogle.zeldacombr.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.parse.ParseAnalytics;
import net.aloogle.zeldacombr.R;

public class SplashScreen extends Activity {

	private static final int TIME = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		ParseAnalytics.trackAppOpened(getIntent());

		if (getIntent().hasExtra("fromnotification")) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
			Editor editor = preferences.edit();
			editor.remove("count");
			editor.commit();
			editor.remove("receivedTitles");
			editor.commit();
		}
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashScreen.this, WebViewActivity.class);
				if (getIntent().hasExtra("url")) {
					intent.putExtra("url", getIntent().getStringExtra("url"));
				}
				startActivity(intent);
				SplashScreen.this.finish();
			}
		}, TIME);
	}
}
