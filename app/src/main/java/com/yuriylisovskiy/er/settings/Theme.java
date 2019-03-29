package com.yuriylisovskiy.er.settings;

import android.app.Activity;

import android.content.Intent;

import com.yuriylisovskiy.er.R;


public class Theme {

	private static int cTheme;

	public final static int LIGHT = 0;

	public final static int DARK = 1;

	public static void changeToTheme(Activity activity, int theme) {
		cTheme = theme;
		activity.startActivity(new Intent(activity, activity.getClass()));
		activity.finish();
	}

	public static void onActivityCreateSetTheme(Activity activity) {
		switch (cTheme) {
			default:
			case LIGHT:
				activity.setTheme(R.style.AppTheme);
				break;
			case DARK:
				activity.setTheme(R.style.AppTheme_Dark);
				break;
		}
	}
}
