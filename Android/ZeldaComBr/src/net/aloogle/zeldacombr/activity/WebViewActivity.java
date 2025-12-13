package net.aloogle.zeldacombr.activity;

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
import net.aloogle.zeldacombr.R;
import net.aloogle.zeldacombr.adapter.WebViewAdapter;
import net.aloogle.zeldacombr.other.Icons;

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
	WebView webView;
	ProgressBar progressBar;
	ProgressBar progressBar2;
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

		setContentView(R.layout.webview);

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
				webView.loadUrl("http://apps.aloogle.net/blogapp/zeldacombr/alooglelayout.php");
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
		
		findViewById(R.id.fab).setOnClickListener(new View. OnClickListener(){
				@Override public void onClick(View v){
					webView.loadUrl("http://apps.aloogle.net/blogapp/zeldacombr/alooglelayout.php");
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
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2e7d32")));
		findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2e7d32")));
		getSupportActionBar().setTitle(getString(R.string.app_name));
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
			webView.loadUrl("http://www.zelda.com.br");
			break;
		case 1:
			webView.loadUrl("http://www.zelda.com.br");
			break;
		case 2:
			webView.loadUrl("http://www.zelda.com.br/artigos");
			break;
		case 3:
			webView.loadUrl("http://www.zelda.com.br/noticias");
			break;
		case 4:
			webView.loadUrl("http://www.zelda.com.br/podcast");
			break;
		case 5:
			webView.loadUrl("http://www.zelda.com.br/jogos");
			break;
		case 6:
			webView.loadUrl("http://www.zelda.com.br/spinoff");
			break;
		case 7:
			webView.loadUrl("http://www.zelda.com.br/detonados");
			break;
		case 8:
			webView.loadUrl("http://www.zelda.com.br/traducoes");
			break;
		case 9:
			webView.loadUrl("http://apps.aloogle.net/blogapp/zeldacombr/midia.php");
			break;
		case 10:
			webView.loadUrl("https://facebook.com/hyrulelegends");
			break;
		case 11:
			webView.loadUrl("https://twitter.com/hyrulelegends");
			break;
		case 12:
			webView.loadUrl("http://youtube.com/user/hyrulelegends");
			break;
		case 13:
			Intent settings = new Intent(WebViewActivity.this, FragmentActivity.class);
			settings.putExtra("fragment", 0);
			startActivity(settings);
			break;
		case 14:
			Intent about = new Intent(WebViewActivity.this, FragmentActivity.class);
			about.putExtra("fragment", 1);
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
				webView.loadUrl("http://www.zelda.com.br/search/node/" + query);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String txt) {
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (Build.VERSION.SDK_INT >= 19) {
			if (webView.getUrl().equals("http://zelda.com.br/") || webView.getUrl().equals("http://zelda.com.br") || webView.getUrl().equals("http://www.zelda.com.br/") || webView.getUrl().equals("http://www.zelda.com.br") || webView.getUrl().contains("hyrulelegends.com/search")) {
				menu.findItem(R.id.menu_share).setVisible(false);
			} else {
				if (webView.getUrl().contains("zelda.com.br")) {
					menu.findItem(R.id.menu_share).setVisible(true);
				} else if (webView.getUrl().contains(".jpg") || webView.getUrl().contains(".png") || webView.getUrl().contains(".gif")) {
					menu.findItem(R.id.menu_share).setVisible(true);
				} else {
					menu.findItem(R.id.menu_share).setVisible(false);
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
		
		menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search_white);
		menu.findItem(R.id.menu_share).setIcon(R.drawable.ic_share_white);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		String lastUrl = preferences.getString("WebViewLastUrl", "http://zelda.com.br");
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
			if (url.contains("zelda.com.br") || url.contains("twitter.com/hyrulelegends") || url.contains("apps.aloogle.net/blogapp/zeldacombr") || url.contains("facebook.com") || url.contains("user/hyrulelegends") || url.contains("youtube.com/watch") || url.contains(".jpg") || url.contains(".png") || url.contains(".gif")) {
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
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2e7d32")));
			findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2e7d32")));
			
			webView.getSettings().setUseWideViewPort(false);
			webView.setInitialScale(0);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			webView.getSettings().setUseWideViewPort(true);
			webView.setInitialScale(50);
			String erro = "<html><head><style>body { background-image: url('error.png'); background-color: #d40000; background-repeat: no-repeat; background-position: center; min-height: 650px; min-width: 650px; }</style></head></html>";
			webView.loadDataWithBaseURL("file:///android_asset/", erro, "text/html", "utf-8", null);
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffd40000")));
			findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffd40000")));
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
					getSupportActionBar().setTitle(getString(R.string.app_name));
				}
			}
			if (progress >= 50) {
				progressBar.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);

				if (webView.getUrl().equals("http://apps.aloogle.net/blogapp/zeldacombr/alooglelayout.php")) {
					findViewById(R.id.fab).setVisibility(View.GONE);
				} else {
					findViewById(R.id.fab).setVisibility(View.VISIBLE);
				}
				
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

	public void onSaveInstanceState(Bundle savedInstanceState) {
		webView.saveState(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}
}
