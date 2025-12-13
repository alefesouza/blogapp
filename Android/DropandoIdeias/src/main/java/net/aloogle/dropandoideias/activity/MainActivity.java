package net.aloogle.dropandoideias.activity;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
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
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.adapter.DrawerAdapter;
import net.aloogle.dropandoideias.database.helper.DatabaseHelper;
import net.aloogle.dropandoideias.database.model.Jsons;
import net.aloogle.dropandoideias.fragment.CategoryFragment;
import net.aloogle.dropandoideias.fragment.FavoritesFragment;
import net.aloogle.dropandoideias.fragment.MainFragment;
import net.aloogle.dropandoideias.fragment.PostFragment;
import net.aloogle.dropandoideias.other.Categorias;
import net.aloogle.dropandoideias.other.CustomTextView;
import net.aloogle.dropandoideias.other.Icons;
import net.aloogle.dropandoideias.other.Other;

@SuppressLint({ "DefaultLocale", "CutPasteId", "Recycle"})
public class MainActivity extends AppCompatActivity {
	final Context context = this;
	public Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList <Icons> icons = new ArrayList < Icons > ();

	ArrayList <Categorias> listlinks = new ArrayList <Categorias> ();
	ArrayList <Categorias> listfcategorias = new ArrayList <Categorias> ();
	ArrayList <Categorias> listcategorias = new ArrayList <Categorias> ();

	String[]sitenames;
	private DrawerAdapter adapter2;
	private TypedArray categoryIcons, socialIcons;
	public static FloatingActionButton fabrandom;
	SharedPreferences preferences;
	Editor editor;
	String titulo, suggestion, iconColor, linksname, fcategoriasname, drawerJson, categsJson;
	public static int pos;
	int linkscount, categoriastotal, fcategoriascount, storedPos;
	boolean passed, start, home, drawerloaded;

	ViewGroup footer, footer2, footer3, footer4, footer5;

	private View mToolbarView;
	private ViewPager mPager;
	private NavigationAdapter mPagerAdapter;

	private static final String TAG_CATEGORIA = "category";
	ArrayList <String> categoriaarray = new ArrayList <String> ();

	ArrayList <String> reallyarray = new ArrayList <String> ();
	private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		iconColor = preferences.getString("prefIconColor", "branco");
		if (iconColor.equals("preto")) {
			setTheme(R.style.BlackOverflow);
		}
		setContentView(R.layout.toolbar_drawer);

		sitenames = getResources().getStringArray(R.array.socialnetworksnames);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		titulo = getString(R.string.app_name);

		initDrawer(0);
		new CompareIfExists().execute();
		initNotification();

		ActionBarColor(this, titulo);

