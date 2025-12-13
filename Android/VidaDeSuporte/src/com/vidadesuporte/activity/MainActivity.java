package com.vidadesuporte.activity;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.vidadesuporte.R;
import com.vidadesuporte.adapter.DrawerAdapter;
import com.vidadesuporte.fragment.CategoryFragment;
import com.vidadesuporte.fragment.FavoritesFragment;
import com.vidadesuporte.fragment.MainFragment;
import com.vidadesuporte.fragment.PopularFragment;
import com.vidadesuporte.lib.JSONParser;
import com.vidadesuporte.lib.SlidingTabLayout;
import com.vidadesuporte.other.Icons;
import com.vidadesuporte.other.Other;

@SuppressLint({ "DefaultLocale", "CutPasteId" })
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {
	final Context context = this;
	public Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList <Icons> icons;
	private DrawerAdapter adapter2;
	private String[]categoryTitles;
	private TypedArray categoryIcons;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor, titulo;
	public static int pos;
	boolean passed, start, home;
	public static boolean gohome;

	private View mHeaderView;
	private View mToolbarView;
	private int mBaseTranslationY;
	private ViewPager mPager;
	private NavigationAdapter mPagerAdapter;

	private static final String TAG_CATEGORIA = "categoria";
	ArrayList <String> categoriaarray = new ArrayList <String> ();
	JSONArray categorias = null;

	ArrayList <String> reallyarray = new ArrayList <String> ();
	private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		iconcolor = preferences.getString("prefIconColor", "branco");
		if (!iconcolor.equals("branco")) {
			setTheme(R.style.BlackOverflow);
		}
		setContentView(R.layout.toolbar_drawer);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		titulo = "Notícias";

		initDrawer();
		initNotification();

		ActionBarColor(this, titulo);

		final String[]from = new String[]{ "categoryName" };
		final int[]to = new int[]{ R.id.text1 };
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.simple_list_item_1,
				null,
				from,
				to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		mHeaderView = findViewById(R.id.header);
		mToolbarView = findViewById(R.id.toolbar);
		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

		SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
		slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.logo_red));
		slidingTabLayout.setDistributeEvenly(true);
		slidingTabLayout.setViewPager(mPager);

		slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {}

			@Override
			public void onPageSelected(int i) {
				propagateToolbarState(toolbarIsShown());
				if (i == 0) {
					PopularFragment.itsok = false;
				} else {
					MainFragment.itsok = false;
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {}
		});

		propagateToolbarState(toolbarIsShown());

		if (Other.isConnected(this)) {
			new JSONParseSearch().execute();
		}

		pos = 1;
		passed = false;
		start = false;

		if (savedInstanceState != null) {
			pos = savedInstanceState.getInt("position");
		}

		selectItem(pos);
	}

	public static void ActionBarColor(AppCompatActivity activity, String title) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String userColor = preferences.getString("prefColor", "padrao");
		if (userColor.equals("padrao")) {
			activity.findViewById(R.id.header).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.splash_bg));
			activity.findViewById(R.id.frame).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.splash_bg));
			activity.findViewById(R.id.dropshadow).setVisibility(View.VISIBLE);
		} else {
			activity.findViewById(R.id.header).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			activity.findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			if (Build.VERSION.SDK_INT >= 21) {
				ViewCompat.setElevation(activity.findViewById(R.id.header), activity.getResources().getDimension(R.dimen.toolbar_elevation));
				activity.findViewById(R.id.dropshadow).setVisibility(View.GONE);
			} else {
				activity.findViewById(R.id.dropshadow).setVisibility(View.VISIBLE);
			}
		}

		String iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {
			activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + title + "</font>"));
			activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_white);
		} else {
			activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + title + "</font>"));
			activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_dark);
		}
		activity.getSupportActionBar().setIcon(R.drawable.ic_toolbar);
	}

	public void initDrawer() {
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		mDrawerList = (ListView)findViewById(R.id.left_drawer);

		categoryTitles = getResources().getStringArray(
				R.array.navigation_main_sections);

		categoryIcons = getResources().obtainTypedArray(R.array.drawable_ids);

		icons = new ArrayList < Icons > ();

		for (int i = 0; i < categoryIcons.length(); i++) {
			icons.add(new Icons(categoryTitles[i], categoryIcons.getResourceId(i, (i + 1)*-1)));
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

		mDrawerToggle.syncState();

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}

	public void initNotification() {
		boolean notification = preferences.getBoolean("prefNotification", true);
		if (!notification) {
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
								editor.putLong("longNotification", System.currentTimeMillis() + 15*24*60*60*1000);
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
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					theHome();
				}
			}, 100);
		} else {
			theHome();
		}
	}

	public void theHome() {
		passed = false;
		start = false;
		home = true;
		pos = 1;
		mDrawerList.setItemChecked(1, true);
		ActionBarColor(MainActivity.this, "Notícias");
		findViewById(R.id.sliding_tabs).setVisibility(View.VISIBLE);
		mPager.setVisibility(View.VISIBLE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.GONE);
		supportInvalidateOptionsMenu();
	}

	public void Favs() {
		titulo = "Favoritos";
		passed = true;
		home = false;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment favorites = new FavoritesFragment();
		ft.replace(R.id.content_frame, favorites);
		if (start) {
			ft.addToBackStack(null);
		} else {
			start = true;
		}
		ft.commit();
		findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
		mPager.setVisibility(View.GONE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.VISIBLE);
		showToolbar();
		supportInvalidateOptionsMenu();
	}

	public void Clear(String title, String label, int thepos) {
		titulo = title;
		passed = true;
		home = false;
		Bundle bundle = new Bundle();
		bundle.putString("url", "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?label=" + label);
		bundle.putString("label", label);
		bundle.putString("titulo", title);
		bundle.putInt("pos", thepos);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment category = new CategoryFragment();
		category.setArguments(bundle);
		ft.replace(R.id.content_frame, category);
		if (start) {
			ft.addToBackStack(null);
		} else {
			start = true;
		}
		ft.commit();
		findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
		mPager.setVisibility(View.GONE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.VISIBLE);
		showToolbar();
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
			Clear("Wii U", "Nintendo%20Wii%20U", position);
			break;
		case 4:
			Clear("3DS", "Nintendo%203DS", position);
			break;
		case 5:
			Clear("Mario", "Mario", position);
			break;
		case 6:
			Clear("Zelda", "Zelda", position);
			break;
		case 7:
			Clear("Pokémon", "Pok%C3%A9mon", position);
			break;
		case 8:
			Clear("Donkey Kong", "Donkey%20Kong", position);
			break;
		case 9:
			Clear("Metroid", "Metroid", position);
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

		if (position >= 10) {
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

	private class JSONParseSearch extends AsyncTask < String, String, JSONObject > {
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
			editor.putString("allcateg", json.toString());
			editor.commit();
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
		final MatrixCursor c = new MatrixCursor(new String[]{
				BaseColumns._ID,
				"categoryName"
			});
		for (int i = 0; i < categoriaarray.size(); i++) {
			if (categoriaarray.get(i).toString().toLowerCase().startsWith(query.toLowerCase())) {
				reallyarray.add(categoriaarray.get(i).toString());
				c.addRow(new Object[]{
					i,
					categoriaarray.get(i).toString()
				});
			}
			mAdapter.changeCursor(c);
		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		if (dragging) {
			int toolbarHeight = mToolbarView.getHeight();
			float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
			if (firstScroll) {
				if (-toolbarHeight < currentHeaderTranslationY) {
					mBaseTranslationY = scrollY;
				}
			}
			float headerTranslationY = ScrollUtils.getFloat( - (scrollY - mBaseTranslationY), -toolbarHeight, 0);
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
		}
	}

	@Override
	public void onDownMotionEvent() {}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		mBaseTranslationY = 0;

		Fragment fragment = getCurrentFragment();
		if (fragment == null) {
			return;
		}
		View view = fragment.getView();
		if (view == null) {
			return;
		}

		int toolbarHeight = mToolbarView.getHeight();
		final ObservableListView listView = (ObservableListView)view.findViewById(R.id.list);
		if (listView == null) {
			return;
		}
		int scrollY = listView.getCurrentScrollY();
		if (scrollState == ScrollState.DOWN) {
			showToolbar();
		} else if (scrollState == ScrollState.UP) {
			if (toolbarHeight <= scrollY) {
				hideToolbar();
			} else {
				showToolbar();
			}
		} else {
			// Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
			if (toolbarIsShown() || toolbarIsHidden()) {
				// Toolbar is completely moved, so just keep its state
				// and propagate it to other pages
				propagateToolbarState(toolbarIsShown());
			} else {
				// Toolbar is moving but doesn't know which to move:
				// you can change this to hideToolbar()
				showToolbar();
			}
		}
	}

	private Fragment getCurrentFragment() {
		return mPagerAdapter.getItemAt(mPager.getCurrentItem());
	}

	private void propagateToolbarState(boolean isShown) {
		int toolbarHeight = mToolbarView.getHeight();

		// Set scrollY for the fragments that are not created yet
		mPagerAdapter.setScrollY(isShown ? 0 : toolbarHeight);

		// Set scrollY for the active fragments
		for (int i = 0; i < mPagerAdapter.getCount(); i++) {
			// Skip current item
			if (i == mPager.getCurrentItem()) {
				continue;
			}

			// Skip destroyed or not created item
			Fragment f = mPagerAdapter.getItemAt(i);
			if (f == null) {
				continue;
			}
		}
	}

	private boolean toolbarIsShown() {
		return ViewHelper.getTranslationY(mHeaderView) == 0;
	}

	private boolean toolbarIsHidden() {
		return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
	}

	private void showToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
		if (headerTranslationY != 0) {
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
		}
		propagateToolbarState(true);
	}

	private void hideToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
		int toolbarHeight = mToolbarView.getHeight();
		if (headerTranslationY != -toolbarHeight) {
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
		}
		propagateToolbarState(false);
	}

	/**
	*This adapter provides two types of fragments as an example.
	*{@linkplain #createItem(int)} should be modified if you use this example for your app.
	 */

	private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

		private static final String[]TITLES = new String[]{
			"Recentes",
			"Populares"
		};

		public NavigationAdapter(FragmentManager fm) {
			super(fm);
		}

		public void setScrollY(int scrollY) {}

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
				if (home) {
					MainActivity.this.finish();
					return true;
				} else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
					getSupportFragmentManager().popBackStack();
					return true;
				} else {
					selectItem(1);
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("position", pos);
		super.onSaveInstanceState(savedInstanceState);
	}

	public void onResume() {
		if (home) {
			titulo = "Notícias";
		}
		ActionBarColor(this, titulo);
		supportInvalidateOptionsMenu();
		super.onResume();
	}
}
