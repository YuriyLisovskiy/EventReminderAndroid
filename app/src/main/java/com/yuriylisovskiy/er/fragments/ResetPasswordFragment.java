package com.yuriylisovskiy.er.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuriylisovskiy.er.R;
import com.yuriylisovskiy.er.client.Client;

public class ResetPasswordFragment extends Fragment {

	private Client client;

	public ResetPasswordFragment() {
		this.client = Client.getInstance();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_reset_password, container, false);
	}
}
