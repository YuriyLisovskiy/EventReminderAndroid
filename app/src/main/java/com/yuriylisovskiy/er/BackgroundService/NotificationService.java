package com.yuriylisovskiy.er.BackgroundService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.yuriylisovskiy.er.Services.EventService.EventService;

public class NotificationService extends Service {

	int REQUEST_CODE = 11223344;

	private EventHandler _handler;

	@Override
	public void onCreate() {
		super.onCreate();
		this._handler = new EventHandler(this, new EventService());
		this.startService();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO: remove in production
		Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();

		Intent restartService = new Intent(getApplicationContext(),this.getClass());
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),this.REQUEST_CODE, restartService, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME,5000,pendingIntent);

		this._handler.stop();

		super.onDestroy();
	}

	private void startService() {
		// TODO: remove in production
		Toast.makeText(this, "Service Started.", Toast.LENGTH_LONG).show();

		this._handler.start();
	}
}
