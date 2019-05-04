package com.yuriylisovskiy.er.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yuriylisovskiy.er.Fragments.Interfaces.IClientFragment;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;
import com.yuriylisovskiy.er.Util.InputValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class LoginFragment extends Fragment implements IClientFragment {

	private TabLayout.Tab _tabItem;

	private View _progressView;
	private EditText _userNameInput;
	private EditText _passwordInput;
	private CheckBox _rememberUser;
	private View _loginFormView;

	private View _userProfile;
	private TextView _profileUserName;
	private TextView _profileUserEmail;

	private IClientService _client;

	private AsyncTask<Void, Void, Boolean> _authTask;

	public LoginFragment() {}

	@Override
	public void setClientService(IClientService clientService, Context baseCtx) {
		this._client = clientService;
	}

	public void setArguments(View tabs) {
		this._tabItem = ((TabLayout) tabs).getTabAt(0);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = this.getView();
		if (view != null) {
			Button loginButton = view.findViewById(R.id.account_sif_sign_in_button);
			loginButton.setOnClickListener(login -> this.processLogin());
			Button logoutButton = view.findViewById(R.id.account_sif_sign_out_button);
			logoutButton.setOnClickListener(logout -> processLogout());
			this._progressView = view.findViewById(R.id.account_sif_progress);
			this._userNameInput = view.findViewById(R.id.account_sif_username);
			this._userNameInput.requestFocus();
			this._passwordInput = view.findViewById(R.id.account_sif_password);
			this._passwordInput.setOnEditorActionListener((v, actionId, event) -> {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					this.processLogin();
					handled = true;
				}
				return handled;
			});
			this._rememberUser = view.findViewById(R.id.account_sif_remember_me);
			this._rememberUser.setChecked(true);
			this._loginFormView = view.findViewById(R.id.account_sif_form);

			this._userProfile = view.findViewById(R.id.account_sif_user_profile);
			this._profileUserName = view.findViewById(R.id.account_sif_profile_name);
			this._profileUserEmail = view.findViewById(R.id.account_sif_profile_email);

		} else {
			Toast.makeText(this.getContext(), "Error: login fragment's view is null", Toast.LENGTH_SHORT).show();
		}
		this.showProgress(true, false);
		new LoginFragment.GetUserTask(this).execute();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sign_in, container, false);
	}

	private void setLoggedInData(boolean isLoggedIn, String username, String email) {
		if (isLoggedIn) {
			this._profileUserName.setText(username);
			this._profileUserEmail.setText(email);
			Objects.requireNonNull(this._tabItem).setText(username);
		} else {
			Objects.requireNonNull(this._tabItem).setText(this.getString(R.string.tab_login));
		}
	}

	private void processLogin() {
		if (this._authTask != null) {
			return;
		}

		// Reset errors.
		this._userNameInput.setError(null);
		this._passwordInput.setError(null);

		// Store values at the time of the login attempt.
		String username = this._userNameInput.getText().toString();
		String password = this._passwordInput.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (InputValidator.isEmpty(password)) {
			this._passwordInput.setError(getString(R.string.error_field_required));
			focusView = this._passwordInput;
			cancel = true;
		} else if (!InputValidator.isPasswordValid(password)) {
			this._passwordInput.setError(getString(R.string.error_invalid_password));
			focusView = this._passwordInput;
			cancel = true;
		}

		// Check for a valid username.
		if (InputValidator.isEmpty(username)) {
			this._userNameInput.setError(getString(R.string.error_field_required));
			focusView = this._userNameInput;
			cancel = true;
		} else if (!InputValidator.isUserNameValid(username)) {
			this._userNameInput.setError(getString(R.string.error_invalid_username));
			focusView = this._userNameInput;
			cancel = true;
		}
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			this.showProgress(true, false);
			this._authTask = new LoginFragment.LoginTask(username, password, this._rememberUser.isChecked(), this);
			this._authTask.execute((Void) null);
		}
	}

	private void processLogout() {
		this.showProgress(true, true);
		new LoginFragment.LogoutTask(this, this.getContext()).execute();
	}

	private void showProgress(final boolean show, final boolean isLoggedIn) {
		int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);
		if (isLoggedIn) {
			this._userProfile.setVisibility(show ? View.GONE : View.VISIBLE);
			this._userProfile.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
					new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							_userProfile.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					}
			);
		} else {
			this._loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			this._loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
					new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							_loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					}
			);
		}
		this._progressView.setVisibility(show ? View.VISIBLE : View.GONE);
		this._progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			}
		);
	}

	private static class LoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String _username;
		private final String _password;
		private final boolean _remember;

		private String _profileUsername;
		private String _profileEmail;

		private final LoginFragment _cls;

		LoginTask(String username, String password, boolean remember, LoginFragment cls) {
			this._username = username;
			this._password = password;
			this._remember = remember;
			this._cls = cls;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = true;
			try {
				this._cls._client.Login(this._username, this._password, this._remember);
				JSONObject user = this._cls._client.User();
				this._profileUsername = user.getString("username");
				this._profileEmail = user.getString("email");
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
			} catch (RequestError e) {
				e.printStackTrace();
				result = false;
			} catch (JSONException e) {
				e.printStackTrace();
				result = false;
			}
			return result;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			this._cls._authTask = null;
			if (success) {
				this._cls.setLoggedInData(true, this._profileUsername, this._profileEmail);
			} else {
				this._cls._passwordInput.setError(this._cls.getString(R.string.error_incorrect_credentials));
				this._cls._passwordInput.requestFocus();
			}
			this._cls.showProgress(false, success);
		}

		@Override
		protected void onCancelled() {
			this._cls._authTask = null;
			this._cls.showProgress(false, false);
		}
	}

	private static class LogoutTask extends AsyncTask<Void, Void, String> {

		private final LoginFragment _cls;
		private WeakReference<Context> _baseCtx;

		LogoutTask(LoginFragment cls, Context baseCtx) {
			this._cls = cls;
			this._baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				this._cls._client.Logout();
			} catch (IOException e) {
				e.printStackTrace();
				result = e.getMessage();
			} catch (RequestError e) {
				e.printStackTrace();
				result = e.getErr();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final String resultMsg) {
			boolean isLoggedIn = false;
			if (resultMsg != null) {
				Toast.makeText(this._baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				isLoggedIn = true;
			} else {
				this._cls.setLoggedInData(false, null, null);
			}
			this._cls._authTask = null;
			this._cls.showProgress(false, isLoggedIn);
		}

		@Override
		protected void onCancelled() {
			this._cls._authTask = null;
			this._cls.showProgress(false, false);
		}
	}

	private static class GetUserTask extends AsyncTask<Void, Void, Boolean> {

		private String _username;
		private String _email;

		private final LoginFragment _cls;

		GetUserTask(LoginFragment cls) {
			this._cls = cls;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
			try {
				JSONObject user = this._cls._client.User();
				this._username = user.getString("username");
				this._email = user.getString("email");
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RequestError e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			this._cls._authTask = null;
			this._cls.showProgress(false, success);
			this._cls.setLoggedInData(success, this._username, this._email);
		}

		@Override
		protected void onCancelled() {
			this._cls._authTask = null;
			this._cls.showProgress(false, false);
		}
	}
}
