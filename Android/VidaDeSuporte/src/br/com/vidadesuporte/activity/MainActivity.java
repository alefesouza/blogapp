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
import android.view.View.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.support.design.widget.*;
import android.text.*;
import br.com.vidadesuporte.other.*;

@SuppressLint({ "DefaultLocale", "CutPasteId" })
public class MainActivity extends AppCompatActivity {
	final Context context = this;
	public Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList <Icons> icons;
	ArrayList<String> linksnames = new ArrayList<String>();
	ArrayList<String> linksurls = new ArrayList<String>();
	ArrayList<String> linksicons = new ArrayList<String>();

	ArrayList<String> fcategoriasids = new ArrayList<String>();
	ArrayList<String> fcategoriasnames = new ArrayList<String>();
	ArrayList<String> fcategoriasicons = new ArrayList<String>();

	ArrayList<String> categoriasids = new ArrayList<String>();
	ArrayList<String> categoriasnames = new ArrayList<String>();
	ArrayList<String> categoriasicons = new ArrayList<String>();
	private DrawerAdapter adapter2;
	private TypedArray categoryIcons, socialIcons;
	public static FloatingActionButton fabrandom;
	SharedPreferences preferences;
	Editor editor;
	String titulo, suggestion, iconColor, linksname, fcategoriasname;
	public static int pos;
	int linkscount, categoriastotal, fcategoriascount;
	boolean passed, start, home, drawerloaded;
	public static boolean gohome;
	
	ViewGroup footer, footer2, footer3, footer4;

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
		iconColor = preferences.getString("prefIconColor", "branco");
		if(iconColor.equals("preto")) {
			setTheme(R.style.BlackOverflow);
		}
		setContentView(R.layout.toolbar_drawer);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		titulo = getString(R.string.app_name);

		initDrawer(0);
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

