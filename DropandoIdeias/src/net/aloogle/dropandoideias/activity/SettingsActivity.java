package net.aloogle.dropandoideias.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.preference.PreferenceFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import net.aloogle.dropandoideias.R;
import android.text.Html;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import net.aloogle.dropandoideias.fragment.*;

public class SettingsActivity extends ActionBarActivity {
	final Context context = this;
	public ImageView iv;
	public RippleDrawable rb;
	public Toolbar mToolbar;
	PreferenceFragment settings = new SettingsFragment();
	Fragment color = new ColorFragment();
	FrameLayout fl;
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

		ActionBarColor(this, preferences.getString("prefIconColor", "branco"));

		fl = (FrameLayout)findViewById(R.id.content_frame);

		selectItem(getIntent().getIntExtra("fragment", 0));
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		switch (position) {
		case 0:
			getSupportActionBar().setTitle(getString(R.string.settings));
			ft.replace(R.id.content_frame, settings);
			break;
		case 1:
			getSupportActionBar().setTitle(getString(R.string.settings));
			ft.replace(R.id.content_frame, color);
			break;
		}
		ft.commit();
	}

	public static void ActionBarColor(ActionBarActivity activity, String bw) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		if (bw.equals("branco")) {
			activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + activity.getString(R.string.settings) + "</font>"));
		} else {
			activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + activity.getString(R.string.settings) + "</font>"));
		}

		String userColor = preferences.getString("prefColor", "ff222222");
		if (userColor.equals("fundo")) {
			activity.getSupportActionBar().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.toolbar_bg));
		} else {
			activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			SettingsActivity.this.finish();
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	public void onResume() {
		super.onResume();
		ActionBarColor(this, preferences.getString("prefIconColor", "branco"));
	}
}
