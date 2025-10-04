package com.app.githubexplorer.ui.main;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.app.githubexplorer.data.model.Repo;
import com.app.githubexplorer.repo.RepoRepository;
import java.util.List;

public class RepoSearchViewModel extends AndroidViewModel {
    private final RepoRepository repo;
    private final MutableLiveData<String> query = new MutableLiveData<>("android");
    public final LiveData<List<Repo>> repos;

    public RepoSearchViewModel(@NonNull Application application) {
        super(application);
        repo = new RepoRepository(application);
        repos = Transformations.switchMap(query, repo::cacheByQuery);
    }

    public LiveData<Boolean> loading() { return repo.loading(); }
    public LiveData<String> error() { return repo.error(); }

    public void search(String q, int page, int perPage) {
        if (q == null || q.trim().isEmpty()) q = "android";
        if (!q.equals(query.getValue())) query.setValue(q);
        repo.searchAndCache(q, page, perPage);
    }
}
