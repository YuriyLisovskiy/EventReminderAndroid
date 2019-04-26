package com.yuriylisovskiy.er.Services.ClientService;

class Routes {

	private final static String HOST = "192.168.1.102:8000";
	private final static String BASE = "http://" + HOST + "/api/v1";

	final static String AUTH_LOGIN = BASE + "/login";
	final static String AUTH_LOGOUT = BASE + "/logout";

	private final static String ACCOUNTS = BASE + "/accounts";

	final static String ACCOUNT_EDIT = ACCOUNTS + "/edit";
	final static String ACCOUNT_DETAILS = ACCOUNTS + "/user";
	final static String ACCOUNT_CREATE = ACCOUNTS + "/create";
	final static String ACCOUNT_DELETE = ACCOUNTS + "/delete";
	final static String ACCOUNT_SEND_CODE = ACCOUNTS + "/send/verification/code";
	final static String ACCOUNT_PASSWORD_RESET = ACCOUNTS + "/password/reset";

	final static String BACKUPS = BASE + "/backups/";

	final static String BACKUP_CREATE = BACKUPS + "create";
	final static String BACKUP_DELETE = BACKUPS + "delete/";
	final static String BACKUP_DETAILS = BACKUPS + "details/";

}
