package com.yuriylisovskiy.er;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yuriylisovskiy.er.settings.Prefs;
import com.yuriylisovskiy.er.settings.Theme;
import com.yuriylisovskiy.er.util.LocaleHelper;

public abstract class BaseActivity extends AppCompatActivity {

	final protected Prefs prefs = Prefs.getInstance();
	protected Integer activityView;
	protected Integer progressBarLayout;
	protected View progressBar;

	protected void initLayouts() {
		this.activityView = null;
		this.progressBarLayout = null;
	}

	protected void onCreate() {}

	protected void configureToolBar(Toolbar toolbar) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Theme.setTheme(prefs.idDarkTheme());
		Theme.onActivityCreateSetTheme(this);

		this.initLayouts();

		assert this.activityView != null;
		setContentView(this.activityView);
		Toolbar toolbar = findViewById(R.id.toolbar);

		this.configureToolBar(toolbar);

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

		this.onCreate();

		if (this.progressBarLayout != null) {
			this.progressBar = findViewById(this.progressBarLayout);
			assert progressBar != null;
			this.progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.onAttach(base));
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
}
