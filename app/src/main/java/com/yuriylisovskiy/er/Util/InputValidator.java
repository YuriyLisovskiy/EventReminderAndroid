package com.yuriylisovskiy.er.Util;

import android.text.TextUtils;

public class InputValidator {

	private final static String UserNameAllowedSymbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_.";
	private final static String ConfirmationCodeAllowedSymbols = "0123456789";

	private static boolean validateSymbols(String input, String allowedSymbols) {
		for (int i = 0; i < input.length(); i++){
			if (allowedSymbols.indexOf(input.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPasswordValid(String password) {
		return password.length() > 5;
	}

	public static boolean isUserNameValid(String userName) {
		return userName.length() > 3 && InputValidator.validateSymbols(userName, InputValidator.UserNameAllowedSymbols);
	}

	public static boolean isEmailValid(String email) {
		return email.length() > 4;
	}

	public static boolean isEmpty(String field) {
		return TextUtils.isEmpty(field);
	}

	public static boolean isConfirmationCodeValid(String confirmationCode) {
		return confirmationCode.length() == 6 && InputValidator.validateSymbols(confirmationCode, InputValidator.ConfirmationCodeAllowedSymbols);
	}
}
