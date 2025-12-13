package br.com.vidadesuporte.fragment;

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
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.activity.MainActivity;
import br.com.vidadesuporte.adapter.CardAdapter;
import br.com.vidadesuporte.other.*;
import android.widget.Button;
import br.com.vidadesuporte.adapter.*;
import br.com.vidadesuporte.activity.*;

@SuppressLint("InflateParams")
public class CategoriasFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	
	Activity activity;
	View view;
	ObservableListView list;
	ArrayList <Categorias> categoriasarray = new ArrayList <Categorias>();
	int more, page, firstPage, lastMore;
	boolean ismore, block, isfirst, passed, nomore, fromtag, seted;
	String title, lastUrl;
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

		firstPage = getActivity().getIntent().getIntExtra("page", 2);
		firstUrl = "http://apps.aloogle.net/blogapp/wordpress/json/categorias.php?id=" + getString(R.string.blogid);
		
		url = firstUrl + "&page=" + String.valueOf(firstPage);

		lastUrl = url;
		
		FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Categorias");

		list = (ObservableListView)view.findViewById(R.id.scroll);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflatere.inflate(R.layout.footer3, list, false);
		footer4 = (ViewGroup)inflatere.inflate(R.layout.no_more, list, false);
		footer5 = (ViewGroup)inflatere.inflate(R.layout.load_more, list, false);
		list.addFooterView(footer3, null, false);

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.colorAccent,
											 R.color.logo_red, R.color.logo_black,
											 R.color.logo_red);

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

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
						e.printStackTrace();
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
					JsonArray categorias = json.get("categorias").getAsJsonObject().get("categorias").getAsJsonArray();
					if (ismore) {
						if (passed == false) {
							more = more + categorias.size();
						}
					}
					block = false;
					passed = false;
					lastMore = categorias.size();

					if(Build.VERSION.SDK_INT > 10) {
					if (lastMore != 15) {
						Space(footer3, footer4);
						nomore = true;
					}
					}

					for (int i = 0; i < categorias.size(); i++) {
						JsonObject c = categorias.get(i).getAsJsonObject();

						String id = c.get(TAG_ID).getAsString();
						String titulo = c.get(TAG_TITULO).getAsString();

						categoriasarray.add(new Categorias(id, titulo));
					}

					if(!seted) {
						swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new ListAdapter(getActivity(), categoriasarray));
						swingBottomInAnimationAdapter.setAbsListView(list);

						assert swingBottomInAnimationAdapter.getViewAnimator() != null;
						swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

						list.setAdapter(swingBottomInAnimationAdapter);
						list.setOnScrollListener(CategoriasFragment.this);
						seted = true;
					} else {
						swingBottomInAnimationAdapter.notifyDataSetChanged(true);
					}

					lastUrl = url;
					
					page ++;
					url = firstUrl + "&page=" + page;
					
					isfirst = false;
					list.setVisibility(View.VISIBLE);

				}});}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
			if (lastMore == 15) {
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
						getCategs();
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
			list.setVisibility(View.GONE);
			categoriasarray.clear();
			url = firstUrl + "&page=" + String.valueOf(firstPage);
			more = 0;
			page = firstPage;
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
}
