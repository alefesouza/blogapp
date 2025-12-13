package com.vidadesuporte.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.vidadesuporte.R;
import com.vidadesuporte.activity.FragmentActivity;
import com.vidadesuporte.adapter.CardAdapter;
import com.vidadesuporte.lib.JSONParser;
import com.vidadesuporte.other.Other;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class SearchFragment extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	Activity activity;
	SharedPreferences preferences;
	Editor editor;
	String iconcolor;

	ObservableListView list;
	ArrayList <String> idarray = new ArrayList <String> ();
	ArrayList <String> tituloarray = new ArrayList <String> ();
	ArrayList <String> descricaoarray = new ArrayList <String> ();
	ArrayList <String> imagemarray = new ArrayList <String> ();
	ArrayList <String> urlarray = new ArrayList <String> ();
	int more;
	boolean ismore, block, isfirst, passed, nomore;
	ViewGroup footer3, footer4, footer5;
	ProgressBar progressBar;
	ProgressBarCircularIndeterminate progressBarCompat;

	private SwipeRefreshLayout mSwipeLayout;

	String url, search, lastToken, lastUrl;
	private static final String TAG_NEWS = "noticias";
	private static final String TAG_ID = "id";
	private static final String TAG_TITULO = "titulo";
	private static final String TAG_DESCRICAO = "descricao";
	private static final String TAG_IMAGEM = "imagem";
	private static final String TAG_URL = "url";

	JSONArray noticias = null;
	View view;

	ArrayList <String> categoriaarray;

	ArrayList <String> reallyarray = new ArrayList <String> ();
	private SimpleCursorAdapter mAdapter;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		iconcolor = preferences.getString("prefIconColor", "branco");
		view = inflater.inflate(R.layout.fragment_category, container, false);

		categoriaarray = getActivity().getIntent().getStringArrayListExtra("categorias");

		final String[]from = new String[]{
			"categoryName"
		};
		final int[]to = new int[]{
			R.id.text1
		};
		mAdapter = new SimpleCursorAdapter(getActivity(), 			R.layout.simple_list_item_1, 			null, 			from, 			to, 			CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		if (savedInstanceState != null) {
			lastToken = savedInstanceState.getString("token");
			lastUrl = savedInstanceState.getString("url");
			url = lastUrl;
			search = savedInstanceState.getString("search");
		} else {
			search = getActivity().getIntent().getStringExtra("query");
			lastToken = "aloogle";
			lastUrl = url;
		}

		try {
			FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + URLDecoder.decode(search, "UTF-8"));
		} catch (UnsupportedEncodingException e) {}

		list = (ObservableListView)view.findViewById(R.id.scroll);

		if (Build.VERSION.SDK_INT >= 21) {
			if (preferences.getString("prefColor", "padrao").equals("padrao")) {
				view.findViewById(R.id.dropshadow).setVisibility(View.VISIBLE);
			} else {
				view.findViewById(R.id.dropshadow).setVisibility(View.GONE);
			}
		}
		
		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.logo_red, 		R.color.logo_green, R.color.logo_brown, 		R.color.logo_beige);

		LayoutInflater inflatere = getActivity().getLayoutInflater();
		footer3 = (ViewGroup)inflater.inflate(R.layout.footer3, list, false);
		if (Build.VERSION.SDK_INT >= 21) {
			ProgressBar progress = (ProgressBar)footer3.findViewById(R.id.progressBar1);
			progress.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFFD92525, 0xFFD92525));
		}
		footer4 = (ViewGroup)inflater.inflate(R.layout.no_more, list, false);
		footer5 = (ViewGroup)inflatere.inflate(R.layout.load_more, list, false);
		list.addFooterView(footer3, null, false);

		more = 0;
		ismore = false;
		block = false;
		isfirst = true;
		passed = false;

		url = "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?search=" + search;

		if (Other.isConnected(getActivity())) {
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
					if (Other.isConnected(getActivity())) {
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

	private class JSONParse extends AsyncTask < String, String, JSONObject > {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isfirst) {
				if (Build.VERSION.SDK_INT >= 21) {
					progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
					progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFFD92525, 0xFFD92525));
					progressBar.setVisibility(View.VISIBLE);
				} else {
					progressBarCompat = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBar1);
					progressBarCompat.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		protected JSONObject doInBackground(String...args) {

			JSONParser jParser = new JSONParser();

			JSONObject json = jParser.getJSONFromUrl(url);
			return json;
		}
		@Override
		protected void onPostExecute(JSONObject json) {
			if (isfirst) {
				if (Build.VERSION.SDK_INT >= 21) {
					progressBar.setVisibility(View.GONE);
				} else {
					progressBarCompat.setVisibility(View.GONE);
				}
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

					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView <  ?  > parent, View view, 						int position, long id) {
							Intent intent = new Intent(getActivity(), FragmentActivity.class);
							intent.putExtra("fragment", 3);
							intent.putExtra("id", idarray.get(+position).toString());
							intent.putExtra("titulo", tituloarray.get(+position).toString());
							intent.putExtra("imagem", imagemarray.get(+position).toString());
							intent.putExtra("url", urlarray.get(+position).toString());
							startActivity(intent);
						}
					});
					SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new CardAdapter(getActivity(), tituloarray, descricaoarray, imagemarray, idarray, urlarray));
					swingBottomInAnimationAdapter.setAbsListView(list);

					assert swingBottomInAnimationAdapter.getViewAnimator() != null;
					swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);

					list.setAdapter(swingBottomInAnimationAdapter);
					list.setOnScrollListener(SearchFragment.this);
					list.setSelection(more);

					url = "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?search=" + search + "&token=" + json.getString("token");
					isfirst = false;
					list.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					e.printStackTrace();
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
			} catch (Exception e) {}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_search);

		SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint(getString(R.string.search));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				try {
					try {
						idarray.clear();
						tituloarray.clear();
						descricaoarray.clear();
						imagemarray.clear();
						urlarray.clear();
						list.setVisibility(View.GONE);
						more = 0;
						ismore = false;
						block = true;
						isfirst = true;
						passed = true;
						search = URLEncoder.encode(query, "UTF-8");
						if (nomore) {
							list.removeFooterView(footer4);
							list.addFooterView(footer3, null, false);
						}
						FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + query);
						url = "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?search=" + query;
						new JSONParse().execute();
					} catch (UnsupportedEncodingException e) {}
				} catch (Exception e) {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String txt) {
				try {
					reallyarray.clear();
					populateAdapter(txt);
				} catch (Exception e) {
					Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
					toast.show();
				}
				return false;
			}
		});

		SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);

		if (iconcolor.equals("branco")) {
			theTextArea.setTextColor(Color.WHITE);
		} else {
			theTextArea.setTextColor(Color.BLACK);
		}

		searchView.setSuggestionsAdapter(mAdapter);

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionClick(int position) {
				try {
					idarray.clear();
					tituloarray.clear();
					descricaoarray.clear();
					imagemarray.clear();
					urlarray.clear();
					list.setVisibility(View.GONE);
					more = 0;
					ismore = false;
					block = true;
					isfirst = true;
					passed = true;
					search = URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8");
					if (nomore) {
						list.removeFooterView(footer4);
						list.addFooterView(footer3, null, false);
					}
					FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + reallyarray.get(position).toString());
					url = "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?search=" + search;
					new JSONParse().execute();
				} catch (UnsupportedEncodingException e) {}
				return true;
			}

			@Override
			public boolean onSuggestionSelect(int position) {
				try {
					idarray.clear();
					tituloarray.clear();
					descricaoarray.clear();
					imagemarray.clear();
					urlarray.clear();
					list.setVisibility(View.GONE);
					more = 0;
					ismore = false;
					block = true;
					isfirst = true;
					passed = true;
					search = URLEncoder.encode(reallyarray.get(position).toString(), "UTF-8");
					if (nomore) {
						list.removeFooterView(footer4);
						list.addFooterView(footer3, null, false);
					}
					FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), "Busca: " + reallyarray.get(position).toString());
					url = "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?search=" + search;
					new JSONParse().execute();
				} catch (UnsupportedEncodingException e) {}
				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (iconcolor.equals("branco")) {
			menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search_white);
		} else {
			menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search_black);
		}

		menu.findItem(R.id.menu_refresh).setVisible(false);
		menu.findItem(R.id.menu_opensite).setVisible(false);

		super.onPrepareOptionsMenu(menu);
	}

	private void populateAdapter(String query) {
		final MatrixCursor c = new MatrixCursor(new String[]{
				BaseColumns._ID, 			"categoryName"
			});
		for (int i = 0; i < categoriaarray.size(); i++) {
			if (categoriaarray.get(i).toString().toLowerCase().startsWith(query.toLowerCase())) {
				reallyarray.add(categoriaarray.get(i).toString());
				c.addRow(new Object[]{
					i, 				categoriaarray.get(i).toString()
				});
			}
			mAdapter.changeCursor(c);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, 	int visibleItemCount, int totalItemCount) {

		if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 && list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {
			if (lastToken.equals("")) {}
			else {
				if (block == false) {
					if (Other.isConnected(getActivity())) {
						ismore = true;
						new JSONParse().execute();
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
		if (Other.isConnected(getActivity())) {
			list.setVisibility(View.GONE);
			idarray.clear();
			tituloarray.clear();
			descricaoarray.clear();
			imagemarray.clear();
			urlarray.clear();
			url = "http://apps.aloogle.net/blogapp/acasadocogumelo/json/main.php?search=" + search;
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

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("search", search);
		savedInstanceState.putString("token", lastToken);
		savedInstanceState.putString("url", lastUrl);
		super.onSaveInstanceState(savedInstanceState);
	}
}
