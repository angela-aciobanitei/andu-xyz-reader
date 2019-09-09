package com.ang.acb.materialme.data.repository;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.data.remote.ApiResponse;
import com.ang.acb.materialme.utils.AppExecutors;

/**
 * A generic class that can provide a resource backed by both the SQLite database and
 * the network. It defines two type parameters, RequestType and ResultType, because the
 * data type returned from the API might not match the data type used locally.
 *
 * See: https://developer.android.com/jetpack/docs/guide#addendum.
 * See: https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample
 *
 * @param <ResultType>  Type for the Resource data.
 * @param <RequestType> Type for the API response.
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    // The final result LiveData. Note: why use MediatorLiveData? Consider the following
    // scenario: we have 2 instances of LiveData, let's name them liveData1 and liveData2,
    // and we want to merge their emissions in one object: liveDataMerger. Then, liveData1
    // and liveData2 will become sources for the MediatorLiveData liveDataMerger and every
    // time onChanged is called for either of them, we set a new value in liveDataMerger.
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private AppExecutors appExecutors;

    @MainThread
    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;

        // Send loading state to the UI.
        result.setValue(Resource.loading(null));

        // Get the cached data from the database.
        final LiveData<ResultType> dbSource = loadFromDb();

        // Call addSource(LiveData<S> source, Observer<S> onChanged)
        // to start listening the given source LiveData. The onChanged
        // observer will be called when source value was changed.
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType changedData) {
                // The source value was changed, so we can call
                // removeSource(LiveData<S> toRemote) to stop
                // listening the database LiveData.
                result.removeSource(dbSource);
                // Decide whether to fetch potentially updated
                // data from the network.
                if (shouldFetch(changedData)) {
                    // Fetch data from network, persist it into DB
                    // and then send it back to the UI.
                    fetchFromNetwork(dbSource);
                } else {
                    // Re-attach the database LiveData as a new source.
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(ResultType newData) {
                            // Persist the new data into the DB.
                            setValue(Resource.success(newData));
                        }
                    });
                }
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (result.getValue() != newValue) {
            // Note: Use the LiveData method void setValue (T value)
            // to set the value of a LiveData object. If there are
            // active observers, the value will be dispatched to them.
            // This method must be called from the main thread.
            // If you need set a value from a background thread,
            // you can use postValue(Object).
            result.setValue(newValue);
        }
    }

    /**
     * Fetch the data from network and persist it into DB and then send it back to the UI.
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        // Create the API call to load data from themoviedb.org.
        // Note the use of ApiResponse, the generic class we created earlier,
        // which consists of an HTTP status code, some data and an error.
        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        // Re-attach the database LiveData as a new source,
        // it will dispatch its latest value quickly.
        result.addSource(dbSource, newData ->
                setValue(Resource.loading(newData)));

        // Start listening to the API response LiveData.
        result.addSource(apiResponse, response -> {
            // The source value was changed, so we can stop listening
            // to both the API response LiveData and database LiveData.
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            // If the network call completes successfully, save the
            // response into the database and re-initialize the stream.
            if (response.isSuccessful() && response.body != null) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(response.body);
                    appExecutors.mainThread().execute(() -> {
                        // We specially request new live data, otherwise we will
                        // get the immediately last cached value, which may not
                        // be updated with latest results received from network.
                        result.addSource(loadFromDb(), newData ->
                                setValue(Resource.success(newData)));
                    });
                });
            }
            // If the response is empty, reload from disk whatever we had.
            else if(response.isSuccessful() && response.body == null) {
                appExecutors.mainThread().execute(() ->
                        result.addSource(loadFromDb(), newData ->
                                setValue(Resource.success(newData))));
            }
            // If network request fails, dispatch a failure directly.
            else {
                onFetchFailed();
                result.addSource(dbSource, newData -> {
                    String errorMessage = response.getErrorMessage();
                    setValue(Resource.error(errorMessage, newData));
                });
            }
        });
    }

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called when the fetch fails. The child class may want to reset
    // components like rate limiter.
    @NonNull
    @MainThread
    protected abstract void onFetchFailed();

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // Called to get the cached data from the database.
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Returns a LiveData object that represents the resource that's
    // implemented in the base class.
    public final LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }
}
