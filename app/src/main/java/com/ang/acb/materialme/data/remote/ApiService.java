package com.ang.acb.materialme.data.remote;

import androidx.lifecycle.LiveData;

import com.ang.acb.materialme.data.model.Article;

import java.util.List;

import retrofit2.http.GET;

public interface ApiService {

    @GET("xyz-reader.json")
    LiveData<ApiResponse<List<Article>>> getAllArticles();
}
