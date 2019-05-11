package com.yuriylisovskiy.er.Util;

import android.support.annotation.NonNull;

public class SizeOfString {

	private double size;
	private Units units;

	public SizeOfString(String string) {
		int counter = 0;
		double sizeInBytes = string.getBytes().length;
		while (sizeInBytes > 1 && counter < unitsList.length) {
			sizeInBytes /= 1024;
			counter++;
		}
		this.size = sizeInBytes;
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
