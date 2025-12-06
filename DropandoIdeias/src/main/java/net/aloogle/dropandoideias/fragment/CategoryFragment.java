package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.FragmentActivity;
import net.aloogle.dropandoideias.activity.MainActivity;
import net.aloogle.dropandoideias.other.Other;
import net.aloogle.dropandoideias.other.Posts;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class CategoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

	boolean fromtag;
	String categOrTag, value;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.fragment_main, container, false);

		fromtag = getArguments().getBoolean("fromtag", false);

		categOrTag = fromtag && !getActivity().getIntent().hasExtra("fromcategs") ? "tag=" : "label=";

		if (getActivity().getIntent().hasExtra("fromwidget")) {
			categOrTag = getActivity().getIntent().getBooleanExtra("iscateg", false) ? "label=" : "tag=";
		}

		try {
			value = URLEncoder.encode(getArguments().getString("value"), "UTF-8");
		} catch (UnsupportedEncodingException e) {}

		configCreate(!fromtag);
		firstUrl = defaultUrl + "main.php" + defaultQuery + "&" + categOrTag + value;
		url = firstUrl;

		if (!fromtag) {
			MainActivity.ActionBarColor(((AppCompatActivity)getActivity()), getArguments().getString("label"));
			MainActivity.mDrawerList.setItemChecked(getArguments().getInt("pos"), true);
			MainActivity.pos = getArguments().getInt("pos");
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
			public void onDownMotionEvent() {}

			@Override
			public void onUpOrCancelMotionEvent(ScrollState scrollState) {}
		});

		if (Other.isConnected(getActivity())) {
			getPosts(false, false);
		} else {
			setError();
		}
		return view;
	}

	public void getPosts(final boolean fromUpdate, final boolean toStore) {
		stored = null;
		Ion.with (this)
		.load(url)
		.asJsonObject()
		.setCallback(new FutureCallback < JsonObject > () {
			@Override
			public void onCompleted(Exception e, JsonObject json) {
				if (e != null) {
					if (!toStore) {
						Toast toast = Toast.makeText(getActivity(), "Houve um erro, " + getString(R.string.needinternet).toLowerCase(), Toast.LENGTH_LONG);
						toast.show();
					}
					if (isfirst) {
						setError();
					} else {
						Space(footer5, 50);
						block = true;
					}
					e.printStackTrace();
					return;
				}
				mSwipeLayout.setRefreshing(false);
				if (toStore) {
					stored = json;
				} else {
					makeList(json, false, fromUpdate);
					getPosts(false, true);
				}
			}
		});
	}

	public void makeList(JsonObject json, boolean fromOff, boolean fromUpdate) {
		if (fromUpdate) {
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
			String categoria = c.get(TAG_CATEGORYICON).getAsString();
			String data = c.get(TAG_DATA).getAsString();

			postsarray.add(new Posts(id, titulo, imagem, descricao, url, comentarios, categoria, data));
		}

		if (isfirst) {

				progressBar.setVisibility(View.GONE);

			list.setVisibility(View.VISIBLE);
			isfirst = false;
		}

		hv.notifyDataSetChanged();

		if (lastMore < Other.numberPosts) {
			Space(footer4, 50);
			nomore = true;
		}

		page++;
		url = defaultUrl + "main.php" + defaultQuery + "&" + categOrTag + value + "&page=" + String.valueOf(page);
	}

	public void onRefresh() {
		if (Other.isConnected(getActivity())) {
			url = firstUrl;
			more = 0;
			page = 1;
			ismore = false;
			block = true;
			if (nomore) {
				Space(footer3, 0);
			}
			getPosts(true, false);
		} else {
			mSwipeLayout.setRefreshing(false);
			Toast toast = Toast.makeText(getActivity(), getString(R.string.needinternet), Toast.LENGTH_LONG);
			toast.show();
		}
	}
}
