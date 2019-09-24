package com.ang.acb.materialme.ui.grid;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class ArticleViewHolder extends RecyclerView.ViewHolder {

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
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(
                        (int) Utils.dipToPixels(binding.getRoot().getContext(), 6))))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
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
                        return false;
                    }
                })
                .into(binding.articleItemThumbnail);

        // Binding must be executed immediately.
        binding.executePendingBindings();
    }


}
