package com.yuriylisovskiy.er.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.yuriylisovskiy.er.client.Client;
import com.yuriylisovskiy.er.client.exceptions.RequestError;

import java.io.IOException;

public class RegisterFragment extends Fragment {

	private View progressView;
	private AutoCompleteTextView emailView;
	private EditText usernameView;
	private View registerFormView;

	private Client client;

	private AsyncTask<Void, Void, Boolean> authTask;

	public RegisterFragment() {
		this.client = Client.getInstance();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (view != null) {
			Button registerButton = view.findViewById(R.id.sign_up_button);
			registerButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ProcessRegister();
				}
			});
			this.progressView = view.findViewById(R.id.register_progress);
			this.usernameView = view.findViewById(R.id.username);
			this.usernameView.requestFocus();
			this.emailView = view.findViewById(R.id.email);
			this.registerFormView = view.findViewById(R.id.register_form);
		} else {
			Toast.makeText(getContext(), "Error: register fragment's view is null", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isUserNameValid(String username) {
		//TODO: Replace this with your own logic
		return username.length() > 4;
	}

	private boolean isEmailValid(String email) {
		//TODO: Replace this with your own logic
		return email.length() > 4;
	}

	private void ProcessRegister() {
		if (this.authTask != null) {
			return;
		}

		this.usernameView.setError(null);
		this.emailView.setError(null);

		String username = this.usernameView.getText().toString();
		String email = this.emailView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email.
		if (TextUtils.isEmpty(email)) {
			this.emailView.setError(getString(R.string.error_field_required));
			focusView = this.emailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			this.emailView.setError(getString(R.string.error_invalid_email));
			focusView = this.emailView;
			cancel = true;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(username)) {
			this.usernameView.setError(getString(R.string.error_field_required));
			focusView = this.usernameView;
			cancel = true;
		} else if (!isUserNameValid(username)) {
			this.usernameView.setError(getString(R.string.error_invalid_username));
			focusView = this.usernameView;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true);
			this.authTask = new RegisterTask(email, username, this);
			this.authTask.execute((Void) null);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sign_up, container, false);
	}

	private void showProgress(final boolean show) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		this.registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		this.registerFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
				new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

	public static class RegisterTask extends AsyncTask<Void, Void, Boolean> {

		private final String email;
		private final String username;
		private RegisterFragment cls;

		RegisterTask(String email, String username, RegisterFragment cls) {
			this.email = email;
			this.username = username;
			this.cls = cls;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				this.cls.client.RegisterAccount(this.username, this.email);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (RequestError e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			this.cls.authTask = null;
			this.cls.showProgress(false);
			if (success) {
				// TODO: hide login view and show account info
			} else {
				// TODO: show error returned by the server
			}
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false);
		}
	}
}
