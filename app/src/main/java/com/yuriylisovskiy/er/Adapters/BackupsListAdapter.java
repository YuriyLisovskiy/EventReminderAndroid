package com.yuriylisovskiy.er.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuriylisovskiy.er.DataAccess.PreferencesDefaults;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Util.DateTimeHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class BackupsListAdapter extends ArrayAdapter<BackupsListAdapter.BackupItem> {

	private Context _context;
	private int _layoutResourceId;
	private String _lang;
	private List<BackupsListAdapter.BackupItem> _data;

	public BackupsListAdapter(Context context, int layoutResourceId, List<BackupsListAdapter.BackupItem> data, String lang) {
		super(context, layoutResourceId, data);
		this._layoutResourceId = layoutResourceId;
		this._context = context;
		this._data = data;
		this._lang = lang;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		View row = convertView;
		BackupItemHolder holder;
		if (row == null) {
			LayoutInflater inflater = ((Activity) this._context).getLayoutInflater();
			row = inflater.inflate(this._layoutResourceId, parent, false);

			holder = new BackupItemHolder();
			holder.backupTimestamp = row.findViewById(R.id.backup_timestamp);
			holder.backupDigest = row.findViewById(R.id.backup_digest);
			holder.backupSize = row.findViewById(R.id.backup_size);
			holder.eventsAmount = row.findViewById(R.id.backup_events_amount);
			holder.containsSettings = row.findViewById(R.id.backup_contains_settings);

			row.setTag(holder);
		} else {
			holder = (BackupItemHolder)row.getTag();
		}
		BackupsListAdapter.BackupItem backupItem = this._data.get(position);
		try {
			Calendar timestamp = DateTimeHelper.parseUtc(backupItem.Timestamp);
			holder.backupTimestamp.setText(DateTimeHelper.format(timestamp.getTimeInMillis(), DateTimeHelper.NORMAL));
		} catch (ParseException e) {
			e.printStackTrace();
			holder.backupTimestamp.setText(backupItem.Timestamp);
		}
		holder.backupDigest.setText(backupItem.Digest);
		holder.backupSize.setText(backupItem.Size);
		holder.eventsAmount.setText(this.getEventsCountStringResource(backupItem.EventsAmount));
		if (backupItem.ContainsSettings) {
			holder.containsSettings.setText(R.string.backup_and_restore_full_backup);
		} else {
			holder.containsSettings.setText(R.string.backup_and_restore_excluded_settings);
		}
		return row;
	}

	private String getEventsCountStringResource(int eventsCount) {
		String ending = "";
		switch (this._lang) {
			case PreferencesDefaults.UK_UA:
				String numStr = String.valueOf(eventsCount);
				int lastNum = Integer.parseInt(String.valueOf(numStr.charAt(numStr.length() - 1)));
				if (numStr.length() > 1 && Integer.parseInt(String.valueOf(numStr.charAt(numStr.length() - 2))) == 1) {
					ending = "й";
				} else if (lastNum == 1) {
					ending = "я";
				} else if (lastNum > 1 && lastNum < 5) {
					ending = "ї";
				} else {
					ending = "й";
				}
				break;
			default:
				if (eventsCount != 1) {
					ending = "s";
				}
		}
		return this._context.getString(R.string.backup_and_restore_events_count, eventsCount, ending);
	}

	static class BackupItemHolder {
		TextView backupTimestamp;
		TextView backupDigest;
		TextView backupSize;
		TextView eventsAmount;
		TextView containsSettings;
	}

	public static class BackupItem {
		String Digest;
		String Timestamp;
		int EventsAmount;
		String Size;
		boolean ContainsSettings;

		public BackupItem(String digest, String timestamp, int eventsAmount, String size, boolean containsSettings) {
			this.Digest = digest;
			this.Timestamp = timestamp;
			this.EventsAmount = eventsAmount;
			this.Size = size;
			this.ContainsSettings = containsSettings;
		}
	}
}
