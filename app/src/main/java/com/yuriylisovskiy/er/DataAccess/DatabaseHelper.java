package com.yuriylisovskiy.er.DataAccess;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseHelper {

	private static AppDatabase database;

	public static void Initialize(Context ctx, String name) {
		if (database == null) {
			database = Room.databaseBuilder(ctx, AppDatabase.class, name).build();
		}
	}

	public static AppDatabase GetInstance() {
		assert database != null;
		return database;
	}

}
