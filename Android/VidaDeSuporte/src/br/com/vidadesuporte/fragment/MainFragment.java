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
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.adapter.CardAdapter;
import br.com.vidadesuporte.other.Other;
import com.github.ksoichiro.android.observablescrollview.*;
import br.com.vidadesuporte.activity.*;
import br.com.vidadesuporte.other.*;

@SuppressLint("InflateParams")
public class MainFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, ObservableScrollViewCallbacks {
	
	Activity activity;
	View view;
	public static ObservableListView list;
	ArrayList <Posts> postsarray = new ArrayList <Posts>();
	int more, mLastFirstVisibleItem, page, lastMore;
	boolean ismore, block, isfirst, passed, nomore, seted;
	String title, lastUrl;
	ViewGroup footer3, footer4, footer5;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	private SwipeRefreshLayout mSwipeLayout;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	private static String url;
	private static final String TAG_POSTS = "posts";
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "titulo";
	private static final String TAG_DESCRICAO = "descricao";
	private static final String TAG_IMAGEM = "imagem";
	private static final String TAG_URL = "url";
	private static final String TAG_COMENTARIOS = "comentarios";

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
		view = inflater.inflate(R.layout.fragment_main, container, false);

		url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?id=" + getString(R.string.blogid);
		page = 1;
			
		lastMore = 10;
		lastUrl = url;

		list = (ObservableListView)view.findViewById(R.id.list);
		list.setScrollViewCallbacks(this);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflatere.inflate(R.layout.footer3, list, false);
		footer4 = (ViewGroup)inflatere.inflate(R.layout.no_more, list, false);
		footer5 = (ViewGroup)inflatere.inflate(R.layout.load_more, list, false);
		list.addFooterView(footer3, null, false);

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent,
											 R.color.logo_red, R.color.logo_black,
											 R.color.logo_red);
		if (Other.isConnected(getActivity())) {
			getPosts();
			if (Build.VERSION.SDK_INT == 10) {
				new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							getPosts();
						}
					}, 2000);
			}
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
					passed = false;

					lastMore = posts.size();

					if (Build.VERSION.SDK_INT > 10) {
						if (lastMore < 10) {
							list.removeFooterView(footer3);
							list.addFooterView(footer4, null, false);
							nomore = true;
						}
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
						list.setOnScrollListener(MainFragment.this);
						seted = true;
					} else {
						swingBottomInAnimationAdapter.notifyDataSetChanged(true);
					}

					lastUrl = url;

					page ++;
					url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?page=" + page + "&id=" + getString(R.string.blogid);
					isfirst = false;
					list.setVisibility(View.VISIBLE);
				}});}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				if (Other.isConnected(getActivity())) {
					postsarray.clear();
					url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?id=" + getString(R.string.blogid);
					list.setVisibility(View.GONE);
					more = 0;
					ismore = false;
					block = true;
					isfirst = true;
					page = 1;
					if (nomore) {
						list.removeFooterView(footer4);
						list.addFooterView(footer3, null, false);
					}
					getPosts();
				} else {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
				return true;
			default:
				return
					super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
			if (lastMore == 10) {
				if (!block) {
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

		boolean enabled = list.getChildCount() > 0 && list.getChildAt(0).getTop() == 0 && list.getFirstVisiblePosition() == 0;
		mSwipeLayout.setEnabled(enabled);
	}
	


	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
	}

	@Override
	public void onDownMotionEvent() {
	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		if(scrollState == ScrollState.UP) {
			Other.fabShow(false, MainActivity.fabrandom);
		} else if (scrollState == ScrollState.DOWN) {
			Other.fabShow(true, MainActivity.fabrandom);
		}
	}

	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
			list.setVisibility(View.GONE);
			postsarray.clear();
			url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?id=" + getString(R.string.blogid);
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
}
