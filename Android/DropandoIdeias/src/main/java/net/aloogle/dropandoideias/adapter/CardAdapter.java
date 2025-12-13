package net.aloogle.dropandoideias.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import java.util.List;

import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.fragment.CategoryFragment;
import net.aloogle.dropandoideias.fragment.PostFragment;
import net.aloogle.dropandoideias.other.CustomTextView;
import net.aloogle.dropandoideias.other.Other;
import net.aloogle.dropandoideias.other.Posts;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

@SuppressLint("InflateParams")
public class CardAdapter extends RecyclerView.Adapter < RecyclerView.ViewHolder > {
	private Context context;
	private List <Posts> posts;
    private boolean toRightPanel;

	public CardAdapter(Context context, List <Posts> posts, boolean toRightPanel) {
		this.context = context;
		this.posts = posts;
        this.toRightPanel = toRightPanel;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, 	int viewType) {
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
			final ItemViewHolder holder = (ItemViewHolder)holderr;
			holder.titulo.setText(posts.get(position).getTitle());

			if (posts.get(position).getDescription().equals("")) {
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
							float p = (float)downloaded / (float)total*100;
							holder.progressBar2.setProgress((int)(Math.round(p)));

							holder.progressBar2.setVisibility(View.VISIBLE);
						}
					});
				}
			})
			.withBitmap()
			.error(R.drawable.logo)
			.intoImageView(holder.imagem)
			.setCallback(new FutureCallback < ImageView > () {
				@Override
				public void onCompleted(Exception e, final ImageView imageView) {
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
                    String extraJson = "{ \"id\": \"" + posts.get(position).getId() + "\", \"title\": \"" + posts.get(position).getTitle() + "\", \"description\": \"" + posts.get(position).getDescription().replace("\"", "\\\"") + "\", \"image\": \"" + posts.get(position).getImage() + "\", \"url\": \"" + posts.get(position).getUrl() + "\", \"categoryicon\": \"" + posts.get(position).getCategory() + "\" }";
                    String comments = posts.get(position).getComments();
                    boolean istablet = context.getResources().getBoolean(R.bool.isTablet);
                    if (!istablet || !toRightPanel) {
                        Intent intent = new Intent(context, net.aloogle.dropandoideias.activity.PostActivity.class);
                        intent.putExtra("extraJson", extraJson);
                        intent.putExtra("comments", comments);
                        context.startActivity(intent);
                    } else {
                        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        Fragment post = new PostFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("extraJson", extraJson);
                        bundle.putString("comments", comments);
                        post.setArguments(bundle);
                        ft.replace(R.id.post_frame, post);
                        ft.commit();
                    }
                }
            });

			if (posts.get(position).getComments().equals("")) {
				holder.comentariosline.setVisibility(View.GONE);
				holder.commentsspace.setVisibility(View.GONE);

				if(Build.VERSION.SDK_INT < 19) {
					holder.relative.setPadding(0, 0, 0, Other.dpToPx((Activity)context, 10));
				}
			} else {
				if(posts.get(position).getComments().matches("0|1")) {
					holder.comentarios.setText(posts.get(position).getComments() + " " + context.getString(R.string.comentarioss));
				} else {
					holder.comentarios.setText(posts.get(position).getComments() + " " + context.getString(R.string.comentarios));
				}
				holder.commentsspace.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(context, net.aloogle.dropandoideias.activity.CommentsActivity.class);
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
		public ProgressBar progressBar2;
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
			progressBar2 = (ProgressBar)item.findViewById(R.id.progress);
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
