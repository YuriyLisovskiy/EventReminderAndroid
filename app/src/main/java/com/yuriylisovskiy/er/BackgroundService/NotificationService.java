package com.yuriylisovskiy.er.BackgroundService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;

import com.yuriylisovskiy.er.DataAccess.DatabaseHelper;
import com.yuriylisovskiy.er.Services.EventService.EventService;
import com.yuriylisovskiy.er.Util.Logger;
import com.yuriylisovskiy.er.Util.Names;

public class NotificationService extends Service {

	private final Logger _logger = Logger.getInstance();

	int REQUEST_CODE = 18052019;

	private EventHandler _eventHandler;
	private AlarmManager _alarmManager;

	@Override
	public void onCreate() {
		super.onCreate();
		this._alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		this.startService();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (this._alarmManager != null) {
			Intent intent = new Intent(this, BroadcastReceiver.class);
			this._alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
		}
		if (this._eventHandler != null) {
			this._eventHandler.stop();
		}
	}

	private void startService() {
		try {
			if (!DatabaseHelper.isInitialized()) {
				DatabaseHelper.Initialize(this, Names.ER_DB);
			}
			this._eventHandler = new EventHandler(this, new EventService());
			this._eventHandler.start();
		} catch (Exception e) {
			this._logger.error(e.getMessage());
		}
	}
}
