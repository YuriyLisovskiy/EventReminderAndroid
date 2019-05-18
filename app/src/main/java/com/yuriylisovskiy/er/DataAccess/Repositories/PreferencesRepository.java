package com.yuriylisovskiy.er.DataAccess.Repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.PreferencesDefaults;
import com.yuriylisovskiy.er.Util.LocaleHelper;
import com.yuriylisovskiy.er.Util.Names;
import com.yuriylisovskiy.er.Util.TypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class PreferencesRepository implements IPreferencesRepository {
	private static PreferencesRepository _instance;

	private SharedPreferences _prefs = null;

	private boolean _isDarkTheme = PreferencesDefaults.IS_DARK_THEME;
	private String _lang = PreferencesDefaults.LANG;
	private int _maxBackups = PreferencesDefaults.MAX_BACKUPS;
	private boolean _removeEventAfterTimeUp = PreferencesDefaults.REMOVE_EVENT_AFTER_TIME_UP;
	private boolean _runWithSystemStart = PreferencesDefaults.RUN_WITH_SYSTEM_START;
	private int _remindTimeBeforeEventValue = PreferencesDefaults.REMIND_TIME_VALUE;
	private int _remindTimeBeforeEventUnit = PreferencesDefaults.REMIND_TIME_UNIT;
	private boolean _backupSettings = PreferencesDefaults.BACKUP_SETTINGS;
	private Locale _locale = PreferencesDefaults.LOCALE;

	private PreferencesRepository() {}

	public static IPreferencesRepository getInstance() {
		if (_instance == null) {
			_instance = new PreferencesRepository();
		}
		return _instance;
	}

	public boolean IsInitialized() {
		return this._prefs != null;
	}

	public void Initialize(Context ctx) {
		this._prefs = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);

		this._isDarkTheme = this._prefs.getBoolean(Names.IS_DARK_THEME, _isDarkTheme);
		this._lang = this._prefs.getString(Names.LANG, _lang);
		this._maxBackups = this._prefs.getInt(Names.MAX_BACKUPS, _maxBackups);
		this._removeEventAfterTimeUp = this._prefs.getBoolean(Names.REMOVE_EVENT_AFTER_TIME_UP, _removeEventAfterTimeUp);
		this._runWithSystemStart = this._prefs.getBoolean(Names.AUTO_START, _runWithSystemStart);
		this._remindTimeBeforeEventValue = this._prefs.getInt(Names.REMIND_TIME_VALUE, _remindTimeBeforeEventValue);
		this._remindTimeBeforeEventUnit = this._prefs.getInt(Names.REMIND_TIME_UNITS, _remindTimeBeforeEventUnit);
		this._backupSettings = this._prefs.getBoolean(Names.BACKUP_SETTINGS, _backupSettings);
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
		return this._backupSettings;
	}

	public Locale locale() {
		return this._locale;
	}

	public void setIsDarkTheme(boolean value) {
		this._prefs.edit().putBoolean(Names.IS_DARK_THEME, value).apply();
		this._isDarkTheme = value;
	}

	public void setLang(String value) {
		this._prefs.edit().putString(Names.LANG, value).apply();
		this._lang = value;
		this._locale = value.equals(PreferencesDefaults.UK_UA) ? PreferencesDefaults.LOCALE_UKRAINE : Locale.US;
	}

	public void setMaxBackups(int value) {
		this._prefs.edit().putInt(Names.MAX_BACKUPS, value).apply();
		this._maxBackups = value;
	}

	public void setRemoveEventAfterTimeUp(boolean value) {
		this._prefs.edit().putBoolean(Names.REMOVE_EVENT_AFTER_TIME_UP, value).apply();
		this._removeEventAfterTimeUp = value;
	}

	public void setRunWithSystemStart(boolean value) {
		this._prefs.edit().putBoolean(Names.AUTO_START, value).apply();
		this._runWithSystemStart = value;
	}

	public void setRemindTimeBeforeEventValue(int value) {
		this._prefs.edit().putInt(Names.REMIND_TIME_VALUE, value).apply();
		this._remindTimeBeforeEventValue = value;
	}

	public void setRemindTimeBeforeEventUnits(int value) {
		this._prefs.edit().putInt(Names.REMIND_TIME_UNITS, value).apply();
		this._remindTimeBeforeEventUnit = value;
	}

	public void setBackupSettings(boolean value) {
		this._prefs.edit().putBoolean(Names.BACKUP_SETTINGS, value).apply();
		this._backupSettings = value;
	}

	public JSONObject ToJSONObject() throws JSONException {
		JSONObject prefsObject = new JSONObject();
		prefsObject.put(Names.LANG, this.lang());
		prefsObject.put(Names.AUTO_START, TypeConverter.BoolToInt(this.runWithSystemStart()));
		prefsObject.put(Names.BACKUP_SETTINGS, TypeConverter.BoolToInt(this.backupSettings()));
		prefsObject.put(Names.MAX_BACKUPS, this.maxBackups());
		prefsObject.put(Names.REMOVE_EVENT_AFTER_TIME_UP, TypeConverter.BoolToInt(this.removeEventAfterTimeUp()));
		prefsObject.put(Names.REMIND_TIME_VALUE, this.remindTimeBeforeEventValue());
		prefsObject.put(Names.REMIND_TIME_UNITS, this.remindTimeBeforeEventUnit());
		prefsObject.put(Names.IS_DARK_THEME, TypeConverter.BoolToInt(this.idDarkTheme()));
		return prefsObject;
	}

	public void FromJSONObject(JSONObject settings) throws JSONException {
		if (settings.has(Names.LANG)) {
			this.setLang(PreferencesRepository.normalizeLang(settings.getString(Names.LANG)));
		}
		if (settings.has(Names.AUTO_START)) {
			this.setRunWithSystemStart(TypeConverter.IntToBool(settings.getInt(Names.AUTO_START)));
		}
		if (settings.has(Names.BACKUP_SETTINGS)) {
			this.setBackupSettings(TypeConverter.IntToBool(settings.getInt(Names.BACKUP_SETTINGS)));
		}
		if (settings.has(Names.MAX_BACKUPS)) {
			this.setMaxBackups(settings.getInt(Names.MAX_BACKUPS));
		}
		if (settings.has(Names.REMOVE_EVENT_AFTER_TIME_UP)) {
			this.setRemoveEventAfterTimeUp(TypeConverter.IntToBool(settings.getInt(Names.REMOVE_EVENT_AFTER_TIME_UP)));
		}
		if (settings.has(Names.REMIND_TIME_VALUE)) {
			this.setRemindTimeBeforeEventValue(settings.getInt(Names.REMIND_TIME_VALUE));
		}
		if (settings.has(Names.REMIND_TIME_UNITS)) {
			this.setRemindTimeBeforeEventUnits(settings.getInt(Names.REMIND_TIME_UNITS));
		}
		if (settings.has(Names.IS_DARK_THEME)) {
			this.setIsDarkTheme(TypeConverter.IntToBool(settings.getInt(Names.IS_DARK_THEME)));
		}
	}

	private static String normalizeLang(String globalLocale) {
		String result;
		switch (globalLocale) {
			case "uk_UA":
				result = "uk";
				break;
			case "en_US":
				result = "en";
				break;
			case "uk":
				result = "uk";
				break;
			case "en":
				result = "en";
				break;
			default:
				result = "en";
				break;
		}
		return result;
	}
}
