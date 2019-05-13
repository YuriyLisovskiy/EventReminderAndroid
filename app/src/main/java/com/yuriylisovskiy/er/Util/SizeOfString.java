package com.yuriylisovskiy.er.Util;

import android.support.annotation.NonNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

public class SizeOfString {

	private double size;
	private Units units;

	public SizeOfString(String string) throws ParseException {
		int counter = 0;
		int K = 1000;
		double sizeInBytes = string.getBytes().length;
		while (sizeInBytes > K - 1 && counter < unitsList.length) {
			sizeInBytes /= K;
			counter++;
		}
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		this.size = df.parse(df.format(sizeInBytes)).doubleValue();
		this.units = unitsList[counter];
	}

	@NonNull
	@Override
	public String toString() {
		return this.size + " " + this.units.name();
	}

	private static Units[] unitsList = new Units[] {
		Units.BYTES, Units.KB, Units.MB, Units.GB
	};

	public enum Units {
		BYTES, KB, MB, GB
	}
}
