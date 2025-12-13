package net.aloogle.canais.demonstracao.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.other.CustomTextView;
import net.aloogle.canais.demonstracao.lib.PlayerViewActivity;
import com.google.android.youtube.player.*;
import net.aloogle.canais.demonstracao.other.*;
import android.widget.*;
import net.aloogle.canais.demonstracao.activity.*;

public class ListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <Playlists> playlists;

	public ListAdapter(Context context, ArrayList <Playlists> playlists) {
		this.context = context;
		this.playlists = playlists;
	}

	@Override
	public int getCount() {
		return playlists.size();
	}

	@Override
	public Object getItem(int position) {
		return playlists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)context
			.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.simple_list_item_3, null);
		}

		CustomTextView titulo = (CustomTextView)convertView.findViewById(R.id.titulo);

		titulo.setText(playlists.get(position).getTitle());
		
		RelativeLayout relative = (RelativeLayout)convertView.findViewById(R.id.conteudo);
		relative.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context, FragmentActivity.class);
					intent.putExtra("fragment", 1);
					intent.putExtra("id", playlists.get(position).getId());
					intent.putExtra("titulo", playlists.get(position).getTitle());
					context.startActivity(intent);
				}
			});
		return convertView;
	}
}
