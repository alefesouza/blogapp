package com.acasadocogumelo.cogumelonoticias.adapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.ion.Ion;
import com.acasadocogumelo.cogumelonoticias.R;
import net.aloogle.apps.blogapp.other.Icons;

public class DrawerAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <Icons> navDrawerItems;

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;

	private LayoutInflater mInflater;

	public DrawerAdapter(Context context, ArrayList < Icons > navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		mInflater = (LayoutInflater)context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getItemViewType(int position) {
		return navDrawerItems.get(position).getType() == 5 ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public String getItem(int position) {
		return "";
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		return navDrawerItems.get(position).getType() < 5;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		int rowType = getItemViewType(position);

		if (convertView == null) {
			holder = new ViewHolder();
			switch (rowType) {
			case TYPE_ITEM:
				convertView = mInflater.inflate(R.layout.drawer_adapter, null);
				holder.textView = (TextView)convertView.findViewById(R.id.categoryText);
				holder.imageView = (ImageView)convertView.findViewById(R.id.iconPicture);
				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.footer, null);
				holder.textView = (TextView)convertView.findViewById(R.id.myTextView1);
				holder.imageView = null;
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.textView.setText(navDrawerItems.get(position).getTitle());

		if (navDrawerItems.get(position).getType() < 5) {
			holder.imageView.setImageResource(navDrawerItems.get(position).getIcon());
			if (!navDrawerItems.get(position).getIcon2().equals("")) {
				Ion.with (context)
				.load(navDrawerItems.get(position).getIcon2())
				.withBitmap()
				.intoImageView(holder.imageView);
			}
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView textView;
		public ImageView imageView;
	}
}
