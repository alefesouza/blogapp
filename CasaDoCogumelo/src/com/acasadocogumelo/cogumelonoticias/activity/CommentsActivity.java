package com.acasadocogumelo.cogumelonoticias.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.acasadocogumelo.cogumelonoticias.R;
import com.acasadocogumelo.cogumelonoticias.fragment.CommentsFragment;
import com.acasadocogumelo.cogumelonoticias.lib.SlidingTabLayout;

@SuppressLint({"DefaultLocale","CutPasteId"})
@SuppressWarnings("deprecation")
public class CommentsActivity extends AppCompatActivity {
	final Context context = this;
	public Toolbar mToolbar;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor,
	titulo;
	public static String url;
	FragmentStatePagerAdapter TabAdapter;

	private NavigationAdapter mPagerAdapter;
	private ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		iconcolor = preferences.getString("prefIconColor", "branco");
		if (!iconcolor.equals("branco")) {
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

		url = getIntent().getStringExtra("url");

		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
		slidingTabLayout.setDistributeEvenly(true);
		slidingTabLayout.setViewPager(mPager);
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

	/**
	*This adapter provides two types of fragments as an example.
	*{@linkplain #createItem(int)} should be modified if you use this example for your app.
	 */
	private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

		private static final String[]TITLES = new String[]{
			"Facebook",
			"Google"
		};

		public NavigationAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		protected Fragment createItem(int position) {
			Fragment f;
			final int pattern = position % 2;
			switch (pattern) {
			case 0:
				Bundle bundlef = new Bundle();
				bundlef.putString("url", "https://www.facebook.com/plugins/comments.php?api_key=&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2FNM7BtzAR8RM.js%3Fversion%3D41%23cb%3Df2a641b004%26domain%3Dwww.acasadocogumelo.com%26origin%3Dhttp%253A%252F%252Fwww.acasadocogumelo.com%252Ff118e9bd1c%26relation%3Dparent.parent&href=" + url + "&locale=pt_BR&numposts=10&sdk=joey");
				Fragment facebook = new CommentsFragment();
				facebook.setArguments(bundlef);
				f = facebook;
				break;
			case 1:
				Bundle bundleg = new Bundle();
				bundleg.putString("url", "https://apis.google.com/u/0/_/widget/render/comments?usegapi=1&href=" + url + "&first_party_property=BLOGGER#_methods=onPlusOne%2C_ready%2C_close%2C_open%2C_resizeMe%2C_renderstart%2Concircled%2Cdrefresh%2Cerefresh%2Cscroll%2Copenwindow&id=I0_1421023212687&parent=http%3A%2F%2Facasadocogumelo.com");
				Fragment google = new CommentsFragment();
				google.setArguments(bundleg);
				f = google;
				break;
			default:
				Bundle bundlefd = new Bundle();
				bundlefd.putString("url", "https://www.facebook.com/plugins/comments.php?api_key=&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2FNM7BtzAR8RM.js%3Fversion%3D41%23cb%3Df2a641b004%26domain%3Dwww.acasadocogumelo.com%26origin%3Dhttp%253A%252F%252Fwww.acasadocogumelo.com%252Ff118e9bd1c%26relation%3Dparent.parent&href=" + url + "&locale=pt_BR&numposts=10&sdk=joey");
				Fragment facebookd = new CommentsFragment();
				facebookd.setArguments(bundlefd);
				f = facebookd;
				break;
			}
			return f;
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}
	}

	public void onResume() {
		ActionBarColor(titulo);
		supportInvalidateOptionsMenu();
		super.onResume();
	}
}
