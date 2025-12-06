package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.SettingsActivity;

@SuppressLint("NewApi")
public class SettingsFragment extends PreferenceFragment {
	@SuppressWarnings("unused")
	private Activity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		final Editor editor = preferences.edit();

		if(preferences.getString("prefColor", "ff222222").equals("fundo")) {
			editor.putString("lastColor", "ff222222");
			editor.commit();
			editor.putInt("lastFundo", 1);
			editor.commit();
		} else {
			editor.putString("lastColor", preferences.getString("prefColor", "ff222222"));
			editor.commit();
			editor.putInt("lastFundo", 0);
			editor.commit();
		}
		
		Preference prefColor = findPreference("prefColor");
		prefColor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (newValue.equals("outra")) {
							if(preferences.getInt("lastDefault", 1) == 1) {
								editor.putString("lastColor", "ff222222");
								editor.commit();
							}
							editor.putString("prefColor", preferences.getString("lastColor", "ff222222"));
							editor.commit();
							Intent color = new Intent(getActivity(), SettingsActivity.class);
							color.putExtra("fragment", 1);
							startActivity(color);
						} else if (newValue.equals("fundo")) {
							editor.putInt("lastDefault", 1);
							editor.commit();
							editor.putString("lastColor", "ff222222");
							editor.commit();
							((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_bg));
						} else {
							editor.putString("lastColor", preferences.getString("prefColor", "ff222222"));
							editor.commit();
							editor.putInt("lastDefault", 0);
							editor.commit();
							SettingsActivity.ActionBarColor(((ActionBarActivity)getActivity()), preferences.getString("prefIconColor", "branco"));
						}
					}
				}, 100);
				return true;
			}
		});

		Preference prefIconColor = findPreference("prefIconColor");
		prefIconColor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						SettingsActivity.ActionBarColor(((ActionBarActivity)getActivity()), preferences.getString("prefIconColor", String.valueOf(newValue)));
					}
				}, 100);
				return true;
			}
		});

		Preference prefNotification = findPreference("prefNotification");
		prefNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (newValue.equals(true)) {
							editor.putLong("longNotification", 0);
							editor.commit();
						} else {
							editor.putLong("longNotification", System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000);
							editor.commit();
						}
					}
				}, 100);
				return true;
			}
		});
	}
}
