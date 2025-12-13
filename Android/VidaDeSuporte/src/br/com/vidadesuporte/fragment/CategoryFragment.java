package br.com.vidadesuporte.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.activity.MainActivity;
import br.com.vidadesuporte.adapter.CardAdapter;
import br.com.vidadesuporte.other.Other;
import br.com.vidadesuporte.activity.*;
import br.com.vidadesuporte.other.*;
import com.github.ksoichiro.android.observablescrollview.*;
import br.com.vidadesuporte.lib.*;
import android.support.v7.widget.*;
import android.content.res.*;
import jp.wasabeef.recyclerview.animators.adapters.*;
import jp.wasabeef.recyclerview.animators.*;

@SuppressLint("InflateParams")
public class CategoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	boolean fromtag;
	String categOrTag, value;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_main, container, false);

		fromtag = getArguments().getBoolean("fromtag");
		
		if (fromtag && !getActivity().getIntent().hasExtra("fromwidget") && !getActivity().getIntent().hasExtra("fromcategs")) {
			categOrTag = "tag=";
		} else {
			categOrTag = "label=";
		}

		try {
			value = URLEncoder.encode(getArguments().getString("value"), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {}
		
		url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?" + categOrTag + value + "&id=" + getString(R.string.blogid);
		configCreate();
			
		if (!fromtag) {
			if (Build.VERSION.SDK_INT > 10) {
				MainActivity.ActionBarColor(((AppCompatActivity)getActivity()), getArguments().getString("label"));
				MainActivity.mDrawerList.setItemChecked(getArguments().getInt("pos"), true);
				MainActivity.pos = getArguments().getInt("pos");
			}
		} else {
			FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), getActivity().getIntent().getStringExtra("title"));
			((AppCompatActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);
		}

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
			
		if (Other.isConnected(getActivity())) {
			getPosts(false);
		} else {
			setError();
		}
		return view;
	}

	public void getPosts(final boolean fromUpdate) {
		Ion.with(this)
			.load(url)
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, JsonObject json) {
					if(e != null) {
						e.printStackTrace();
						Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
						toast.show();
						if(isfirst) {
							setError();
						}
						return;
					}
					mSwipeLayout.setRefreshing(false);
					if(fromUpdate) {
						postsarray.clear();
					}
					JsonArray posts = json.get(TAG_POSTS).getAsJsonArray();
					if (ismore) {
						if (passed == false) {
							more = more + posts.size();
						}
					}
					block = false;
					passed = false;

					lastMore = posts.size();

					for (int i = 0; i < posts.size(); i++) {
						JsonObject c = posts.get(i).getAsJsonObject();

						String id = c.get(TAG_ID).getAsString();
						String titulo = c.get(TAG_TITULO).getAsString();
						String descricao = c.get(TAG_DESCRICAO).getAsString();
						String imagem = c.get(TAG_IMAGEM).getAsString();
						String url = c.get(TAG_URL).getAsString();
						String comentarios = c.get(TAG_COMENTARIOS).getAsString();
						String categoria = c.get("categoriaicon").getAsString();
						String data = c.get("data").getAsString();

						postsarray.add(new Posts(id, titulo, imagem, descricao, url, comentarios, categoria, data));
					}
					
					if(isfirst) {
						if (Build.VERSION.SDK_INT >= 21) {
							progressBar.setVisibility(View.GONE);
						} else {
							progressBarCompat.setVisibility(View.GONE);
						}
						list.setVisibility(View.VISIBLE);
						isfirst = false;
					}
					
					hv.notifyDataSetChanged();

					if(Build.VERSION.SDK_INT > 10) {
						if (lastMore < 10) {
							Space(footer4, 50);
							nomore = true;
						}
					}

					page++;
					url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?" + categOrTag + value + "&page=" + String.valueOf(page) + "&id=" + getString(R.string.blogid);

				}});}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
			url = "http://apps.aloogle.net/blogapp/wordpress/json/main.php?" + categOrTag + value + "&id=" + getString(R.string.blogid);
			more = 0;
			page = 1;
			ismore = false;
			block = true;
			if (nomore) {
				Space(footer3, 0);
			}
			getPosts(true);
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}
}
