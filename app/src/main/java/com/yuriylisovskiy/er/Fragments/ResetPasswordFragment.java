package com.yuriylisovskiy.er.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yuriylisovskiy.er.Fragments.Interfaces.IClientFragment;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;
import com.yuriylisovskiy.er.Util.InputValidator;
import com.yuriylisovskiy.er.Util.Utils;
import com.yuriylisovskiy.er.Widgets.PinEditText;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class ResetPasswordFragment extends Fragment implements IClientFragment {

	private IClientService client;

	private View progressView;
	private View resetPasswordView;
	private View confirmResetPasswordView;
	private AutoCompleteTextView userEmailInput;
	private EditText newPasswordInput;
	private EditText newPasswordRepeatInput;
	private PinEditText confirmationCodeInput;

	private AsyncTask<Void, Void, String> authTask;

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
			Button resetButton = view.findViewById(R.id.account_rpf_reset_button);
			resetButton.setOnClickListener(login -> ProcessResetPassword());
			Button confirmButton = view.findViewById(R.id.account_rpf_confirm_button);
			confirmButton.setOnClickListener(logout -> ProcessConfirmReset());
			this.progressView = view.findViewById(R.id.account_rpf_progress);
			this.resetPasswordView = view.findViewById(R.id.account_rpf_reset_view);
			this.confirmResetPasswordView = view.findViewById(R.id.account_rpf_confirm_view);
			this.userEmailInput = view.findViewById(R.id.account_rpf_email);
			this.newPasswordInput = view.findViewById(R.id.account_rpf_new_password);
			this.newPasswordRepeatInput = view.findViewById(R.id.account_rpf_new_password_repeat);
			this.newPasswordRepeatInput.setOnEditorActionListener((v, actionId, event) -> {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					ProcessResetPassword();
					handled = true;
				}
				return handled;
			});
			this.confirmationCodeInput = view.findViewById(R.id.account_rpf_confirmation_code);
			this.confirmationCodeInput.setOnEditorActionListener((v, actionId, event) -> {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					ProcessConfirmReset();
					handled = true;
				}
				return handled;
			});
		} else {
			Toast.makeText(getContext(), "Error: reset password fragment's view is null", Toast.LENGTH_SHORT).show();
		}
	}

	public void resetInputs() {
		this.userEmailInput.setText("");
		this.newPasswordInput.setText("");
		this.newPasswordRepeatInput.setText("");
		this.confirmationCodeInput.setText("");
	}

	private void showProgress(final boolean show, final boolean isConfirmationView) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		if (isConfirmationView) {
			confirmResetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
			confirmResetPasswordView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
				new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						confirmResetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
						if (!show) {
							confirmResetPasswordView.requestFocus();
						}
					}
				}
			);
		} else {
			resetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
			resetPasswordView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
				new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
					resetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
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
		if (this.authTask != null) {
			return;
		}

		// Reset errors.
		this.userEmailInput.setError(null);
		this.newPasswordInput.setError(null);
		this.newPasswordRepeatInput.setError(null);

		// Store values at the time of the login attempt.
		String email = this.userEmailInput.getText().toString();
		String newPassword = this.newPasswordInput.getText().toString();
		String newPasswordRepeat = this.newPasswordRepeatInput.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (InputValidator.isEmpty(email)) {
			this.userEmailInput.setError(getString(R.string.error_field_required));
			focusView = this.userEmailInput;
			cancel = true;
		} else if (!InputValidator.isEmailValid(email)) {
			this.userEmailInput.setError(getString(R.string.error_invalid_email));
			focusView = this.userEmailInput;
			cancel = true;
		}

		// Check for a valid new password.
		if (InputValidator.isEmpty(newPassword)) {
			this.newPasswordInput.setError(getString(R.string.error_field_required));
			focusView = this.newPasswordInput;
			cancel = true;
		} else if (!InputValidator.isPasswordValid(newPassword)) {
			this.newPasswordInput.setError(getString(R.string.error_invalid_password));
			focusView = this.newPasswordInput;
			cancel = true;
		}

		// Check for a valid new repeated password.
		if (InputValidator.isEmpty(newPasswordRepeat)) {
			this.newPasswordRepeatInput.setError(getString(R.string.error_field_required));
			focusView = this.newPasswordRepeatInput;
			cancel = true;
		} else if (!InputValidator.isPasswordValid(newPasswordRepeat)) {
			this.newPasswordRepeatInput.setError(getString(R.string.error_invalid_password));
			focusView = this.newPasswordRepeatInput;
			cancel = true;
		} else if (!newPassword.equals(newPasswordRepeat)) {
			this.newPasswordRepeatInput.setError(getString(R.string.error_repeat_password_failed));
			focusView = this.newPasswordRepeatInput;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true, false);
			TextView tw = this.confirmResetPasswordView.findViewById(R.id.account_rpf_enter_code_details);
			assert tw != null;
			tw.setText(getString(R.string.account_rpf_enter_code_details, email));
			this.authTask = new ResetPasswordFragment.GetConfirmationCodeTask(
				email, this, getContext()
			);
			this.authTask.execute((Void) null);
		}
	}

	private void ProcessConfirmReset() {
		if (this.authTask != null) {
			return;
		}

		this.confirmationCodeInput.setError(null);

		String email = this.userEmailInput.getText().toString();
		String newPassword = this.newPasswordInput.getText().toString();
		String newPasswordRepeat = this.newPasswordRepeatInput.getText().toString();
		String confirmationCode = this.confirmationCodeInput.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (InputValidator.isEmpty(confirmationCode)) {
			this.confirmationCodeInput.setError(getString(R.string.error_field_required));
			focusView = this.confirmationCodeInput;
			cancel = true;
		} else if (!InputValidator.isConfirmationCodeValid(confirmationCode)) {
			this.confirmationCodeInput.setError(getString(R.string.error_invalid_confirmation_code));
			focusView = this.confirmationCodeInput;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			Utils.HideKeyboard(getContext(), this.confirmResetPasswordView);
			showProgress(true, true);
			this.authTask = new ResetPasswordFragment.PasswordResetTask(
				email, newPassword, newPasswordRepeat, confirmationCode, this, getContext()
			);
			this.authTask.execute((Void) null);
		}
	}

	public static class GetConfirmationCodeTask extends AsyncTask<Void, Void, String> {

		private String email;

		private final ResetPasswordFragment cls;
		private WeakReference<Context> baseCtx;

		GetConfirmationCodeTask(String email, ResetPasswordFragment cls, Context baseCtx) {
			this.email = email;
			this.cls = cls;
			this.baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				this.cls.client.RequestCode(this.email);
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
			boolean showConfirmation = true;
			if (resultMsg != null) {
				Toast.makeText(this.baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				showConfirmation = false;
			}
			this.cls.authTask = null;
			this.cls.showProgress(false, showConfirmation);
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false, false);
		}
	}

	public static class PasswordResetTask extends AsyncTask<Void, Void, String> {

		private String email;
		private String newPassword;
		private String newPasswordRepeat;
		private String confirmationCode;

		private final ResetPasswordFragment cls;
		private WeakReference<Context> baseCtx;

		PasswordResetTask(String email, String newPassword, String newPasswordRepeat, String confirmationCode, ResetPasswordFragment cls, Context baseCtx) {
			this.email = email;
			this.newPassword = newPassword;
			this.newPasswordRepeat = newPasswordRepeat;
			this.confirmationCode = confirmationCode;
			this.cls = cls;
			this.baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				this.cls.client.ResetPassword(this.email, this.newPassword, this.newPasswordRepeat, this.confirmationCode);
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
			boolean showConfirmation = false;
			if (resultMsg != null) {
				Toast.makeText(this.baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				showConfirmation = true;
			} else {
				this.cls.resetInputs();
				Toast.makeText(this.baseCtx.get(), R.string.account_rpf_reset_success, Toast.LENGTH_LONG).show();
			}
			this.cls.authTask = null;
			this.cls.showProgress(false, showConfirmation);
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false, false);
		}
	}
}
