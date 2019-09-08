package com.ang.acb.materialme.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.repository.ArticlesRepository;

public class ArticleDetailsViewModel extends ViewModel {

    private ArticlesRepository repository;

    public ArticleDetailsViewModel(ArticlesRepository repository) {
        this.repository = repository;
    }

    public LiveData<Article> getArticleLiveData(long id) {
        return repository.getArticle(id);
    }
}
