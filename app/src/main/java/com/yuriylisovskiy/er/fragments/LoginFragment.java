package com.yuriylisovskiy.er.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

	private AuthTask authTask;

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
					Login();
				}
			});

			this.progressView = view.findViewById(R.id.login_progress);
			this.emailView = view.findViewById(R.id.email);
			this.passwordView = view.findViewById(R.id.password);
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

	private void Login() {
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
			this.authTask = new AuthTask(email, password);
			this.authTask.execute((Void) null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

		loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		loginFormView.animate().setDuration(shortAnimTime).alpha(
				show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			}
		});

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

	public class AuthTask extends AsyncTask<Void, Void, Boolean> {

		private final String email;
		private final String password;

		AuthTask(String email, String password) {
			this.email = email;
			this.password = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			final String[] DUMMY_CREDENTIALS = new String[]{
					"foo@example.com:hello", "bar@example.com:world"
			};

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(this.email)) {

					// TODO: Send login request

					return pieces[1].equals(this.password);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			authTask = null;
			showProgress(false);

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
