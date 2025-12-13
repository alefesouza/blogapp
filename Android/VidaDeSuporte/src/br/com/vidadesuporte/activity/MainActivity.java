package br.com.vidadesuporte.activity;

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
import android.os.Bundle;
import android.os.Handler;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import br.com.vidadesuporte.R;
import br.com.vidadesuporte.adapter.DrawerAdapter;
import br.com.vidadesuporte.fragment.CategoryFragment;
import br.com.vidadesuporte.fragment.FavoritesFragment;
import br.com.vidadesuporte.fragment.MainFragment;
import br.com.vidadesuporte.other.Icons;
import br.com.vidadesuporte.other.Other;

@SuppressLint({ "DefaultLocale", "CutPasteId" })
public class MainActivity extends AppCompatActivity {
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
	String titulo, suggestion;
	public static int pos;
	boolean passed, start, home;
	public static boolean gohome;

	private View mToolbarView;
	private ViewPager mPager;
	private NavigationAdapter mPagerAdapter;
	
	private static final String TAG_CATEGORIA = "categoria";
	ArrayList <String> categoriaarray = new ArrayList <String> ();

	ArrayList <String> reallyarray = new ArrayList <String> ();
	private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		setContentView(R.layout.toolbar_drawer);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		titulo = getString(R.string.app_name);

		initDrawer();
		initNotification();

		getSupportActionBar().setTitle(titulo);

		final String[]from = new String[]{ "categoryName" };
		final int[]to = new int[]{ R.id.text1 };
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.simple_list_item_1,
				null,
				from,
				to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		mToolbarView = findViewById(R.id.toolbar);
		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);
		
		ViewCompat.setElevation(mToolbarView, R.dimen.toolbar_elevation);

		pos = 1;
		passed = false;
		start = false;

		if (savedInstanceState != null) {
			pos = savedInstanceState.getInt("position");
		}
		
		if(Other.isConnected(this)) {
			Ion.with(this)
				.load("http://apps.aloogle.net/blogapp/vidadesuporte/json/categorias.php")
				.asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {
					@Override
					public void onCompleted(Exception e, JsonObject json) {
						editor.putString("allcateg", json.toString());
						editor.commit();
					}
				});
			}
		selectItem(pos);
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
				mToolbar,
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
		titulo = getString(R.string.app_name);
		passed = false;
		start = false;
		home = true;
		pos = 1;
		mDrawerList.setItemChecked(1, true);
		mPager.setVisibility(View.VISIBLE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.GONE);
		getSupportActionBar().setTitle(titulo);
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
		mPager.setVisibility(View.GONE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.VISIBLE);
		supportInvalidateOptionsMenu();
	}

	public void Clear(String label, int thepos) {
		titulo = label;
		passed = true;
		home = false;
		Bundle bundle = new Bundle();
		bundle.putString("label", label);
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

		String[] categories = getResources().getStringArray(R.array.categories);
		if(position > 2 && position < categories.length + 3) {
			Clear(categories[position - 3], position);
		}

		String[] socialnetworks = getResources().getStringArray(R.array.allow_sites2);
		if(position > categories.length + 2 && position < categories.length + 3 + socialnetworks.length) {
			String[] usernames = getResources().getStringArray(R.array.socialnetworksusers);
			String[] sitenames = getResources().getStringArray(R.array.socialnetworksnames);
			Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			int spos = position - (categories.length + socialnetworks.length);
			intent.putExtra("titulo", sitenames[spos]);
			intent.putExtra("url", "http://" + socialnetworks[spos] + "/" + usernames[spos]);
			startActivity(intent);
		}
		
		if (position >= categories.length + 3) {
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

		searchView.setSuggestionsAdapter(mAdapter);

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionClick(int position) {
				try {
					Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
					intent.putExtra("fragment", 4);
					intent.putExtra("query", URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8"));
					startActivity(intent);
				} catch (UnsupportedEncodingException e) {}
				reallyarray.clear();
				return true;
			}

			@Override
			public boolean onSuggestionSelect(int position) {
				try {
					Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
					intent.putExtra("fragment", 4);
					intent.putExtra("query", URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8"));
					startActivity(intent);
				} catch (UnsupportedEncodingException e) {}
				reallyarray.clear();
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
					startActivity(intent);
				} catch (UnsupportedEncodingException e) {}
				reallyarray.clear();
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String s) {
				suggestion = s;
				if (Other.isConnected(MainActivity.this)) {
				new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (s.equals(suggestion)) {
								Ion.with(MainActivity.this)
									.load("http://apps.aloogle.net/blogapp/vidadesuporte/json/tags.php?q=" + suggestion.replace(" ", "%20"))
									.asJsonObject()
									.setCallback(new FutureCallback<JsonObject>() {
										@Override
										public void onCompleted (Exception e, JsonObject json) {
											categoriaarray.clear();
											JsonArray categorias = json.get("categorias").getAsJsonArray();
											for (int i = 0; i < categorias.size(); i++) {
												JsonObject c = categorias.get(i).getAsJsonObject();

												String categoria = c.get(TAG_CATEGORIA).getAsString();
												categoriaarray.add(categoria);
											}
											populateAdapter();
										}
									});
								}
								
						}
					}, 1000);
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search);

		if (pos == 0 || pos == 1) {
			menu.findItem(R.id.menu_random).setVisible(true);
			menu.findItem(R.id.menu_refresh).setVisible(true);
			menu.findItem(R.id.menu_opensite).setVisible(true);
		} else if (pos > 1 && pos < 11) {
			menu.findItem(R.id.menu_random).setVisible(false);
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
			intent.setData(Uri.parse("http://vidadesuporte.com.br"));
			startActivity(intent);
			return true;
		case R.id.menu_random:
			if(Other.isConnected(this)) {
				Toast toast = Toast.makeText(this, "Carregando...", Toast.LENGTH_SHORT);
				toast.show();

				try {
				Ion.with(this)
					.load("http://apps.aloogle.net/blogapp/vidadesuporte/json/aleatorio.php")
					.asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {
						@Override
						public void onCompleted(Exception e, JsonObject json) {
							Intent intent = new Intent(MainActivity.this, PostActivity.class);
							intent.putExtra("id", json.get("id").getAsString());
							intent.putExtra("fromrandom", true);
							startActivity(intent);
						}
					});
				} catch (Exception e) {
					Toast toastt = Toast.makeText(this, "Erro", Toast.LENGTH_SHORT);
					toastt.show();
				}
			} else {
				Toast toast = Toast.makeText(this, getString(R.string.needinternet), Toast.LENGTH_LONG);
				toast.show();
			}
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}
	
	private void populateAdapter() {
		final MatrixCursor c = new MatrixCursor(new String[]{
				BaseColumns._ID,
				"categoryName"
			});
		for (int i = 0; i < categoriaarray.size(); i++) {
			if (categoriaarray.get(i).toString().toLowerCase().startsWith(suggestion.toLowerCase())) {
				reallyarray.add(categoriaarray.get(i).toString());
				c.addRow(new Object[]{
					i,
					categoriaarray.get(i).toString()
				});
			}
			mAdapter.changeCursor(c);
		}
	}

	private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

		private static final String[]TITLES = new String[]{
			"Recentes"
		};

		public NavigationAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		protected Fragment createItem(int position) {
			Fragment f;
			final int pattern = position % 1;
			switch (pattern) {
			case 0:
				f = new MainFragment();
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
			titulo = getString(R.string.app_name);
		}
		getSupportActionBar().setTitle(titulo);
		supportInvalidateOptionsMenu();
		super.onResume();
	}
}
