package net.aloogle.apps.acasadocogumelo.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import net.aloogle.apps.acasadocogumelo.R;
import net.aloogle.apps.acasadocogumelo.lib.ColorPicker;

public class ColorFragment extends Fragment {

	private ColorPicker mColorPicker;
	private View view;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.color_picker, container, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = preferences.edit();
		mColorPicker = (ColorPicker)view.findViewById(R.id.color_picker);
		if (preferences.getInt("lastDefault", 1) == 1) {
			editor.putString("prefColor", "padrao");
			editor.commit();
			((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.splash_bg));
			getActivity().findViewById(R.id.frame).setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.splash_bg));
			mColorPicker.setColor(Color.parseColor("#ffffffff"));
		} else {
			mColorPicker.setColor(Color.parseColor("#" + preferences.getString("lastColor", "ff222222")));
			applySelectedColor();
		}

		Button buttonSet = (Button)view.findViewById(R.id.set);
		buttonSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				applySelectedColor();
			}
		});

		Button buttonSave = (Button)view.findViewById(R.id.save);
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putString("prefColor", "ff" + String.format("%06x", 0xffffff & mColorPicker.getColor()));
				editor.commit();
				editor.putString("lastColor", preferences.getString("prefColor", "ff222222"));
				editor.commit();
				editor.putInt("lastDefault", 0);
				editor.commit();
				getActivity().finish();
			}
		});
		return view;
	}

	@SuppressWarnings("deprecation")
	private void applySelectedColor() {
		((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mColorPicker.getColor()));
		getActivity().findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(mColorPicker.getColor()));
	}
}
