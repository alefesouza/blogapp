package net.aloogle.canais.demonstracao.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.fragment.*;
import android.support.v4.view.*;

public class FragmentActivity extends AppCompatActivity {
	public Toolbar mToolbar;
	PreferenceFragment settings = new SettingsFragment();
	Fragment about = new AboutFragment();
	Fragment search = new SearchFragment();
	Fragment webview = new WebViewFrag();
	Fragment licenses = new LicensesFragment();
	Fragment playlists = new PlaylistsFragment();
	Fragment playlist = new PlaylistFragment();
	SharedPreferences preferences;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
			ft.replace(R.id.content_frame, settings);
			break;
		case 1:
			Bundle bundle = new Bundle();
			bundle.putString("id", getIntent().getStringExtra("id"));
			bundle.putString("titulo", getIntent().getStringExtra("titulo"));
			bundle.putBoolean("tofrag", true);
			playlist.setArguments(bundle);
			ft.replace(R.id.content_frame, playlist);
			break;
		case 2:
			ft.replace(R.id.content_frame, about);
			break;
		case 3:
			ft.replace(R.id.content_frame, playlists);
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
		}
		ft.commit();
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
