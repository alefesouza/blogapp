package net.aloogle.dropandoideias.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.fragment.CommentsFragment;
import net.aloogle.dropandoideias.other.Other;

@SuppressLint({"DefaultLocale", "CutPasteId"})
public class CommentsActivity extends AppCompatActivity {
	static Context context;
	public Toolbar mToolbar;
	SharedPreferences preferences;
	Editor editor;
	String titulo;
	public static String url, id, blogid;
	FragmentStatePagerAdapter TabAdapter;

	private NavigationAdapter mPagerAdapter;
	private ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getString("prefIconColor", "branco").equals("preto")) {
			setTheme(R.style.BlackOverflow);
		}
		setContentView(R.layout.comments);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		titulo = "Comentários";
		FragmentActivity.ActionBarColor(this, titulo);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		url = getIntent().getStringExtra("url");
		id = getIntent().getStringExtra("id");
		blogid = getString(R.string.blogid);

		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

		TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
		if (preferences.getString("prefIconColor", "branco").equals("preto")) {
			tabLayout.setTabTextColors(getResources().getColor(android.R.color.secondary_text_light), getResources().getColor(android.R.color.primary_text_light));
		} else {
			tabLayout.setTabTextColors(getResources().getColor(android.R.color.secondary_text_dark), getResources().getColor(android.R.color.primary_text_dark));
		}
		tabLayout.setupWithViewPager(mPager);
		tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab p1) {
				mPager.setCurrentItem(p1.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab p1) {}

			@Override
			public void onTabReselected(TabLayout.Tab p1) {}
		});

		mPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
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
			"Facebook", 		"Site"
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
				bundlef.putString("url", "https://www.facebook.com/plugins/comments.php?api_key=&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2FNM7BtzAR8RM.js%3Fversion%3D41%23cb%3Df2a641b004%26domain%3D" + context.getString(R.string.sitename) + "%26origin%3Dhttp%253A%252F%252F" + context.getString(R.string.sitename) + "%252Ff118e9bd1c%26relation%3Dparent.parent&href=" + url + "&locale=pt_BR&numposts=10&sdk=joey");
				Fragment facebook = new CommentsFragment();
				facebook.setArguments(bundlef);
				f = facebook;
				break;
			case 1:
				Bundle bundleg = new Bundle();
				bundleg.putString("url", Other.defaultUrl + "comentarios.php?id=" + id + "&blogid=" + blogid);
				Fragment google = new CommentsFragment();
				google.setArguments(bundleg);
				f = google;
				break;
			default:
				Bundle bundlefd = new Bundle();
				bundlefd.putString("url", "https://www.facebook.com/plugins/comments.php?api_key=&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2FNM7BtzAR8RM.js%3Fversion%3D41%23cb%3Df2a641b004%26domain%3D" + context.getString(R.string.sitename) + "%26origin%3Dhttp%253A%252F%252F" + context.getString(R.string.sitename) + "%252Ff118e9bd1c%26relation%3Dparent.parent&href=" + url + "&locale=pt_BR&numposts=10&sdk=joey");
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
		FragmentActivity.ActionBarColor(this, titulo);
		supportInvalidateOptionsMenu();
		super.onResume();
	}
}
