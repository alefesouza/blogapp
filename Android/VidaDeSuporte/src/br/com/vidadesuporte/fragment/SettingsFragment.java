package br.com.vidadesuporte.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import br.com.vidadesuporte.R;

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

		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.settings));
		getActivity().findViewById(R.id.content_frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#eeeeee")));
		((AppCompatActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);

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
}
