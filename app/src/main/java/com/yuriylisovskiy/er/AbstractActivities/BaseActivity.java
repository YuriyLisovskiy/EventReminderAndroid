package com.yuriylisovskiy.er.AbstractActivities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.Repositories.PreferencesRepository;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Util.ThemeHelper;

public abstract class BaseActivity extends AppCompatActivity {

	protected IPreferencesRepository prefs = PreferencesRepository.getInstance();
	protected Integer activityView;
	protected Integer progressBarLayout;
	protected View progressBar;
	protected Toolbar toolbar;

	protected void setupPreferences(IPreferencesRepository preferencesRepository) {
		this.prefs = preferencesRepository;
	}

	protected void initialSetup() {}

	protected void initLayouts() {
		this.activityView = null;
		this.progressBarLayout = null;
	}

	protected void onCreate() {}

	protected void configureToolBar(Toolbar toolbar) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.initialSetup();
		assert this.prefs != null;

		ThemeHelper.setTheme(prefs.idDarkTheme());
		ThemeHelper.onActivityCreateSetTheme(this);

		this.initLayouts();

		assert this.activityView != null;
		setContentView(this.activityView);
		this.toolbar = findViewById(R.id.toolbar);

		this.configureToolBar(this.toolbar);

		setSupportActionBar(this.toolbar);
		this.toolbar.setNavigationOnClickListener(bp -> onBackPressed());

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}

		this.onCreate();

		if (this.progressBarLayout != null) {
			this.progressBar = findViewById(this.progressBarLayout);
			assert progressBar != null;
			this.progressBar.setVisibility(View.GONE);
		}
	}
}
