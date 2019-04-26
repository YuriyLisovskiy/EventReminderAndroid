package com.yuriylisovskiy.er.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.yuriylisovskiy.er.Fragments.Interfaces.IClientFragment;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class ResetPasswordFragment extends Fragment implements IClientFragment {

	private IClientService client;

	private View progressView;

	private AsyncTask<Void, Void, Boolean> authTask;

	public ResetPasswordFragment() {}

	@Override
	public void setClientService(IClientService clientService, Context baseCtx) {
		this.client = clientService;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_reset_password, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (view != null) {
			Button resetButton = view.findViewById(R.id.reset_password_button);
			resetButton.setOnClickListener(login -> ProcessResetPassword());
			Button confirmButton = view.findViewById(R.id.confirm_reset_password_button);
			confirmButton.setOnClickListener(logout -> ProcessConfirmReset());
			this.progressView = view.findViewById(R.id.login_progress);

			this.usernameView = view.findViewById(R.id.username);
			this.usernameView.requestFocus();
			this.passwordView = view.findViewById(R.id.password);
			this.rememberUser = view.findViewById(R.id.remember_user);
			this.rememberUser.setChecked(true);
			this.loginFormView = view.findViewById(R.id.login_form);

			this.userProfile = view.findViewById(R.id.user_profile);
			this.profileUserName = view.findViewById(R.id.profile_user_name);
			this.profileUserEmail = view.findViewById(R.id.profile_user_email);

		} else {
			Toast.makeText(getContext(), "Error: reset password fragment's view is null", Toast.LENGTH_SHORT).show();
		}
		showProgress(true, false);
		new ResetPasswordFragment.GetConfirmationTask(this, getContext()).execute();
	}

	private void showProgress(final boolean show, final boolean isConfirmationView) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		if (isConfirmationView) {
			userProfile.setVisibility(show ? View.GONE : View.VISIBLE);
			userProfile.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
					new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							userProfile.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					}
			);
		} else {
			loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			loginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
					new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					}
			);
		}
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

	private void ProcessResetPassword() {

	}

	private void ProcessConfirmReset() {

	}

	public static class GetConfirmationTask extends AsyncTask<Void, Void, String> {

		private final ResetPasswordFragment cls;
		private WeakReference<Context> baseCtx;

		GetConfirmationTask(ResetPasswordFragment cls, Context baseCtx) {
			this.cls = cls;
			this.baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				this.cls.client.Logout();
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
				Toast.makeText(this.baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				isLoggedIn = true;
			} else {
				this.cls.setLoggedInData(false, null, null);
			}
			this.cls.authTask = null;
			this.cls.showProgress(false, isLoggedIn);
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false, false);
		}
	}
}
