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
import android.widget.Toast;

import com.yuriylisovskiy.er.Fragments.Interfaces.IClientFragment;
import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.Services.ClientService.Exceptions.RequestError;
import com.yuriylisovskiy.er.Services.ClientService.IClientService;
import com.yuriylisovskiy.er.Util.InputValidator;
import com.yuriylisovskiy.er.Util.Utils;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class RegisterFragment extends Fragment implements IClientFragment {

	private View _progressView;
	private AutoCompleteTextView _emailInput;
	private EditText _userNameInput;
	private View _registerFormView;

	private Context _baseContext;
	private IClientService _client;

	private AsyncTask<Void, Void, String> _authTask;

	public RegisterFragment() {}

	@Override
	public void setClientService(IClientService clientService, Context baseCtx) {
		this._client = clientService;
		this._baseContext = baseCtx;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = this.getView();
		if (view != null) {
			Button registerButton = view.findViewById(R.id.account_suf_sign_up_button);
			registerButton.setOnClickListener(pr -> this.processRegister());
			this._progressView = view.findViewById(R.id.account_suf_progress);
			this._userNameInput = view.findViewById(R.id.account_suf_username);
			this._userNameInput.requestFocus();
			this._emailInput = view.findViewById(R.id.account_suf_email);
			this._emailInput.setOnEditorActionListener((v, actionId, event) -> {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					this.processRegister();
					handled = true;
				}
				return handled;
			});
			this._registerFormView = view.findViewById(R.id.account_suf_form);
		} else {
			Toast.makeText(this.getContext(), "Error: register fragment's view is null", Toast.LENGTH_SHORT).show();
		}
	}

	private void processRegister() {
		if (this._authTask != null) {
			return;
		}

		this._userNameInput.setError(null);
		this._emailInput.setError(null);

		String username = this._userNameInput.getText().toString();
		String email = this._emailInput.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email.
		if (InputValidator.isEmpty(email)) {
			this._emailInput.setError(this.getString(R.string.error_field_required));
			focusView = this._emailInput;
			cancel = true;
		} else if (!InputValidator.isEmailValid(email)) {
			this._emailInput.setError(this.getString(R.string.error_invalid_email));
			focusView = this._emailInput;
			cancel = true;
		}

		// Check for a valid username.
		if (InputValidator.isEmpty(username)) {
			this._userNameInput.setError(this.getString(R.string.error_field_required));
			focusView = this._userNameInput;
			cancel = true;
		} else if (!InputValidator.isUserNameValid(username)) {
			this._userNameInput.setError(this.getString(R.string.error_invalid_username));
			focusView = this._userNameInput;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			Utils.HideKeyboard(this.getContext(), this.getView());
			showProgress(true);
			this._authTask = new RegisterFragment.RegisterTask(email, username, this._baseContext, this);
			this._authTask.execute((Void) null);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sign_up, container, false);
	}

	private void showProgress(final boolean show) {
		int shortAnimTime = this.getResources().getInteger(android.R.integer.config_shortAnimTime);
		this._registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		this._registerFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
				new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						_registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
					}
				}
		);
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

	private static class RegisterTask extends AsyncTask<Void, Void, String> {

		private final String _email;
		private final String _username;
		private RegisterFragment _cls;
		private WeakReference<Context> _baseCtx;

		RegisterTask(String email, String username, Context baseCtx, RegisterFragment cls) {
			this._email = email;
			this._username = username;
			this._cls = cls;
			this._baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				this._cls._client.RegisterAccount(this._username, this._email);
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			} catch (RequestError e) {
				e.printStackTrace();
				return e.getErr();
			}
			return null;
		}

		@Override
		protected void onPostExecute(final String resultMsg) {
			this._cls._authTask = null;
			this._cls.showProgress(false);
			if (resultMsg == null) {
				Toast.makeText(this._baseCtx.get(), R.string.register_success, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this._baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			this._cls._authTask = null;
			this._cls.showProgress(false);
		}
	}
}
