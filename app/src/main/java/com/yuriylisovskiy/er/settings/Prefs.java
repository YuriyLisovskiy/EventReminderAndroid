package com.yuriylisovskiy.er.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

	private static Prefs instance;

	private SharedPreferences prefs;

	private boolean isDarkTheme = Defaults.IS_DARK_THEME;
	private int fontSize = Defaults.FONT_SIZE;
	private String lang = Defaults.LANG;
	private int maxBackups = Defaults.MAX_BACKUPS;
	private boolean removeEventAfterTimeUp = Defaults.REMOVE_EVENT_AFTER_TIME_UP;
	private boolean runWithSystemStart = Defaults.RUN_WITH_SYSTEM_START;
	private int remindTimeBeforeEvent = Defaults.REMIND_TIME;
	private boolean includeSettingsBackup = Defaults.INCLUDE_SETTINGS_BACKUP;

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
		this.fontSize = prefs.getInt("fontSize", fontSize);
		this.lang = prefs.getString("lang", lang);
		this.maxBackups = prefs.getInt("maxBackups", maxBackups);
		this.removeEventAfterTimeUp = prefs.getBoolean("removeEventAfterTimeUp", removeEventAfterTimeUp);
		this.runWithSystemStart = prefs.getBoolean("runWithSystemStart", runWithSystemStart);
		this.remindTimeBeforeEvent = prefs.getInt("remindTimeBeforeEvent", remindTimeBeforeEvent);
		this.includeSettingsBackup = prefs.getBoolean("includeSettingsBackup", includeSettingsBackup);
	}

	// ======= Getters ====== //
	public boolean idDarkTheme() {
		return this.isDarkTheme;
	}

	public int fontSize() {
		return this.fontSize;
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

	public int remindTimeBeforeEvent() {
		return this.remindTimeBeforeEvent;
	}

	public boolean includeSettingsBackup() {
		return this.includeSettingsBackup;
	}

	// ======= Setters ====== //
	public void setIsDarkTheme(boolean value) {
		this.prefs.edit().putBoolean("isDarkTheme", value).apply();
		this.isDarkTheme = value;
	}

	public void setFontSize(int value) {
		this.prefs.edit().putInt("fontSize", value).apply();
		this.fontSize = value;
	}

	public void setLang(String value) {
		this.prefs.edit().putString("lang", value).apply();
		this.lang = value;
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

	public void setRemindTimeBeforeEvent(int value) {
		this.prefs.edit().putInt("remindTimeBeforeEvent", value).apply();
		this.remindTimeBeforeEvent = value;
	}

	public void setIncludeSettingsBackup(boolean value) {
		this.prefs.edit().putBoolean("includeSettingsBackup", value).apply();
		this.includeSettingsBackup = value;
	}
}
