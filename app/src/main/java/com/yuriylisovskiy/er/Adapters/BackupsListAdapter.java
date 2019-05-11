package com.yuriylisovskiy.er.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Util.DateTimeHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class BackupsListAdapter extends ArrayAdapter<BackupsListAdapter.BackupItem> {

	private Context context;
	private int layoutResourceId;
	private List<BackupsListAdapter.BackupItem> data;

	public BackupsListAdapter(Context context, int layoutResourceId, List<BackupsListAdapter.BackupItem> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		View row = convertView;
		BackupItemHolder holder;
		if (row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

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
		BackupsListAdapter.BackupItem backupItem = data.get(position);
		try {
			Calendar timestamp = DateTimeHelper.parseUtc(backupItem.Timestamp);
			holder.backupTimestamp.setText(DateTimeHelper.format(timestamp.getTimeInMillis(), DateTimeHelper.NORMAL));
		} catch (ParseException e) {
			e.printStackTrace();
			holder.backupTimestamp.setText(backupItem.Timestamp);
		}
		holder.backupDigest.setText(backupItem.Digest);
		holder.backupSize.setText(backupItem.Size);
		holder.eventsAmount.setText(context.getString(
			R.string.backup_and_restore_events_amount, backupItem.EventsAmount
		));
		if (backupItem.ContainsSettings) {
			holder.containsSettings.setText(R.string.backup_and_restore_full_backup);
		} else {
			holder.containsSettings.setText(R.string.backup_and_restore_excluded_settings);
		}
		return row;
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
