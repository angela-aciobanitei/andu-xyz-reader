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

    private List<Article> articles;

    // TODO This is deprecated...
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return ArticleDetailsFragment.newInstance(position, articles.get(position).getId());
    }

    @Override
    public int getCount() {
        return articles != null ? articles.size() : 0;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    public void submitList(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }
}
