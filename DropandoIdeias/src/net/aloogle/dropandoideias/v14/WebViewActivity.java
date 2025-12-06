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
import android.view.Window;
import android.view.ViewGroup;
import android.widget.RelativeLayout.*;
import android.view.*;
import android.view.ViewTreeObserver.*;

@SuppressLint("NewApi")
public class WebViewActivity extends Activity {

	WebView webView;
	ProgressBar progressBar;
	ProgressBar progressBar2;

	private FrameLayout mTargetView;
	private FrameLayout mContentView;
	private CustomViewCallback mCustomViewCallback;
	private View mCustomView;
	private webChromeClient mClient;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		progressBar = (ProgressBar)findViewById(R.id.progressBar1);

		progressBar2 = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
		progressBar2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24));

		final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
		decorView.addView(progressBar2);

		ViewTreeObserver observer = progressBar2.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					View contentView = decorView.findViewById(android.R.id.content);
					progressBar2.setY(contentView.getY() - 10);

					ViewTreeObserver observer = progressBar2.getViewTreeObserver();
					observer.removeGlobalOnLayoutListener(this);
				}
			});

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
					webView.loadUrl("http://dropandoideias.com/index.php?s=" + query);
					return false;
				}

				@Override
				public boolean onQueryTextChange(String txt) {
					return false;
				}
			});

		int linlayId = getResources().getIdentifier("android:id/search_plate", null, null);
        ViewGroup v = (ViewGroup) searchView.findViewById(linlayId);
        v.setBackgroundResource(R.drawable.apptheme_textfield_activated_holo_light);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String lastUrl = preferences.getString("WebViewLastUrl", "http://dropandoideias.com");
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
				webView.loadUrl(lastUrl);
				return true;
			case R.id.menu_share:
				Intent sharePageIntent = new Intent();
				sharePageIntent.setAction(Intent.ACTION_SEND);
				sharePageIntent.putExtra(Intent.EXTRA_TEXT, lastUrl);
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
			progressBar2.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("dropandoideias.com") || url.contains("twitter.com/dropandoideias") || url.contains("facebook.com/DropandoIdeias") || url.contains("/user/dropandoideias") || url.contains("youtube.com/watch")) {
				view.loadUrl(url);
			} else {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WebViewActivity.this);
			Editor editor = preferences.edit();
			editor.putString("WebViewLastUrl", url);
			editor.commit();
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
		public void onProgressChanged(WebView view, int progress) {
			progressBar2.setProgress(progress);

			if(progress == 100) { progressBar2.setVisibility(View.GONE); }
		}

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