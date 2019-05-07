package com.yuriylisovskiy.er;

import android.support.design.widget.BottomNavigationView;
import android.widget.TextView;

import com.yuriylisovskiy.er.AbstractActivities.ChildActivity;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;

public class BackupAndRestoreActivity extends ChildActivity {

	private IClientService _clientService = ClientService.getInstance();

	private TextView mTextMessage;

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_backup_and_restore;
	}

	@Override
	protected void onCreate() {
		mTextMessage = findViewById(R.id.message);
		BottomNavigationView navigation = findViewById(R.id.backup_and_restore_nav);
		navigation.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.navigation_home:
					mTextMessage.setText(R.string.title_home);
					return true;
				case R.id.navigation_dashboard:
					mTextMessage.setText(R.string.title_dashboard);
					return true;
				case R.id.navigation_notifications:
					mTextMessage.setText(R.string.title_notifications);
					return true;
			}
			return false;
		});
	}

}
