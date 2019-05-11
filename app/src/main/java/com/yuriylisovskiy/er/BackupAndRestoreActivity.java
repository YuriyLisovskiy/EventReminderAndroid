package com.yuriylisovskiy.er;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yuriylisovskiy.er.AbstractActivities.ChildActivity;
import com.yuriylisovskiy.er.Adapters.BackupsListAdapter;
import com.yuriylisovskiy.er.DataAccess.Interfaces.IPreferencesRepository;
import com.yuriylisovskiy.er.DataAccess.Models.BackupModel;
import com.yuriylisovskiy.er.DataAccess.Repositories.PreferencesRepository;
import com.yuriylisovskiy.er.Services.BackupService.BackupService;
import com.yuriylisovskiy.er.Services.BackupService.IBackupService;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;
import com.yuriylisovskiy.er.Services.EventService.EventService;
import com.yuriylisovskiy.er.Services.EventService.IEventService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BackupAndRestoreActivity extends ChildActivity {

	private IClientService _clientService = ClientService.getInstance();
	private IBackupService _backupService = new BackupService();
	private IEventService _eventService = new EventService();
	private IPreferencesRepository _prefsRepository = PreferencesRepository.getInstance();

	private RadioGroup _backupTypeRadioGroup;
	private ListView _backupsListView;
	private TextView _noBackupsTextView;

	private AsyncTask<Void, Void, Boolean> _checkAvailableStorageTask;
	private AsyncTask<Void, Void, String> _processBackupTask;
	private AsyncTask<Void, Void, List<BackupsListAdapter.BackupItem>> _loadBackupsTask;

	private enum Processes {
		RESTORE, CREATE, REMOVE
	}

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
		this.checkCloudStorage();
		this.loadBackups(false);
		BottomNavigationView navigation = findViewById(R.id.backup_and_restore_nav);
		navigation.setOnNavigationItemSelectedListener(item -> {
			boolean isCloud = false;
			switch (this._backupTypeRadioGroup.getCheckedRadioButtonId()) {
				case R.id.cloud_storage:
					isCloud = true;
					break;
			}
			switch (item.getItemId()) {
				case R.id.action_backup_restore:
					Toast.makeText(getBaseContext(), "Restore", Toast.LENGTH_SHORT).show();
					return true;
				case R.id.action_backup_create:
					this._processBackupTask = new BackupAndRestoreActivity.ProcessBackupTask(
						this, Processes.CREATE, isCloud
					);
					this._processBackupTask.execute((Void) null);
					return true;
				case R.id.action_backup_remove:
					Toast.makeText(getBaseContext(), "Remove", Toast.LENGTH_SHORT).show();
					return true;
			}
			return false;
		});
	}

	private void showProgress(final boolean show, boolean hasBackups) {
		int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);
		if (show) {
			this._noBackupsTextView.setVisibility(View.GONE);
		} else {
			if (!hasBackups) {
				this._noBackupsTextView.setVisibility(View.VISIBLE);
			}
		}
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

	private String restore(boolean isCloud) {
		if (isCloud) {
			// TODO: restore from cloud backup
		} else {
			// TODO: restore from local backup
		}
		return null;
	}

	private String create(boolean isCloud) {
		String result = null;
		String userName = null;
		try {
			userName = this._clientService.User().getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RequestError requestError) {
			requestError.printStackTrace();
		}
		BackupModel backupModel = null;
		try {
			backupModel = this._backupService.PrepareBackup(
				this._eventService.GetAll(),
				this._prefsRepository.backupSettings(),
				userName
			);
		} catch (JSONException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (ParseException e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		if (backupModel != null) {
			if (isCloud) {
				try {
					this._clientService.UploadBackup(backupModel);
				} catch (IOException e) {
					e.printStackTrace();
					result = e.getMessage();
				} catch (RequestError e) {
					e.printStackTrace();
					result = e.getMessage();
				}
			} else {
				this._backupService.CreateBackup(backupModel);
			}
		}
		return result;
	}

	private String remove(boolean isCloud) {
		if (isCloud) {
			// TODO: remove cloud backup
		} else {
			// TODO: remove local backup
		}
		return null;
	}

	// Disables 'Cloud' radio button if user is not logged in.
	private void checkCloudStorage() {
		this.showProgress(true, false);
		this._checkAvailableStorageTask = new BackupAndRestoreActivity.CheckCloudStorageTask(this);
		this._checkAvailableStorageTask.execute((Void) null);
	}

	private void loadBackups(boolean isCloud) {
		this.showProgress(true, false);
		this._loadBackupsTask = new BackupAndRestoreActivity.LoadBackupsTask(this, isCloud);
		this._loadBackupsTask.execute((Void) null);
	}

	private static class ProcessBackupTask extends AsyncTask<Void, Void, String> {

		private Processes _processType;
		private boolean _isCloud;

		private WeakReference<BackupAndRestoreActivity> _cls;

		ProcessBackupTask(BackupAndRestoreActivity cls, Processes processType, boolean isCloud) {
			this._cls = new WeakReference<>(cls);
			this._processType = processType;
			this._isCloud = isCloud;
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			switch (this._processType) {
				case RESTORE:
					result = this._cls.get().restore(this._isCloud);
					break;
				case CREATE:
					result = this._cls.get().create(this._isCloud);
					break;
				case REMOVE:
					result = this._cls.get().remove(this._isCloud);
					break;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			// TODO: success if result == null

			this.taskFinished();
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get()._processBackupTask = null;
		}
	}

	private static class CheckCloudStorageTask extends AsyncTask<Void, Void, Boolean> {

		private WeakReference<BackupAndRestoreActivity> _cls;

		CheckCloudStorageTask(BackupAndRestoreActivity cls) {
			this._cls = new WeakReference<>(cls);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return this._cls.get()._clientService.IsLoggedIn();
		}

		@Override
		protected void onPostExecute(Boolean cloudIsAvailable) {
			if (!cloudIsAvailable) {
				RadioButton cloudButton = this._cls.get().findViewById(R.id.cloud_storage);
				cloudButton.setEnabled(false);
				Toast.makeText(
						this._cls.get().getBaseContext(),
						R.string.cloud_storage_login_required,
						Toast.LENGTH_LONG
				).show();
			}
			this.taskFinished();
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get()._checkAvailableStorageTask = null;
			this._cls.get().showProgress(false, false);
		}
	}

	private static class LoadBackupsTask extends AsyncTask<Void, Void, List<BackupsListAdapter.BackupItem>> {

		private boolean _isCloud;

		private WeakReference<BackupAndRestoreActivity> _cls;

		LoadBackupsTask(BackupAndRestoreActivity cls, boolean isCloud) {
			this._cls = new WeakReference<>(cls);
			this._isCloud = isCloud;
		}

		@Override
		protected List<BackupsListAdapter.BackupItem> doInBackground(Void... params) {
			List<BackupsListAdapter.BackupItem> result;
			if (this._isCloud) {
				result = this.loadCloud();
			} else {
				result = this.loadLocal();
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<BackupsListAdapter.BackupItem> backups) {
			boolean hasBackups = backups != null && backups.size() > 0;
			if (hasBackups) {
				BackupsListAdapter adapter = new BackupsListAdapter(
					this._cls.get(), R.layout.backup_list_item, backups
				);
				this._cls.get()._backupsListView.setAdapter(adapter);
				this._cls.get()._backupsListView.setVisibility(View.VISIBLE);
			} else {
				this._cls.get()._noBackupsTextView.setVisibility(View.VISIBLE);
			}
			this.taskFinished(hasBackups);
			this._cls.get()._backupTypeRadioGroup.setEnabled(true);
		}

		private List<BackupsListAdapter.BackupItem> loadLocal() {
			List<BackupModel> models = this._cls.get()._backupService.GetAll();
			List<BackupsListAdapter.BackupItem> items = new ArrayList<>();
			for (BackupModel model : models) {
				items.add(model.ToBackupItem());
			}
			return items;
		}

		private List<BackupsListAdapter.BackupItem> loadCloud() {
			List<BackupsListAdapter.BackupItem> result = null;
			try {
				result = this.fromJson(this._cls.get()._clientService.Backups());
			} catch (IOException exc) {
				exc.printStackTrace();
			} catch (RequestError exc) {
				exc.printStackTrace();
				Toast.makeText(this._cls.get().getBaseContext(), exc.getErr(), Toast.LENGTH_SHORT).show();
			}
			return result;
		}

		private List<BackupsListAdapter.BackupItem> fromJson(JSONArray backups) {
			List<BackupsListAdapter.BackupItem> result = null;
			if (backups != null) {
				result = new ArrayList<>();
				for (int i = 0; i < backups.length(); i++) {
					try {
						JSONObject backup = (JSONObject) backups.get(i);
						result.add(new BackupModel(
							backup.getString("digest"),
							backup.getString("timestamp"),
							"",
							backup.getInt("events_amount"),
							backup.getString("backup_size"),
							backup.getBoolean("contains_settings")
						).ToBackupItem());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}

		@Override
		protected void onCancelled() {
			this.taskFinished(false);
		}

		private void taskFinished(boolean hasBackups) {
			this._cls.get()._loadBackupsTask = null;
			this._cls.get().showProgress(false, hasBackups);
		}
	}
}
