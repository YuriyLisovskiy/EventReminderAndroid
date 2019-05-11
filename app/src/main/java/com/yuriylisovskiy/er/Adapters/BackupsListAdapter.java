package com.yuriylisovskiy.er.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuriylisovskiy.er.Adapters.Models.BackupModel;
import com.yuriylisovskiy.er.R;

import java.util.List;

public class BackupsListAdapter extends ArrayAdapter<BackupModel> {

	private Context context;
	private int layoutResourceId;
	private List<BackupModel> data;

	public BackupsListAdapter(Context context, int layoutResourceId, List<BackupModel> data) {
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

			row.setTag(holder);
		} else {
			holder = (BackupItemHolder)row.getTag();
		}
		BackupModel backupItem = data.get(position);
		holder.backupTimestamp.setText(String.valueOf(backupItem.Timestamp));
		holder.backupDigest.setText(backupItem.Digest);
		return row;
	}

	static class BackupItemHolder
	{
		TextView backupTimestamp;
		TextView backupDigest;
	}

}
