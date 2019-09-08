package com.ang.acb.materialme.ui.common;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.ang.acb.materialme.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Binding adapters are responsible for making the appropriate framework calls to set values.
 *
 * See: https://developer.android.com/topic/libraries/data-binding/binding-adapters
 * See: https://github.com/googlesamples/android-sunflower/blob/master/app/src/main/java/com/google/samples/apps/sunflower/adapters
 */
public class BindingAdapters {

    @BindingAdapter({"imageUrl"})
    public static void bindImage(ImageView imageView, String imageUrl) {
                Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(R.color.colorImagePlaceholder)
                .into(imageView);
    }

    @BindingAdapter("toggleVisibility")
    public static void toggleVisibility(View view, Boolean isVisible) {
        if (isVisible) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);
    }
}
