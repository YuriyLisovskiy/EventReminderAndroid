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

	private IClientService _client;

	private View _progressView;
	private View _resetPasswordView;
	private View _confirmResetPasswordView;
	private AutoCompleteTextView _userEmailInput;
	private EditText _newPasswordInput;
	private EditText _newPasswordRepeatInput;
	private PinEditText _confirmationCodeInput;

	private AsyncTask<Void, Void, String> _authTask;

	public ResetPasswordFragment() {}

	@Override
	public void setClientService(IClientService clientService, Context baseCtx) {
		this._client = clientService;
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
			resetButton.setOnClickListener(login -> this.processResetPassword());
			Button confirmButton = view.findViewById(R.id.account_rpf_confirm_button);
			confirmButton.setOnClickListener(logout -> this.processConfirmReset());
			this._progressView = view.findViewById(R.id.account_rpf_progress);
			this._resetPasswordView = view.findViewById(R.id.account_rpf_reset_view);
			this._confirmResetPasswordView = view.findViewById(R.id.account_rpf_confirm_view);
			this._userEmailInput = view.findViewById(R.id.account_rpf_email);
			this._newPasswordInput = view.findViewById(R.id.account_rpf_new_password);
			this._newPasswordRepeatInput = view.findViewById(R.id.account_rpf_new_password_repeat);
			this._newPasswordRepeatInput.setOnEditorActionListener((v, actionId, event) -> {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					this.processResetPassword();
					handled = true;
				}
				return handled;
			});
			this._confirmationCodeInput = view.findViewById(R.id.account_rpf_confirmation_code);
			this._confirmationCodeInput.setOnEditorActionListener((v, actionId, event) -> {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					this.processConfirmReset();
					handled = true;
				}
				return handled;
			});
		} else {
			Toast.makeText(this.getContext(), "Error: reset password fragment's view is null", Toast.LENGTH_SHORT).show();
		}
	}

	public void resetInputs() {
		this._userEmailInput.setText("");
		this._newPasswordInput.setText("");
		this._newPasswordRepeatInput.setText("");
		this._confirmationCodeInput.setText("");
	}

	private void showProgress(final boolean show, final boolean isConfirmationView) {
		int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);
		if (isConfirmationView) {
			this._confirmResetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
			this._confirmResetPasswordView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
				new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						_confirmResetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
						if (!show) {
							_confirmResetPasswordView.requestFocus();
						}
					}
				}
			);
		} else {
			this._resetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
			this._resetPasswordView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
				new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						_resetPasswordView.setVisibility(show ? View.GONE : View.VISIBLE);
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

	private void processResetPassword() {
		if (this._authTask != null) {
			return;
		}

		// Reset errors.
		this._userEmailInput.setError(null);
		this._newPasswordInput.setError(null);
		this._newPasswordRepeatInput.setError(null);

		// Store values at the time of the login attempt.
		String email = this._userEmailInput.getText().toString();
		String newPassword = this._newPasswordInput.getText().toString();
		String newPasswordRepeat = this._newPasswordRepeatInput.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (InputValidator.isEmpty(email)) {
			this._userEmailInput.setError(getString(R.string.error_field_required));
			focusView = this._userEmailInput;
			cancel = true;
		} else if (!InputValidator.isEmailValid(email)) {
			this._userEmailInput.setError(getString(R.string.error_invalid_email));
			focusView = this._userEmailInput;
			cancel = true;
		}

		// Check for a valid new password.
		if (InputValidator.isEmpty(newPassword)) {
			this._newPasswordInput.setError(getString(R.string.error_field_required));
			focusView = this._newPasswordInput;
			cancel = true;
		} else if (!InputValidator.isPasswordValid(newPassword)) {
			this._newPasswordInput.setError(getString(R.string.error_invalid_password));
			focusView = this._newPasswordInput;
			cancel = true;
		}

		// Check for a valid new repeated password.
		if (InputValidator.isEmpty(newPasswordRepeat)) {
			this._newPasswordRepeatInput.setError(getString(R.string.error_field_required));
			focusView = this._newPasswordRepeatInput;
			cancel = true;
		} else if (!InputValidator.isPasswordValid(newPasswordRepeat)) {
			this._newPasswordRepeatInput.setError(getString(R.string.error_invalid_password));
			focusView = this._newPasswordRepeatInput;
			cancel = true;
		} else if (!newPassword.equals(newPasswordRepeat)) {
			this._newPasswordRepeatInput.setError(getString(R.string.error_repeat_password_failed));
			focusView = this._newPasswordRepeatInput;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			this.showProgress(true, false);
			TextView tw = this._confirmResetPasswordView.findViewById(R.id.account_rpf_enter_code_details);
			assert tw != null;
			tw.setText(getString(R.string.account_rpf_enter_code_details, email));
			this._authTask = new ResetPasswordFragment.GetConfirmationCodeTask(
				email, this, this.getContext()
			);
			this._authTask.execute((Void) null);
		}
	}

	private void processConfirmReset() {
		if (this._authTask != null) {
			return;
		}

		this._confirmationCodeInput.setError(null);

		String email = this._userEmailInput.getText().toString();
		String newPassword = this._newPasswordInput.getText().toString();
		String newPasswordRepeat = this._newPasswordRepeatInput.getText().toString();
		String confirmationCode = this._confirmationCodeInput.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (InputValidator.isEmpty(confirmationCode)) {
			this._confirmationCodeInput.setError(getString(R.string.error_field_required));
			focusView = this._confirmationCodeInput;
			cancel = true;
		} else if (!InputValidator.isConfirmationCodeValid(confirmationCode)) {
			this._confirmationCodeInput.setError(getString(R.string.error_invalid_confirmation_code));
			focusView = this._confirmationCodeInput;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			Utils.HideKeyboard(getContext(), this._confirmResetPasswordView);
			this.showProgress(true, true);
			this._authTask = new ResetPasswordFragment.PasswordResetTask(
				email, newPassword, newPasswordRepeat, confirmationCode, this, this.getContext()
			);
			this._authTask.execute((Void) null);
		}
	}

	private static class GetConfirmationCodeTask extends AsyncTask<Void, Void, String> {

		private String _email;

		private final ResetPasswordFragment _cls;
		private WeakReference<Context> _baseCtx;

		GetConfirmationCodeTask(String email, ResetPasswordFragment cls, Context baseCtx) {
			this._email = email;
			this._cls = cls;
			this._baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				this._cls._client.RequestCode(this._email);
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
				Toast.makeText(this._baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				showConfirmation = false;
			}
			this._cls._authTask = null;
			this._cls.showProgress(false, showConfirmation);
		}

		@Override
		protected void onCancelled() {
			this._cls._authTask = null;
			this._cls.showProgress(false, false);
		}
	}

	private static class PasswordResetTask extends AsyncTask<Void, Void, String> {

		private String _email;
		private String _newPassword;
		private String _newPasswordRepeat;
		private String _confirmationCode;

		private final ResetPasswordFragment _cls;
		private WeakReference<Context> _baseCtx;

		PasswordResetTask(String email, String newPassword, String newPasswordRepeat, String confirmationCode, ResetPasswordFragment cls, Context baseCtx) {
			this._email = email;
			this._newPassword = newPassword;
			this._newPasswordRepeat = newPasswordRepeat;
			this._confirmationCode = confirmationCode;
			this._cls = cls;
			this._baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			try {
				this._cls._client.ResetPassword(
					this._email, this._newPassword, this._newPasswordRepeat, this._confirmationCode
				);
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
				Toast.makeText(this._baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
				showConfirmation = true;
			} else {
				this._cls.resetInputs();
				Toast.makeText(this._baseCtx.get(), R.string.account_rpf_reset_success, Toast.LENGTH_LONG).show();
			}
			this._cls._authTask = null;
			this._cls.showProgress(false, showConfirmation);
		}

		@Override
		protected void onCancelled() {
			this._cls._authTask = null;
			this._cls.showProgress(false, false);
		}
	}
}
