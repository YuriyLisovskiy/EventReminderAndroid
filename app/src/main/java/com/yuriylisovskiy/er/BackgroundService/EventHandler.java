package com.yuriylisovskiy.er.BackgroundService;

import android.app.Notification;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Services.EventService.IEventService;
import com.yuriylisovskiy.er.Util.DateTimeHelper;
import com.yuriylisovskiy.er.Util.Logger;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

class EventHandler {

	private Timer _timer;
	private Context _ctx;
	private IEventService _eventService;
	private IPreferencesRepository _prefs;
	private NotificationManagerCompat _notificationManager;

	private AsyncTask<Void, Void, List<EventModel>> _task;

	private Logger _logger = Logger.getInstance();

	private final int INTERVAL;
	private final String CHANNEL_ID = UUID.randomUUID().toString();

	EventHandler(Context ctx, IEventService eventService, IPreferencesRepository preferencesRepository) {
		this._ctx = ctx;
		this._timer = new Timer();
		this._eventService = eventService;
		this._prefs = preferencesRepository;
		this._notificationManager = NotificationManagerCompat.from(ctx);
		INTERVAL = 1000;
	}

	void start() {
		this.processEvents(null);
		this._timer.schedule(new TimerTask() {
			@Override
			public void run() {
				processEvents(Calendar.getInstance().getTime());
			}
		}, 0, this.INTERVAL);
	}

	void stop() {
		this._timer.cancel();
	}

	private void sendNotification(EventModel[] messages) {
		NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle()
			.setBigContentTitle(messages.length + " " + this._ctx.getString(R.string.new_notifications));
		for (EventModel event : messages) {
			inbox.addLine(event.Date + " at " + event.Time + ": " + event.Title);
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

	private void processEvents(Date dateFilter) {
		if (this._task != null) {
			return;
		}
		this._task = new EventHandler.ProcessEventsTask(this, dateFilter);
		this._task.execute((Void) null);
	}

	private static class ProcessEventsTask extends AsyncTask<Void, Void, List<EventModel>> {

		private WeakReference<EventHandler> _cls;
		private Date _now;
		private Date _dateFilter;

		ProcessEventsTask(EventHandler cls, Date dateFilter) {
			this._cls = new WeakReference<>(cls);
			this._dateFilter = dateFilter;
		}

		@Override
		protected List<EventModel> doInBackground(Void... params) {
			int remindTime = this._cls.get()._prefs.remindTimeBeforeEventValueInMinutes();
			List<EventModel> events;
			if (this._dateFilter == null) {
				this._dateFilter = new Date(0);
			}
			if (this._cls.get()._eventService == null) {
				return null;
			}
			events = this._cls.get()._eventService.GetRange(this._dateFilter, remindTime);
			List<EventModel> eventsToNotify = new ArrayList<>();
			this._now = Calendar.getInstance().getTime();
			for (EventModel event : events) {
				try {
					double dividedRemindTime = remindTime * 1.0 / event.RemindDivisor;
					Calendar nowPlusDelta = Calendar.getInstance();
					if (dividedRemindTime >= 1) {
						nowPlusDelta.set(Calendar.MINUTE, (int) dividedRemindTime);
					}
					nowPlusDelta.set(Calendar.MILLISECOND, 0);
					Calendar eventTime = Calendar.getInstance();
					eventTime.setTime(DateTimeHelper.parseDate(event.Date).getTime());

					boolean isDateBeforeNowPlusDelta = eventTime.before(nowPlusDelta);

					Calendar time = Calendar.getInstance();
					time.setTime(DateTimeHelper.parseTime(event.Time).getTime());
					eventTime.set(Calendar.HOUR, time.get(Calendar.HOUR));
					eventTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
					eventTime.set(Calendar.SECOND, time.get(Calendar.SECOND));

					if (!event.IsPast && (event.Date.equals(DateTimeHelper.formatDate(this._now.getTime())) && eventTime.before(nowPlusDelta)) || isDateBeforeNowPlusDelta) {
						eventsToNotify.add(event);
						if (event.Expired(Calendar.getInstance().getTime())) {
							if (event.RepeatWeekly) {
								Calendar today = Calendar.getInstance();
								while (eventTime.before(today)) {
									eventTime.add(Calendar.DAY_OF_WEEK, 7);
								}
								event.Date = DateTimeHelper.formatDate(eventTime.getTime());
								event.RemindDivisor = 1;
								if (this._cls.get()._eventService != null) {
									this._cls.get()._eventService.UpdateItem(event);
								}
							} else {
								if (this._cls.get()._eventService != null) {
									if (this._cls.get()._prefs.removeEventAfterTimeUp()) {
										this._cls.get()._eventService.DeleteById(event.Id);
									} else {
										event.IsPast = true;
										this._cls.get()._eventService.UpdateItem(event);
									}
								}
							}
						} else {
							Logger logger = Logger.getInstance();
							event.RemindDivisor *= 2;
							if (this._cls.get()._eventService != null) {
								this._cls.get()._eventService.UpdateItem(event);
								logger.debug("Updated");
							}
							logger.debug(event.RemindDivisor + "");
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
					this._cls.get()._logger.error(e.getMessage());
				}
			}
			return eventsToNotify;
		}

		@Override
		protected void onPostExecute(List<EventModel> events) {
			if (events != null && events.size() > 0) {
				this._cls.get().sendNotification(events.toArray(new EventModel[0]));
			}
			this.taskFinished();
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get()._task = null;
		}
	}
}
