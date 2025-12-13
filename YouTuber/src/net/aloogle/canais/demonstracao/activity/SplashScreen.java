package net.aloogle.canais.demonstracao.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import com.parse.ParseAnalytics;
import net.aloogle.canais.demonstracao.R;

public class SplashScreen extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			
			Intent intent = new Intent(SplashScreen.this, MainActivity.class);
			intent.putExtra("id", getIntent().getStringExtra("id"));
			intent.putExtra("fromnotification", true);
			startActivity(intent);
		} else if (getIntent().hasExtra("ispersonalized")) {
			Intent intent = new Intent(SplashScreen.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			intent.putExtra("url", getIntent().getStringExtra("url"));
			intent.putExtra("fromnotification", true);
			startActivity(intent);
			finish();
		} else if (!isRoot) {
		} else {
			Intent intent = new Intent(SplashScreen.this, MainActivity.class);
			startActivity(intent);
		}
		SplashScreen.this.finish();
	}
}
