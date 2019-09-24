package com.ang.acb.materialme.ui.grid;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.databinding.ArticleItemBinding;
import com.ang.acb.materialme.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.card.MaterialCardView;

class ArticleViewHolder extends RecyclerView.ViewHolder  {

    private ArticleItemBinding binding;
    private ViewHolderListener listener;

    // Required constructor matching super
    private ArticleViewHolder(@NonNull ArticleItemBinding binding, ViewHolderListener listener) {
        super(binding.getRoot());

        this.binding = binding;
        this.listener = listener;
    }

    public static ArticleViewHolder create(ViewGroup parent, ViewHolderListener listener) {
        // Inflate view and obtain an instance of the binding class.
        ArticleItemBinding binding = ArticleItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ArticleViewHolder(binding, listener);
    }


    void bindTo(Article article) {
        // Bind article data.
        binding.setArticle(article);
        bindArticleThumbnail(article);

        // TODO Set the string value of the article ID as the unique transition name for the view.
        ViewCompat.setTransitionName(binding.articleItemThumbnail, String.valueOf(article.getId()));

        // Handle article items click events.
        binding.getRoot().setOnClickListener(view ->
                listener.onItemClicked(
                        binding.articleItemThumbnail,    // shared element transition view
                        String.valueOf(article.getId()), // shared element transition name
                        getAdapterPosition()));

        // Binding must be executed immediately.
        binding.executePendingBindings();
    }

    private void bindArticleThumbnail(Article article) {
        binding.articleItemThumbnail.setAspectRatio(article.getAspectRatio());
        Glide.with(binding.getRoot().getContext())
                .asBitmap()
                .load(article.getThumbUrl())
                .dontAnimate()
                .placeholder(R.color.photoPlaceholder)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners((int)
                        Utils.dipToPixels(binding.getRoot().getContext(), 6))))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        listener.onLoadCompleted(binding.articleItemThumbnail, getAdapterPosition());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        generatePalette(resource);
                        listener.onLoadCompleted(binding.articleItemThumbnail, getAdapterPosition());
                        return false;
                    }
                })
                .into(binding.articleItemThumbnail);
    }

    private void generatePalette(Bitmap resource) {
        // Generate palette synchronously
        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                Palette.Swatch swatch = Utils.getDominantColor(p);
                if (swatch != null) {
                    MaterialCardView cardView = (MaterialCardView) binding.getRoot();
                    cardView.setCardBackgroundColor(swatch.getRgb());
                    cardView.setStrokeColor(swatch.getRgb());
                }
            }
        });
    }
}
