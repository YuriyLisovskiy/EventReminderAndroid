package com.yuriylisovskiy.er.Util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

public class Utils {

	public static void HideKeyboard(Context ctx, View view) {
		InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(ctx).getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(Objects.requireNonNull(view).getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

}
