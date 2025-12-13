package br.com.vidadesuporte.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.MatrixCursor;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.activity.FragmentActivity;
import br.com.vidadesuporte.adapter.CardAdapter;
import br.com.vidadesuporte.other.Other;
import br.com.vidadesuporte.other.*;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class SearchFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	SharedPreferences preferences;
	Editor editor;

	ObservableListView list;
	ArrayList <Posts> postsarray = new ArrayList <Posts>();
	int more, page, lastMore;
	boolean ismore, block, isfirst, passed, nomore, seted;
	ViewGroup footer3, footer4, footer5;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	private SwipeRefreshLayout mSwipeLayout;

	String url, search, lastUrl, suggestion;
	private static final String TAG_POSTS = "posts";
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "titulo";
	private static final String TAG_DESCRICAO = "descricao";
	private static final String TAG_IMAGEM = "imagem";
	private static final String TAG_URL = "url";
	private static final String TAG_COMENTARIOS = "comentarios";

	View view;

	ArrayList <String> categoriaarray = new ArrayList <String>();

	ArrayList <String> reallyarray = new ArrayList <String>();
	private SimpleCursorAdapter mAdapter;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		view = inflater.inflate(R.layout.fragment_category, container, false);

		final String[]from = new String[]{
			"categoryName"
		};
		final int[]to = new int[]{
			R.id.text1
		};
		mAdapter = new SimpleCursorAdapter(getActivity(), 			R.layout.simple_list_item_1, 			null, 			from, 			to, 			CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		if (savedInstanceState != null) {
			page = savedInstanceState.getInt("page");
			lastUrl = savedInstanceState.getString("url");
			url = lastUrl;
			search = savedInstanceState.getString("search");
		} else {
			search = getActivity().getIntent().getStringExtra("query");
			page = 1;
			lastUrl = url;
		}

		lastMore = 10;

		try {
			FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + URLDecoder.decode(search, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {}

		list = (ObservableListView)view.findViewById(R.id.scroll);

		if (Build.VERSION.SDK_INT >= 21) {
			if (preferences.getString("prefColor", "padrao").equals("padrao")) {
				view.findViewById(R.id.dropshadow).setVisibility(View.VISIBLE);
			} else {
				view.findViewById(R.id.dropshadow).setVisibility(View.GONE);
			}
		}

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent,
											 R.color.colorAccent, R.color.colorAccent,
											 R.color.colorAccent);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflater.inflate(R.layout.footer3, list, false);
		footer4 = (ViewGroup)inflater.inflate(R.layout.no_more, list, false);
		footer5 = (ViewGroup)inflatere.inflate(R.layout.load_more, list, false);
		list.addFooterView(footer3, null, false);

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;
		page = 1;

		url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?search=" + search + "&id=" + getString(R.string.blogid);

		if (Other.isConnected(getActivity())) {
			getPosts();
		} else {
			final RelativeLayout mainContent = (RelativeLayout)view.findViewById(R.id.main_content);
			mainContent.setVisibility(View.GONE);
			final RelativeLayout fragment = (RelativeLayout)view.findViewById(R.id.fragment);
			LayoutInflater errorinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View error = errorinflater.inflate(R.layout.error, null);
			LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			error.setLayoutParams(vp);
			error.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Other.isConnected(getActivity())) {
							fragment.removeView(error);
							mainContent.setVisibility(View.VISIBLE);
							getPosts();
						} else {
							Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
							toast.show();
						}
					}
				});
			fragment.addView(error);
		}
		return view;
	}


	public void getPosts() {
		if (isfirst) {
			if (Build.VERSION.SDK_INT >= 21) {
				progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
				progressBarCompat.setVisibility(View.VISIBLE);
			}
		}
		Ion.with(this)
			.load(url)
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, JsonObject json) {
					if (e != null) {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
						return;
					}
					if (isfirst) {
						if (Build.VERSION.SDK_INT >= 21) {
							progressBar.setVisibility(View.GONE);
						} else {
							progressBarCompat.setVisibility(View.GONE);
						}
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

					if (lastMore < 10) {
						list.removeFooterView(footer3);
						list.addFooterView(footer4, null, false);
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

						postsarray.add(new Posts(id, titulo, imagem, descricao, url, comentarios));
					}
					
					if(!seted) {
						swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new CardAdapter(getActivity(), postsarray));
						swingBottomInAnimationAdapter.setAbsListView(list);

						assert swingBottomInAnimationAdapter.getViewAnimator() != null;
						swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

						list.setAdapter(swingBottomInAnimationAdapter);
						list.setOnScrollListener(SearchFragment.this);
						seted = true;
					} else {
						swingBottomInAnimationAdapter.notifyDataSetChanged(true);
					}

					page++;
					url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?search=" + search + "&page=" + String.valueOf(page) + "&id=" + getString(R.string.blogid);
					isfirst = false;
					list.setVisibility(View.VISIBLE);
				}});}

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
							clearAll();
							list.setVisibility(View.GONE);
							more = 0;
							ismore = false;
							block = true;
							isfirst = true;
							passed = true;
							page = 1;
							search = URLEncoder.encode(query, "UTF-8");
							if (nomore) {
								list.removeFooterView(footer4);
								list.addFooterView(footer3, null, false);
							}
							FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + query);
							url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?search=" + search + "&id=" + getString(R.string.blogid);
							getPosts();
						}
						catch (UnsupportedEncodingException e) {}
					}
					catch (Exception e) {
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
										Ion.with(getActivity())
											.load("http://apps.aloogle.net/blogapp/wordpress/json/tags.php?q=" + suggestion.replace(" ", "%20") + "&id=" + getString(R.string.blogid))
											.asJsonObject()
											.setCallback(new FutureCallback<JsonObject>() {
												@Override
												public void onCompleted (Exception e, JsonObject json) {
													categoriaarray.clear();
													JsonArray categorias = json.get("categorias").getAsJsonArray();
													for (int i = 0; i < categorias.size(); i++) {
														JsonObject c = categorias.get(i).getAsJsonObject();

														String categoria = c.get("categoria").getAsString();
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
						clearAll();
						list.setVisibility(View.GONE);
						more = 0;
						ismore = false;
						block = true;
						isfirst = true;
						passed = true;
						search = URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8");
						if (nomore) {
							list.removeFooterView(footer4);
							list.addFooterView(footer3, null, false);
						}
						FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + reallyarray.get(position).toString());
						url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?search=" + search + "&id=" + getString(R.string.blogid);
						getPosts();
					}
					catch (UnsupportedEncodingException e) {}
					return true;
				}

				@Override
				public boolean onSuggestionSelect(int position) {
					try {
						clearAll();
						list.setVisibility(View.GONE);
						more = 0;
						ismore = false;
						block = true;
						isfirst = true;
						passed = true;
						search = URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8");
						if (nomore) {
							list.removeFooterView(footer4);
							list.addFooterView(footer3, null, false);
						}
						FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + reallyarray.get(position).toString());
						url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?search=" + search + "&id=" + getString(R.string.blogid);
						getPosts();
					}
					catch (UnsupportedEncodingException e) {}
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
													BaseColumns._ID,
													"categoryName"
												});
		for (int i = 0; i < categoriaarray.size(); i++) {
			if (categoriaarray.get(i).toString().toLowerCase().startsWith(suggestion.toLowerCase())) {
				reallyarray.add(categoriaarray.get(i).toString());
				c.addRow(new Object[]{
							 i,
							 categoriaarray.get(i).toString()
						 });
			}
			mAdapter.changeCursor(c);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, 	int visibleItemCount, int totalItemCount) {
		if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
			if (lastMore == 10) {
				if (block == false) {
					if (Other.isConnected(getActivity())) {
						ismore = true;
						getPosts();
						block = true;
					} else {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
						list.removeFooterView(footer3);
						footer5.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									if (Other.isConnected(getActivity())) {
										list.removeFooterView(footer5);
										list.addFooterView(footer3);
										ismore = true;
										getPosts();
									} else {
										Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
										toast.show();
									}
								}
							});
						list.addFooterView(footer5);
						block = true;
					}
				}
			}
		}

		if (list.getChildCount() > 0 && list.getChildAt(0).getTop() == 0 && list.getFirstVisiblePosition() == 0) {
			mSwipeLayout.setEnabled(true);
		} else {
			mSwipeLayout.setEnabled(false);
		}
	}

	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
			list.setVisibility(View.GONE);
			clearAll();
			url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?search=" + search + "&id=" + getString(R.string.blogid);
			more = 0;
			ismore = false;
			block = true;
			page = 1;
			if (nomore) {
				list.removeFooterView(footer4);
				list.addFooterView(footer3, null, false);
			}
			getPosts();
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
		savedInstanceState.putString("url", lastUrl);
		super.onSaveInstanceState(savedInstanceState);
	}
}
