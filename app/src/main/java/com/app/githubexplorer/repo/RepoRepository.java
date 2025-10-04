package com.app.githubexplorer.repo;

import android.app.Application;
import androidx.lifecycle.*;

import com.app.githubexplorer.data.local.RepoDao;
import com.app.githubexplorer.data.model.Repo;
import com.app.githubexplorer.data.model.SearchResponse;
import com.app.githubexplorer.data.remote.GitHubApi;
import com.app.githubexplorer.data.remote.NetworkModule;
import com.app.githubexplorer.util.ExecutorsProvider;

import java.util.*;
import retrofit2.*;
public class RepoRepository {
    private final GitHubApi api;
    private final RepoDao dao;
    private final ExecutorsProvider exec = new ExecutorsProvider();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);
    public RepoRepository(Application app) { this.api = NetworkModule.provideApi(); this.dao = NetworkModule.provideRepoDao(app); }
    public LiveData<List<Repo>> cacheByQuery(String q) {
        return dao.getByQuery(q);
    }
    public LiveData<Boolean> loading() {
        return loading;
    } public LiveData<String> error() {
        return error;
    }
    public void searchAndCache(final String query, final int page, final int perPage) {
        loading.postValue(true); error.postValue(null);
        api.searchRepos(query, page, perPage).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> resp) {
                loading.postValue(false);
                if (resp.isSuccessful() && resp.body() != null) {

                    final List<Repo> items = resp.body().items != null ? resp.body().items : new ArrayList<>();
                    for (Repo r : items)
                    {
                        r.searchQuery = query; r.page = page;
                        if (r.ownerFlat == null && r.owner != null)
                            r.ownerFlat = r.owner;
                    }
                    exec.io.execute(() -> { if (page == 1) dao.clearByQuery(query); dao.insertAll(items); });
                } else {
                    error.postValue("HTTP " + resp.code());
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                loading.postValue(false); error.postValue(t.getMessage());
            }
        });
    }
}
