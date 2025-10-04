package com.app.githubexplorer.data.remote;

import com.app.githubexplorer.data.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface GitHubApi {
    @GET("search/repositories")
    Call<SearchResponse> searchRepos(@Query("q") String query, @Query("page") int page, @Query("per_page") int perPage);
}