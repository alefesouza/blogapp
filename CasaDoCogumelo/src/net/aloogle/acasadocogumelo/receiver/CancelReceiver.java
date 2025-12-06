package net.aloogle.acasadocogumelo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class CancelReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("notification_cancelled")) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			Editor editor = preferences.edit();
			editor.remove("count");
			editor.commit();
			editor.remove("receivedTitles");
			editor.commit();
		}
	}
}
