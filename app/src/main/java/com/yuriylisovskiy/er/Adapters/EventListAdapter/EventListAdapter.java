package com.yuriylisovskiy.er.Adapters.EventListAdapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.R;

import java.util.List;

public class EventListAdapter extends ArrayAdapter<EventModel> {

	private Context context;
	private int layoutResourceId;
	private List<EventModel> data;

	public EventListAdapter(Context context, int layoutResourceId, List<EventModel> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		View row = convertView;
		EventItemHolder holder;
		if (row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new EventItemHolder();
			holder.eventTime = row.findViewById(R.id.event_time);
			holder.eventTitle = row.findViewById(R.id.event_title);

			row.setTag(holder);
		} else {
			holder = (EventItemHolder)row.getTag();
		}
		EventModel eventItem = data.get(position);
		holder.eventTime.setText(eventItem.Time);
		holder.eventTitle.setText(eventItem.Title);

		return row;
	}

	static class EventItemHolder
	{
		TextView eventTime;
		TextView eventTitle;
	}

}
