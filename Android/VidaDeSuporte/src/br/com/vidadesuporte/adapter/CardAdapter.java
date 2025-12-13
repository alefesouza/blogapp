package br.com.vidadesuporte.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.*;
import java.util.*;
import br.com.vidadesuporte.other.*;
import br.com.vidadesuporte.R;
import android.widget.*;
import com.koushikdutta.ion.*;
import android.view.View.*;
import android.os.*;
import com.koushikdutta.async.future.*;
import android.app.*;
import com.gc.materialdesign.views.*;
import ir.noghteh.*;
import android.util.*;
import android.graphics.Paint.Align;
import android.view.ViewGroup.*;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private Context context;
	private List <Posts> posts;

	public CardAdapter(Context context, List <Posts> posts) {
		this.context = context;
		this.posts = posts;
	}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
		  View item = LayoutInflater.from(parent.getContext())
			  .inflate(R.layout.card, null);

		  ItemViewHolder viewHolder = new ItemViewHolder(item);
		  return viewHolder;
    }
	
	public void remove(int position) {
		posts.remove(position);
		notifyItemRemoved(position);
	}
	
	public void add(Posts card, int position) {
		posts.add(card);
		notifyItemInserted(position);
	}

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderr, final int position) {
		if (holderr instanceof ItemViewHolder) {
		final ItemViewHolder holder =	(ItemViewHolder)holderr;
		holder.titulo.setText(posts.get(position).getTitle());

		if(posts.get(position).getDescription().equals("")) {
			holder.descricao.setVisibility(View.GONE);
		} else {
			holder.descricao.setText(posts.get(position).getDescription());
		}

		Ion.with (context)
			.load(posts.get(position).getImage())
			.progress(new ProgressCallback() {
				@Override
				public void onProgress(final long downloaded, final long total) {
					((Activity)context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								float p = (float)downloaded / (float)total * 100;
								holder.progressBar2.setProgress((int)(Math.round(p)));

								holder.progressBar2.setVisibility(View.VISIBLE);
							}
						});
				}
			})
			.withBitmap()
			.error(R.drawable.logo)
			.intoImageView(holder.imagem)
			.setCallback(new FutureCallback<ImageView>() {
				@Override
				public void onCompleted(Exception e, final ImageView imageView) {
					if (e != null) return;
					new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								holder.progressBar2.setVisibility(View.GONE);
							}
						}, 100);
				}
			});

		Ion.with(context)
			.load(posts.get(position).getCategory())
			.withBitmap()
			.intoImageView(holder.category);

		holder.relative.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context, br.com.vidadesuporte.activity.PostActivity.class);
					intent.putExtra("extraJson", "{ \"id\": \"" + posts.get(position).getId() + "\", \"titulo\": \"" + posts.get(position).getTitle() + "\", \"descricao\": \"" + posts.get(position).getDescription() + "\", \"imagem\": \"" + posts.get(position).getImage() + "\", \"url\": \"" + posts.get(position).getUrl() + "\", \"categoriaicon\": \"" + posts.get(position).getCategory() + "\" }");
					intent.putExtra("comentarios", posts.get(position).getComments());
					context.startActivity(intent);
				}
			});

		if(posts.get(position).getComments().equals("")) {
			holder.comentariosline.setVisibility(View.GONE);
			holder.commentsspace.setVisibility(View.GONE);
		} else {
			holder.comentarios.setText(posts.get(position).getComments() + " " + context.getString(R.string.comentarios));
			holder.commentsspace.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(context, br.com.vidadesuporte.activity.CommentsActivity.class);
						intent.putExtra("url", posts.get(position).getUrl());
						intent.putExtra("id", posts.get(position).getId());
						context.startActivity(intent);
					}
				});
			}
		}
    }
 
    public class ItemViewHolder extends RecyclerView.ViewHolder {

		public CustomTextView titulo;
		public ImageView imagem;
		public ImageView category;
		public CustomTextView descricao;
		public CustomTextView comentarios;
		public RelativeLayout imagerelative;
		public ProgressBarDeterminate progressBar2;
		public RelativeLayout commentsspace;
		public RelativeLayout relative;
		public View comentariosline;

        public ItemViewHolder(View item) {
            super(item);
			titulo = (CustomTextView)item.findViewById(R.id.titulo);
			imagem = (ImageView)item.findViewById(R.id.image);
			category = (ImageView)item.findViewById(R.id.category);
			descricao = (CustomTextView)item.findViewById(R.id.descricao);
			comentarios = (CustomTextView)item.findViewById(R.id.comentarios);
			imagerelative = (RelativeLayout)item.findViewById(R.id.imagerelative);
			progressBar2 = (ProgressBarDeterminate)item.findViewById(R.id.progress);
			commentsspace = (RelativeLayout)item.findViewById(R.id.commentsspace);
			relative = (RelativeLayout)item.findViewById(R.id.conteudo);
			comentariosline = item.findViewById(R.id.comentariosline);
        }
    }
	
    @Override
    public int getItemCount() {
        return posts.size();
    }
}
