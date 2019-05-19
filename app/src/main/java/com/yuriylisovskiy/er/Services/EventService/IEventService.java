package com.yuriylisovskiy.er.Services.EventService;

import com.yuriylisovskiy.er.DataAccess.Models.EventModel;

import java.util.Date;
import java.util.List;

public interface IEventService {

	List<EventModel> GetAll();

	List<EventModel> GetByDate(Date date);

	List<EventModel> GetRange(Date dateFrom, int delta);

	EventModel GetById(long id);

	void CreateItem(EventModel model);

	void UpdateItem(EventModel model);

	void DeleteById(long id);

	boolean Exists(long id);
}
