package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import java.lang.reflect.InvocationTargetException;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import net.aloogle.dropandoideias.R;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class CommentsFragment extends Fragment {
	View view;
	Activity activity;

	SharedPreferences preferences;
	Editor editor;
	ObservableWebView webView;
	ProgressBar progressBar, progressBar2;
	boolean finished;

	long enqueue;

	private FrameLayout mTargetView;
	private FrameLayout mContentView;
	private CustomViewCallback mCustomViewCallback;
	private View mCustomView;
	private webChromeClient mClient;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		view = inflater.inflate(R.layout.webview, container, false);

		finished = false;

		progressBar2 = (ProgressBar)view.findViewById(R.id.progressBar2);

		progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);

		webView = (ObservableWebView)view.findViewById(R.id.webview01);
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
			webView.loadUrl(getArguments().getString("url"));
		}

		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		mContentView = (FrameLayout)view.findViewById(R.id.main_content);
		mTargetView = (FrameLayout)view.findViewById(R.id.target_view);
		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		return view;
	}

	public class webViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!finished) {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			String erro = "<html><head><style>body { background-image: url('erro.png'); background-repeat: no-repeat; background-position: center; background-color: #1b1b1b; min-height: 431px; }</style></head></html>";
			webView.loadDataWithBaseURL("file:///android_asset/", erro, "text/html", "utf-8", null);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	public class webChromeClient extends WebChromeClient {
		public void onProgressChanged(WebView view, int progress) {
			if (finished == false) {
				progressBar2.setProgress(progress);
				progressBar2.setVisibility(View.VISIBLE);

				if (progress == 100) {
					progressBar2.setVisibility(View.GONE);
				}

				if (progress >= 50) {
					progressBar.setVisibility(View.GONE);
					webView.setVisibility(View.VISIBLE);
				}
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
			((AppCompatActivity)getActivity()).getSupportActionBar().hide();
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
			((AppCompatActivity)getActivity()).getSupportActionBar().show();
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

	@Override
	public void onDestroy() {
		finished = true;
		super.onDestroy();
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		webView.saveState(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}
}
