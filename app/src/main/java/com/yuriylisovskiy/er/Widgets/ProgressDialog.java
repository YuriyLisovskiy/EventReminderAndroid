package com.yuriylisovskiy.er.Widgets;

import android.app.Activity;
import android.view.WindowManager;

import java.util.Objects;

public class ProgressDialog extends android.app.ProgressDialog {

	private Activity _parent;

	public ProgressDialog(Activity parent) {
		super(parent);
		this._parent = parent;
	}

	@Override
	public void show() {
		super.show();
		this._parent.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		);
		Objects.requireNonNull(this.getWindow()).setFlags(
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		this._parent.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		Objects.requireNonNull(this.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}
}
