package com.ang.acb.materialme.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A custom ItemDecoration to provide equal column spacing
 * for a RecyclerView that lays out items in a grid.
 *
 * See: https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int itemOffset;

    private GridSpacingItemDecoration(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    public GridSpacingItemDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(itemOffset, itemOffset, itemOffset, itemOffset);
    }
}