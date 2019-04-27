package com.yuriylisovskiy.er;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.yuriylisovskiy.er.AbstractActivities.ChildActivity;

import java.util.Calendar;

public class EventActivity extends ChildActivity {

	private Calendar _dateAndTime = Calendar.getInstance();
	private TextView _eventTimeLabel;
	private TextView _eventDateLabel;

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_event;
		this.progressBarLayout = R.id.event_progress;
	}

	@Override
	protected void onCreate() {
		this.setTitle(getString(R.string.title_activity_event, getIntent().getStringExtra("title_parameter")));
		this.initDateTimeDialogs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.event_save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_done) {
			this.processSaveEvent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initDateTimeDialogs() {
		TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
			_dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			_dateAndTime.set(Calendar.MINUTE, minute);
			setInitialDateTime();
		};

		DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
			_dateAndTime.set(Calendar.YEAR, year);
			_dateAndTime.set(Calendar.MONTH, monthOfYear);
			_dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			setInitialDateTime();
		};

		this._eventDateLabel = findViewById(R.id.event_date);
		this._eventDateLabel.setOnClickListener(v -> new DatePickerDialog(
						EventActivity.this,
						dateSetListener,
						_dateAndTime.get(Calendar.YEAR),
						_dateAndTime.get(Calendar.MONTH),
						_dateAndTime.get(Calendar.DAY_OF_MONTH)
				).show()
		);
		this._eventTimeLabel = findViewById(R.id.event_time);
		this._eventTimeLabel.setOnClickListener(v -> new TimePickerDialog(
						EventActivity.this,
						timeSetListener,
						_dateAndTime.get(Calendar.HOUR_OF_DAY),
						_dateAndTime.get(Calendar.MINUTE), true
				).show()
		);
		setInitialDateTime();
	}

	private void setInitialDateTime() {

		long timeInMillis = this._dateAndTime.getTimeInMillis();
		this._eventDateLabel.setText(
			DateUtils.formatDateTime(
				this,
				timeInMillis,
				DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
			)
		);
		this._eventTimeLabel.setText(
			DateUtils.formatDateTime(
				this,
				timeInMillis,
				DateUtils.FORMAT_SHOW_TIME
			)
		);
	}

	private void processSaveEvent() {

	}
}
