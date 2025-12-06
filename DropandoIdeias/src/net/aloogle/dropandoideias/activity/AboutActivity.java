package net.aloogle.dropandoideias.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import net.aloogle.dropandoideias.R;

@SuppressWarnings("deprecation")
public class AboutActivity extends ActionBarActivity {
	final Context context = this;
	public Toolbar mToolbar;
	SharedPreferences preferences;
	WebView webView;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		progressBar = (ProgressBar)findViewById(R.id.progressBar1);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		ActionBarColor(getString(R.string.app_name));

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {
			getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.app_name) + "</font>"));
		} else {
			getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name) + "</font>"));
		}

		webView = (WebView)findViewById(R.id.webview01);
		webView.setWebViewClient(new webViewClient());
		webView.loadUrl("file:///android_asset/sobre.html");
	}

	public void ActionBarColor(String title) {
		String userColor = preferences.getString("prefColor", "ff222222");

		if(userColor.equals("fundo")) {
			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_bg));
			findViewById(R.id.frame).setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_bg));
		} else {
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}
	}

	public class webViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("aloogleapp=sendemail")) {
				Intent i = new Intent(Intent.ACTION_SENDTO);
				i.setData(Uri.parse("mailto:alefe@aloogle.net"));
				startActivity(i);
			} else {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			progressBar.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
			super.onPageFinished(view, url);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AboutActivity.this.finish();
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}
}
