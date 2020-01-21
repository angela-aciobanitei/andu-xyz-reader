package com.ang.acb.materialme.ui.grid;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.data.model.Article;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticleViewHolder>{

    private List<Article> articles;
    private ArticleItemListener listener;

    public ArticlesAdapter(ArticleItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ArticleViewHolder.createViewHolder(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bindTo(articles.get(position));
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 :  articles.size();
    }

    public void submitList(List<Article> articles) {
        this.articles = articles;
        // Notify any registered observers
        // that the data set has changed.
        notifyDataSetChanged();
    }
}
