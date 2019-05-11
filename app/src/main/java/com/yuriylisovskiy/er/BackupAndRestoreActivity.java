package com.yuriylisovskiy.er;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yuriylisovskiy.er.AbstractActivities.ChildActivity;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;
import com.yuriylisovskiy.er.Services.EventService.EventService;
import com.yuriylisovskiy.er.Services.EventService.IEventService;

import java.lang.ref.WeakReference;

public class BackupAndRestoreActivity extends ChildActivity {

	private IClientService _clientService = ClientService.getInstance();

	private RadioGroup _backupTypeRadioGroup;
	private ListView _backupsListView;
	private TextView _noBackupsTextView;

	private AsyncTask<Void, Void, Boolean> _checkAvailableStorageTask;

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_backup_and_restore;
		this.progressBarLayout = R.id.progress;
	}

	@Override
	protected void onCreate() {
		this._noBackupsTextView = this.findViewById(R.id.no_backups_text);
		this._backupsListView = this.findViewById(R.id.backups_list);
		this._backupTypeRadioGroup = this.findViewById(R.id.storage_switch);
		this._backupTypeRadioGroup.setEnabled(false);
		this._backupTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			boolean isCloud = false;
			switch (checkedId) {
				case R.id.cloud_storage:
					isCloud = true;
					break;
			}
			this.loadBackups(isCloud);
		});
		this.initStorage();
		BottomNavigationView navigation = findViewById(R.id.backup_and_restore_nav);
		navigation.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.action_backup_restore:
					this.processRestore();
					return true;
				case R.id.action_backup_create:
					this.processCreate();
					return true;
				case R.id.action_backup_remove:
					this.processRemove();
					return true;
			}
			return false;
		});
	}

	private void showProgress(final boolean show) {
		int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);
		this._backupsListView.setVisibility(show ? View.GONE : View.VISIBLE);
		this._backupsListView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					_backupsListView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			}
		);
		if (show) {
			this.showProgressBar();
		} else {
			this.hideProgressBar();
		}
	}

	private void processRestore() {
		Toast.makeText(getBaseContext(), "Restore", Toast.LENGTH_SHORT).show();
	}

	private void processCreate() {
		Toast.makeText(getBaseContext(), "Create", Toast.LENGTH_SHORT).show();
	}

	private void processRemove() {
		Toast.makeText(getBaseContext(), "Remove", Toast.LENGTH_SHORT).show();
	}

	// Sets 'Cloud' radio button checked if user is logged in
	//  and loads available backups according to checked radio button
	private void initStorage() {
		this.showProgress(true);
		this._checkAvailableStorageTask = new BackupAndRestoreActivity.InitStorageTask(this);
		this._checkAvailableStorageTask.execute();
	}

	private void loadBackups(boolean isCloud) {
		if (isCloud) {
			Toast.makeText(getBaseContext(), "Cloud", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getBaseContext(), "Local", Toast.LENGTH_SHORT).show();
		}
		this._noBackupsTextView.setVisibility(View.VISIBLE);
		this._backupTypeRadioGroup.setEnabled(true);
	}

	private static class InitStorageTask extends AsyncTask<Void, Void, Boolean> {

		private WeakReference<BackupAndRestoreActivity> _cls;

		InitStorageTask(BackupAndRestoreActivity cls) {
			this._cls = new WeakReference<>(cls);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return this._cls.get()._clientService.IsLoggedIn();
		}

		@Override
		protected void onPostExecute(Boolean cloudIsAvailable) {
			if (cloudIsAvailable) {
				this._cls.get()._backupTypeRadioGroup.check(R.id.cloud_storage);
			} else {
				RadioButton cloudButton = this._cls.get().findViewById(R.id.cloud_storage);
				cloudButton.setEnabled(false);
				this._cls.get()._backupTypeRadioGroup.check(R.id.local_storage);
				Toast.makeText(
					this._cls.get().getBaseContext(),
					R.string.cloud_storage_login_required,
					Toast.LENGTH_LONG
				).show();
			}
			this._cls.get()._backupTypeRadioGroup.performClick();
			this.taskFinished();
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get()._checkAvailableStorageTask = null;
			this._cls.get().showProgress(false);
		}
	}

	private static class LoadBackupsTask extends AsyncTask<Void, Void, Boolean> {

		private boolean _isCloud;

		private WeakReference<BackupAndRestoreActivity> _cls;

		LoadBackupsTask(BackupAndRestoreActivity cls, boolean isCloud) {
			this._cls = new WeakReference<>(cls);
			this._isCloud = isCloud;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: perform backups loading
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			this.taskFinished();
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get()._checkAvailableStorageTask = null;
			this._cls.get().showProgress(false);
		}
	}
}
