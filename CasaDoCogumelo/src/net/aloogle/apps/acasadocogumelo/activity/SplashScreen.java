package net.aloogle.apps.acasadocogumelo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.parse.ParseAnalytics;
import net.aloogle.apps.acasadocogumelo.R;

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
				if (getIntent().hasExtra("url")) {
					Intent intent = new Intent(SplashScreen.this, PostActivity.class);
					intent.putExtra("url", getIntent().getStringExtra("url"));
					intent.putExtra("id", getIntent().getStringExtra("id"));
					intent.putExtra("titulo", getIntent().getStringExtra("titulo"));
					intent.putExtra("descricao", getIntent().getStringExtra("descricao"));
					intent.putExtra("imagem", getIntent().getStringExtra("imagem"));
					intent.putExtra("fromnotification", true);
					startActivity(intent);
				} else if (getIntent().hasExtra("ispersonalized")) {
					Intent intent = new Intent(SplashScreen.this, FragmentActivity.class);
					intent.putExtra("fragment", 5);
					intent.putExtra("url", getIntent().getStringExtra("url"));
					intent.putExtra("fromnotification", true);
					startActivity(intent);
				} else if(Build.VERSION.SDK_INT == 10) {
					Intent intent = new Intent(SplashScreen.this, net.aloogle.apps.acasadocogumelo.activity.v10.MainActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(SplashScreen.this, MainActivity.class);
					startActivity(intent);
				}
				SplashScreen.this.finish();
			}
		}, TIME);
	}
}
