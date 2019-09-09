package com.ang.acb.materialme.data.remote;

import androidx.lifecycle.LiveData;

import com.ang.acb.materialme.data.model.Article;

import java.util.List;

import retrofit2.http.GET;

/**
 * Defines the REST API access points for Retrofit.
 */
public interface ApiService {

    @GET("data.json")
    LiveData<ApiResponse<List<Article>>> getAllArticles();
}
