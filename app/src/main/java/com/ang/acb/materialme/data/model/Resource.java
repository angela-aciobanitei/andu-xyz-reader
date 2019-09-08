package com.ang.acb.materialme.data.model;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic class that contains data and status about loading this data.
 *
 * See: https://developer.android.com/jetpack/docs/guide#addendum.
 * See: https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample
 */
public class Resource<T> {

    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;

    public Resource(@NonNull Status status, @Nullable T data,
                    @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    // Creates a Resource object with a SUCCESS status and some data.
    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    // Creates a Resource object with an ERROR status and an error message.
    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    // Creates a Resource object with a LOADING status to notify the UI.
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource<?> resource = (Resource<?>) o;
        return status == resource.status &&
                Objects.equals(data, resource.data) &&
                Objects.equals(message, resource.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data, message);
    }

    /**
     * Status of a resource that is provided to the UI. These are usually created by the
     * the Repository classes where they return LiveData<Resource<T>> to pass back latest
     * data to the UI with its fetch status.
     */
    public enum Status {
        SUCCESS, ERROR, LOADING
    }
}

