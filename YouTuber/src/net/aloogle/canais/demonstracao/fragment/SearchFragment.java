package net.aloogle.canais.demonstracao.fragment;

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
import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.activity.FragmentActivity;
import net.aloogle.canais.demonstracao.adapter.CardAdapter;
import net.aloogle.canais.demonstracao.other.Other;
import android.support.v4.view.*;
import net.aloogle.canais.demonstracao.other.*;
import android.widget.*;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class SearchFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	SharedPreferences preferences;
	Editor editor;

	ObservableListView list;
	ArrayList <Videos> videosarray = new ArrayList <Videos>();
	int more;
	boolean ismore, block, isfirst, passed, nomore, seted;
	ViewGroup footer3, footer4, footer5;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	private SwipeRefreshLayout mSwipeLayout;

	String url, search, lastUrl, suggestion, lastToken, firstUrl;
	private static final String TAG_VIDEOS = "videos";
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "titulo";
	private static final String TAG_LIKES = "likes";
	private static final String TAG_VIEWS = "visualizacoes";

	View view;

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

		if (savedInstanceState != null) {
			search = savedInstanceState.getString("search");
		} else {
			search = getActivity().getIntent().getStringExtra("query");
		}

		lastToken = "aloogle";
		url = "http://apps.aloogle.net/blogapp/youtuber/json/search.php?id=" + getString(R.string.channelid) + "&key=" + getString(R.string.developerkey) + "&q=" + search;
		lastUrl = url;
		
		try {
			((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Busca: " + URLDecoder.decode(search, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {}

		list = (ObservableListView)view.findViewById(R.id.scroll);

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
		lastToken = "aloogle";

		if (Other.isConnected(getActivity())) {
			getVideos();
		} else {
			setError();
		}
		return view;
	}


	public void getVideos() {
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
					if(e != null) {
						if(isfirst) {
							setError();
						}
						Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
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
					JsonArray videos = json.get(TAG_VIDEOS).getAsJsonArray();
					if (ismore) {
						if (!passed) {
							more = more + videos.size();
						}
					}
					block = false;

					lastToken = json.get("token").getAsString();

					if (lastToken.equals("")) {
						list.removeFooterView(footer3);
						list.addFooterView(footer4, null, false);
						nomore = true;
					}

					for (int i = 0; i < videos.size(); i++) {
						JsonObject c = videos.get(i).getAsJsonObject();

						String id = c.get(TAG_ID).getAsString();
						String titulo = c.get(TAG_TITULO).getAsString();
						String likes = c.get(TAG_LIKES).getAsString();
						String visualizacoes = c.get(TAG_VIEWS).getAsString();

						videosarray.add(new Videos(id, titulo, likes, visualizacoes));
					}

					if(!seted) {
						swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new CardAdapter(getActivity(), videosarray));
						swingBottomInAnimationAdapter.setAbsListView(list);

						assert swingBottomInAnimationAdapter.getViewAnimator() != null;
						swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

						list.setAdapter(swingBottomInAnimationAdapter);
						list.setOnScrollListener(SearchFragment.this);
						seted = true;
					} else {
						swingBottomInAnimationAdapter.notifyDataSetChanged(true);
					}
					
					url = "http://apps.aloogle.net/blogapp/youtuber/json/search.php?id=" + getString(R.string.channelid) + "&key=" + getString(R.string.developerkey) + "&q=" + search + "&token=" + lastToken;
					isfirst = false;
					list.setVisibility(View.VISIBLE);
				}});}

	public void setError() {
		final RelativeLayout mainContent = (RelativeLayout)view.findViewById(R.id.main_content);
		mainContent.setVisibility(View.GONE);
		final RelativeLayout fragment = (RelativeLayout)view.findViewById(R.id.fragment);
		LayoutInflater errorinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View error = errorinflater.inflate(R.layout.error, null);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		error.setLayoutParams(vp);
		Button er = (Button)error.findViewById(R.id.button);
		er.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Other.isConnected(getActivity())) {
						fragment.removeView(error);
						mainContent.setVisibility(View.VISIBLE);
						getVideos();
					} else {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
					}
				}
			});
		fragment.addView(error);
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
							videosarray.clear();
							list.setVisibility(View.GONE);
							more = 0;
							ismore = false;
							block = true;
							isfirst = true;
							passed = true;
							lastToken = "aloogle";
							search = URLEncoder.encode(query, "UTF-8");
							if (nomore) {
								list.removeFooterView(footer4);
								list.addFooterView(footer3, null, false);
							}
							((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Busca: " + query);
							url = "http://apps.aloogle.net/blogapp/youtuber/json/search.php?id=" + getString(R.string.channelid) + "&key=" + getString(R.string.developerkey) + "&q=" + search;
							getVideos();
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
					return false;
				}
			});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_refresh).setVisible(false);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, 	int visibleItemCount, int totalItemCount) {
		if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
			if (!lastToken.equals("")) {
				if (block == false) {
					if (Other.isConnected(getActivity())) {
						ismore = true;
						getVideos();
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
										getVideos();
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
			videosarray.clear();
			url = "http://apps.aloogle.net/blogapp/youtuber/json/search.php?id=" + getString(R.string.channelid) + "&key=" + getString(R.string.developerkey) + "&q=" + search;
			more = 0;
			ismore = false;
			block = true;
			lastToken = "aloogle";
			if (nomore) {
				list.removeFooterView(footer4);
				list.addFooterView(footer3, null, false);
			}
			getVideos();
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("search", search);
		super.onSaveInstanceState(savedInstanceState);
	}
}
