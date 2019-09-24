package com.ang.acb.materialme.ui.details;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ang.acb.materialme.data.model.Article;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Article> articleList;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, int behavior) {
        super(fragmentManager, behavior);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return ArticleDetailsFragment.newInstance(position, articleList.get(position).getId());
    }

    @Override
    public int getCount() {
        return articleList != null ? articleList.size() : 0;
    }


    public void submitList(List<Article> articles) {
        articleList = articles;
        notifyDataSetChanged();
    }
}
