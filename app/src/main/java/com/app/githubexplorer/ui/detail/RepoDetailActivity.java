package com.app.githubexplorer.ui.detail;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.app.githubexplorer.R;
import com.app.githubexplorer.data.model.Repo;
import com.bumptech.glide.Glide;

public class RepoDetailActivity extends AppCompatActivity {
    private static final String EXTRA_NAME = "extra_repo_name";
    private static final String EXTRA_DESC = "extra_repo_desc";
    private static final String EXTRA_LANG = "extra_repo_lang";
    private static final String EXTRA_STARS = "extra_repo_stars";
    private static final String EXTRA_URL = "extra_repo_url";
    private static final String EXTRA_AVATAR = "extra_repo_avatar";
    public static void open(Context ctx, Repo r) {
        Intent i = new Intent(ctx, RepoDetailActivity.class);
        i.putExtra(EXTRA_NAME, r.fullName != null ? r.fullName : r.name);
        i.putExtra(EXTRA_DESC, r.description); i.putExtra(EXTRA_LANG, r.language);
        i.putExtra(EXTRA_STARS, r.stars); i.putExtra(EXTRA_URL, r.htmlUrl);
        String avatar = r.ownerFlat != null ? r.ownerFlat.avatarUrl : null;
        i.putExtra(EXTRA_AVATAR, avatar); ctx.startActivity(i); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_repo_detail);
        ImageView avatar = findViewById(R.id.avatar);
        TextView title = findViewById(R.id.title);
        TextView desc = findViewById(R.id.description);
        TextView meta = findViewById(R.id.meta);
        Button open = findViewById(R.id.open);
        Glide.with(this).load(getIntent().getStringExtra(EXTRA_AVATAR)).placeholder(R.drawable.ic_avatar).into(avatar);
        title.setText(getIntent().getStringExtra(EXTRA_NAME));
        desc.setText(getIntent().getStringExtra(EXTRA_DESC));
        meta.setText(getIntent().getStringExtra(EXTRA_LANG) + " • ★ " + getIntent().getIntExtra(EXTRA_STARS, 0));
        open.setOnClickListener(v -> { String url = getIntent().getStringExtra(EXTRA_URL);
            if (url != null)
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });
    }
}
