package com.ang.acb.materialme.ui.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.databinding.ArticleItemBinding;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    private ArticleItemBinding binding;
    private ArticleClickCallback clickCallback;

    public interface ArticleClickCallback {
        void onArticleClicked();
    }

    // Required constructor matching super
    public ArticleViewHolder(@NonNull ArticleItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public static ArticleViewHolder create (ViewGroup parent) {
        // Inflate view and obtain an instance of the binding class.
        ArticleItemBinding binding = ArticleItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ArticleViewHolder(binding);
    }

    public void bindTo (Article article) {
        // Bind article data.
        binding.setArticle(article);
        // Handle item click events.
        binding.getRoot().setOnClickListener(view -> {

        });

        // Binding must be executed immediately.
        binding.executePendingBindings();
    }


}
