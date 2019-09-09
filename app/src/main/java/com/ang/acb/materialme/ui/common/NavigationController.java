package com.ang.acb.materialme.ui.common;

import androidx.fragment.app.FragmentManager;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.ui.details.ArticleDetailsFragment;
import com.ang.acb.materialme.ui.details.ArticlesViewPagerFragment;
import com.ang.acb.materialme.ui.list.ArticleListFragment;

/**
 * A utility class that handles navigation in {@link MainActivity}.
 */
public class NavigationController {

    private final int containerId;
    private final FragmentManager fragmentManager;

    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.fragment_container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToArticleList() {
        ArticleListFragment fragment = new ArticleListFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }

    public void navigateToArticlesPager(int position) {
        ArticlesViewPagerFragment fragment = ArticlesViewPagerFragment.newInstance(position);
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }

    public void navigateToArticleDetails(int position) {
        ArticleDetailsFragment fragment = ArticleDetailsFragment.newInstance(position);
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
