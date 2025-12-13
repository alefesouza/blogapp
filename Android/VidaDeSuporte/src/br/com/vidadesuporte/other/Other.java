package br.com.vidadesuporte.other;

import android.app.Activity;
import android.net.ConnectivityManager;

public class Other {
	public static boolean isConnected(Activity activity) {
		@SuppressWarnings("static-access")
		ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(activity.CONNECTIVITY_SERVICE);
		boolean connected = cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
		return connected;
	}
}
