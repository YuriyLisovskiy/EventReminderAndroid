package com.yuriylisovskiy.er.Services.BackupService;

import com.yuriylisovskiy.er.DataAccess.Models.BackupModel;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;

import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

public interface IBackupService {

	BackupModel PrepareBackup(List<EventModel> events, boolean includeSettings, String userName) throws JSONException, ParseException;
	void CreateBackup(BackupModel model);
	void DeleteBackup(String digest);
	List<BackupModel> GetAll();
}
