package com.yuriylisovskiy.er.client;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuriylisovskiy.er.client.exceptions.RequestError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class Client {

	private Connection connection;
	private SharedPreferences prefs;

	public Client(Context ctx) {
		this.connection = new Connection();
		this.prefs = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
		String token = this.prefs.getString("authToken", null);
		if (token != null) {
			this.connection.setHeader("Authorization", "Token " + token);
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

	public void RegisterAccount(String username, String email) {
		// TODO: implement method
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

	public void ResetPassword(String email, String newPassword, String newPasswordConfirm, String code) {
		// TODO: implement method
	}

	public void Backups() {
		// TODO: implement method
	}

	public void UploadBackup(String backup) {
		// TODO: implement method
	}

	public void DownloadBackup(String backupHash) {
		// TODO: implement method
	}

	public void DeleteBackup(String backupHash) {
		// TODO: implement method
	}

}
