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
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("net.aloogle.acasadocogumelo.UPDATE_STATUS")) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			final Editor editor = preferences.edit();
			boolean notification = preferences.getBoolean("prefNotification", true);
			if (notification) {
				try {
					JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

					editor.putString("receivedId", json.getString("id"));
					editor.commit();
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
					if (json.getString("id").equals(preferences.getString("lastPostId", "5"))) {}
					else {
						if (json.getString("titulo").equals("")) {}
						else {
							editor.putInt("count", preferences.getInt("count", 0) + 1);
							editor.commit();
							editor.putString("receivedTitles", json.getString("texto") + "$%#" + preferences.getString("receivedTitles", ""));
							editor.commit();
						}
					}
				} catch (JSONException e) {}
				Intent cancelintent = new Intent(context, CancelReceiver.class);
				cancelintent.setAction("notification_cancelled");
				PendingIntent cancel = PendingIntent.getBroadcast(context, 0, cancelintent, PendingIntent.FLAG_CANCEL_CURRENT);
				if (preferences.getString("receivedId", "5").equals(preferences.getString("lastPostId", "5"))) {}
				else {
					if (preferences.getString("receivedTitle", "").equals("")) {}
					else {
						if (preferences.getInt("count", 0) == 1) {
							editor.putString("lastPostId", preferences.getString("receivedId", "5"));
							editor.commit();

							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
								.setSmallIcon(R.drawable.ic_launcher)
								.setTicker(preferences.getString("receivedTicker", ""))
								.setContentTitle(preferences.getString("receivedTitle", ""))
								.setContentText(preferences.getString("receivedText", ""))
								.setAutoCancel(true)
								.setSound(Uri.parse("android.resource://net.aloogle.acasadocogumelo/raw/ringtone"))
								.setLights(0xFFFF0000, 1500, 2500)
								.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
								.setStyle(new BigTextStyle()
									.setBigContentTitle(preferences.getString("receivedBigTitle", ""))
									.bigText(preferences.getString("receivedBigText", ""))
									.setSummaryText(preferences.getString("receivedSummary", "")))
								.setDeleteIntent(cancel);

							Intent resultIntent = new Intent(context, net.aloogle.acasadocogumelo.activity.SplashScreen.class);
							resultIntent.putExtra("url", preferences.getString("receivedUrl", "http://acasadocogumelo.com"));
							resultIntent.putExtra("fromnotification", "true");

							TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

							stackBuilder.addParentStack(net.aloogle.acasadocogumelo.activity.SplashScreen.class);

							stackBuilder.addNextIntent(resultIntent);
							PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
							mBuilder.setContentIntent(resultPendingIntent);
							NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

							mNotificationManager.notify(0, mBuilder.build());
						} else {
							editor.putString("lastPostId", preferences.getString("receivedId", "5"));
							editor.commit();
							String[]parts = preferences.getString("receivedTitles", "").split("\\$\\%\\#");
							NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
							inboxStyle.setBigContentTitle("A Casa do Cogumelo");
							inboxStyle.setSummaryText(preferences.getInt("count", 0) + " novas notícias");
							for (int i = 0; i <= parts.length - 1; i++) {
								inboxStyle.addLine(parts[i]);
							}

							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
								.setSmallIcon(R.drawable.ic_launcher)
								.setTicker("Novas notícias! - A Casa do Cogumelo")
								.setContentTitle("A Casa do Cogumelo")
								.setLights(0xFFFF0000, 1500, 2500)
								.setContentText(preferences.getInt("count", 0) + " novas notícias")
								.setAutoCancel(true)
								.setStyle(inboxStyle)
								.setDeleteIntent(cancel);

							Intent resultIntent = new Intent(context, net.aloogle.acasadocogumelo.activity.SplashScreen.class);
							resultIntent.putExtra("fromnotification", "true");

							TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

							stackBuilder.addParentStack(net.aloogle.acasadocogumelo.activity.SplashScreen.class);

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
	}
}
