package br.com.vidadesuporte.fragment;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.content.res.*;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.LinearLayout.*;
import android.widget.*;
import br.com.vidadesuporte.*;
import br.com.vidadesuporte.lib.*;
import br.com.vidadesuporte.other.*;
import com.gc.materialdesign.views.*;
import com.github.ksoichiro.android.observablescrollview.*;
import com.google.gson.*;
import java.util.*;
import jp.wasabeef.recyclerview.animators.*;

import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;
import br.com.vidadesuporte.R;
import android.os.*;
import android.preference.*;
import jp.wasabeef.recyclerview.animators.adapters.*;
import br.com.vidadesuporte.adapter.*;

public class BaseFragment extends Fragment {
	Activity activity;
	View view;
	List <Posts> postsarray = new ArrayList <Posts>();
	int more, mLastFirstVisibleItem, page, lastMore, anddp;
	boolean ismore, block, isfirst, passed, nomore, lastisfromoff;
	ViewGroup footer3, footer4, footer5, actualfooter;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	SwipeRefreshLayout mSwipeLayout;

	ObservableRecyclerView list;
	HeaderViewRecyclerAdapter hv;
	LinearLayoutManager lm;
	StaggeredGridLayoutManager mStaggeredGridLayoutManager;
	int firstVisibleItem, lastVisibleItem;

	SharedPreferences preferences;
	Editor editor;
	JsonObject stored;

	String url, firstUrl;
	String TAG_POSTS = "posts";
	String TAG_ID = "id";
	String TAG_TITULO = "titulo";
	String TAG_DESCRICAO = "descricao";
	String TAG_IMAGEM = "imagem";
	String TAG_URL = "url";
	String TAG_COMENTARIOS = "comentarios";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	public void configCreate() {
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = preferences.edit();

		url = firstUrl;
		page = 1;

		lastMore = Other.numberPosts;

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;
		
		if(Build.VERSION.SDK_INT >= 21) {
			progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
			progressBarCompat.setVisibility(View.VISIBLE);
		}
		
		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent,
											 R.color.logo_red, R.color.logo_black,
											 R.color.logo_red);
		
		list = (ObservableRecyclerView)view.findViewById(R.id.list);
		CardAdapter adapter = new CardAdapter(getActivity(), postsarray);
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
					if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == hv.getItemCount()) {
						if(lastMore >= Other.numberPosts) {
							if(!block) {
								if(Other.isConnected(getActivity())) {
									ismore = true;
									block = true;
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
					if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						firstVisibleItem = lm.findFirstVisibleItemPosition();
						lastVisibleItem = lm.findLastVisibleItemPosition();
					} else {
						int[] firstVisibleItems = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(null);
						firstVisibleItem = Math.min(firstVisibleItems[0], firstVisibleItems[1]);
						int[] lastVisibleItems = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
						lastVisibleItem = Math.max(lastVisibleItems[0], lastVisibleItems[1]);
					}
				}

			});

		lm = new LinearLayoutManager(getActivity());
		mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

		list.setHasFixedSize(true);
        list.setItemAnimator(new FadeInUpAnimator());
		list.getItemAnimator().setAddDuration(300);

		setLayoutManager(getActivity().getResources().getConfiguration());

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
					if(Other.isConnected(getActivity())) {
						Space(footer3, 0);
						ismore = true;
					} else {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
					}
				}
			});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLayoutManager(newConfig);
		Space(actualfooter, anddp);
	}

	private void setLayoutManager(Configuration configuration) {
		if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			list.setLayoutManager(lm);
		} else if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			list.setLayoutManager(mStaggeredGridLayoutManager);
		}
		list.scrollToPosition(firstVisibleItem);
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
					if(Other.isConnected(getActivity())) {
						fragment.removeView(error);
						mainContent.setVisibility(View.VISIBLE);
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
}
