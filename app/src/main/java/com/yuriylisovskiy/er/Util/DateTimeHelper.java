package com.yuriylisovskiy.er.Util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String TIME_FORMAT = "HH:mm";

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, LocaleHelper.getLocale());
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, LocaleHelper.getLocale());

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	public static String formatTime(Time time) {
		return timeFormat.format(time);
	}

	public static String formatDate(long timestamp) {
		return dateFormat.format(timestamp);
	}

	public static String formatDate(Context ctx, long timestamp) {
		return DateUtils.formatDateTime(ctx, timestamp, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
	}

	public static String formatTime(long timestamp) {
		return timeFormat.format(timestamp);
	}

	public static String formatTime(Context ctx, long timestamp) {
		return DateUtils.formatDateTime(ctx, timestamp, DateUtils.FORMAT_SHOW_TIME);
	}

	public static Calendar dateFromString(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateFormat.parse(date));
		return calendar;
	}

	public static Calendar timeFromString(String time) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeFormat.parse(time));
		return calendar;
	}

	public static boolean isPast(Date time) {
		return Calendar.getInstance().getTime().after(time);
	}

	public static boolean isToday(Date date) {
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(date);
		return Calendar.getInstance().get(Calendar.DATE) == dateCalendar.get(Calendar.DATE);
	}

}
