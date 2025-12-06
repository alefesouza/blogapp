package net.aloogle.apps.acasadocogumelo.activity.v10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.google.android.gms.ads.*;
import net.aloogle.apps.acasadocogumelo.R;
import net.aloogle.apps.acasadocogumelo.fragment.CommentsFragment;
import net.aloogle.apps.acasadocogumelo.lib.SlidingTabLayout;

@SuppressLint("DefaultLocale")
@SuppressWarnings("deprecation")
public class CommentsActivity extends ActionBarActivity {
	final Context context = this;
	public Toolbar mToolbar;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor, titulo;
	FragmentStatePagerAdapter TabAdapter;

	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;

	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {}
		else {
			setTheme(R.style.BlackOverflow);
		}
		setContentView(R.layout.comments);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		titulo = "Comentários";
		ActionBarColor(titulo);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (iconcolor.equals("branco")) {
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
		} else {
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_black);
		}

		adView = new AdView(this);
		adView.setAdUnitId("")
		adView.setAdSize(AdSize.SMART_BANNER);

		LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);

		layout.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);

		TabAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
			@Override
			public CharSequence getPageTitle(int position) {
				String title = null;
				if (position == 0) {
					title = "Facebook";
				} else if (position == 1) {
					title = "Google";
				}
				return title;
			}

			@Override
			public Fragment getItem(int i) {
				switch (i) {
				case 0:
					Bundle bundlef = new Bundle();
					bundlef.putString("url", "https://www.facebook.com/plugins/comments.php?href=" + getIntent().getStringExtra("url") + "&locale=pt_BR&numposts=10");
					Fragment facebook = new CommentsFragment();
					facebook.setArguments(bundlef);
					return facebook;
				case 1:
					Bundle bundleg = new Bundle();
					bundleg.putString("url", "https://apis.google.com/u/0/_/widget/render/comments?usegapi=1&href=" + getIntent().getStringExtra("url") + "&first_party_property=BLOGGER#_methods=onPlusOne%2C_ready%2C_close%2C_open%2C_resizeMe%2C_renderstart%2Concircled%2Cdrefresh%2Cerefresh%2Cscroll%2Copenwindow&id=I0_1421023212687&parent=http%3A%2F%2Facasadocogumelo.com");
					Fragment google = new CommentsFragment();
					google.setArguments(bundleg);
					return google;
				}
				return null;
			}

			@Override
			public int getCount() {
				return 2;
			}};
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(TabAdapter);
		mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setViewPager(mViewPager);
	}

	public void ActionBarColor(String title) {
		String userColor = preferences.getString("prefColor", "padrao");
		if (userColor.equals("padrao")) {
			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_bg));
			findViewById(R.id.frame).setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_bg));
		} else {
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}

		String iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {
			getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + title + "</font>"));
		} else {
			getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + title + "</font>"));
		}
		getSupportActionBar().setIcon(R.drawable.ic_toolbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.webview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			CommentsActivity.this.finish();
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CommentsActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	public void onResume() {
		ActionBarColor(titulo);
		supportInvalidateOptionsMenu();
		adView.resume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}
}
