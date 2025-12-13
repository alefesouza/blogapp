package com.vidadesuporte.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.HashMap;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.melnykov.fab.FloatingActionButton;
import com.vidadesuporte.R;
import com.vidadesuporte.activity.CommentsActivity;
import com.vidadesuporte.activity.FragmentActivity;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class WebViewFrag extends Fragment implements ObservableScrollViewCallbacks {
	View view;
	Activity activity;

	SharedPreferences preferences;
	Editor editor;
	String iconcolor;
	ObservableWebView webView;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	ProgressBarDeterminate progressBar2;
	boolean finished;

	FloatingActionButton fabcomment,
	fabdownload,
	fabopen;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		iconcolor = preferences.getString("prefIconColor", "branco");
		view = inflater.inflate(R.layout.webview, container, false);

		finished = false;

		progressBar2 = (ProgressBarDeterminate)view.findViewById(R.id.progressBar2);

		if (Build.VERSION.SDK_INT >= 21) {
			progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
			progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFFD92525, 0xFFD92525));
		} else {
			progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
		}

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
			webView.loadUrl(getActivity().getIntent().getStringExtra("url"));
		}

		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), getActivity().getIntent().getStringExtra("titulo"));

		mContentView = (FrameLayout)view.findViewById(R.id.main_content);
		mTargetView = (FrameLayout)view.findViewById(R.id.target_view);
		webView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		webView.setOnKeyListener(new OnKeyListener() {
			@SuppressWarnings("static-access")
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (mCustomView != null) {
							mClient.onHideCustomView();
						} else {
							if (webView.canGoBack()) {
								webView.goBack();
							} else {
								getActivity().finish();
							}
						}
						return true;
					}
				}
				return true;
			}
		});

		fabcomment = (FloatingActionButton)view.findViewById(R.id.fabcomments);

		if (Build.VERSION.SDK_INT <= 10) {
			fabcomment.setShadow(false);
		}

		fabcomment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CommentsActivity.class);
				intent.putExtra("url", webView.getUrl().replace("?m=1", ""));
				startActivity(intent);
			}
		});

		fabcomment.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast toast = Toast.makeText(getActivity(), "Comentários", Toast.LENGTH_SHORT);
				toast.show();
				return true;
			}
		});

		fabdownload = (FloatingActionButton)view.findViewById(R.id.fabdownload);

		if (Build.VERSION.SDK_INT <= 10) {
			fabdownload.setShadow(false);
		}

		fabdownload.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				String[]parts = webView.getUrl().split("/");
				String fileName = parts[parts.length - 1];
				@SuppressWarnings("static-access")
				DownloadManager dm = (DownloadManager)getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
				Request request = new Request(Uri.parse(webView.getUrl()));
				request.setTitle(fileName);
				request.setDescription("Dropando Ideias");
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
				enqueue = dm.enqueue(request);

				BroadcastReceiver onComplete = new BroadcastReceiver() {
					public void onReceive(Context ctxt, Intent intent) {
						Toast toast = Toast.makeText(getActivity(), "Imagem salva na pasta " + Environment.DIRECTORY_DOWNLOADS, Toast.LENGTH_LONG);
						toast.show();
					}
				};

				getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
			}
		});

		fabdownload.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast toast = Toast.makeText(getActivity(), "Baixar imagem", Toast.LENGTH_SHORT);
				toast.show();
				return true;
			}
		});

		fabopen = (FloatingActionButton)view.findViewById(R.id.fabopen);

		if (Build.VERSION.SDK_INT <= 10) {
			fabopen.setShadow(false);
		}

		fabopen.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (webView.getUrl().contains("facebook")) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/137869276285719"));
						startActivity(intent);
					} catch (Exception e) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(webView.getUrl()));
						startActivity(intent);
					}
				} else if (webView.getUrl().contains("twitter")) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=acasadocogumelo"));
						startActivity(intent);
					} catch (Exception e) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(webView.getUrl()));
						startActivity(intent);
					}
				} else if (webView.getUrl().contains("youtube")) {
					if (webView.getUrl().contains("v=")) {
						try {
							URL aURL = null;
							try {
								aURL = new URL(webView.getUrl());
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("vnd.youtube://" + getQueryMap(aURL.getQuery()).get("v")));
							startActivity(intent);
						} catch (Exception e) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(webView.getUrl()));
							startActivity(intent);
						}
					} else {
						try {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setPackage("com.google.android.youtube");
							String canal = webView.getUrl().replace("m.", "").replace("#/", "");
							intent.setData(Uri.parse(canal));
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(webView.getUrl()));
							startActivity(intent);
						}
					}
				} else if (webView.getUrl().contains("plus.google")) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://plus.google.com/+Acasadocogumelo"));
						intent.setPackage("com.google.android.apps.plus");
						startActivity(intent);
					} catch (Exception e) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(webView.getUrl()));
						startActivity(intent);
					}
				}
			}
		});

		fabopen.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				String app = null;
				if (webView.getUrl().contains("facebook")) {
					app = "Facebook";
				} else if (webView.getUrl().contains("twitter")) {
					app = "Twitter";
				} else if (webView.getUrl().contains("plus.google")) {
					app = "Google+";
				} else if (webView.getUrl().contains("youtube")) {
					app = "YouTube";
				}
				Toast toast = Toast.makeText(getActivity(), "Abrir no aplicativo do " + app, Toast.LENGTH_SHORT);
				toast.show();
				return true;
			}
		});

		webView.setScrollViewCallbacks(this);

		return view;
	}

	public static Map < String,
	String > getQueryMap(String query) {
		String a = query.replace("?", "&");
		String[]params = a.split("&");
		Map < String,
		String > map = new HashMap < String,
		String > ();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.webview_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (webView.canGoForward()) {
			menu.findItem(R.id.menu_forward).setEnabled(true);
		} else {
			menu.findItem(R.id.menu_forward).setEnabled(false);
		}

		if (iconcolor.equals("branco")) {
			menu.findItem(R.id.menu_share).setIcon(R.drawable.ic_share_white);
		} else {
			menu.findItem(R.id.menu_share).setIcon(R.drawable.ic_share_black);
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_open:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(webView.getUrl()));
			startActivity(intent);
			return true;
		case R.id.menu_forward:
			webView.goForward();
			return true;
		case R.id.menu_reload:
			webView.reload();
			return true;
		case R.id.menu_share:
			Intent sharePageIntent = new Intent();
			sharePageIntent.setAction(Intent.ACTION_SEND);
			sharePageIntent.putExtra(Intent.EXTRA_TEXT, webView.getTitle() + " " + webView.getUrl().replace("?m=1", ""));
			sharePageIntent.setType("text/plain");
			startActivity(Intent.createChooser(sharePageIntent, getResources().getText(R.string.share)));
			return true;
		case R.id.menu_cache:
			webView.clearCache(true);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.cache2), Toast.LENGTH_LONG);
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
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!finished) {
				if (getActivity().getIntent().hasExtra("internalbrowser")) {
					view.loadUrl(url);
				} else {
					if (url.contains("acasadocogumelo.com") || url.contains("facebook.com") || url.contains("google.com") || url.contains("twitter.com") || url.contains("youtube.com") || url.contains(".jpg") || url.contains(".png") || url.contains(".gif")) {
						view.loadUrl(url);
					} else {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url));
						startActivity(i);
					}
				}
				getActivity().supportInvalidateOptionsMenu();
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			String erro = "<html><head><style>body { background-image: url('erro.png'); background-repeat: no-repeat; background-position: center; background-color: #000000; min-height: 431px; }</style></head></html>";
			webView.loadDataWithBaseURL("file:///android_asset/", erro, "text/html", "utf-8", null);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	public class webChromeClient extends WebChromeClient {
		public void onProgressChanged(WebView view, int progress) {
			if (!finished) {
				if (getActivity().getIntent().hasExtra("internalbrowser")) {
					((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(webView.getTitle());
					((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(webView.getUrl());
				}
				if (!finished) {
					progressBar2.setProgress(progress);
					progressBar2.setVisibility(View.VISIBLE);

					if (progress == 100) {
						progressBar2.setVisibility(View.GONE);
					}

					if (progress >= 50) {
						if (Build.VERSION.SDK_INT >= 21) {
							progressBar.setVisibility(View.GONE);
						} else {
							progressBarCompat.setVisibility(View.GONE);
						}
						webView.setVisibility(View.VISIBLE);

						if (webView.getUrl().replace("?m=1", "").equals("http://acasadocogumelo.com") || webView.getUrl().replace("?m=1", "").equals("http://acasadocogumelo.com/") || webView.getUrl().replace("?m=1", "").equals("http://www.acasadocogumelo.com") || webView.getUrl().replace("?m=1", "").equals("http://www.acasadocogumelo.com/") || webView.getUrl().contains("acasadocogumelo.com/search")) {
							fabcomment.setVisibility(View.GONE);
							fabdownload.setVisibility(View.GONE);
							fabopen.setVisibility(View.GONE);
						} else if (webView.getUrl().contains(".jpg") || webView.getUrl().contains(".png") || webView.getUrl().contains(".gif")) {
							fabcomment.setVisibility(View.GONE);
							fabdownload.setVisibility(View.VISIBLE);
							fabopen.setVisibility(View.GONE);
						} else if (webView.getUrl().contains("acasadocogumelo.com")) {
							fabcomment.setVisibility(View.VISIBLE);
							fabdownload.setVisibility(View.GONE);
							fabopen.setVisibility(View.GONE);
						} else if (webView.getUrl().contains("facebook.com") || webView.getUrl().contains("plus.google") || webView.getUrl().contains("twitter.com") || webView.getUrl().contains("youtube.com")) {
							fabcomment.setVisibility(View.GONE);
							fabdownload.setVisibility(View.GONE);
							fabopen.setVisibility(View.VISIBLE);
						} else {
							fabcomment.setVisibility(View.GONE);
							fabdownload.setVisibility(View.GONE);
							fabopen.setVisibility(View.GONE);
						}
					}
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
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {}

	@Override
	public void onDownMotionEvent() {}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		if (scrollState == ScrollState.UP) {
			fabcomment.hide(true);
			fabdownload.hide(true);
			fabopen.hide(true);
		} else if (scrollState == ScrollState.DOWN) {
			fabcomment.show(true);
			fabdownload.show(true);
			fabopen.show(true);
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