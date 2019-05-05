package com.yuriylisovskiy.er;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuriylisovskiy.er.AbstractActivities.ChildActivity;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.Services.EventService.EventService;
import com.yuriylisovskiy.er.Services.EventService.IEventService;
import com.yuriylisovskiy.er.Util.DateTimeHelper;
import com.yuriylisovskiy.er.Util.Globals;
import com.yuriylisovskiy.er.Util.InputValidator;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.Calendar;

// TODO: date and time is not set when editing!
public class EventActivity extends ChildActivity {

	private Calendar _currentDate;
	private Calendar _currentTime;
	private TextView _eventTimeLabel;
	private TextView _eventDateLabel;
	private ScrollView _eventForm;

	private EditText _titleInput;
	private EditText _descriptionInput;
	private CheckBox _repeatWeeklyInput;

	private boolean _isEditing;

	private EventModel _eventModel;

	private AsyncTask<Void, Void, String> _task;

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_event;
		this.progressBarLayout = R.id.event_progress;
	}

	@Override
	protected void onCreate() {
		this._eventForm = this.findViewById(R.id.event_form);
		Intent intent = this.getIntent();
		this.setTitle(this.getString(
			R.string.title_activity_event, intent.getStringExtra(Globals.EVENT_ACTIVITY_TITLE_EXTRA)
		));
		this._titleInput = this._eventForm.findViewById(R.id.title);
		this._descriptionInput = this._eventForm.findViewById(R.id.description);
		this._repeatWeeklyInput = this._eventForm.findViewById(R.id.repeat_weekly);
		this._isEditing = intent.getBooleanExtra(Globals.IS_EDITING_EXTRA, false);
		if (this._isEditing) {
			new EventActivity.InitFormTask(
				this, (long) intent.getSerializableExtra(Globals.EVENT_ID_EXTRA)
			).execute((Void) null);
		} else {
			this._eventModel = new EventModel();
			this.setDefaultDateTime();
			Calendar selectedDate = (Calendar)intent.getSerializableExtra(Globals.SELECTED_DATE_EXTRA);
			if (!DateTimeHelper.isToday(selectedDate.getTime()) && System.currentTimeMillis() <= selectedDate.getTimeInMillis()) {
				this._currentDate = selectedDate;
			}
			this.initDateTimeDialogs();
		}
	}

	private void setDefaultDateTime() {
		Calendar calendar = Calendar.getInstance();
		this._currentDate = calendar;
		this._currentTime = calendar;
		this._currentTime.add(Calendar.MINUTE, 3);
	}

	private void initForm(EventModel model) {
		this._eventModel = model;
		this._titleInput.setText(this._eventModel.Title);
		this._descriptionInput.setText(this._eventModel.Description);
		this._repeatWeeklyInput.setChecked(this._eventModel.RepeatWeekly);
		try {
			this._currentDate = DateTimeHelper.dateFromString(this._eventModel.Date);
			this._currentTime = DateTimeHelper.timeFromString(this._eventModel.Time);
		} catch (ParseException e) {
			e.printStackTrace();
			this.setDefaultDateTime();
		}
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
			_currentTime.set(Calendar.HOUR, hourOfDay);
			_currentTime.set(Calendar.MINUTE, minute);
			setInitialDateTime();
		};

		DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
			_currentDate.set(Calendar.YEAR, year);
			_currentDate.set(Calendar.MONTH, monthOfYear);
			_currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			setInitialDateTime();
		};

		this._eventDateLabel = this._eventForm.findViewById(R.id.date);
		DatePickerDialog datePickerDialog = new DatePickerDialog(
			EventActivity.this,
			dateSetListener,
			_currentDate.get(Calendar.YEAR),
			_currentDate.get(Calendar.MONTH),
			_currentDate.get(Calendar.DAY_OF_MONTH)
		);
		datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
		this._eventDateLabel.setOnClickListener(v -> datePickerDialog.show());
		this._eventTimeLabel = this._eventForm.findViewById(R.id.time);
		this._eventTimeLabel.setOnClickListener(v -> new TimePickerDialog(
				EventActivity.this,
				timeSetListener,
				_currentTime.get(Calendar.HOUR),
				_currentTime.get(Calendar.MINUTE), true
			).show()
		);
		setInitialDateTime();
	}

	private void setInitialDateTime() {
		this._eventDateLabel.setText(
			DateUtils.formatDateTime(
				this,
				_currentDate.getTimeInMillis(),
				DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
			)
		);
		this._eventTimeLabel.setText(
			DateUtils.formatDateTime(
				this,
				_currentTime.getTimeInMillis(),
				DateUtils.FORMAT_SHOW_TIME
			)
		);
	}

	private void showProgress(final boolean show) {
		int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);
		this._eventForm.setVisibility(show ? View.GONE : View.VISIBLE);
		this._eventForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
				new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						_eventForm.setVisibility(show ? View.GONE : View.VISIBLE);
						if (!show) {
							_eventForm.requestFocus();
						}
					}
				}
		);
		if (show) {
			this.showProgressBar();
		} else {
			this.hideProgressBar();
		}
	}

	private void processSaveEvent() {
		if (this._task != null) {
			return;
		}

		this._titleInput.setError(null);
		this._descriptionInput.setError(null);

		String title = this._titleInput.getText().toString();
		String description = this._descriptionInput.getText().toString();
		Boolean repeatWeekly = this._repeatWeeklyInput.isChecked();

		boolean cancel = false;
		View focusView = null;

		if (InputValidator.isEmpty(title)) {
			this._titleInput.setError(getString(R.string.error_field_required));
			focusView = this._titleInput;
			cancel = true;
		}

		if (DateTimeHelper.isPast(this._currentTime.getTime())) {
			Toast.makeText(getBaseContext(), getString(R.string.invalid_creation_time), Toast.LENGTH_LONG).show();
			cancel = true;
		}

		if (cancel) {
			if (focusView != null) {
				focusView.requestFocus();
			}
		} else {
			this.showProgress(true);
			this._eventModel.Title = title;
			this._eventModel.Time = DateTimeHelper.formatTime(this._currentTime.getTimeInMillis());
			this._eventModel.Date = DateTimeHelper.formatDate(this._currentDate.getTimeInMillis());
			this._eventModel.Description = description;
			this._eventModel.RepeatWeekly = repeatWeekly;
			this._task = new EventActivity.SaveEventTask(this._eventModel, this._isEditing, this, this.getBaseContext());
			this._task.execute((Void) null);
		}
	}

	private static class SaveEventTask extends AsyncTask<Void, Void, String> {

		private boolean _isEditing;
		private EventModel _event;

		private WeakReference<EventActivity> _cls;
		private WeakReference<Context> _baseCtx;

		SaveEventTask(EventModel event, boolean isEditing, EventActivity cls, Context baseCtx) {
			this._isEditing = isEditing;
			this._event = event;
			this._cls = new WeakReference<>(cls);
			this._baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				IEventService service = new EventService();
				if (this._isEditing) {
					service.UpdateItem(this._event);
				} else {
					service.CreateItem(this._event);
				}
			} catch (Exception exc) {
				result = exc.getMessage();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final String resultMsg) {
			if (resultMsg != null) {
				Toast.makeText(this._baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				this._cls.get().showProgress(false);
			} else {
				int successResource;
				if (this._isEditing) {
					successResource = R.string.event_update_success;
				} else {
					successResource = R.string.event_create_success;
				}
				Toast.makeText(this._baseCtx.get(), successResource, Toast.LENGTH_SHORT).show();
				this._cls.get().onBackPressed();
			}
			this._cls.get()._task = null;
		}

		@Override
		protected void onCancelled() {
			this._cls.get()._task = null;
			this._cls.get().showProgress(false);
		}
	}

	private static class InitFormTask extends AsyncTask<Void, Void, EventModel> {

		private WeakReference<EventActivity> _cls;
		private long _eventId;

		InitFormTask(EventActivity cls, long eventId) {
			this._cls = new WeakReference<>(cls);
			this._eventId = eventId;
		}

		@Override
		protected EventModel doInBackground(Void... params) {
			EventModel event = null;
			try {
				IEventService service = new EventService();
				event = service.GetById(this._eventId);
			} catch (Exception exc) {
				Log.e("InitFormTask:DIB", exc.getMessage());
			}
			return event;
		}

		@Override
		protected void onPostExecute(final EventModel event) {
			if (event != null) {
				this._cls.get().initForm(event);
			} else {
				Log.e("InitFormTask:OPE", "Event is null");
			}
		}
	}
}
