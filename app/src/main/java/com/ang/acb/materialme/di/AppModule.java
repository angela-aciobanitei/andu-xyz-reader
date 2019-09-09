package com.ang.acb.materialme.di;

import android.app.Application;

import androidx.room.Room;

import com.ang.acb.materialme.data.local.AppDatabase;
import com.ang.acb.materialme.data.local.ArticleDao;
import com.ang.acb.materialme.data.remote.ApiService;
import com.ang.acb.materialme.utils.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * We annotate this class with @Module to signal to Dagger to search within the available
 * methods for possible instance providers. The methods that will actually expose available
 * return types should also be annotated with the @Provides annotation. The @Singleton
 * annotation also signals to the Dagger compiler that the instance should be created
 * only once in the application.
 *
 * See: https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2
 */
@Module(includes = ViewModelModule.class)
class AppModule {

    @Provides
    @Singleton
    AppDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(
                application, AppDatabase.class, "articles.db")
                    .fallbackToDestructiveMigration()
                    .build();
    }

    @Provides
    @Singleton
    ArticleDao provideArticleDao(AppDatabase database) {
        return database.articleDao();
    }

    @Provides
    @Singleton
    ApiService provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5d68f_xyz-reader/")
                // Configure which converter is used for the data serialization.
                // Gson is a Java serialization/deserialization library that
                // converts Java Objects into JSON and back.
                .addConverterFactory(GsonConverterFactory.create())
                // Add a call adapter factory for supporting service method
                // return types other than Retrofit2.Call. We will use a custom
                // Retrofit adapter that converts the Retrofit2.Call into a
                // LiveData of ApiResponse.
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(ApiService.class);
    }
}
