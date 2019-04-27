package com.yuriylisovskiy.er;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Switch;
import android.widget.TextView;

import com.yuriylisovskiy.er.AbstractActivities.BaseActivity;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Util.LocaleHelper;
import com.yuriylisovskiy.er.Util.ThemeHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends BaseActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private CalendarView _calendar;
	private SimpleDateFormat _sdf;

	@Override
	protected void initialSetup() {
		Context ctx = this.getApplicationContext();

		this.prefs.Initialize(ctx);
		ClientService.getInstance().Initialize(ctx);

		this._sdf = new SimpleDateFormat("dd/MM/yyyy", prefs.locale());

		LocaleHelper.Initialize(this.prefs);
		LocaleHelper.setLocale(MainActivity.this, this.prefs.lang());
	}

	protected void initLayouts() {
		this.activityView = R.layout.activity_main;
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

		final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
		final TextView selectedDate = this.findViewById(R.id.selected_date_label);
		final Calendar calendarInstance = Calendar.getInstance();
		selectedDate.setText(format.format(calendarInstance.getTime()));

		this._calendar = findViewById(R.id.calendar);
		this._calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
			calendarInstance.set(year, month, dayOfMonth);
			selectedDate.setText(format.format(calendarInstance.getTime()));
		});
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
			this._calendar.setDate(Calendar.getInstance().getTimeInMillis(), true, true);
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
}
