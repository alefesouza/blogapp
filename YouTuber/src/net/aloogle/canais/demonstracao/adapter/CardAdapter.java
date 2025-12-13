package net.aloogle.canais.demonstracao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.*;
import java.util.*;
import net.aloogle.canais.demonstracao.other.*;
import net.aloogle.canais.demonstracao.R;
import android.widget.*;
import com.koushikdutta.ion.*;
import android.view.View.*;
import android.os.*;
import com.koushikdutta.async.future.*;
import android.app.*;
import com.gc.materialdesign.views.*;
import android.util.*;
import android.graphics.Paint.Align;
import android.view.ViewGroup.*;
import net.aloogle.canais.demonstracao.other.*;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private Context context;
	private List <Videos> posts;

	public CardAdapter(Context context, List <Videos> posts) {
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

	public void add(Videos card, int position) {
		posts.add(card);
		notifyItemInserted(position);
	}

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderr, final int position) {
		if (holderr instanceof ItemViewHolder) {
			final ItemViewHolder holder =	(ItemViewHolder)holderr;
			holder.titulo.setText(posts.get(position).getTitle());

			Ion.with (context)
				.load(posts.get(position).getId())
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

			holder.relative.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
					}
				});
		}
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

		public CustomTextView titulo;
		public ImageView imagem;
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
			comentarios = (CustomTextView)item.findViewById(R.id.comentarios);
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
