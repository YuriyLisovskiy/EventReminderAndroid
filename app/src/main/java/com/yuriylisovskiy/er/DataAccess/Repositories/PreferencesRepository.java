package com.yuriylisovskiy.er.DataAccess.Repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.PreferencesDefaults;

import java.util.Locale;

public class PreferencesRepository implements IPreferencesRepository {
	private static PreferencesRepository _instance;

	private SharedPreferences _prefs;

	private boolean _isDarkTheme = PreferencesDefaults.IS_DARK_THEME;
	private String _lang = PreferencesDefaults.LANG;
	private int _maxBackups = PreferencesDefaults.MAX_BACKUPS;
	private boolean _removeEventAfterTimeUp = PreferencesDefaults.REMOVE_EVENT_AFTER_TIME_UP;
	private boolean _runWithSystemStart = PreferencesDefaults.RUN_WITH_SYSTEM_START;
	private int _remindTimeBeforeEventValue = PreferencesDefaults.REMIND_TIME_VALUE;
	private int _remindTimeBeforeEventUnit = PreferencesDefaults.REMIND_TIME_UNIT;
	private boolean _includeSettingsBackup = PreferencesDefaults.INCLUDE_SETTINGS_BACKUP;
	private Locale _locale = PreferencesDefaults.LOCALE;

	private PreferencesRepository() {}

	public static IPreferencesRepository getInstance() {
		if (_instance == null) {
			_instance = new PreferencesRepository();
		}
		return _instance;
	}

	public void Initialize(Context ctx) {
		this._prefs = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);

		this._isDarkTheme = this._prefs.getBoolean("isDarkTheme", _isDarkTheme);
		this._lang = this._prefs.getString("lang", _lang);
		this._maxBackups = this._prefs.getInt("maxBackups", _maxBackups);
		this._removeEventAfterTimeUp = this._prefs.getBoolean("removeEventAfterTimeUp", _removeEventAfterTimeUp);
		this._runWithSystemStart = this._prefs.getBoolean("runWithSystemStart", _runWithSystemStart);
		this._remindTimeBeforeEventValue = this._prefs.getInt("remindTimeBeforeEventValue", _remindTimeBeforeEventValue);
		this._remindTimeBeforeEventUnit = this._prefs.getInt("remindTimeBeforeEventUnit", _remindTimeBeforeEventUnit);
		this._includeSettingsBackup = this._prefs.getBoolean("includeSettingsBackup", _includeSettingsBackup);
		this._locale = this._lang.equals(PreferencesDefaults.UK_UA) ? PreferencesDefaults.LOCALE_UKRAINE : Locale.US;
	}

	public boolean idDarkTheme() {
		return this._isDarkTheme;
	}

	public String lang() {
		return this._lang;
	}

	public int maxBackups() {
		return this._maxBackups;
	}

	public boolean removeEventAfterTimeUp() {
		return this._removeEventAfterTimeUp;
	}

	public boolean runWithSystemStart() {
		return this._runWithSystemStart;
	}

	public int remindTimeBeforeEventValue() {
		return this._remindTimeBeforeEventValue;
	}

	public int remindTimeBeforeEventUnit() {
		return this._remindTimeBeforeEventUnit;
	}

	public boolean backupSettings() {
		return this._includeSettingsBackup;
	}

	public Locale locale() {
		return this._locale;
	}

	public void setIsDarkTheme(boolean value) {
		this._prefs.edit().putBoolean("isDarkTheme", value).apply();
		this._isDarkTheme = value;
	}

	public void setLang(String value) {
		this._prefs.edit().putString("lang", value).apply();
		this._lang = value;
		this._locale = value.equals(PreferencesDefaults.UK_UA) ? PreferencesDefaults.LOCALE_UKRAINE : Locale.US;
	}

	public void setMaxBackups(int value) {
		this._prefs.edit().putInt("maxBackups", value).apply();
		this._maxBackups = value;
	}

	public void setRemoveEventAfterTimeUp(boolean value) {
		this._prefs.edit().putBoolean("removeEventAfterTimeUp", value).apply();
		this._removeEventAfterTimeUp = value;
	}

	public void setRunWithSystemStart(boolean value) {
		this._prefs.edit().putBoolean("runWithSystemStart", value).apply();
		this._runWithSystemStart = value;
	}

	public void setRemindTimeBeforeEventValue(int value) {
		this._prefs.edit().putInt("remindTimeBeforeEventValue", value).apply();
		this._remindTimeBeforeEventValue = value;
	}

	public void setRemindTimeBeforeEventUnits(int value) {
		this._prefs.edit().putInt("remindTimeBeforeEventUnit", value).apply();
		this._remindTimeBeforeEventUnit = value;
	}

	public void setBackupSettings(boolean value) {
		this._prefs.edit().putBoolean("includeSettingsBackup", value).apply();
		this._includeSettingsBackup = value;
	}
}
