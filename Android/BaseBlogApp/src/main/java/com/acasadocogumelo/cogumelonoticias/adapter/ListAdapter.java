package com.acasadocogumelo.cogumelonoticias.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.List;
import com.koushikdutta.ion.Ion;
import com.acasadocogumelo.cogumelonoticias.R;
import net.aloogle.apps.blogapp.other.Categorias;
import net.aloogle.apps.blogapp.other.CustomTextView;

@SuppressLint("InflateParams")
public class ListAdapter extends RecyclerView.Adapter < RecyclerView.ViewHolder > {
	private Context context;
	private List <Categorias> categorias;

	public ListAdapter(Context context, List <Categorias> categorias) {
		this.context = context;
		this.categorias = categorias;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, 	int viewType) {
		View item = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.simple_list_item_3, null);

		LayoutParams vg = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		item.setLayoutParams(vg);

		ItemViewHolder viewHolder = new ItemViewHolder(item);
		return viewHolder;
	}

	public void remove(int position) {
		categorias.remove(position);
		notifyItemRemoved(position);
	}

	public void add(Categorias categ, int position) {
		categorias.add(categ);
		notifyItemInserted(position);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holderr, final int position) {
		if (holderr instanceof ItemViewHolder) {
			final ItemViewHolder holder = (ItemViewHolder)holderr;

			holder.titulo.setText(categorias.get(position).getTitle());

			holder.linear.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context, com.acasadocogumelo.cogumelonoticias.activity.FragmentActivity.class);
					intent.putExtra("fragment", 7);
					intent.putExtra("fromtag", true);
					intent.putExtra("fromcategs", true);
					intent.putExtra("title", categorias.get(position).getTitle());
					intent.putExtra("value", categorias.get(position).getId());
					context.startActivity(intent);
				}
			});
		}
	}

	public class ItemViewHolder extends RecyclerView.ViewHolder {

		public CustomTextView titulo;
		public LinearLayout linear;

		public ItemViewHolder(View item) {
			super(item);
			titulo = (CustomTextView)item.findViewById(R.id.titulo);
			linear = (LinearLayout)item.findViewById(R.id.conteudo);
		}
	}

	@Override
	public int getItemCount() {
		return categorias.size();
	}
}
