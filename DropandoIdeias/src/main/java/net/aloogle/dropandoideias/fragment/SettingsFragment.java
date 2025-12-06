package net.aloogle.dropandoideias.fragment;

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
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.FragmentActivity;

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

		if (preferences.getString("prefColor", "222222").equals("fundo")) {
			editor.putString("lastColor", "222222");
			editor.commit();
			editor.putInt("lastDefault", 1);
			editor.commit();
		} else {
			editor.putString("lastColor", preferences.getString("prefColor", "222222"));
			editor.commit();
			editor.putInt("lastDefault", 0);
			editor.commit();
		}

		FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), getActivity().getString(R.string.settings));
		getActivity().findViewById(R.id.content_frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#eeeeee")));
		((AppCompatActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);

		Preference prefColor = findPreference("prefColor");
		prefColor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (newValue.equals("fundo")) {
							editor.putInt("lastDefault", 1);
							editor.commit();
							editor.putString("lastColor", "222222");
							editor.commit();
							FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), getActivity().getString(R.string.settings));
						} else if (newValue.equals("outra")) {
							if (preferences.getInt("lastDefault", 1) == 1) {
								editor.putString("lastColor", "222222");
								editor.commit();
							}
							editor.putString("prefColor", preferences.getString("lastColor", "222222"));
							editor.commit();
							Intent color = new Intent(getActivity(), FragmentActivity.class);
							color.putExtra("fragment", 1);
							startActivity(color);
						} else {
							editor.putString("lastColor", preferences.getString("prefColor", "222222"));
							editor.commit();
							editor.putInt("lastDefault", 0);
							editor.commit();
							FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), getActivity().getString(R.string.settings));
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
						FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), getActivity().getString(R.string.settings));
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
							editor.putLong("longNotification", System.currentTimeMillis() + 15*24*60*60*1000);
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
			editor.putString("prefColor", "fundo");
			editor.commit();
		}
		FragmentActivity.ActionBarColor(((AppCompatActivity)getActivity()), getActivity().getString(R.string.settings));
	}
}
