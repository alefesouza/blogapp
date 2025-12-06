package net.aloogle.acasadocogumelo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
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
import com.google.android.gms.ads.*;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import net.aloogle.acasadocogumelo.R;
import net.aloogle.acasadocogumelo.activity.FragmentActivity;
import net.aloogle.acasadocogumelo.adapter.TagAdapter;
import net.aloogle.acasadocogumelo.lib.JSONParser;
import net.aloogle.acasadocogumelo.lib.PlayerViewActivity;
import net.aloogle.acasadocogumelo.other.CustomTextView;

@SuppressLint({"NewApi","InflateParams","SetJavaScriptEnabled", "SimpleDateFormat"})
@SuppressWarnings("deprecation")
public class PostFragment extends Fragment implements ObservableScrollView.OnScrollChangedListener {
	CustomTextView titulo, autorhora;
	ImageView imagem;
	Activity activity;
	View view;
	private static final String TAG_TEXTO = "texto";
	private static final String TAG_IMAGEM = "imagem";
	private static final String TAG_IFRAME = "iframe";
	private static final String TAG_TAG = "tag";

	ArrayList <String> textoarray = new ArrayList <String> ();
	ArrayList <String> imagemarray = new ArrayList <String> ();
	ArrayList <String> iframearray = new ArrayList <String> ();
	ArrayList <String> tagarray = new ArrayList <String> ();

	ProgressBarCircularIndeterminate progressBar;
	JSONArray textos = null;
	JSONArray imagens = null;
	JSONArray iframes = null;
	JSONArray tags = null;
	LinearLayout linear;
	boolean favorite, finished, pressed;

	FloatingActionButton fabcomment;

	SharedPreferences preferences;
	Editor editor;
	private AdView adView;

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
		editor = preferences.edit();
		view = inflater.inflate(R.layout.post, container, false);

		String[]favorites = preferences.getString("favoritesPostsId", "").split("\\$\\%\\#");
		if (Arrays.asList(favorites).contains(getActivity().getIntent().getStringExtra("id"))) {
			favorite = true;
		} else {
			favorite = false;
		}

		finished = false;
		pressed = false;

		linear = (LinearLayout)view.findViewById(R.id.linear);

		adView = new AdView(getActivity());
		adView.setAdUnitId("")
		adView.setAdSize(AdSize.SMART_BANNER);

		LinearLayout layout = (LinearLayout)view.findViewById(R.id.adLayout);

