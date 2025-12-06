package com.acasadocogumelo.cogumelonoticias.other;

import android.app.Activity;
import android.net.ConnectivityManager;
import com.acasadocogumelo.cogumelonoticias.R;
import android.content.*;

public class Other {
	public static boolean isConnected(Activity activity) {
		@SuppressWarnings("static-access")
		ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(activity.CONNECTIVITY_SERVICE);
		boolean connected = cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
		return connected;
	}
	
	public static int getShortcutDrawable(Context activity, int pos) {
		int b;
		switch(pos) {
			case 1:
				b = R.drawable.widget_wiiu;
				break;
			case 2:
				b = R.drawable.widget_3ds;
				break;
			case 3:
				b = R.drawable.widget_mario;
				break;
			case 4:
				b = R.drawable.widget_zelda;
				break;
			case 5:
				b = R.drawable.widget_pokemon;
				break;
			case 6:
				b = R.drawable.widget_donkeykong;
				break;
			case 7:
				b = R.drawable.widget_metroid;
				break;
			default:
				b = R.drawable.ic_launcher;
		}
		return b;
	}

}
