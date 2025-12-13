package br.com.vidadesuporte.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.adapter.CardAdapter;
import br.com.vidadesuporte.activity.MainActivity;
import br.com.vidadesuporte.other.*;
import android.widget.*;
import android.widget.LinearLayout.*;
import android.content.*;
import android.webkit.*;
import ir.noghteh.*;
import android.util.*;
import android.graphics.Paint.Align;
import android.graphics.*;
import com.github.ksoichiro.android.observablescrollview.*;
import br.com.vidadesuporte.lib.*;
import android.support.v7.widget.*;
import jp.wasabeef.recyclerview.animators.*;
import android.content.res.*;
import jp.wasabeef.recyclerview.animators.adapters.*;
import br.com.vidadesuporte.database.helper.*;
import br.com.vidadesuporte.database.model.*;
import java.util.*;

public class FavoritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	View view, card;
	ObservableRecyclerView list;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	ArrayList <Posts> postsarray = new ArrayList <Posts>();
	boolean fromno, seted;
	RelativeLayout fragment, mainContent;

	HeaderViewRecyclerAdapter hv;
	private LinearLayoutManager lm;
	private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_category, container, false);

		if(Build.VERSION.SDK_INT > 10) {
			MainActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Favoritos");
			MainActivity.mDrawerList.setItemChecked(2, true);
			MainActivity.pos = 2;
		}
		
		list = (ObservableRecyclerView)view.findViewById(R.id.list);
		CardAdapter adapter = new CardAdapter(getActivity(), postsarray);
		AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
		ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
		hv = new HeaderViewRecyclerAdapter(scaleAdapter);
		list.setAdapter(hv);

		list.setOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
					super.onScrollStateChanged(recyclerView, newState);}

				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					super.onScrolled(recyclerView, dx, dy);
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
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
				}
			});
			
		if(Build.VERSION.SDK_INT >= 21) {
			progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
			progressBar.setVisibility(View.GONE);
		} else {
			progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
			progressBarCompat.setVisibility(View.GONE);
		}

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

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent,
											 R.color.colorAccent, R.color.colorAccent,
											 R.color.colorAccent);

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
		List<Favorites> favorites = db.getAllFavorites();
		if(favorites.size() == 0) {
			mainContent.setVisibility(View.GONE);
			fragment.addView(card);
			fromno = true;
		} else {
			if(fromno) {
				mainContent.setVisibility(View.VISIBLE);
				fragment.removeView(card);
				fromno = false;
			}

			for (Favorites favorite : favorites) {
				try {
					JSONObject json = new JSONObject(favorite.getJson());
					postsarray.add(new Posts(json.getString("id"), json.getString("titulo"), json.getString("imagem"), json.getString("descricao"), json.getString("url"), "", json.getString("categoriaicon"), ""));
				} catch(JSONException e) {}
			
		}
		
		hv.notifyDataSetChanged();
		
		list.setVisibility(View.VISIBLE);
		}
	}

	public void ClearAll() {
		if(!fromno) {
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
