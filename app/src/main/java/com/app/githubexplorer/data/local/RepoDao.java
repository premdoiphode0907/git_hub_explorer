package com.app.githubexplorer.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.app.githubexplorer.data.model.Repo;
import java.util.List;
@Dao
public interface RepoDao {
    @Query("SELECT * FROM repos WHERE search_query = :q ORDER BY stars DESC")
    LiveData<List<Repo>> getByQuery(String q);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Repo> repos);
    @Query("DELETE FROM repos WHERE search_query = :q")
    void clearByQuery(String q);
}
