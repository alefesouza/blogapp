package net.aloogle.dropandoideias;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import net.aloogle.dropandoideias.R;
import com.parse.ParseAnalytics;

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
				if (Build.VERSION.SDK_INT < 14) {
					Intent intent = new Intent(SplashScreen.this, WebViewActivity.class);
					if(getIntent().hasExtra("url")) {
						intent.putExtra("url", getIntent().getStringExtra("url"));
					}
					startActivity(intent);
				} else {
					Intent intent = new Intent(SplashScreen.this, net.aloogle.dropandoideias.v14.WebViewActivity.class);
					if(getIntent().hasExtra("url")) {
						intent.putExtra("url", getIntent().getStringExtra("url"));
					}
					startActivity(intent);
				}
				SplashScreen.this.finish();
			}
		}, TIME);
	}
}