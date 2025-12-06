package net.aloogle.dropandoideias.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver. * ;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import java.lang.reflect.InvocationTargetException;
import net.aloogle.dropandoideias.R;

@SuppressLint({"SetJavaScriptEnabled","NewApi"})
@SuppressWarnings("deprecation")
public class CommentActivity extends ActionBarActivity {
	final Context context = this;
	public Toolbar mToolbar;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor;
	WebView webView;
	ProgressBar progressBar;
	ProgressBar progressBar2;
	int mActionBarSize;
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

		setContentView(R.layout.comments);

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

		String receivedUrl = getIntent().getStringExtra("fburl").replace("?m=1", "");
		webView.loadUrl("https://www.facebook.com/plugins/comments.php?href=" + receivedUrl + "&locale=pt_BR&numposts=5");

		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ActionBarColor();
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
		String userColor = preferences.getString("prefColor", "ff222222");
		if (userColor.equals("fundo")) {
			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_bg));
			findViewById(R.id.frame).setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_bg));
		} else {
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}

		String iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {
			getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">Comentários</font>"));
		} else {
			getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">Comentários</font>"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.webview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_search).setVisible(false);
		menu.findItem(R.id.menu_share).setVisible(false);
		menu.findItem(R.id.menu_cache).setVisible(false);

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
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back:
			webView.goBack();
			return true;
		case R.id.menu_forward:
			webView.goForward();
			return true;
		case R.id.menu_reload:
			webView.reload();
			return true;
		case android.R.id.home:
			CommentActivity.this.finish();
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
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("dropandoideias.com") || url.contains("facebook.com")) {
				view.loadUrl(url);
			} else {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			supportInvalidateOptionsMenu();
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			String erro = "<html><head><style>body { background-image: url('erro.png'); background-repeat: no-repeat; background-position: center; min-height: 431px; }</style></head></html>";
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
					getSupportActionBar().setTitle(getString(R.string.comments));
				}
			}
			if (progress >= 50) {
				progressBar.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}
		}
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

	public void onResume() {
		super.onResume();
		ActionBarColor();
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
