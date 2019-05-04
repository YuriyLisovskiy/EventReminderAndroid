package com.yuriylisovskiy.er.DataAccess;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.yuriylisovskiy.er.DataAccess.Interfaces.IEventRepository;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;

@Database(entities = {EventModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

	public abstract IEventRepository EventRepository();

}
