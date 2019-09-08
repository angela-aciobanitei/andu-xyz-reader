package com.ang.acb.materialme.utils;

import android.content.Context;

import com.ang.acb.materialme.data.local.AppDatabase;
import com.ang.acb.materialme.data.remote.ApiClient;
import com.ang.acb.materialme.data.remote.ApiService;
import com.ang.acb.materialme.data.repository.ArticlesRepository;

public class InjectorUtils {

    public static ViewModelFactory provideViewModelFactory(Context context) {
        ArticlesRepository repository = provideArticlesRepository(context);
        return ViewModelFactory.getInstance(repository);
    }

    private static ArticlesRepository provideArticlesRepository(Context context) {
        AppExecutors executors = AppExecutors.getInstance();
        AppDatabase database = AppDatabase.getInstance(context);
        ApiService apiService = ApiClient.getInstance();
        return ArticlesRepository.getInstance(executors, database, apiService);
    }
}
