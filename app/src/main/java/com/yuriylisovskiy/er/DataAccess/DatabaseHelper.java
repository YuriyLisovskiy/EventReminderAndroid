package com.yuriylisovskiy.er.DataAccess;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IEventRepository;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;

import java.util.List;

public class DatabaseHelper {

	private static AppDatabase database;

	public static void Initialize(Context ctx, String name) {
		if (database == null) {
			database = Room.databaseBuilder(ctx, AppDatabase.class, name).build();
		}
	}

	public static boolean isInitialized() {
		return DatabaseHelper.database != null;
	}

	public static AppDatabase GetInstance() {
		assert database != null;
		return database;
	}

	public static void Restore(List<EventModel> events) {
		assert database != null;
		database.clearAllTables();
		IEventRepository eventRepository = database.EventRepository();
		for (int i = 0; i < events.size(); i++) {
			eventRepository.insert(events.get(i));
		}
	}
}
