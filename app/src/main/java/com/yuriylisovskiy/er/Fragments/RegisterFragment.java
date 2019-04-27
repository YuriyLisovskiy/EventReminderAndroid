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

	private View progressView;
	private AutoCompleteTextView emailView;
	private EditText usernameView;
	private View registerFormView;

	private Context baseContext;
	private IClientService client;

	private AsyncTask<Void, Void, String> authTask;

	public RegisterFragment() {}

	@Override
	public void setClientService(IClientService clientService, Context baseCtx) {
		this.client = clientService;
		this.baseContext = baseCtx;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (view != null) {
			Button registerButton = view.findViewById(R.id.account_suf_sign_up_button);
			registerButton.setOnClickListener(pr -> ProcessRegister());
			this.progressView = view.findViewById(R.id.account_suf_progress);
			this.usernameView = view.findViewById(R.id.account_suf_username);
			this.usernameView.requestFocus();
			this.emailView = view.findViewById(R.id.account_suf_email);
			this.emailView.setOnEditorActionListener((v, actionId, event) -> {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					ProcessRegister();
					handled = true;
				}
				return handled;
			});
			this.registerFormView = view.findViewById(R.id.account_suf_form);
		} else {
			Toast.makeText(getContext(), "Error: register fragment's view is null", Toast.LENGTH_SHORT).show();
		}
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
		if (InputValidator.isEmpty(email)) {
			this.emailView.setError(getString(R.string.error_field_required));
			focusView = this.emailView;
			cancel = true;
		} else if (!InputValidator.isEmailValid(email)) {
			this.emailView.setError(getString(R.string.error_invalid_email));
			focusView = this.emailView;
			cancel = true;
		}

		// Check for a valid username.
		if (InputValidator.isEmpty(username)) {
			this.usernameView.setError(getString(R.string.error_field_required));
			focusView = this.usernameView;
			cancel = true;
		} else if (!InputValidator.isUserNameValid(username)) {
			this.usernameView.setError(getString(R.string.error_invalid_username));
			focusView = this.usernameView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			Utils.HideKeyboard(getContext(), getView());
			showProgress(true);
			this.authTask = new RegisterTask(email, username, this.baseContext, this);
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

	public static class RegisterTask extends AsyncTask<Void, Void, String> {

		private final String email;
		private final String username;
		private RegisterFragment cls;
		private WeakReference<Context> baseCtx;

		RegisterTask(String email, String username, Context baseCtx, RegisterFragment cls) {
			this.email = email;
			this.username = username;
			this.cls = cls;
			this.baseCtx = new WeakReference<>(baseCtx);
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				this.cls.client.RegisterAccount(this.username, this.email);
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
			this.cls.authTask = null;
			this.cls.showProgress(false);
			if (resultMsg == null) {
				Toast.makeText(this.baseCtx.get(), R.string.register_success, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this.baseCtx.get(), resultMsg, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			this.cls.authTask = null;
			this.cls.showProgress(false);
		}
	}
}
