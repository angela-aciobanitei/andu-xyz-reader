package com.ang.acb.materialme.ui.grid;

import android.view.View;

/**
 * A listener that is attached to all ViewHolders to handle image loading events and clicks.
 */
public interface ArticleViewHolderListener {

    void onItemClicked(View view, int adapterPosition);

    void onLoadCompleted(int adapterPosition);
}
