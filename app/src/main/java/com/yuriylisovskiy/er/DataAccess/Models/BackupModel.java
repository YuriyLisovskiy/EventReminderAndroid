package com.yuriylisovskiy.er.DataAccess.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.yuriylisovskiy.er.Adapters.BackupsListAdapter;
import com.yuriylisovskiy.er.Util.DateTimeHelper;
import com.yuriylisovskiy.er.Util.Names;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = Names.BACKUPS)
public class BackupModel {

	@NonNull
	@PrimaryKey
	public String Digest = "";

	public String Timestamp;

	public String Backup;

	@ColumnInfo(name = Names.EVENTS_AMOUNT)
	public int EventsAmount;

	public String Size;

	@ColumnInfo(name = Names.CONTAINS_SETTINGS)
	public boolean ContainsSettings;

	private void init(String digest, String timestamp, String backup, int eventsAmount, String size, boolean containsSettings) {
		this.Digest = digest;
		this.Timestamp = timestamp;
		this.Backup = backup;
		this.EventsAmount = eventsAmount;
		this.Size = size;
		this.ContainsSettings = containsSettings;
	}

	public BackupModel() {}

	public BackupModel(String digest, long timestamp, String backup, int eventsAmount, String size, boolean containsSettings) {
		this.init(digest, DateTimeHelper.formatUtc(timestamp), backup, eventsAmount, size, containsSettings);
	}

	public BackupModel(String digest, String timestamp, String backup, int eventsAmount, String size, boolean containsSettings) {
		this.init(digest, timestamp, backup, eventsAmount, size, containsSettings);
	}

	public static BackupModel FromJSONObject(JSONObject object) throws JSONException {
		return new BackupModel(
			object.getString(Names.DIGEST),
			object.getString(Names.TIMESTAMP),
			object.getString(Names.BACKUP),
			object.getInt(Names.EVENTS_COUNT),
			object.getString(Names.BACKUP_SIZE),
			object.getBoolean(Names.CONTAINS_SETTINGS)
		);
	}

	public BackupsListAdapter.BackupItem ToBackupItem() {
		return new BackupsListAdapter.BackupItem(
			this.Digest, this.Timestamp, this.EventsAmount, this.Size, this.ContainsSettings
		);
	}
}
