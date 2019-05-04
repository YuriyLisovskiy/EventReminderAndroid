package com.yuriylisovskiy.er;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Switch;

import com.events.calendar.views.EventsCalendar;
import com.yuriylisovskiy.er.AbstractActivities.BaseActivity;
import com.yuriylisovskiy.er.Adapters.EventListAdapter;
import com.yuriylisovskiy.er.DataAccess.DatabaseHelper;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Services.EventService.EventService;
import com.yuriylisovskiy.er.Services.EventService.IEventService;
import com.yuriylisovskiy.er.Util.DateTimeHelper;
import com.yuriylisovskiy.er.Util.LocaleHelper;
import com.yuriylisovskiy.er.Util.ThemeHelper;

import org.jetbrains.annotations.Nullable;

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

	@Override
	protected void initialSetup() {
		Context ctx = this.getApplicationContext();

		this.prefs.Initialize(ctx);
		ClientService.getInstance().Initialize(ctx);

		DatabaseHelper.Initialize(ctx, "er_db");

		this._sdf = new SimpleDateFormat("dd/MM/yyyy", prefs.locale());

		LocaleHelper.Initialize(this.prefs);
		LocaleHelper.setLocale(MainActivity.this, this.prefs.lang());
	}

	protected void initLayouts() {
		this.activityView = R.layout.activity_main;
		this.progressBarLayout = R.id.progress;
	}

	@Override
	protected void onCreate() {
		FloatingActionButton fab = this.findViewById(R.id.fab);
		fab.setOnClickListener(sa -> {
			Intent eventActivity = new Intent(MainActivity.this, EventActivity.class);
			eventActivity.putExtra("title_parameter", getString(R.string.create));
			this.startActivity(eventActivity);
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

		this._calendar.setMonthRange(start, end);
		this._calendar.setSelectionMode(this._calendar.getSINGLE_SELECTION());
		this._calendar.setToday(Calendar.getInstance());
		this._calendar.setCallback(new EventsCalendar.Callback() {
			@Override
			public void onDaySelected(@Nullable Calendar calendar) {
				if (calendar != null) {
					loadEvents(calendar);
				}
			}

			@Override
			public void onDayLongPressed(@Nullable Calendar calendar) {

			}

			@Override
			public void onMonthChanged(@Nullable Calendar calendar) {

			}
		});
		this._eventListView = findViewById(R.id.event_list);
	}

	@Override
	protected void onResume() {
		this.loadEvents(Objects.requireNonNull(this._calendar.getCurrentSelectedDate()));
		super.onResume();
	}

	private void loadEvents(Calendar calendar) {
		new GetEventsTask(this, calendar.getTime()).execute((Void) null);
	}

	private void addEventsToCalendar(List<Calendar> calendars) {
		for (Calendar calendar : calendars) {
			this._calendar.addEvent(calendar);
		}
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
				// TODO: Handle the calendar action
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
				// TODO: Handle the about action
				break;
			case R.id.nav_switch:
				return true;
		}

		DrawerLayout drawer = this.findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
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
				events = service.GetByDate(this._searchDate);
			} catch (Exception exc) {
				Log.e("GetEventsTask:DIB", exc.getMessage());
			}
			return events;
		}

		@Override
		protected void onPostExecute(final List<EventModel> events) {
			if (events != null) {
				EventListAdapter adapter = new EventListAdapter(
					this._cls.get(), R.layout.event_list_item, events
				);
				this._cls.get()._eventListView.setAdapter(adapter);
				List<Calendar> calendars = new ArrayList<>();
				for (EventModel event : events) {
					try {
						calendars.add(DateTimeHelper.fromString(event.Date));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				this._cls.get().addEventsToCalendar(calendars);
			} else {
				Log.e("GetEventsTask:OPE", "Events are null");
			}
		}
	}
}
