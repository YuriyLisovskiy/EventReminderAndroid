package com.yuriylisovskiy.er.AbstractActivities;

import android.content.Context;

import com.yuriylisovskiy.er.Util.LocaleHelper;

public abstract class ChildActivity extends BaseActivity {

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
