package com.yuriylisovskiy.er.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuriylisovskiy.er.R;

public class LoginFragment extends Fragment {

	private Button loginButton;
	private View progressView;
	private AutoCompleteTextView emailView;
	private EditText passwordView;
	private View loginFormView;

	private AsyncTask<Void, Void, Boolean> authTask;

	public LoginFragment() {}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (view != null) {
			this.loginButton = view.findViewById(R.id.email_sign_in_button);
			this.loginButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ProcessAuth();
				}
			});
			this.progressView = view.findViewById(R.id.login_progress);
			this.emailView = view.findViewById(R.id.email);
			this.passwordView = view.findViewById(R.id.password);
			this.passwordView.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s.length() == 0) {
						loginButton.setText(getString(R.string.button_sign_up));
					} else {
						loginButton.setText(getString(R.string.button_sign_in));
					}
				}

				@Override
				public void afterTextChanged(Editable s) {}
			});
			this.loginFormView = view.findViewById(R.id.login_form);
		} else {
			Toast.makeText(getContext(), "Error: view is null", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private boolean isEmailValid(String email) {
		//TODO: Replace this with your own logic
		return email.contains("@");
	}

	private boolean isPasswordValid(String password) {
		//TODO: Replace this with your own logic
		return password.length() > 4;
	}

	private void ProcessAuth() {
		if (this.authTask != null) {
			return;
		}

		// Reset errors.
		this.emailView.setError(null);
		this.passwordView.setError(null);

		// Store values at the time of the login attempt.
		String email = this.emailView.getText().toString();
		String password = this.passwordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			this.passwordView.setError(getString(R.string.error_invalid_password));
			focusView = this.passwordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			this.emailView.setError(getString(R.string.error_field_required));
			focusView = this.emailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			this.emailView.setError(getString(R.string.error_invalid_email));
			focusView = this.emailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			if (TextUtils.isEmpty(this.passwordView.getText())) {
				this.authTask = new RegisterTask(email, password);
			} else {
				this.authTask = new LoginTask(email, password);
			}
			this.authTask.execute((Void) null);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_auth, container, false);
	}

	private void showProgress(final boolean show) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			}
		);
		this.progressView.setVisibility(show ? View.VISIBLE : View.GONE);
		this.progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(
			new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					progressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			}
		);
	}

	public class LoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String email;
		private final String password;

		LoginTask(String email, String password) {
			this.email = email;
			this.password = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			authTask = null;
			showProgress(false);

			Toast.makeText(getContext(), "Processing sign in - " + email + ":" + password, Toast.LENGTH_SHORT).show();

			if (success) {
				// TODO: hide login view and show account info
			} else {
				passwordView.setError(getString(R.string.error_incorrect_password));
				passwordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			authTask = null;
			showProgress(false);
		}
	}

	public class RegisterTask extends AsyncTask<Void, Void, Boolean> {

		private final String email;
		private final String username;

		RegisterTask(String email, String username) {
			this.email = email;
			this.username = username;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			authTask = null;
			showProgress(false);

			Toast.makeText(getContext(), "Processing register - " + email + ":" + username, Toast.LENGTH_SHORT).show();

			if (success) {
				// TODO: hide login view and show account info
			} else {
				passwordView.setError(getString(R.string.error_incorrect_password));
				passwordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			authTask = null;
			showProgress(false);
		}
	}
}
