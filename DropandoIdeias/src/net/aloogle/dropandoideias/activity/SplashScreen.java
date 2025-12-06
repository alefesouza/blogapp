package net.aloogle.dropandoideias.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.parse.ParseAnalytics;
import net.aloogle.dropandoideias.R;

public class SplashScreen extends Activity {

	private static final int TIME = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		ParseAnalytics.trackAppOpened(getIntent());

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
