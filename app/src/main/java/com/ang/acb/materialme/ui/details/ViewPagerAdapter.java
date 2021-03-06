package com.ang.acb.materialme.ui.details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ang.acb.materialme.data.model.Article;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Article> articles;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, int behavior) {
        super(fragmentManager, behavior);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return ArticleDetailsFragment.newInstance(articles.get(position).getId());
    }

    @Override
    public int getCount() {
        return articles != null ? articles.size() : 0;
    }

    public void submitList(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }


}
