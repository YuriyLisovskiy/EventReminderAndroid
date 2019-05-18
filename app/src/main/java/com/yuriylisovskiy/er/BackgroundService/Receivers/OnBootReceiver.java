package com.yuriylisovskiy.er.BackgroundService.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yuriylisovskiy.er.BackgroundService.NotificationService;
import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.Repositories.PreferencesRepository;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			IPreferencesRepository prefs = PreferencesRepository.getInstance();
			if (!prefs.IsInitialized()) {
				prefs.Initialize(context);
			}
			if (prefs.runWithSystemStart()) {
				Intent service = new Intent(context, NotificationService.class);
				context.startService(service);
			}
		}
	}
}
