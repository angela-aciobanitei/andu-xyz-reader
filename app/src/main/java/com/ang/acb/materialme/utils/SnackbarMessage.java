package com.ang.acb.materialme.utils;

import androidx.annotation.StringRes;
import androidx.lifecycle.LifecycleOwner;

/**
 * A SingleLiveEvent used for Snackbar messages. Like a {@link SingleLiveEvent} but also prevents
 * null messages and uses a custom observer. Note that only one observer is going to be notified
 * of changes.
 *
 * See: https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 * See: https://github.com/googlesamples/android-architecture/tree/dev-todo-mvvm-live/todoapp
 */
public class SnackbarMessage extends SingleLiveEvent<Integer> {

    public interface SnackbarObserver {
        /**
         * Called when there is a new message to be shown.
         * @param snackbarMessageResourceId The new message, non-null.
         */
        void onNewMessage(@StringRes int snackbarMessageResourceId);
    }

    public void observe(LifecycleOwner owner, final SnackbarObserver observer) {
        super.observe(owner, t -> {
            if (t == null) return;
            observer.onNewMessage(t);
        });
    }
}
