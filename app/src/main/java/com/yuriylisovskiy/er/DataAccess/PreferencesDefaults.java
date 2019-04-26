package com.yuriylisovskiy.er.DataAccess;

import java.util.Locale;

public class PreferencesDefaults {

	final public static String UK_UA = "uk";
	final public static String EN_US = "en";

	final public static Locale LOCALE = Locale.US;
	final public static Locale LOCALE_UKRAINE = new Locale("uk","UA");

	final public static boolean IS_DARK_THEME = false;
	final public static String LANG = EN_US;
	final public static int MAX_BACKUPS = 5;
	final public static boolean REMOVE_EVENT_AFTER_TIME_UP = true;
	final public static boolean RUN_WITH_SYSTEM_START = true;

	// In minutes
	final public static int REMIND_TIME_VALUE = 1;

	// 0 - minute(s), 1 - hour(s), 2 - day(s), 3 - week(s)
	final public static int REMIND_TIME_UNIT = 0;

	final public static boolean INCLUDE_SETTINGS_BACKUP = true;
}
