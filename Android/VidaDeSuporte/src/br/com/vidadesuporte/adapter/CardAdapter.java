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

public class CardAdapter extends BaseAdapter {

	private Context context;
	private ArrayList <String> title;
	private ArrayList <String> description;
	private ArrayList <String> image;
	private ArrayList <String> id;
	private ArrayList <String> url;
	private ArrayList <String> comments;

	public CardAdapter(Context context, ArrayList <String> title, ArrayList <String> description, ArrayList <String> image, ArrayList <String> id, ArrayList <String> url, ArrayList <String> comments) {
		this.context = context;
		this.title = title;
		this.description = description;
		this.image = image;
		this.id = id;
		this.url = url;
		this.comments = comments;
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
		CustomTextView comentarios = (CustomTextView)convertView.findViewById(R.id.comentarios);

		titulo.setText(title.get(position));
		
		if(description.get(position).toString().equals("")) {
			descricao.setVisibility(View.GONE);
		} else {
			descricao.setText(description.get(position));
		}

		final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)convertView.findViewById(R.id.progress);

		Ion.with (context)
		.load(image.get(position))
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
				intent.putExtra("extraJson", "{ \"id\": \"" + id.get(position).toString() + "\", \"titulo\": \"" + title.get(position).toString() + "\", \"descricao\": \"" + description.get(position).toString() + "\", \"imagem\": \"" + image.get(position).toString() + "\", \"url\": \"" + url.get(position).toString() + "\", \"comentarios\": \"" + comments.get(position) + "\" }");
				context.startActivity(intent);
			}
		});

		RelativeLayout commentsspace = (RelativeLayout)convertView.findViewById(R.id.commentsspace);
		if(comments.get(position).equals("")) {
			convertView.findViewById(R.id.comentariosline).setVisibility(View.GONE);
			commentsspace.setVisibility(View.GONE);
		} else {
		comentarios.setText(comments.get(position));
		commentsspace.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context, br.com.vidadesuporte.activity.CommentsActivity.class);
					intent.putExtra("url", url.get(position).toString());
					intent.putExtra("id", id.get(position).toString());
					context.startActivity(intent);
				}
			});
		}
		return convertView;
	}
}
