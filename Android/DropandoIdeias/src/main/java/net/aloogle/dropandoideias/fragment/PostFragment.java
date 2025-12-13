package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONException;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.FragmentActivity;
import net.aloogle.dropandoideias.database.helper.DatabaseHelper;
import net.aloogle.dropandoideias.database.model.Favorites;
import net.aloogle.dropandoideias.database.model.Jsons;
import net.aloogle.dropandoideias.other.CustomTextView;
import net.aloogle.dropandoideias.other.Other;

@SuppressLint({"NewApi", "InflateParams", "SetJavaScriptEnabled", "SimpleDateFormat", "CutPasteId", "ClickableViewAccessibility"})
@SuppressWarnings("deprecation")
public class PostFragment extends Fragment implements ObservableScrollViewCallbacks {

    Activity activity;

	CustomTextView titulo, autorhora, comentarios;
	ImageView imagem;

    private ObservableScrollView mScrollView;

	private FrameLayout mTargetView;
	private FrameLayout mContentView;
	private CustomViewCallback mCustomViewCallback;
	private View mCustomView;
	private WebChromeClient mClient;

	ProgressBar progressBar, progressBar2;
	WebView webView;
	boolean favorite, finished, pressed, fromrni, fromcallback;
	String sjson, sid, stitulo, sdescricao, simagem, surl, scategoria, content;
	Favorites favorites;

	FloatingActionButton fabcomment;

	SharedPreferences preferences;
	Editor editor;
	String userColor, iconColor;

    View view;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.post_fragment, container, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = preferences.edit();

