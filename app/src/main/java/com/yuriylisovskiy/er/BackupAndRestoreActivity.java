package com.yuriylisovskiy.er;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Intent;
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
import com.yuriylisovskiy.er.Services.BackupService.Exceptions.InvalidBackupException;
import com.yuriylisovskiy.er.Services.BackupService.IBackupService;
import com.yuriylisovskiy.er.Services.ClientService.ClientService;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;
import com.yuriylisovskiy.er.Services.EventService.EventService;
import com.yuriylisovskiy.er.Services.EventService.IEventService;
import com.yuriylisovskiy.er.Util.Globals;
import com.yuriylisovskiy.er.Util.Names;
import com.yuriylisovskiy.er.Widgets.ProgressDialog;

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

	private String _selectedBackup;
	private int _selectedBackupPosition;

	private RadioGroup _backupTypeRadioGroup;
	private ListView _backupsListView;
	private TextView _noBackupsTextView;

	private ProgressDialog _progressDialog;

	private AsyncTask<Void, Void, Boolean> _checkAvailableStorageTask;
	private AsyncTask<Void, Void, String> _processBackupTask;
	private AsyncTask<Void, Void, BackupsListAdapter.BackupItem> _createBackupTask;
	private AsyncTask<Void, Void, List<BackupsListAdapter.BackupItem>> _loadBackupsTask;

	@Override
	protected void initLayouts() {
		this.activityView = R.layout.activity_backup_and_restore;
		this.progressBarLayout = R.id.progress;
	}

	@Override
	protected void onCreate() {
		this._progressDialog = new ProgressDialog(this);
		this._noBackupsTextView = this.findViewById(R.id.no_backups_text);
		this._backupsListView = this.findViewById(R.id.backups_list);
		this._backupsListView.setOnItemClickListener(
			(parent, view, position, arg3) -> this.selectBackup(view, position)
		);
		this._backupTypeRadioGroup = this.findViewById(R.id.storage_switch);
		this._backupTypeRadioGroup.setEnabled(false);
		this._backupTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			this.resetBackupSelection();
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
		BottomNavigationView _managePanelNavView = findViewById(R.id.backup_and_restore_nav);
		_managePanelNavView.setOnNavigationItemSelectedListener(item -> {
			boolean isCloud = false;
			switch (this._backupTypeRadioGroup.getCheckedRadioButtonId()) {
				case R.id.cloud_storage:
					isCloud = true;
					break;
			}
			switch (item.getItemId()) {
				case R.id.action_backup_restore:
					this.processRestoreBackup(isCloud);
					return true;
				case R.id.action_backup_create:
					this.processCreateBackup(isCloud);
					return true;
				case R.id.action_backup_remove:
					this.processRemoveBackup(isCloud);
					return true;
			}
			return false;
		});
	}

	@Override
	protected void onPause() {
		this.unregisterNetworkStateReceiver();
		super.onPause();
	}

	private void selectBackup(View view, int position) {
		this._selectedBackup = ((TextView) view.findViewById(R.id.backup_digest)).getText().toString();
		this._selectedBackupPosition = position;
	}

	private void resetBackupSelection() {
		this._selectedBackup = null;
		this._selectedBackupPosition = -1;
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

	private void showProgress(boolean show, String text) {
		if (show) {
			this._progressDialog.setMessage(text);
			this._progressDialog.show();
		} else {
			if (this._progressDialog.isShowing()) {
				this._progressDialog.dismiss();
			}
		}
	}

	private void processRestoreBackup(boolean isCloud) {
		if (this._processBackupTask != null) {
			return;
		}
		if (this._selectedBackup != null) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(getString(R.string.restore_backup) + Globals.QUESTION_MARK);
			adb.setMessage(this.getString(R.string.restart_confirmation));
			adb.setNegativeButton(R.string.cancel, null);
			adb.setPositiveButton(Names.OK_CAPS, (dialog, which) -> {
				this._processBackupTask = new BackupAndRestoreActivity.RestoreBackupTask(this, this._selectedBackup, isCloud);
				this._processBackupTask.execute((Void) null);
			});
			adb.show();
		} else {
			Toast.makeText(getBaseContext(), this.getString(R.string.backup_and_restore_no_backup_to_restore), Toast.LENGTH_SHORT).show();
		}
	}

	private void processCreateBackup(boolean isCloud) {
		if (this._createBackupTask != null) {
			return;
		}
		this._createBackupTask = new BackupAndRestoreActivity.CreateBackupTask(this, isCloud);
		this._createBackupTask.execute((Void) null);
	}

	private void processRemoveBackup(boolean isCloud) {
		if (this._processBackupTask != null) {
			return;
		}
		if (this._selectedBackup != null) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(getString(R.string.remove) + Globals.QUESTION_MARK);
			adb.setMessage(R.string.backup_remove_confirmation);
			adb.setNegativeButton(R.string.cancel, null);
			adb.setPositiveButton(Names.OK_CAPS, (dialog, which) -> {
				this._processBackupTask = new BackupAndRestoreActivity.RemoveBackupTask(this, this._selectedBackup, isCloud);
				this._processBackupTask.execute((Void) null);
			});
			adb.show();
		} else {
			Toast.makeText(getBaseContext(), this.getString(R.string.backup_and_restore_no_backup_to_delete), Toast.LENGTH_SHORT).show();
		}
	}

	/** Is called by "BackupAndRestoreActivity.RestoreBackupTask" **/
	private String restoreBackup(String digest, boolean isCloud) {
		String result = null;
		BackupModel backup = null;
		if (isCloud) {
			try {
				JSONObject backupObj = this._clientService.DownloadBackup(digest);
				backup = BackupModel.FromJSONObject(backupObj);
			} catch (IOException e) {
				e.printStackTrace();
				result = e.getMessage();
			} catch (RequestError e) {
				e.printStackTrace();
				result = e.getErr();
			} catch (JSONException e) {
				e.printStackTrace();
				result = e.getMessage();
			}
		} else {
			backup = this._backupService.GetByDigest(digest);
		}
		if (backup != null) {
			try {
				this._backupService.RestoreBackup(backup);
			} catch (InvalidBackupException e) {
				e.printStackTrace();
				result = this.getString(R.string.unable_to_restore_backup);
			} catch (ParseException e) {
				e.printStackTrace();
				result = e.getMessage();
			} catch (JSONException e) {
				e.printStackTrace();
				result = e.getMessage();
			}
		}
		return result;
	}

	/** Is called by "BackupAndRestoreActivity.CreateBackupTask" **/
	private BackupsListAdapter.BackupItem createBackup(boolean isCloud) {
		BackupsListAdapter.BackupItem result = null;
		BackupModel backupModel = null;
		try {
			backupModel = this._backupService.PrepareBackup(
				this._eventService.GetAll(),
				this._prefsRepository.backupSettings(),
				this._clientService.GetUserName()
			);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (backupModel != null) {
			if (isCloud) {
				try {
					this._clientService.UploadBackup(backupModel);
					result = backupModel.ToBackupItem();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RequestError e) {
					e.printStackTrace();
				}
			} else {
				if (this._backupService.CreateBackup(backupModel)) {
					result = backupModel.ToBackupItem();
				}
			}
		}
		return result;
	}

	/** Is called by "BackupAndRestoreActivity.RemoveBackupTask" **/
	private String removeBackup(String digest, boolean isCloud) {
		String result = null;
		if (isCloud) {
			try {
				this._clientService.DeleteBackup(digest);
			} catch (IOException e) {
				e.printStackTrace();
				result = e.getMessage();
			} catch (RequestError e) {
				e.printStackTrace();
				result = e.getErr();
			}
		} else {
			if (!this._backupService.DeleteBackup(digest)) {
				result = this.getString(R.string.backup_does_not_exist);
			}
		}
		return result;
	}

	/** Disables 'Cloud' radio button if user is not logged in. **/
	private void checkCloudStorage() {
		if (this._checkAvailableStorageTask != null) {
			return;
		}
		this.showProgress(true, false);
		this._checkAvailableStorageTask = new BackupAndRestoreActivity.CheckCloudStorageTask(this);
		this._checkAvailableStorageTask.execute((Void) null);
	}

	private void loadBackups(boolean isCloud) {
		if (this._loadBackupsTask != null) {
			return;
		}
		this.showProgress(true, false);
		this._loadBackupsTask = new BackupAndRestoreActivity.LoadBackupsTask(this, isCloud);
		this._loadBackupsTask.execute((Void) null);
	}

	private static class CreateBackupTask extends AsyncTask<Void, Void, BackupsListAdapter.BackupItem> {

		private boolean _isCloud;

		private WeakReference<BackupAndRestoreActivity> _cls;

		CreateBackupTask(BackupAndRestoreActivity cls, boolean isCloud) {
			this._cls = new WeakReference<>(cls);
			this._isCloud = isCloud;
		}

		@Override
		protected void onPreExecute() {
			this._cls.get().showProgress(true, this._cls.get().getString(R.string.backuping_data));
			super.onPreExecute();
		}

		@Override
		protected BackupsListAdapter.BackupItem doInBackground(Void... params) {
			return this._cls.get().createBackup(this._isCloud);
		}

		@Override
		protected void onPostExecute(BackupsListAdapter.BackupItem resultModel) {
			String resultMessage;
			if (resultModel != null) {
				resultMessage = this._cls.get().getString(R.string.backup_and_restore_created);
				if (this._cls.get()._backupsListView.getVisibility() != View.VISIBLE) {
					this._cls.get()._backupsListView.setVisibility(View.VISIBLE);
				}
				BackupsListAdapter adapter = (BackupsListAdapter) this._cls.get()._backupsListView.getAdapter();
				if (adapter == null || adapter.isEmpty()) {
					this._cls.get().loadBackups(this._isCloud);
				} else {
					adapter.insert(resultModel, 0);
					adapter.notifyDataSetChanged();
				}
			} else {
				resultMessage = this._cls.get().getString(R.string.backup_and_restore_already_exists);
			}
			AlertDialog.Builder adb = new AlertDialog.Builder(this._cls.get());
			adb.setTitle(this._cls.get().getString(R.string.result));
			adb.setMessage(resultMessage);
			adb.setPositiveButton(Names.OK_CAPS, (dialog, which) -> dialog.dismiss());
			this.taskFinished();
			adb.show();
			super.onPostExecute(resultModel);
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get().showProgress(false, null);
			this._cls.get()._createBackupTask = null;
		}
	}

	private static class RemoveBackupTask extends AsyncTask<Void, Void, String> {

		private boolean _isCloud;
		private String _digest;

		private WeakReference<BackupAndRestoreActivity> _cls;

		RemoveBackupTask(BackupAndRestoreActivity cls, String digest, boolean isCloud) {
			this._cls = new WeakReference<>(cls);
			this._isCloud = isCloud;
			this._digest = digest;
		}

		@Override
		protected void onPreExecute() {
			this._cls.get().showProgress(true, this._cls.get().getString(R.string.removing_backup));
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			return this._cls.get().removeBackup(this._digest, this._isCloud);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				AlertDialog.Builder adb = new AlertDialog.Builder(this._cls.get());
				adb.setTitle(this._cls.get().getString(R.string.result));
				adb.setMessage(result);
				adb.setPositiveButton(Names.OK_CAPS, (dialog, which) -> dialog.dismiss());
				adb.show();
			} else {
				BackupsListAdapter adapter = (BackupsListAdapter) this._cls.get()._backupsListView.getAdapter();
				adapter.remove(adapter.getItem(this._cls.get()._selectedBackupPosition));
				adapter.notifyDataSetChanged();
				if (adapter.isEmpty()) {
					this._cls.get().loadBackups(this._isCloud);
				}
			}
			this.taskFinished();
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get().showProgress(false, null);
			this._cls.get()._processBackupTask = null;
		}
	}

	private static class RestoreBackupTask extends AsyncTask<Void, Void, String> {

		private boolean _isCloud;
		private String _digest;

		private WeakReference<BackupAndRestoreActivity> _cls;

		RestoreBackupTask(BackupAndRestoreActivity cls, String digest, boolean isCloud) {
			this._cls = new WeakReference<>(cls);
			this._isCloud = isCloud;
			this._digest = digest;
		}

		@Override
		protected void onPreExecute() {
			this._cls.get().showProgress(true, this._cls.get().getString(R.string.restoring_backup));
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			return this._cls.get().restoreBackup(this._digest, this._isCloud);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			this.taskFinished();
			if (result != null) {
				AlertDialog.Builder adb = new AlertDialog.Builder(this._cls.get());
				adb.setTitle(this._cls.get().getString(R.string.result));
				adb.setMessage(result);
				adb.setPositiveButton(Names.OK_CAPS, (dialog, which) -> dialog.dismiss());
				adb.show();
			} else {
				this._cls.get().startActivity(new Intent(this._cls.get(), MainActivity.class));
				this._cls.get().finishAffinity();
			}
		}

		@Override
		protected void onCancelled() {
			this.taskFinished();
		}

		private void taskFinished() {
			this._cls.get().showProgress(false, null);
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
			} else {
				this._cls.get().registerNetworkStateReceiver(this._cls.get().getTitle().toString());
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
					this._cls.get(), R.layout.backup_list_item, backups, this._cls.get()._prefsRepository.lang()
				);
				this._cls.get()._backupsListView.setAdapter(adapter);
				this._cls.get()._backupsListView.setVisibility(View.VISIBLE);
				this._cls.get()._noBackupsTextView.setVisibility(View.GONE);
			} else {
				this._cls.get()._backupsListView.setAdapter(null);
				this._cls.get()._backupsListView.setVisibility(View.GONE);
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
							backup.getString(Names.DIGEST),
							backup.getString(Names.TIMESTAMP),
							Globals.EMPTY_STRING,
							backup.getInt(Names.EVENTS_COUNT),
							backup.getString(Names.BACKUP_SIZE),
							backup.getBoolean(Names.CONTAINS_SETTINGS)
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
