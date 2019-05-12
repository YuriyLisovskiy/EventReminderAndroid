package com.yuriylisovskiy.er.DataAccess.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.yuriylisovskiy.er.Adapters.BackupsListAdapter;
import com.yuriylisovskiy.er.Util.DateTimeHelper;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "backups")
public class BackupModel {

	@NonNull
	@PrimaryKey
	public String Digest = "";

	public String Timestamp;

	public String Backup;

	@ColumnInfo(name = "events_amount")
	public int EventsAmount;

	public String Size;

	@ColumnInfo(name = "contains_settings")
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

	public JSONObject ToJSONObject() throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("digest", this.Digest);
		jsonData.put("timestamp", this.Timestamp);
		jsonData.put("backup", this.Backup);
		jsonData.put("events_count", this.EventsAmount);
		jsonData.put("size", this.Size);
		jsonData.put("contains_settings", this.ContainsSettings);
		return jsonData;
	}

	public static BackupModel FromJSONObject(JSONObject object) throws JSONException {
		return new BackupModel(
			object.getString("digest"),
			object.getString("timestamp"),
			object.getString("backup"),
			object.getInt("events_count"),
			object.getString("backup_size"),
			object.getBoolean("contains_settings")
		);
	}

	public BackupsListAdapter.BackupItem ToBackupItem() {
		return new BackupsListAdapter.BackupItem(
			this.Digest, this.Timestamp, this.EventsAmount, this.Size, this.ContainsSettings
		);
	}
}
