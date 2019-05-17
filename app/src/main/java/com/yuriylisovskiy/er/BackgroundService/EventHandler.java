package com.yuriylisovskiy.er.BackgroundService;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Services.EventService.IEventService;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

class EventHandler {

	private Timer _timer;
	private Context _ctx;
	private IEventService _eventService;
	private NotificationManagerCompat _notificationManager;

	private final int INTERVAL;
	private final String CHANNEL_ID = UUID.randomUUID().toString();

	EventHandler(Context ctx, IEventService eventService) {
		this._ctx = ctx;
		this._timer = new Timer();
		this._eventService = eventService;
		this._notificationManager = NotificationManagerCompat.from(ctx);
		INTERVAL = 10000;
	}

	void start() {
		this._timer.schedule(new TimerTask() {
			@Override
			public void run() {
				List<EventModel> events = _eventService.GetAll();
				if (events.size() > 0) {
					String[] messages = new String[events.size()];
					for (int i = 0; i < events.size(); i++) {
						messages[i] = events.get(i).Title;
					}
					sendNotification(messages);
				}
			}
		}, 0, this.INTERVAL);
	}

	void stop() {
		this._timer.cancel();
	}

	private void sendNotification(String[] messages) {
		NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle()
			.setBigContentTitle(messages.length + " " + this._ctx.getString(R.string.new_notifications));
		for (String msg : messages) {
			inbox.addLine(msg);
		}
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this._ctx, CHANNEL_ID)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(this._ctx.getString(R.string.app_name))
			.setStyle(inbox)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setDefaults(Notification.DEFAULT_SOUND)
			.setAutoCancel(true);

		//	notificationBuilder.setContentIntent(contentIntent);

		int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
		this._notificationManager.notify(id, notificationBuilder.build());
	}
}
