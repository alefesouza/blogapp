package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.adapter.CardAdapter;
import net.aloogle.dropandoideias.activity.MainActivity;
import net.aloogle.dropandoideias.database.helper.DatabaseHelper;
import net.aloogle.dropandoideias.database.model.Favorites;
import net.aloogle.dropandoideias.lib.HeaderViewRecyclerAdapter;
import net.aloogle.dropandoideias.other.CustomTextView;
import net.aloogle.dropandoideias.other.Other;
import net.aloogle.dropandoideias.other.Posts;

@SuppressLint("InflateParams")
public class FavoritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	View view, card;
	ObservableRecyclerView list;
	ProgressBar progressBar;
	ArrayList <Posts> postsarray = new ArrayList <Posts> ();
	boolean fromno;
	RelativeLayout fragment, mainContent;

	HeaderViewRecyclerAdapter hv;
	private LinearLayoutManager lm;
	private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
	@SuppressWarnings("unused")
	private int firstVisibleItem, lastVisibleItem;

	private SwipeRefreshLayout mSwipeLayout;

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

		MainActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Favoritos");
		MainActivity.mDrawerList.setItemChecked(2, true);
		MainActivity.pos = 2;

		list = (ObservableRecyclerView)view.findViewById(R.id.list);
		CardAdapter adapter = new CardAdapter(getActivity(), postsarray, true);
		AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
		ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
		hv = new HeaderViewRecyclerAdapter(scaleAdapter);
		list.setAdapter(hv);

		list.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
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

		lm = new LinearLayoutManager(getActivity());
		mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

		list.setHasFixedSize(true);
		list.setItemAnimator(new FadeInUpAnimator());
		list.getItemAnimator().setAddDuration(300);

		setLayoutManager(getActivity().getResources().getConfiguration());

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

		progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.GONE);

		mainContent = (RelativeLayout)view.findViewById(R.id.main_content);
		fragment = (RelativeLayout)view.findViewById(R.id.fragment);
		LayoutInflater cardinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		card = cardinflater.inflate(R.layout.card, null);
		card.findViewById(R.id.head).setVisibility(View.GONE);
		card.findViewById(R.id.imagerelative).setVisibility(View.GONE);
		card.findViewById(R.id.comentariosline).setVisibility(View.GONE);
		card.findViewById(R.id.commentsspace).setVisibility(View.GONE);
		CustomTextView warning = (CustomTextView)card.findViewById(R.id.descricao);
		warning.setText(getString(R.string.nofavorites));
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		vp.setMargins(Other.dpToPx(getActivity(), 5), 0, Other.dpToPx(getActivity(), 5), 0);
		card.setLayoutParams(vp);
		if(Build.VERSION.SDK_INT < 19) {
			card.findViewById(R.id.conteudo).setPadding(0, 0, 0, Other.dpToPx(getActivity(), 10));
		}

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent, 		R.color.colorAccent, R.color.colorAccent, 		R.color.colorAccent);

		Go();
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
	}

	public void Go() {
		DatabaseHelper db = new DatabaseHelper(getActivity());
		List <Favorites> favorites = db.getAllFavorites();
		if (favorites.size() == 0) {
			mainContent.setVisibility(View.GONE);
			fragment.addView(card);
			fromno = true;
		} else {
			if (fromno) {
				mainContent.setVisibility(View.VISIBLE);
				fragment.removeView(card);
				fromno = false;
			}

			Collections.reverse(favorites);

			for (Favorites favorite : favorites) {
				try {
					JSONObject json = new JSONObject(favorite.getJson());
					postsarray.add(new Posts(json.getString("id"), json.getString("title"), json.getString("image"), json.getString("description"), json.getString("url"), "", json.getString("categoryicon"), ""));
				} catch (JSONException e) {}

			}

			hv.notifyDataSetChanged();

			list.setVisibility(View.VISIBLE);
		}
	}

	public void ClearAll() {
		if (!fromno) {
			postsarray.clear();
			Go();
		}
	}

	public void onRefresh() {
		ClearAll();
		mSwipeLayout.setRefreshing(false);
	}

	public void onResume() {
		ClearAll();
		super.onResume();
	}
}
