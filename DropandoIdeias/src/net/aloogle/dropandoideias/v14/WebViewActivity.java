package net.aloogle.dropandoideias.v14;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;
import net.aloogle.dropandoideias.R;

@SuppressLint("NewApi")public class WebViewActivity extends Activity {

	WebView webView;
	ProgressBar progressBar;

	private FrameLayout mTargetView;
	private FrameLayout mContentView;
	private CustomViewCallback mCustomViewCallback;
	private View mCustomView;
	private webChromeClient mClient;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private String[]mDrawerTitles;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		progressBar = (ProgressBar)findViewById(R.id.progressBar1);

		webView = (WebView)findViewById(R.id.webview01);
		mClient = new webChromeClient();
		webView.setWebChromeClient(mClient);
		webView.setWebViewClient(new webViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);
		webView.loadUrl("http://dropandoideias.com");

		mContentView = (FrameLayout)findViewById(R.id.main_content);
		mTargetView = (FrameLayout)findViewById(R.id.target_view);
		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		mDrawerTitles = getResources().getStringArray(R.array.drawer_array);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerList = (ListView)findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerList.setAdapter(new ArrayAdapter < String > (this, R.layout.drawer_list_item, mDrawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView <  ?  > parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		switch (position) {
		case 0:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://dropandoideias.com");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 1:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://dropandoideias.com/category/animes-2");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 2:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://dropandoideias.com/category/blogsfera");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 3:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://dropandoideias.com/category/games-2");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 4:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://dropandoideias.com/category/internet-2");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 5:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://dropandoideias.com/category/livros-2");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 6:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://dropandoideias.com/category/nerdices");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 7:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://facebook.com/DropandoIdeias");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 8:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://twitter.com/dropandoideias");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		case 9:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl("http://youtube.com/user/dropandoideias");
			mDrawerLayout.closeDrawer(mDrawerList);
			break;
		default:
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_webview, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_search);
		SearchView searchView = (SearchView)searchItem.getActionView();
		searchView.setQueryHint(getString(R.string.queryhint));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				progressBar.setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
				webView.loadUrl("http://dropandoideias.com/index.php?s=" + query);
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
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.menu_search).setVisible(!drawerOpen);
		menu.findItem(R.id.menu_back).setVisible(!drawerOpen);
		menu.findItem(R.id.menu_reload).setVisible(!drawerOpen);
		menu.findItem(R.id.menu_forward).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.menu_back:
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.nopages), Toast.LENGTH_LONG);
				toast.show();
			}
			return true;
		case R.id.menu_forward:
			if (webView.canGoForward()) {
				webView.goForward();
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.nopages), Toast.LENGTH_LONG);
				toast.show();
			}
			return true;
		case R.id.menu_reload:
			progressBar.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.reload();
			return true;
		case R.id.menu_share:
			Intent sharePageIntent = new Intent();
			sharePageIntent.setAction(Intent.ACTION_SEND);
			sharePageIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
			sharePageIntent.setType("text/plain");
			startActivity(Intent.createChooser(sharePageIntent, getResources().getText(R.string.app_name)));
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = preferences.edit();
			editor.putString("WebViewLastUrl", webView.getUrl());
			editor.commit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class webViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("dropandoideias.com") || url.contains("twitter.com/dropandoideias") || url.contains("facebook.com/DropandoIdeias") || url.contains("/user/dropandoideias") || url.contains("youtube.com/watch")) {
				progressBar.setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
				view.loadUrl(url);
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
			progressBar.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			String erro = "<html><head><style>body { background-image: url('erro.png'); background-color: #1b1b1b; background-repeat: no-repeat; background-position: center; min-height: 350px; }</style></head></html>";
			webView.loadDataWithBaseURL("file:///android_asset/", erro, "text/html", "utf-8", null);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	class webChromeClient extends WebChromeClient {

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			mCustomViewCallback = callback;
			mTargetView.addView(view);
			mCustomView = view;
			mContentView.setVisibility(View.GONE);
			mTargetView.setVisibility(View.VISIBLE);
			mTargetView.bringToFront();
			getActionBar().hide();
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
			getActionBar().show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCustomView != null) {
				mClient.onHideCustomView();
				return true;
			} else {
				if (webView.canGoBack()) {
					webView.goBack();
					return true;
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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = preferences.edit();
		editor.putString("WebViewLastUrl", webView.getUrl());
		editor.commit();
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String lastUrl = preferences.getString("WebViewLastUrl", "http://dropandoideias.com");
		webView.loadUrl(lastUrl);
	}
}
