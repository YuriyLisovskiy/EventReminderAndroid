package com.yuriylisovskiy.er;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;

import com.yuriylisovskiy.er.settings.Defaults;
import com.yuriylisovskiy.er.util.LocaleHelper;

public class SettingsActivity extends BaseActivity {

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_settings;
		this.progressBarLayout = R.id.settings_progress_bar;
	}

	@Override
	protected void onCreate() {
		this.setLanguageSelection();
		this.setMaxBackupsOption();
		this.setAutoStartOption();
		this.setRemoveEventAfterTimeIsUpOption();
		this.setBackupSettingsOption();
		this.setRemindTimeBeforeEventOption();
	}

	private void setLanguageSelection() {
		Spinner spinner = findViewById(R.id.language_selection);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.languages_array, android.R.layout.simple_spinner_item
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(this.prefs.lang().equals(Defaults.UK_UA) ? 1 : 0);
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
		numberPicker.setValue(this.prefs.maxBackups());
		numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				prefs.setMaxBackups(newVal);
			}
		});
	}

	private void setAutoStartOption() {
		Switch autoStartSwitch = findViewById(R.id.auto_start);
		autoStartSwitch.setChecked(this.prefs.runWithSystemStart());
		autoStartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.setRunWithSystemStart(isChecked);
			}
		});
	}

	private void setRemoveEventAfterTimeIsUpOption() {
		Switch removeEventSwitch = findViewById(R.id.remove_event_after_time_up);
		removeEventSwitch.setChecked(this.prefs.removeEventAfterTimeUp());
		removeEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.setRemoveEventAfterTimeUp(isChecked);
			}
		});
	}

	private void setBackupSettingsOption() {
		Switch backupSettingsSwitch = findViewById(R.id.backup_settings);
		backupSettingsSwitch.setChecked(this.prefs.backupSettings());
		backupSettingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.setBackupSettings(isChecked);
			}
		});
	}

	private void setRemindTimeBeforeEventOption() {
		final NumberPicker remindTimePicker = findViewById(R.id.remind_time_before_event_number);
		remindTimePicker.setMinValue(1);
		remindTimePicker.setMaxValue(this.getMaxValForRemindTimeEvent(this.prefs.remindTimeBeforeEventUnit()));
		remindTimePicker.setValue(this.prefs.remindTimeBeforeEventValue());
		remindTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				prefs.setRemindTimeBeforeEventValue(newVal);
			}
		});

		NumberPicker unitsPicker = findViewById(R.id.remind_time_before_event_units);
		String units[] = getResources().getStringArray(R.array.remind_before_event_units_array);
		unitsPicker.setMinValue(0);
		unitsPicker.setMaxValue(units.length - 1);
		unitsPicker.setDisplayedValues(units);
		unitsPicker.setValue(this.prefs.remindTimeBeforeEventUnit());
		unitsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				prefs.setRemindTimeBeforeEventUnits(newVal);
				remindTimePicker.setMaxValue(getMaxValForRemindTimeEvent(newVal));
				prefs.setRemindTimeBeforeEventValue(remindTimePicker.getValue());
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
}
