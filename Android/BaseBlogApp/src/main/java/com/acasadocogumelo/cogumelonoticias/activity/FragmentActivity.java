package com.acasadocogumelo.cogumelonoticias.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import com.acasadocogumelo.cogumelonoticias.R;
import com.acasadocogumelo.cogumelonoticias.fragment.*;

public class FragmentActivity extends AppCompatActivity {
	public Toolbar mToolbar;
	PreferenceFragment settings = new SettingsFragment();
	Fragment color = new ColorFragment();
	Fragment about = new AboutFragment();
	Fragment categorias = new CategoriasFragment();
	Fragment search = new SearchFragment();
	Fragment webview = new WebViewFrag();
	Fragment licenses = new LicensesFragment();
	Fragment category = new CategoryFragment();
	Fragment zoom = new ZoomFragment();
	SharedPreferences preferences;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getString("prefIconColor", "branco").equals("preto")) {
			setTheme(R.style.BlackOverflow);
		}
		super.onCreate(savedInstanceState);

		setContentView(R.layout.toolbar);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		selectItem(getIntent().getIntExtra("fragment", 0));
	}

	private void selectItem(int position) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (position) {
		case 0:
			getFragmentManager().beginTransaction().replace(R.id.content_frame, settings).commit();
			break;
		case 1:
			ft.replace(R.id.content_frame, color);
			break;
		case 2:
			ft.replace(R.id.content_frame, about);
			break;
		case 3:
			ft.replace(R.id.content_frame, categorias);
			break;
		case 4:
			ft.replace(R.id.content_frame, search);
			break;
		case 5:
			ft.replace(R.id.content_frame, webview);
			break;
		case 6:
			ft.replace(R.id.content_frame, licenses);
			break;
		case 7:
			Bundle bundle = new Bundle();
			bundle.putBoolean("fromtag", true);
			bundle.putBoolean("fromwidget", true);
			bundle.putBoolean("iscateg", getIntent().getBooleanExtra("iscateg", false));
			bundle.putString("title", getIntent().getStringExtra("title"));
			bundle.putString("value", getIntent().getStringExtra("value"));
			category.setArguments(bundle);
			ft.replace(R.id.content_frame, category);
			break;
		case 8:
			ft.replace(R.id.content_frame, zoom);
			break;
		}
		ft.commit();
	}

	@SuppressWarnings("deprecation")
	public static void ActionBarColor(AppCompatActivity activity, String titulo) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String bw = preferences.getString("prefIconColor", "branco");

		int indicator = bw.equals("branco") ? R.drawable.ic_arrow_back_white_24dp : R.drawable.ic_arrow_back_black_24dp;
		String title = bw.equals("branco") ? "ffffff" : "000000";

		activity.getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#" + title + "\">" + titulo + "</font>"));
		activity.getSupportActionBar().setHomeAsUpIndicator(indicator);

		String userColor = preferences.getString("prefColor", "222222");
		if (userColor.equals("fundo")) {
			activity.getSupportActionBar().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.toolbar_bg));
			activity.findViewById(R.id.frame).setBackgroundResource(R.drawable.toolbar_bg);
		} else {
			activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
			activity.findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + userColor)));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (getIntent().hasExtra("fromwidget")) {
				Intent intent = new Intent(FragmentActivity.this, MainActivity.class);
				startActivity(intent);
			}
			FragmentActivity.this.finish();
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (getIntent().getIntExtra("fragment", 0) != 5) {
			FragmentActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
