package com.app.githubexplorer.data.model;

import androidx.room.*;
import com.google.gson.annotations.SerializedName;
@Entity(tableName = "repos")
public class Repo {
    @PrimaryKey @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("full_name")
    public String fullName;
    @SerializedName("stargazers_count")
    public int stars;
    @SerializedName("language")
    public String language;
    @SerializedName("html_url")
    public String htmlUrl;
    @SerializedName("description")
    public String description;
    @Embedded(prefix = "owner_")
    public Owner ownerFlat; // flatten owner for Room
    @Ignore
    public Owner owner; // used by network only
    @ColumnInfo(name = "search_query")
    public String searchQuery; // cache key
    @ColumnInfo(name = "page")
    public int page; // pagination marker
}
