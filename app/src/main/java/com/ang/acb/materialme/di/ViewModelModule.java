package com.ang.acb.materialme.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModel;
import com.ang.acb.materialme.ui.viewmodel.ArticlesViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ArticlesViewModel.class)
    abstract ViewModel bindArticlesViewModel(ArticlesViewModel viewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ArticlesViewModelFactory factory);
}

