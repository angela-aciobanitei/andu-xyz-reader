package com.ang.acb.materialme.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ang.acb.materialme.data.repository.ArticlesRepository;
import com.ang.acb.materialme.ui.details.ArticleDetailsViewModel;
import com.ang.acb.materialme.ui.list.ArticleListViewModel;

/**
 * A factory class for creating ViewModels with a constructor that takes a [ArticlesRepository].
 *
 * See: https://github.com/googlesamples/android-sunflower
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    // Each view model needs access to the repository that handles the local and remote data.
    private final ArticlesRepository repository;

    public static ViewModelFactory getInstance(ArticlesRepository repository) {
        return new ViewModelFactory(repository);
    }

    private ViewModelFactory(ArticlesRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ArticleListViewModel.class)) {
            // noinspection unchecked
            return (T) new ArticleListViewModel(repository);
        } else if (modelClass.isAssignableFrom(ArticleDetailsViewModel.class)) {
            // noinspection unchecked
            return (T) new ArticleDetailsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
