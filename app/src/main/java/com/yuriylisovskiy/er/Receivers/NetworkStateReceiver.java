package com.yuriylisovskiy.er.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.yuriylisovskiy.er.AbstractActivities.BaseActivity;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Util.NetworkHelpers;

public class NetworkStateReceiver extends BroadcastReceiver {

	private String _originalTitle;

	public NetworkStateReceiver(String originalTitle) {
		this._originalTitle = originalTitle;
	}

	@Override
	public void onReceive(Context context, final Intent intent) {
		int status = NetworkHelpers.getConnectivityStatusString(context);
		if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
			BaseActivity activity = (BaseActivity) context;
			if (status == NetworkHelpers.NETWORK_STATUS_NOT_CONNECTED) {

				// TODO: set title is not working
				activity.setTitle(R.string.waiting_for_network);
				Toast.makeText(context, R.string.waiting_for_network, Toast.LENGTH_LONG).show();
			} else {

				// TODO: set title is not working
				activity.setTitle(this._originalTitle);
				Toast.makeText(context, this._originalTitle, Toast.LENGTH_LONG).show();
			}
		}
	}
}