		mToolbarView = findViewById(R.id.toolbar);
		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);
		
		ViewCompat.setElevation(mToolbarView, R.dimen.toolbar_elevation);

		pos = 1;
		passed = false;
		start = false;
		
		fabrandom = (FloatingActionButton)findViewById(R.id.fabrandom);
		fabrandom.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(Other.isConnected(MainActivity.this)) {
						Toast toast = Toast.makeText(MainActivity.this, "Carregando...", Toast.LENGTH_LONG);
						toast.show();
							Ion.with(MainActivity.this)
								.load("http://apps.aloogle.net/blogapp/wordpress/json/aleatorio.php?id=" + getString(R.string.blogid) + "&start=" + getString(R.string.randomstart))
								.asJsonObject()
								.setCallback(new FutureCallback<JsonObject>() {
									@Override
									public void onCompleted(Exception e, JsonObject json) {
										if(e != null) {
											Toast toastt = Toast.makeText(MainActivity.this, "Houve um erro ao buscar por post aleatório, tente novamente", Toast.LENGTH_SHORT);
											toastt.show();
											return;
										}
										Intent intent = new Intent(MainActivity.this, PostActivity.class);
										intent.putExtra("id", json.get("id").getAsString());
										intent.putExtra("fromrandom", true);
										startActivity(intent);
									}
								});
					} else {
						Toast toast = Toast.makeText(MainActivity.this, getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
					}
				}
		});
		
		fabrandom.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View p1) {
				Toast toast = Toast.makeText(MainActivity.this, "Post aleatório", Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		});

		if (savedInstanceState != null) {
			pos = savedInstanceState.getInt("position");
		}
		
		if(Other.isConnected(this)) {
			loadDrawer();
		}
		selectItem(pos);
	}

	public void loadDrawer() {
		Ion.with(this)
			.load("http://apps.aloogle.net/blogapp/wordpress/json/categorias.php?id=" + getString(R.string.blogid))
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, JsonObject json) {
					if(e != null) {
						e.printStackTrace();
						return;
					}
					JsonArray links = json.get("links").getAsJsonObject().get("links").getAsJsonArray();
					for (int i = 0; i < links.size(); i++) {
						JsonObject c = links.get(i).getAsJsonObject();

						String name = c.get("name").getAsString();
						String url = c.get("url").getAsString();
						String icon = c.get("icon").getAsString();

						linksnames.add(name);
						linksurls.add(url);
						linksicons.add(icon);
					}
					linksname = json.get("links").getAsJsonObject().get("name").getAsString();

					JsonArray playlists = json.get("categorias").getAsJsonObject().get("categorias").getAsJsonArray();
					for (int i = 0; i < playlists.size(); i++) {
						JsonObject c = playlists.get(i).getAsJsonObject();

						String id = c.get("id").getAsString();
						String name = c.get("titulo").getAsString();
						String icon = c.get("icon").getAsString();

						categoriasids.add(id);
						categoriasnames.add(name);
						categoriasicons.add(icon);
					}
					categoriastotal = json.get("categorias").getAsJsonObject().get("total").getAsInt();

					JsonArray fplaylists = json.get("featuredcategorias").getAsJsonObject().get("featuredcategorias").getAsJsonArray();
					for (int i = 0; i < fplaylists.size(); i++) {
						JsonObject c = fplaylists.get(i).getAsJsonObject();

						String id = c.get("id").getAsString();
						String name = c.get("name").getAsString();
						String icon = c.get("icon").getAsString();

						fcategoriasids.add(id);
						fcategoriasnames.add(name);
						fcategoriasicons.add(icon);
					}
					fcategoriasname = json.get("featuredcategorias").getAsJsonObject().get("name").getAsString();
					
					drawerloaded = true;
					initDrawer(1);
					mDrawerList.setItemChecked(1, true);
				}
			});
	}
	
	public static void ActionBarColor(AppCompatActivity activity, String title) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String userColor = preferences.getString("prefColor", "222222");
		if (userColor.equals("fundo")) {
			activity.getSupportActionBar().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.toolbar_bg));
			activity.findViewById(R.id.frame).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.toolbar_bg));
		} else {
			activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			activity.findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}
		
		String iconColor = preferences.getString("prefIconColor", "branco");
		
		int indicator = iconColor.equals("branco") ? R.drawable.ic_drawer : R.drawable.ic_drawer_dark;
		String titlecolor = iconColor.equals("branco") ? "ffffff" : "000000";

		activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#" + titlecolor + "\">" + title + "</font>"));
		activity.getSupportActionBar().setHomeAsUpIndicator(indicator);
	}

	public void initDrawer(int fase) {
		LayoutInflater inflater = getLayoutInflater();
		if(fase == 0) {
			mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

			mDrawerList = (ListView)findViewById(R.id.left_drawer);

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

			final ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header,
																 mDrawerList, false);
			footer = (ViewGroup)inflater.inflate(R.layout.footer,
												 mDrawerList, false);
			footer2 = (ViewGroup)inflater.inflate(R.layout.footer2,
												  mDrawerList, false);
			footer3 = (ViewGroup)inflater.inflate(R.layout.footer3,
												  mDrawerList, false);
			footer4 = (ViewGroup)inflater.inflate(R.layout.footer4,
												  mDrawerList, false);

			mDrawerList.addHeaderView(header, null, true);
			if(Other.isConnected(this)) {
				mDrawerList.addFooterView(footer3, null, false);
			} else {
				CustomTextView text = (CustomTextView)footer4.findViewById(R.id.myTextView4);
				text.setText("Tentar novamente");

				footer4.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(Other.isConnected(MainActivity.this)) {
								mDrawerList.removeFooterView(footer);
								mDrawerList.removeFooterView(footer2);
								mDrawerList.removeFooterView(footer4);
								mDrawerList.addFooterView(footer3, null, false);
								mDrawerList.addFooterView(footer, null, false);
								mDrawerList.addFooterView(footer2, null, false);
								loadDrawer();
							} else {
								Toast toast = Toast.makeText(MainActivity.this, getString(R.string.needinternet), Toast.LENGTH_SHORT);
								toast.show();
							}
						}
					});
				mDrawerList.addFooterView(footer4, null, false);
			}
			mDrawerList.addFooterView(footer, null, false);
			footer.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent settings = new Intent(MainActivity.this, FragmentActivity.class);
						settings.putExtra("fragment", 0);
						startActivity(settings);
					}
				});
			mDrawerList.addFooterView(footer2, null, false);
			footer2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent about = new Intent(MainActivity.this, FragmentActivity.class);
						about.putExtra("fragment", 2);
						startActivity(about);
					}
				});
			categoryIcons = getResources().obtainTypedArray(R.array.drawable_ids);
			socialIcons = getResources().obtainTypedArray(R.array.socialicons);

			icons = new ArrayList < Icons > ();

			icons.add(new Icons("Início", categoryIcons.getResourceId(0, -1), 0, ""));
			icons.add(new Icons("Favoritos", categoryIcons.getResourceId(1, -1), 0, ""));
			icons.add(new Icons("Rede sociais", categoryIcons.getResourceId(0, -1), 5, ""));
			icons.add(new Icons("Facebook", socialIcons.getResourceId(0, -1), 1, ""));
			icons.add(new Icons("Instagram", socialIcons.getResourceId(1, -1), 1, ""));
			icons.add(new Icons("Twitter", socialIcons.getResourceId(2, -1), 1, ""));
			icons.add(new Icons("YouTube", socialIcons.getResourceId(3, -1), 1, ""));

			adapter2 = new DrawerAdapter(getApplicationContext(), icons);
			mDrawerList.setAdapter(adapter2);

			mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

			mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		} else {
			if(linksnames.size() > 0) {
				icons.add(new Icons(linksname, categoryIcons.getResourceId(0, -1), 5, ""));
				for (int i = 0; i < linksnames.size(); i++) {
					icons.add(new Icons(linksnames.get(i), categoryIcons.getResourceId(2, (i + 1)*-1), 2, linksicons.get(i)));
				}
				linkscount = linksnames.size() + 1;
			}

			if(fcategoriasids.size() > 0) {
				icons.add(new Icons(fcategoriasname, categoryIcons.getResourceId(0, -1), 5, ""));
				for (int i = 0; i < fcategoriasids.size(); i++) {
					icons.add(new Icons(fcategoriasnames.get(i), categoryIcons.getResourceId(3, (i + 1)*-1), 3, fcategoriasicons.get(i)));
				}
				fcategoriascount = fcategoriasids.size() + 1;
			}

			if(categoriasids.size() > 0) {
				icons.add(new Icons("Categorias", categoryIcons.getResourceId(0, -1), 5, ""));
				for (int i = 0; i < categoriasids.size(); i++) {
					icons.add(new Icons(categoriasnames.get(i), categoryIcons.getResourceId(3, (i + 1)*-1), 4, categoriasicons.get(i)));
				}
			}

			if(categoriastotal > 15) {
				ViewGroup footer5 = (ViewGroup)inflater.inflate(R.layout.footer4,
																mDrawerList, false);
				CustomTextView text = (CustomTextView)footer5.findViewById(R.id.myTextView4);
				text.setText("Carregar mais");
				footer5.findViewById(R.id.divider).setVisibility(View.GONE);
				footer5.findViewById(R.id.divider2).setVisibility(View.GONE);

				footer5.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent categorias = new Intent(MainActivity.this, FragmentActivity.class);
							categorias.putExtra("fragment", 3);
							startActivity(categorias);
						}
					});
				mDrawerList.removeFooterView(footer);
				mDrawerList.removeFooterView(footer2);
				mDrawerList.addFooterView(footer5, null, false);
				mDrawerList.addFooterView(footer, null, false);
				mDrawerList.addFooterView(footer2, null, false);
			}

			adapter2 = new DrawerAdapter(getApplicationContext(), icons);
			mDrawerList.setAdapter(adapter2);

			categoryIcons.recycle();

			mDrawerList.removeFooterView(footer3);
		}
	}

	public void initNotification() {
		boolean notification = preferences.getBoolean("prefNotification", true);
		boolean haveTimerToDialogRate = preferences.getBoolean("haveTimerToDialog", false);
		boolean rated = preferences.getBoolean("ratedapp", false);
		
		if(!haveTimerToDialogRate) {
			editor.putLong("longToDialogRate", System.currentTimeMillis() + 3*24*60*60*1000);
			editor.commit();
			editor.putBoolean("haveTimerToDialog", true);
			editor.commit();
		}

		if(!rated) {
			if (System.currentTimeMillis() > preferences.getLong("longToDialogRate", 0)) {
				final AlertDialog dialograte = new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.rateapp)
					.setMessage(R.string.dialog_rate)
					.setPositiveButton(R.string.later, null)
					.setNegativeButton(R.string.rate, null)
					.create();

				dialograte.setOnShowListener(new
					DialogInterface.OnShowListener() {
						@Override
						public void onShow(DialogInterface dialog) {
							Button b = dialograte.getButton(AlertDialog.BUTTON_POSITIVE);
							b.setOnClickListener(new
								View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Intent intent = new Intent(Intent.ACTION_VIEW);
										intent.setData(Uri.parse("market://details?id=" + getPackageName()));
										startActivity(intent);
										editor.putBoolean("ratedapp", true);
										editor.commit();
										dialograte.dismiss();
									}
								});
							Button n = dialograte.getButton(AlertDialog.BUTTON_NEGATIVE);
							n.setOnClickListener(new
								View.OnClickListener() {
									@Override
									public void onClick(View view) {
										editor.putLong("longToDialogRate", System.currentTimeMillis() + 8*24*60*60*1000);
										dialograte.dismiss();
									}
								});
						}
					});
				dialograte.show();
			}
		}
		
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
		ActionBarColor(this, titulo);
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

	public void Clear(String value, String label, int thepos) {
		titulo = label;
		passed = true;
		home = false;
		Bundle bundle = new Bundle();
		bundle.putString("label", label);
		bundle.putString("value", value);
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
		}

		String[] socialnetworks = getResources().getStringArray(R.array.allow_sites2);
		int n = socialnetworks.length + 5;
		int n2 = position - socialnetworks.length;

		if(position > 2 && position < n) {
			String[] usernames = getResources().getStringArray(R.array.socialnetworksusers);
			String[] sitenames = getResources().getStringArray(R.array.socialnetworksnames);
			Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			intent.putExtra("titulo", sitenames[n2]);
			intent.putExtra("url", "http://" + socialnetworks[n2] + "/" + usernames[n2]);
			startActivity(intent);
		}

		if(position >= n && position < n + linkscount && linkscount > 0) {
			Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			intent.putExtra("titulo", linksnames.get(position - n));
			intent.putExtra("url", linksurls.get(position - n));
			startActivity(intent);
		}

		if(position >= n + linkscount && position < n + linkscount + fcategoriascount && fcategoriascount > 0) {
			Clear(fcategoriasids.get(position - n - linkscount), fcategoriasnames.get(position - n - linkscount), position);
		}

		if(position >= n + linkscount + fcategoriascount) {
			Clear(categoriasids.get(position - n - linkscount - fcategoriascount), categoriasnames.get(position - n - linkscount - fcategoriascount), position);
		}

		if (position > 2 && position <= n + linkscount) {
			mDrawerList.setItemChecked(pos, true);
		}

		supportInvalidateOptionsMenu();

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
									.load("http://apps.aloogle.net/blogapp/wordpress/json/tags.php?q=" + suggestion.replace(" ", "%20") + "&id=" + getString(R.string.blogid))
									.asJsonObject()
									.setCallback(new FutureCallback<JsonObject>() {
										@Override
										public void onCompleted (Exception e, JsonObject json) {
											if(e != null) {
												return;
											}
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
		int searchIcon = iconColor.equals("branco") ? R.drawable.ic_search : R.drawable.ic_search_black;
		menu.findItem(R.id.menu_search).setIcon(searchIcon);

		if (pos == 0 || pos == 1) {
			menu.findItem(R.id.menu_refresh).setVisible(true);
			menu.findItem(R.id.menu_opensite).setVisible(true);
		} else if (pos > 1 && pos < 11) {
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
		ActionBarColor(this, titulo);
		supportInvalidateOptionsMenu();
		super.onResume();
	}
}
