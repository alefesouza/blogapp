package net.aloogle.zeldacombr.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import net.aloogle.zeldacombr.R;
import android.media.MediaPlayer;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = preferences.edit();
		
		findPreference("prefSound").setEnabled(preferences.getBoolean("prefNotification", true));

		Preference prefNotification = findPreference("prefNotification");
		prefNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, final Object newValue) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (newValue.equals(true)) {
							editor.putLong("longNotification", 0);
							findPreference("prefSound").setEnabled(true);
						} else {
							editor.putLong("longNotification", System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000);
							findPreference("prefSound").setEnabled(false);
						}
						editor.commit();
					}
				}, 100);
				return true;
			}
		});
		

		Preference prefSound = findPreference("prefSound");
		prefSound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, final Object newValue) {
					new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if(newValue.equals("item")) {
									MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.item);
									mp.start();
								} else if(newValue.equals("item2")) {
									MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.item2);
									mp.start();
								} else if(newValue.equals("hey")) {
									MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.hey);
									mp.start();
								} else if(newValue.equals("listen")) {
									MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.listen);
									mp.start();
								} else if(newValue.equals("tatl")) {
									MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.tatl);
									mp.start();
								}
							}
						}, 100);
					return true;
				}
			});
	}
}
