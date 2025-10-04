package com.app.githubexplorer.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.githubexplorer.data.model.Repo;


@Database(entities = {Repo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RepoDao repoDao();

}