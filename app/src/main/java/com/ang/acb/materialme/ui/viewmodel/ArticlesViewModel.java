package com.ang.acb.materialme.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
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
    private MutableLiveData<Integer> positionLiveData;
    private MediatorLiveData<Article> currentArticleLiveData;

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

    public Integer getPositionLiveDataValue() {
        return getPositionLiveData().getValue();
    }

    public LiveData<Integer> getPositionLiveData() {
        if (positionLiveData == null) {
            positionLiveData = new MutableLiveData<>();
            positionLiveData.setValue(0);
        }
        return positionLiveData;
    }

    public void setPositionLiveData(int value) {
        if (positionLiveData == null) {
            positionLiveData = new MutableLiveData<>();
        }
        positionLiveData.setValue(value);
    }

    public LiveData<Article> getCurrentArticle() {
        if (currentArticleLiveData == null) {
            setCurrentArticle();
        }
        return currentArticleLiveData;
    }

    private void setCurrentArticle() {
        if (currentArticleLiveData == null) {
            currentArticleLiveData = new MediatorLiveData<>();
        }

        LiveData<Resource<List<Article>>> articlesLiveData = getObservableArticles();
        currentArticleLiveData.addSource(articlesLiveData, articles -> {
            if (articles != null && articles.getData() != null &&
                        getPositionLiveData().getValue() != null) {
                currentArticleLiveData.setValue(
                        articles.getData().get(getPositionLiveData().getValue()));
            }
        });

        LiveData<Integer> positionLiveData = getPositionLiveData();
        currentArticleLiveData.addSource(positionLiveData, position -> {
            if (position != null &&
                        articlesLiveData.getValue()!= null &&
                        articlesLiveData.getValue().getData()!= null) {
                currentArticleLiveData.setValue(
                        articlesLiveData.getValue().getData().get(position));
            }
        });
    }


}
