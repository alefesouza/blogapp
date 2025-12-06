package net.aloogle.acasadocogumelo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.ads.*;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import net.aloogle.acasadocogumelo.R;
import net.aloogle.acasadocogumelo.adapter.CardAdapter;
import net.aloogle.acasadocogumelo.lib.JSONParser;

@SuppressLint("InflateParams")
public class CategoryFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	View view;
	ListView list;
	ArrayList <String> idarray = new ArrayList <String> ();
	ArrayList <String> tituloarray = new ArrayList <String> ();
	ArrayList <String> descricaoarray = new ArrayList <String> ();
	ArrayList <String> imagemarray = new ArrayList <String> ();
	ArrayList <String> urlarray = new ArrayList <String> ();
	int more;
	boolean ismore, block, isfirst, passed, nomore;
	String label, lastToken, title, lastUrl;
	ViewGroup footer3, footer4, footer5;
	ProgressBarCircularIndeterminate progressBar;

	private SwipeRefreshLayout mSwipeLayout;
	private AdView adView;

	private static String url;
	private static final String TAG_NEWS = "noticias";
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "titulo";
	private static final String TAG_DESCRICAO = "descricao";
	private static final String TAG_IMAGEM = "imagem";
	private static final String TAG_URL = "url";

	JSONArray noticias = null;

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
		view = inflater.inflate(R.layout.fragment_main, container, false);

		url = getArguments().getString("url");

		label = getArguments().getString("label");
		lastToken = "aloogle";
		lastUrl = url;

		list = (ListView)view.findViewById(R.id.list);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflatere.inflate(R.layout.footer3, list, false);
		footer4 = (ViewGroup)inflatere.inflate(R.layout.no_more, list, false);
		footer5 = (ViewGroup)inflatere.inflate(R.layout.load_more, list, false);
		list.addFooterView(footer3, null, false);

		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.logo_red,
			R.color.logo_green, R.color.logo_brown,
			R.color.logo_beige);

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

		if (getArguments().getBoolean("fromtag")) {
			net.aloogle.acasadocogumelo.activity.FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), getActivity().getIntent().getStringExtra("label"));
			((ActionBarActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);

			adView = new AdView(getActivity());
			adView.setAdUnitId("")
			adView.setAdSize(AdSize.BANNER);

			LinearLayout layout = (LinearLayout)view.findViewById(R.id.adLayout);

			layout.addView(adView);

			AdRequest adRequest = new AdRequest.Builder().build();

			adView.loadAd(adRequest);
			layout.setVisibility(View.VISIBLE);
		}

		ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
			new JSONParse().execute();
		} else {
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
					ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
					if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
						fragment.removeView(error);
						mainContent.setVisibility(View.VISIBLE);
						new JSONParse().execute();
					} else {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
					}
				}
			});
			fragment.addView(error);
		}
		return view;
	}

	private class JSONParse extends AsyncTask <String, String, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isfirst) {
				progressBar = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
				progressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected JSONObject doInBackground(String...args) {
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl(url.replace(" ", "%20"));
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			if (isfirst) {
				progressBar.setVisibility(View.GONE);
			}
			mSwipeLayout.setRefreshing(false);
			try {
			try {
				if (ismore) {
					if (passed == false) {
						more = more + noticias.length();
					}
				}
				block = false;
				passed = false;
				noticias = json.getJSONArray(TAG_NEWS);

				lastToken = json.getString("token");

				if (lastToken.equals("")) {
					list.removeFooterView(footer3);
					list.addFooterView(footer4, null, false);
					nomore = true;
				}
				
				for (int i = 0; i < noticias.length(); i++) {
					JSONObject c = noticias.getJSONObject(i);

					String id = c.getString(TAG_ID);
					String titulo = c.getString(TAG_TITULO);
					String descricao = c.getString(TAG_DESCRICAO);
					String imagem = c.getString(TAG_IMAGEM);
					String url = c.getString(TAG_URL);

					idarray.add(id);
					tituloarray.add(titulo);
					descricaoarray.add(descricao);
					imagemarray.add(imagem);
					urlarray.add(url);
				}

				SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new CardAdapter(getActivity(), tituloarray, descricaoarray, imagemarray, idarray, urlarray));
				swingBottomInAnimationAdapter.setAbsListView(list);

				assert swingBottomInAnimationAdapter.getViewAnimator() != null;
				swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

				list.setAdapter(swingBottomInAnimationAdapter);
				list.setOnScrollListener(CategoryFragment.this);
				list.setSelection(more);

				lastUrl = url;

				url = "http://apps.aloogle.net/blogapp/acasadocogumelo/app/json/main.php?label=" + label + "&token=" + json.getString("token");

				isfirst = false;
				list.setVisibility(View.VISIBLE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
		int visibleItemCount, int totalItemCount) {
		if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
			if (lastToken.equals("")) {}
			else {
				if (block == false) {
					final ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
					if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
						ismore = true;
						new JSONParse().execute();
						block = true;
					} else {
						Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
						toast.show();
						list.removeFooterView(footer3);
						footer5.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
									list.removeFooterView(footer5);
									list.addFooterView(footer3);
									ismore = true;
									new JSONParse().execute();
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

		if (list.getChildCount() > 0 && list.getChildAt(0).getTop() == 0 && list.getFirstVisiblePosition() == 0) {
			mSwipeLayout.setEnabled(true);
		} else {
			mSwipeLayout.setEnabled(false);
		}
	}

	public void onRefresh() {
		ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
			idarray.clear();
			tituloarray.clear();
			descricaoarray.clear();
			imagemarray.clear();
			urlarray.clear();
			url = getArguments().getString("url");
			more = 0;
			ismore = false;
			block = true;
			if (nomore) {
				list.removeFooterView(footer4);
				list.addFooterView(footer3, null, false);
			}
			new JSONParse().execute();
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	@Override
	public void onPause() {
		if (getArguments().getBoolean("fromtag")) {
			adView.pause();
		}
		super.onPause();
	}

	public void onResume() {
		if (getArguments().getBoolean("fromtag")) {
			adView.resume();
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (getArguments().getBoolean("fromtag")) {
			adView.destroy();
		}
		super.onDestroy();
	}
}
