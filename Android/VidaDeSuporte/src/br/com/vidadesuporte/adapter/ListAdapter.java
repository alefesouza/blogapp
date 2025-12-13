package br.com.vidadesuporte.adapter;

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
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.other.CustomTextView;
import br.com.vidadesuporte.other.*;
import android.widget.*;
import br.com.vidadesuporte.activity.*;

public class ListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <Categorias> categorias;

	public ListAdapter(Context context, ArrayList <Categorias> categorias) {
		this.context = context;
		this.categorias = categorias;
	}

	@Override
	public int getCount() {
		return categorias.size();
	}

	@Override
	public Object getItem(int position) {
		return categorias.get(position);
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

		titulo.setText(categorias.get(position).getTitle());
		
		RelativeLayout relative = (RelativeLayout)convertView.findViewById(R.id.conteudo);
		relative.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context, br.com.vidadesuporte.activity.FragmentActivity.class);
					intent.putExtra("fragment", 7);
					intent.putExtra("fromtag", true);
					intent.putExtra("fromcategs", true);
					intent.putExtra("label", categorias.get(position).getTitle());
					intent.putExtra("value", categorias.get(position).getId());
					context.startActivity(intent);
				}
			});
		return convertView;
	}
}
