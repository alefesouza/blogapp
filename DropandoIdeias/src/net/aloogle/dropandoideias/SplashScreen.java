package net.aloogle.dropandoideias;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import net.aloogle.dropandoideias.R;

public class SplashScreen extends Activity {

	private static final int TIME = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (Build.VERSION.SDK_INT < 14) {
					Intent intent = new Intent(SplashScreen.this, WebViewActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(SplashScreen.this, net.aloogle.dropandoideias.v14.WebViewActivity.class);
					startActivity(intent);
				}
				SplashScreen.this.finish();
			}
		}, TIME);
	}
}