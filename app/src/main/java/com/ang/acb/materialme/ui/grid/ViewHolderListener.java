package com.ang.acb.materialme.ui.grid;

import android.view.View;
import android.widget.ImageView;

/**
 * A listener that is attached to all ViewHolders to handle image loading events and clicks.
 */

public interface ViewHolderListener {

    void onItemClicked(View sharedView, String sharedElementName, int adapterPosition);

    void onLoadCompleted(ImageView view, int adapterPosition);
}
