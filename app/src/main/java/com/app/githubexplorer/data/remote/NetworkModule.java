package com.app.githubexplorer.data.remote;

import android.content.Context;
import androidx.room.Room;

import com.app.githubexplorer.data.local.AppDatabase;
import com.app.githubexplorer.data.local.RepoDao;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class NetworkModule {
    private static Retrofit retrofit; private static AppDatabase db;
    public static Retrofit provideRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(log).connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
            retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/").addConverterFactory(GsonConverterFactory.create()).client(client).build();
        } return retrofit; }
    public static GitHubApi provideApi() { return provideRetrofit().create(GitHubApi.class); }
    public static synchronized AppDatabase provideDb(Context ctx) { if (db == null) db = Room.databaseBuilder(ctx.getApplicationContext(), AppDatabase.class, "gh-cache.db").build(); return db; }
    public static RepoDao provideRepoDao(Context ctx) { return provideDb(ctx).repoDao(); }
}