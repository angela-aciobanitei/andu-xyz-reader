package com.ang.acb.materialme.di;

import com.ang.acb.materialme.ui.details.ArticleDetailsFragment;
import com.ang.acb.materialme.ui.details.ArticlesPagerFragment;
import com.ang.acb.materialme.ui.grid.ArticleGridFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract ArticleGridFragment contributeArticleListFragment();

    @ContributesAndroidInjector
    abstract ArticlesPagerFragment contributeArticlesViewPagerFragment();

    @ContributesAndroidInjector
    abstract ArticleDetailsFragment contributeArticleDetailsFragment();
}
