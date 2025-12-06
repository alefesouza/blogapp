package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.FragmentActivity;
import net.aloogle.dropandoideias.adapter.ListAdapter;
import net.aloogle.dropandoideias.lib.HeaderViewRecyclerAdapter;
import net.aloogle.dropandoideias.other.Categorias;
import net.aloogle.dropandoideias.other.CustomTextView;
import net.aloogle.dropandoideias.other.Other;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class CategoriasFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	Activity activity;
	View view;
	ObservableRecyclerView list;
	ArrayList <Categorias> categoriasarray = new ArrayList <Categorias> ();
	int more, page, firstPage, lastMore, anddp;
	boolean ismore, block, isfirst, passed, nomore, fromtag;
	String title, lastUrl;
	ViewGroup footer3, footer4, footer5, actualfooter;
	ProgressBar progressBar;

	HeaderViewRecyclerAdapter hv;
	private LinearLayoutManager lm;
	private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
	private int firstVisibleItem, lastVisibleItem;

	SharedPreferences preferences;

	private SwipeRefreshLayout mSwipeLayout;

	private static String url, firstUrl;
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "name";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_main, container, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		firstPage = getActivity().getIntent().getIntExtra("page", 2);
		firstUrl = Other.defaultUrl + "categorias.php?id=" + getString(R.string.blogid);

		url = firstUrl + "&page=" + String.valueOf(firstPage);

		lastUrl = url;

		FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Categorias");

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
					getCategs(false);
				} else {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});

		list = (ObservableRecyclerView)view.findViewById(R.id.list);
		ListAdapter adapter = new ListAdapter(getActivity(), categoriasarray);
		AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
		ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
		hv = new HeaderViewRecyclerAdapter(scaleAdapter);
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
							if (Other.isConnected(getActivity())) {
								ismore = true;
								getCategs(false);
								block = true;
							} else {
								Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
								toast.show();
								if (!fromtag) {
									Space(footer5, 50);
								}
								block = true;
							}
						}
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					firstVisibleItem = lm.findFirstVisibleItemPosition();
					lastVisibleItem = lm.findLastVisibleItemPosition();
				} else {
					int[]firstVisibleItems = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(null);
					firstVisibleItem = Math.min(firstVisibleItems[0], firstVisibleItems[1]);
					int[]lastVisibleItems = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
					lastVisibleItem = Math.max(lastVisibleItems[0], lastVisibleItems[1]);
				}
			}

		});

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

		lm = new LinearLayoutManager(getActivity());
		mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

		list.setHasFixedSize(true);
		list.setItemAnimator(new FadeInUpAnimator());
		list.getItemAnimator().setAddDuration(300);

		setLayoutManager(getActivity().getResources().getConfiguration());

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent, 		R.color.logo_red, R.color.logo_black, 		R.color.logo_red);

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

		if (Other.isConnected(getActivity())) {
			getCategs(false);
		} else {
			setError();
		}
		return view;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLayoutManager(newConfig);
	}

	private void setLayoutManager(Configuration configuration) {
		if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			list.setLayoutManager(lm);
		} else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			list.setLayoutManager(mStaggeredGridLayoutManager);
		}
		list.scrollToPosition(firstVisibleItem);
		Space(actualfooter, anddp);
	}

	public void getCategs(final boolean fromUpdate) {
		if (isfirst) {
				progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
				progressBar.setVisibility(View.VISIBLE);
		}
		Ion.with (this)
		.load(url)
		.asJsonObject()
		.setCallback(new FutureCallback < JsonObject > () {
			@Override
			public void onCompleted(Exception e, JsonObject json) {
				if (e != null) {
					if (isfirst) {
						setError();
					}
					Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
					toast.show();
					e.printStackTrace();
					return;
				}
				if (isfirst) {
						progressBar.setVisibility(View.GONE);
				}
				if (fromUpdate) {
					categoriasarray.clear();
				}
				mSwipeLayout.setRefreshing(false);
				JsonArray categorias = json.get("categories").getAsJsonObject().get("categories").getAsJsonArray();
				if (ismore) {
					if (!passed) {
						more = more + categorias.size();
					}
				}
				block = false;
				passed = false;
				lastMore = categorias.size();

				if (lastMore != 15) {
					Space(footer4, 50);
					nomore = true;
				}

				for (int i = 0; i < categorias.size(); i++) {
					JsonObject c = categorias.get(i).getAsJsonObject();

					String id = c.get(TAG_ID).getAsString();
					String titulo = c.get(TAG_TITULO).getAsString();
					String icon = c.get("icon").getAsString();

					categoriasarray.add(new Categorias(id, titulo, icon));
				}

				hv.notifyDataSetChanged();

				lastUrl = url;

				page++;
				url = firstUrl + "&page=" + page;

				isfirst = false;
				list.setVisibility(View.VISIBLE);
			}
		});
	}

	public void setError() {
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
					getCategs(false);
				} else {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});
		fragment.addView(error);
	}

	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
			url = firstUrl + "&page=" + String.valueOf(firstPage);
			more = 0;
			page = firstPage;
			ismore = false;
			block = true;
			if (nomore) {
				Space(footer3, 0);
			}
			getCategs(true);
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}
	public void Space(ViewGroup v2, int dp) {
		hv.removeFooterView(0);
		hv.addFooterView(v2, getActivity(), dp);
		actualfooter = v2;
		anddp = dp;
	}
}
