package com.yuriylisovskiy.er;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
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
import com.yuriylisovskiy.er.Util.Globals;
import com.yuriylisovskiy.er.Util.InputValidator;

import java.lang.ref.WeakReference;
import java.util.Calendar;

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
		this._isEditing = intent.getBooleanExtra(Globals.IS_EDITING_EXTRA, false);
		if (this._isEditing) {

			this._currentDate = (Calendar)intent.getSerializableExtra(Globals.SELECTED_DATE_EXTRA);
			this._currentTime = Calendar.getInstance();
			this._currentTime.add(Calendar.MINUTE, 3);
		} else {
			this._currentDate = (Calendar)intent.getSerializableExtra(Globals.SELECTED_DATE_EXTRA);
			this._currentTime = Calendar.getInstance();
			this._currentTime.add(Calendar.MINUTE, 3);
		}
		this.initDateTimeDialogs();
		this._titleInput = this._eventForm.findViewById(R.id.title);
		this._descriptionInput = this._eventForm.findViewById(R.id.description);
		this._repeatWeeklyInput = this._eventForm.findViewById(R.id.repeat_weekly);
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
			_currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
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
		this._eventDateLabel.setOnClickListener(v -> new DatePickerDialog(
				EventActivity.this,
				dateSetListener,
				_currentDate.get(Calendar.YEAR),
				_currentDate.get(Calendar.MONTH),
				_currentDate.get(Calendar.DAY_OF_MONTH)
			).show()
		);
		this._eventTimeLabel = this._eventForm.findViewById(R.id.time);
		this._eventTimeLabel.setOnClickListener(v -> new TimePickerDialog(
				EventActivity.this,
				timeSetListener,
				_currentTime.get(Calendar.HOUR_OF_DAY),
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

		if (cancel) {
			focusView.requestFocus();
		} else {
			this.showProgress(true);
			EventModel event = new EventModel(
				title, this._currentTime.getTimeInMillis(), this._currentDate.getTimeInMillis(), description, repeatWeekly
			);
			this._task = new EventActivity.SaveEventTask(event, this._isEditing, this, this.getBaseContext());
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
				this._cls.get()._task = null;
				this._cls.get().showProgress(false);
			} else {
				int successResource;
				if (this._isEditing) {
					successResource = R.string.event_update_success;
				} else {
					successResource = R.string.event_create_success;
				}
				Toast.makeText(this._baseCtx.get(), successResource, Toast.LENGTH_LONG).show();
				this._cls.get().onBackPressed();
			}
		}

		@Override
		protected void onCancelled() {
			this._cls.get()._task = null;
			this._cls.get().showProgress(false);
		}
	}
}
