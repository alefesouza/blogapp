package net.aloogle.canais.demonstracao.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.adapter.CardAdapter;
import net.aloogle.canais.demonstracao.other.Other;
import android.widget.*;
import android.graphics.*;
import com.nineoldandroids.view.*;
import com.github.ksoichiro.android.observablescrollview.*;
import android.content.res.*;
import android.util.*;
import com.google.gson.*;
import net.aloogle.canais.demonstracao.other.*;
import net.aloogle.canais.demonstracao.activity.*;
import android.support.v7.app.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;

@SuppressLint("InflateParams")
public class MainFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	View view;
	public static ObservableRecyclerView list;
	ArrayList <Videos> videosarray = new ArrayList <Videos>();
	int more, mLastFirstVisibleItem;
	boolean ismore, block, isfirst, passed, nomore, lastisfromoff, seted;
	String title, lastToken;
	ViewGroup footer3, footer4, footer5;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	private SwipeRefreshLayout mSwipeLayout;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	SharedPreferences preferences;
	Editor editor;

	private static String url, firstUrl;
	private static final String TAG_VIDEOS = "videos";
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "titulo";
	private static final String TAG_LIKES = "likes";
	private static final String TAG_VIEWS = "visualizacoes";

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
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = preferences.edit();
		view = inflater.inflate(R.layout.fragment_main, container, false);

		firstUrl = "http://apps.aloogle.net/blogapp/youtuber/json/videos.php?id=" + getString(R.string.homeid) + "&key=" + getString(R.string.developerkey);
		
		url = firstUrl;
		lastToken = "aloogle";

        list = (ObservableRecyclerView)view.findViewById(R.id.list);
        list.setScrollViewCallbacks((ObservableScrollViewCallbacks)getActivity());
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setHasFixedSize(false);
		
		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflatere.inflate(R.layout.footer3, list, false);
		footer4 = (ViewGroup)inflatere.inflate(R.layout.no_more, list, false);
		footer5 = (ViewGroup)inflatere.inflate(R.layout.load_more, list, false);
		
		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setProgressViewOffset(false, 0, MainActivity.getActionBarSize(((AppCompatActivity)getActivity())) + MainActivity.getActionBarSize(((AppCompatActivity)getActivity())));
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent,
											 R.color.colorAccent, R.color.colorAccent,
											 R.color.colorAccent);
											 
			if (Build.VERSION.SDK_INT >= 21) {
				progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
				progressBarCompat.setVisibility(View.VISIBLE);
			}
			
		lastisfromoff = false;
		
		final boolean hasHome = preferences.contains("homeJson");
		if(hasHome) {
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject)parser.parse(preferences.getString("homeJson", ""));
			makeList(json, true, false);
			lastisfromoff = true;
			getActivity().findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
		}
		
		if (Other.isConnected(getActivity())) {
			getVideos(hasHome);
			if (Build.VERSION.SDK_INT == 10) {
				new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							getVideos(hasHome);
						}
					}, 2000);
			}
		} else {
			if(!preferences.contains("homeJson")) {
				setError();
			}
		}

		return view;
	}

	public void getVideos(final boolean fromUpdate) {
		Ion.with(this)
			.load(url)
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, final JsonObject json) {
					if(e != null) {
						if(isfirst && !preferences.contains("homeJson")) {
							setError();
						}
						Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
						toast.show();
						return;
					}

					if (isfirst) {
						isfirst = false;
					}
					
						if(url.equals(firstUrl)) {
							if(!preferences.getString("homeJson", "").equals(json.toString())) {
								Snackbar
									.make(getActivity().findViewById(R.id.coordinatorLayout), "Há novos vídeos!", Snackbar.LENGTH_LONG)
									.setAction("Atualizar", new OnClickListener() {
										@Override
										public void onClick(View p1) {
											makeList(json, false, fromUpdate);
										}
									})
									.show();
								editor.putString("homeJson",json.toString());
								editor.commit();
								update(false);
							}
						} else {
							makeList(json, false, fromUpdate);
						}
					
					mSwipeLayout.setRefreshing(false);
					getActivity().findViewById(R.id.progressBar2).setVisibility(View.GONE);
				}});}

	public void makeList(JsonObject json, boolean fromOff, boolean fromUpdate) {
		if(fromUpdate) {
			videosarray.clear();
		}
		JsonArray videos = json.get(TAG_VIDEOS).getAsJsonArray();
		if (ismore) {
			if (!passed) {
				more = more + videos.size();
			}
		}
		block = false;
		passed = false;

		if(!fromOff) {
			lastToken = json.get("token").getAsString();
		}

		if (Build.VERSION.SDK_INT > 10) {
			if (lastToken.equals("")) {
				nomore = true;
			}
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
			seted = true;
		} else {
			swingBottomInAnimationAdapter.notifyDataSetChanged(true);
		}
		
		if(!fromOff) {
		url = firstUrl + "&token=" + lastToken;
		}
		if (Build.VERSION.SDK_INT >= 21) {
			progressBar.setVisibility(View.GONE);
		} else {
			progressBarCompat.setVisibility(View.GONE);
		}
		list.setVisibility(View.VISIBLE);
	}

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
						getVideos(false);
					} else {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
					}
				}
			});
		fragment.addView(error);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				mSwipeLayout.setRefreshing(true);
				update(false);
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
			if (!lastToken.equals("")) {
				if (!block) {
					if (Other.isConnected(getActivity())) {
						ismore = true;
						getVideos(false);
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
										getVideos(false);
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
	
	public void update(boolean toLimpar) {
		if (Other.isConnected(getActivity())) {
			url = firstUrl;
			more = 0;
			ismore = false;
			block = true;
			isfirst = true;
			lastToken = "aloogle";
			if (nomore) {
				list.removeFooterView(footer4);
				list.addFooterView(footer3, null, false);
			}
			if(!toLimpar) {
				getVideos(true);
			}
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	public void onRefresh() {
		update(false);
	}
}
