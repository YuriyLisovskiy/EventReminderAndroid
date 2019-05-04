package com.yuriylisovskiy.er.Widgets;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.yuriylisovskiy.er.R;

@SuppressLint("AppCompatCustomView")
public class PinEditText extends EditText {

	public static final String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";

	private float _space = 24; // 24 dp by default, space between the lines
	private float _numChars = 4;
	private float _lineSpacing = 8; // 8dp by default, height of the text from our lines

	private OnClickListener _clickListener;

	private float _lineStroke = 1; // 1dp by default
	private float _lineStrokeSelected = 2; // 2dp by default
	private Paint _linesPaint;
	private int[][] _states = new int[][] {
		new int[]{android.R.attr.state_selected}, // selected
		new int[]{android.R.attr.state_focused}, // focused
		new int[]{-android.R.attr.state_focused}, // unfocused
	};

	private int[] _colors = new int[] {
		Color.GREEN,
		Color.BLACK,
		Color.GRAY
	};

	private ColorStateList _colorStates = new ColorStateList(this._states, this._colors);

	public PinEditText(Context context) {
		super(context);
	}

	public PinEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context, attrs);
	}

	public PinEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PinEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		float multi = context.getResources().getDisplayMetrics().density;
		this._lineStroke *= multi;
		this._lineStrokeSelected *= multi;
		this._linesPaint = new Paint(getPaint());
		this._linesPaint.setStrokeWidth(this._lineStroke);
		if (!this.isInEditMode()) {
			TypedValue outValue = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.colorControlActivated, outValue, true);
			final int colorActivated = outValue.data;
			this._colors[0] = colorActivated;

			context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, outValue, true);
			final int colorDark = outValue.data;
			this._colors[1] = colorDark;

			context.getTheme().resolveAttribute(R.attr.colorControlHighlight, outValue, true);
			final int colorHighlight = outValue.data;
			this._colors[2] = colorHighlight;
		}
		this.setBackgroundResource(0);
		this._space *= multi; // convert to pixels for our density
		this._lineSpacing *= multi; // convert to pixels for our density
		this._numChars = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", 4);

		// Disable copy paste
		super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {

			}
		});

		// When tapped, move cursor to end of text.
		super.setOnClickListener(v -> {
			this.setSelection(this.getText().length());
			if (this._clickListener != null) {
				this._clickListener.onClick(v);
			}
		});
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		this._clickListener = listener;
	}

	@Override
	public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
		throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		int availableWidth = this.getWidth() - this.getPaddingRight() - this.getPaddingLeft();
		float charSize;
		if (this._space < 0) {
			charSize = (availableWidth / (this._numChars * 2 - 1));
		} else {
			charSize = (availableWidth - (this._space * (this._numChars - 1))) / this._numChars;
		}

		int startX = this.getPaddingLeft();
		int bottom = this.getHeight() - this.getPaddingBottom();

		//Text Width
		Editable text = this.getText();
		int textLength = text.length();
		float[] textWidths = new float[textLength];
		this.getPaint().getTextWidths(getText(), 0, textLength, textWidths);

		for (int i = 0; i < this._numChars; i++) {
			updateColorForLines(i == textLength);
			canvas.drawLine(startX, bottom, startX + charSize, bottom, this._linesPaint);

			if (this.getText().length() > i) {
				float middle = startX + charSize / 2;
				canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2, bottom - this._lineSpacing, this.getPaint());
			}

			if (this._space < 0) {
				startX += charSize * 2;
			} else {
				startX += charSize + this._space;
			}
		}
	}

	private int getColorForState(int... states) {
		return this._colorStates.getColorForState(states, Color.GRAY);
	}

	/**
	 * @param next Is the current char the next character to be entered
	 */
	private void updateColorForLines(boolean next) {
		if (this.isFocused()) {
			this._linesPaint.setStrokeWidth(this._lineStrokeSelected);
			this._linesPaint.setColor(this.getColorForState(android.R.attr.state_focused));
			if (next) {
				this._linesPaint.setColor(this.getColorForState(android.R.attr.state_selected));
			}
		} else {
			this._linesPaint.setStrokeWidth(this._lineStroke);
			this._linesPaint.setColor(this.getColorForState(-android.R.attr.state_focused));
		}
	}
}
