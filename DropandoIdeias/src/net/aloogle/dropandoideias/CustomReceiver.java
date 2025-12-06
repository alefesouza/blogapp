package net.aloogle.dropandoideias;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.TaskStackBuilder;
import android.app.PendingIntent;

public class CustomReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			if (action.equals("net.aloogle.dropandoideias.UPDATE_STATUS"))
			{
				JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setTicker(json.getString("barra"))
					.setContentTitle(json.getString("titulo"))
					.setContentText(json.getString("texto"))
					.setAutoCancel(true)
					.setLights(0xFFFF0000, 1500, 2500)
					.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
					.setStyle(new BigTextStyle()
						.bigText(json.getString("textogrande"))
						.setBigContentTitle(json.getString("titulogrande"))
						.setSummaryText(json.getString("sumario")));
					
				Intent resultIntent = new Intent(context, SplashScreen.class);
				resultIntent.putExtra("url", json.getString("url"));

				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				
				stackBuilder.addParentStack(SplashScreen.class);
				
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				mNotificationManager.notify(001, mBuilder.build());
			}

		} catch (JSONException e) {}
	}
}