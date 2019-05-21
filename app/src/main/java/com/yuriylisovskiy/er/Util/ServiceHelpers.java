package com.yuriylisovskiy.er.Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

public class ServiceHelpers {

	public static boolean serviceIsRunning(Activity client, Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) client.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void restartService(Activity client, Class<?> serviceClass) {
		if (!ServiceHelpers.serviceIsRunning(client, serviceClass)) {
			Intent service = new Intent(client, serviceClass);
			client.startService(service);
		}
	}
}
