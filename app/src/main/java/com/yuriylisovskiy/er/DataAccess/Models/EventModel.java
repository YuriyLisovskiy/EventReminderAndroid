package com.yuriylisovskiy.er.DataAccess.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.yuriylisovskiy.er.Util.DateTimeHelper;
import com.yuriylisovskiy.er.Util.Globals;
import com.yuriylisovskiy.er.Util.Names;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = Names.EVENTS)
public class EventModel {

	@PrimaryKey(autoGenerate = true)
	public long Id;

	public String Title;

	public String Date;

	public String Time;

	public String Description;

	@ColumnInfo(name = Names.IS_PAST)
	public boolean IsPast;

	@ColumnInfo(name = Names.REPEAT_WEEKLY)
	public boolean RepeatWeekly;

	@ColumnInfo(name = Names.IS_NOTIFIED)
	public boolean IsNotified;

	private void init(String title, long timeInMillis, long dateInMillis, String description, boolean isPast, boolean repeatWeekly, boolean IsNotified) {
		this.Title = title;
		this.Date = DateTimeHelper.formatDate(dateInMillis);
		this.Time = DateTimeHelper.formatTime(timeInMillis);
		this.Description = description;
		this.IsPast = isPast;
		this.RepeatWeekly = repeatWeekly;
		this.IsNotified = IsNotified;
	}

	public EventModel() {}

	public EventModel(String title, long timeInMillis, long dateInMillis, String description, boolean repeatWeekly) {
		this.init(title, timeInMillis, dateInMillis, description, false, repeatWeekly, false);
	}

	public EventModel(String title, long timeInMillis, long dateInMillis, String description, boolean isPast, boolean repeatWeekly, boolean isNotified) {
		this.init(title, timeInMillis, dateInMillis, description, isPast, repeatWeekly, isNotified);
	}

	public boolean Expired(java.util.Date time) throws ParseException {
		Calendar eventDateTime = Calendar.getInstance();
		eventDateTime.setTime(DateTimeHelper.parseDate(this.Date).getTime());
		Calendar eventTime = Calendar.getInstance();
		eventTime.setTime(DateTimeHelper.parseTime(this.Time).getTime());
		eventDateTime.set(Calendar.HOUR, eventTime.get(Calendar.HOUR));
		eventDateTime.set(Calendar.MINUTE, eventTime.get(Calendar.MINUTE));
		eventDateTime.set(Calendar.SECOND, eventTime.get(Calendar.SECOND));
		return time.after(eventDateTime.getTime());
	}

	public JSONObject ToJSONObject() throws JSONException, ParseException {
		JSONObject object = new JSONObject();
		object.put(Names.TITLE, this.Title);
		object.put(Names.DATE, DateTimeHelper.format(
			DateTimeHelper.parseDate(this.Date).getTimeInMillis(), DateTimeHelper.DASH_DATE_FORMAT)
		);
		object.put(Names.TIME, this.Time + Constants.TIME_APPENDIX);
		object.put(Names.DESCRIPTION, this.Description);
		object.put(Names.IS_PAST, this.IsPast ? Globals.INT_TRUE : Globals.INT_FALSE);
		object.put(Names.REPEAT_WEEKLY, this.RepeatWeekly ? Globals.INT_TRUE : Globals.INT_FALSE);
		object.put(Names.IS_NOTIFIED, this.IsNotified ? 1 : 0);
		return object;
	}

	public static EventModel FromJSONObject(JSONObject object) throws JSONException, ParseException {
		return new EventModel(
			object.getString(Names.TITLE),
			DateTimeHelper.parseTime(object.getString(Names.TIME)).getTimeInMillis(),
			DateTimeHelper.parse(object.getString(Names.DATE), DateTimeHelper.DASH_DATE_FORMAT).getTimeInMillis(),
			object.getString(Names.DESCRIPTION),
			object.getInt(Names.IS_PAST) == Globals.INT_TRUE,
			object.getInt(Names.REPEAT_WEEKLY) == Globals.INT_TRUE,
			object.getInt(Names.IS_NOTIFIED) != 0
		);
	}
}
