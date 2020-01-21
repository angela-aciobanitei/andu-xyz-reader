package com.ang.acb.materialme.data.remote;

import java.io.IOException;

import androidx.annotation.Nullable;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
 *
 * See: https://github.com/android/android-architecture-components/tree/master/GithubBrowserSample
 */
public class ApiResponse<T> {

    // HTTP status code
    public final int code;

    // Data
    @Nullable
    public final T body;

    // Error
    @Nullable
    public final Throwable error;


    public ApiResponse(@Nullable Throwable throwable) {
        code = 500;
        body = null;
        error = throwable;
    }

    public ApiResponse(Response<T> response) {
        code = response.code();
        if (response.isSuccessful()) {
            body = response.body();
            error = null;
        } else {
            String message = null;
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                } catch (IOException ignored) {
                    Timber.e(ignored, "Error while parsing response");
                }
            }
            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }
            error = new IOException(message);
            body = null;
        }
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public T getBody() {
        return body;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }
}

