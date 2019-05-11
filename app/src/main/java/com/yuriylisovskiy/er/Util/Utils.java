package com.yuriylisovskiy.er.Util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Utils {

	public static void HideKeyboard(Context ctx, View view) {
		InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(ctx).getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(Objects.requireNonNull(view).getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	public static String Sha512(String data) {
		try {
			// getInstance() method is called with algorithm SHA-512
			MessageDigest md = MessageDigest.getInstance("SHA-512");

			// digest() method is called
			// to calculate message digest of the input string
			// returned as array of byte
			byte[] messageDigest = md.digest(data.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			StringBuilder hashtext = new StringBuilder(no.toString(16));

			// Add preceding 0s to make it 32 bit
			while (hashtext.length() < 32) {
				hashtext.insert(0, "0");
			}

			// return the HashText
			return hashtext.toString();
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