		fromrni = getArguments().getBoolean("fromrandom", false) || getArguments().getBoolean("fromnotification", false) || getArguments().getBoolean("frompostid", false);
		progressBar2 = (ProgressBar)view.findViewById(R.id.progress);
		progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);

		userColor = preferences.getString("prefColor", "222222");
		iconColor = preferences.getString("prefIconColor", "branco");

		if (!fromrni) {
			progressBar2.setVisibility(View.GONE);
		}

		finished = false;
		pressed = false;

        mScrollView = (ObservableScrollView)view.findViewById(R.id.scroll);
		mScrollView.setScrollViewCallbacks(this);

		webView = (WebView)view.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.setScrollContainer(false);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE)
                    return true;
                return false;
            }
        });

		mContentView = (FrameLayout)view.findViewById(R.id.main_content);
		mTargetView = (FrameLayout)view.findViewById(R.id.target_view);
		mClient = new webChromeClient();
		webView.setWebChromeClient(mClient);

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

		imagem = (ImageView)view.findViewById(R.id.image);
		autorhora = (CustomTextView)view.findViewById(R.id.autor);
		titulo = (CustomTextView)view.findViewById(R.id.titulo);
		comentarios = (CustomTextView)view.findViewById(R.id.comentarioscount);

		if (!getArguments().getString("extraJson", "").equals("")) {
			sjson = getArguments().getString("extraJson");
			try {
				JSONObject extraJson = new JSONObject(sjson);
				sid = extraJson.getString("id");
				stitulo = extraJson.getString("title");
				sdescricao = extraJson.getString("description");
				simagem = extraJson.getString("image");
				surl = extraJson.getString("url");
				scategoria = extraJson.getString("categoryicon");

				String comments = getArguments().getString("comments");
				comentarios.setText(comments);
			} catch (JSONException e) {}

			Ion.with (getActivity())
			.load(simagem)
			.withBitmap()
			.intoImageView(imagem);
			imagem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), FragmentActivity.class);
					intent.putExtra("fragment", 8);
					intent.putExtra("imgurl", simagem);
					startActivity(intent);
				}
			});
			titulo.setText(stitulo);
		} else {
			sid = getArguments().getString("id");
		}

		fabcomment = (FloatingActionButton)view.findViewById(R.id.fabcomments);

		fabcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sjson.equals(null)) {
                    Intent intent = new Intent(getActivity(), net.aloogle.dropandoideias.activity.CommentsActivity.class);
                    intent.putExtra("url", surl);
                    intent.putExtra("id", sid);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                }
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

		progressBar.setVisibility(View.VISIBLE);

		fromcallback = false;
		new checkFav().execute();
        return view;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.reload();
            }
        }, 100);
	}

	public class webChromeClient extends WebChromeClient {
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			mCustomViewCallback = callback;
			mTargetView.addView(view);
			mCustomView = view;
			mContentView.setVisibility(View.GONE);
			mTargetView.setVisibility(View.VISIBLE);
			mTargetView.bringToFront();
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
		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
	}

	@Override
	public void onDownMotionEvent() {}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		if (scrollState == ScrollState.DOWN) {
			Other.fabShow(true, fabcomment);
			Other.fabShow(true, view.findViewById(R.id.relat));
		} else if (scrollState == ScrollState.UP) {
				Other.fabShow(false, fabcomment);
				Other.fabShow(false, view.findViewById(R.id.relat));
		} else {
			Other.fabShow(true, fabcomment);
			Other.fabShow(true, view.findViewById(R.id.relat));
		}
	}

	@SuppressLint("DefaultLocale")
	public void setPost() {
		Ion.with (this)
		.load(Other.defaultUrl + "post.php?id=" + sid + "&blogid=" + getString(R.string.blogid))
		.asJsonObject()
		.setCallback(new FutureCallback<JsonObject>() {
			@Override
			public void onCompleted(Exception e, JsonObject json) {
				if (e != null) {
					if (favorites == null || favorites.getContent() == null) {
						Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
						toast.show();
						progressBar.setVisibility(View.GONE);
						final RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl);
						LayoutInflater inflater = getActivity().getLayoutInflater();
						final ViewGroup tryagain = (ViewGroup) inflater.inflate(R.layout.message_footer, null, false);
						LayoutParams vg = new LayoutParams(LayoutParams.MATCH_PARENT, Other.dpToPx(getActivity(), 50));
						vg.addRule(RelativeLayout.BELOW, R.id.autorhora);
						tryagain.setLayoutParams(vg);
						CustomTextView loadmore = (CustomTextView) tryagain.findViewById(R.id.message);
						loadmore.setText("Tentar novamente");
						tryagain.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								if (Other.isConnected(getActivity())) {
									setPost();
									rl.removeView(tryagain);
									progressBar.setVisibility(View.VISIBLE);
								} else {
									Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
									toast.show();
								}
							}
						});
						rl.addView(tryagain);
					}
					e.printStackTrace();
					return;
				}
				if (!finished) {
					if (getArguments().getString("extraJson", "").equals("")) {

						sjson = "{ \"id\": \"" + sid + "\", \"title\": \"" + json.get("title").getAsString() + "\", \"description\": \"" + json.get("description").getAsString() + "\", \"image\": \"" + json.get("image").getAsString() + "\", \"url\": \"" + json.get("url").getAsString() + "\", \"categoryicon\": \"" + json.get("categoryicon").getAsString() + "\" }";
						try {
							JSONObject extraJson = new JSONObject(sjson);
							sid = extraJson.getString("id");
							stitulo = extraJson.getString("title");
							sdescricao = extraJson.getString("description");
							simagem = extraJson.getString("image");
							surl = extraJson.getString("url");
							scategoria = extraJson.getString("categoryicon");
						} catch (JSONException i) {
						}

						if (simagem.equals("")) {
							progressBar.setVisibility(View.GONE);
						} else {
							Ion.with(getActivity())
									.load(simagem)
									.progress(new ProgressCallback() {
										@Override
										public void onProgress(final long downloaded, final long total) {
											getActivity().runOnUiThread(new Runnable() {
												@Override
												public void run() {
													float p = (float) downloaded / (float) total * 100;
													progressBar2.setProgress((int) (Math.round(p)));

													progressBar2.setVisibility(View.VISIBLE);
												}
											});
										}
									})
									.withBitmap()
									.intoImageView(imagem)
									.setCallback(new FutureCallback<ImageView>() {
										@Override
										public void onCompleted(Exception e, final ImageView imageView) {
											if (e != null)
												return;
											new Handler().postDelayed(new Runnable() {
												@Override
												public void run() {
													progressBar2.setVisibility(View.GONE);
													ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
														@Override
														public void run() {
															onScrollChanged(0, false, false);
														}
													});
												}
											}, 100);
										}
									});

							imagem.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(getActivity(), FragmentActivity.class);
									intent.putExtra("fragment", 8);
									intent.putExtra("imgurl", simagem);
									startActivity(intent);
								}
							});
						}
						titulo.setText(stitulo);
					}

                    comentarios.setText(json.get("comments").getAsString());

                    fromcallback = true;
                    content = json.toString();
                    new checkFav().execute();
				}
			}
		});
	}

	public void makePost(String content) {
		JsonParser parse = new JsonParser();
		JsonObject json = (JsonObject)parse.parse(content);

		String postData = json.get("date").getAsString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date postDate = dateFormat.parse(postData);
			Date currentDate = new Date();

			long diff = currentDate.getTime() - postDate.getTime();
			long seconds = diff / 1000;
			long minutes = seconds / 60;
			long hours = minutes / 60;
			long days = hours / 24;

			if (seconds < 0) {
				autorhora.setText("Por " + json.get("author").getAsString());
			} else {
				String what = "";

				if (seconds < 60) {
					String plural = null;
					if (seconds <= 1) {
						plural = "segundo";
					} else {
						plural = "segundos";
					}
					what = String.valueOf(seconds) + " " + plural;
				} else if (minutes < 60) {
					String plural = null;
					if (minutes <= 1) {
						plural = "minuto";
					} else {
						plural = "minutos";
					}
					what = String.valueOf(minutes) + " " + plural;
				} else if (hours < 24) {
					String plural = null;
					if (hours <= 1) {
						plural = "hora";
					} else {
						plural = "horas";
					}
					what = String.valueOf(hours) + " " + plural;
				} else if (days < 31) {
					String plural = null;
					if (days <= 1) {
						plural = "dia";
					} else {
						plural = "dias";
					}
					what = String.valueOf(days) + " " + plural;
				}

				if (what.equals("")) {
					Calendar c = Calendar.getInstance();
					int year = c.get(Calendar.YEAR);
					String withyear = "";
					if (json.get("date2").getAsString().contains(String.valueOf(year))) {
						withyear = json.get("date2").getAsString().replace(" " + String.valueOf(year), "");
					} else {
						withyear = json.get("date2").getAsString();
					}
					autorhora.setText("Por " + json.get("author").getAsString() + " - " + withyear);
				} else {
					autorhora.setText("Por " + json.get("author").getAsString() + " - Há " + what);
				}
			}

		} catch (ParseException o) {
			o.printStackTrace();
		}

		webView.loadData(json.get("content").getAsString(), "text/html; charset=utf-8", "utf-8");
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains("apps.aloogle.net/blogapp/start")) {
					URL qURL = null;
					try {
						qURL = new URL(url);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					String query = null;
					if (url.contains("image")) {
						try {
							query = URLDecoder.decode(getQueryMap(qURL.getQuery()).get("image"), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(getActivity(), FragmentActivity.class);
						intent.putExtra("fragment", 8);
						intent.putExtra("imgurl", query);
						startActivity(intent);
					} else if (url.contains("tag")) {
						String title = null;
						try {
							query = getQueryMap(qURL.getQuery()).get("tag");
							title = URLDecoder.decode(getQueryMap(qURL.getQuery()).get("title"), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(getActivity(), net.aloogle.dropandoideias.activity.FragmentActivity.class);
						intent.putExtra("fragment", 7);
						intent.putExtra("value", query);
						intent.putExtra("title", title);
						startActivity(intent);
					}
				} else if (url.contains(getString(R.string.sitename))) {
					if (url.contains("postid=")) {
						URL idURL = null;
						try {
							idURL = new URL(url);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
						String vID = null;
						try {
							vID = URLDecoder.decode(getQueryMap(idURL.getQuery()).get("postid"), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						Intent intent = new Intent(getActivity(), net.aloogle.dropandoideias.activity.PostActivity.class);
						intent.putExtra("frompostid", true);
						intent.putExtra("id", vID);
						startActivity(intent);
					} else {
						Intent intent = new Intent(getActivity(), net.aloogle.dropandoideias.activity.FragmentActivity.class);
						intent.putExtra("fragment", 5);
						intent.putExtra("titulo", getString(R.string.app_name));
						intent.putExtra("url", url);
						startActivity(intent);
					}
				} else {
					Intent intent = new Intent(getActivity(), net.aloogle.dropandoideias.activity.FragmentActivity.class);
					intent.putExtra("fragment", 5);
					intent.putExtra("titulo", getString(R.string.app_name));
					intent.putExtra("url", url);
					intent.putExtra("internalbrowser", true);
					startActivity(intent);
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				progressBar.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
				super.onPageFinished(view, url);
			}
		});
	}

	public static Map < String, String > getQueryMap(String query) {
		String a = query.replace("?", "&");
		String[]params = a.split("&");
		Map < String, 	String > map = new HashMap < String, 	String > ();
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
		inflater.inflate(R.menu.post_menu, menu);

		MenuItem shareItem = menu.findItem(R.id.menu_share);
		ShareActionProvider shareProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);

		Intent sharePageIntent = new Intent();
		sharePageIntent.setAction(Intent.ACTION_SEND);
		sharePageIntent.putExtra(Intent.EXTRA_TEXT, stitulo + " " + surl);
		sharePageIntent.setType("text/plain");
		shareProvider.setShareIntent(sharePageIntent);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		int normal = iconColor.equals("branco") ? R.drawable.ic_star : R.drawable.ic_star_black;
		int outline = iconColor.equals("branco") ? R.drawable.ic_star_outline : R.drawable.ic_star_black_outline;
		if (favorite) {
			menu.findItem(R.id.menu_favorite).setIcon(normal);
			menu.findItem(R.id.menu_favorite).setTitle("Desmarcar como favorito");
		} else {
			menu.findItem(R.id.menu_favorite).setIcon(outline);
			menu.findItem(R.id.menu_favorite).setTitle("Marcar como favorito");
		}
        super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_favorite:
			if (!sjson.equals(null)) {
				if (favorite) {
					new desFav().execute();
					favorite = false;
				} else {
					new markAsFavorite().execute();
					favorite = true;
				}
				getActivity().supportInvalidateOptionsMenu();
			}
			return true;
		case R.id.menu_copylink:
			if (!sjson.equals(null)) {
				if (Build.VERSION.SDK_INT >= 11) {
					final android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
					final android.content.ClipData clipData = android.content.ClipData.newPlainText(surl, surl);
					clipboardManager.setPrimaryClip(clipData);
				} else {
					final android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
					clipboardManager.setText(surl);
				}
				Toast toast = Toast.makeText(getActivity(), "Link copiado para a área de transferência", Toast.LENGTH_SHORT);
				toast.show();
			}
			return true;
		case R.id.menu_openinbrowser:
			if (!sjson.equals(null)) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(surl));
				startActivity(intent);
			}
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDestroy() {
		finished = true;
		super.onDestroy();
	}

	private class markAsFavorite extends AsyncTask < JsonObject, Jsons, Jsons > {
		DatabaseHelper db = new DatabaseHelper(getActivity());

		@Override
		protected Jsons doInBackground(JsonObject...arg) {
			db.createFavorite(new Favorites(sid, sjson, content));
			favorite = true;
            getActivity().supportInvalidateOptionsMenu();
			return null;
		}

		@Override
		protected void onPostExecute(Jsons home) {
			db.closeDB();
		}
	}

	private class desFav extends AsyncTask < JsonObject, Jsons, Jsons > {
		DatabaseHelper db = new DatabaseHelper(getActivity());

		@Override
		protected Jsons doInBackground(JsonObject...arg) {
			db.deleteFavorite(sid);
			favorite = false;
			return null;
		}

		@Override
		protected void onPostExecute(Jsons home) {
            getActivity().supportInvalidateOptionsMenu();
			db.closeDB();
		}
	}

	private class checkFav extends AsyncTask < JsonObject, Jsons, Jsons > {
		DatabaseHelper db = new DatabaseHelper(getActivity());

        @Override
		protected Jsons doInBackground(JsonObject...arg) {
			if (db.getFavorite(sid) != null) {
				favorites = db.getFavorite(sid);
			}

			favorite = favorites != null;
			return null;
		}

		@Override
		protected void onPostExecute(Jsons home) {
			getActivity().supportInvalidateOptionsMenu();
			db.closeDB();

			if (!fromcallback) {
				setPost();
				if (favorite && favorites.getContent() != null) {
					content = favorites.getContent();
					makePost(content);
				}
			} else {
				if (favorites == null || favorites.getContent() == null) {
					makePost(content);
				}
			}
		}
	}
}
