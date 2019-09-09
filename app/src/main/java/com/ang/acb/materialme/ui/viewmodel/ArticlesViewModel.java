package com.ang.acb.materialme.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.data.repository.ArticlesRepository;

import java.util.List;

import javax.inject.Inject;

public class ArticlesViewModel extends ViewModel {

    private ArticlesRepository repository;
    private int currentPosition;

    @Inject
    public ArticlesViewModel(ArticlesRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Article>>> getArticleListLiveData() {
        return repository.getAllArticles();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
