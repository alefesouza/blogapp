package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.MainActivity;
import net.aloogle.dropandoideias.database.helper.DatabaseHelper;
import net.aloogle.dropandoideias.database.model.Jsons;
import net.aloogle.dropandoideias.other.Other;
import net.aloogle.dropandoideias.other.Posts;

@SuppressLint({ "InflateParams", "DefaultLocale", "SimpleDateFormat" })
public class MainFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
	String homeJson, toupdate;
	boolean hasHome, updatedb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_main, container, false);

		configCreate(true);

		firstUrl = defaultUrl + "main.php" + defaultQuery;
		url = firstUrl;
		hasHome = false;

		mSwipeLayout.setOnRefreshListener(this);

		lastisfromoff = false;

		list.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
			@Override
			public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
				mSwipeLayout.setEnabled(scrollY == 0);
			}

			@Override
			public void onDownMotionEvent() {}

			@Override
			public void onUpOrCancelMotionEvent(ScrollState scrollState) {
				if (scrollState == ScrollState.UP) {
					Other.fabShow(false, MainActivity.fabrandom);
				} else if (scrollState == ScrollState.DOWN) {
					Other.fabShow(true, MainActivity.fabrandom);
				}
			}
		});

		new CompareIfExists().execute();

		return view;
	}

	public void getPosts(final boolean fromUpdate, final boolean toStore) {
		stored = null;
		Ion.with (this)
		.load(url)
		.asJsonObject()
		.setCallback(new FutureCallback < JsonObject > () {
			@Override
			public void onCompleted(Exception e, final JsonObject json) {
				if (e != null) {
					if (!toStore) {
						Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
						toast.show();
					}
					if (fromUpdate) {
						getActivity().findViewById(R.id.progressBar2).setVisibility(View.GONE);
					}
					if (isfirst && !hasHome) {
						setError();
					} else {
						Space(footer5, 50);
						block = true;
					}
					e.printStackTrace();
					return;
				}

				if (toStore) {
					stored = json;
				} else {
					if (url.equals(firstUrl) && hasHome) {
						toupdate = json.toString();
                        updatedb = true;
						new HomeJson().execute();
                        update(true);
					} else if (url.equals(firstUrl) && !hasHome) {
						homeJson = json.toString();
						new HomeJson().execute();
					}
                    makeList(json, false, fromUpdate);

					mSwipeLayout.setRefreshing(false);
					getActivity().findViewById(R.id.progressBar2).setVisibility(View.GONE);
					getPosts(false, true);
				}
			}
		});
	}

	public void makeList(JsonObject json, boolean fromOff, boolean fromUpdate) {
		if (fromUpdate) {
			postsarray.clear();
		}
		JsonArray posts = json.get(TAG_POSTS).getAsJsonArray();
		if (ismore) {
			if (!passed) {
				more = more + posts.size();
			}
		}
		block = false;
		passed = false;

		lastMore = posts.size();

		for (int i = 0; i < posts.size(); i++) {
			JsonObject c = posts.get(i).getAsJsonObject();

			String id = c.get(TAG_ID).getAsString();
			String titulo = c.get(TAG_TITULO).getAsString();
			String descricao = c.get(TAG_DESCRICAO).getAsString();
			String imagem = c.get(TAG_IMAGEM).getAsString();
			String url = c.get(TAG_URL).getAsString();
			String comentarios = c.get(TAG_COMENTARIOS).getAsString();
			String categoria = c.get(TAG_CATEGORYICON).getAsString();
			String data = c.get(TAG_DATA).getAsString();

			postsarray.add(new Posts(id, titulo, imagem, descricao, url, comentarios, categoria, data));
		}

		if (isfirst) {
				progressBar.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			isfirst = false;
		} else {
			hv.notifyDataSetChanged();
		}

		if (lastMore < Other.numberPosts) {
			Space(footer4, 50);
			nomore = true;
		}

		if (!fromOff) {
			page++;
		}

		url = firstUrl + "&page=" + page;
	}

	public void update(boolean toLimpar) {
		url = firstUrl;
		more = 0;
		ismore = false;
		block = true;
		page = 1;
		if (nomore) {
			Space(footer3, 0);
		}
		if (!toLimpar) {
			getPosts(true, false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			if (Other.isConnected(getActivity())) {
				mSwipeLayout.setRefreshing(true);
				update(false);
			} else {
				mSwipeLayout.setRefreshing(false);
				Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
				toast.show();
			}
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
            update(false);
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private class CompareIfExists extends AsyncTask <String, Jsons, Jsons> {
		DatabaseHelper db = new DatabaseHelper(getActivity());

		@Override
		protected Jsons doInBackground(String...args) {
			Jsons home = db.getJson("home");
			return home;
		}

		@Override
		protected void onPostExecute(Jsons home) {
			hasHome = home != null;
			if (hasHome) {
				homeJson = home.getJson();
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject)parser.parse(homeJson);
				makeList(json, true, false);
				lastisfromoff = true;
			}
			db.closeDB();

			if (Other.isConnected(getActivity())) {
				if (hasHome) {
					getActivity().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
				}
				getPosts(hasHome, false);
				if (Build.VERSION.SDK_INT == 10) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							getPosts(hasHome, false);
						}
					}, 2000);
				}
			} else {
				if (!hasHome) {
					setError();
				}
			}
		}
	}

	private class HomeJson extends AsyncTask <JsonObject, Jsons, Jsons> {
		DatabaseHelper db = new DatabaseHelper(getActivity());

		@Override
		protected Jsons doInBackground(JsonObject...arg) {
			Jsons home = db.getJson("home");
			if (home == null) {
				db.createJson(new Jsons("home", homeJson));
			}
            if(updatedb) {
                db.updateJson(new Jsons("home", toupdate));
            }
			return home;
		}

		@Override
		protected void onPostExecute(Jsons home) {
			db.closeDB();
		}
	}
}
