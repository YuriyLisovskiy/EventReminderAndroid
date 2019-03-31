package com.yuriylisovskiy.er;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.yuriylisovskiy.er.settings.Defaults;
import com.yuriylisovskiy.er.settings.Prefs;
import com.yuriylisovskiy.er.settings.Theme;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Prefs prefs = Prefs.getInstance();

		Theme.setTheme(prefs.idDarkTheme());
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

		RadioGroup fontSizeSetting = findViewById(R.id.font_settings);
		if (fontSizeSetting != null) {
			switch (prefs.fontSize()) {
				case Defaults.FONT_SMALL:
					fontSizeSetting.check(R.id.font_small);
					break;
				default:
				case Defaults.FONT_MEDIUM:
					fontSizeSetting.check(R.id.font_medium);
					break;
				case Defaults.FONT_LARGE:
					fontSizeSetting.check(R.id.font_large);
					break;
			}
			fontSizeSetting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
						case R.id.font_small:
							prefs.setFontSize(Defaults.FONT_SMALL);
							break;
						default:
						case R.id.font_medium:
							prefs.setFontSize(Defaults.FONT_MEDIUM);
							break;
						case R.id.font_large:
							prefs.setFontSize(Defaults.FONT_LARGE);
							break;
					}
					Toast.makeText(SettingsActivity.this, Integer.toString(prefs.fontSize()), Toast.LENGTH_LONG).show();
				}
			});
		}

		findViewById(R.id.settings_progress_bar).setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
}
