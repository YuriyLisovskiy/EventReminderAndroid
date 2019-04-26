package com.yuriylisovskiy.er.Fragments.Interfaces;

import android.content.Context;

import com.yuriylisovskiy.er.Services.ClientService.IClientService;

public interface IClientFragment {

	void setClientService(IClientService clientService, Context baseCtx);
}
