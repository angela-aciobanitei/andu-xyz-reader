package com.ang.acb.materialme.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.data.repository.ArticlesRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Stores and manages UI-related data in a lifecycle conscious way.
 *
 * See: https://medium.com/androiddevelopers/viewmodels-a-simple-example-ed5ac416317e
 * See: https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
 */
public class ArticlesViewModel extends ViewModel {

    private ArticlesRepository repository;
    private LiveData<Resource<List<Article>>> observableArticles;
    private int position;

    @Inject
    ArticlesViewModel(ArticlesRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Article>>> getObservableArticles() {
        if (observableArticles == null) {
            observableArticles = repository.getAllArticles();
        }
        return observableArticles;
    }

    public LiveData<Article> getArticleById(long id) {
        return repository.getArticleById(id);
    }

    public int getCurrentPosition() {
        return position;
    }

    public void setCurrentPosition(int currentPosition) {

        position = currentPosition;
    }

}
