package com.app.githubexplorer.ui.main;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.githubexplorer.R;
import com.app.githubexplorer.data.model.Repo;
import com.bumptech.glide.Glide;
import java.util.*;
public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.VH> {
    public interface OnClick {
        void onRepo(Repo r);
    }
    private final Context ctx;
    private final OnClick onClick;
    private final List<Repo> data = new ArrayList<>();
    public RepoListAdapter(Context ctx, OnClick onClick) {
        this.ctx = ctx; this.onClick = onClick;
    }
    public void submit(List<Repo> items) {
        data.clear();
        if (items != null)
            data.addAll(items);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repo, parent, false); return new VH(v); }
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Repo r = data.get(pos);
        h.name.setText(r.fullName != null ? r.fullName : r.name);
        String lang = r.language != null ? r.language : "Unknown"; h.subtitle.setText(lang + " • ★ " + r.stars);
        String avatar = r.ownerFlat != null ? r.ownerFlat.avatarUrl : null;
        Glide.with(ctx).load(avatar).placeholder(R.drawable.ic_avatar).into(h.avatar);
        h.itemView.setOnClickListener(v -> onClick.onRepo(r));
    }
    @Override public int getItemCount() {
        return data.size();
    }
    static class VH extends RecyclerView.ViewHolder {
        ImageView avatar; TextView name; TextView subtitle;
        VH(@NonNull View v) {
            super(v); avatar = v.findViewById(R.id.avatar);
            name = v.findViewById(R.id.name);
            subtitle = v.findViewById(R.id.subtitle);
        }
    }
}