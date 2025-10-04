package com.app.githubexplorer.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.githubexplorer.R;
import com.app.githubexplorer.ui.detail.RepoDetailActivity;

public class HomeActivity extends AppCompatActivity {
    private RepoSearchViewModel vm;
    private RepoListAdapter adapter;
    private int currentPage = 1;
    private final android.os.Handler debounce = new android.os.Handler();
    private Runnable pendingSearch;
    private String currentQuery = "android";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        // Observe once. Repo list will update when vm.search(...) updates the query.
        vm.repos.observe(this, repos -> adapter.submit(repos));
        vm.loading().observe(this, isLoading ->
                progress.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE));
        vm.error().observe(this, e -> {
            error.setVisibility(e != null ? View.VISIBLE : View.GONE);
            if (e != null) ((android.widget.TextView) error).setText(e);
        });

        // Infinite scroll (simple)
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView rv, int dx, int dy) {
                if (dy <= 0) return;
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                int visible = lm.getChildCount();
                int total = lm.getItemCount();
                int first = lm.findFirstVisibleItemPosition();
                if (visible + first >= total - 4) vm.search(currentQuery, ++currentPage, 30);
            }
        });

        // Search
        sv.setQueryHint("Search repositories");

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; } // not needed

            @Override public boolean onQueryTextChange(String s) {
                if (pendingSearch != null) debounce.removeCallbacks(pendingSearch);

                pendingSearch = () -> {
                    String q = (s == null) ? "" : s.trim();
                    if (q.length() < 1) return;           // start after first character
                    if (q.equals(currentQuery)) return;    // skip identical query
                    currentQuery = q;
                    currentPage = 1;
                    vm.search(currentQuery, 1, 30);        // switches LiveData via ViewModel
                };

                debounce.postDelayed(pendingSearch, 400);  // 300–500 ms is typical
                return true;
            }
        });

        // Initial load
        vm.search(currentQuery, 1, 30);
    }
}
