package com.yuriylisovskiy.er.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

	private final SharedPreferences prefs;

	// ======= Loaded settings ======= //
	private boolean isDarkTheme;

	public Prefs(Activity activity) {
		prefs = activity.getSharedPreferences("com.yuriylisovskiy.er", Context.MODE_PRIVATE);

		this.isDarkTheme = this.prefs.getBoolean("isDarkTheme", Defaults.IS_DARK_THEME);
	}

	// ======= Getters ====== //
	public boolean idDarkTheme() {
		return this.isDarkTheme;
	}

	// ======= Setters ====== //
	public void setIsDarkTheme(boolean isDark) {
		this.prefs.edit().putBoolean("isDarkTheme", isDark).apply();
		this.isDarkTheme = isDark;
	}

}
