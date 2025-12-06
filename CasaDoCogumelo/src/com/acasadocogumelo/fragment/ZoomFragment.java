package com.acasadocogumelo.fragment;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import uk.co.senab.photoview.PhotoViewAttacher;
import com.acasadocogumelo.R;

public class ZoomFragment extends Fragment {

	ImageView mImageView;
	PhotoViewAttacher mAttacher;
	long enqueue;

	@SuppressWarnings("unused")
	private Activity activity;
	View view;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.image_zoom, container, false);
		
		mImageView = (ImageView)view.findViewById(R.id.image);
		final ProgressBarDeterminate progressBar2 = (ProgressBarDeterminate)view.findViewById(R.id.progress);
		
		Ion.with(getActivity()).load(getActivity().getIntent().getStringExtra("imgurl"))
			.progress(new ProgressCallback() {
				@Override
				public void onProgress(final long downloaded, final long total) {
					getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								float p = (float)downloaded / (float)total * 100;
								progressBar2.setProgress((int)(Math.round(p)));

								progressBar2.setVisibility(View.VISIBLE);

								if (downloaded == total) {
									progressBar2.setVisibility(View.GONE);
								}
							}
						});
				}
			})
			.withBitmap()
			.intoImageView(mImageView);

		mAttacher = new PhotoViewAttacher(mImageView);

		((ActionBarActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
		((ActionBarActivity)getActivity()).setTitle("");
		((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		getActivity().findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.zoom_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String url = getActivity().getIntent().getStringExtra("imgurl");
		switch (item.getItemId()) {
			case R.id.menu_download:
				String[]parts = url.split("/");
				String fileName = parts[parts.length - 1];
				@SuppressWarnings("static-access")
				DownloadManager dm = (DownloadManager)getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
				Request request = new Request(Uri.parse(url));
				request.setTitle(fileName);
				request.setDescription("A Casa do Cogumelo");
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
				enqueue = dm.enqueue(request);

				BroadcastReceiver onComplete = new BroadcastReceiver() {
					public void onReceive(Context ctxt, Intent intent) {
						Toast toast = Toast.makeText(getActivity(), "Imagem salva na pasta " + Environment.DIRECTORY_DOWNLOADS, Toast.LENGTH_LONG);
						toast.show();
					}
				};

				getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
				return true;
			case R.id.menu_share:
				Intent sharePageIntent = new Intent();
				sharePageIntent.setAction(Intent.ACTION_SEND);
				sharePageIntent.putExtra(Intent.EXTRA_TEXT, url);
				sharePageIntent.setType("text/plain");
				startActivity(Intent.createChooser(sharePageIntent, getResources().getText(R.string.share)));
				return true;
			default:
				return
					super.onOptionsItemSelected(item);
		}
	}
}
