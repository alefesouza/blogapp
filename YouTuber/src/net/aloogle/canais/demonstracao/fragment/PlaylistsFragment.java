package net.aloogle.canais.demonstracao.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.activity.MainActivity;
import net.aloogle.canais.demonstracao.adapter.CardAdapter;
import net.aloogle.canais.demonstracao.*;
import android.widget.Button;
import net.aloogle.canais.demonstracao.*;
import net.aloogle.canais.demonstracao.other.*;
import net.aloogle.canais.demonstracao.adapter.*;

@SuppressLint("InflateParams")
public class PlaylistsFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	
	Activity activity;
	View view;
	ObservableListView list;
	ArrayList <Playlists> playlistsarray = new ArrayList <Playlists>();
	int more;
	boolean ismore, block, isfirst, passed, nomore, fromtag, seted;
	String title, lastUrl, firstToken, lastToken;
	ViewGroup footer3, footer4, footer5;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	SharedPreferences preferences;

	private SwipeRefreshLayout mSwipeLayout;

	private static String url, firstUrl;
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "titulo";

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
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		firstToken = getActivity().getIntent().getStringExtra("token");
		firstUrl = "http://apps.aloogle.net/blogapp/youtuber/json/playlists.php?id=" + getString(R.string.channelid) + "&key=" + getString(R.string.developerkey);
		
		url = firstUrl + "&token=" + firstToken;

		lastUrl = url;
		
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Playlists");

		list = (ObservableListView)view.findViewById(R.id.scroll);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflatere.inflate(R.layout.footer3, list, false);
		footer4 = (ViewGroup)inflatere.inflate(R.layout.no_more, list, false);
		footer5 = (ViewGroup)inflatere.inflate(R.layout.load_more, list, false);
		list.addFooterView(footer3, null, false);

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent,
											 R.color.colorAccent, R.color.colorAccent,
											 R.color.colorAccent);

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

		if (fromtag) {
			((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getActivity().getIntent().getStringExtra("label"));
		}

		if (Other.isConnected(getActivity())) {
			getCategs();
		} else {
			setError();
		}
		return view;
	}

	public void getCategs() {
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
					JsonArray playlists = json.get("playlists").getAsJsonObject().get("playlists").getAsJsonArray();
					if (ismore) {
						if (passed == false) {
							more = more + playlists.size();
						}
					}
					block = false;
					passed = false;
					
					lastToken = json.get("token").getAsString();

					if(Build.VERSION.SDK_INT > 10) {
					if (lastToken.equals("")) {
						Space(footer3, footer4);
						nomore = true;
					}
					}

					for (int i = 0; i < playlists.size(); i++) {
						JsonObject c = playlists.get(i).getAsJsonObject();

						String id = c.get(TAG_ID).getAsString();
						String titulo = c.get(TAG_TITULO).getAsString();

						playlistsarray.add(new Playlists(id, titulo));
					}

					if(!seted) {
						swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new ListAdapter(getActivity(), playlistsarray));
						swingBottomInAnimationAdapter.setAbsListView(list);

						assert swingBottomInAnimationAdapter.getViewAnimator() != null;
						swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

						list.setAdapter(swingBottomInAnimationAdapter);
						list.setOnScrollListener(PlaylistsFragment.this);
						seted = true;
					} else {
						swingBottomInAnimationAdapter.notifyDataSetChanged(true);
					}

					lastUrl = url;
					
					url = firstUrl + "&token=" + lastToken;
					
					isfirst = false;
					list.setVisibility(View.VISIBLE);

				}});}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
			if (!lastToken.equals("")) {
				if (!block) {
					if (Other.isConnected(getActivity())) {
						ismore = true;
						getCategs();
						block = true;
					} else {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
						if (!fromtag) {
							list.removeFooterView(footer3);
						}
						footer5.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									if (Other.isConnected(getActivity())) {
										Space(footer5, footer3);
										ismore = true;

										getCategs();
									} else {
										Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
										toast.show();
									}
								}
							});
						if (!fromtag) {
							list.addFooterView(footer5);
						}
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

	public void setError() {
	}
	
	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
			list.setVisibility(View.GONE);
			playlistsarray.clear();
			url = firstUrl + "&token=" + firstToken;
			more = 0;
			lastToken = "aloogle";
			ismore = false;
			block = true;
			if (nomore) {
				Space(footer4, footer3);
			}

			getCategs();
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	public void Space(ViewGroup v1, ViewGroup v2) {
		list.removeFooterView(v1);
		list.addFooterView(v2, null, false);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}
}
