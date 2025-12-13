package net.aloogle.dropandoideias.widget;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.FragmentActivity;
import net.aloogle.dropandoideias.database.helper.DatabaseHelper;
import net.aloogle.dropandoideias.database.model.Jsons;
import net.aloogle.dropandoideias.other.Categorias;

public class WidgetShortcutConfigure extends AppCompatActivity {

	String value, titulo, categ;
	int pos;
	boolean allow;
	TypedArray icons;
	ArrayList <Categorias> categs = new ArrayList <Categorias> ();
	ArrayList <String> ids = new ArrayList <String> ();
	ArrayList <String> titles = new ArrayList <String> ();
	String selectedid;
	AutoCompleteTextView editcateg;
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_shortcut_configure);

		pos = 0;
		titulo = "";
		categ = "";
		allow = false;

		Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		FragmentActivity.ActionBarColor(this, getString(R.string.addshortcutwidget));

		icons = getResources().obtainTypedArray(R.array.categories_icons);

		edit = (EditText)findViewById(R.id.editText1);
		editcateg = (AutoCompleteTextView)findViewById(R.id.editText2);

		new getCategs().execute();

		RadioGroup rg = (RadioGroup)findViewById(R.id.radioCategory);
		final String[]categories = getResources().getStringArray(R.array.categories);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		for (int i = 0; i < categories.length; i++) {
			RadioButton rb = new RadioButton(this);
			rb.setLayoutParams(lp);
			rb.setText(categories[i]);
			rg.addView(rb);
		}

		RadioButton rb = new RadioButton(this);
		rb.setLayoutParams(lp);
		rb.setText("Outro");
		rg.addView(rb);

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				pos = checkedId;
				if (checkedId != categories.length + 1) {
					editcateg.setVisibility(View.GONE);
					RadioButton radio = (RadioButton)findViewById(group.getCheckedRadioButtonId());
					titulo = radio.getText().toString();
					categ = titulo;
					allow = true;
				} else {
					editcateg.setVisibility(View.VISIBLE);
					categ = "";
					titulo = "";
				}
				edit.setText(titulo);
			}
		});

		Button b = (Button)findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String textvalue = edit.getText().toString();
				if (pos == 0) {
					Toast toast = Toast.makeText(getApplicationContext(), "Selecione uma categoria", Toast.LENGTH_SHORT);
					toast.show();
				} else {
					if (pos == categories.length + 1) {
						categ = editcateg.getText().toString();
						if (categ.equals("")) {
							Toast toast = Toast.makeText(getApplicationContext(), "Digite uma categoria", Toast.LENGTH_SHORT);
							toast.show();
						} else {
							if (textvalue.equals("")) {
								textvalue = categ;
							}
							allow = true;
						}
					}
					if (allow) {
						String id = null;
						boolean iscateg = false;
						if (titles.equals(categ)) {
							id = ids.get(getIndexByname(textvalue));
							iscateg = true;
						} else {
							id = categ;
						}

						Intent launchIntent = new Intent();
						launchIntent.setClassName(getApplicationContext(), getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName()).getComponent().getClassName());
						launchIntent.putExtra("fromwidget", true);
						launchIntent.putExtra("title", categ);
						launchIntent.putExtra("value", id);
						launchIntent.putExtra("iscateg", iscateg);
						launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						int ic;
						if (pos == categories.length + 1) {
							ic = R.drawable.ic_launcher;
						} else {
							ic = icons.getResourceId(pos - 1, -1);
						}

						Intent.ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), ic);

						Intent intent = new Intent();
						intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
						intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, textvalue);
						intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
						icons.recycle();

						intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

						setResult(RESULT_OK, intent);

						finish();
					}
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			WidgetShortcutConfigure.this.finish();
			return true;
		default:
			return
			super.onOptionsItemSelected(item);
		}
	}

	private class getCategs extends AsyncTask < String, Jsons, Jsons> {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());

		@Override
		protected Jsons doInBackground(String...args) {
			Jsons categories = db.getJson("categories");
			return categories;
		}

		@Override
		protected void onPostExecute(Jsons categories) {
			if (categories != null) {
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject)parser.parse(categories.getJson());
				JsonArray categorias = json.get("categories").getAsJsonObject().get("categories").getAsJsonArray();

				for (int i = 0; i < categorias.size(); i++) {
					String id = categorias.get(i).getAsJsonObject().get("id").getAsString();
					String title = categorias.get(i).getAsJsonObject().get("name").getAsString();

					ids.add(id);
					titles.add(title);
				}

				String[]data = titles.toArray(new String[titles.size()]);
				final ArrayAdapter <  ?  > adapter = new ArrayAdapter < Object > (getApplicationContext(), R.layout.simple_list_item_2, data);
				editcateg.setAdapter(adapter);
				if (titles.size() < 40)
					editcateg.setThreshold(1);
				else
					editcateg.setThreshold(2);

				editcateg.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView <  ?  > parent, View view,
						int position, long id) {
						edit.setText(adapter.getItem(position).toString());
					}
				});
			}
			db.closeDB();
		}
	}

	public int getIndexByname(String pName) {
		for (String item : titles) {
			if (item.equals(pName))
				return pName.indexOf(item);
		}
		return -1;
	}
}
