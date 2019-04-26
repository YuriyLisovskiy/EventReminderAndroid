package com.yuriylisovskiy.er.Services.ClientService;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class ClientService implements IClientService {

	private static ClientService instance;

	private Connection connection;
	private SharedPreferences prefs;

	private ClientService() {}

	public static ClientService getInstance() {
		if (instance == null) {
			instance = new ClientService();
		}
		return instance;
	}

	public void Initialize(Context ctx) {
		connection = new Connection();
		prefs = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
		String token = prefs.getString("authToken", null);
		if (token != null) {
			connection.setHeader("Authorization", "Token " + token);
		}
	}

	private void removeToken() {
		this.prefs.edit().remove("authToken").apply();
		this.connection.removeHeader("Authorization");
	}

	public void Login(final String username, final String password, boolean remember) throws IOException, RequestError {
		try {
			Connection.JsonResponse response = this.connection.Post(Routes.AUTH_LOGIN, new HashMap<String, String>(){{
				put("username", username);
				put("password", password);
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					String token = response.getData().getString("key");
					this.connection.setHeader("Authorization", "Token " + token);
					if (remember) {
						this.prefs.edit().putString("authToken", token).apply();
					}
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
			Connection.JsonResponse response = this.connection.Post(Routes.AUTH_LOGOUT, null);
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
			Connection.JsonResponse response = this.connection.Post(Routes.ACCOUNT_CREATE, new HashMap<String, String>(){{
				put("username", username);
				put("email", email);
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_201_CREATED:
					break;
				case Connection.Status.HTTP_400_BAD_REQUEST:
					JSONObject jsonData = response.getData();
					if (jsonData.has("non_field_errors")) {
						if (jsonData.getString("non_field_errors").contains("username")) {
							throw new RequestError("Register failed, username is not provided", status);
						} else if (jsonData.getString("non_field_errors").contains("email")) {
							throw new RequestError("Register failed, email is not provided", status);
						}
					}
					throw new RequestError("Register failed, user already exists", status);
				default:
					throw new RequestError("Register failed error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject User() throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this.connection.Get(Routes.ACCOUNT_DETAILS, null);
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					responseData = response.getData();
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required error", status);
				default:
					throw new RequestError("User retrieving error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONObject UpdateUser(final int maxBackups) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this.connection.Post(Routes.ACCOUNT_EDIT, new HashMap<String, String>(){{
				put("max_backups", Integer.toString(maxBackups));
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_201_CREATED:
					responseData = response.getData();
					break;
				default:
					throw new RequestError("User updating error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONObject RequestCode(final String email) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this.connection.Post(Routes.ACCOUNT_SEND_CODE, new HashMap<String, String>() {{
				put("email", email);
			}});
			switch (response.getStatus()) {
				case Connection.Status.HTTP_201_CREATED:
					responseData = response.getData();
					break;
				default:
					throw new RequestError("Request code error", response.getStatus());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONObject ResetPassword(final String email, final String newPassword, final String newPasswordConfirm, final String code) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this.connection.Post(Routes.ACCOUNT_PASSWORD_RESET, new HashMap<String, String>(){{
				put("email", email);
				put("new_password", newPassword);
				put("new_password_confirm", newPasswordConfirm);
				put("verification_code", code);
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_201_CREATED:
					responseData = response.getData();
					break;
				default:
					throw new RequestError(response.getError().getString("detail"), status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public JSONObject Backups() throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this.connection.Get(Routes.BACKUPS, null);
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					responseData = response.getData();
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required error", status);
				default:
					throw new RequestError("Reading backups error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public void UploadBackup(final String backup, final String digest, final String timestamp) throws IOException, RequestError {
		try {
			Connection.JsonResponse response = this.connection.Post(Routes.BACKUP_CREATE, new HashMap<String, String>(){{
				put("timestamp", timestamp);
				put("digest", digest);
				put("backup", backup);
			}});
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required error", status);
				case Connection.Status.HTTP_400_BAD_REQUEST:
					throw new RequestError("Backup already exists error", status);
				default:
					throw new RequestError("Uploading backup error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject DownloadBackup(String backupHash) throws IOException, RequestError {
		JSONObject responseData = null;
		try {
			Connection.JsonResponse response = this.connection.Get(Routes.BACKUP_DETAILS + backupHash, null);
			int status = response.getStatus();
			switch (status) {
				case Connection.Status.HTTP_200_OK:
					responseData = response.getData();
					break;
				case Connection.Status.HTTP_401_UNAUTHORIZED:
					throw new RequestError("Authentication required error", status);
				default:
					throw new RequestError("Backup downloading error", status);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseData;
	}

	public void DeleteBackup(String backupHash) throws IOException, RequestError {
		try {
			Connection.JsonResponse response = this.connection.Post(Routes.BACKUP_DELETE + backupHash, null);
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
