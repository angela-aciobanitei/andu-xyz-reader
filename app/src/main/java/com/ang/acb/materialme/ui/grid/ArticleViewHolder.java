package com.ang.acb.materialme.ui.grid;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.databinding.ArticleItemBinding;
import com.ang.acb.materialme.utils.GlideApp;
import com.ang.acb.materialme.utils.Utils;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import timber.log.Timber;

class ArticleViewHolder extends RecyclerView.ViewHolder {

    private ArticleItemBinding binding;
    private ArticleViewHolderListener listener;

    // Required constructor matching super
    private ArticleViewHolder(@NonNull ArticleItemBinding binding, ArticleViewHolderListener listener) {
        super(binding.getRoot());

        this.binding = binding;
        this.listener = listener;
    }

    static ArticleViewHolder createViewHolder(ViewGroup parent, ArticleViewHolderListener listener) {
        // Inflate view and obtain an instance of the binding class.
        ArticleItemBinding binding = ArticleItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ArticleViewHolder(binding, listener);
    }


    void bindTo(Article article) {
        // Bind article data (title, author and thumbnail)
        binding.setArticle(article);
        bindArticleThumbnail(article);

        // TODO Set the string value of the article ID as the unique transition name
        // for the image view that will be used in the shared element transition.
        ViewCompat.setTransitionName(
                binding.articleItemThumbnail,
                String.valueOf(article.getId()));

        // Handle article items click events.
        binding.getRoot().setOnClickListener(view -> {
                listener.onItemClicked(binding.getRoot(), getAdapterPosition());
        });

        // Binding must be executed immediately.
        binding.executePendingBindings();
    }

    private void bindArticleThumbnail(Article article) {
        // Set the aspect ratio for this image.
        binding.articleItemThumbnail.setAspectRatio(article.getAspectRatio());

        // Load the image with Glide to prevent OOM error when the image drawables are very large.
        final int adapterPosition = getAdapterPosition();
        GlideApp.with(binding.getRoot().getContext())
                // Calling Glide.with() returns a RequestBuilder.
                // By default you get a Drawable RequestBuilder, but
                // you can change the requested type using as... methods.
                // For example, asBitmap() returns a Bitmap RequestBuilder.
                .asBitmap()
                .load(article.getThumbUrl())
                // Tell Glide not to use its standard crossfade animation.
                .dontAnimate()
                // Display a placeholder until the image is loaded and processed.
                .placeholder(R.color.photoPlaceholder)
                // Transform bitmap to obtain rounded corners.
                .apply(RequestOptions.bitmapTransform(new RoundedCorners((int)
                        Utils.dipToPixels(binding.getRoot().getContext(),
                        6))))
                // Keep track of errors and successful image loading.
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException exception, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        listener.onLoadCompleted(adapterPosition);
                        Timber.d("Image loading failed: %s", exception.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        generatePaletteAsync(resource);
                        listener.onLoadCompleted(adapterPosition);
                        return false;
                    }
                })
                .into(binding.articleItemThumbnail);
    }

    private void generatePaletteAsync(Bitmap bitmap) {
        // To extract prominent colors from an image, we can use the Platte
        // class. By passing in a PaletteAsyncListener to generate() method,
        // we can generate the palette asynchronously using an AsyncTask
        // to gather the Palette swatch information from the bitmap.
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = Utils.getDominantColor(palette);
                if (swatch != null) {
                    binding.articleItemCardView.setCardBackgroundColor(swatch.getRgb());
                    binding.articleItemCardView.setStrokeColor(swatch.getRgb());
                }
            }
        });
    }
}
