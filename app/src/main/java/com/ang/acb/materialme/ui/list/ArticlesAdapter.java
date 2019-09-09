package com.ang.acb.materialme.ui.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.databinding.ArticleItemBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind article data
        Article article = articleList.get(position);
        ((ArticleViewHolder) holder).bindTo(article);

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

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        private ArticleItemBinding binding;

        // Required constructor matching super
        ArticleViewHolder(@NonNull ArticleItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Article article) {
            // Bind article data.
            binding.setArticle(article);

            // Bind article thumbnail separately, because we need to set the aspect ratio.
            binding.articleItemThumbnail.setAspectRatio(article.getAspectRatio());
            Glide.with(binding.getRoot().getContext())
                    .asBitmap()
                    .load(article.getThumbUrl())
                    .dontAnimate()
                    .placeholder(R.color.photoPlaceholder)
                    .into(binding.articleItemThumbnail);

            // Binding must be executed immediately.
            binding.executePendingBindings();
        }


    }
}
