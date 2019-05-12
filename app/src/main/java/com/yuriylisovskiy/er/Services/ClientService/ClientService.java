package com.yuriylisovskiy.er.Services.ClientService;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuriylisovskiy.er.DataAccess.Models.BackupModel;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class ClientService implements IClientService {

	private static ClientService _instance;

	private Connection _connection;
	private SharedPreferences _prefs;

	private ClientService() {}

	public static ClientService getInstance() {
		if (_instance == null) {
			_instance = new ClientService();
		}
		return _instance;
	}

	public void Initialize(Context ctx) {
		this._connection = new Connection();
		this._prefs = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
		String token = this._prefs.getString("authToken", null);
		if (token != null) {
			this._connection.setHeader("Authorization", "Token " + token);
		}
	}

	private void removeToken() {
		this._prefs.edit().remove("authToken").apply();
		this._connection.removeHeader("Authorization");
	}

	public String GetUserName() {
		String result = null;
		if (this._connection.hasHeader("UserName")) {
			result = this._connection.getHeader("UserName");
		}
		return result;
	}

	public boolean IsLoggedIn() {
		if (this._connection.hasHeader("Authorization")) {
			try {
				this.User();
				return true;
			} catch (IOException ignored) {} catch (RequestError ignored) {}
		}
		return false;
	}

	public void Login(final String username, final String password, boolean remember) throws IOException, RequestError {
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.AUTH_LOGIN, new HashMap<String, String>(){{
				put("username", username);
				put("password", password);
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					String token = response.getJSONObject().getString("key");
					this._connection.setHeader("Authorization", "Token " + token);
					if (remember) {
						this._prefs.edit().putString("authToken", token).apply();
					}
					JSONObject user = this.User();
					this._connection.setHeader("UserName", user.getString("username"));
					break;
				case Connection.Status.HTTP_400_BAD_REQUEST:
					throw new RequestError("Credentials error", status);
				default:
					throw new RequestError("Login failed", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void Logout() throws IOException, RequestError {
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.AUTH_LOGOUT, null);
			if (response.getStatus() != Connection.Status.HTTP_200_OK) {
				throw new RequestError("Logout error", response.getStatus());
			}
			this.removeToken();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void RegisterAccount(final String username, final String email) throws IOException, RequestError {
		this.removeToken();
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.ACCOUNT_CREATE, new HashMap<String, String>(){{
				put("username", username);
				put("email", email);
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_201_CREATED:
					break;
				case Connection.Status.HTTP_400_BAD_REQUEST:
					JSONObject jsonData = response.getJSONObject();
					assert jsonData != null;
					if (jsonData.has("non_field_errors")) {
						if (jsonData.getString("non_field_errors").contains("username")) {
							throw new RequestError("Register failed, username is not provided", status);
						} else if (jsonData.getString("non_field_errors").contains("email")) {
							throw new RequestError("Register failed, email is not provided", status);
						}
					}
					throw new RequestError("Registration failed, user with this email address already exists", status);
				default:
					throw new RequestError("Registration failed", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}

	public JSONObject User() throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this._connection.Get(Routes.ACCOUNT_DETAILS, null);
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					responseData = response.getJSONObject();
					assert responseData != null;
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required error", status);
				default:
					throw new RequestError("User retrieving error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONObject UpdateUser(final int maxBackups) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.ACCOUNT_EDIT, new HashMap<String, String>(){{
				put("max_backups", Integer.toString(maxBackups));
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_201_CREATED:
					responseData = response.getJSONObject();
					assert responseData != null;
					break;
				default:
					throw new RequestError("User updating error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONObject RequestCode(final String email) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.ACCOUNT_SEND_CODE, new HashMap<String, String>() {{
				put("email", email);
			}});
			switch (response.getStatus()) {
				case Connection.Status.HTTP_201_CREATED:
					responseData = response.getJSONObject();
					assert responseData != null;
					break;
				default:
					throw new RequestError(response.getError().getString("detail"), response.getStatus());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONObject ResetPassword(final String email, final String newPassword, final String newPasswordConfirm, final String code) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.ACCOUNT_PASSWORD_RESET, new HashMap<String, String>(){{
				put("email", email);
				put("new_password", newPassword);
				put("new_password_confirm", newPasswordConfirm);
				put("confirmation_code", code);
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_201_CREATED:
					responseData = response.getJSONObject();
					assert responseData != null;
					this.removeToken();
					break;
				default:
					throw new RequestError(response.getError().getString("detail"), status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONArray Backups() throws IOException, RequestError {
		JSONArray responseData = null;
		try {
			Connection.JsonResponse response = this._connection.Get(Routes.BACKUPS, null);
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					responseData = response.getJSONArray();
					assert responseData != null;
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required error", status);
				default:
					throw new RequestError("Reading backups error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public void UploadBackup(final BackupModel backup) throws IOException, RequestError {
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.BACKUP_CREATE, new HashMap<String, String>(){{
				put("timestamp", backup.Timestamp);
				put("digest", backup.Digest);
				put("backup", backup.Backup);
				put("backup_size", backup.Size);
				put("events_count", String.valueOf(backup.EventsAmount));
				put("contains_settings", String.valueOf(backup.ContainsSettings));
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required", status);
				case Connection.Status.HTTP_400_BAD_REQUEST:
					throw new RequestError("Backup already exists", status);
				default:
					throw new RequestError("Uploading backup", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject DownloadBackup(String backupHash) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this._connection.Get(Routes.BACKUP_DETAILS + backupHash, null);
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					responseData = response.getJSONObject();
					assert responseData != null;
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required error", status);
				default:
					throw new RequestError("Backup downloading error", status);
			}
		} catch (JSONException exc) {
			exc.printStackTrace();
		} catch (AssertionError exc) {
			exc.printStackTrace();
		}
		return responseData;
	}

	public void DeleteBackup(String backupHash) throws IOException, RequestError {
		try {
			Connection.JsonResponse response = this._connection.Post(Routes.BACKUP_DELETE + backupHash, null);
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					break;
				case Connection.Status.HTTP_400_BAD_REQUEST:
					throw new RequestError("Authentication required error", status);
				default:
					throw new RequestError("Backup deleting error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
