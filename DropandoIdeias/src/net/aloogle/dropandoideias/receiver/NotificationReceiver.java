package net.aloogle.dropandoideias.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.TaskStackBuilder;
import android.preference.PreferenceManager;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import net.aloogle.dropandoideias.R;
import net.aloogle.dropandoideias.activity.SplashScreen;

public class NotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("net.aloogle.dropandoideias.UPDATE_STATUS")) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			final Editor editor = preferences.edit();
			boolean notification = preferences.getBoolean("prefNotification", true);
			if (notification) {
				String[]lastparts = preferences.getString("lastReceivedTitles", "").split("\\$\\%\\#");
				try {
					JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

					editor.putString("receivedTicker", json.getString("barra"));
					editor.commit();
					editor.putString("receivedTitle", json.getString("titulo"));
					editor.commit();
					editor.putString("receivedText", json.getString("texto"));
					editor.commit();
					editor.putString("receivedBigTitle", json.getString("titulogrande"));
					editor.commit();
					editor.putString("receivedBigText", json.getString("textogrande"));
					editor.commit();
					editor.putString("receivedSummary", json.getString("sumario"));
					editor.commit();
					editor.putString("receivedUrl", json.getString("url"));
					editor.commit();
				} catch (JSONException e) {}

				if (Arrays.asList(lastparts).contains(preferences.getString("receivedText", ""))) {}
				else {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setTicker(preferences.getString("receivedTicker", ""))
						.setContentTitle(preferences.getString("receivedTitle", ""))
						.setContentText(preferences.getString("receivedText", ""))
						.setAutoCancel(true)
						.setLights(0xFF1B5C1F, 1500, 2500)
						.setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND)
						.setStyle(new BigTextStyle()
							.setBigContentTitle(preferences.getString("receivedBigTitle", ""))
							.bigText(preferences.getString("receivedBigText", ""))
							.setSummaryText(preferences.getString("receivedSummary", "")));

					Intent resultIntent = new Intent(context, SplashScreen.class);
					resultIntent.putExtra("url", preferences.getString("receivedUrl", "http://dropandoideias.com"));
					resultIntent.putExtra("fromnotification", "true");

					TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

					stackBuilder.addParentStack(SplashScreen.class);

					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

					mNotificationManager.notify(0, mBuilder.build());
				}
			}
		}
	}
}
