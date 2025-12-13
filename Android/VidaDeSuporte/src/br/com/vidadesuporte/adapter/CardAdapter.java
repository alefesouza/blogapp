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
import br.com.vidadesuporte.other.*;

public class CardAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <Posts> posts;

	public CardAdapter(Context context, ArrayList <Posts> posts) {
		this.context = context;
		this.posts = posts;
	}

	@Override
	public int getCount() {
		return posts.size();
	}

	@Override
	public Object getItem(int position) {
		return posts.get(position);
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
		CustomTextView comentarios = (CustomTextView)convertView.findViewById(R.id.comentarios);

		titulo.setText(posts.get(position).getTitle());

		if(posts.get(position).getDescription().equals("")) {
			descricao.setVisibility(View.GONE);
		} else {
			descricao.setText(posts.get(position).getDescription());
		}

		final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)convertView.findViewById(R.id.progress);

		Ion.with (context)
			.load(posts.get(position).getImage())
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
			.error(R.drawable.logo)
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
					Intent intent = new Intent(context, br.com.vidadesuporte.activity.PostActivity.class);
					intent.putExtra("extraJson", "{ \"id\": \"" + posts.get(position).getId() + "\", \"titulo\": \"" + posts.get(position).getTitle() + "\", \"descricao\": \"" + posts.get(position).getDescription() + "\", \"imagem\": \"" + posts.get(position).getImage() + "\", \"url\": \"" + posts.get(position).getUrl() + "\", \"comentarios\": \"" + posts.get(position).getComments() + "\" }");
					context.startActivity(intent);
				}
			});

		RelativeLayout commentsspace = (RelativeLayout)convertView.findViewById(R.id.commentsspace);
		if(posts.get(position).getComments().equals("")) {
			convertView.findViewById(R.id.comentariosline).setVisibility(View.GONE);
			commentsspace.setVisibility(View.GONE);
		} else {
			comentarios.setText(posts.get(position).getComments() + " " + context.getString(R.string.comentarios));
			commentsspace.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(context, br.com.vidadesuporte.activity.CommentsActivity.class);
						intent.putExtra("url", posts.get(position).getUrl());
						intent.putExtra("id", posts.get(position).getId());
						context.startActivity(intent);
					}
				});
		}
		return convertView;
	}
}
