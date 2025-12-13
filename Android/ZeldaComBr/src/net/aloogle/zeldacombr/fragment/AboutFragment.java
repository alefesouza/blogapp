package net.aloogle.zeldacombr.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import net.aloogle.zeldacombr.R;
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
		((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2e7d32")));
		view.findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2e7d32")));
		((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
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
				i.setData(Uri.parse("mailto:blogapp@apps.aloogle.net"));
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
