package com.yuriylisovskiy.er;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yuriylisovskiy.er.fragments.LoginFragment;
import com.yuriylisovskiy.er.fragments.RegisterFragment;
import com.yuriylisovskiy.er.fragments.ResetPasswordFragment;
import com.yuriylisovskiy.er.settings.Prefs;
import com.yuriylisovskiy.er.settings.Theme;
import com.yuriylisovskiy.er.util.LocaleHelper;

public class AccountActivity extends AppCompatActivity {

	private SectionsPagerAdapter sectionsPagerAdapter;

	private ViewPager viewPager;

	final private Prefs _prefs = Prefs.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Theme.setTheme(_prefs.idDarkTheme());
		Theme.onActivityCreateSetTheme(this);

		setContentView(R.layout.activity_account);

		Toolbar toolbar = findViewById(R.id.toolbar);
		AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
		params.setScrollFlags(0);  // clear all scroll flags

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
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		viewPager = findViewById(R.id.container);
		viewPager.setAdapter(sectionsPagerAdapter);

		TabLayout tabLayout = findViewById(R.id.tabs);

		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			Context ctx = getApplicationContext();
			switch (position) {
				case 0:
					fragment = new LoginFragment();
					((LoginFragment) fragment).setArguments(ctx);
					break;
				case 1:
					fragment = new RegisterFragment();
					((RegisterFragment) fragment).setArguments(ctx);
					break;
				case 2:
					fragment = new ResetPasswordFragment();
					((ResetPasswordFragment) fragment).setArguments(ctx);
					break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
	}
}
