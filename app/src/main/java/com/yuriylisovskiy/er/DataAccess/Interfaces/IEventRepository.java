package com.yuriylisovskiy.er.DataAccess.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yuriylisovskiy.er.DataAccess.Models.EventModel;

import java.util.List;

@Dao
public interface IEventRepository {

	@Query("SELECT * FROM events")
	List<EventModel> getAll();

	@Query("SELECT * FROM events WHERE events.Date >= :date AND events.Time >= :time")
	List<EventModel> getAllFrom(String date, String time);

	@Query("SELECT * FROM events WHERE events.Date = :date")
	List<EventModel> getByDate(String date);

	@Query("SELECT * FROM events WHERE events.Date BETWEEN :dateFrom AND :dateTo")
	List<EventModel> getRange(String dateFrom, String dateTo);

	@Query("SELECT * FROM events WHERE events.Id = :id")
	EventModel getById(long id);

	@Insert
	void insert(EventModel model);

	@Update
	void update(EventModel model);

	@Delete
	void delete(EventModel model);
}
