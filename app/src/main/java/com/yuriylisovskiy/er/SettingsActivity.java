package com.yuriylisovskiy.er;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.yuriylisovskiy.er.settings.Defaults;
import com.yuriylisovskiy.er.settings.Prefs;
import com.yuriylisovskiy.er.settings.Theme;
import com.yuriylisovskiy.er.util.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

	final private Prefs _prefs = Prefs.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Theme.setTheme(_prefs.idDarkTheme());
		Theme.onActivityCreateSetTheme(this);

		setContentView(R.layout.activity_settings);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}

		this.setLanguageSelection();
		this.setMaxBackupsOption();
		this.setAutoStartOption();
		this.setRemoveEventAfterTimeIsUpOption();
		this.setBackupSettingsOption();
		this.setRemindTimeBeforeEventOption();

		findViewById(R.id.settings_progress_bar).setVisibility(View.GONE);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.onAttach(base));
	}

	private void setLanguageSelection() {
		Spinner spinner = findViewById(R.id.language_selection);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.languages_array, android.R.layout.simple_spinner_item
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(_prefs.lang().equals(Defaults.UK_UA) ? 1 : 0);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					default:
					case 0:
						LocaleHelper.setLocale(getApplicationContext(), Defaults.EN_US);
						break;
					case 1:
						LocaleHelper.setLocale(getApplicationContext(), Defaults.UK_UA);
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void setMaxBackupsOption() {
		NumberPicker numberPicker = findViewById(R.id.max_backups);
		numberPicker.setMaxValue(10);
		numberPicker.setMinValue(1);
		numberPicker.setValue(_prefs.maxBackups());
		numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				_prefs.setMaxBackups(newVal);
			}
		});
	}

	private void setAutoStartOption() {
		Switch autoStartSwitch = findViewById(R.id.auto_start);
		autoStartSwitch.setChecked(_prefs.runWithSystemStart());
		autoStartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_prefs.setRunWithSystemStart(isChecked);
			}
		});
	}

	private void setRemoveEventAfterTimeIsUpOption() {
		Switch removeEventSwitch = findViewById(R.id.remove_event_after_time_up);
		removeEventSwitch.setChecked(_prefs.removeEventAfterTimeUp());
		removeEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_prefs.setRemoveEventAfterTimeUp(isChecked);
			}
		});
	}

	private void setBackupSettingsOption() {
		Switch backupSettingsSwitch = findViewById(R.id.backup_settings);
		backupSettingsSwitch.setChecked(_prefs.backupSettings());
		backupSettingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_prefs.setBackupSettings(isChecked);
			}
		});
	}

	private void setRemindTimeBeforeEventOption() {
		final NumberPicker remindTimePicker = findViewById(R.id.remind_time_before_event_number);
		remindTimePicker.setMinValue(1);
		remindTimePicker.setMaxValue(this.getMaxValForRemindTimeEvent(_prefs.remindTimeBeforeEventUnit()));
		remindTimePicker.setValue(_prefs.remindTimeBeforeEventValue());
		remindTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				_prefs.setRemindTimeBeforeEventValue(newVal);
			}
		});

		NumberPicker unitsPicker = findViewById(R.id.remind_time_before_event_units);
		String units[] = getResources().getStringArray(R.array.remind_before_event_units_array);
		unitsPicker.setMinValue(0);
		unitsPicker.setMaxValue(units.length - 1);
		unitsPicker.setDisplayedValues(units);
		unitsPicker.setValue(_prefs.remindTimeBeforeEventUnit());
		unitsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				_prefs.setRemindTimeBeforeEventUnits(newVal);
				remindTimePicker.setMaxValue(getMaxValForRemindTimeEvent(newVal));
				_prefs.setRemindTimeBeforeEventValue(remindTimePicker.getValue());
			}
		});
	}

	private int getMaxValForRemindTimeEvent(int input) {
		int max = 60;
		switch (input) {
			default:
				break;
			case 1:
				max = 24;
				break;
			case 2:
				max = 7;
				break;
			case 3:
				max = 4;
				break;
		}
		return max;
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
}
