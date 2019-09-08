package com.ang.acb.materialme.ui.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.data.repository.ArticlesRepository;

import java.util.List;

public class ArticleListViewModel extends ViewModel {

    private ArticlesRepository repository;

    public ArticleListViewModel(ArticlesRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Article>>> getArticleListLiveData() {
        return repository.getAllArticles();
    }
}
