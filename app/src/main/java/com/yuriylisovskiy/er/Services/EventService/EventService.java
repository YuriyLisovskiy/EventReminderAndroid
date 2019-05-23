package com.yuriylisovskiy.er.Services.EventService;

import com.yuriylisovskiy.er.DataAccess.DatabaseHelper;
import com.yuriylisovskiy.er.DataAccess.Interfaces.IEventRepository;
import com.yuriylisovskiy.er.DataAccess.Models.EventModel;
import com.yuriylisovskiy.er.Util.DateTimeHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventService implements IEventService {

	private IEventRepository _eventRepository;

	public EventService() {
		this._eventRepository = DatabaseHelper.GetInstance().EventRepository();
	}

	@Override
	public List<EventModel> GetAll() {
		return this._eventRepository.getAll();
	}

	@Override
	public List<EventModel> GetByDate(Date date) {
		return this._eventRepository.getByDate(DateTimeHelper.formatDate(date));
	}

	@Override
	public List<EventModel> GetAllFromNow() {
		Date now = Calendar.getInstance().getTime();
		return this._eventRepository.getAllFrom(
			DateTimeHelper.formatDate(now),
			DateTimeHelper.formatTime(now.getTime())
		);
	}

	@Override
	public List<EventModel> GetRange(Date dateFrom, int delta) {
		Calendar dateToCalendar = Calendar.getInstance();
		dateToCalendar.setTime(dateFrom);
		dateToCalendar.add(Calendar.MINUTE, delta);
		return this._eventRepository.getRange(
			DateTimeHelper.formatDate(dateFrom),
			DateTimeHelper.formatDate(dateToCalendar.getTime())
		);
	}

	@Override
	public EventModel GetById(long id) {
		return this._eventRepository.getById(id);
	}

	@Override
	public void CreateItem(EventModel model) {
		if (!this.Exists(model.Id)) {
			this._eventRepository.insert(model);
		}
	}

	@Override
	public void UpdateItem(EventModel model) {
		if (this.Exists(model.Id)) {
			this._eventRepository.update(model);
		}
	}

	@Override
	public void DeleteById(long id) {
		EventModel model = this._eventRepository.getById(id);
		if (model != null) {
			this._eventRepository.delete(model);
		}
	}

	@Override
	public boolean Exists(long id) {
		return this._eventRepository.getById(id) != null;
	}
}
