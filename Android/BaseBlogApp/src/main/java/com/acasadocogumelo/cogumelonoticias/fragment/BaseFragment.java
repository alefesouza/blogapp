package com.acasadocogumelo.cogumelonoticias.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.google.gson.JsonObject;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import com.acasadocogumelo.cogumelonoticias.R;
import com.acasadocogumelo.cogumelonoticias.adapter.CardAdapter;
import net.aloogle.apps.blogapp.lib.HeaderViewRecyclerAdapter;
import net.aloogle.apps.blogapp.other.CustomTextView;
import net.aloogle.apps.blogapp.other.Other;
import net.aloogle.apps.blogapp.other.Posts;

@SuppressLint("InflateParams")
public class BaseFragment extends Fragment {
	Activity activity;
	View view, error;
	List <Posts> postsarray = new ArrayList <Posts> ();
	int more, page, lastMore, anddp, count;
	boolean ismore, block, isfirst, passed, nomore, lastisfromoff, istablet;
	ViewGroup footer3, footer4, footer5, actualfooter;
	ProgressBar progressBar;
	SwipeRefreshLayout mSwipeLayout;
	RelativeLayout mainContent, fragment;

	ObservableRecyclerView list;
	HeaderViewRecyclerAdapter hv;
	LinearLayoutManager lm;
	StaggeredGridLayoutManager mStaggeredGridLayoutManager;
	int firstVisibleItem, lastVisibleItem;
	boolean toGrid;

	SharedPreferences preferences;
	Editor editor;
	JsonObject stored;

	String url, firstUrl, defaultQuery, defaultUrl;
	String TAG_POSTS = "posts";
	String TAG_ID = "id";
	String TAG_TITULO = "title";
	String TAG_DESCRICAO = "description";
	String TAG_IMAGEM = "image";
	String TAG_URL = "url";
	String TAG_COMENTARIOS = "comments";
	String TAG_CATEGORYICON = "categoryicon";
	String TAG_DATA = "date";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@SuppressWarnings("deprecation")
	public void configCreate(boolean toRightPanel) {
		istablet = getResources().getBoolean(R.bool.isTablet);
		toGrid = !toRightPanel;

		mainContent = (RelativeLayout)view.findViewById(R.id.main_content);
		if (mainContent.getVisibility() == View.GONE) {
			mainContent.setVisibility(View.VISIBLE);
		}

		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = preferences.edit();

		defaultUrl = Other.defaultUrl;
		defaultQuery = "?id=" + getString(R.string.blogid) + "&number=" + String.valueOf(Other.numberPosts);
		page = 1;

		lastMore = Other.numberPosts;

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

		progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.VISIBLE);

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setColorSchemeResources(R.color.logo_red, 		R.color.logo_green, R.color.logo_brown, 		R.color.logo_beige);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflatere.inflate(R.layout.footer3, list, false);
		footer4 = (ViewGroup)inflatere.inflate(R.layout.message_footer, list, false);
		CustomTextView nomore = (CustomTextView)footer4.findViewById(R.id.message);
		nomore.setText(getString(R.string.nomore));
		footer5 = (ViewGroup)inflatere.inflate(R.layout.message_footer, list, false);
		CustomTextView loadmore = (CustomTextView)footer5.findViewById(R.id.message);
		loadmore.setText(getString(R.string.loadmore));
		footer5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Other.isConnected(getActivity())) {
					Space(footer3, 0);
					ismore = true;
					getPosts(false, false);
				} else {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});

		list = (ObservableRecyclerView)view.findViewById(R.id.list);
		CardAdapter adapter = new CardAdapter(getActivity(), postsarray, toRightPanel);
		AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
		ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
		hv = new HeaderViewRecyclerAdapter(scaleAdapter);
		list.setItemAnimator(new FadeInUpAnimator());
		list.getItemAnimator().setAddDuration(300);
		list.setAdapter(hv);
		hv.addFooterView(footer3, getActivity(), 0);
		actualfooter = footer3;
		anddp = 0;

		list.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == hv.getItemCount()) {
					if (lastMore >= Other.numberPosts) {
						if (!block) {
							if (stored != null) {
								ismore = true;
								block = true;
								makeList(stored, false, false);
								getPosts(false, true);
							} else if (Other.isConnected(getActivity())) {
								ismore = true;
								block = true;
								getPosts(false, false);
							} else {
								Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
								toast.show();
								Space(footer5, 50);
								block = true;
							}
						}
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if(!istablet) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					firstVisibleItem = lm.findFirstVisibleItemPosition();
					lastVisibleItem = lm.findLastVisibleItemPosition();
				} else {
					int[]firstVisibleItems = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(null);
					firstVisibleItem = Math.min(firstVisibleItems[0], firstVisibleItems[1]);
					int[]lastVisibleItems = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
					lastVisibleItem = Math.max(lastVisibleItems[0], lastVisibleItems[1]);
				}
				} else {
                    if(toGrid) {
                        int[]firstVisibleItems = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(null);
                        firstVisibleItem = Math.min(firstVisibleItems[0], firstVisibleItems[1]);
                        int[]lastVisibleItems = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
                        lastVisibleItem = Math.max(lastVisibleItems[0], lastVisibleItems[1]);
                    } else {
                        firstVisibleItem = lm.findFirstVisibleItemPosition();
                        lastVisibleItem = lm.findLastVisibleItemPosition();
                    }
				}
			}

		});

		lm = new LinearLayoutManager(getActivity());
		mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

		list.setHasFixedSize(true);

		setLayoutManager(getActivity().getResources().getConfiguration());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLayoutManager(newConfig);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Space(actualfooter, anddp);
			}
		}, 100);
	}

	private void setLayoutManager(Configuration configuration) {
		if(!istablet) {
			if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
				list.setLayoutManager(lm);
			} else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				list.setLayoutManager(mStaggeredGridLayoutManager);
			}
			list.scrollToPosition(firstVisibleItem);
		} else {
            if(toGrid) {
                list.setLayoutManager(mStaggeredGridLayoutManager);
            } else {
                list.setLayoutManager(lm);
            }
		}
	}

	public void setError() {
		mainContent.setVisibility(View.GONE);
		fragment = (RelativeLayout)view.findViewById(R.id.fragment);
		LayoutInflater errorinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		error = errorinflater.inflate(R.layout.error, null);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		error.setLayoutParams(vp);
		error.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Other.isConnected(getActivity())) {
					fragment.removeView(error);
					mainContent.setVisibility(View.VISIBLE);
					getPosts(false, false);
				} else {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});
		fragment.addView(error);
	}

	public void Space(ViewGroup v2, int dp) {
		hv.removeFooterView(0);
		hv.addFooterView(v2, getActivity(), dp);
		actualfooter = v2;
		anddp = dp;
	}

	public void getPosts(final boolean fromUpdate, final boolean toStore) {}

	public void makeList(JsonObject json, boolean fromOff, boolean fromUpdate) {}
}
