package com.yuriylisovskiy.er;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.yuriylisovskiy.er.AbstractActivities.ChildActivity;
import com.yuriylisovskiy.er.Fragments.Interfaces.IClientFragment;
import com.yuriylisovskiy.er.Fragments.LoginFragment;
import com.yuriylisovskiy.er.Fragments.RegisterFragment;
import com.yuriylisovskiy.er.Fragments.ResetPasswordFragment;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;

public class AccountActivity extends ChildActivity {

	private IClientService _clientService = ClientService.getInstance();

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_account;
	}

	@Override
	protected void onCreate() {
		SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager());

		ViewPager viewPager = this.findViewById(R.id.container);
		viewPager.setAdapter(sectionsPagerAdapter);

		TabLayout tabLayout = this.findViewById(R.id.tabs);

		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
	}

	@Override
	protected void onResume() {
		this.registerNetworkStateReceiver(this.getTitle().toString());
		super.onResume();
	}

	@Override
	protected void onPause() {
		this.unregisterNetworkStateReceiver();
		super.onPause();
	}

	@Override
	protected void configureToolBar(Toolbar toolbar) {
		AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

		// clear all scroll flags
		params.setScrollFlags(0);
	}

	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		private final int _fragmentsCount = 3;

		SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
				case 0:
					fragment = new LoginFragment();
					((LoginFragment) fragment).setArguments(findViewById(R.id.tabs));
					break;
				case 1:
					fragment = new RegisterFragment();
					break;
				case 2:
					fragment = new ResetPasswordFragment();
					break;
			}
			assert fragment != null;
			((IClientFragment) fragment).setClientService(_clientService, getBaseContext());
			return fragment;
		}

		@Override
		public int getCount() {
			return this._fragmentsCount;
		}
	}
}
