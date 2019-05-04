package com.yuriylisovskiy.er.Services.ClientService;

import android.content.Context;

import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;

import org.json.JSONObject;

import java.io.IOException;

public interface IClientService {

	void Initialize(Context ctx);

	void Login(final String username, final String password, boolean remember) throws IOException, RequestError;

	void Logout() throws IOException, RequestError;

	void RegisterAccount(final String username, final String email) throws IOException, RequestError;

	JSONObject User() throws IOException, RequestError;

	JSONObject UpdateUser(final int maxBackups) throws IOException, RequestError;

	JSONObject RequestCode(final String email) throws IOException, RequestError;

	JSONObject ResetPassword(final String email, final String newPassword, final String newPasswordConfirm, final String code) throws IOException, RequestError;

	JSONObject Backups() throws IOException, RequestError;

	void UploadBackup(final String backup, final String digest, final String timestamp) throws IOException, RequestError;

	JSONObject DownloadBackup(String backupHash) throws IOException, RequestError;

	void DeleteBackup(String backupHash) throws IOException, RequestError;
}
