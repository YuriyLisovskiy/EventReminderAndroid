package com.yuriylisovskiy.er.DataAccess.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.yuriylisovskiy.er.Util.DateTimeHelper;

@Entity(tableName = "events")
public class EventModel {

	@PrimaryKey(autoGenerate = true)
	public long Id;

	public String Title;

	public String Date;

	public String Time;

	public String Description;

	@ColumnInfo(name = "is_past")
	public boolean IsPast;

	@ColumnInfo(name = "repeat_weekly")
	public boolean RepeatWeekly;

	@ColumnInfo(name = "remind_divisor")
	public int RemindDivisor;

	private void init(String title, long timeInMillis, long dateInMillis, String description, boolean isPast, boolean repeatWeekly, int remindDivisor) {
		this.Title = title;
		this.Date = DateTimeHelper.formatDate(dateInMillis);
		this.Time = DateTimeHelper.formatTime(timeInMillis);
		this.Description = description;
		this.IsPast = isPast;
		this.RepeatWeekly = repeatWeekly;
		this.RemindDivisor = remindDivisor;
	}

	public EventModel() {}

	public EventModel(String title, long timeInMillis, long dateInMillis, String description, boolean repeatWeekly) {
		this.init(title, timeInMillis, dateInMillis, description, false, repeatWeekly, 1);
	}

	public EventModel(String title, long timeInMillis, long dateInMillis, String description, boolean isPast, boolean repeatWeekly, int remindDivisor) {
		this.init(title, timeInMillis, dateInMillis, description, isPast, repeatWeekly, remindDivisor);
	}
}
