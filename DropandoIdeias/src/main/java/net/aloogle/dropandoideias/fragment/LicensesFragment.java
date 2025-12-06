package net.aloogle.dropandoideias.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.adapter.TagAdapter;
import net.aloogle.dropandoideias.other.CustomTextView;

@SuppressLint("InflateParams")
public class LicensesFragment extends Fragment {
	Activity activity;
	View view;
	ArrayList <String> textos = new ArrayList <String> ();
	ArrayList <String> licenses = new ArrayList <String> ();

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 	Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.license, container, false);

		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Licenças de código aberto");

		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=androidsupport\">Android Support Libraries</a>" +
			"<br><ul>" +
			"<li>android-support-v4</li>" +
			"<li>android-support-v7-appcompat</li>" +
			"<li>android-support-v7-cardview</li>" +
			"<li>android-support-v7-recyclerview</li>" +
			"<li>android-support-design</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			" Copyright (C) 2015 The Android Open Source Project" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=gson\">Gson</a>" +
			"<br><ul>" +
			"<li>gson-2.3.1.jar</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Copyright 2008 Google Inc. All rights reserved." +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=parse\">Parse</a>" +
			"<br><ul>" +
			"<li>Parse-1.4.3.jar</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Parse grants you a revocable, personal, worldwide, royalty-free," +
			"   non-assignable and non-exclusive license to use the software provided to you by Parse as part of the Parse Services as provided to you by Parse." +
			"   This license is for the sole purpose of enabling you to use and enjoy the benefit of the Parse Services as provided by Parse, in the manner permitted by the Terms." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=ion\">Ion</a>" +
			"<br><ul>" +
			"<li>ion-2.1.6.jar</li>" +
			"<li>androidasync-2.1.6.jar</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"Copyright 2013 Koushik Dutta (2013)" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=observablescrollview\">ObservableScrollView</a>" +
			"<br><ul>" +
			"<li>ObservableScrollView</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Copyright 2014 Soichiro Kashima" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=nineoldandroids\">NineOldAndroids</a>" +
			"<br><ul>" +
			"<li>nineoldandroids-2.4.0.jar</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Copyright 2012 Jake Wharton" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"</code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=materialdesignlibrary\">MaterialDesignLibrary</a>" +
			"<br><ul>" +
			"<li>MaterialDesignLibrary</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"Copyright 2014 Ivan Navas" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=photoview\">PhotoView</a>" +
			"<br><ul>" +
			"<li>PhotoView</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"Copyright 2011, 2012 Chris Banes" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=supportv4pf\">Android Support v4 Preference Fragment</a>" +
			"<br><ul>" +
			"<li>android-support-v4-preferencefragment</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Copyright 2014 kolavar" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=recyclerviewanimators\">RecyclerView Animators</a>" +
			"<br><ul>" +
			"<li>recyclerview-animators-1.2.0.jar</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Copyright 2015 Wasabeef" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=headerviewrecycleradapter\">HeaderViewRecyclerAdapter</a>" +
			"<br><ul>" +
			"<li>HeaderViewRecyclerAdapter.java</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Copyright 2014 darnmason" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=beautifulflaticons\">Beautiful Flat Icons</a>" +
			"<br><ul>" +
			"<li>widget_animes.png</li>" +
			"<li>widget_blogsfera.png</li>" +
			"<li>widget_games.png</li>" +
			"<li>widget_internet.png</li>" +
			"<li>widget_livros.png</li>" +
			"<li>widget_nerdices.png</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Copyright 2014 Elegant Themes" +
			"<br><br>" +
			"   Licensed under the Apache License, Version 2.0 (the \"License\");" +
			"   you may not use this file except in compliance with the License." +
			"   You may obtain a copy of the License at" +
			"<br><br>" +
			"       http://www.apache.org/licenses/LICENSE-2.0" +
			"<br><br>" +
			"   Unless required by applicable law or agreed to in writing, software" +
			"   distributed under the License is distributed on an \"AS IS\" BASIS," +
			"   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." +
			"   See the License for the specific language governing permissions and" +
			"   limitations under the License." +
			"  </code></pre>");
		textos.add("<h4>Notice for file(s)</h4>" +
			"<a href=\"http://apps.aloogle.net/blogapp/redirect.php?to=circleiconspack\">CircleIconsPack</a>" +
			"<br><ul>" +
			"<li>drawer_facebook.png</li>" +
			"<li>drawer_instagram.png</li>" +
			"<li>drawer_twitter.png</li>" +
			"<li>drawer_youtube.png</li>" +
			"</ul>");
		licenses.add("<pre><code>" +
			"   Icons by Martz90 (DeviantArt)." +
			"  </code></pre>");
		
		for (int i = 0; i < textos.size(); i++) {
			LayoutInflater textoinflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View textoview = textoinflater.inflate(R.layout.license_text, null);
			TextView texto = (TextView)textoview.findViewById(R.id.text);
			CustomTextView code = (CustomTextView)textoview.findViewById(R.id.licensetext);
			texto.setMovementMethod(LinkMovementMethod.getInstance());
			texto.setText(Html.fromHtml(textos.get(i), null, new TagAdapter()));
			code.setText(Html.fromHtml(licenses.get(i), null, new TagAdapter()));
			LinearLayout viewlicenses = (LinearLayout)view.findViewById(R.id.licenses);
			viewlicenses.addView(textoview);
		}
		return view;
	}
}
