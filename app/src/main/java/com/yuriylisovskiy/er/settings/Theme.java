package com.yuriylisovskiy.er.settings;

import android.app.Activity;

import android.app.Application;
import android.content.Intent;

import com.yuriylisovskiy.er.R;


public class Theme {

	private static int cTheme;

	private final static int LIGHT = 0;

	private final static int DARK = 1;

	public static void setTheme(boolean isChecked) {
		if (isChecked) {
			cTheme = DARK;
		} else {
			cTheme = LIGHT;
		}
	}

	public static void changeTheme(Activity activity) {
		activity.recreate();
	}

	public static void onActivityCreateSetTheme(Activity activity) {
		switch (cTheme) {
			default:
			case LIGHT:
				activity.getApplication().setTheme(R.style.AppTheme);
				activity.setTheme(R.style.AppTheme_NoActionBar);
				break;
			case DARK:
				activity.getApplication().setTheme(R.style.AppTheme_Dark);
				activity.setTheme(R.style.AppTheme_Dark_NoActionBar);
				break;
		}
	}
}
