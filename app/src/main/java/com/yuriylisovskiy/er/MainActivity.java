package com.yuriylisovskiy.er;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.events.calendar.views.EventsCalendar;
import com.yuriylisovskiy.er.AbstractActivities.BaseActivity;
import com.yuriylisovskiy.er.Adapters.EventListAdapter;
import com.yuriylisovskiy.er.DataAccess.DatabaseHelper;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;
import com.yuriylisovskiy.er.Services.EventService.EventService;
import com.yuriylisovskiy.er.Services.EventService.IEventService;
import com.yuriylisovskiy.er.Util.DateTimeHelper;
import com.yuriylisovskiy.er.Util.Globals;
import com.yuriylisovskiy.er.Util.LocaleHelper;
import com.yuriylisovskiy.er.Util.ThemeHelper;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private EventsCalendar _calendar;
	private SimpleDateFormat _sdf;

	private ListView _eventListView;
	private Calendar _selectedDate;

	private Dialog _eventDetailsDialog;
	private Dialog _aboutDialog;

	private Long _selectedEvent;
	private int _selectedEventPosition;

	private AsyncTask<Void, Void, List<EventModel>> _eventModelListTask;
	private AsyncTask<Void, Void, String> _strTask;
	private AsyncTask<Void, Void, EventModel> _eventModelTask;

	@Override
	protected void initialSetup() {
		Context ctx = this.getApplicationContext();

		this.prefs.Initialize(ctx);
		ClientService.getInstance().Initialize(ctx);

		DatabaseHelper.Initialize(ctx, Globals.APP_DB_NAME);

		this._sdf = new SimpleDateFormat(DateTimeHelper.DATE_FORMAT, prefs.locale());

		LocaleHelper.Initialize(this.prefs);
		LocaleHelper.setLocale(MainActivity.this, this.prefs.lang());
	}

	protected void initLayouts() {
		this.activityView = R.layout.activity_main;
		this.progressBarLayout = R.id.progress;
	}

	@Override
	protected void onCreate() {
		this._eventDetailsDialog = new Dialog(this);
		this._aboutDialog = this.buildAboutDialog();
		this._eventListView = findViewById(R.id.event_list);
		this._eventListView.setOnItemClickListener((parent, view, position, arg3) -> {
			this._selectedEvent = Long.valueOf(((TextView) view.findViewById(R.id.event_id)).getText().toString());
			this._selectedEventPosition = position;
		});
		this._eventListView.setOnItemLongClickListener((parent, view, position, id) -> {
			this._selectedEvent = Long.valueOf(((TextView) view.findViewById(R.id.event_id)).getText().toString());
			this._selectedEventPosition = position;
			this._eventModelTask = new ShowEventDetailsTask(this, this._selectedEvent);
			this._eventModelTask.execute((Void) null);
			return true;
		});
		BottomNavigationView bottomNavigationView = this.findViewById(R.id.event_managing);
		bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
			switch (menuItem.getItemId()) {
				case R.id.action_event_add:
					Intent createActivity = new Intent(MainActivity.this, EventActivity.class);
					createActivity.putExtra(Globals.EVENT_ACTIVITY_TITLE_EXTRA, getString(R.string.create));
					createActivity.putExtra(Globals.SELECTED_DATE_EXTRA, this._selectedDate);
					this.startActivity(createActivity);
					break;
				case R.id.action_event_edit:
					if (this._selectedEvent != null) {
						Intent editActivity = new Intent(MainActivity.this, EventActivity.class);
						editActivity.putExtra(Globals.EVENT_ACTIVITY_TITLE_EXTRA, getString(R.string.edit));
						editActivity.putExtra(Globals.EVENT_ID_EXTRA, this._selectedEvent);
						editActivity.putExtra(Globals.IS_EDITING_EXTRA, true);
						this.startActivity(editActivity);
					} else {
						Toast.makeText(getBaseContext(), R.string.no_event_to_edit, Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.action_event_remove:
					if (this._selectedEvent != null) {
						AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
						adb.setTitle(getString(R.string.delete) + "?");
						adb.setMessage(R.string.event_delete_confirmation);
						adb.setNegativeButton(R.string.cancel, null);
						adb.setPositiveButton("Ok", (dialog, which) -> {
							this._strTask = new DeleteEventTask(this, this._selectedEvent);
							this._strTask.execute((Void) null);
						});
						adb.show();
					} else {
						Toast.makeText(getBaseContext(), R.string.no_event_to_delete, Toast.LENGTH_SHORT).show();
					}
					break;
			}
			return false;
		});
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
		);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = this.findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		final Switch switchItem = (Switch) navigationView.getMenu().findItem(R.id.nav_switch).getActionView();
		switchItem.setChecked(this.prefs.idDarkTheme());
		switchItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
			this.setNewTheme(isChecked);
			this.prefs.setIsDarkTheme(isChecked);
		});

		this._calendar = findViewById(R.id.calendar);
		this._calendar.setWeekStartDay(Calendar.MONDAY, false);

		Calendar start = Calendar.getInstance();
		start.set(1900, 1, 1);

		Calendar end = Calendar.getInstance();
		end.set(2100, 12, 31);

		this._selectedDate = Calendar.getInstance();

		this._calendar.setMonthRange(start, end);
		this._calendar.setSelectionMode(this._calendar.getSINGLE_SELECTION());
		this._calendar.setToday(this._selectedDate);
		this._calendar.setCallback(new EventsCalendar.Callback() {
			@Override
			public void onDaySelected(@Nullable Calendar calendar) {
				if (calendar != null) {
					_selectedEvent = null;
					_selectedDate = calendar;
					loadEvents();
				}
			}

			@Override
			public void onDayLongPressed(@Nullable Calendar calendar) {

			}

			@Override
			public void onMonthChanged(@Nullable Calendar calendar) {

			}
		});
		new GetEventsTask(this, null).execute((Void) null);
	}

	@Override
	protected void onResume() {
		_selectedEvent = null;
		this.loadEvents();
		super.onResume();
	}

	private void loadEvents() {
		this._eventModelListTask = new GetEventsTask(this, this._selectedDate.getTime());
		this._eventModelListTask.execute((Void) null);
	}

	private void addEventsToCalendar(List<Calendar> calendars) {
		for (Calendar calendar : calendars) {
			this._calendar.addEvent(calendar);
		}
		this._calendar.postInvalidate();
	}

	private void setNewTheme(boolean isChecked) {
		ThemeHelper.setTheme(isChecked);
		ThemeHelper.changeTheme(this);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_now).setTitle(
			this._sdf.format(Calendar.getInstance().getTime())
		);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_now) {
			this._calendar.setCurrentSelectedDate(Calendar.getInstance());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.nav_calendar:
				break;
			case R.id.nav_settings:
				this.startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.nav_backup_and_restore:
				// TODO: Handle the backup and restore action
				break;
			case R.id.nav_account:
				this.startActivity(new Intent(this, AccountActivity.class));
				break;
			case R.id.nav_about:
				this.showAboutDialog();
				break;
			case R.id.nav_switch:
				return true;
		}

		DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void showEventDetails(EventModel model) {
		this._eventDetailsDialog.setContentView(R.layout.event_details);
		TextView titleView = this._eventDetailsDialog.findViewById(R.id.event_details_title);
		titleView.setText(model.Title);
		TextView descriptionView = this._eventDetailsDialog.findViewById(R.id.event_details_description);
		descriptionView.setText(model.Description);
		TextView dateView = this._eventDetailsDialog.findViewById(R.id.event_details_date);
		dateView.setText(model.Date);
		TextView timeView = this._eventDetailsDialog.findViewById(R.id.event_details_time);
		timeView.setText(model.Time);
		this._eventDetailsDialog.findViewById(R.id.event_details_dismiss).setOnClickListener(
			v -> _eventDetailsDialog.dismiss()
		);
		Objects.requireNonNull(
			this._eventDetailsDialog.getWindow()
		).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this._eventDetailsDialog.show();
	}

	private Dialog buildAboutDialog() {
		Dialog aboutDialog = new Dialog(this);
		aboutDialog.setContentView(R.layout.app_about);

		TextView nameView = aboutDialog.findViewById(R.id.app_about_name);
		nameView.setText(BuildConfig.APP_NAME);

		TextView versionNumberView = aboutDialog.findViewById(R.id.app_about_version_number);
		versionNumberView.setText(this.getString(R.string.app_version_number, BuildConfig.VERSION_NAME));

		TextView buildDateView = aboutDialog.findViewById(R.id.app_about_build_date);
		buildDateView.setText(this.getString(R.string.app_build_date, BuildConfig.BUILD_DATE));

		TextView copyrightView = aboutDialog.findViewById(R.id.app_about_copyright);
		copyrightView.setText(BuildConfig.COPYRIGHT);

		aboutDialog.findViewById(R.id.app_about_name).setOnClickListener(
			v -> _aboutDialog.dismiss()
		);
		Objects.requireNonNull(
				aboutDialog.getWindow()
		).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		return aboutDialog;
	}

	private void showAboutDialog() {
		this._strTask = new LoadDistroUserTask(this);
		this._strTask.execute((Void) null);
		this._aboutDialog.show();
	}

	private static class GetEventsTask extends AsyncTask<Void, Void, List<EventModel>> {

		private WeakReference<MainActivity> _cls;
		private Date _searchDate;

		GetEventsTask(MainActivity cls, Date searchDate) {
			this._cls = new WeakReference<>(cls);
			this._searchDate = searchDate;
		}

		@Override
		protected List<EventModel> doInBackground(Void... params) {
			List<EventModel> events = null;
			try {
				IEventService service = new EventService();
				if (this._searchDate != null) {
					events = service.GetByDate(this._searchDate);
				} else {
					events = service.GetAll();
				}
			} catch (Exception exc) {
				Log.e("GetEventsTask:DIB", exc.getMessage());
			}
			return events;
		}

		@Override
		protected void onPostExecute(final List<EventModel> events) {
			if (events != null) {
				if (this._searchDate != null) {
					EventListAdapter adapter = new EventListAdapter(
							this._cls.get(), R.layout.event_list_item, events
					);
					this._cls.get()._eventListView.setAdapter(adapter);
				}
				if (this._searchDate == null) {
					List<Calendar> calendars = new ArrayList<>();
					for (EventModel event : events) {
						try {
							calendars.add(DateTimeHelper.dateFromString(event.Date));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					this._cls.get().addEventsToCalendar(calendars);
				}
			} else {
				Log.e("GetEventsTask:OPE", "Events are null");
			}
			this._cls.get()._eventModelListTask = null;
		}

		@Override
		protected void onCancelled() {
			this._cls.get()._eventModelListTask = null;
		}
	}

	private static class DeleteEventTask extends AsyncTask<Void, Void, String> {

		private WeakReference<MainActivity> _cls;
		private long _eventId;

		DeleteEventTask(MainActivity cls, long eventId) {
			this._cls = new WeakReference<>(cls);
			this._eventId = eventId;
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				IEventService service = new EventService();
				service.DeleteById(this._eventId);
			} catch (Exception exc) {
				result = exc.getMessage();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
			if (result != null) {
				Log.e("DeleteEventTask:OPE", result);
			} else {
				EventListAdapter adapter = (EventListAdapter) this._cls.get()._eventListView.getAdapter();
				adapter.remove(adapter.getItem(this._cls.get()._selectedEventPosition));
				adapter.notifyDataSetChanged();
			}
			this._cls.get()._strTask = null;
		}

		@Override
		protected void onCancelled() {
			this._cls.get()._strTask = null;
		}
	}

	private static class ShowEventDetailsTask extends AsyncTask<Void, Void, EventModel> {

		private WeakReference<MainActivity> _cls;
		private long _eventId;

		ShowEventDetailsTask(MainActivity cls, long eventId) {
			this._cls = new WeakReference<>(cls);
			this._eventId = eventId;
		}

		@Override
		protected EventModel doInBackground(Void... params) {
			EventModel result = null;
			try {
				IEventService service = new EventService();
				result = service.GetById(this._eventId);
			} catch (Exception exc) {
				Log.e("EventDetailsTask:DIB", exc.getMessage());
			}
			return result;
		}

		@Override
		protected void onPostExecute(final EventModel result) {
			if (result != null) {
				this._cls.get().showEventDetails(result);
			}
			this._cls.get()._eventModelTask = null;
		}

		@Override
		protected void onCancelled() {
			this._cls.get()._eventModelTask = null;
		}
	}

	private static class LoadDistroUserTask extends AsyncTask<Void, Void, String> {

		private final static IClientService _client = ClientService.getInstance();
		private WeakReference<MainActivity> _cls;

		LoadDistroUserTask(MainActivity cls) {
			this._cls = new WeakReference<>(cls);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				JSONObject user = LoadDistroUserTask._client.User();
				result = user.getString("username");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RequestError e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
			TextView info = this._cls.get()._aboutDialog.findViewById(R.id.app_about_info);
			if (result != null) {
				info.setVisibility(View.VISIBLE);
				info.setText(this._cls.get().getString(R.string.app_distro_user_info, result));
			} else {
				info.setVisibility(View.GONE);
			}
			this._cls.get()._strTask = null;
		}

		@Override
		protected void onCancelled() {
			this._cls.get()._strTask = null;
		}
	}
}
