package com.ang.acb.materialme.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.ang.acb.materialme.data.local.AppDatabase;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.data.remote.ApiResponse;
import com.ang.acb.materialme.data.remote.ApiService;
import com.ang.acb.materialme.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;


public class ArticlesRepository {

    private final AppExecutors executors;
    private final AppDatabase database;
    private final ApiService apiService;

    @Inject
    ArticlesRepository(AppExecutors executors,
                       AppDatabase database,
                       ApiService apiService) {
        this.executors = executors;
        this.database = database;
        this.apiService = apiService;
    }

    public LiveData<Resource<List<Article>>> getAllArticles() {
        // Note that we are using the NetworkBoundResource<ResultType, RequestType> class
        // that we've created earlier which can provide a resource backed by both the
        // SQLite database and the network. It defines two type parameters, ResultType
        // and RequestType, because the data type used locally might not match the data
        // type returned from the API.
        return new NetworkBoundResource<List<Article>, List<Article>>(executors) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Article>>> createCall() {
                // Create the API call to load the all recipes.
                return apiService.getAllArticles();
            }

            @Override
            protected void saveCallResult(@NonNull List<Article> callResult) {
                // Save the result of the API response into the database.
                database.articleDao().insertAllArticles(callResult);
            }

            @Override
            protected void onFetchFailed() {}

            @Override
            protected boolean shouldFetch(@Nullable List<Article> localData) {
                // Fetch fresh data only if it doesn't exist in database.
                return localData == null || localData.size() == 0;
            }

            @NonNull
            @Override
            protected LiveData<List<Article>> loadFromDb() {
                // Get the cached data from the database.
                return database.articleDao().getAllArticles();
            }
        }.asLiveData();
    }

    public LiveData<Article> getArticleById(long id){
        return database.articleDao().getArticleById(id);
    }
}
