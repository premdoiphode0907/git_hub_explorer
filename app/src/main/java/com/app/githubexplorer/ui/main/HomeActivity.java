package com.app.githubexplorer.ui.main;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.app.githubexplorer.R;
import com.app.githubexplorer.data.model.Repo;
import com.app.githubexplorer.ui.detail.RepoDetailActivity;

import java.util.List;
public class HomeActivity extends AppCompatActivity {
    private RepoSearchViewModel vm; private RepoListAdapter adapter; private int currentPage = 1; private String currentQuery = "android";
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        vm = new ViewModelProvider(this).get(RepoSearchViewModel.class);
        RecyclerView list = findViewById(R.id.recycler);
        View progress = findViewById(R.id.progress);
        View error = findViewById(R.id.errorText);
        SearchView sv = findViewById(R.id.searchView);

        adapter = new RepoListAdapter(this, repo -> RepoDetailActivity.open(this, repo));
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        observeQuery(currentQuery);
        vm.loading().observe(this, isLoading -> progress.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE));
        vm.error().observe(this, e -> { error.setVisibility(e != null ? View.VISIBLE : View.GONE);
            if (e != null) ((android.widget.TextView) error).setText(e);
        });
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy)
            {
                if (dy <= 0)
                    return; LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                    int visible = lm.getChildCount();
                    int total = lm.getItemCount();
                    int first = lm.findFirstVisibleItemPosition();
                    if (visible + first >= total - 4) vm.search(currentQuery, ++currentPage, 30);
            }});
        sv.setQueryHint("Search repositories");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String q) {
                currentQuery = q.trim().isEmpty() ? "android" : q.trim(); currentPage = 1;
                vm.search(currentQuery, 1, 30); observeQuery(currentQuery);
                sv.clearFocus(); return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }});
        vm.search(currentQuery, 1, 30);
    }
    private void observeQuery(String q) {
        vm.getRepos(q).observe(
                this, (List<Repo> repos) -> adapter.submit(repos));
    }
}