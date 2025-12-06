package com.acasadocogumelo.cogumelonoticias.fragment;

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
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.acasadocogumelo.cogumelonoticias.R;
import com.acasadocogumelo.cogumelonoticias.adapter.CardAdapter;
import com.acasadocogumelo.cogumelonoticias.activity.MainActivity;

public class FavoritesFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	View view;
	ObservableListView list;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	SharedPreferences preferences;
	ArrayList <String> idarray = new ArrayList <String> ();
	ArrayList <String> tituloarray = new ArrayList <String> ();
	ArrayList <String> descricaoarray = new ArrayList <String> ();
	ArrayList <String> imagemarray = new ArrayList <String> ();
	ArrayList <String> urlarray = new ArrayList <String> ();
	String favstring;

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
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		view = inflater.inflate(R.layout.fragment_category, container, false);
		favstring = preferences.getString("favoritesPosts", "");

		if (Build.VERSION.SDK_INT > 10) {
			MainActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Favoritos");
			MainActivity.mDrawerList.setItemChecked(2, true);
			MainActivity.pos = 2;
		}

		if (Build.VERSION.SDK_INT >= 21) {
			progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
			progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFFD92525, 0xFFD92525));
			progressBar.setVisibility(View.GONE);
		} else {
			progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
			progressBarCompat.setVisibility(View.GONE);
		}

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.logo_red,
			R.color.logo_green, R.color.logo_brown,
			R.color.logo_beige);

		Go();
		return view;
	}

	public void Go() {
		String[]favorites = favstring.split("\\$\\%\\#");
		for (int i = 0; i < favorites.length; i++) {
			if (!favorites[i].equals("")) {
				try {
					JSONObject json = new JSONObject(favorites[i]);
					idarray.add(json.getString("id"));
					tituloarray.add(json.getString("titulo"));
					descricaoarray.add(json.getString("descricao"));
					imagemarray.add(json.getString("imagem"));
					urlarray.add(json.getString("url"));
				} catch (JSONException e) {}
			}
		}

		list = (ObservableListView)view.findViewById(R.id.scroll);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		ViewGroup space = (ViewGroup)inflatere.inflate(R.layout.space, list, false);
		list.addFooterView(space, null, false);

		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new CardAdapter(getActivity(), tituloarray, descricaoarray, imagemarray, idarray, urlarray));
		swingBottomInAnimationAdapter.setAbsListView(list);

		assert swingBottomInAnimationAdapter.getViewAnimator() != null;
		swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

		list.setAdapter(swingBottomInAnimationAdapter);
		list.setOnScrollListener(FavoritesFragment.this);
	}

	public void ClearAll() {
		idarray.clear();
		tituloarray.clear();
		descricaoarray.clear();
		imagemarray.clear();
		urlarray.clear();
	}

	public void onRefresh() {
		ClearAll();
		Go();
		mSwipeLayout.setRefreshing(false);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
		int visibleItemCount, int totalItemCount) {
		if (list.getChildCount() > 0 && list.getChildAt(0).getTop() == 0 && list.getFirstVisiblePosition() == 0) {
			mSwipeLayout.setEnabled(true);
		} else {
			mSwipeLayout.setEnabled(false);
		}
	}

	public void onResume() {
		if (!preferences.getString("favoritesPosts", "").equals(favstring)) {
			favstring = preferences.getString("favoritesPosts", "");
			ClearAll();
			Go();
		}
		super.onResume();
	}
}
