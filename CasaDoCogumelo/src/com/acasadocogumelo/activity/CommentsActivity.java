package com.acasadocogumelo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.acasadocogumelo.R;
import com.acasadocogumelo.fragment.CommentsFragment;
import com.acasadocogumelo.lib.SlidingTabLayout;

@SuppressLint({ "DefaultLocale", "CutPasteId" })
@SuppressWarnings("deprecation")
public class CommentsActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {
	final Context context = this;
	public Toolbar mToolbar;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor,
	titulo;
	public static String url;
	FragmentStatePagerAdapter TabAdapter;

	private NavigationAdapter mPagerAdapter;
	private View mToolbarView;
	private TouchInterceptionFrameLayout mInterceptionLayout;
	private ViewPager mPager;
	private int mSlop;
	private boolean mScrolled;
	private ScrollState mLastScrollState;

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

		url = getIntent().getStringExtra("url");

		ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
		mToolbarView = findViewById(R.id.toolbar);
		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

		final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
		findViewById(R.id.pager_wrapper).setPadding(0, getActionBarSize() + tabHeight, 0, 0);

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
		slidingTabLayout.setDistributeEvenly(true);
		slidingTabLayout.setViewPager(mPager);

		ViewConfiguration vc = ViewConfiguration.get(this);
		mSlop = vc.getScaledTouchSlop();
		mInterceptionLayout = (TouchInterceptionFrameLayout)findViewById(R.id.container);
		mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
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
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {}

	@Override
	public void onDownMotionEvent() {}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		if (!mScrolled) {
			// This event can be used only when TouchInterceptionFrameLayout
			// doesn't handle the consecutive events.
			adjustToolbar(scrollState);
		}
	}

	private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
		@Override
		public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
			if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
				// Horizontal scroll is maybe handled by ViewPager
				return false;
			}

			Scrollable scrollable = getCurrentScrollable();
			if (scrollable == null) {
				mScrolled = false;
				return false;
			}

			// If interceptionLayout can move, it should intercept.
			// And once it begins to move, horizontal scroll shouldn't work any longer.
			int toolbarHeight = mToolbarView.getHeight();
			int translationY = (int)ViewHelper.getTranslationY(mInterceptionLayout);
			boolean scrollingUp = 0 < diffY;
			boolean scrollingDown = diffY < 0;
			if (scrollingUp) {
				if (translationY < 0) {
					mScrolled = true;
					mLastScrollState = ScrollState.UP;
					return true;
				}
			} else if (scrollingDown) {
				if (-toolbarHeight < translationY) {
					mScrolled = true;
					mLastScrollState = ScrollState.DOWN;
					return true;
				}
			}
			mScrolled = false;
			return false;
		}

		@Override
		public void onDownMotionEvent(MotionEvent ev) {}

		@Override
		public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
			float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -mToolbarView.getHeight(), 0);
			ViewHelper.setTranslationY(mInterceptionLayout, translationY);
			if (translationY < 0) {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mInterceptionLayout.getLayoutParams();
				lp.height = (int)(-translationY + getScreenHeight());
				mInterceptionLayout.requestLayout();
			}
		}

		@Override
		public void onUpOrCancelMotionEvent(MotionEvent ev) {
			mScrolled = false;
			adjustToolbar(mLastScrollState);
		}
	};

	private Scrollable getCurrentScrollable() {
		Fragment fragment = getCurrentFragment();
		if (fragment == null) {
			return null;
		}
		View view = fragment.getView();
		if (view == null) {
			return null;
		}
		return (Scrollable)view.findViewById(R.id.webview01);
	}

	private void adjustToolbar(ScrollState scrollState) {
		int toolbarHeight = mToolbarView.getHeight();
		final Scrollable scrollable = getCurrentScrollable();
		if (scrollable == null) {
			return;
		}
		int scrollY = scrollable.getCurrentScrollY();
		if (scrollState == ScrollState.DOWN) {
			showToolbar();
		} else if (scrollState == ScrollState.UP) {
			if (toolbarHeight <= scrollY) {
				hideToolbar();
			} else {
				showToolbar();
			}
		} else if (!toolbarIsShown() && !toolbarIsHidden()) {
			// Toolbar is moving but doesn't know which to move:
			// you can change this to hideToolbar()
			showToolbar();
		}
	}

	private Fragment getCurrentFragment() {
		return mPagerAdapter.getItemAt(mPager.getCurrentItem());
	}

	private boolean toolbarIsShown() {
		return ViewHelper.getTranslationY(mInterceptionLayout) == 0;
	}

	private boolean toolbarIsHidden() {
		return ViewHelper.getTranslationY(mInterceptionLayout) == -mToolbarView.getHeight();
	}

	private void showToolbar() {
		animateToolbar(0);
	}

	private void hideToolbar() {
		animateToolbar(-mToolbarView.getHeight());
	}

	private void animateToolbar(final float toY) {
		float layoutTranslationY = ViewHelper.getTranslationY(mInterceptionLayout);
		if (layoutTranslationY != toY) {
			ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mInterceptionLayout), toY).setDuration(200);
			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float translationY = (float)animation.getAnimatedFraction();
					ViewHelper.setTranslationY(mInterceptionLayout, translationY);
					if (translationY < 0) {
						FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mInterceptionLayout.getLayoutParams();
						lp.height = (int)(-translationY + getScreenHeight());
						mInterceptionLayout.requestLayout();
					}
				}
			});
			animator.start();
		}
	}

	protected int getActionBarSize() {
		TypedValue typedValue = new TypedValue();
		int[]textSizeAttr = new int[]{
			R.attr.actionBarSize
		};
		int indexOfAttrTextSize = 0;
		TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
		int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
		a.recycle();
		return actionBarSize;
	}

	protected int getScreenHeight() {
		return findViewById(android.R.id.content).getHeight();
	}

	/**
	 * This adapter provides two types of fragments as an example.
	 * {@linkplain #createItem(int)} should be modified if you use this example for your app.
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
				bundlef.putString("url", "https://www.facebook.com/plugins/comments.php?href=" + url + "&locale=pt_BR&numposts=10");
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
				bundlefd.putString("url", "https://www.facebook.com/plugins/comments.php?href=" + url + "&locale=pt_BR&numposts=10");
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
