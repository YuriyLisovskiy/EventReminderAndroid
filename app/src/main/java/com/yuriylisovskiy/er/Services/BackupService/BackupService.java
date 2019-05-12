package com.yuriylisovskiy.er.Services.BackupService;

import android.util.Base64;

import com.yuriylisovskiy.er.DataAccess.DatabaseHelper;
import com.yuriylisovskiy.er.DataAccess.Interfaces.IBackupRepository;
import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.Models.BackupModel;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.DataAccess.Repositories.PreferencesRepository;
import com.yuriylisovskiy.er.Services.BackupService.Exceptions.InvalidBackupException;
import com.yuriylisovskiy.er.Util.DateTimeHelper;
import com.yuriylisovskiy.er.Util.SizeOfString;
import com.yuriylisovskiy.er.Util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BackupService implements IBackupService {

	private IBackupRepository _backupRepository;

	private static final String DB = "db";
	private static final String SETTINGS = "settings";
	private static final String USERNAME = "username";

	public BackupService() {
		this._backupRepository = DatabaseHelper.GetInstance().BackupRepository();
	}

	public BackupModel PrepareBackup(List<EventModel> events, boolean includeSettings, String userName) throws JSONException, ParseException {
		JSONObject jsonData = new JSONObject();
		jsonData.put(BackupService.DB, BackupService.eventsToJSONArray(events));
		if (includeSettings) {
			IPreferencesRepository prefs = PreferencesRepository.getInstance();
			jsonData.put(BackupService.SETTINGS, prefs.ToJSONObject());
		}
		if (userName != null) {
			jsonData.put(BackupService.USERNAME, userName);
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

	public void RestoreBackup(BackupModel backupModel) throws InvalidBackupException, ParseException, JSONException {
		if (Calendar.getInstance().before(DateTimeHelper.parseUtc(backupModel.Timestamp))) {
			throw new InvalidBackupException();
		}
		String decodedBackup = new String(Base64.decode(backupModel.Backup, Base64.NO_WRAP));
		if (!Utils.Sha512(decodedBackup).equals(backupModel.Digest)) {
			throw new InvalidBackupException();
		}
		JSONObject backup = new JSONObject(decodedBackup);
		if (!backup.has(BackupService.DB)) {
			throw new InvalidBackupException();
		}

		JSONArray eventsJsonArray = backup.getJSONArray(BackupService.DB);
		List<EventModel> events = new ArrayList<>();
		for (int i = 0; i < eventsJsonArray.length(); i++) {
			events.add(EventModel.FromJSONObject(eventsJsonArray.getJSONObject(i)));
		}
		DatabaseHelper.Restore(events);
		if (backup.has(BackupService.SETTINGS)) {
			PreferencesRepository.getInstance().FromJSONObject(backup.getJSONObject(BackupService.SETTINGS));
		}
	}

	public boolean CreateBackup(BackupModel model) {
		if (this._backupRepository.getById(model.Digest) == null) {
			this._backupRepository.insert(model);
			return true;
		}
		return false;
	}

	public boolean DeleteBackup(String digest) {
		BackupModel model = this._backupRepository.getById(digest);
		if (model != null) {
			this._backupRepository.delete(model);
			return true;
		}
		return false;
	}

	public BackupModel GetByDigest(String digest) {
		return this._backupRepository.getById(digest);
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
