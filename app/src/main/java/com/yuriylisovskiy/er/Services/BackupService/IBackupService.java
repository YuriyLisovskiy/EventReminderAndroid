package com.yuriylisovskiy.er.Services.BackupService;

import com.yuriylisovskiy.er.DataAccess.Models.BackupModel;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.Services.BackupService.Exceptions.InvalidBackupException;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

public interface IBackupService {

	BackupModel PrepareBackup(List<EventModel> events, boolean includeSettings, String userName) throws JSONException, ParseException;
	boolean CreateBackup(BackupModel model);
	boolean DeleteBackup(String digest);
	void RestoreBackup(BackupModel backupModel) throws InvalidBackupException, ParseException, JSONException;
	BackupModel GetByDigest(String digest);
	List<BackupModel> GetAll();
}
