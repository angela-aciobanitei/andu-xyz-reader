package com.ang.acb.materialme.ui.details;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleDetailsBinding;
import com.ang.acb.materialme.databinding.FragmentArticlesViewPagerBinding;
import com.ang.acb.materialme.ui.common.ArticlesViewModel;
import com.ang.acb.materialme.utils.InjectorUtils;
import com.ang.acb.materialme.utils.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class ArticlesViewPagerFragment extends Fragment {

    private static final String ARG_POSITION = "ARG_POSITION";

    private FragmentArticlesViewPagerBinding binding;
    private ArticlesViewModel viewModel;
    private ArticlesViewPagerAdapter viewPagerAdapter;
    private int position;


    // Required empty public constructor
    public ArticlesViewPagerFragment() {}

    public static ArticlesViewPagerFragment newInstance(int position) {
        ArticlesViewPagerFragment fragment = new ArticlesViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentArticlesViewPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        setupViewPagerAdapter();
        populateUi();
    }

    private void initViewModel() {
        ViewModelFactory viewModelFactory = InjectorUtils.provideViewModelFactory(getActivity());
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
        viewModel.setCurrentPosition(position);
        Timber.d("Setup article list view model.");
    }

    private void setupViewPagerAdapter(){
        viewPagerAdapter = new ArticlesViewPagerAdapter(
                getChildFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.articlesViewPager.setAdapter(viewPagerAdapter);
    }

    private void populateUi() {
        viewModel.getArticleListLiveData().observe(this, new Observer<Resource<List<Article>>>() {
            @Override
            public void onChanged(Resource<List<Article>> resource) {
                if (resource != null && resource.data != null) {
                    viewPagerAdapter.submitList(resource.data);
                    binding.articlesViewPager.setCurrentItem(viewModel.getCurrentPosition());
                }
            }
        });

        // Save current page position in articles view model
        binding.articlesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                viewModel.setCurrentPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }

}
