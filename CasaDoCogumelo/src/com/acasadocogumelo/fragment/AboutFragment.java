package com.acasadocogumelo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.acasadocogumelo.R;
import com.acasadocogumelo.lib.CustomLinkMovementMethod;

public class AboutFragment extends Fragment {
	Activity activity;
	View view;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.about, container, false);

		com.acasadocogumelo.activity.FragmentActivity.ActionBarColor(((ActionBarActivity)getActivity()), "Sobre");
		((ActionBarActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.ic_toolbar);

		String sobretext = "<h3>Site</h3>" +
			"Todos os direitos reservados por A Casa do Cogumelo - 2014.<br><br>Caso queira entrar em contato com a equipe do site <a href=\"http://www.acasadocogumelo.com/p/contato.html\">clique aqui</a>.<br><br>Propriedade intelectual de A Casa do Cogumelo." +
			"<h3>Aplicativo</h3>" +
			"Aplicativo desenvolvido por <a href=\"http://google.com/+AlefeSouza\">Alefe Souza</a>.<br><br>Quer ter uma versão desse aplicativo para seu blog ou site? Entre em contato <a href=\"mailto:blogapp@apps.aloogle.net\">clicando aqui</a>!";

		TextView sobre = (TextView)view.findViewById(R.id.sobre);
		sobre.setMovementMethod(CustomLinkMovementMethod.getInstance(getActivity()));
		sobre.setText(Html.fromHtml(sobretext));

		view.findViewById(R.id.sourcecodelicenses).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), com.acasadocogumelo.activity.FragmentActivity.class);
				intent.putExtra("fragment", 6);
				startActivity(intent);
			}
		});
		return view;
	}
}
