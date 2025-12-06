package net.aloogle.acasadocogumelo.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import net.aloogle.acasadocogumelo.R;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.Activity;
import android.support.v4.app.Fragment;

@SuppressWarnings("deprecation")
public class AboutFragment extends Fragment {
	SharedPreferences preferences;
	WebView webView;
	ProgressBar progressBar;
	@SuppressWarnings("unused")
	private Activity activity;
	View view;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.about, container, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);

		ActionBarColor();
		((ActionBarActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);

		webView = (WebView)view.findViewById(R.id.webview01);
		webView.setWebViewClient(new webViewClient());
		webView.loadUrl("file:///android_asset/sobre.html");
		return view;
	}

	public void ActionBarColor() {
		String userColor = preferences.getString("prefColor", "padrao");
		if(userColor.equals("padrao")) {
			((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_bg));
			view.findViewById(R.id.frame).setBackgroundDrawable(getResources().getDrawable(R.drawable.splash_bg));
		} else {
			((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			view.findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}
		
		String iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.app_name) + "</font>"));
		} else {
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_name) + "</font>"));
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
}