package com.yuriylisovskiy.er.Util;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String TIME_FORMAT = "hh:mm";

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

	public static String formatTime(long timestamp) {
		return timeFormat.format(timestamp);
	}

	public static Calendar fromString(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateFormat.parse(date));
		return calendar;
	}

}
