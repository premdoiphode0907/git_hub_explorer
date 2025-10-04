package com.app.githubexplorer.repo;

import android.app.Application;
import androidx.lifecycle.*;

import com.app.githubexplorer.data.local.RepoDao;
import com.app.githubexplorer.data.model.Repo;
import com.app.githubexplorer.data.model.SearchResponse;
import com.app.githubexplorer.data.remote.GitHubApi;
import com.app.githubexplorer.data.remote.NetworkModule;
import com.app.githubexplorer.util.ExecutorsProvider;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepoRepository {
    private final GitHubApi api;
    private final RepoDao dao;
    private final ExecutorsProvider exec = new ExecutorsProvider();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    // Track the latest request to cancel stale ones
    private volatile Call<SearchResponse> inflight;

    public RepoRepository(Application app) {
        this.api = NetworkModule.provideApi();
        this.dao = NetworkModule.provideRepoDao(app);
    }

    public LiveData<List<Repo>> cacheByQuery(String q) { return dao.getByQuery(q); }
    public LiveData<Boolean> loading() { return loading; }
    public LiveData<String> error() { return error; }
    public void clearError() { error.postValue(null); }

    public synchronized void searchAndCache(final String query, final int page, final int perPage) {
        final String q = (query == null || query.trim().isEmpty()) ? "android" : query.trim();
        final int size = Math.max(1, Math.min(perPage, 100)); // GitHub max per_page=100

        // Cancel previous search when starting a fresh query/page 1
        if (page == 1 && inflight != null) inflight.cancel();

        loading.postValue(true);
        error.postValue(null);

        inflight = api.searchRepos(q, page, size);
        inflight.enqueue(new Callback<SearchResponse>() {
            @Override public void onResponse(Call<SearchResponse> call, Response<SearchResponse> resp) {
                loading.postValue(false);
                if (!resp.isSuccessful()) {
                    error.postValue(humanReadableError(resp));
                    return;
                }
                SearchResponse body = resp.body();
                final List<Repo> items = (body != null && body.items != null) ? body.items : new ArrayList<>();

                // tag rows for the cache key and pagination
                for (Repo r : items) {
                    r.searchQuery = q;
                    r.page = page;
                    if (r.ownerFlat == null && r.owner != null) r.ownerFlat = r.owner;
                }

                exec.io.execute(() -> {
                    if (page == 1) dao.clearByQuery(q);
                    if (!items.isEmpty()) dao.insertAll(items);
                });
            }

            @Override public void onFailure(Call<SearchResponse> call, Throwable t) {
                loading.postValue(false);
                if (call.isCanceled()) return; // ignore cancellations
                if (t instanceof UnknownHostException) {
                    error.postValue("No internet connection");
                } else {
                    error.postValue(t.getMessage() != null ? t.getMessage() : "Request failed");
                }
            }
        });
    }

    private String humanReadableError(Response<SearchResponse> resp) {
        int code = resp.code();
        // GitHub rate limit often 403 with headers
        if (code == 403) {
            String remaining = resp.headers().get("X-RateLimit-Remaining");
            if ("0".equals(remaining)) return "GitHub rate limit exceeded. Try again later.";
            return "Forbidden (403).";
        }
        if (code == 422) return "Validation failed (422). Check your search query.";
        // Try read message from body
        ResponseBody err = resp.errorBody();
        if (err != null) {
            try { String msg = err.string();
                if (!msg.isEmpty()) return "HTTP " + code + ": " + msg;
            } catch (IOException ignored) {}
        }
        return "HTTP " + code;
    }
}
