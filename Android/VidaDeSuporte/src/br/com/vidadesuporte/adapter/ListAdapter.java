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

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private Context context;
	private List <Categorias> categorias;

	public ListAdapter(Context context, List <Categorias> categorias) {
		this.context = context;
		this.categorias = categorias;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
													  int viewType) {
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
		if(holderr instanceof ItemViewHolder) {
			final ItemViewHolder holder =	(ItemViewHolder)holderr;

			Ion.with(context)
				.load(categorias.get(position).getIcon())
				.withBitmap()
				.intoImageView(holder.category);

			holder.titulo.setText(categorias.get(position).getTitle());

			holder.linear.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(context, br.com.vidadesuporte.activity.FragmentActivity.class);
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
		public ImageView category;
		public LinearLayout linear;

		public ItemViewHolder(View item) {
			super(item);
			titulo = (CustomTextView)item.findViewById(R.id.titulo);
			category = (ImageView)item.findViewById(R.id.category);
			linear = (LinearLayout)item.findViewById(R.id.conteudo);
		}
	}

	@Override
	public int getItemCount() {
		return categorias.size();
	}
}
