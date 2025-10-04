package com.app.githubexplorer.data.remote;

import android.content.Context;

import androidx.room.Room;

import com.app.githubexplorer.data.local.AppDatabase;
import com.app.githubexplorer.data.local.RepoDao;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkModule {
    private static volatile Retrofit retrofit;
    private static volatile OkHttpClient client;
    private static volatile AppDatabase db;

    private NetworkModule() {}

    private static OkHttpClient provideClient() {
        if (client == null) {
            synchronized (NetworkModule.class) {
                if (client == null) {
                    HttpLoggingInterceptor log = new HttpLoggingInterceptor();
                    log.setLevel(HttpLoggingInterceptor.Level.BASIC);

                    Interceptor headers = chain -> chain.proceed(
                            chain.request().newBuilder()
                                    .header("User-Agent", "GitHubExplorer/1.0 (Android)")
                                    .header("Accept", "application/vnd.github+json")
                                    .build()
                    );

                    client = new OkHttpClient.Builder()
                            .addInterceptor(headers)
                            .addInterceptor(log)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();
                }
            }
        }
        return client;
    }

    public static Retrofit provideRetrofit() {
        if (retrofit == null) {
            synchronized (NetworkModule.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.github.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(provideClient())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static GitHubApi provideApi() {
        return provideRetrofit().create(GitHubApi.class);
    }

    public static synchronized AppDatabase provideDb(Context ctx) {
        if (db == null) {
            db = Room.databaseBuilder(
                    ctx.getApplicationContext(),
                    AppDatabase.class,
                    "gh-cache.db"
            ).build();
        }
        return db;
    }

    public static RepoDao provideRepoDao(Context ctx) {
        return provideDb(ctx).repoDao();
    }
}
