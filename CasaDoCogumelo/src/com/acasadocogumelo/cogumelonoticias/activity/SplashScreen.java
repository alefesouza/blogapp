package com.acasadocogumelo.cogumelonoticias.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.parse.ParseAnalytics;
import com.acasadocogumelo.cogumelonoticias.R;

public class SplashScreen extends Activity {

	private static final int TIME = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		ParseAnalytics.trackAppOpened(getIntent());

		boolean isRoot = true;
		if (!isTaskRoot()) {
			final Intent intent2 = getIntent();
			final String intentAction = intent2.getAction();
			isRoot = intent2.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN);
		}

		if (getIntent().hasExtra("fromnotification")) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
			Editor editor = preferences.edit();
			editor.remove("count");
			editor.commit();
			editor.remove("receivedTitles");
			editor.commit();
		}

		if (getIntent().hasExtra("extraJson")) {
			Intent intent = new Intent(SplashScreen.this, PostActivity.class);
			intent.putExtra("extraJson", getIntent().getStringExtra("extraJson"));
			intent.putExtra("fromnotification", true);
			startActivity(intent);
			finish();
		} else if (getIntent().hasExtra("ispersonalized")) {
			Intent intent = new Intent(SplashScreen.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			intent.putExtra("url", getIntent().getStringExtra("url"));
			intent.putExtra("fromnotification", true);
			startActivity(intent);
			finish();
		} else if(getIntent().hasExtra("widgetpos")) {
			Intent intent = new Intent(SplashScreen.this, com.acasadocogumelo.cogumelonoticias.activity.FragmentActivity.class);
			intent.putExtra("fragment", 7);
			intent.putExtra("fromwidget", true);
			intent.putExtra("label", getIntent().getStringExtra("value"));
			startActivity(intent);
			finish();
		} else if (!isRoot) {
			finish();
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SplashScreen.this, MainActivity.class);
					startActivity(intent);
					SplashScreen.this.finish();
				}
			}, TIME);
		}
	}
}
