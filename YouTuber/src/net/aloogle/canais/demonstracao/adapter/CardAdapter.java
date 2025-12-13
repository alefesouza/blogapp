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

public class CardAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <Videos> videos;

	public CardAdapter(Context context, ArrayList <Videos> videos) {
		this.context = context;
		this.videos = videos;
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
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
		CustomTextView gosteis = (CustomTextView)convertView.findViewById(R.id.likes);
		CustomTextView visualizacoes = (CustomTextView)convertView.findViewById(R.id.visualizacoes);
		ImageView imagem = (ImageView)convertView.findViewById(R.id.image);

		titulo.setText(videos.get(position).getTitle());
		gosteis.setText(videos.get(position).getLikes());
		visualizacoes.setText(videos.get(position).getViews());

		final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)convertView.findViewById(R.id.progress);

		Ion.with (context)
		.load("https://i.ytimg.com/vi/" + videos.get(position).getId() + "/hqdefault.jpg")
		.progress(new ProgressCallback() {
			@Override
			public void onProgress(final long downloaded, final long total) {
				((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						float p = (float)downloaded / (float)total * 100;
						progressBar2.setProgress((int)(Math.round(p)));

						progressBar2.setVisibility(View.VISIBLE);
					}
				});
			}
		})
		.withBitmap()
			.intoImageView(imagem)
			.setCallback(new FutureCallback<ImageView>() {
				@Override
				public void onCompleted(Exception e, final ImageView imageView) {
					if (e != null) return;
					new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								progressBar2.setVisibility(View.GONE);
							}
						}, 100);
				}
			});

		RelativeLayout relative = (RelativeLayout)convertView.findViewById(R.id.conteudo);
		relative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, PlayerViewActivity.class);
				intent.putExtra("id", videos.get(position).getId());
				context.startActivity(intent);
			}
		});

		relative.setOnLongClickListener(new View.OnLongClickListener() {
				public boolean onLongClick(View v) {
					context.startActivity(YouTubeIntents.createPlayVideoIntent(context, videos.get(position).getId()));
					return false;
				}
			});

		return convertView;
	}
}
