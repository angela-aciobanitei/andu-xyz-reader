package com.ang.acb.materialme.ui.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.databinding.ArticleItemBinding;


import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticleViewHolder>{

    private List<Article> articleList;
    private ArticleClickCallback clickCallback;

    public interface ArticleClickCallback {
        void onArticleClicked(int position);
    }

    public ArticlesAdapter(ArticleClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view and obtain an instance of the binding class.
        ArticleItemBinding binding = ArticleItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ArticleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        // Bind article data
        Article article = articleList.get(position);
        holder.bindTo(article);

        // Handle click item events.
        holder.itemView.setOnClickListener(v -> {
            if (article != null && clickCallback != null) {
                clickCallback.onArticleClicked(position);
            }
        });
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
