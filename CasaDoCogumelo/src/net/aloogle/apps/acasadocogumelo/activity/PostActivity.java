package net.aloogle.apps.acasadocogumelo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.ads.*;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import net.aloogle.apps.acasadocogumelo.R;
import net.aloogle.apps.acasadocogumelo.activity.FragmentActivity;
import net.aloogle.apps.acasadocogumelo.adapter.TagAdapter;
import net.aloogle.apps.acasadocogumelo.lib.CustomLinkMovementMethod;
import net.aloogle.apps.acasadocogumelo.lib.JSONParser;
import net.aloogle.apps.acasadocogumelo.other.CustomTextView;

@SuppressLint({ "NewApi", "InflateParams", "SetJavaScriptEnabled", "SimpleDateFormat", "CutPasteId" })
@SuppressWarnings("deprecation")
public class PostActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

	private View mHeaderView;
	private View mToolbarView;
	private ObservableScrollView mScrollView;
	private int mBaseTranslationY;

	CustomTextView titulo,
	autorhora;
	ImageView imagem;
	Toolbar mToolbar;
	private static final String TAG_TEXTO = "texto";
	private static final String TAG_IMAGEM = "imagem";
	private static final String TAG_IFRAME = "iframe";
	private static final String TAG_TAG = "tag";

	ArrayList <String> textoarray = new ArrayList <String> ();
	ArrayList <String> imagemarray = new ArrayList <String> ();
	ArrayList <String> iframearray = new ArrayList <String> ();
	ArrayList <String> tagarray = new ArrayList <String> ();

	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	JSONArray textos = null;
	JSONArray imagens = null;
	JSONArray iframes = null;
	JSONArray tags = null;
	LinearLayout linear;
	boolean favorite, finished, pressed;
	String iconcolor, stitulo, sdescricao, simagem, surl;

	FloatingActionButton fabcomment;

	SharedPreferences preferences;
	Editor editor;
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		iconcolor = preferences.getString("prefIconColor", "branco");
		if (iconcolor.equals("branco")) {}
		else {
			setTheme(R.style.BlackOverflow);
		}
		setContentView(R.layout.post);

		mHeaderView = findViewById(R.id.header);
		ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
		mToolbarView = findViewById(R.id.toolbar);

		mScrollView = (ObservableScrollView)findViewById(R.id.scroll);
		mScrollView.setScrollViewCallbacks(this);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String[]favorites = preferences.getString("favoritesPostsId", "").split("\\$\\%\\#");
		if (Arrays.asList(favorites).contains(getIntent().getStringExtra("id"))) {
			favorite = true;
		} else {
			favorite = false;
		}

		finished = false;
		pressed = false;

		linear = (LinearLayout)findViewById(R.id.linear);

		adView = new AdView(this);
		adView.setAdUnitId("")
		adView.setAdSize(AdSize.SMART_BANNER);

		LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);

		layout.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);

		autorhora = (CustomTextView)findViewById(R.id.autor);
		titulo = (CustomTextView)findViewById(R.id.titulo);
		imagem = (ImageView)findViewById(R.id.image);

		if (getIntent().hasExtra("imagem")) {
			stitulo = getIntent().getStringExtra("titulo");
			sdescricao = getIntent().getStringExtra("descricao");
			simagem = getIntent().getStringExtra("imagem");
			surl = getIntent().getStringExtra("url");

			Ion.with (imagem).load(simagem.replace("s1600", preferences.getString("prefImageQuality", "s400")));
			imagem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(PostActivity.this, FragmentActivity.class);
					intent.putExtra("fragment", 8);
					intent.putExtra("imgurl", simagem);
					startActivity(intent);
				}
			});
			titulo.setText(stitulo);
		}

		net.aloogle.apps.acasadocogumelo.activity.FragmentActivity.ActionBarColor(PostActivity.this, "");
		getSupportActionBar().setIcon(R.drawable.ic_toolbar);

		fabcomment = (FloatingActionButton)findViewById(R.id.fabcomments);

		if (Build.VERSION.SDK_INT <= 10) {
			fabcomment.setShadow(false);
		}

		fabcomment.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if(Build.VERSION.SDK_INT == 10) {
					Intent intent = new Intent(PostActivity.this, net.aloogle.apps.acasadocogumelo.activity.v10.CommentsActivity.class);
					intent.putExtra("url", surl);
					startActivity(intent);
				} else {
					Intent intent = new Intent(PostActivity.this, net.aloogle.apps.acasadocogumelo.activity.CommentsActivity.class);
					intent.putExtra("url", surl);
					startActivity(intent);
				}
				overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
			}
		});

		fabcomment.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast toast = Toast.makeText(PostActivity.this, "Comentários", Toast.LENGTH_SHORT);
				toast.show();
				return true;
			}
		});

		new JSONParse().execute();
	}

	private class JSONParse extends AsyncTask <String, String, JSONObject> {

		@Override
		protected void onPreExecute() {
			if(Build.VERSION.SDK_INT >= 21) {
				progressBar = (ProgressBar)findViewById(R.id.progressBar1);
				progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFFD92525, 0xFFD92525));
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBarCompat = (ProgressBarCircularIndeterminate)findViewById(R.id.progressBar1);
				progressBarCompat.setVisibility(View.VISIBLE);
			}
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String...args) {
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl("http://apps.aloogle.net/blogapp/acasadocogumelo/json/post.php?id=" + getIntent().getStringExtra("id"));
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			if (finished == false) {
				if(Build.VERSION.SDK_INT >= 21) {
					progressBar.setVisibility(View.GONE);
				} else {
					progressBarCompat.setVisibility(View.GONE);
				}
				try {
					try {
						if (getIntent().hasExtra("imagem")) {}
						else {
							stitulo = json.getString("titulo");
							sdescricao = json.getString("descricao");
							simagem = json.getString("imagem");
							surl = json.getString("url");

							Ion.with (imagem).load(simagem.replace("s1600", preferences.getString("prefImageQuality", "s400")));
							imagem.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(PostActivity.this, FragmentActivity.class);
									intent.putExtra("fragment", 8);
									intent.putExtra("imgurl", simagem);
									startActivity(intent);
								}
							});
							titulo.setText(stitulo);
						}

						String postData = json.getString("data");
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
								autorhora.setText("Por " + json.getString("autor"));
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
									if (seconds <= 1) {
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
									if (json.getString("data2").contains(String.valueOf(year))) {
										withyear = json.getString("data2").replace(" " + String.valueOf(year), "");
									} else {
										withyear = json.getString("data2");
									}
									autorhora.setText("Por " + json.getString("autor") + " - " + withyear);
								} else {
									autorhora.setText("Por " + json.getString("autor") + " - Há " + what);
								}
							}

						} catch (ParseException e) {
							e.printStackTrace();
						}

						textos = json.getJSONArray("textos");
						for (int i = 0; i < textos.length(); i++) {
							JSONObject c = textos.getJSONObject(i);

							String texto = c.getString(TAG_TEXTO);
							textoarray.add(texto);
						}

						imagens = json.getJSONArray("imagens");
						for (int i = 0; i < imagens.length(); i++) {
							JSONObject c = imagens.getJSONObject(i);

							String imagem = c.getString(TAG_IMAGEM);
							imagemarray.add(imagem);
						}

						iframes = json.getJSONArray("iframes");
						for (int i = 0; i < iframes.length(); i++) {
							JSONObject c = iframes.getJSONObject(i);

							String iframe = c.getString(TAG_IFRAME);
							iframearray.add(iframe);
						}

						tags = json.getJSONArray("tags");
						for (int i = 0; i < tags.length(); i++) {
							JSONObject c = tags.getJSONObject(i);

							String tag = c.getString(TAG_TAG);
							tagarray.add(tag);
						}

						for (int i = 0; i < textos.length(); i++) {
							if (textoarray.get(i).toString().equals("")) {}
							else {
								LayoutInflater textoinflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View texto = textoinflater.inflate(R.layout.post_text, null);
								CustomTextView text = (CustomTextView)texto.findViewById(R.id.texto);
								text.setMovementMethod(CustomLinkMovementMethod.getInstance(PostActivity.this));
								text.setText(Html.fromHtml(textoarray.get(i).toString(), null, new TagAdapter()));
								linear.addView(texto);
							}
							if (i < imagemarray.size()) {
								if (imagemarray.get(i).toString().equals("")) {
									if (iframearray.get(i).toString().contains("youtube.com/embed")) {
										LayoutInflater videoinflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
										View video = videoinflater.inflate(R.layout.post_video, null);
										String[]parts = iframearray.get(i).toString().split("/");
										final String videoId = parts[parts.length - 1];
										ImageView imageView = (ImageView)video.findViewById(R.id.thumbnail);
										final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)video.findViewById(R.id.progress);
										Ion.with (PostActivity.this)
										.load("http://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg")
										.progress(new ProgressCallback() {
											@Override
											public void onProgress(final long downloaded, final long total) {
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														float p = (float)downloaded / (float)total * 100;
														progressBar2.setProgress((int)(Math.round(p)));

														progressBar2.setVisibility(View.VISIBLE);

														if (downloaded == total) {
															progressBar2.setVisibility(View.GONE);
														}
													}
												});
											}
										})
										.withBitmap()
										.error(R.drawable.drawer_logo)
										.intoImageView(imageView);

										imageView.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												Intent intent = new Intent(Intent.ACTION_VIEW);
												intent.setData(Uri.parse("http://youtube.com/watch?v=" + videoId));
												startActivity(intent);
											}
										});
										linear.addView(video);
									} else {
										LayoutInflater webviewinflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
										View webview = webviewinflater.inflate(R.layout.post_webview, null);
										final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)webview.findViewById(R.id.progressBar2);
										final WebView webView = (WebView)webview.findViewById(R.id.webview01);
										webView.getSettings().setJavaScriptEnabled(true);
										webView.setWebViewClient(new WebViewClient() {
											@Override
											public boolean shouldOverrideUrlLoading(WebView view, String url) {
												if (url.contains("imgur")) {
													view.loadUrl(url);
												} else {
													Intent i = new Intent(Intent.ACTION_VIEW);
													i.setData(Uri.parse(url));
													startActivity(i);
												}
												return true;
											}
										});
										webView.setWebChromeClient(new WebChromeClient() {
											public void onProgressChanged(WebView view, int progress) {
												progressBar2.setProgress(progress);
												progressBar2.setVisibility(View.VISIBLE);

												if (progress == 100) {
													progressBar2.setVisibility(View.GONE);
												}

												if (progress >= 50) {
													webView.setVisibility(View.VISIBLE);
												}
											}
										});
										webView.loadUrl(iframearray.get(i).toString());
										linear.addView(webview);
									}
								} else {
									if (i != 0) {
										LayoutInflater imageminflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
										final View imagem = imageminflater.inflate(R.layout.post_image, null);
										ImageView imageView = (ImageView)imagem.findViewById(R.id.imagem);
										final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)imagem.findViewById(R.id.progress);
										Ion.with (PostActivity.this)
										.load(imagemarray.get(i).toString().replace("s1600", preferences.getString("prefImageQuality", "s400")))
										.progress(new ProgressCallback() {
											@Override
											public void onProgress(final long downloaded, final long total) {
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														float p = (float)downloaded / (float)total * 100;
														progressBar2.setProgress((int)(Math.round(p)));

														progressBar2.setVisibility(View.VISIBLE);

														if (downloaded == total) {
															progressBar2.setVisibility(View.GONE);
														}
													}
												});
											}
										})
										.withBitmap()
										.error(R.drawable.drawer_logo)
										.intoImageView(imageView);
										final int a = i;
										imageView.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												Intent intent = new Intent(PostActivity.this, FragmentActivity.class);
												intent.putExtra("fragment", 8);
												intent.putExtra("imgurl", imagemarray.get(a).toString());
												startActivity(intent);
											}
										});
										linear.addView(imagem);
									}
								}
							}
						}

						LinearLayout tagslayout = (LinearLayout)findViewById(R.id.tags);
						LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

						TextView tagscall = new TextView(PostActivity.this);
						tagscall.setLayoutParams(vp);
						tagscall.setText("Tags: ");
						tagscall.setTextSize(16);
						tagscall.setTextColor(Color.parseColor("#000000"));
						tagslayout.addView(tagscall);

						for (int i = 0; i < tagarray.size(); i++) {
							if (i == 0 || i == tagarray.size()) {}
							else {
								TextView space = new TextView(PostActivity.this);
								space.setLayoutParams(vp);
								space.setText(", ");
								space.setTextSize(16);
								space.setTextColor(Color.parseColor("#000000"));
								tagslayout.addView(space);
							}

							TextView tagtext = new TextView(PostActivity.this);
							tagtext.setLayoutParams(vp);
							tagtext.setText(tagarray.get(i).toString());
							tagtext.setTextSize(16);
							tagtext.setTextColor(Color.parseColor("#ff0000"));
							tagslayout.addView(tagtext);

							final int e = i;

							tagtext.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(PostActivity.this, net.aloogle.apps.acasadocogumelo.activity.FragmentActivity.class);
									intent.putExtra("fragment", 7);
									intent.putExtra("label", tagarray.get(e).toString());
									startActivity(intent);
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.post_menu, menu);

		MenuItem shareItem = menu.findItem(R.id.menu_share);
		ShareActionProvider shareProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);

		Intent sharePageIntent = new Intent();
		sharePageIntent.setAction(Intent.ACTION_SEND);
		sharePageIntent.putExtra(Intent.EXTRA_TEXT, stitulo + " " + surl);
		sharePageIntent.setType("text/plain");
		shareProvider.setShareIntent(sharePageIntent);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (iconcolor.equals("branco")) {
			if (favorite) {
				menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_star_white);
				menu.findItem(R.id.menu_favorite).setTitle("Desmarcar como favorito");
			} else {
				menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_star_white_outline);
				menu.findItem(R.id.menu_favorite).setTitle("Marcar como favorito");
			}
		} else {
			if (favorite) {
				menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_star_black);
				menu.findItem(R.id.menu_favorite).setTitle("Desmarcar como favorito");
			} else {
				menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_star_black_outline);
				menu.findItem(R.id.menu_favorite).setTitle("Marcar como favorito");
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (getIntent().hasExtra("fromnotification")) {
				if(Build.VERSION.SDK_INT == 10) {
					Intent intent = new Intent(PostActivity.this, net.aloogle.apps.acasadocogumelo.activity.v10.MainActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(PostActivity.this, MainActivity.class);
					startActivity(intent);
				}
			}
			PostActivity.this.finish();
			return true;
		case R.id.menu_favorite:
			if (favorite) {
				editor.putString("favoritesPostsId", preferences.getString("favoritesPostsId", "").replace(getIntent().getStringExtra("id") + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsTitle", preferences.getString("favoritesPostsTitle", "").replace(stitulo + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsDescription", preferences.getString("favoritesPostsDescription", "").replace(sdescricao + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsImage", preferences.getString("favoritesPostsImage", "").replace(simagem + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsUrl", preferences.getString("favoritesPostsUrl", "").replace(surl + "$%#", ""));
				editor.commit();
				favorite = false;
			} else {
				editor.putString("favoritesPostsId", getIntent().getStringExtra("id") + "$%#" + preferences.getString("favoritesPostsId", ""));
				editor.commit();
				editor.putString("favoritesPostsTitle", stitulo + "$%#" + preferences.getString("favoritesPostsTitle", ""));
				editor.commit();
				editor.putString("favoritesPostsDescription", sdescricao + "$%#" + preferences.getString("favoritesPostsDescription", ""));
				editor.commit();
				editor.putString("favoritesPostsImage", simagem + "$%#" + preferences.getString("favoritesPostsImage", ""));
				editor.commit();
				editor.putString("favoritesPostsUrl", surl + "$%#" + preferences.getString("favoritesPostsUrl", ""));
				editor.commit();
				favorite = true;
			}
			supportInvalidateOptionsMenu();
			return true;
		case R.id.menu_copylink:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				final android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				final android.content.ClipData clipData = android.content.ClipData.newPlainText(surl, surl);
				clipboardManager.setPrimaryClip(clipData);
			} else {
				final android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(surl);
			}
			Toast toast = Toast.makeText(PostActivity.this, "Link copiado para a área de transferência", Toast.LENGTH_SHORT);
			toast.show();
			return true;
		case R.id.menu_openinbrowser:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(surl));
			startActivity(intent);
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		if (dragging) {
			int toolbarHeight = mToolbarView.getHeight();
			if (firstScroll) {
				float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
				if (-toolbarHeight < currentHeaderTranslationY) {
					mBaseTranslationY = scrollY;
				}
			}
			float headerTranslationY = ScrollUtils.getFloat( - (scrollY - mBaseTranslationY), -toolbarHeight, 0);
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
		}

		View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);
		int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
		if (diff <= 10) {
			showToolbar();
		}
	}

	@Override
	public void onDownMotionEvent() {}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		mBaseTranslationY = 0;

		if (scrollState == ScrollState.DOWN) {
			showToolbar();
		} else if (scrollState == ScrollState.UP) {
			int toolbarHeight = mToolbarView.getHeight();
			int scrollY = mScrollView.getCurrentScrollY();
			if (toolbarHeight <= scrollY) {
				hideToolbar();
			} else {
				showToolbar();
			}
		} else {
			// Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
			if (!toolbarIsShown() && !toolbarIsHidden()) {
				// Toolbar is moving but doesn't know which to move:
				// you can change this to hideToolbar()
				showToolbar();
			}
		}
	}

	private boolean toolbarIsShown() {
		return ViewHelper.getTranslationY(mHeaderView) == 0;
	}

	private boolean toolbarIsHidden() {
		return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
	}

	private void showToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
		if (headerTranslationY != 0) {
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
		}
		fabcomment.show(true);
	}

	private void hideToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
		int toolbarHeight = mToolbarView.getHeight();
		if (headerTranslationY != -toolbarHeight) {
			ViewPropertyAnimator.animate(mHeaderView).cancel();
			ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
		}
		fabcomment.hide(true);
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	public void onResume() {
		adView.resume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		finished = true;
		adView.destroy();
		super.onDestroy();
	}
}
