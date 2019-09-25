package com.ang.acb.materialme.ui.grid;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.data.model.Article;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticleViewHolder>{

    private List<Article> articleList;
    private ViewHolderListener listener;

    public ArticlesAdapter(ViewHolderListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ArticleViewHolder.createViewHolder(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        // Bind article data.
        holder.bindTo(articleList.get(position));
    }

    @Override
    public int getItemCount() {
        return articleList == null ? 0 :  articleList.size();
    }

    public void submitList(List<Article> articleList) {
        this.articleList = articleList;
        // Notify any registered observers that the data set has changed.
        notifyDataSetChanged();
    }
}
