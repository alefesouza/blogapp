package br.com.vidadesuporte.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.adapter.CardAdapter;
import br.com.vidadesuporte.other.Other;
import com.github.ksoichiro.android.observablescrollview.*;
import br.com.vidadesuporte.activity.*;
import br.com.vidadesuporte.other.*;
import android.content.*;
import android.content.SharedPreferences.Editor;
import com.google.gson.*;
import android.support.design.widget.*;
import android.preference.*;
import android.support.v7.widget.*;
import jp.wasabeef.recyclerview.animators.*;
import jp.wasabeef.recyclerview.animators.adapters.*;
import java.util.*;
import android.content.res.*;
import java.net.*;
import java.io.*;
import java.text.*;
import br.com.vidadesuporte.lib.*;

@SuppressLint("InflateParams")
public class MainFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_main, container, false);

		firstUrl = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?id=" + getString(R.string.blogid);
		url = firstUrl;
		
		configCreate();
		mSwipeLayout.setOnRefreshListener(this);

		lastisfromoff = false;

		final boolean hasHome = preferences.contains("homeJson");
		if(hasHome) {
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject)parser.parse(preferences.getString("homeJson", ""));
			makeList(json, true, false);
			lastisfromoff = true;
		}
		
		if(Other.isConnected(getActivity())) {
			if(hasHome) {
				getActivity().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
			}
			getPosts(hasHome);
			if(Build.VERSION.SDK_INT == 10) {
				new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							getPosts(hasHome);
						}
					}, 2000);
			}
		} else {
			if(!preferences.contains("homeJson")) {
				setError();
			}
		}

		list.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
				@Override
				public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
					mSwipeLayout.setEnabled(scrollY == 0);
				}

				@Override
				public void onDownMotionEvent() {
				}

				@Override
				public void onUpOrCancelMotionEvent(ScrollState scrollState) {
					if(scrollState == ScrollState.UP) {
						Other.fabShow(false, MainActivity.fabrandom);
					} else if(scrollState == ScrollState.DOWN) {
						Other.fabShow(true, MainActivity.fabrandom);
					}
				}
			});

		return view;
	}

	public void getPosts(final boolean fromUpdate) {
		Ion.with(this)
			.load(url)
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, final JsonObject json) {
					if(e != null) {
						Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
						toast.show();
						if(fromUpdate) {
							getActivity().findViewById(R.id.progressBar2).setVisibility(View.GONE);
						}
						if(isfirst && !preferences.contains("homeJson")) {
							setError();
						} else {
							Space(footer5, 50);
							block = true;
						}
						e.printStackTrace();
						return;
					}

					JsonArray posts = json.get(TAG_POSTS).getAsJsonArray();

					if(url.equals(firstUrl) && preferences.contains("homeJson")) {
						if(!preferences.getString("homeJson", "").equals(json.toString()) && posts.size() > 0) {
							editor.putString("homeJson", json.toString());
							editor.commit();
							update(false);
							try {
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String postLastData = posts.get(posts.size() - 1).getAsJsonObject().get("data").getAsString();
								Date postLastDate = dateFormat.parse(postLastData);
								Date mostRecentDate = dateFormat.parse(postsarray.get(0).getDate());

								if(postLastDate.getTime() < mostRecentDate.getTime()) {
									ArrayList <JsonObject> js = new ArrayList<JsonObject>();
									for(int i = 0; i < posts.size(); i++) {
										String postData = posts.get(i).getAsJsonObject().get("data").getAsString();
										Date postDate = dateFormat.parse(postData);

										boolean diff = mostRecentDate.getTime() < postDate.getTime();
										if(diff) {
											js.add(posts.get(i).getAsJsonObject());
										} else {
											break;
										}
									}

									Collections.reverse(js);

									for(int i = 0; i < js.size(); i++) {
										JsonObject c = js.get(i);

										String id = c.get(TAG_ID).getAsString();
										String titulo = c.get(TAG_TITULO).getAsString();
										String descricao = c.get(TAG_DESCRICAO).getAsString();
										String imagem = c.get(TAG_IMAGEM).getAsString();
										String url = c.get(TAG_URL).getAsString();
										String comentarios = c.get(TAG_COMENTARIOS).getAsString();
										String categoria = c.get("categoriaicon").getAsString();
										String data = c.get("data").getAsString();

										postsarray.add(0, new Posts(id, titulo, imagem, descricao, url, comentarios, categoria, data));
										hv.notifyItemInserted(0);
									}
									lm.scrollToPosition(0);
									update(false);
								} else {
									update(false);
									makeList(json, false, fromUpdate);
								}
							}
							catch(ParseException p) {}
						}
					} else if(url.equals(firstUrl) && !preferences.contains("homeJson")) {
						editor.putString("homeJson", json.toString());
						editor.commit();
						makeList(json, false, fromUpdate);
					} else {
						makeList(json, false, fromUpdate);
					}

					mSwipeLayout.setRefreshing(false);
					getActivity().findViewById(R.id.progressBar2).setVisibility(View.GONE);
				}
			});
	}

	public void makeList(JsonObject json, boolean fromOff, boolean fromUpdate) {
		if(fromUpdate) {
			postsarray.clear();
		}
		JsonArray posts = json.get(TAG_POSTS).getAsJsonArray();
		if(ismore) {
			if(!passed) {
				more = more + posts.size();
			}
		}
		block = false;
		passed = false;

		lastMore = posts.size();

		for(int i = 0; i < posts.size(); i++) {
			JsonObject c = posts.get(i).getAsJsonObject();

			String id = c.get(TAG_ID).getAsString();
			String titulo = c.get(TAG_TITULO).getAsString();
			String descricao = c.get(TAG_DESCRICAO).getAsString();
			String imagem = c.get(TAG_IMAGEM).getAsString();
			String url = c.get(TAG_URL).getAsString();
			String comentarios = c.get(TAG_COMENTARIOS).getAsString();
			String categoria = c.get("categoriaicon").getAsString();
			String data = c.get("data").getAsString();

			postsarray.add(new Posts(id, titulo, imagem, descricao, url, comentarios, categoria, data));
		}

		if(isfirst) {
			if(Build.VERSION.SDK_INT >= 21) {
				progressBar.setVisibility(View.GONE);
			} else {
				progressBarCompat.setVisibility(View.GONE);
			}
			list.setVisibility(View.VISIBLE);
			isfirst = false;
		} else {
			hv.notifyDataSetChanged();
		}

		if(Build.VERSION.SDK_INT > 10) {
			if(lastMore < 10) {
				Space(footer4, 50);
				nomore = true;
			}
		}

		if(!fromOff) {
			page ++;
		}

		url = firstUrl + "&page=" + page;
	}

	public void update(boolean toLimpar) {
		if(Other.isConnected(getActivity())) {
			url = firstUrl;
			more = 0;
			ismore = false;
			block = true;
			page = 1;
			if(nomore) {
				Space(footer3, 0);
			}
			if(!toLimpar) {
				getPosts(true);
			}
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_refresh:
				mSwipeLayout.setRefreshing(true);
				update(true);
				return true;
			default:
				return
					super.onOptionsItemSelected(item);
		}
	}

	public void onRefresh() {
		update(true);
	}
}
