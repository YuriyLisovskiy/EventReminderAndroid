package com.yuriylisovskiy.er.Services.BackupService;

import android.util.Base64;

import com.yuriylisovskiy.er.DataAccess.DatabaseHelper;
import com.yuriylisovskiy.er.DataAccess.Interfaces.IBackupRepository;
import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.Models.BackupModel;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.DataAccess.Repositories.PreferencesRepository;
import com.yuriylisovskiy.er.Util.SizeOfString;
import com.yuriylisovskiy.er.Util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class BackupService implements IBackupService {

	private IBackupRepository _backupRepository;

	public BackupService() {
		this._backupRepository = DatabaseHelper.GetInstance().BackupRepository();
	}

	public BackupModel PrepareBackup(List<EventModel> events, boolean includeSettings, String userName) throws JSONException, ParseException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("db", BackupService.eventsToJSONArray(events));
		if (includeSettings) {
			IPreferencesRepository prefs = PreferencesRepository.getInstance();
			jsonData.put("settings", prefs.ToJSONObject());
		}
		if (userName != null) {
			jsonData.put("username", userName);
		}
		String encodedBackupData = Base64.encodeToString(jsonData.toString().getBytes(), Base64.NO_WRAP);
		return new BackupModel(
			Utils.Sha512(jsonData.toString()),
			Calendar.getInstance().getTimeInMillis(),
			encodedBackupData,
			events.size(),
			new SizeOfString(encodedBackupData).toString(),
			includeSettings
		);
	}

	public void CreateBackup(BackupModel model) {
		this._backupRepository.insert(model);
	}

	public void DeleteBackup(String digest) {
		BackupModel model = this._backupRepository.getById(digest);
		if (model != null) {
			this._backupRepository.delete(model);
		}
	}

	public List<BackupModel> GetAll() {
		return this._backupRepository.getAll();
	}

	private static JSONArray eventsToJSONArray(List<EventModel> events) throws JSONException, ParseException {
		JSONArray result = new JSONArray();
		for (int i = 0; i < events.size(); i++) {
			result.put(events.get(i).ToJSONObject());
		}
		return result;
	}
}
