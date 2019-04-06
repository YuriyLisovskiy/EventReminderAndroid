package com.yuriylisovskiy.er.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class Prefs {

	private static Prefs instance;

	private SharedPreferences prefs;

	private boolean isDarkTheme = Defaults.IS_DARK_THEME;
	private String lang = Defaults.LANG;
	private int maxBackups = Defaults.MAX_BACKUPS;
	private boolean removeEventAfterTimeUp = Defaults.REMOVE_EVENT_AFTER_TIME_UP;
	private boolean runWithSystemStart = Defaults.RUN_WITH_SYSTEM_START;
	private int remindTimeBeforeEventValue = Defaults.REMIND_TIME_VALUE;
	private int remindTimeBeforeEventUnit = Defaults.REMIND_TIME_UNIT;
	private boolean includeSettingsBackup = Defaults.INCLUDE_SETTINGS_BACKUP;
	private Locale locale = Defaults.LOCALE;

	private Prefs() {}

	public static Prefs getInstance() {
		if (instance == null) {
			instance = new Prefs();
		}
		return instance;
	}

	public void Initialize(Context ctx, String prefsName, int prefsMode) {
		prefs = ctx.getSharedPreferences(prefsName, prefsMode);

		this.isDarkTheme = prefs.getBoolean("isDarkTheme", isDarkTheme);
		this.lang = prefs.getString("lang", lang);
		this.maxBackups = prefs.getInt("maxBackups", maxBackups);
		this.removeEventAfterTimeUp = prefs.getBoolean("removeEventAfterTimeUp", removeEventAfterTimeUp);
		this.runWithSystemStart = prefs.getBoolean("runWithSystemStart", runWithSystemStart);
		this.remindTimeBeforeEventValue = prefs.getInt("remindTimeBeforeEventValue", remindTimeBeforeEventValue);
		this.remindTimeBeforeEventUnit = prefs.getInt("remindTimeBeforeEventUnit", remindTimeBeforeEventUnit);
		this.includeSettingsBackup = prefs.getBoolean("includeSettingsBackup", includeSettingsBackup);
		this.locale = this.lang.equals(Defaults.UK_UA) ? Defaults.LOCALE_UKRAINE : Locale.US;
	}

	// ======= Getters ====== //
	public boolean idDarkTheme() {
		return this.isDarkTheme;
	}

	public String lang() {
		return this.lang;
	}

	public int maxBackups() {
		return this.maxBackups;
	}

	public boolean removeEventAfterTimeUp() {
		return this.removeEventAfterTimeUp;
	}

	public boolean runWithSystemStart() {
		return this.runWithSystemStart;
	}

	public int remindTimeBeforeEventValue() {
		return this.remindTimeBeforeEventValue;
	}

	public int remindTimeBeforeEventUnit() {
		return this.remindTimeBeforeEventUnit;
	}

	public boolean backupSettings() {
		return this.includeSettingsBackup;
	}

	public Locale locale() {
		return this.locale;
	}

	// ======= Setters ====== //
	public void setIsDarkTheme(boolean value) {
		this.prefs.edit().putBoolean("isDarkTheme", value).apply();
		this.isDarkTheme = value;
	}

	public void setLang(String value) {
		this.prefs.edit().putString("lang", value).apply();
		this.lang = value;
		this.locale = value.equals(Defaults.UK_UA) ? Defaults.LOCALE_UKRAINE : Locale.US;
	}

	public void setMaxBackups(int value) {
		this.prefs.edit().putInt("maxBackups", value).apply();
		this.maxBackups = value;
	}

	public void setRemoveEventAfterTimeUp(boolean value) {
		this.prefs.edit().putBoolean("removeEventAfterTimeUp", value).apply();
		this.removeEventAfterTimeUp = value;
	}

	public void setRunWithSystemStart(boolean value) {
		this.prefs.edit().putBoolean("runWithSystemStart", value).apply();
		this.runWithSystemStart = value;
	}

	public void setRemindTimeBeforeEventValue(int value) {
		this.prefs.edit().putInt("remindTimeBeforeEventValue", value).apply();
		this.remindTimeBeforeEventValue = value;
	}

	public void setRemindTimeBeforeEventUnits(int value) {
		this.prefs.edit().putInt("remindTimeBeforeEventUnit", value).apply();
		this.remindTimeBeforeEventUnit = value;
	}

	public void setBackupSettings(boolean value) {
		this.prefs.edit().putBoolean("includeSettingsBackup", value).apply();
		this.includeSettingsBackup = value;
	}
}
