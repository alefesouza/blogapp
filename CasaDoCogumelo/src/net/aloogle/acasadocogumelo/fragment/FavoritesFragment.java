package net.aloogle.acasadocogumelo.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;
import android.widget.ListView;
import java.util.ArrayList;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import net.aloogle.acasadocogumelo.R;
import net.aloogle.acasadocogumelo.adapter.CardAdapter;

public class FavoritesFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	View view;
	ListView list;
	ProgressBarCircularIndeterminate progressBar;
	SharedPreferences preferences;
	ArrayList <String> idarray = new ArrayList <String> ();
	ArrayList <String> tituloarray = new ArrayList <String> ();
	ArrayList <String> descricaoarray = new ArrayList <String> ();
	ArrayList <String> imagemarray = new ArrayList <String> ();
	ArrayList <String> urlarray = new ArrayList <String> ();

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
		view = inflater.inflate(R.layout.fragment_main, container, false);

		progressBar = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.GONE);

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.logo_red,
			R.color.logo_green, R.color.logo_brown,
			R.color.logo_beige);

		Go();
		return view;
	}

	public void Go() {
		String[]ids = preferences.getString("favoritesPostsId", "").split("\\$\\%\\#");
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].equals("")) {}
			else {
				idarray.add(ids[i]);
			}
		}

		String[]titulos = preferences.getString("favoritesPostsTitle", "").split("\\$\\%\\#");
		for (int i = 0; i < titulos.length; i++) {
			if (titulos[i].equals("")) {}
			else {
				tituloarray.add(titulos[i]);
			}
		}

		String[]descricoes = preferences.getString("favoritesPostsDescription", "").split("\\$\\%\\#");
		for (int i = 0; i < descricoes.length; i++) {
			if (descricoes[i].equals("")) {}
			else {
				descricaoarray.add(descricoes[i]);
			}
		}

		String[]imagens = preferences.getString("favoritesPostsImage", "").split("\\$\\%\\#");
		for (int i = 0; i < imagens.length; i++) {
			if (imagens[i].equals("")) {}
			else {
				imagemarray.add(imagens[i]);
			}
		}

		String[]urls = preferences.getString("favoritesPostsUrl", "").split("\\$\\%\\#");
		for (int i = 0; i < urls.length; i++) {
			if (urls[i].equals("")) {}
			else {
				urlarray.add(urls[i]);
			}
		}

		list = (ListView)view.findViewById(R.id.list);

		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new CardAdapter(getActivity(), tituloarray, descricaoarray, imagemarray, idarray, urlarray));
		swingBottomInAnimationAdapter.setAbsListView(list);

		assert swingBottomInAnimationAdapter.getViewAnimator() != null;
		swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

		list.setAdapter(swingBottomInAnimationAdapter);
		list.setOnScrollListener(FavoritesFragment.this);
	}

	public void onRefresh() {
		idarray.clear();
		tituloarray.clear();
		descricaoarray.clear();
		imagemarray.clear();
		urlarray.clear();
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
}
