package net.aloogle.acasadocogumelo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import net.aloogle.acasadocogumelo.R;
import net.aloogle.acasadocogumelo.activity.FragmentActivity;

@SuppressLint("NewApi")
public class SettingsFragment extends PreferenceFragment {
	@SuppressWarnings("unused")
	private Activity activity;
	SharedPreferences preferences;
	Editor editor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = preferences.edit();
		
		if (preferences.getString("prefColor", "padrao").equals("padrao")) {
			editor.putString("lastColor", "ff222222");
			editor.commit();
			editor.putInt("lastDefault", 1);
			editor.commit();
		} else {
			editor.putString("lastColor", preferences.getString("prefColor", "ff222222"));
			editor.commit();
			editor.putInt("lastDefault", 0);
			editor.commit();
		}

		FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), getActivity().getString(R.string.settings));
		getActivity().findViewById(R.id.content_frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff37474f")));
		((ActionBarActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);

		Preference prefColor = findPreference("prefColor");
		prefColor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (newValue.equals("padrao")) {
							editor.putInt("lastDefault", 1);
							editor.commit();
							editor.putString("lastColor", "ff222222");
							editor.commit();
							FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), getActivity().getString(R.string.settings));
						} else if (newValue.equals("outra")) {
							if (preferences.getInt("lastDefault", 1) == 1) {
								editor.putString("lastColor", "ff222222");
								editor.commit();
							}
							editor.putString("prefColor", preferences.getString("lastColor", "ff222222"));
							editor.commit();
							Intent color = new Intent(getActivity(), FragmentActivity.class);
							color.putExtra("fragment", 1);
							startActivity(color);
						} else {
							editor.putString("lastColor", preferences.getString("prefColor", "ff222222"));
							editor.commit();
							editor.putInt("lastDefault", 0);
							editor.commit();
							FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), getActivity().getString(R.string.settings));
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
						FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), getActivity().getString(R.string.settings));
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
						} else {
							editor.putLong("longNotification", System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000);
						}
						editor.commit();
					}
				}, 100);
				return true;
			}
		});
	}

	public void onResume() {
		super.onResume();
		if (preferences.getInt("lastDefault", 1) == 1) {
			editor.putString("prefColor", "padrao");
			editor.commit();
		}
		FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), getActivity().getString(R.string.settings));
	}
}
