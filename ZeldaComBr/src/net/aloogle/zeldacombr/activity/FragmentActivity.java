package net.aloogle.zeldacombr.activity;

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
import net.aloogle.zeldacombr.R;
import android.text.Html;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import net.aloogle.zeldacombr.fragment.*;

public class FragmentActivity extends ActionBarActivity {
	final Context context = this;
	public ImageView iv;
	public RippleDrawable rb;
	public Toolbar mToolbar;
	PreferenceFragment settings = new SettingsFragment();
	Fragment about = new AboutFragment();
	FrameLayout fl;
	SharedPreferences preferences;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.toolbar);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		fl = (FrameLayout)findViewById(R.id.content_frame);

		selectItem(getIntent().getIntExtra("fragment", 0));
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		switch (position) {
		case 0:
			ActionBarColor(this, preferences.getString("prefIconColor", "branco"));
			ft.replace(R.id.content_frame, settings);
			break;
		case 1:
			ft.replace(R.id.content_frame, about);
			break;
		}
		ft.commit();
	}

	public static void ActionBarColor(ActionBarActivity activity, String bw) {
		activity.getSupportActionBar().setTitle(Html.fromHtml(activity.getString(R.string.settings)));
		activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2e7d32")));
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
}