		layout.addView(adView);

		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);

		
		autorhora = (CustomTextView)view.findViewById(R.id.autor);
		titulo = (CustomTextView)view.findViewById(R.id.titulo);
		imagem = (ImageView)view.findViewById(R.id.image);
		Ion.with (imagem).load(getActivity().getIntent().getStringExtra("imagem").replace("s1600", preferences.getString("prefImageQuality", "s400")));
		imagem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FragmentActivity.class);
				intent.putExtra("fragment", 8);
				intent.putExtra("imgurl", getActivity().getIntent().getStringExtra("imagem"));
				startActivity(intent);
			}
		});
		titulo.setText(getActivity().getIntent().getStringExtra("titulo"));

		net.aloogle.acasadocogumelo.activity.FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), "");
		((ActionBarActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);

		fabcomment = (FloatingActionButton)view.findViewById(R.id.fabcomments);

		if (Build.VERSION.SDK_INT <= 10) {
			fabcomment.setShadow(false);
		}

		fabcomment.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(getActivity(), net.aloogle.acasadocogumelo.activity.FragmentActivity.class);
				intent.putExtra("fragment", 5);
				intent.putExtra("comments", true);
				intent.putExtra("titulo", "Comentários");
				intent.putExtra("url", "https://www.facebook.com/plugins/comments.php?href=" + getActivity().getIntent().getStringExtra("url") + "&locale=pt_BR&numposts=5");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
			}
		});

		fabcomment.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast toast = Toast.makeText(getActivity(), "Comentários do Facebook", Toast.LENGTH_SHORT);
				toast.show();
				return true;
			}
		});

		ObservableScrollView scroll = (ObservableScrollView)view.findViewById(R.id.scrollView1);
		scroll.setOnScrollChangedListener(this);

		if (savedInstanceState != null) {}
		else {
			new JSONParse().execute();
		}
		return view;
	}

	private class JSONParse extends AsyncTask <String, String, JSONObject> {

		@Override
		protected void onPreExecute() {
			progressBar = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
			progressBar.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected JSONObject doInBackground(String...args) {
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl("http://apps.aloogle.net/blogapp/acasadocogumelo/app/json/post.php?id=" + getActivity().getIntent().getStringExtra("id"));
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			if (finished == false) {
				progressBar.setVisibility(View.GONE);
				try {
					try {
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

							if(seconds < 0) {
								autorhora.setText("Por " + json.getString("autor"));
							} else {
							String what = "";
							
							if(seconds < 60) {
								String plural = null;
								if(seconds <= 1) {
									plural = "segundo";
								} else {
									plural = "segundos";
								}
								what = String.valueOf(seconds) + " " + plural;
							} else if (minutes < 60) {
								String plural = null;
								if(seconds <= 1) {
									plural = "minuto";
								} else {
									plural = "minutos";
								}
								what = String.valueOf(minutes) + " " + plural;
							} else if (hours < 24) {
								String plural = null;
								if(hours <= 1) {
									plural = "hora";
								} else {
									plural = "horas";
								}
								what = String.valueOf(hours) + " " + plural;
							} else if (days < 31) {
								String plural = null;
								if(days <= 1) {
									plural = "dia";
								} else {
									plural = "dias";
								}
								what = String.valueOf(days) + " " + plural;
							}
							
							if(what.equals("")) {
								Calendar c = Calendar.getInstance();
								int year = c.get(Calendar.YEAR);
								String withyear = "";
								if(json.getString("data2").contains(String.valueOf(year))) {
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
								LayoutInflater textoinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View texto = textoinflater.inflate(R.layout.post_text, null);
								CustomTextView text = (CustomTextView)texto.findViewById(R.id.texto);
								text.setMovementMethod(LinkMovementMethod.getInstance());
								text.setText(Html.fromHtml(textoarray.get(i).toString(), null, new TagAdapter()));
								linear.addView(texto);
							}
							if (i < imagemarray.size()) {
								if (imagemarray.get(i).toString().equals("")) {
									if (iframearray.get(i).toString().contains("youtube.com/embed")) {
										LayoutInflater videoinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
										View video = videoinflater.inflate(R.layout.post_video, null);
										String[]parts = iframearray.get(i).toString().split("/");
										final String videoId = parts[parts.length - 1];
										ImageView imageView = (ImageView)video.findViewById(R.id.thumbnail);
										final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)video.findViewById(R.id.progress);
										Ion.with(getActivity())
										.load("http://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg")
											.progress(new ProgressCallback() {
												@Override
												public void onProgress(final long downloaded, final long total) {
													getActivity().runOnUiThread(new Runnable() {
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
												Intent intent = new Intent(getActivity(), PlayerViewActivity.class);
												intent.putExtra("id", videoId);
												startActivity(intent);
											}
										});
										linear.addView(video);
									} else {
										LayoutInflater webviewinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
										LayoutInflater imageminflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
										final View imagem = imageminflater.inflate(R.layout.post_image, null);
										ImageView imageView = (ImageView)imagem.findViewById(R.id.imagem);
										final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)imagem.findViewById(R.id.progress);
										Ion.with(getActivity())
										.load(imagemarray.get(i).toString().replace("s1600", preferences.getString("prefImageQuality", "s400")))
											.progress(new ProgressCallback() {
												@Override
												public void onProgress(final long downloaded, final long total) {
													getActivity().runOnUiThread(new Runnable() {
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
												Intent intent = new Intent(getActivity(), FragmentActivity.class);
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

						LinearLayout tagslayout = (LinearLayout)view.findViewById(R.id.tags);
						LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

						TextView tagscall = new TextView(getActivity());
						tagscall.setLayoutParams(vp);
						tagscall.setText("Tags: ");
						tagscall.setTextSize(16);
						tagscall.setTextColor(Color.parseColor("#000000"));
						tagslayout.addView(tagscall);

						for (int i = 0; i < tagarray.size(); i++) {
							if (i == 0 || i == tagarray.size()) {}
							else {
								TextView space = new TextView(getActivity());
								space.setLayoutParams(vp);
								space.setText(", ");
								space.setTextSize(16);
								space.setTextColor(Color.parseColor("#000000"));
								tagslayout.addView(space);
							}

							TextView tagtext = new TextView(getActivity());
							tagtext.setLayoutParams(vp);
							tagtext.setText(tagarray.get(i).toString());
							tagtext.setTextSize(16);
							tagtext.setTextColor(Color.parseColor("#ff0000"));
							tagslayout.addView(tagtext);

							final int e = i;

							tagtext.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(getActivity(), net.aloogle.acasadocogumelo.activity.FragmentActivity.class);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.post_menu, menu);

		MenuItem shareItem = menu.findItem(R.id.menu_share);
		ShareActionProvider shareProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);

		Intent sharePageIntent = new Intent();
		sharePageIntent.setAction(Intent.ACTION_SEND);
		sharePageIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getIntent().getStringExtra("titulo") + " " + getActivity().getIntent().getStringExtra("url"));
		sharePageIntent.setType("text/plain");
		shareProvider.setShareIntent(sharePageIntent);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		String iconcolor = preferences.getString("prefIconColor", "branco");
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
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_favorite:
			if (favorite) {
				editor.putString("favoritesPostsId", preferences.getString("favoritesPostsId", "").replace(getActivity().getIntent().getStringExtra("id") + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsTitle", preferences.getString("favoritesPostsTitle", "").replace(getActivity().getIntent().getStringExtra("titulo") + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsDescription", preferences.getString("favoritesPostsDescription", "").replace(getActivity().getIntent().getStringExtra("descricao") + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsImage", preferences.getString("favoritesPostsImage", "").replace(getActivity().getIntent().getStringExtra("imagem") + "$%#", ""));
				editor.commit();
				editor.putString("favoritesPostsUrl", preferences.getString("favoritesPostsUrl", "").replace(getActivity().getIntent().getStringExtra("url") + "$%#", ""));
				editor.commit();
				favorite = false;
			} else {
				editor.putString("favoritesPostsId", getActivity().getIntent().getStringExtra("id") + "$%#" + preferences.getString("favoritesPostsId", ""));
				editor.commit();
				editor.putString("favoritesPostsTitle", getActivity().getIntent().getStringExtra("titulo") + "$%#" + preferences.getString("favoritesPostsTitle", ""));
				editor.commit();
				editor.putString("favoritesPostsDescription", getActivity().getIntent().getStringExtra("descricao") + "$%#" + preferences.getString("favoritesPostsDescription", ""));
				editor.commit();
				editor.putString("favoritesPostsImage", getActivity().getIntent().getStringExtra("imagem") + "$%#" + preferences.getString("favoritesPostsImage", ""));
				editor.commit();
				editor.putString("favoritesPostsUrl", getActivity().getIntent().getStringExtra("url") + "$%#" + preferences.getString("favoritesPostsImage", ""));
				editor.commit();
				favorite = true;
			}
			getActivity().supportInvalidateOptionsMenu();
			return true;
		case R.id.menu_copylink:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				final android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				final android.content.ClipData clipData = android.content.ClipData.newPlainText(getActivity().getIntent().getStringExtra("url"), getActivity().getIntent().getStringExtra("url"));
				clipboardManager.setPrimaryClip(clipData);
			} else {
				final android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(getActivity().getIntent().getStringExtra("url"));
			}
			Toast toast = Toast.makeText(getActivity(), "Link copiado para a área de transferência", Toast.LENGTH_SHORT);
			toast.show();
			return true;
		case R.id.menu_openinbrowser:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(getActivity().getIntent().getStringExtra("url")));
			startActivity(intent);
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
		if (y < 400) {
			fabcomment.show(true);
		} else if (y < oldy) {
			fabcomment.show(true);
		} else {
			fabcomment.hide(true);
		}
		
		View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
		int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
		if (diff == 0) {
			fabcomment.show(true);
		}
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

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}
}
