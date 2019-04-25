package com.yuriylisovskiy.er;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class EventActivity extends BaseActivity {

	private Calendar _dateAndTime = Calendar.getInstance();
	private TextView _eventTimeLabel;
	private TextView _eventDateLabel;
	TimePickerDialog.OnTimeSetListener timeSetListener;
	DatePickerDialog.OnDateSetListener dateSetListener;

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_event;
		this.progressBarLayout = R.id.event_progress_bar;
	}

	@Override
	protected void onCreate() {
		this.timeSetListener = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				_dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				_dateAndTime.set(Calendar.MINUTE, minute);
				setInitialDateTime();
			}
		};

		this.dateSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				_dateAndTime.set(Calendar.YEAR, year);
				_dateAndTime.set(Calendar.MONTH, monthOfYear);
				_dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				setInitialDateTime();
			}
		};

		this._eventDateLabel = findViewById(R.id.event_date);
		this._eventTimeLabel = findViewById(R.id.event_time);
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

	public void setDate(View v) {
		new DatePickerDialog(EventActivity.this, this.dateSetListener,
				_dateAndTime.get(Calendar.YEAR),
				_dateAndTime.get(Calendar.MONTH),
				_dateAndTime.get(Calendar.DAY_OF_MONTH))
				.show();
	}

	public void setTime(View v) {
		new TimePickerDialog(EventActivity.this, this.timeSetListener,
				_dateAndTime.get(Calendar.HOUR_OF_DAY),
				_dateAndTime.get(Calendar.MINUTE), true)
				.show();
	}

}
