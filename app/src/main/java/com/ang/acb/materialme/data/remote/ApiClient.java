package com.ang.acb.materialme.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit is a REST Client for Java and Android. It makes it relatively easy to
 * retrieve and upload JSON (or other structured data) via a REST based webservice.
 *
 * See: https://www.androidhive.info/2016/05/android-working-with-retrofit-http-library/
 * See: https://futurestud.io/tutorials/retrofit-2-log-requests-and-responses
 */
public class ApiClient {

    private static ApiService sInstance;
    private static final Object sLock = new Object();


    // Creates the Retrofit instance.
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5d68f_xyz-reader/")
                // Configure which converter is used for the data serialization.
                // Gson is a Java serialization/deserialization library to convert
                // Java Objects into JSON and back.
                .addConverterFactory(GsonConverterFactory.create())
                // Add a call adapter factory for supporting service method
                // return types other than Retrofit2.Call. We will use a custom
                // Retrofit adapter that converts the Retrofit2.Call into a
                // LiveData of ApiResponse.
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
    }

    // Returns the single instance of this class, creating it if necessary.
    public static ApiService getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = getRetrofitInstance().create(ApiService.class);
            }
            return sInstance;
        }
    }
}
