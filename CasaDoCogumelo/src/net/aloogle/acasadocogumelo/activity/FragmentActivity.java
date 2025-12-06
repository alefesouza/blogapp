package net.aloogle.acasadocogumelo.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import net.aloogle.acasadocogumelo.R;
import net.aloogle.acasadocogumelo.fragment.*;

@SuppressWarnings("deprecation")
public class FragmentActivity extends ActionBarActivity {
	public Toolbar mToolbar;
	PreferenceFragment settings = new SettingsFragment();
	Fragment color = new ColorFragment();
	Fragment about = new AboutFragment();
	Fragment post = new PostFragment();
	Fragment search = new SearchFragment();
	Fragment webview = new WebViewFrag();
	Fragment licenses = new LicensesFragment();
	Fragment category = new CategoryFragment();
	Fragment zoom = new ZoomFragment();
	SharedPreferences preferences;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {}
		else {
			setTheme(R.style.BlackOverflow);
		}
		super.onCreate(savedInstanceState);

		setContentView(R.layout.toolbar);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		selectItem(getIntent().getIntExtra("fragment", 0));
	}

	private void selectItem(int position) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (position) {
		case 0:
			ft.replace(R.id.content_frame, settings);
			break;
		case 1:
			ft.replace(R.id.content_frame, color);
			break;
		case 2:
			ft.replace(R.id.content_frame, about);
			break;
		case 3:
			ft.replace(R.id.content_frame, post);
			break;
		case 4:
			ft.replace(R.id.content_frame, search);
			break;
		case 5:
			ft.replace(R.id.content_frame, webview);
			break;
		case 6:
			ft.replace(R.id.content_frame, licenses);
			break;
		case 7:
			try {
				Bundle bundle = new Bundle();
				bundle.putBoolean("fromtag", true);
				bundle.putString("url", "http://apps.aloogle.net/blogapp/acasadocogumelo/app/json/main.php?label=" + URLEncoder.encode(getIntent().getStringExtra("label"), "UTF-8"));
				bundle.putString("label", getIntent().getStringExtra("label"));
				category.setArguments(bundle);
				ft.replace(R.id.content_frame, category);
			} catch (UnsupportedEncodingException e) {}
			break;
		case 8:
			ft.replace(R.id.content_frame, zoom);
			break;
		}
		ft.commit();
	}
	
	public static void ActionBarColor(ActionBarActivity activity, String title) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {
			activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + title + "</font>"));
			activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
		} else {
			activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + title + "</font>"));
			activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_black);
		}
		String userColor = preferences.getString("prefColor", "padrao");
		if (userColor.equals("padrao")) {
			activity.getSupportActionBar().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.splash_bg));
			activity.findViewById(R.id.frame).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.splash_bg));
		} else {
			activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			activity.findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			FragmentActivity.this.finish();
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			FragmentActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
