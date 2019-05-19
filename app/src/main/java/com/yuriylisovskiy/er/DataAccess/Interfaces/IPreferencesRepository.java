package com.yuriylisovskiy.er.DataAccess.Interfaces;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public interface IPreferencesRepository {

	boolean IsInitialized();
	void Initialize(Context ctx);

	// ======= Getters ====== //
	boolean idDarkTheme();
	String lang();
	int maxBackups();
	boolean removeEventAfterTimeUp();
	boolean runWithSystemStart();
	int remindTimeBeforeEventValue();
	int remindTimeBeforeEventValueInMinutes();
	int remindTimeBeforeEventUnit();
	boolean backupSettings();
	Locale locale();

	// ======= Setters ====== //
	void setIsDarkTheme(boolean value);
	void setLang(String value);
	void setMaxBackups(int value);
	void setRemoveEventAfterTimeUp(boolean value);
	void setRunWithSystemStart(boolean value);
	void setRemindTimeBeforeEventValue(int value);
	void setRemindTimeBeforeEventUnits(int value);
	void setBackupSettings(boolean value);

	// ======== Other ======= //
	JSONObject ToJSONObject() throws JSONException;
	void FromJSONObject(JSONObject settings) throws JSONException;
}
