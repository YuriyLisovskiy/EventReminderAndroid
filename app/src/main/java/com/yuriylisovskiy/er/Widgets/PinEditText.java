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

	private float space = 24; // 24 dp by default, space between the lines
	private float numChars = 4;
	private float lineSpacing = 8; // 8dp by default, height of the text from our lines

	private OnClickListener clickListener;

	private float lineStroke = 1; // 1dp by default
	private float lineStrokeSelected = 2; // 2dp by default
	private Paint linesPaint;
	int[][] states = new int[][] {
		new int[]{android.R.attr.state_selected}, // selected
		new int[]{android.R.attr.state_focused}, // focused
		new int[]{-android.R.attr.state_focused}, // unfocused
	};

	int[] colors = new int[] {
		Color.GREEN,
		Color.BLACK,
		Color.GRAY
	};

	ColorStateList colorStates = new ColorStateList(this.states, this.colors);

	public PinEditText(Context context) {
		super(context);
	}

	public PinEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PinEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public PinEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		float multi = context.getResources().getDisplayMetrics().density;
		this.lineStroke *= multi;
		this.lineStrokeSelected *= multi;
		this.linesPaint = new Paint(getPaint());
		this.linesPaint.setStrokeWidth(this.lineStroke);
		if (!isInEditMode()) {
			TypedValue outValue = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.colorControlActivated, outValue, true);
			final int colorActivated = outValue.data;
			this.colors[0] = colorActivated;

			context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, outValue, true);
			final int colorDark = outValue.data;
			this.colors[1] = colorDark;

			context.getTheme().resolveAttribute(R.attr.colorControlHighlight, outValue, true);
			final int colorHighlight = outValue.data;
			this.colors[2] = colorHighlight;
		}
		setBackgroundResource(0);
		this.space *= multi; // convert to pixels for our density
		this.lineSpacing *= multi; // convert to pixels for our density
		this.numChars = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", 4);

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
			setSelection(getText().length());
			if (this.clickListener != null) {
				this.clickListener.onClick(v);
			}
		});
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		this.clickListener = listener;
	}

	@Override
	public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
		throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
		float charSize;
		if (this.space < 0) {
			charSize = (availableWidth / (this.numChars * 2 - 1));
		} else {
			charSize = (availableWidth - (this.space * (this.numChars - 1))) / this.numChars;
		}

		int startX = getPaddingLeft();
		int bottom = getHeight() - getPaddingBottom();

		//Text Width
		Editable text = getText();
		int textLength = text.length();
		float[] textWidths = new float[textLength];
		getPaint().getTextWidths(getText(), 0, textLength, textWidths);

		for (int i = 0; i < this.numChars; i++) {
			updateColorForLines(i == textLength);
			canvas.drawLine(startX, bottom, startX + charSize, bottom, this.linesPaint);

			if (getText().length() > i) {
				float middle = startX + charSize / 2;
				canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2, bottom - this.lineSpacing, getPaint());
			}

			if (space < 0) {
				startX += charSize * 2;
			} else {
				startX += charSize + this.space;
			}
		}
	}

	private int getColorForState(int... states) {
		return this.colorStates.getColorForState(states, Color.GRAY);
	}

	/**
	 * @param next Is the current char the next character to be entered
	 */
	private void updateColorForLines(boolean next) {
		if (isFocused()) {
			this.linesPaint.setStrokeWidth(this.lineStrokeSelected);
			this.linesPaint.setColor(getColorForState(android.R.attr.state_focused));
			if (next) {
				this.linesPaint.setColor(getColorForState(android.R.attr.state_selected));
			}
		} else {
			this.linesPaint.setStrokeWidth(this.lineStroke);
			this.linesPaint.setColor(getColorForState(-android.R.attr.state_focused));
		}
	}
}
