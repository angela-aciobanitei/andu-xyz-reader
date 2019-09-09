package com.ang.acb.materialme.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 *
 * A factory class for creating ViewModels. Note: Dagger 2 allows multibindings and
 * with it the possibility to create a "map" of objects with a specific key:
 *      the key: for example the class type of ViewModel MyViewModel::class
 *      the value: the instance of ViewModel MyViewModel(repository, …)
 *
 * See: https://github.com/googlesamples/android-architecture-components/tree/GithubBrowserSample
 * See: https://blog.kotlin-academy.com/understanding-dagger-2-multibindings-viewmodel-8418eb372848
 */

@Singleton
public class ArticlesViewModelFactory implements ViewModelProvider.Factory {

    // Maps a Class that extends ViewModel as key, and a Provider of ViewModel (a
    // Dagger 2-specific class that let us provide — and so instantiate — a
    // dependency-injected class) as value.
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public ArticlesViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        this.creators = creators;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
        // Get the possible Provider for the given ViewModel from the Map.
        Provider<? extends ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()) {
                // If our Providers map doesn't have that specific key, check
                // if there is a subclass of the ViewModel we must instantiate.
                if (modelClass.isAssignableFrom(entry.getKey())) {
                    creator = entry.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            // If all previous attempts of getting a valid Provider from the map
            // fail, throw an exception.
            throw new IllegalArgumentException("Unknown model class: " + modelClass);
        }
        try {
            // Finally, let Dagger create our ViewModel by invoking the get() method
            // on the Provider object and casting it to our final type.
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
