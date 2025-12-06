package net.aloogle.acasadocogumelo.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.TaskStackBuilder;
import android.preference.PreferenceManager;
import org.json.JSONException;
import org.json.JSONObject;
import net.aloogle.acasadocogumelo.R;

public class NotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			if (action.equals("net.aloogle.acasadocogumelo.UPDATE_STATUS")) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = preferences.edit();
				boolean notification = preferences.getBoolean("prefNotification", true);
				if (notification) {
					JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

					if (json.getString("id").equals(preferences.getString("lastPostId", "5"))) {}
					else {
						if (json.getString("titulo").equals("")) {}
						else {
							editor.putString("lastPostId", json.getString("id"));
							editor.commit();

							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
								.setSmallIcon(R.drawable.ic_launcher)
								.setTicker(json.getString("barra"))
								.setContentTitle(json.getString("titulo"))
								.setContentText(json.getString("texto"))
								.setAutoCancel(true)
								.setSound(Uri.parse("android.resource://net.aloogle.acasadocogumelo/raw/ringtone"))
								.setLights(0xFFFF0000, 1500, 2500)
								.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
								.setStyle(new BigTextStyle()
									.bigText(json.getString("textogrande"))
									.setBigContentTitle(json.getString("titulogrande"))
									.setSummaryText(json.getString("sumario")));

							editor.putInt("notifID", preferences.getInt("notifID", 0) + 1);
							editor.commit();

							Intent resultIntent = new Intent(context, net.aloogle.acasadocogumelo.activity.SplashScreen.class);
							resultIntent.putExtra("url", json.getString("url"));

							TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

							stackBuilder.addParentStack(net.aloogle.acasadocogumelo.activity.SplashScreen.class);

							stackBuilder.addNextIntent(resultIntent);
							PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(preferences.getInt("notifID", 0), PendingIntent.FLAG_UPDATE_CURRENT);
							mBuilder.setContentIntent(resultPendingIntent);
							NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

							mNotificationManager.notify(preferences.getInt("notifID", 0), mBuilder.build());
						}
					}
				}
			}
		} catch (JSONException e) {}
	}
}
