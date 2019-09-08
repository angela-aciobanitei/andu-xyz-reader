package com.ang.acb.materialme.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events
 * like navigation and Snackbar messages. This avoids a common problem with events: on configuration
 * change (like rotation) an update can be emitted if the observer is active. This LiveData only
 * calls the observable if there's an explicit call to setValue() or call().
 * Note that only one observer is going to be notified of changes.
 *
 * See: https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 * See: https://github.com/googlesamples/android-architecture/tree/dev-todo-mvvm-live/todoapp
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @MainThread
    public void observe(@NotNull LifecycleOwner owner,
                        @NotNull final Observer<? super T> observer) {

        if (hasActiveObservers()) {
            Timber.w("Multiple observers registered but only one will be notified of changes.");
        }

        // Observe the internal MutableLiveData
        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }

    @MainThread
    public void setValue(@Nullable T t) {
        mPending.set(true);
        super.setValue(t);
    }

    // Used for cases where T is Void, to make calls cleaner.
    @MainThread
    public void call() {
        setValue(null);
    }
}
