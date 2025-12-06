package net.aloogle.acasadocogumelo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import net.aloogle.acasadocogumelo.R;
import net.aloogle.acasadocogumelo.other.*;
import java.util.ArrayList;

public class WebViewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <Icons> navDrawerItems;

	public WebViewAdapter(Context context, ArrayList <Icons> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)context
			.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.webview_adapter, null);
		}

		ImageView imgIcon = (ImageView)convertView
		.findViewById(R.id.iconPicture);
		WebViewTextView txtTitle = (WebViewTextView)convertView.findViewById(R.id.MDText);

		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		return convertView;
	}
}
