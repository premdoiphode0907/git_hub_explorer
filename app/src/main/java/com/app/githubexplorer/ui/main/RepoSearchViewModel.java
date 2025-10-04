package com.app.githubexplorer.ui.main;

import android.app.Application; import androidx.annotation.NonNull; import androidx.lifecycle.*;

import com.app.githubexplorer.data.model.Repo;
import com.app.githubexplorer.repo.RepoRepository;
import java.util.List;
public class RepoSearchViewModel extends AndroidViewModel {
    private final RepoRepository repo; private LiveData<List<Repo>> repos;
    public RepoSearchViewModel(@NonNull Application application) {
        super(application); repo = new RepoRepository(application);
    }
    public LiveData<List<Repo>> getRepos(String query) {
        if (repos == null)
            repos = repo.cacheByQuery(query);
        return repos;
    }
    public LiveData<Boolean> loading() {
        return repo.loading();
    }
    public LiveData<String> error() {
        return repo.error();
    }
    public void search(String query, int page, int perPage) {
        repo.searchAndCache(query, page, perPage);
    }
}
