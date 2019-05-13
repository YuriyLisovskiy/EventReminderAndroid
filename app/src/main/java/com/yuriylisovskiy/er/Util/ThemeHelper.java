package com.yuriylisovskiy.er.Util;

import android.app.Activity;

import com.yuriylisovskiy.er.MainActivity;
import com.yuriylisovskiy.er.R;

public class ThemeHelper {

	private static int _theme;

	private final static int LIGHT = 0;
	private final static int DARK = 1;

	public static void setTheme(boolean isChecked) {
		if (isChecked) {
			ThemeHelper._theme = DARK;
		} else {
			ThemeHelper._theme = LIGHT;
		}
	}

	public static void changeTheme(Activity activity) {
		activity.recreate();
	}

	public static void onActivityCreateSetTheme(Activity activity) {
		switch (ThemeHelper._theme) {
			default:
			case LIGHT:
				if (activity instanceof MainActivity) {
					activity.getApplication().setTheme(R.style.AppTheme);
				}
				activity.setTheme(R.style.AppTheme_NoActionBar);
				break;
			case DARK:
				if (activity instanceof MainActivity) {
					activity.getApplication().setTheme(R.style.AppTheme_Dark);
				}
				activity.setTheme(R.style.AppTheme_Dark_NoActionBar);
				break;
		}
	}
}
