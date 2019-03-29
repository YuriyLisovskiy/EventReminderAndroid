package com.yuriylisovskiy.er;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		super.onCreate(savedInstanceState);

//		Theme.onActivityCreateSetTheme(this);

//		Switch nightModeSwitch = findViewById(R.id.nav_night_mode_switch);
//		nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				setTheme(isChecked);
//			}
//		});

//		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View contentView = inflater.inflate(R.layout.nav_header_main, null, false);
//		drawer.addView(contentView, 0);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_calendar) {
			// TODO: Handle the calendar action
		} else if (id == R.id.nav_settings) {
			// TODO: Handle the settings action
		} else if (id == R.id.nav_backup_and_restore) {
			// TODO: Handle the backup and restore action
		} else if (id == R.id.nav_account) {
			// TODO: Handle the account action
		} else if (id == R.id.nav_about) {
			// TODO: Handle the about action
		} else if (id == R.id.nav_switch) {
			return true;
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
