package net.aloogle.zeldacombr.receiver;

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
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import net.aloogle.zeldacombr.R;

public class NotificationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("net.aloogle.zeldacombr.UPDATE_STATUS")) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			final Editor editor = preferences.edit();
			boolean notification = preferences.getBoolean("prefNotification", true);
			if (notification) {
				String[] lastparts = preferences.getString("lastReceivedTitles", "").split("\\$\\%\\#");
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
					if (Arrays.asList(lastparts).contains(json.getString("texto"))) {}
					else {
						if (json.getString("texto").equals("")) {}
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

				String[] parts = preferences.getString("receivedTitles", "").split("\\$\\%\\#");
				if (Arrays.asList(lastparts).contains(preferences.getString("receivedText", ""))) {}
				else {
					if (preferences.getString("receivedTitle", "").equals("")) {}
					else {
						if (preferences.getInt("count", 0) == 1) {
							editor.putString("lastReceivedTitles", preferences.getString("receivedText", "") + "$%#" + preferences.getString("lastReceivedTitles", ""));
							editor.commit();
							String[] lastparts2 = preferences.getString("lastReceivedTitles", "").split("\\$\\%\\#");
							if(lastparts2.length >= 5) {
								editor.putString("lastReceivedTitles", lastparts2[0]  + "$%#" + lastparts2[1] + "$%#" + lastparts2[2] + "$%#" + lastparts2[3] + "$%#" + lastparts2[4]);
								editor.commit();
							}

							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
								.setSmallIcon(R.drawable.ic_launcher)
								.setTicker(preferences.getString("receivedTicker", ""))
								.setContentTitle(preferences.getString("receivedTitle", ""))
								.setContentText(preferences.getString("receivedText", ""))
								.setAutoCancel(true)
								.setLights(0xFF1B5C1F, 1500, 2500)
								.setStyle(new BigTextStyle()
									.setBigContentTitle(preferences.getString("receivedBigTitle", ""))
									.bigText(preferences.getString("receivedBigText", ""))
									.setSummaryText(preferences.getString("receivedSummary", "")))
								.setDeleteIntent(cancel);
							
							if(preferences.getString("prefSound", "item").equals("padrao")) {
								mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND);
							} else if (preferences.getString("prefSound", "item").equals("nenhum")) {
								mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
							} else {
								mBuilder.setSound(Uri.parse("android.resource://net.aloogle.zeldacombr/raw/" + preferences.getString("prefSound", "item")));
								mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
							}

							Intent resultIntent = new Intent(context, net.aloogle.zeldacombr.activity.SplashScreen.class);
							resultIntent.putExtra("url", preferences.getString("receivedUrl", "http://zelda.com.br"));
							resultIntent.putExtra("fromnotification", "true");

							TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

							stackBuilder.addParentStack(net.aloogle.zeldacombr.activity.SplashScreen.class);

							stackBuilder.addNextIntent(resultIntent);
							PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
							mBuilder.setContentIntent(resultPendingIntent);
							NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

							mNotificationManager.notify(0, mBuilder.build());
						} else {
							editor.putString("lastReceivedTitles", preferences.getString("receivedText", "") + "$%#" + preferences.getString("lastReceivedTitles", ""));
							editor.commit();
							String[] lastparts2 = preferences.getString("lastReceivedTitles", "").split("\\$\\%\\#");
							if(lastparts2.length >= 5) {
								editor.putString("lastReceivedTitles", lastparts2[0]  + "$%#" + lastparts2[1] + "$%#" + lastparts2[2] + "$%#" + lastparts2[3] + "$%#" + lastparts2[4]);
								editor.commit();
							}
							
							NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
							inboxStyle.setBigContentTitle("Zelda.com.br");
							inboxStyle.setSummaryText(preferences.getInt("count", 0) + " novas notícias");
							for (int i = 0; i <= parts.length - 1; i++) {
								inboxStyle.addLine(parts[i]);
							}

							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
								.setSmallIcon(R.drawable.ic_launcher)
								.setTicker("Novas notícias! - Zelda.com.br")
								.setContentTitle("Zelda.com.br")
								.setLights(0xFF1B5C1F, 1500, 2500)
								.setContentText(preferences.getInt("count", 0) + " novas notícias")
								.setAutoCancel(true)
								.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
								.setStyle(inboxStyle)
								.setDeleteIntent(cancel);

							Intent resultIntent = new Intent(context, net.aloogle.zeldacombr.activity.SplashScreen.class);
							resultIntent.putExtra("fromnotification", "true");

							TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

							stackBuilder.addParentStack(net.aloogle.zeldacombr.activity.SplashScreen.class);

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
