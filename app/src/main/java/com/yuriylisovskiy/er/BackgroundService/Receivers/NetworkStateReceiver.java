package com.yuriylisovskiy.er.BackgroundService.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yuriylisovskiy.er.Interfaces.INetworkStateListener;
import com.yuriylisovskiy.er.Util.Logger;
import com.yuriylisovskiy.er.Util.NetworkHelpers;

public class NetworkStateReceiver extends BroadcastReceiver {

	private INetworkStateListener _networkStateListener;

	public NetworkStateReceiver(INetworkStateListener networkStateListener) {
		this._networkStateListener = networkStateListener;
	}

	@Override
	public void onReceive(Context context, final Intent intent) {
		int status = NetworkHelpers.getConnectivityStatusString(context);
		if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
			try {
				if (status == NetworkHelpers.NETWORK_STATUS_NOT_CONNECTED) {
					this._networkStateListener.disconnected();
				} else {
					this._networkStateListener.connected();
				}
			} catch (Exception exc) {
				Logger.getInstance().error(exc.getMessage());
			}
		}
	}
}
