package net.aloogle.acasadocogumelo.activity;

import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.*;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import com.google.android.gms.ads.*;
import net.aloogle.acasadocogumelo.R;
import net.aloogle.acasadocogumelo.adapter.WebViewAdapter;
import net.aloogle.acasadocogumelo.other.Icons;

@SuppressLint({"SetJavaScriptEnabled","NewApi"})
@SuppressWarnings("deprecation")
public class WebViewActivity extends ActionBarActivity {
	final Context context = this;
	public Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	@SuppressWarnings("unused")
	private CharSequence mDrawerTitle;
	private ArrayList < Icons > icons;
	private WebViewAdapter adapter;
	private String[]MDTitles;
	private TypedArray MDIcons;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor;
	WebView webView;
	ProgressBar progressBar;
	ProgressBar progressBar2;
	private AdView adView;
	int mActionBarSize;

	private FrameLayout mTargetView;
	private FrameLayout mContentView;
	private CustomViewCallback mCustomViewCallback;
	private View mCustomView;
	private webChromeClient mClient;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {}
		else {
			setTheme(R.style.BlackOverflow);
		}

		setContentView(R.layout.webview);

		adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-5148143657396132/1702526203");
		adView.setAdSize(AdSize.BANNER);

		LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);

		layout.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);

		if (Build.VERSION.SDK_INT >= 11) {
			progressBar2 = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
			progressBar2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24));
			final LinearLayout decorView = (LinearLayout)findViewById(R.id.linear);
			decorView.addView(progressBar2);

			final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
					new int[]{
					android.R.attr.actionBarSize
				});
			mActionBarSize = (int)styledAttributes.getDimension(0, 0);
			styledAttributes.recycle();

			ViewTreeObserver observer = progressBar2.getViewTreeObserver();
			observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@TargetApi(11)
				@Override
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT >= 19) {
						progressBar2.setY(mActionBarSize + getStatusBarHeight());
					} else {
						progressBar2.setY(mActionBarSize);
					}

					ViewTreeObserver observer = progressBar2.getViewTreeObserver();
					observer.removeGlobalOnLayoutListener(this);
				}
			});
		}

		progressBar = (ProgressBar)findViewById(R.id.progressBar1);

		webView = (WebView)findViewById(R.id.webview01);
		mClient = new webChromeClient();
		webView.setWebChromeClient(mClient);
		webView.setWebViewClient(new webViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		if (Build.VERSION.SDK_INT >= 11) {
			webView.getSettings().setDisplayZoomControls(false);
		}

		if (savedInstanceState != null) {
			webView.restoreState(savedInstanceState);
		} else {
			if (getIntent().hasExtra("url")) {
				webView.loadUrl(getIntent().getStringExtra("url"));
			} else {
				webView.loadUrl("http://acasadocogumelo.com");
			}
		}

		editor = preferences.edit();
		editor.putString("WebViewLastUrl", webView.getUrl());
		editor.commit();

		mContentView = (FrameLayout)findViewById(R.id.main_content);
		mTargetView = (FrameLayout)findViewById(R.id.target_view);
		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		initDrawer();
		initNotification();
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public void ActionBarColor() {
		String userColor = preferences.getString("prefColor", "padrao");
		if (userColor.equals("padrao")) {
			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_bg));
			findViewById(R.id.frame).setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_bg));
		} else {
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}

		getSupportActionBar().setTitle("");
		getSupportActionBar().setIcon(R.drawable.ic_toolbar);
	}

	public void initDrawer() {
		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		ActionBarColor();

		mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		mDrawerList = (ListView)findViewById(R.id.left_drawer);

		MDTitles = getResources().getStringArray(
				R.array.navigation_main_sections);

		MDIcons = getResources().obtainTypedArray(R.array.drawable_ids);

		icons = new ArrayList < Icons > ();

		icons.add(new Icons(MDTitles[0], MDIcons.getResourceId(0, -1)));
		icons.add(new Icons(MDTitles[1], MDIcons.getResourceId(1, -2)));
		icons.add(new Icons(MDTitles[2], MDIcons.getResourceId(2, -3)));
		icons.add(new Icons(MDTitles[3], MDIcons.getResourceId(3, -4)));
		icons.add(new Icons(MDTitles[4], MDIcons.getResourceId(4, -5)));
		icons.add(new Icons(MDTitles[5], MDIcons.getResourceId(5, -6)));
		icons.add(new Icons(MDTitles[6], MDIcons.getResourceId(6, -7)));
		icons.add(new Icons(MDTitles[7], MDIcons.getResourceId(7, -8)));
		icons.add(new Icons(MDTitles[8], MDIcons.getResourceId(8, -9)));
		icons.add(new Icons(MDTitles[9], MDIcons.getResourceId(9, -10)));
		icons.add(new Icons(MDTitles[10], MDIcons.getResourceId(10, -11)));
		icons.add(new Icons(MDTitles[11], MDIcons.getResourceId(11, -12)));
		icons.add(new Icons(MDTitles[12], MDIcons.getResourceId(12, -13)));
		icons.add(new Icons(MDTitles[13], MDIcons.getResourceId(13, -14)));
		icons.add(new Icons(MDTitles[14], MDIcons.getResourceId(14, -15)));
		icons.add(new Icons(MDTitles[15], MDIcons.getResourceId(15, -16)));

		MDIcons.recycle();

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

		adapter = new WebViewAdapter(getApplicationContext(), icons);
		mDrawerList.setAdapter(adapter);

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

				public void onDrawerClosed(View view) {
					supportInvalidateOptionsMenu();
				}

				public void onDrawerOpened(View drawerView) {
					supportInvalidateOptionsMenu();
				}
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
				final AlertDialog dialogsprites = new AlertDialog.Builder(WebViewActivity.this)
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

	private void selectItem(int position) {
		switch (position) {
		case 0:
			webView.loadUrl("http://acasadocogumelo.com");
			break;
		case 1:
			webView.loadUrl("http://acasadocogumelo.com");
			break;
		case 2:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Nintendo%20Wii%20U");
			break;
		case 3:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Nintendo%203DS");
			break;
		case 4:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Nintendo%20DS");
			break;
		case 5:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Mario");
			break;
		case 6:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Zelda");
			break;
		case 7:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Pok%C3%A9mon");
			break;
		case 8:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Smash%20Bros");
			break;
		case 9:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Donkey%20Kong");
			break;
		case 10:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Yoshi");
			break;
		case 11:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Metroid");
			break;
		case 12:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Xenoblade");
			break;
		case 13:
			webView.loadUrl("http://www.acasadocogumelo.com/search/label/Fire%20Emblem");
			break;
		case 14:
			webView.loadUrl("https://www.facebook.com/pages/A-Casa-do-Cogumelo/137869276285719");
			break;
		case 15:
			webView.loadUrl("https://twitter.com/ACasadoCogumelo");
			break;
		case 16:
			webView.loadUrl("https://www.youtube.com/channel/UCEHol_7LxomGTVIKopJfQXA");
			break;
		case 17:
			Intent settings = new Intent(WebViewActivity.this, FragmentActivity.class);
			settings.putExtra("fragment", 0);
			startActivity(settings);
			break;
		case 18:
			Intent about = new Intent(WebViewActivity.this, FragmentActivity.class);
			about.putExtra("fragment", 2);
			startActivity(about);
			break;
		}

		mDrawerList.setItemChecked(position, true);

		mDrawerLayout.closeDrawer(mDrawerList);
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

	private class DrawerItemClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView <  ?  > parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.webview_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_search);
		SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint(getString(R.string.search));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				progressBar.setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
				webView.loadUrl("http://www.acasadocogumelo.com/search?q=" + query);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String txt) {
				return false;
			}
		});

		SearchView.SearchAutoComplete
		theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);

		if (iconcolor.equals("branco")) {
			theTextArea.setTextColor(Color.WHITE);
		} else {
			theTextArea.setTextColor(Color.BLACK);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (Build.VERSION.SDK_INT >= 19) {
			if (webView.getUrl().replace("?m=1", "").equals("http://acasadocogumelo.com/") || webView.getUrl().replace("?m=1", "").equals("http://acasadocogumelo.com") || webView.getUrl().replace("?m=1", "").equals("http://www.acasadocogumelo.com/") || webView.getUrl().replace("?m=1", "").equals("http://www.acasadocogumelo.com") || webView.getUrl().contains("acasadocogumelo.com/search")) {
				menu.findItem(R.id.menu_share).setVisible(false);
				menu.findItem(R.id.menu_comments).setVisible(false);
			} else {
				if (webView.getUrl().contains("acasadocogumelo.com")) {
					menu.findItem(R.id.menu_share).setVisible(true);
					menu.findItem(R.id.menu_comments).setVisible(true);
				} else if (webView.getUrl().contains(".jpg") || webView.getUrl().contains(".png") || webView.getUrl().contains(".gif")) {
					menu.findItem(R.id.menu_share).setVisible(true);
					menu.findItem(R.id.menu_comments).setVisible(false);
				} else {
					menu.findItem(R.id.menu_share).setVisible(false);
					menu.findItem(R.id.menu_comments).setVisible(false);
				}
			}
		}

		if (webView.canGoBack()) {
			menu.findItem(R.id.menu_back).setEnabled(true);
		} else {
			menu.findItem(R.id.menu_back).setEnabled(false);
		}
		if (webView.canGoForward()) {
			menu.findItem(R.id.menu_forward).setEnabled(true);
		} else {
			menu.findItem(R.id.menu_forward).setEnabled(false);
		}

		if (iconcolor.equals("branco")) {
			menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search_white);
			menu.findItem(R.id.menu_share).setIcon(R.drawable.ic_share_white);
			menu.findItem(R.id.menu_comments).setIcon(R.drawable.ic_fbcomments_white);
		} else {
			menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search_black);
			menu.findItem(R.id.menu_share).setIcon(R.drawable.ic_share_black);
			menu.findItem(R.id.menu_comments).setIcon(R.drawable.ic_fbcomments_black);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String lastUrl = preferences.getString("WebViewLastUrl", "http://acasadocogumelo.com");
		switch (item.getItemId()) {
		case R.id.menu_back:
			webView.goBack();
			return true;
		case R.id.menu_forward:
			webView.goForward();
			return true;
		case R.id.menu_reload:
			webView.loadUrl(lastUrl);
			return true;
		case R.id.menu_share:
			Intent sharePageIntent = new Intent();
			sharePageIntent.setAction(Intent.ACTION_SEND);
			sharePageIntent.putExtra(Intent.EXTRA_TEXT, webView.getTitle() + " " + webView.getUrl().replace("?m=1", ""));
			sharePageIntent.setType("text/plain");
			startActivity(Intent.createChooser(sharePageIntent, getResources().getText(R.string.sharepage)));
			return true;
		case R.id.menu_cache:
			webView.clearCache(true);
			Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.cache2), Toast.LENGTH_LONG);
			toast.show();
			return true;
		case R.id.menu_comments:
			Intent comments = new Intent(WebViewActivity.this, CommentActivity.class);
			comments.putExtra("fburl", webView.getUrl());
			startActivity(comments);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class webViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (Build.VERSION.SDK_INT >= 11) {
				progressBar2.setVisibility(View.VISIBLE);
			}
			editor.putString("WebViewLastUrl", url);
			editor.commit();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("acasadocogumelo.com") || url.contains("twitter.com/ACasadoCogumelo") || url.contains("facebook.com/acasadocogumelo") || url.contains("channel/UCEHol_7LxomGTVIKopJfQXA") || url.contains("youtube.com/watch") || url.contains(".jpg") || url.contains(".png") || url.contains(".gif")) {
				view.loadUrl(url);
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WebViewActivity.this);
				Editor editor = preferences.edit();
				editor.putString("WebViewLastUrl", url);
				editor.commit();
			} else {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			String erro = "<html><head><style>body { background-image: url('erro.png'), url('background.png'); background-repeat: no-repeat, repeat; background-position: center; min-height: 431px; }</style></head></html>";
			webView.loadDataWithBaseURL("file:///android_asset/", erro, "text/html", "utf-8", null);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	public class webChromeClient extends WebChromeClient {
		public void onProgressChanged(WebView view, int progress) {
			if (Build.VERSION.SDK_INT >= 11) {
				progressBar2.setProgress(progress);
				if (progress == 100) {
					progressBar2.setVisibility(View.GONE);
				}
			} else {
				getSupportActionBar().setTitle(String.valueOf(progress) + "%");
				if (progress == 100) {
					getSupportActionBar().setTitle("");
				}
			}
			if (progress >= 50) {
				progressBar.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
				supportInvalidateOptionsMenu();
			}
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			mCustomViewCallback = callback;
			mTargetView.addView(view);
			mCustomView = view;
			mContentView.setVisibility(View.GONE);
			mTargetView.setVisibility(View.VISIBLE);
			mTargetView.bringToFront();
			getSupportActionBar().hide();
		}

		@Override
		public void onHideCustomView() {
			if (mCustomView == null)
				return;

			mCustomView.setVisibility(View.GONE);
			mTargetView.removeView(mCustomView);
			mCustomView = null;
			mTargetView.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();
			mContentView.setVisibility(View.VISIBLE);
			getSupportActionBar().show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCustomView != null) {
				mClient.onHideCustomView();
				return true;
			} else {
				boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
				if (drawerOpen) {
					mDrawerLayout.closeDrawer(mDrawerList);
					return true;
				} else {
					if (webView.canGoBack()) {
						webView.goBack();
						return true;
					}
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();
		adView.pause();
		try {
			Class.forName
			("android.webkit.WebView")
			.getMethod
			("onPause", (Class[])null)
			.invoke
			(webView, (Object[])null);
		} catch (ClassNotFoundException cnfe) {}

		catch (NoSuchMethodException nsme) {}

		catch (InvocationTargetException ite) {}

		catch (IllegalAccessException iae) {}
	}

	public void onResume() {
		super.onResume();
		adView.resume();
		ActionBarColor();
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		webView.saveState(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}
}
