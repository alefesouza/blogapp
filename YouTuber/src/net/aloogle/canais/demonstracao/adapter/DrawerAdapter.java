package net.aloogle.canais.demonstracao.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.other.*;
import java.util.ArrayList;
import android.widget.*;
import java.util.*;

public class DrawerAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <Icons> navDrawerItems;

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;

	private LayoutInflater mInflater;

	public DrawerAdapter(Context context, ArrayList <Icons> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		mInflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getItemViewType(int position) {
		return navDrawerItems.get(position).isSection() ? TYPE_SEPARATOR : TYPE_ITEM;
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
		return !navDrawerItems.get(position).isSection();
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
					holder.textView = (TextView) convertView.findViewById(R.id.categoryText);
					holder.imageView = (ImageView)convertView.findViewById(R.id.iconPicture);
					break;
				case TYPE_SEPARATOR:
					convertView = mInflater.inflate(R.layout.footer, null);
					holder.textView = (TextView) convertView.findViewById(R.id.myTextView1);
					holder.imageView = null;
					break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(navDrawerItems.get(position).getTitle());

		if(!navDrawerItems.get(position).isSection()) {
			holder.imageView.setImageResource(navDrawerItems.get(position).getIcon());
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView textView;
		public ImageView imageView;
	}
}
