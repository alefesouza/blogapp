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
import com.google.android.youtube.player.YouTubeIntents;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.activity.MainActivity;
import net.aloogle.canais.demonstracao.adapter.CardAdapter;
import net.aloogle.canais.demonstracao.other.Other;
import net.aloogle.canais.demonstracao.other.Videos;
import android.widget.*;
import android.view.*;
import android.support.design.widget.*;

@SuppressLint("InflateParams")
public class PlaylistFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, ObservableScrollViewCallbacks {
	
	Activity activity;
	View view;
	ObservableListView list;
	ArrayList <Videos> videosarray = new ArrayList <Videos>();
	int more;
	boolean ismore, block, isfirst, passed, nomore, fromtag, tofrag, seted;
	String id, title, lastUrl, lastToken;
	ViewGroup footer3, footer4, footer5;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	SharedPreferences preferences;

	private SwipeRefreshLayout mSwipeLayout;

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
		tofrag = getArguments().getBoolean("tofrag", false);
		if(tofrag) {
			setHasOptionsMenu(true);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_category, container, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		id = getArguments().getString("id");
		firstUrl = "http://apps.aloogle.net/blogapp/youtuber/json/videos.php?id=" + id + "&key=" + getString(R.string.developerkey);
		
		if (savedInstanceState != null) {
			url = savedInstanceState.getString("url");
			lastToken = savedInstanceState.getString("token");
		} else {
			url = firstUrl;
			lastToken = "aloogle";
		}

		lastUrl = url;

		list = (ObservableListView)view.findViewById(R.id.scroll);
		list.setScrollViewCallbacks(this);

		if (Build.VERSION.SDK_INT > 10) {
			((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getArguments().getString("titulo"));
			if(!tofrag) {
			MainActivity.mDrawerList.setItemChecked(getArguments().getInt("pos"), true);
			MainActivity.pos = getArguments().getInt("pos");
			}
		}
			
		if(!tofrag) {
		MainActivity.fabopen.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					Intent intent = YouTubeIntents.createPlayPlaylistIntent(getActivity(), id);
					startActivity(intent);
				}
			});

		MainActivity.fabopen.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View p1) {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.openplaylist), Toast.LENGTH_SHORT);
					toast.show();
					return false;
				}
			});
		}
		
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
			getVideos();
		} else {
			setError();
		}
		return view;
	}

	public void getVideos() {
		if (isfirst) {
			if (Build.VERSION.SDK_INT >= 21) {
				progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
				progressBarCompat.setVisibility(View.VISIBLE);
			}}
		Ion.with(this)
			.load(url.replace(" ", "%20"))
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
					JsonArray videos = json.get(TAG_VIDEOS).getAsJsonArray();
					if (ismore) {
						if (passed == false) {
							more = more + videos.size();
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

					for (int i = 0; i < videos.size(); i++) {
						JsonObject c = videos.get(i).getAsJsonObject();

						String id = c.get(TAG_ID).getAsString();
						String titulo = c.get(TAG_TITULO).getAsString();
						String likes = c.get(TAG_LIKES).getAsString();
						String visualizacoes = c.get(TAG_VIEWS).getAsString();

						videosarray.add(new Videos(id, titulo, likes, visualizacoes));
					}

					if(!seted) {
						swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new CardAdapter(getActivity(), videosarray));
						swingBottomInAnimationAdapter.setAbsListView(list);

						assert swingBottomInAnimationAdapter.getViewAnimator() != null;
						swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

						list.setAdapter(swingBottomInAnimationAdapter);
						list.setOnScrollListener(PlaylistFragment.this);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if(tofrag) {
		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.frag_menu, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(tofrag) {
		switch (item.getItemId()) {
			case R.id.menu_open:
				Intent intent = YouTubeIntents.createPlayPlaylistIntent(getActivity(), id);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		}
		return super.onOptionsItemSelected(item);
	}
	
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
						getVideos();
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

										getVideos();
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
			getVideos();
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
			videosarray.clear();
			url = firstUrl;
			more = 0;
			lastToken = "aloogle";
			ismore = false;
			block = true;
			if (nomore) {
				Space(footer4, footer3);
			}
			getVideos();
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
	}

	@Override
	public void onDownMotionEvent() {
	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		if(scrollState == ScrollState.DOWN) {
			if(!tofrag) {
			if(!MainActivity.fabvisible) {
				MainActivity.fabvisible = true;
				MainActivity.showFab(MainActivity.fabopen, MainActivity.fabvisible);
			}}
		} else if(scrollState == ScrollState.UP) {
			if(!tofrag) {
			if(MainActivity.fabvisible) {
				MainActivity.fabvisible = false;
				MainActivity.showFab(MainActivity.fabopen, MainActivity.fabvisible);
			}}
		}
	}
	
	public void Space(ViewGroup v1, ViewGroup v2) {
		list.removeFooterView(v1);
		list.addFooterView(v2, null, false);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("url", url);
		savedInstanceState.putString("token", lastToken);
		super.onSaveInstanceState(savedInstanceState);
	}
}