		final String[]from = new String[]{
			"categoryName"
		};
		final int[]to = new int[]{
			R.id.text1
		};
		mAdapter = new SimpleCursorAdapter(this, 			R.layout.simple_list_item_1, 			null, 			from, 			to, 			CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		mToolbarView = findViewById(R.id.toolbar);
		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

		ViewCompat.setElevation(mToolbarView, R.dimen.toolbar_elevation);

		pos = 1;
		storedPos = 0;
		passed = false;
		start = false;

		fabrandom = (FloatingActionButton)findViewById(R.id.fabrandom);
		fabrandom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View p1) {
				if (Other.isConnected(MainActivity.this)) {
					Toast toast = Toast.makeText(MainActivity.this, "Carregando...", Toast.LENGTH_LONG);
					toast.show();
					Ion.with (MainActivity.this)
					.load(Other.defaultUrl + "aleatorio.php?id=" + getString(R.string.blogid) + "&start=" + getString(R.string.randomstart))
					.asJsonObject()
					.setCallback(new FutureCallback < JsonObject > () {
						@Override
						public void onCompleted(Exception e, JsonObject json) {
							if (e != null) {
								Toast toastt = Toast.makeText(MainActivity.this, "Houve um erro ao buscar por post aleatório, tente novamente", Toast.LENGTH_SHORT);
								toastt.show();
								return;
							}
                            boolean istablet = context.getResources().getBoolean(R.bool.isTablet);
                            if (!istablet) {
                                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                                intent.putExtra("id", json.get("id").getAsString());
                                intent.putExtra("fromrandom", true);
                                startActivity(intent);
                            } else {
                                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                                Fragment post = new PostFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", json.get("id").getAsString());
                                bundle.putBoolean("fromrandom", true);
                                post.setArguments(bundle);
                                ft.replace(R.id.post_frame, post);
                                ft.commit();
                            }
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
			storedPos = savedInstanceState.getInt("position");
		}

		if (Other.isConnected(this)) {
			loadDrawer();
		}
		selectItem(1);

		loadCategs();
	}

	private class saveCategs extends AsyncTask <String, Jsons, Jsons> {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());

		@Override
		protected Jsons doInBackground(String...args) {
			Jsons categs = db.getJson("categories");
			if (categs == null) {
				db.createJson(new Jsons("categories", categsJson));
				return categs;
			} else {
				if (!categs.getJson().equals(drawerJson)) {
					categs = new Jsons("categories", categsJson);
					db.updateJson(categs);
					return categs;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Jsons categs) {
			db.closeDB();
		}
	}

	private class CompareIfExists extends AsyncTask <String, Jsons, Jsons> {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());

		@Override
		protected Jsons doInBackground(String...args) {
			Jsons drawer = db.getJson("drawer");
			return drawer;
		}

		@Override
		protected void onPostExecute(Jsons drawer) {
			String jsons = null;
			if (drawer != null) {
				jsons = drawer.getJson();
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject)parser.parse(jsons);
				makeDrawer(json);
			}
			db.closeDB();
		}
	}

	private class IfHasNewDrawer extends AsyncTask <String, Jsons, Jsons> {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());

		@Override
		protected Jsons doInBackground(String...args) {
			Jsons drawer = db.getJson("drawer");
			if (drawer == null) {
				drawer = new Jsons("drawer", drawerJson);
				db.createJson(drawer);
				return drawer;
			} else {
				if (!drawer.getJson().equals(drawerJson)) {
					drawer = new Jsons("drawer", drawerJson);
					db.updateJson(drawer);
					icons.clear();
					listlinks.clear();
					listfcategorias.clear();
					listcategorias.clear();
					if (footer5 != null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mDrawerList.removeFooterView(footer5);
							}
						});
					}
					headerIcons();
					return drawer;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Jsons drawer) {
			if (drawer != null) {
				String jsons = drawer.getJson();
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject)parser.parse(jsons);
				makeDrawer(json);
			}
			db.closeDB();
		}
	}

	public void loadCategs() {
		Ion.with (this)
		.load(Other.defaultUrl + "allcategs.php?id=" + getString(R.string.blogid))
		.asJsonObject()
		.setCallback(new FutureCallback < JsonObject > () {
			@Override
			public void onCompleted(Exception e, JsonObject json) {
				if (e != null) {
					e.printStackTrace();
					return;
				}

				categsJson = json.toString();
				new saveCategs().execute();
			}
		});
	}

	public void loadDrawer() {
		Ion.with (this)
		.load(Other.defaultUrl + "categorias.php?id=" + getString(R.string.blogid))
		.asJsonObject()
		.setCallback(new FutureCallback < JsonObject > () {
			@Override
			public void onCompleted(Exception e, JsonObject json) {
				if (e != null) {
					if (!drawerloaded) {
						CustomTextView text = (CustomTextView)footer4.findViewById(R.id.myTextView4);
						text.setText("Tentar novamente");

						footer4.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (Other.isConnected(MainActivity.this)) {
									mDrawerList.removeFooterView(footer);
									mDrawerList.removeFooterView(footer2);
									mDrawerList.removeFooterView(footer4);
									mDrawerList.addFooterView(footer3, null, false);
									mDrawerList.addFooterView(footer, null, false);
									mDrawerList.addFooterView(footer2, null, false);
									footer4 = null;
									loadDrawer();
								} else {
									Toast toast = Toast.makeText(MainActivity.this, getString(R.string.needinternet), Toast.LENGTH_SHORT);
									toast.show();
								}
							}
						});
						mDrawerList.removeFooterView(footer3);
						mDrawerList.removeFooterView(footer);
						mDrawerList.removeFooterView(footer2);
						mDrawerList.addFooterView(footer4, null, false);
						mDrawerList.addFooterView(footer, null, false);
						mDrawerList.addFooterView(footer2, null, false);
					}
					e.printStackTrace();
					return;
				}

				drawerJson = json.toString();
				new IfHasNewDrawer().execute();
			}
		});
	}

	public void makeDrawer(JsonObject json) {
		JsonArray links = json.get("links").getAsJsonObject().get("links").getAsJsonArray();
		for (int i = 0; i < links.size(); i++) {
			JsonObject c = links.get(i).getAsJsonObject();

			String url = c.get("url").getAsString();
			String name = c.get("name").getAsString();
			String icon = c.get("icon").getAsString();

			listlinks.add(new Categorias(url, name, icon));
		}
		linksname = json.get("links").getAsJsonObject().get("name").getAsString();

		JsonArray playlists = json.get("categories").getAsJsonObject().get("categories").getAsJsonArray();
		for (int i = 0; i < playlists.size(); i++) {
			JsonObject c = playlists.get(i).getAsJsonObject();

			String id = c.get("id").getAsString();
			String name = c.get("name").getAsString();
			String icon = c.get("icon").getAsString();

			listcategorias.add(new Categorias(id, name, icon));
		}
		categoriastotal = json.get("categories").getAsJsonObject().get("total").getAsInt();

		JsonArray fplaylists = json.get("featuredcategories").getAsJsonObject().get("featuredcategories").getAsJsonArray();
		for (int i = 0; i < fplaylists.size(); i++) {
			JsonObject c = fplaylists.get(i).getAsJsonObject();

			String id = c.get("id").getAsString();
			String name = c.get("name").getAsString();
			String icon = c.get("icon").getAsString();

			listfcategorias.add(new Categorias(id, name, icon));
		}
		fcategoriasname = json.get("featuredcategories").getAsJsonObject().get("name").getAsString();

		drawerloaded = true;
		initDrawer(1);
		if (pos < 2) {
			mDrawerList.setItemChecked(1, true);
		}

		if (storedPos > 1) {
			selectItem(storedPos);
		}
	}

	@SuppressWarnings("deprecation")
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
		if (fase == 0) {
			mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

			mDrawerList = (ListView)findViewById(R.id.left_drawer);

			mDrawerToggle = new ActionBarDrawerToggle(
					this, 				mDrawerLayout, 				mToolbar, 				R.string.drawer_open, 				R.string.drawer_close) {

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

			final ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header, 			mDrawerList, false);
			footer = (ViewGroup)inflater.inflate(R.layout.footer, 			mDrawerList, false);
			footer2 = (ViewGroup)inflater.inflate(R.layout.footer2, 			mDrawerList, false);
			footer3 = (ViewGroup)inflater.inflate(R.layout.footer3, 			mDrawerList, false);
			footer4 = (ViewGroup)inflater.inflate(R.layout.footer4, 			mDrawerList, false);

			mDrawerList.addHeaderView(header, null, true);
			mDrawerList.addFooterView(footer3, null, false);
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

			headerIcons();

			adapter2 = new DrawerAdapter(getApplicationContext(), icons);
			mDrawerList.setAdapter(adapter2);

			mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

			mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		} else {
			if (footer4 != null) {
				mDrawerList.removeFooterView(footer4);
			}

			if (listlinks.size() > 0) {
				icons.add(new Icons(linksname, categoryIcons.getResourceId(0, -1), 5, ""));
				for (int i = 0; i < listlinks.size(); i++) {
					icons.add(new Icons(listlinks.get(i).getTitle(), categoryIcons.getResourceId(2, (i + 1)*-1), 2, listlinks.get(i).getIcon()));
				}
				linkscount = listlinks.size() + 1;
			}

			if (listfcategorias.size() > 0) {
				icons.add(new Icons(fcategoriasname, categoryIcons.getResourceId(0, -1), 5, ""));
				for (int i = 0; i < listfcategorias.size(); i++) {
					icons.add(new Icons(listfcategorias.get(i).getTitle(), categoryIcons.getResourceId(3, (i + 1)*-1), 3, listfcategorias.get(i).getIcon()));
				}
				fcategoriascount = listfcategorias.size() + 1;
			}

			if (listcategorias.size() > 0) {
				icons.add(new Icons("Categorias", categoryIcons.getResourceId(0, -1), 5, ""));
				for (int i = 0; i < listcategorias.size(); i++) {
					icons.add(new Icons(listcategorias.get(i).getTitle(), categoryIcons.getResourceId(3, (i + 1)*-1), 4, listcategorias.get(i).getIcon()));
				}
			}

			if (categoriastotal > 15) {
				footer5 = (ViewGroup)inflater.inflate(R.layout.footer4, 				mDrawerList, false);
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

			mDrawerList.removeFooterView(footer3);
		}
	}

	public void headerIcons() {
		icons.add(new Icons("Início", categoryIcons.getResourceId(0, -1), 0, ""));
		icons.add(new Icons("Favoritos", categoryIcons.getResourceId(1, -1), 0, ""));
		icons.add(new Icons("Rede sociais", categoryIcons.getResourceId(0, -1), 5, ""));
		for (int i = 0; i < sitenames.length; i++) {
			icons.add(new Icons(sitenames[i], socialIcons.getResourceId(i, -1), 1, ""));
		}
	}

	public void initNotification() {
		boolean notification = preferences.getBoolean("prefNotification", true);
		boolean haveTimerToDialogRate = preferences.getBoolean("haveTimerToDialog", false);
		boolean rated = preferences.getBoolean("ratedapp", false);

		if (!haveTimerToDialogRate) {
			editor.putLong("longToDialogRate", System.currentTimeMillis() + 3*24*60*60*1000);
			editor.commit();
			editor.putBoolean("haveTimerToDialog", true);
			editor.commit();
		}

		if (!rated) {
			if (System.currentTimeMillis() > preferences.getLong("longToDialogRate", 0)) {
				final AlertDialog dialograte = new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.rateapp)
					.setMessage(R.string.dialog_rate)
					.setPositiveButton(R.string.rate, null)
					.setNegativeButton(R.string.later, null)
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
								editor.commit();
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
				final AlertDialog dialognotif = new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.notifications)
					.setMessage(R.string.dialog_notification)
					.setPositiveButton(R.string.yes, null)
					.setNegativeButton(R.string.no, null)
					.create();

				dialognotif.setOnShowListener(new
					DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						Button b = dialognotif.getButton(AlertDialog.BUTTON_POSITIVE);
						b.setOnClickListener(new
							View.OnClickListener() {
							@Override
							public void onClick(View view) {
								editor.putBoolean("prefNotification", true);
								editor.commit();
								editor.putLong("longNotification", 0);
								editor.commit();
								dialognotif.dismiss();
							}
						});
						Button n = dialognotif.getButton(AlertDialog.BUTTON_NEGATIVE);
						n.setOnClickListener(new
							View.OnClickListener() {
							@Override
							public void onClick(View view) {
								editor.putLong("longNotification", System.currentTimeMillis() + 15*24*60*60*1000);
								editor.commit();
								dialognotif.dismiss();
							}
						});
					}
				});
				dialognotif.show();
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

		String[]socialnetworks = getResources().getStringArray(R.array.allow_sites2);
		int n = socialnetworks.length + 5;
		int n2 = position - socialnetworks.length;

		if (position > 2 && position < n) {
			String[]usernames = getResources().getStringArray(R.array.socialnetworksusers);
			Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			intent.putExtra("titulo", sitenames[n2]);
			intent.putExtra("url", "http://" + socialnetworks[n2] + "/" + usernames[n2]);
			startActivity(intent);
		}

		if (position >= n && position < n + linkscount && linkscount > 0) {
			Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			intent.putExtra("titulo", listlinks.get(position - n).getTitle());
			intent.putExtra("url", listlinks.get(position - n).getId());
			startActivity(intent);
		}

		if (position >= n + linkscount && position < n + linkscount + fcategoriascount && fcategoriascount > 0) {
			Clear(listfcategorias.get(position - n - linkscount).getId(), listfcategorias.get(position - n - linkscount).getTitle(), position);
		}

		if (position >= n + linkscount + fcategoriascount) {
			Clear(listcategorias.get(position - n - linkscount - fcategoriascount).getId(), listcategorias.get(position - n - linkscount - fcategoriascount).getTitle(), position);
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
								Ion.with (MainActivity.this)
								.load(Other.defaultUrl + "tags.php?q=" + suggestion.replace(" ", "%20") + "&id=" + getString(R.string.blogid))
								.asJsonObject()
								.setCallback(new FutureCallback < JsonObject > () {
									@Override
									public void onCompleted(Exception e, JsonObject json) {
										if (e != null) {
											return;
										}
										categoriaarray.clear();
										JsonArray categorias = json.get("categories").getAsJsonArray();
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
			intent.setData(Uri.parse("http://" + getString(R.string.sitename)));
			startActivity(intent);
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	private void populateAdapter() {
		final MatrixCursor c = new MatrixCursor(new String[]{
				BaseColumns._ID, 			"categoryName"
			});
		for (int i = 0; i < categoriaarray.size(); i++) {
			if (categoriaarray.get(i).toString().toLowerCase().startsWith(suggestion.toLowerCase())) {
				reallyarray.add(categoriaarray.get(i).toString());
				c.addRow(new Object[]{
					i, 				categoriaarray.get(i).toString()
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
