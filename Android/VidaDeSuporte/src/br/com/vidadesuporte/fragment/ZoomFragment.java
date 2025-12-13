package br.com.vidadesuporte.fragment;

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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import uk.co.senab.photoview.PhotoViewAttacher;
import br.com.vidadesuporte.R;
import uk.co.senab.photoview.*;
import android.app.*;
import android.util.*;

public class ZoomFragment extends Fragment {

	ImageView mImageView;
	PhotoViewAttacher mAttacher;
	long enqueue;
	String url, fileName;

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

        PhotoView photoView = new PhotoView(getActivity());
		photoView.setMaximumScale(3);
        view = photoView;

		url = getActivity().getIntent().getStringExtra("imgurl");
		String[]parts = url.split("/");
		fileName = parts[parts.length - 1];
			
        final ProgressDialog dlg = new ProgressDialog(getActivity());
        dlg.setTitle("Carregando...");
        dlg.setIndeterminate(false);
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dlg.show();
		
        Ion.with(this)
        .load(url)
        .progressDialog(dlg)
        .withBitmap()
        .deepZoom()
        .intoImageView(photoView)
        .setCallback(new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {
                dlg.cancel();
            }
        });

		((AppCompatActivity)getActivity()).setTitle(fileName);
		((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		getActivity().findViewById(R.id.frame).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		getActivity().findViewById(R.id.dropshadow).setVisibility(View.GONE);
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
		switch (item.getItemId()) {
		case R.id.menu_download:
			@SuppressWarnings("static-access")
			DownloadManager dm = (DownloadManager)getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
			Request request = new Request(Uri.parse(url));
			request.setTitle(fileName);
			request.setDescription(getString(R.string.app_name));
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
