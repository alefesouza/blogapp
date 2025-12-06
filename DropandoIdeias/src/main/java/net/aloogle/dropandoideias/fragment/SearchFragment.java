package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.FragmentActivity;
import net.aloogle.dropandoideias.other.Other;
import net.aloogle.dropandoideias.other.Posts;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class SearchFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
	String search, suggestion;

	ArrayList <String> categoriaarray = new ArrayList <String> ();

	ArrayList <String> reallyarray = new ArrayList <String> ();
	private SimpleCursorAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_main, container, false);

		if (savedInstanceState != null) {
			page = savedInstanceState.getInt("page");
			url = savedInstanceState.getString("url");
			search = savedInstanceState.getString("search");
		} else {
			search = getActivity().getIntent().getStringExtra("query");
			page = 1;
		}

		configCreate(false);
		firstUrl = defaultUrl + "main.php" + defaultQuery + "&search=" + search;
		url = firstUrl;

		mSwipeLayout.setOnRefreshListener(this);

		final String[]from = new String[]{
			"categoryName"
		};
		final int[]to = new int[]{
			R.id.text1
		};
		mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		try {
			FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + URLDecoder.decode(search, "UTF-8"));
		} catch (UnsupportedEncodingException e) {}

		list.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
			@Override
			public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
				mSwipeLayout.setEnabled(scrollY == 0);
			}

			@Override
			public void onDownMotionEvent() {}

			@Override
			public void onUpOrCancelMotionEvent(ScrollState scrollState) {}
		});

		if (Other.isConnected(getActivity())) {
			getPosts(false, false);
		} else {
			setError();
		}
		return view;
	}

	public void getPosts(final boolean fromUpdate, final boolean toStore) {
		stored = null;
		Ion.with (this)
		.load(url)
		.asJsonObject()
		.setCallback(new FutureCallback < JsonObject > () {
			@Override
			public void onCompleted(Exception e, JsonObject json) {
				if (e != null) {
					if (!toStore) {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
					}
					if (isfirst) {
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
					makeList(json, false, fromUpdate);
					getPosts(false, true);
				}
			}
		});
	}

	@Override
	public void makeList(JsonObject json, boolean fromOff, boolean fromUpdate) {
		if (fromUpdate) {
			postsarray.clear();
		}
		mSwipeLayout.setRefreshing(false);
		JsonArray posts = json.get(TAG_POSTS).getAsJsonArray();
		if (ismore) {
			if (!passed) {
				more = more + posts.size();
			}
		}
		block = false;

		lastMore = posts.size();

		if (lastMore < Other.numberPosts) {
			Space(footer4, 50);
			nomore = true;
		}

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

		hv.notifyDataSetChanged();
		if (fromUpdate) {
			list.scrollToPosition(0);
		}

		page++;
		url = defaultUrl + "main.php" + defaultQuery + "&search=" + search + "&page=" + String.valueOf(page) + "&id=" + getString(R.string.blogid);

		if (isfirst) {
				progressBar.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			isfirst = false;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_search);

		SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint(getString(R.string.search));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				try {
					try {
						mSwipeLayout.setRefreshing(true);
						defaultVariables();
						search = URLEncoder.encode(query, "UTF-8");
						if (nomore) {
							Space(footer3, 0);
						}
						FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + query);
						url = defaultUrl + "main.php" + defaultQuery + "&search=" + search + "&id=" + getString(R.string.blogid);
						getPosts(true, false);
					} catch (UnsupportedEncodingException e) {}
				} catch (Exception e) {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String s) {
				suggestion = s;
				if (Other.isConnected(getActivity())) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (s.equals(suggestion)) {
								Ion.with (getActivity())
								.load(defaultUrl + "tags.php?q=" + suggestion.replace(" ", "%20") + "&id=" + getString(R.string.blogid))
								.asJsonObject()
								.setCallback(new FutureCallback < JsonObject > () {
									@Override
									public void onCompleted(Exception e, JsonObject json) {
										categoriaarray.clear();
										JsonArray categorias = json.get("categories").getAsJsonArray();
										for (int i = 0; i < categorias.size(); i++) {
											JsonObject c = categorias.get(i).getAsJsonObject();

											String categoria = c.get("category").getAsString();
											categoriaarray.add(categoria);
										}
										populateAdapter();
									}
								});
							}

						}
					}, 1000);
				}
				return false;
			}
		});

		searchView.setSuggestionsAdapter(mAdapter);

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionClick(int position) {
				try {
					mSwipeLayout.setRefreshing(true);
					defaultVariables();
					search = URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8");
					if (nomore) {
						Space(footer3, 0);
					}
					FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + reallyarray.get(position).toString());
					url = defaultUrl + "main.php" + defaultQuery + "&search=" + search + "&id=" + getString(R.string.blogid);
					getPosts(true, false);
				} catch (UnsupportedEncodingException e) {}
				return true;
			}

			@Override
			public boolean onSuggestionSelect(int position) {
				try {
					mSwipeLayout.setRefreshing(true);
					defaultVariables();
					search = URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8");
					if (nomore) {
						Space(footer3, 0);
					}
					FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + reallyarray.get(position).toString());
					url = defaultUrl + "main.php" + defaultQuery + "&search=" + search + "&id=" + getString(R.string.blogid);
					getPosts(true, false);
				} catch (UnsupportedEncodingException e) {}
				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_refresh).setVisible(false);
		menu.findItem(R.id.menu_opensite).setVisible(false);

		super.onPrepareOptionsMenu(menu);
	}

	private void populateAdapter() {
		final MatrixCursor c = new MatrixCursor(new String[]{
				BaseColumns._ID, 			"categoryName"
			});
		for (int i = 0; i < categoriaarray.size(); i++) {
			if (categoriaarray.get(i).toString().toLowerCase().startsWith(suggestion.toLowerCase())) {
				reallyarray.add(categoriaarray.get(i).toString());
				c.addRow(new Object[]{
					i, 				categoriaarray.get(i).toString()
				});
			}
			mAdapter.changeCursor(c);
		}
	}

	public void defaultVariables() {
		more = 0;
		ismore = false;
		block = true;
		isfirst = true;
		passed = true;
		page = 1;
	}

	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
			url = defaultUrl + "main.php" + defaultQuery + "&search=" + search + "&id=" + getString(R.string.blogid);
			defaultVariables();
			if (nomore) {
				Space(footer3, 0);
			}
			getPosts(true, false);
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	public void clearAll() {
		postsarray.clear();
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("search", search);
		savedInstanceState.putInt("page", page);
		savedInstanceState.putString("url", url);
		super.onSaveInstanceState(savedInstanceState);
	}
}
