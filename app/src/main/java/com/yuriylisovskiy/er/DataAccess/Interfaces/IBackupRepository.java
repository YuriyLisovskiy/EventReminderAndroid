package com.yuriylisovskiy.er.DataAccess.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yuriylisovskiy.er.DataAccess.Models.BackupModel;

import java.util.List;

@Dao
public interface IBackupRepository {

	@Query("SELECT * FROM backups")
	List<BackupModel> getAll();

	@Query("SELECT * FROM backups WHERE backups.Digest = :digest")
	BackupModel getById(String digest);

	@Insert
	void insert(BackupModel model);

	@Delete
	void delete(BackupModel model);
}
