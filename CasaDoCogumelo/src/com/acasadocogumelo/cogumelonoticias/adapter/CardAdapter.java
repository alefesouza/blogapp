package com.acasadocogumelo.cogumelonoticias.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.acasadocogumelo.cogumelonoticias.R;
import com.acasadocogumelo.cogumelonoticias.other.CustomTextView;

public class CardAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <String> title;
	private ArrayList <String> description;
	private ArrayList <String> image;
	private ArrayList <String> id;
	private ArrayList <String> url;

	public CardAdapter(Context context, ArrayList <String> title, ArrayList <String> description, ArrayList <String> image, ArrayList <String> id, ArrayList <String> url) {
		this.context = context;
		this.title = title;
		this.description = description;
		this.image = image;
		this.id = id;
		this.url = url;
	}

	@Override
	public int getCount() {
		return title.size();
	}

	@Override
	public Object getItem(int position) {
		return title.get(position);
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
					R.layout.card, null);
		}

		CustomTextView titulo = (CustomTextView)convertView.findViewById(R.id.titulo);
		ImageView imagem = (ImageView)convertView.findViewById(R.id.image);
		CustomTextView descricao = (CustomTextView)convertView.findViewById(R.id.descricao);

		titulo.setText(title.get(position));
		descricao.setText(description.get(position));

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)convertView.findViewById(R.id.progress);
		final LinearLayout linear = (LinearLayout)convertView.findViewById(R.id.space);
		
		Ion.with (context)
		.load(image.get(position).replace("s1600", preferences.getString("prefImageQuality", "s400")))
		.progress(new ProgressCallback() {
			@Override
			public void onProgress(final long downloaded, final long total) {
				((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						float p = (float)downloaded / (float)total * 100;
						progressBar2.setProgress((int)(Math.round(p)));
						
						if (downloaded == total) {
							linear.setVisibility(View.GONE);
						}
					}
				});
			}
		})
		.withBitmap()
		.error(R.drawable.drawer_logo)
		.intoImageView(imagem);

		RelativeLayout relative = (RelativeLayout)convertView.findViewById(R.id.conteudo);
		relative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, com.acasadocogumelo.cogumelonoticias.activity.PostActivity.class);
				intent.putExtra("extraJson", "{ \"id\": \"" + id.get(position).toString() + "\", \"titulo\": \"" + title.get(position).toString() + "\", \"descricao\": \"" + description.get(position).toString() + "\", \"imagem\": \"" + image.get(position).toString() + "\", \"url\": \"" + url.get(position).toString() + "\" }");
				context.startActivity(intent);
			}
		});

		return convertView;
	}
}
