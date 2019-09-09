package com.ang.acb.materialme.di;

import com.ang.acb.materialme.ui.details.ArticleDetailsFragment;
import com.ang.acb.materialme.ui.details.ArticlesViewPagerFragment;
import com.ang.acb.materialme.ui.list.ArticleListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract ArticleListFragment contributeArticleListFragment();

    @ContributesAndroidInjector
    abstract ArticlesViewPagerFragment contributeArticlesViewPagerFragment();

    @ContributesAndroidInjector
    abstract ArticleDetailsFragment contributeArticleDetailsFragment();
}
