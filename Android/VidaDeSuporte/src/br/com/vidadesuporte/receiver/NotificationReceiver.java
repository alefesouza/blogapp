package br.com.vidadesuporte.receiver;

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
import br.com.vidadesuporte.R;
import br.com.vidadesuporte.activity.SplashScreen;

public class NotificationReceiver extends BroadcastReceiver {
	SharedPreferences preferences;
	Editor editor;
	String[] lastparts;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("net.aloogle.dropandoideias.UPDATE_STATUS")) {
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
			editor = preferences.edit();
			boolean notification = preferences.getBoolean("prefNotification", true);
			if (notification) {
				lastparts = preferences.getString("lastReceivedIds", "").split("\\$\\%\\#");
				try {
					JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

					if (!json.getString("tipo").equals("1")) {
						if (!Arrays.asList(lastparts).contains(json.getString("id"))) {
							editor.putInt("count", preferences.getInt("count", 0) + 1);
							editor.commit();
							editor.putString("receivedTitles", json.getString("texto") + "$%#" + preferences.getString("receivedTitles", ""));
							editor.commit();
						}
					}
					
					makeNotif(context, json.getString("tipo"), json.getString("id"), json.getString("barra"), json.getString("titulo"), json.getString("texto"), json.getString("titulogrande"), json.getString("textogrande"), json.getString("sumario"), json.getString("url"));
				} catch (JSONException e) {}
			}
		}
	}
	
	public void makeNotif(Context context, String receivedType, String receivedId, String receivedTicker, String receivedTitle, String receivedText, String receivedBigTitle, String receivedBigText, String receivedSummary, String receivedUrl) {
		Intent cancelintent = new Intent(context, CancelReceiver.class);
		cancelintent.setAction("notification_cancelled");
		PendingIntent cancel = PendingIntent.getBroadcast(context, 0, cancelintent, PendingIntent.FLAG_CANCEL_CURRENT);

		if (receivedType.equals("0")) {
			if (!Arrays.asList(lastparts).contains(receivedId)) {
				if (!receivedTitle.equals("")) {
					if (preferences.getInt("count", 0) == 1) {
						editor.putString("lastReceivedIds", receivedId + "$%#" + preferences.getString("lastReceivedIds", ""));
						editor.commit();
						String[]lastparts2 = preferences.getString("lastReceivedIds", "").split("\\$\\%\\#");
						if (lastparts2.length >= 5) {
							editor.putString("lastReceivedIds", lastparts2[0] + "$%#" + lastparts2[1] + "$%#" + lastparts2[2] + "$%#" + lastparts2[3] + "$%#" + lastparts2[4]);
							editor.commit();
						}

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
							.setSmallIcon(R.drawable.ic_launcher)
							.setTicker(receivedTicker)
							.setContentTitle(receivedTitle)
							.setContentText(receivedText)
							.setAutoCancel(true)
							.setSound(Uri.parse("android.resource://br.com.vidadesuporte/raw/ringtone"))
							.setLights(0xFFFF0000, 1500, 2500)
							.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
							.setPriority(NotificationCompat.PRIORITY_MAX)
							.setStyle(new BigTextStyle()
									  .setBigContentTitle(receivedBigTitle)
									  .bigText(receivedBigText)
									  .setSummaryText(receivedSummary))
							.setDeleteIntent(cancel);

						Intent resultIntent = new Intent(context, br.com.vidadesuporte.activity.SplashScreen.class);
						resultIntent.putExtra("id", receivedId);
						resultIntent.putExtra("fromnotification", true);

						TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

						stackBuilder.addParentStack(br.com.vidadesuporte.activity.SplashScreen.class);

						stackBuilder.addNextIntent(resultIntent);
						PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
						mBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

						mNotificationManager.notify(0, mBuilder.build());
					} else {
						editor.putString("lastReceivedIds", receivedId + "$%#" + preferences.getString("lastReceivedIds", ""));
						editor.commit();
						String[]lastparts2 = preferences.getString("lastReceivedTitles", "").split("\\$\\%\\#");
						if (lastparts2.length >= 5) {
							editor.putString("lastReceivedIds", lastparts2[0] + "$%#" + lastparts2[1] + "$%#" + lastparts2[2] + "$%#" + lastparts2[3] + "$%#" + lastparts2[4]);
							editor.commit();
						}

						String[]parts = preferences.getString("receivedTitles", "").split("\\$\\%\\#");
						NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
						inboxStyle.setBigContentTitle(context.getString(R.string.app_name));
						inboxStyle.setSummaryText(preferences.getInt("count", 0) + " novos posts");
						for (int i = 0; i <= parts.length - 1; i++) {
							inboxStyle.addLine(parts[i]);
						}

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
							.setSmallIcon(R.drawable.ic_launcher)
							.setTicker("Novos posts! - " + context.getString(R.string.app_name))
							.setContentTitle(context.getString(R.string.app_name))
							.setLights(0xFFFF0000, 1500, 2500)
							.setContentText(preferences.getInt("count", 0) + " novos posts")
							.setAutoCancel(true)
							.setStyle(inboxStyle)
							.setDeleteIntent(cancel);

						Intent resultIntent = new Intent(context, br.com.vidadesuporte.activity.SplashScreen.class);
						resultIntent.putExtra("fromnotification", "true");

						TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

						stackBuilder.addParentStack(br.com.vidadesuporte.activity.SplashScreen.class);

						stackBuilder.addNextIntent(resultIntent);
						PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
						mBuilder.setContentIntent(resultPendingIntent);

						NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(0, mBuilder.build());
					}
				}
			}
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(receivedTicker)
				.setContentTitle(receivedTitle)
				.setContentText(receivedText)
				.setAutoCancel(true)
				.setSound(Uri.parse("android.resource://br.com.vidadesuporte/raw/ringtone"))
				.setLights(0xFFFF0000, 1500, 2500)
				.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
				.setPriority(NotificationCompat.PRIORITY_MAX)
				.setStyle(new BigTextStyle()
						  .setBigContentTitle(receivedBigTitle)
						  .bigText(receivedBigText)
						  .setSummaryText(receivedSummary));

			Intent resultIntent = new Intent(context, SplashScreen.class);
			resultIntent.putExtra("titulo", context.getString(R.string.app_name));
			resultIntent.putExtra("url", receivedUrl);
			resultIntent.putExtra("ispersonalized", true);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

			stackBuilder.addParentStack(SplashScreen.class);

			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

			mNotificationManager.notify(1, mBuilder.build());
		}
	}
}
