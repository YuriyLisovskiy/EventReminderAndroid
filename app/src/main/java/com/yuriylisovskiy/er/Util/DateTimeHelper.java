package com.yuriylisovskiy.er.Util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {

	public static final String DASH_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String TIME_FORMAT = "HH:mm";
	public static final String UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	public static final String NORMAL = "EEE, d MMM 'at' HH:mm";

	private static Locale locale = LocaleHelper.getLocale();

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, locale);
	private static SimpleDateFormat utcFormat = new SimpleDateFormat(UTC, locale);

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

	public static String formatUtc(long timestamp) {
		return utcFormat.format(timestamp);
	}

	public static String format(long timestamp, String format) {
		return new SimpleDateFormat(format, locale).format(timestamp);
	}

	public static Calendar parseDate(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateFormat.parse(date));
		return calendar;
	}

	public static Calendar parseTime(String time) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeFormat.parse(time));
		return calendar;
	}

	public static Calendar parseUtc(String timestamp) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(utcFormat.parse(timestamp));
		return calendar;
	}

	public static Calendar parse(String timestamp, String format) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new SimpleDateFormat(format, locale).parse(timestamp));
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
