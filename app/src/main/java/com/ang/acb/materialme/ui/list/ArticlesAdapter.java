package com.ang.acb.materialme.ui.list;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.data.model.Article;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticleViewHolder>{

    private List<Article> articleList;

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ArticleViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bindTo(articleList.get(position));
    }

    @Override
    public int getItemCount() {
        return articleList == null ? 0 :  articleList.size();
    }

    public void submitList(List<Article> articleList) {
        this.articleList = articleList;
        notifyDataSetChanged();
    }
}
