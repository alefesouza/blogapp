package net.aloogle.apps.acasadocogumelo.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.google.android.gms.ads.*;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import net.aloogle.apps.acasadocogumelo.R;
import net.aloogle.apps.acasadocogumelo.adapter.DrawerAdapter;
import net.aloogle.apps.acasadocogumelo.fragment.CategoryFragment;
import net.aloogle.apps.acasadocogumelo.fragment.FavoritesFragment;
import net.aloogle.apps.acasadocogumelo.fragment.MainFragment;
import net.aloogle.apps.acasadocogumelo.fragment.PopularFragment;
import net.aloogle.apps.acasadocogumelo.lib.JSONParser;
import net.aloogle.apps.acasadocogumelo.lib.SlidingTabLayout;
import net.aloogle.apps.acasadocogumelo.other.Icons;

@SuppressLint({ "DefaultLocale", "CutPasteId" })
@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {
	final Context context = this;
	public Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	@SuppressWarnings("unused")
	private CharSequence mDrawerTitle;
	private ArrayList < Icons > icons;
	private DrawerAdapter adapter2;
	private String[]categoryTitles;
	private TypedArray categoryIcons;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor, titulo;
	private NavigationAdapter mPagerAdapter;
	int pos;
	private View mToolbarView;
	private TouchInterceptionFrameLayout mInterceptionLayout;
	private ViewPager mPager;
	private int mSlop;
	private boolean mScrolled,
	passed;
	private ScrollState mLastScrollState;

	private AdView adView;

	private static final String TAG_CATEGORIA = "categoria";
	ArrayList <String> categoriaarray = new ArrayList <String> ();
	JSONArray categorias = null;

	ArrayList <String> reallyarray = new ArrayList <String> ();
	private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {}
		else {
			setTheme(R.style.BlackOverflow);
		}
		setContentView(R.layout.toolbar_drawer);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		titulo = "Notícias";
		ActionBarColor(titulo);

		initDrawer();
		initNotification();

		final String[]from = new String[]{ "categoryName" };
		final int[]to = new int[]{ android.R.id.text1 };
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1,
				null,
				from,
				to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		adView = new AdView(this);
		adView.setAdUnitId("")
		adView.setAdSize(AdSize.SMART_BANNER);

		LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);

		layout.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);

        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
		
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        findViewById(R.id.pager_wrapper).setPadding(0, getActionBarSize() + tabHeight, 0, 0);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(mPager);
        
        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
		
		ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
			new JSONParseSearch().execute();
		} else {}

		pos = 1;
		passed = false;

		if (savedInstanceState != null) {
			pos = savedInstanceState.getInt("position");
		}

		selectItem(pos);
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

	public void initDrawer() {
		mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		mDrawerList = (ListView)findViewById(R.id.left_drawer);

		categoryTitles = getResources().getStringArray(
				R.array.navigation_main_sections);

		categoryIcons = getResources().obtainTypedArray(R.array.drawable_ids);

		icons = new ArrayList <Icons> ();

		for (int i = 0; i < categoryIcons.length(); i++) {
			icons.add(new Icons(categoryTitles[i], categoryIcons.getResourceId(i, (i + 1) * -1)));
		}

		categoryIcons.recycle();

		LayoutInflater inflater = getLayoutInflater();
		final ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header,
			mDrawerList, false);
		final ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.footer,
			mDrawerList, false);
		final ViewGroup footer2 = (ViewGroup)inflater.inflate(R.layout.footer2,
			mDrawerList, false);

		mDrawerList.addHeaderView(header, null, true);
		mDrawerList.addFooterView(footer, null, true);
		mDrawerList.addFooterView(footer2, null, true);

		adapter2 = new DrawerAdapter(getApplicationContext(), icons);
		mDrawerList.setAdapter(adapter2);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		if (iconcolor.equals("branco")) {
			mDrawerToggle = new ActionBarDrawerToggle(
					this,
					mDrawerLayout,
					R.drawable.ic_drawer_white,
					R.string.drawer_open,
					R.string.drawer_close) {

				public void onDrawerClosed(View view) {
					supportInvalidateOptionsMenu();
				}

				public void onDrawerOpened(View drawerView) {
					supportInvalidateOptionsMenu();
				}
			};
		} else {
			mDrawerToggle = new ActionBarDrawerToggle(
					this,
					mDrawerLayout,
					R.drawable.ic_drawer_dark,
					R.string.drawer_open,
					R.string.drawer_close) {

				public void onDrawerClosed(View view) {}

				public void onDrawerOpened(View drawerView) {}
			};
		}

		mDrawerToggle.syncState();

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void initNotification() {
		boolean notification = preferences.getBoolean("prefNotification", true);
		if (notification) {}
		else {
			if (System.currentTimeMillis() > preferences.getLong("longNotification", 0)) {
				final AlertDialog dialogsprites = new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.notifications)
					.setMessage(R.string.dialog_notification)
					.setPositiveButton(R.string.yes, null)
					.setNegativeButton(R.string.no, null)
					.create();

				dialogsprites.setOnShowListener(new
					DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						Button b = dialogsprites.getButton(AlertDialog.BUTTON_POSITIVE);
						b.setOnClickListener(new
							View.OnClickListener() {
							@Override
							public void onClick(View view) {
								editor.putBoolean("prefNotification", true);
								editor.commit();
								editor.putLong("longNotification", 0);
								editor.commit();
								dialogsprites.dismiss();
							}
						});
						Button n = dialogsprites.getButton(AlertDialog.BUTTON_NEGATIVE);
						n.setOnClickListener(new
							View.OnClickListener() {
							@Override
							public void onClick(View view) {
								editor.putLong("longNotification", System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000);
								editor.commit();
								dialogsprites.dismiss();
							}
						});
					}
				});
				dialogsprites.show();
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView <  ?  > parent, View view, int position, long id) {
			int posi;

			if (position == 0) {
				posi = 1;
				mDrawerList.setItemChecked(1, true);
			} else {
				posi = position;
			}

			if (posi != pos) {
				selectItem(posi);
			}
		}
	}

	public void Home() {
		titulo = "Notícias";
		passed = false;
		ActionBarColor(titulo);
		findViewById(R.id.sliding_tabs).setVisibility(View.VISIBLE);
		mPager.setVisibility(View.VISIBLE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.GONE);
		supportInvalidateOptionsMenu();
	}

	public void Favs() {
		titulo = "Favoritos";
		ActionBarColor(titulo);
		passed = true;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment favorites = new FavoritesFragment();
		ft.replace(R.id.content_frame, favorites);
		ft.commit();
		findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
		mPager.setVisibility(View.GONE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.VISIBLE);
		supportInvalidateOptionsMenu();
	}

	public void Clear(String title, String label) {
		titulo = title;
		ActionBarColor(titulo);
		passed = true;
		Bundle bundle = new Bundle();
		bundle.putString("url", "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?label=" + label);
		bundle.putString("label", label);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment category = new CategoryFragment();
		category.setArguments(bundle);
		ft.replace(R.id.content_frame, category);
		ft.commit();
		findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
		mPager.setVisibility(View.GONE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.VISIBLE);
		supportInvalidateOptionsMenu();
	}

	private void selectItem(int position) {
		switch (position) {
		case 0:
			Home();
			break;
		case 1:
			Home();
			break;
		case 2:
			Favs();
			break;
		case 3:
			Clear("Wii U", "Nintendo%20Wii%20U");
			break;
		case 4:
			Clear("3DS", "Nintendo%203DS");
			break;
		case 5:
			Clear("Mario", "Mario");
			break;
		case 6:
			Clear("Zelda", "Zelda");
			break;
		case 7:
			Clear("Pokémon", "Pok%C3%A9mon");
			break;
		case 8:
			Clear("Donkey Kong", "Donkey%20Kong");
			break;
		case 9:
			Clear("Metroid", "Metroid");
			break;
		case 10:
			Intent facebook = new Intent(MainActivity.this, FragmentActivity.class);
			facebook.putExtra("fragment", 5);
			facebook.putExtra("titulo", "Facebook");
			facebook.putExtra("url", "https://www.facebook.com/pages/A-Casa-do-Cogumelo/137869276285719");
			startActivity(facebook);
			break;
		case 11:
			Intent googleplus = new Intent(MainActivity.this, FragmentActivity.class);
			googleplus.putExtra("fragment", 5);
			googleplus.putExtra("titulo", "Google+");
			googleplus.putExtra("url", "https://plus.google.com/+Acasadocogumelo/posts");
			startActivity(googleplus);
			break;
		case 12:
			Intent twitter = new Intent(MainActivity.this, FragmentActivity.class);
			twitter.putExtra("fragment", 5);
			twitter.putExtra("titulo", "Twitter");
			twitter.putExtra("url", "https://twitter.com/ACasadoCogumelo");
			startActivity(twitter);
			break;
		case 13:
			Intent youtube = new Intent(MainActivity.this, FragmentActivity.class);
			youtube.putExtra("fragment", 5);
			youtube.putExtra("titulo", "YouTube");
			youtube.putExtra("url", "https://www.youtube.com/channel/UCEHol_7LxomGTVIKopJfQXA");
			startActivity(youtube);
			break;
		case 14:
			Intent settings = new Intent(MainActivity.this, FragmentActivity.class);
			settings.putExtra("fragment", 0);
			startActivity(settings);
			break;
		case 15:
			Intent about = new Intent(MainActivity.this, FragmentActivity.class);
			about.putExtra("fragment", 2);
			startActivity(about);
			break;
		}

		if (position < 10) {
			pos = position;
			mDrawerList.setItemChecked(position, true);
		} else {
			mDrawerList.setItemChecked(pos, true);
		}

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_search);
		SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint(getString(R.string.search));

		SearchView.SearchAutoComplete
		theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);

		if (iconcolor.equals("branco")) {
			theTextArea.setTextColor(Color.WHITE);
		} else {
			theTextArea.setTextColor(Color.BLACK);
		}

		searchView.setSuggestionsAdapter(mAdapter);

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionClick(int position) {
				try {
					Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
					intent.putExtra("fragment", 4);
					intent.putExtra("query", URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8"));
					intent.putExtra("categorias", categoriaarray);
					startActivity(intent);
				} catch (UnsupportedEncodingException e) {}
				return true;
			}

			@Override
			public boolean onSuggestionSelect(int position) {
				try {
					Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
					intent.putExtra("fragment", 4);
					intent.putExtra("query", URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8"));
					intent.putExtra("categorias", categoriaarray);
					startActivity(intent);
				} catch (UnsupportedEncodingException e) {}
				return true;
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				try {
					Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
					intent.putExtra("fragment", 4);
					intent.putExtra("query", URLEncoder.encode(s, "UTF-8"));
					intent.putExtra("categorias", categoriaarray);
					startActivity(intent);
				} catch (UnsupportedEncodingException e) {}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				reallyarray.clear();
				populateAdapter(s);
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (iconcolor.equals("branco")) {
			menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search_white);
		} else {
			menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search_black);
		}

		if (pos == 0 || pos == 1) {
			menu.findItem(R.id.menu_refresh).setVisible(true);
			menu.findItem(R.id.menu_opensite).setVisible(true);
		} else if (pos > 1 && pos < 10) {
			menu.findItem(R.id.menu_refresh).setVisible(false);
			menu.findItem(R.id.menu_opensite).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.menu_opensite:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://acasadocogumelo.com"));
			startActivity(intent);
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	private class JSONParseSearch extends AsyncTask <String, String, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String...args) {
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl("http://apps.aloogle.net/blogapp/acasadocogumelo/json/categorias.php");
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				try {
					categorias = json.getJSONArray("categorias");
					for (int i = 0; i < categorias.length(); i++) {
						JSONObject c = categorias.getJSONObject(i);

						String categoria = c.getString(TAG_CATEGORIA);
						categoriaarray.add(categoria);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {}
		}
	}

	private void populateAdapter(String query) {
		final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "categoryName" });
		for (int i = 0; i < categoriaarray.size(); i++) {
			if (categoriaarray.get(i).toString().toLowerCase().startsWith(query.toLowerCase())) {
				reallyarray.add(categoriaarray.get(i).toString());
				c.addRow(new Object[]{ i, categoriaarray.get(i).toString() });
			}
			mAdapter.changeCursor(c);
		}
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
		return (Scrollable)view.findViewById(R.id.list);
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
				if (passed == false) {
					showToolbar();
				}
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
		int[]textSizeAttr = new int[]{ R.attr.actionBarSize };
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

		private static final String[]TITLES = new String[]{ "Recentes", "Populares" };

		public NavigationAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		protected Fragment createItem(int position) {
			Fragment f;
			final int pattern = position % 2;
			switch (pattern) {
			case 0:
				f = new MainFragment();
				break;
			case 1:
				f = new PopularFragment();
				break;
			default:
				f = new MainFragment();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
			if (drawerOpen) {
				mDrawerLayout.closeDrawer(mDrawerList);
				return true;
			} else {
				MainActivity.this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("position", pos);
		super.onSaveInstanceState(savedInstanceState);
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
