package com.yuriylisovskiy.er.Util;

public class TypeConverter {

	public static boolean IntToBool(int _int) {
		return _int != 0;
	}

	public static int BoolToInt(boolean _boolean) {
		return _boolean ? 1 : 0;
	}
}
